package com.example.dbmonitor.controller;

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
    
    /**
     * 测试警告发送的接口
     * 接收JSON参数：senduser, token, content
     */
    @PostMapping("/alert")
    public ResponseEntity<Map<String, Object>> testAlert(@RequestBody TestAlertRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证参数
            if (request.getSenduser() == null || request.getSenduser().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "senduser 参数不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (request.getToken() == null || request.getToken().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "token 参数不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "content 参数不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 构建测试警告消息
            AlertMessage testAlert = AlertMessage.builder()
                    .alertType("TEST_ALERT")
                    .dataSource("TEST_DATASOURCE")
                    .message(String.format("测试消息 - 发送用户: %s, 内容: %s", 
                            request.getSenduser(), request.getContent()))
                    .timestamp(LocalDateTime.now())
                    .severity("info")
                    .build();
            
            // 发送到测试URL
            String testUrl = "http://localhost:8080/api/test/webhook"; // 默认测试URL
            if (request.getWebhookUrl() != null && !request.getWebhookUrl().trim().isEmpty()) {
                testUrl = request.getWebhookUrl();
            }
            
            httpUtil.sendAlert(testUrl, testAlert);
            
            response.put("success", true);
            response.put("message", "测试警告发送成功");
            response.put("senduser", request.getSenduser());
            response.put("token", request.getToken());
            response.put("content", request.getContent());
            response.put("timestamp", LocalDateTime.now());
            
            log.info("测试警告发送成功 - 用户: {}, 内容: {}", request.getSenduser(), request.getContent());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "发送失败: " + e.getMessage());
            log.error("测试警告发送失败", e);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 测试Webhook接收端点
     */
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> receiveWebhook(@RequestBody Object payload) {
        Map<String, Object> response = new HashMap<>();
        
        log.info("收到Webhook测试消息: {}", payload);
        
        response.put("received", true);
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Webhook接收成功");
        
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
     * 测试请求DTO
     */
    public static class TestAlertRequest {
        private String senduser;
        private String token;
        private String content;
        private String webhookUrl; // 可选的webhook URL
        
        // Getters and Setters
        public String getSenduser() { return senduser; }
        public void setSenduser(String senduser) { this.senduser = senduser; }
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getWebhookUrl() { return webhookUrl; }
        public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
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
}