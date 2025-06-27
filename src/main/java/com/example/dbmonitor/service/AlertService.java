package com.example.dbmonitor.service;

import com.example.dbmonitor.config.MonitorProperties;
import com.example.dbmonitor.entity.AlertMessage;
import com.example.dbmonitor.entity.MonitorResult;
import com.example.dbmonitor.exception.AlertSendException;
import com.example.dbmonitor.util.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.http.HttpTimeoutException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    
    private final MonitorProperties monitorProperties;
    private final HttpUtil httpUtil;
    
    // 用于跟踪数据源的上一次状态（是否健康）
    private final Map<String, Boolean> lastHealthStatus = new ConcurrentHashMap<>();
    
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
        
        // 获取数据源配置
        var dsConfig = monitorProperties.getDatasources().stream()
                .filter(ds -> ds.getName().equals(result.getDataSourceName()))
                .findFirst()
                .orElse(null);
        
        // 确定警告类型
        String alertType = isStateChange ? stateChangeType : determineAlertType(result);
        
        // 构建报警消息
        AlertMessage message = AlertMessage.builder()
                .alertType(alertType)
                .dataSource(result.getDataSourceName())
                .message(buildAlertMessage(result, isStateChange, stateChangeType))
                .timestamp(LocalDateTime.now())
                .severity(determineSeverity(result, isStateChange))
                .longRunningQueries(result.getLongRunningQueries())
                .additionalInfo(result.getMetrics())
                .tags(dsConfig != null && dsConfig.getTags() != null ? 
                      new ArrayList<>(dsConfig.getTags()) : new ArrayList<>())
                .metrics(extractMetrics(result))
                .build();
        
        // 根据规则发送到对应的端点（按严重程度分发）
        List<MonitorProperties.AlertEndpoint> endpoints = determineEndpointsBySeverity(result, message.getSeverity());
        
        List<AlertSendException> failures = new ArrayList<>();
        
        for (MonitorProperties.AlertEndpoint endpoint : endpoints) {
            try {
                // 确保使用HTTP协议
                String url = ensureHttpProtocol(endpoint.getUrl());
                httpUtil.sendAlert(url, message);
                log.info("Alert sent to endpoint: {} for datasource: {} (severity: {})", 
                    endpoint.getName(), result.getDataSourceName(), message.getSeverity());
            } catch (HttpTimeoutException e) {
                String msg = String.format("Alert timeout for endpoint %s: %s", 
                    endpoint.getName(), e.getMessage());
                log.error(msg, e);
                failures.add(new AlertSendException(msg, endpoint.getName(), e));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复中断状态
                String msg = "Alert sending interrupted for endpoint: " + endpoint.getName();
                log.error(msg, e);
                failures.add(new AlertSendException(msg, endpoint.getName(), e));
            } catch (IllegalArgumentException e) {
                String msg = String.format("Invalid alert configuration for endpoint %s: %s", 
                    endpoint.getName(), e.getMessage());
                log.error(msg, e);
                failures.add(new AlertSendException(msg, endpoint.getName(), e));
            }
        }
        
        // 如果所有端点都失败，抛出异常
        if (!failures.isEmpty() && failures.size() == endpoints.size()) {
            throw failures.get(0); // 抛出第一个异常
        }
    }
    
    /**
     * 确保URL使用HTTP协议
     */
    private String ensureHttpProtocol(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        
        url = url.trim();
        
        // 如果没有协议，添加http://
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        
        return url;
    }
    
    private String determineAlertType(MonitorResult result) {
        if (!result.isHealthy()) return "DATABASE_UNHEALTHY";
        if (!result.getLongRunningQueries().isEmpty()) return "LONG_RUNNING_QUERIES";
        if (!result.getIssues().isEmpty()) return "PERFORMANCE_ISSUES";
        return "GENERAL";
    }
    
    private String determineSeverity(MonitorResult result) {
        return determineSeverity(result, false);
    }
    
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
    
    private AlertMessage.AlertMetrics extractMetrics(MonitorResult result) {
        var builder = AlertMessage.AlertMetrics.builder();
        Map<String, Object> metrics = result.getMetrics();
        
        if (metrics != null) {
            // 从metrics中提取连接数等信息
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> connections = (List<Map<String, Object>>) metrics.get("connections");
            if (connections != null) {
                parseConnectionMetrics(connections, builder);
            }
            
            // 提取其他指标
            Integer lockWaits = (Integer) metrics.get("lockWaits");
            if (lockWaits != null) {
                builder.lockWaits(lockWaits);
            }
        }
        
        return builder.build();
    }
    
    private void parseConnectionMetrics(List<Map<String, Object>> connections, 
                                       AlertMessage.AlertMetrics.AlertMetricsBuilder builder) {
        for (Map<String, Object> conn : connections) {
            String varName = (String) conn.get("Variable_name");
            if (varName == null) varName = (String) conn.get("VARIABLE_NAME");
            
            String varValue = (String) conn.get("Value");
            if (varValue == null) varValue = (String) conn.get("VARIABLE_VALUE");
            
            if (varName == null || varValue == null) continue;
            
            try {
                switch (varName) {
                    case "Threads_connected" -> builder.currentConnections(Integer.parseInt(varValue));
                    case "Max_used_connections" -> builder.maxConnections(Integer.parseInt(varValue));
                }
            } catch (NumberFormatException e) {
                log.warn("Failed to parse connection metric {}: {}", varName, varValue);
            }
        }
    }
    
    private String buildAlertMessage(MonitorResult result) {
        return buildAlertMessage(result, false, null);
    }
    
    private String buildAlertMessage(MonitorResult result, boolean isStateChange, String stateChangeType) {
        StringBuilder sb = new StringBuilder();
        
        // 状态变化消息优先显示
        if (isStateChange) {
            if ("DATABASE_DISCONNECT".equals(stateChangeType)) {
                sb.append("🔴 数据库连接断开警告\n");
                sb.append("数据库: ").append(result.getDataSourceName()).append("\n");
                sb.append("状态: 连接断开\n");
                sb.append("时间: ").append(LocalDateTime.now()).append("\n");
            } else if ("DATABASE_RECONNECT".equals(stateChangeType)) {
                sb.append("🟢 数据库连接恢复通知\n");
                sb.append("数据库: ").append(result.getDataSourceName()).append("\n");
                sb.append("状态: 连接已恢复\n");
                sb.append("时间: ").append(LocalDateTime.now()).append("\n");
            }
        } else {
            sb.append("数据库: ").append(result.getDataSourceName()).append("\n");
        }
        
        sb.append("检查耗时: ").append(result.getCheckDuration()).append("ms\n");
        
        if (!result.getLongRunningQueries().isEmpty()) {
            sb.append("长时间运行SQL: ").append(result.getLongRunningQueries().size()).append(" 个\n");
            
            // 添加TOP 3详细信息
            result.getLongRunningQueries().stream()
                    .limit(3)
                    .forEach(q -> {
                        sb.append(String.format("  - ID:%d, 时间:%ds, 严重程度:%s, SQL:%s\n", 
                            q.getId(), 
                            q.getTime(), 
                            q.getSeverity(30, 120),
                            q.getSqlPreview()));
                    });
        }
        
        if (!result.getIssues().isEmpty()) {
            sb.append("检测到的问题: \n");
            result.getIssues().forEach(issue -> sb.append("  - ").append(issue).append("\n"));
        }
        
        return sb.toString();
    }
    
    private List<MonitorProperties.AlertEndpoint> determineEndpoints(MonitorResult result, String severity) {
        return monitorProperties.getAlert().getEndpoints().stream()
                .filter(endpoint -> shouldSendToEndpoint(endpoint, result, severity))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据严重程度确定接收端点（不同等级报告发送不同对象）
     */
    private List<MonitorProperties.AlertEndpoint> determineEndpointsBySeverity(MonitorResult result, String severity) {
        return monitorProperties.getAlert().getEndpoints().stream()
                .filter(endpoint -> shouldSendToEndpointBySeverity(endpoint, result, severity))
                .collect(Collectors.toList());
    }
    
    private boolean shouldSendToEndpoint(MonitorProperties.AlertEndpoint endpoint, 
                                       MonitorResult result, String severity) {
        // 检查标签匹配
        if (endpoint.getTags() != null && !endpoint.getTags().isEmpty()) {
            var dsConfig = monitorProperties.getDatasources().stream()
                    .filter(ds -> ds.getName().equals(result.getDataSourceName()))
                    .findFirst()
                    .orElse(null);
            
            if (dsConfig == null || dsConfig.getTags() == null) {
                return false;
            }
            
            boolean tagMatch = endpoint.getTags().stream()
                    .anyMatch(tag -> dsConfig.getTags().contains(tag));
            
            if (!tagMatch) {
                return false;
            }
        }
        
        // 可以根据severity进一步过滤
        // 例如：某些端点只接收critical级别的报警
        
        return true;
    }
    
    /**
     * 专门为定时报告发送警报（支持自定义消息）
     */
    public void sendScheduledAlert(String dataSourceName, String alertType, String message, 
                                   String severity, Map<String, Object> additionalInfo) throws AlertSendException {
        if (!monitorProperties.getAlert().isEnabled()) {
            return;
        }
        
        // 构建报警消息
        AlertMessage alertMessage = AlertMessage.builder()
                .alertType(alertType)
                .dataSource(dataSourceName)
                .message(message)
                .timestamp(LocalDateTime.now())
                .severity(severity)
                .longRunningQueries(new ArrayList<>())
                .additionalInfo(additionalInfo)
                .tags(new ArrayList<>())
                .metrics(AlertMessage.AlertMetrics.builder().build())
                .build();
        
        // 根据严重程度选择端点
        List<MonitorProperties.AlertEndpoint> endpoints = getEndpointsBySeverity(severity);
        
        List<AlertSendException> failures = new ArrayList<>();
        
        for (MonitorProperties.AlertEndpoint endpoint : endpoints) {
            try {
                String url = ensureHttpProtocol(endpoint.getUrl());
                httpUtil.sendAlert(url, alertMessage);
                log.info("Scheduled alert sent to endpoint: {} for {} (severity: {})", 
                    endpoint.getName(), dataSourceName, severity);
            } catch (HttpTimeoutException e) {
                String msg = String.format("Alert timeout for endpoint %s: %s", 
                    endpoint.getName(), e.getMessage());
                log.error(msg, e);
                failures.add(new AlertSendException(msg, endpoint.getName(), e));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                String msg = "Alert sending interrupted for endpoint: " + endpoint.getName();
                log.error(msg, e);
                failures.add(new AlertSendException(msg, endpoint.getName(), e));
            } catch (IllegalArgumentException e) {
                String msg = String.format("Invalid alert configuration for endpoint %s: %s", 
                    endpoint.getName(), e.getMessage());
                log.error(msg, e);
                failures.add(new AlertSendException(msg, endpoint.getName(), e));
            }
        }
        
        // 如果所有端点都失败，抛出异常
        if (!failures.isEmpty() && failures.size() == endpoints.size()) {
            throw failures.get(0);
        }
    }
    
    /**
     * 根据严重程度获取端点列表
     */
    private List<MonitorProperties.AlertEndpoint> getEndpointsBySeverity(String severity) {
        return monitorProperties.getAlert().getEndpoints().stream()
                .filter(endpoint -> shouldReceiveBySeverity(endpoint, severity))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据严重程度判断端点是否应该接收警报
     */
    private boolean shouldReceiveBySeverity(MonitorProperties.AlertEndpoint endpoint, String severity) {
        String endpointType = endpoint.getType();
        if (endpointType == null) {
            endpointType = "default";
        }
        
        return switch (endpointType.toLowerCase()) {
            case "critical-only" -> "critical".equals(severity);
            case "warning-and-critical" -> "warning".equals(severity) || "critical".equals(severity);
            case "info-and-above" -> true;
            case "info-only" -> "info".equals(severity);
            case "non-critical" -> !"critical".equals(severity);
            default -> true;
        };
    }
    
    /**
     * 根据严重程度判断是否应该发送到指定端点
     * 实现不同等级报告发送给不同对象的逻辑
     */
    private boolean shouldSendToEndpointBySeverity(MonitorProperties.AlertEndpoint endpoint, 
                                                  MonitorResult result, String severity) {
        // 检查标签匹配
        if (endpoint.getTags() != null && !endpoint.getTags().isEmpty()) {
            var dsConfig = monitorProperties.getDatasources().stream()
                    .filter(ds -> ds.getName().equals(result.getDataSourceName()))
                    .findFirst()
                    .orElse(null);
            
            if (dsConfig == null || dsConfig.getTags() == null) {
                return false;
            }
            
            boolean tagMatch = endpoint.getTags().stream()
                    .anyMatch(tag -> dsConfig.getTags().contains(tag));
            
            if (!tagMatch) {
                return false;
            }
        }
        
        // 根据端点类型和严重程度进行分发
        String endpointType = endpoint.getType();
        if (endpointType == null) {
            endpointType = "default";
        }
        
        switch (endpointType.toLowerCase()) {
            case "critical-only":
                // 只接收critical级别的警告（如：管理员，值班人员）
                return "critical".equals(severity);
                
            case "warning-and-critical":
                // 接收warning和critical级别（如：运维团队）
                return "warning".equals(severity) || "critical".equals(severity);
                
            case "info-and-above":
                // 接收所有级别（如：监控系统，日志收集）
                return true;
                
            case "info-only":
                // 只接收info级别（如：状态看板）
                return "info".equals(severity);
                
            case "non-critical":
                // 排除critical级别（如：开发团队通知）
                return !"critical".equals(severity);
                
            default:
                // 默认接收所有级别
                return true;
        }
    }
}