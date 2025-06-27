package com.example.dbmonitor.service;

import com.example.dbmonitor.config.MonitorProperties;
import com.example.dbmonitor.entity.AlertMessage;
import com.example.dbmonitor.entity.AlertRequest;
import com.example.dbmonitor.entity.MonitorResult;
import com.example.dbmonitor.exception.AlertSendException;
import com.example.dbmonitor.util.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.http.HttpTimeoutException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    
    private final MonitorProperties monitorProperties;
    private final HttpUtil httpUtil;
    
    // 用于跟踪数据源的上一次状态（是否健康）
    private final Map<String, Boolean> lastHealthStatus = new ConcurrentHashMap<>();
    
    // 记录每日使用次数的日期，用于重置计数
    private LocalDate lastResetDate = LocalDate.now();
    
    public void sendAlert(MonitorResult result) throws AlertSendException {
        if (!monitorProperties.getAlert().isEnabled()) {
            return;
        }
        
        String dataSourceName = result.getDataSourceName();
        boolean currentHealth = result.isHealthy();
        Boolean previousHealth = lastHealthStatus.get(dataSourceName);
        
        // 检查健康状态变化
        boolean isStateChange = false;
        String stateChangeType = null;
        
        if (previousHealth == null) {
            // 第一次检查，记录当前状态
            lastHealthStatus.put(dataSourceName, currentHealth);
            log.info("首次监控数据源 {}: 状态 = {}", dataSourceName, currentHealth ? "健康" : "不健康");
        } else if (previousHealth != currentHealth) {
            // 状态发生变化
            isStateChange = true;
            stateChangeType = currentHealth ? "DATABASE_RECONNECT" : "DATABASE_DISCONNECT";
            lastHealthStatus.put(dataSourceName, currentHealth);
            
            log.warn("数据源 {} 状态变化: {} -> {}", 
                    dataSourceName, 
                    previousHealth ? "健康" : "不健康", 
                    currentHealth ? "健康" : "不健康");
        }
        
        // 确定严重程度
        String severity = determineSeverity(result, isStateChange);
        
        // 构建报警内容
        String content = buildAlertContent(result, isStateChange, stateChangeType);
        
        // 发送报警
        sendAlertByLevel(severity, dataSourceName, content);
    }
    
    /**
     * 根据级别发送报警
     */
    private void sendAlertByLevel(String severity, String dataSourceName, String content) throws AlertSendException {
        // 检查并重置每日计数
        checkAndResetDailyLimit();
        
        // 标准化严重程度
        String normalizedSeverity = normalizeSeverity(severity);
        
        // 获取对应级别的账号配置
        MonitorProperties.AlertAccount account = getAccountBySeverity(normalizedSeverity);
        
        if (account == null) {
            throw new AlertSendException("无法找到对应级别的账号配置: " + normalizedSeverity, "CONFIG");
        }
        
        // 检查每日限制
        if (account.getUsedToday() >= account.getDailyLimit()) {
            log.warn("账号 {} 今日报警次数已达限制 {}/{}", 
                    account.getReceiver(), account.getUsedToday(), account.getDailyLimit());
            throw new AlertSendException(
                    String.format("账号 %s 今日报警次数已达限制", account.getReceiver()), 
                    "DAILY_LIMIT_EXCEEDED");
        }
        
        // 构建请求
        AlertRequest request = AlertRequest.builder()
                .receiver(account.getReceiver())
                .auth(account.getAuth())
                .content(content)
                .severity(normalizedSeverity)
                .dataSource(dataSourceName)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        
        try {
            // 确保使用HTTP协议
            String url = ensureHttpProtocol(monitorProperties.getAlert().getWebhookUrl());
            httpUtil.sendAlert(url, request);
            
            // 增加使用次数
            account.setUsedToday(account.getUsedToday() + 1);
            
            log.info("报警发送成功: receiver={}, severity={}, dataSource={}, usage={}/{}", 
                    account.getReceiver(), normalizedSeverity, dataSourceName, 
                    account.getUsedToday(), account.getDailyLimit());
                    
        } catch (HttpTimeoutException e) {
            String msg = String.format("报警发送超时: receiver=%s, error=%s", 
                    account.getReceiver(), e.getMessage());
            log.error(msg, e);
            throw new AlertSendException(msg, account.getReceiver(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String msg = "报警发送被中断: receiver=" + account.getReceiver();
            log.error(msg, e);
            throw new AlertSendException(msg, account.getReceiver(), e);
        } catch (IllegalArgumentException e) {
            String msg = String.format("报警配置无效: receiver=%s, error=%s", 
                    account.getReceiver(), e.getMessage());
            log.error(msg, e);
            throw new AlertSendException(msg, account.getReceiver(), e);
        }
    }
    
    /**
     * 专门为定时报告发送警报
     */
    public void sendScheduledAlert(String dataSourceName, String alertType, String message, 
                                   String severity, Map<String, Object> additionalInfo) throws AlertSendException {
        if (!monitorProperties.getAlert().isEnabled()) {
            return;
        }
        
        // 构建完整的报警内容
        String content = buildScheduledAlertContent(dataSourceName, alertType, message, severity, additionalInfo);
        
        // 发送报警
        sendAlertByLevel(severity, dataSourceName, content);
    }
    
    /**
     * 检查并重置每日限制
     */
    private void checkAndResetDailyLimit() {
        LocalDate today = LocalDate.now();
        if (!today.equals(lastResetDate)) {
            // 新的一天，重置所有账号的使用次数
            monitorProperties.getAlert().getAccounts().getInfo().setUsedToday(0);
            monitorProperties.getAlert().getAccounts().getWarn().setUsedToday(0);
            monitorProperties.getAlert().getAccounts().getError().setUsedToday(0);
            lastResetDate = today;
            log.info("每日报警次数已重置");
        }
    }
    
    /**
     * 标准化严重程度
     */
    private String normalizeSeverity(String severity) {
        if (severity == null) return "info";
        
        return switch (severity.toLowerCase()) {
            case "critical" -> "error";  // critical映射到error
            case "warning" -> "warn";    // warning映射到warn  
            case "info" -> "info";       // info保持不变
            default -> "info";
        };
    }
    
    /**
     * 根据严重程度获取对应的账号配置
     */
    private MonitorProperties.AlertAccount getAccountBySeverity(String severity) {
        return switch (severity) {
            case "info" -> monitorProperties.getAlert().getAccounts().getInfo();
            case "warn" -> monitorProperties.getAlert().getAccounts().getWarn();
            case "error" -> monitorProperties.getAlert().getAccounts().getError();
            default -> monitorProperties.getAlert().getAccounts().getInfo();
        };
    }
    
    /**
     * 确定严重程度
     */
    private String determineSeverity(MonitorResult result, boolean isStateChange) {
        if (!result.isHealthy()) return "critical";
        
        // 如果是状态变化且恢复健康，设为info级别
        if (isStateChange && result.isHealthy()) return "info";
        
        // 检查长时间运行的SQL
        if (!result.getLongRunningQueries().isEmpty()) {
            boolean hasCritical = result.getLongRunningQueries().stream()
                    .anyMatch(q -> q.getTime() != null && q.getTime() > 120);
            return hasCritical ? "critical" : "warning";
        }
        
        return "info";
    }
    
    /**
     * 构建报警内容
     */
    private String buildAlertContent(MonitorResult result, boolean isStateChange, String stateChangeType) {
        StringBuilder sb = new StringBuilder();
        
        // 状态变化消息优先显示
        if (isStateChange) {
            if ("DATABASE_DISCONNECT".equals(stateChangeType)) {
                sb.append("🔴 数据库连接断开警告\n");
                sb.append("数据库: ").append(result.getDataSourceName()).append("\n");
                sb.append("状态: 连接断开\n");
                sb.append("时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
            } else if ("DATABASE_RECONNECT".equals(stateChangeType)) {
                sb.append("🟢 数据库连接恢复通知\n");
                sb.append("数据库: ").append(result.getDataSourceName()).append("\n");
                sb.append("状态: 连接已恢复\n");
                sb.append("时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
            }
        } else {
            sb.append("数据库监控报告\n");
            sb.append("数据库: ").append(result.getDataSourceName()).append("\n");
            sb.append("健康状态: ").append(result.isHealthy() ? "健康" : "异常").append("\n");
            sb.append("时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        }
        
        sb.append("检查耗时: ").append(result.getCheckDuration()).append("ms\n");
        
        if (!result.getLongRunningQueries().isEmpty()) {
            sb.append("长时间运行SQL: ").append(result.getLongRunningQueries().size()).append(" 个\n");
            
            // 添加TOP 3详细信息
            result.getLongRunningQueries().stream()
                    .limit(3)
                    .forEach(q -> {
                        sb.append(String.format("  - ID:%d, 时间:%ds, SQL:%s\n", 
                            q.getId(), 
                            q.getTime(), 
                            q.getSqlPreview()));
                    });
        }
        
        if (!result.getIssues().isEmpty()) {
            sb.append("检测到的问题:\n");
            result.getIssues().forEach(issue -> sb.append("  - ").append(issue).append("\n"));
        }
        
        return sb.toString();
    }
    
    /**
     * 构建定时报告的报警内容
     */
    private String buildScheduledAlertContent(String dataSourceName, String alertType, String message, 
                                            String severity, Map<String, Object> additionalInfo) {
        StringBuilder sb = new StringBuilder();
        
        // 根据严重程度添加图标
        String icon = switch (normalizeSeverity(severity)) {
            case "error" -> "🔴";
            case "warn" -> "⚠️";
            default -> "🟢";
        };
        
        sb.append(icon).append(" 定时监控报告\n");
        sb.append("类型: ").append(alertType).append("\n");
        sb.append("数据源: ").append(dataSourceName).append("\n");
        sb.append("时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("内容:\n").append(message);
        
        return sb.toString();
    }
    
    /**
     * 确保URL使用HTTP协议
     */
    private String ensureHttpProtocol(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Webhook URL cannot be null or empty");
        }
        
        url = url.trim();
        
        // 如果没有协议，添加http://
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        
        return url;
    }
    
    /**
     * 获取账号使用情况统计
     */
    public Map<String, Object> getUsageStatistics() {
        checkAndResetDailyLimit();
        
        var accounts = monitorProperties.getAlert().getAccounts();
        return Map.of(
            "date", LocalDate.now().toString(),
            "info", Map.of(
                "receiver", accounts.getInfo().getReceiver(),
                "used", accounts.getInfo().getUsedToday(),
                "limit", accounts.getInfo().getDailyLimit(),
                "remaining", accounts.getInfo().getDailyLimit() - accounts.getInfo().getUsedToday()
            ),
            "warn", Map.of(
                "receiver", accounts.getWarn().getReceiver(),
                "used", accounts.getWarn().getUsedToday(),
                "limit", accounts.getWarn().getDailyLimit(),
                "remaining", accounts.getWarn().getDailyLimit() - accounts.getWarn().getUsedToday()
            ),
            "error", Map.of(
                "receiver", accounts.getError().getReceiver(),
                "used", accounts.getError().getUsedToday(),
                "limit", accounts.getError().getDailyLimit(),
                "remaining", accounts.getError().getDailyLimit() - accounts.getError().getUsedToday()
            )
        );
    }
    
    /**
     * 手动测试报警发送
     */
    public void testAlert(String severity, String testMessage) throws AlertSendException {
        String content = String.format("🧪 测试报警\n级别: %s\n消息: %s\n时间: %s", 
                severity, testMessage, 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        sendAlertByLevel(severity, "TEST_DATASOURCE", content);
    }
}