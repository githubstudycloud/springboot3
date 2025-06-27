package com.example.dbmonitor.controller;

import com.example.dbmonitor.config.MonitorProperties;
import com.example.dbmonitor.entity.AlertMessage;
import com.example.dbmonitor.service.AlertService;
import com.example.dbmonitor.service.ScheduledReportService;
import com.example.dbmonitor.util.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {
    
    private final AlertService alertService;
    private final HttpUtil httpUtil;
    private final ScheduledReportService scheduledReportService;
    private final MonitorProperties monitorProperties;
    
    /**
     * 新版本三参数报警测试接口
     * 接收JSON参数：receiver, auth, content (符合用户要求的新格式)
     */
    @PostMapping("/alert")
    public ResponseEntity<Map<String, Object>> testAlert(@RequestBody TestAlertRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证参数
            if (request.getReceiver() == null || request.getReceiver().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "receiver 参数不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (request.getAuth() == null || request.getAuth().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "auth 参数不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "content 参数不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            log.info("收到测试报警请求 - receiver: {}, auth: {}, content: {}", 
                    request.getReceiver(), request.getAuth(), request.getContent());
            
            response.put("success", true);
            response.put("message", "报警接收成功");
            response.put("receiver", request.getReceiver());
            response.put("auth", request.getAuth());
            response.put("content", request.getContent());
            response.put("timestamp", LocalDateTime.now());
            response.put("serverNote", "这是内部测试接口，实际生产中请替换为您的报警接口地址");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "接收失败: " + e.getMessage());
            log.error("测试报警接收失败", e);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 标准三参数Webhook接收端点 (符合用户格式要求)
     */
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> receiveWebhook(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String receiver = (String) payload.get("receiver");
            String auth = (String) payload.get("auth");
            String content = (String) payload.get("content");
            
            log.info("收到标准格式报警 - receiver: {}, auth: {}, content: {}", 
                    receiver, auth, content);
            
            response.put("received", true);
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "报警接收成功");
            response.put("receiver", receiver);
            response.put("auth_status", auth != null ? "token已验证" : "未提供token");
            response.put("content_length", content != null ? content.length() : 0);
            
        } catch (Exception e) {
            log.error("处理报警请求时出错", e);
            response.put("received", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 测试数据库连接断开警告
     */
    @PostMapping("/test-db-disconnect")
    public ResponseEntity<Map<String, Object>> testDatabaseDisconnect() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 构建数据库断开测试消息
            AlertMessage disconnectAlert = AlertMessage.builder()
                    .alertType("DATABASE_DISCONNECT")
                    .dataSource("TEST_DB")
                    .message("测试数据库连接断开警告")
                    .timestamp(LocalDateTime.now())
                    .severity("critical")
                    .build();
            
            // 使用AlertService发送
            alertService.sendAlert(createTestMonitorResult(false, "数据库连接断开"));
            
            response.put("success", true);
            response.put("message", "数据库断开警告测试完成");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "测试失败: " + e.getMessage());
            log.error("数据库断开测试失败", e);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 测试数据库连接恢复警告
     */
    @PostMapping("/test-db-reconnect")
    public ResponseEntity<Map<String, Object>> testDatabaseReconnect() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 构建数据库重连测试消息
            AlertMessage reconnectAlert = AlertMessage.builder()
                    .alertType("DATABASE_RECONNECT")
                    .dataSource("TEST_DB")
                    .message("测试数据库连接恢复通知")
                    .timestamp(LocalDateTime.now())
                    .severity("info")
                    .build();
            
            // 使用AlertService发送
            alertService.sendAlert(createTestMonitorResult(true, "数据库连接恢复"));
            
            response.put("success", true);
            response.put("message", "数据库重连警告测试完成");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "测试失败: " + e.getMessage());
            log.error("数据库重连测试失败", e);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 测试不同等级的警告
     */
    @PostMapping("/test-severity/{level}")
    public ResponseEntity<Map<String, Object>> testSeverityAlert(@PathVariable String level) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String message;
            boolean healthy;
            
            switch (level.toLowerCase()) {
                case "critical" -> {
                    message = "严重错误：数据库完全不可用";
                    healthy = false;
                }
                case "warning" -> {
                    message = "警告：检测到性能问题";
                    healthy = true;
                }
                case "info" -> {
                    message = "信息：系统状态正常";
                    healthy = true;
                }
                default -> {
                    response.put("success", false);
                    response.put("message", "不支持的严重程度等级: " + level);
                    return ResponseEntity.badRequest().body(response);
                }
            }
            
            alertService.sendAlert(createTestMonitorResult(healthy, message, level));
            
            response.put("success", true);
            response.put("message", String.format("%s 级别警告测试完成", level));
            response.put("severity", level);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "测试失败: " + e.getMessage());
            log.error("严重程度测试失败", e);
        }
        
        return ResponseEntity.ok(response);
    }
    
    // 辅助方法：创建测试用的MonitorResult
    private com.example.dbmonitor.entity.MonitorResult createTestMonitorResult(boolean healthy, String issue) {
        return createTestMonitorResult(healthy, issue, healthy ? "info" : "critical");
    }
    
    private com.example.dbmonitor.entity.MonitorResult createTestMonitorResult(boolean healthy, String issue, String severity) {
        return com.example.dbmonitor.entity.MonitorResult.builder()
                .dataSourceName("TEST_DB")
                .timestamp(LocalDateTime.now())
                .healthy(healthy)
                .issues(java.util.List.of(issue))
                .longRunningQueries(new java.util.ArrayList<>())
                .metrics(new HashMap<>())
                .checkDuration(100L)
                .build();
    }
    
    /**
     * 标准三参数报警请求DTO
     */
    public static class TestAlertRequest {
        private String receiver;
        private String auth;
        private String content;
        
        // Getters and Setters
        public String getReceiver() { return receiver; }
        public void setReceiver(String receiver) { this.receiver = receiver; }
        
        public String getAuth() { return auth; }
        public void setAuth(String auth) { this.auth = auth; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    /**
     * 手动触发定时健康报告（用于测试）
     */
    @PostMapping("/trigger-scheduled-report")
    public ResponseEntity<Map<String, Object>> triggerScheduledReport() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("手动触发定时健康报告...");
            
            // 在新线程中执行，避免阻塞HTTP请求
            new Thread(() -> {
                try {
                    scheduledReportService.scheduledHealthReport();
                } catch (Exception e) {
                    log.error("手动执行定时报告失败", e);
                }
            }).start();
            
            response.put("success", true);
            response.put("message", "定时健康报告已触发，请查看日志和警告接收端点");
            response.put("timestamp", LocalDateTime.now());
            response.put("note", "报告将在后台异步执行，包含以下检查：");
            response.put("checks", java.util.List.of(
                "1. Java程序状态检查 (INFO级别)",
                "2. MySQL连接状态检查 (INFO/CRITICAL级别)",
                "3. 长时间运行SQL分析 (INFO/WARNING/CRITICAL级别)",
                "4. 系统资源状态检查 (INFO/WARNING/CRITICAL级别)",
                "5. 磁盘空间状态检查 (INFO/WARNING/CRITICAL级别)",
                "6. 综合健康状态总结 (INFO级别)"
            ));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "触发定时报告失败: " + e.getMessage());
            log.error("手动触发定时报告失败", e);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取定时报告配置信息
     */
    @GetMapping("/scheduled-report-info")
    public ResponseEntity<Map<String, Object>> getScheduledReportInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("scheduleDescription", "定时健康报告配置");
        info.put("cronExpression", "0 5,35 * * * *");
        info.put("cronDescription", "每小时的第5分钟和第35分钟执行");
        info.put("nextExecutionTimes", java.util.List.of(
            "每天 00:05, 00:35",
            "每天 01:05, 01:35",
            "每天 02:05, 02:35",
            "... (以此类推，每半小时执行一次)"
        ));
        
        info.put("reportSequence", java.util.List.of(
            "1. Java程序状态检查 - 报告程序本身正常运行",
            "2. MySQL连接状态检查 - 验证所有数据库连接",
            "3. 长时间SQL分析 - 检查超过30分钟的SQL查询",
            "4. 系统资源检查 - CPU、内存使用情况",
            "5. 磁盘空间检查 - 磁盘使用率和可用空间",
            "6. 综合健康总结 - 整体状态汇总"
        ));
        
        info.put("severityLevels", Map.of(
            "info", "正常状态报告，发送给监控系统和状态看板",
            "warning", "警告级别问题，发送给运维团队",
            "critical", "严重问题，发送给管理员和值班人员"
        ));
        
        info.put("longRunningSqlThreshold", "30分钟");
        info.put("note", "如果发现超过30分钟的SQL，将发送异常报告包含具体SQL语句");
        
        return ResponseEntity.ok(info);
    }
    
    /**
     * 获取报警账号使用统计
     */
    @GetMapping("/alert-usage")
    public ResponseEntity<Map<String, Object>> getAlertUsage() {
        return ResponseEntity.ok(alertService.getUsageStatistics());
    }
    
    /**
     * 手动测试不同级别的报警
     */
    @PostMapping("/test-alert/{level}")
    public ResponseEntity<Map<String, Object>> testAlertLevel(@PathVariable String level,
                                                              @RequestParam(required = false, defaultValue = "这是一条测试报警消息") String message) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证级别
            if (!java.util.Set.of("info", "warn", "error").contains(level.toLowerCase())) {
                response.put("success", false);
                response.put("message", "不支持的报警级别: " + level + "，支持的级别: info, warn, error");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 发送测试报警
            alertService.testAlert(level.toLowerCase(), message);
            
            response.put("success", true);
            response.put("message", String.format("成功发送 %s 级别测试报警", level));
            response.put("level", level.toLowerCase());
            response.put("content", message);
            response.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "发送失败: " + e.getMessage());
            log.error("手动测试报警失败", e);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 重置账号每日使用次数 (仅用于测试)
     */
    @PostMapping("/reset-daily-usage")
    public ResponseEntity<Map<String, Object>> resetDailyUsage() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 注意：这是一个测试方法，生产环境中应该移除或加强权限控制
            var accounts = monitorProperties.getAlert().getAccounts();
            accounts.getInfo().setUsedToday(0);
            accounts.getWarn().setUsedToday(0);
            accounts.getError().setUsedToday(0);
            
            response.put("success", true);
            response.put("message", "所有账号的每日使用次数已重置为0");
            response.put("timestamp", LocalDateTime.now());
            response.put("warning", "这是测试功能，生产环境请谨慎使用");
            
            log.info("手动重置了所有账号的每日使用次数");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "重置失败: " + e.getMessage());
            log.error("重置每日使用次数失败", e);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取报警配置信息
     */
    @GetMapping("/alert-config")
    public ResponseEntity<Map<String, Object>> getAlertConfig() {
        Map<String, Object> config = new HashMap<>();
        
        var alertConfig = monitorProperties.getAlert();
        var accounts = alertConfig.getAccounts();
        
        config.put("enabled", alertConfig.isEnabled());
        config.put("webhook_url", alertConfig.getWebhookUrl());
        
        // 账号信息（隐藏敏感的auth token）
        config.put("accounts", Map.of(
            "info", Map.of(
                "receiver", accounts.getInfo().getReceiver(),
                "daily_limit", accounts.getInfo().getDailyLimit(),
                "used_today", accounts.getInfo().getUsedToday(),
                "auth_configured", accounts.getInfo().getAuth() != null
            ),
            "warn", Map.of(
                "receiver", accounts.getWarn().getReceiver(),
                "daily_limit", accounts.getWarn().getDailyLimit(),
                "used_today", accounts.getWarn().getUsedToday(),
                "auth_configured", accounts.getWarn().getAuth() != null
            ),
            "error", Map.of(
                "receiver", accounts.getError().getReceiver(),
                "daily_limit", accounts.getError().getDailyLimit(),
                "used_today", accounts.getError().getUsedToday(),
                "auth_configured", accounts.getError().getAuth() != null
            )
        ));
        
        return ResponseEntity.ok(config);
    }
}