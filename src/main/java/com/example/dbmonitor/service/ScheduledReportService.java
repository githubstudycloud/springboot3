package com.example.dbmonitor.service;

import com.example.dbmonitor.config.DataSourceManager;
import com.example.dbmonitor.config.MonitorProperties;
import com.example.dbmonitor.entity.AlertMessage;
import com.example.dbmonitor.entity.MonitorResult;
import com.example.dbmonitor.entity.ProcessInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledReportService {
    
    private final AlertService alertService;
    private final DatabaseMonitorService databaseMonitorService;
    private final SystemHealthService systemHealthService;
    private final DataSourceManager dataSourceManager;
    private final MonitorProperties monitorProperties;
    
    private static final int LONG_RUNNING_THRESHOLD_MINUTES = 30; // 30分钟阈值
    
    /**
     * 定时报告 - 每小时的5分和35分执行
     * cron表达式：秒 分 时 日 月 周
     * "0 5,35 * * * *" 表示每小时的第5分钟和第35分钟执行
     */
    @Scheduled(cron = "0 5,35 * * * *")
    public void scheduledHealthReport() {
        log.info("开始执行定时健康报告...");
        
        try {
            // 1. 检查Java程序本身状态 (INFO级别)
            reportJavaApplicationStatus();
            
            // 等待1秒，避免报告过于密集
            Thread.sleep(1000);
            
            // 2. 检查MySQL连接状态 (INFO级别)
            reportMySqlConnectionStatus();
            
            Thread.sleep(1000);
            
            // 3. 分析长时间运行的SQL (WARNING/CRITICAL级别)
            reportLongRunningSqlAnalysis();
            
            Thread.sleep(1000);
            
            // 4. 系统资源检查 (WARNING/CRITICAL级别)
            reportSystemResourceStatus();
            
            Thread.sleep(1000);
            
            // 5. 磁盘空间检查 (WARNING/CRITICAL级别)
            reportDiskSpaceStatus();
            
            Thread.sleep(1000);
            
            // 6. 综合健康状态总结 (INFO级别)
            reportOverallHealthSummary();
            
            log.info("定时健康报告执行完成");
            
        } catch (Exception e) {
            log.error("执行定时健康报告时发生错误", e);
            
            // 发送报告失败警告
            sendReportFailureAlert(e);
        }
    }
    
    /**
     * 1. 报告Java程序本身状态
     */
    private void reportJavaApplicationStatus() {
        try {
            Map<String, Object> jvmInfo = collectJvmInfo();
            
            AlertMessage message = AlertMessage.builder()
                    .alertType("JAVA_APPLICATION_STATUS")
                    .dataSource("JAVA_APPLICATION")
                    .message(buildJavaStatusMessage(jvmInfo))
                    .timestamp(LocalDateTime.now())
                    .severity("info")
                    .additionalInfo(jvmInfo)
                    .build();
            
            // 发送给info级别接收者
            sendInfoLevelAlert(message);
            
            log.debug("Java应用状态报告已发送");
            
        } catch (Exception e) {
            log.error("报告Java程序状态时发生错误", e);
        }
    }
    
    /**
     * 2. 报告MySQL连接状态
     */
    private void reportMySqlConnectionStatus() {
        Map<String, String> connectionResults = new HashMap<>();
        boolean allHealthy = true;
        
        try {
            Map<String, JdbcTemplate> templates = dataSourceManager.getAllJdbcTemplates();
            
            for (Map.Entry<String, JdbcTemplate> entry : templates.entrySet()) {
                String dataSourceName = entry.getKey();
                JdbcTemplate template = entry.getValue();
                
                try {
                    // 测试连接
                    template.queryForObject("SELECT 1", Integer.class);
                    connectionResults.put(dataSourceName, "连接正常");
                    log.debug("数据源 {} 连接正常", dataSourceName);
                    
                } catch (Exception e) {
                    connectionResults.put(dataSourceName, "连接异常: " + e.getMessage());
                    allHealthy = false;
                    log.warn("数据源 {} 连接异常: {}", dataSourceName, e.getMessage());
                }
            }
            
            String severity = allHealthy ? "info" : "critical";
            
            AlertMessage message = AlertMessage.builder()
                    .alertType("MYSQL_CONNECTION_STATUS")
                    .dataSource("ALL_MYSQL_CONNECTIONS")
                    .message(buildConnectionStatusMessage(connectionResults, allHealthy))
                    .timestamp(LocalDateTime.now())
                    .severity(severity)
                    .additionalInfo(Map.of("connectionResults", connectionResults))
                    .build();
            
            if (allHealthy) {
                sendInfoLevelAlert(message);
            } else {
                sendCriticalLevelAlert(message);
            }
            
        } catch (Exception e) {
            log.error("报告MySQL连接状态时发生错误", e);
        }
    }
    
    /**
     * 3. 分析长时间运行的SQL
     */
    private void reportLongRunningSqlAnalysis() {
        Map<String, List<ProcessInfo>> longRunningSqlByDataSource = new HashMap<>();
        boolean hasLongRunningSql = false;
        
        try {
            Map<String, JdbcTemplate> templates = dataSourceManager.getAllJdbcTemplates();
            
            for (Map.Entry<String, JdbcTemplate> entry : templates.entrySet()) {
                String dataSourceName = entry.getKey();
                JdbcTemplate template = entry.getValue();
                
                try {
                    List<ProcessInfo> longRunningQueries = analyzeLongRunningSql(template);
                    
                    if (!longRunningQueries.isEmpty()) {
                        longRunningSqlByDataSource.put(dataSourceName, longRunningQueries);
                        hasLongRunningSql = true;
                    }
                    
                } catch (Exception e) {
                    log.warn("分析数据源 {} 的长时间运行SQL时发生错误: {}", dataSourceName, e.getMessage());
                }
            }
            
            String severity;
            String alertType;
            
            if (!hasLongRunningSql) {
                severity = "info";
                alertType = "LONG_RUNNING_SQL_NORMAL";
            } else {
                // 根据最长SQL运行时间决定严重程度
                int maxRunningTime = longRunningSqlByDataSource.values().stream()
                        .flatMap(List::stream)
                        .mapToInt(p -> p.getTime() != null ? p.getTime().intValue() : 0)
                        .max()
                        .orElse(0);
                
                if (maxRunningTime > 3600) { // 超过1小时
                    severity = "critical";
                } else {
                    severity = "warning";
                }
                alertType = "LONG_RUNNING_SQL_DETECTED";
            }
            
            AlertMessage message = AlertMessage.builder()
                    .alertType(alertType)
                    .dataSource("ALL_MYSQL_CONNECTIONS")
                    .message(buildLongRunningSqlMessage(longRunningSqlByDataSource, hasLongRunningSql))
                    .timestamp(LocalDateTime.now())
                    .severity(severity)
                    .additionalInfo(Map.of("longRunningSqlDetails", longRunningSqlByDataSource))
                    .build();
            
            switch (severity) {
                case "info" -> sendInfoLevelAlert(message);
                case "warning" -> sendWarningLevelAlert(message);
                case "critical" -> sendCriticalLevelAlert(message);
            }
            
        } catch (Exception e) {
            log.error("分析长时间运行SQL时发生错误", e);
        }
    }
    
    /**
     * 4. 报告系统资源状态
     */
    private void reportSystemResourceStatus() {
        try {
            Map<String, Object> systemHealth = systemHealthService.checkSystemHealth();
            
            @SuppressWarnings("unchecked")
            List<String> issues = (List<String>) systemHealth.getOrDefault("issues", new ArrayList<>());
            boolean healthy = (Boolean) systemHealth.getOrDefault("healthy", false);
            
            String severity = healthy ? "info" : (issues.size() > 2 ? "critical" : "warning");
            
            AlertMessage message = AlertMessage.builder()
                    .alertType("SYSTEM_RESOURCE_STATUS")
                    .dataSource("SYSTEM_RESOURCES")
                    .message(buildSystemResourceMessage(systemHealth, healthy))
                    .timestamp(LocalDateTime.now())
                    .severity(severity)
                    .additionalInfo(systemHealth)
                    .build();
            
            switch (severity) {
                case "info" -> sendInfoLevelAlert(message);
                case "warning" -> sendWarningLevelAlert(message);
                case "critical" -> sendCriticalLevelAlert(message);
            }
            
        } catch (Exception e) {
            log.error("报告系统资源状态时发生错误", e);
        }
    }
    
    /**
     * 5. 报告磁盘空间状态
     */
    private void reportDiskSpaceStatus() {
        try {
            Map<String, Object> systemHealth = systemHealthService.checkSystemHealth();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> diskInfo = (Map<String, Object>) systemHealth.get("disk");
            
            if (diskInfo != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> diskSpaces = (List<Map<String, Object>>) diskInfo.get("diskSpaces");
                
                boolean hasCriticalDiskIssue = false;
                boolean hasWarningDiskIssue = false;
                
                if (diskSpaces != null) {
                    for (Map<String, Object> disk : diskSpaces) {
                        Double usagePercent = (Double) disk.get("usagePercent");
                        if (usagePercent != null) {
                            if (usagePercent > 95) {
                                hasCriticalDiskIssue = true;
                            } else if (usagePercent > 85) {
                                hasWarningDiskIssue = true;
                            }
                        }
                    }
                }
                
                String severity;
                if (hasCriticalDiskIssue) {
                    severity = "critical";
                } else if (hasWarningDiskIssue) {
                    severity = "warning";
                } else {
                    severity = "info";
                }
                
                AlertMessage message = AlertMessage.builder()
                        .alertType("DISK_SPACE_STATUS")
                        .dataSource("DISK_STORAGE")
                        .message(buildDiskSpaceMessage(diskSpaces, severity))
                        .timestamp(LocalDateTime.now())
                        .severity(severity)
                        .additionalInfo(diskInfo)
                        .build();
                
                switch (severity) {
                    case "info" -> sendInfoLevelAlert(message);
                    case "warning" -> sendWarningLevelAlert(message);
                    case "critical" -> sendCriticalLevelAlert(message);
                }
            }
            
        } catch (Exception e) {
            log.error("报告磁盘空间状态时发生错误", e);
        }
    }
    
    /**
     * 6. 综合健康状态总结
     */
    private void reportOverallHealthSummary() {
        try {
            // 收集各项检查结果
            Map<String, Object> summary = new HashMap<>();
            summary.put("timestamp", LocalDateTime.now());
            summary.put("reportType", "SCHEDULED_HEALTH_SUMMARY");
            
            // Java应用状态
            Map<String, Object> jvmInfo = collectJvmInfo();
            summary.put("javaApplication", "运行正常");
            
            // MySQL连接状态
            Map<String, JdbcTemplate> templates = dataSourceManager.getAllJdbcTemplates();
            long healthyConnections = templates.entrySet().stream()
                    .mapToLong(entry -> {
                        try {
                            entry.getValue().queryForObject("SELECT 1", Integer.class);
                            return 1;
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .sum();
            
            summary.put("mysqlConnections", String.format("%d/%d 连接正常", 
                    healthyConnections, templates.size()));
            
            // 系统健康状态
            Map<String, Object> systemHealth = systemHealthService.checkSystemHealth();
            boolean systemHealthy = (Boolean) systemHealth.getOrDefault("healthy", false);
            summary.put("systemHealth", systemHealthy ? "正常" : "异常");
            
            AlertMessage message = AlertMessage.builder()
                    .alertType("HEALTH_SUMMARY_REPORT")
                    .dataSource("OVERALL_SYSTEM")
                    .message(buildHealthSummaryMessage(summary))
                    .timestamp(LocalDateTime.now())
                    .severity("info")
                    .additionalInfo(summary)
                    .build();
            
            sendInfoLevelAlert(message);
            
        } catch (Exception e) {
            log.error("生成综合健康状态总结时发生错误", e);
        }
    }
    
    /**
     * 分析长时间运行的SQL
     */
    private List<ProcessInfo> analyzeLongRunningSql(JdbcTemplate jdbcTemplate) throws DataAccessException {
        String sql = "SHOW FULL PROCESSLIST";
        
        try {
            List<ProcessInfo> allProcesses = jdbcTemplate.query(sql, new ProcessListRowMapper());
            
            // 筛选超过30分钟的查询
            return allProcesses.stream()
                    .filter(p -> p.getTime() != null && p.getTime() > LONG_RUNNING_THRESHOLD_MINUTES * 60)
                    .sorted((a, b) -> Long.compare(b.getTime(), a.getTime()))
                    .collect(Collectors.toList());
                    
        } catch (DataAccessException e) {
            log.warn("执行SHOW FULL PROCESSLIST失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * ProcessList行映射器
     */
    private static class ProcessListRowMapper implements RowMapper<ProcessInfo> {
        @Override
        public ProcessInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProcessInfo process = new ProcessInfo();
            process.setId(rs.getLong("Id"));
            process.setUser(rs.getString("User"));
            process.setHost(rs.getString("Host"));
            process.setDb(rs.getString("db"));
            process.setCommand(rs.getString("Command"));
            process.setTime(rs.getLong("Time"));
            process.setState(rs.getString("State"));
            process.setInfo(rs.getString("Info"));
            return process;
        }
    }
    
    /**
     * 收集JVM信息
     */
    private Map<String, Object> collectJvmInfo() {
        Map<String, Object> jvmInfo = new HashMap<>();
        
        try {
            // 运行时间
            long uptimeMs = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
            jvmInfo.put("uptimeMs", uptimeMs);
            jvmInfo.put("uptimeHours", uptimeMs / (1000 * 60 * 60));
            
            // 内存使用
            var memoryBean = java.lang.management.ManagementFactory.getMemoryMXBean();
            long usedHeap = memoryBean.getHeapMemoryUsage().getUsed();
            long maxHeap = memoryBean.getHeapMemoryUsage().getMax();
            double heapUsagePercent = (double) usedHeap / maxHeap * 100;
            
            jvmInfo.put("heapUsagePercent", Math.round(heapUsagePercent * 100.0) / 100.0);
            
            // 线程数
            int threadCount = java.lang.management.ManagementFactory.getThreadMXBean().getThreadCount();
            jvmInfo.put("threadCount", threadCount);
            
        } catch (Exception e) {
            log.warn("收集JVM信息时发生错误: {}", e.getMessage());
        }
        
        return jvmInfo;
    }
    
    // 构建各种消息的方法
    private String buildJavaStatusMessage(Map<String, Object> jvmInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("🟢 Java应用程序状态报告\n");
        sb.append("时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("状态: 正常运行\n");
        
        Long uptimeHours = (Long) jvmInfo.get("uptimeHours");
        if (uptimeHours != null) {
            sb.append("运行时间: ").append(uptimeHours).append(" 小时\n");
        }
        
        Double heapUsage = (Double) jvmInfo.get("heapUsagePercent");
        if (heapUsage != null) {
            sb.append("堆内存使用率: ").append(heapUsage).append("%\n");
        }
        
        Integer threadCount = (Integer) jvmInfo.get("threadCount");
        if (threadCount != null) {
            sb.append("线程数: ").append(threadCount).append("\n");
        }
        
        return sb.toString();
    }
    
    private String buildConnectionStatusMessage(Map<String, String> connectionResults, boolean allHealthy) {
        StringBuilder sb = new StringBuilder();
        
        if (allHealthy) {
            sb.append("🟢 MySQL连接状态报告\n");
        } else {
            sb.append("🔴 MySQL连接异常报告\n");
        }
        
        sb.append("时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("检查结果:\n");
        
        for (Map.Entry<String, String> entry : connectionResults.entrySet()) {
            sb.append("  - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        return sb.toString();
    }
    
    private String buildLongRunningSqlMessage(Map<String, List<ProcessInfo>> longRunningSqlByDataSource, boolean hasLongRunningSql) {
        StringBuilder sb = new StringBuilder();
        
        if (!hasLongRunningSql) {
            sb.append("🟢 长时间SQL检查报告\n");
            sb.append("时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
            sb.append("结果: 未发现持续时间超过30分钟的SQL查询\n");
            sb.append("状态: 正常\n");
        } else {
            sb.append("⚠️ 长时间SQL异常报告\n");
            sb.append("时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
            sb.append("发现超过30分钟的SQL查询:\n");
            
            for (Map.Entry<String, List<ProcessInfo>> entry : longRunningSqlByDataSource.entrySet()) {
                String dataSource = entry.getKey();
                List<ProcessInfo> processes = entry.getValue();
                
                sb.append("\n数据源: ").append(dataSource).append("\n");
                
                for (ProcessInfo process : processes) {
                    long minutes = process.getTime() / 60;
                    sb.append(String.format("  - ID: %d, 用户: %s, 运行时间: %d分钟, 状态: %s\n",
                            process.getId(),
                            process.getUser(),
                            minutes,
                            process.getState()));
                    
                    // 显示SQL语句（限制长度）
                    String sql = process.getInfo();
                    if (sql != null && !sql.trim().isEmpty()) {
                        String shortSql = sql.length() > 100 ? sql.substring(0, 100) + "..." : sql;
                        sb.append("    SQL: ").append(shortSql).append("\n");
                    }
                }
            }
        }
        
        return sb.toString();
    }
    
    private String buildSystemResourceMessage(Map<String, Object> systemHealth, boolean healthy) {
        StringBuilder sb = new StringBuilder();
        
        if (healthy) {
            sb.append("🟢 系统资源状态报告\n");
        } else {
            sb.append("⚠️ 系统资源异常报告\n");
        }
        
        sb.append("时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        
        @SuppressWarnings("unchecked")
        List<String> issues = (List<String>) systemHealth.getOrDefault("issues", new ArrayList<>());
        
        if (issues.isEmpty()) {
            sb.append("状态: 系统资源正常\n");
        } else {
            sb.append("检测到的问题:\n");
            for (String issue : issues) {
                sb.append("  - ").append(issue).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    private String buildDiskSpaceMessage(List<Map<String, Object>> diskSpaces, String severity) {
        StringBuilder sb = new StringBuilder();
        
        switch (severity) {
            case "critical" -> sb.append("🔴 磁盘空间严重不足\n");
            case "warning" -> sb.append("⚠️ 磁盘空间警告\n");
            default -> sb.append("🟢 磁盘空间正常\n");
        }
        
        sb.append("时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("磁盘使用情况:\n");
        
        if (diskSpaces != null) {
            for (Map<String, Object> disk : diskSpaces) {
                String name = (String) disk.get("name");
                Double usagePercent = (Double) disk.get("usagePercent");
                Long usableSpace = (Long) disk.get("usableSpace");
                
                sb.append(String.format("  - %s: %.1f%% 使用 (可用: %.1f GB)\n",
                        name,
                        usagePercent,
                        usableSpace / 1024.0 / 1024.0 / 1024.0));
            }
        }
        
        return sb.toString();
    }
    
    private String buildHealthSummaryMessage(Map<String, Object> summary) {
        StringBuilder sb = new StringBuilder();
        sb.append("📊 系统健康状态总结\n");
        sb.append("时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("Java应用: ").append(summary.get("javaApplication")).append("\n");
        sb.append("MySQL连接: ").append(summary.get("mysqlConnections")).append("\n");
        sb.append("系统健康: ").append(summary.get("systemHealth")).append("\n");
        sb.append("\n本次定时检查完成。\n");
        
        return sb.toString();
    }
    
    // 发送不同级别警报的方法 - 使用新的AlertService方法
    private void sendInfoLevelAlert(AlertMessage message) {
        try {
            alertService.sendScheduledAlert(
                    message.getDataSource(),
                    message.getAlertType(),
                    message.getMessage(),
                    "info",
                    message.getAdditionalInfo()
            );
        } catch (Exception e) {
            log.error("发送info级别警报失败", e);
        }
    }
    
    private void sendWarningLevelAlert(AlertMessage message) {
        try {
            alertService.sendScheduledAlert(
                    message.getDataSource(),
                    message.getAlertType(),
                    message.getMessage(),
                    "warning",
                    message.getAdditionalInfo()
            );
        } catch (Exception e) {
            log.error("发送warning级别警报失败", e);
        }
    }
    
    private void sendCriticalLevelAlert(AlertMessage message) {
        try {
            alertService.sendScheduledAlert(
                    message.getDataSource(),
                    message.getAlertType(),
                    message.getMessage(),
                    "critical",
                    message.getAdditionalInfo()
            );
        } catch (Exception e) {
            log.error("发送critical级别警报失败", e);
        }
    }
    
    private void sendReportFailureAlert(Exception e) {
        try {
            MonitorResult result = MonitorResult.builder()
                    .dataSourceName("SCHEDULED_REPORT_SYSTEM")
                    .timestamp(LocalDateTime.now())
                    .healthy(false)
                    .issues(List.of("定时报告执行失败: " + e.getMessage()))
                    .longRunningQueries(new ArrayList<>())
                    .metrics(new HashMap<>())
                    .checkDuration(0L)
                    .build();
            
            alertService.sendAlert(result);
            
        } catch (Exception sendException) {
            log.error("发送报告失败警报时也发生错误", sendException);
        }
    }
}