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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    
    private final MonitorProperties monitorProperties;
    private final HttpUtil httpUtil;
    
    public void sendAlert(MonitorResult result) throws AlertSendException {
        if (!monitorProperties.getAlert().isEnabled()) {
            return;
        }
        
        // 获取数据源配置
        var dsConfig = monitorProperties.getDatasources().stream()
                .filter(ds -> ds.getName().equals(result.getDataSourceName()))
                .findFirst()
                .orElse(null);
        
        // 构建报警消息
        AlertMessage message = AlertMessage.builder()
                .alertType(determineAlertType(result))
                .dataSource(result.getDataSourceName())
                .message(buildAlertMessage(result))
                .timestamp(LocalDateTime.now())
                .severity(determineSeverity(result))
                .longRunningQueries(result.getLongRunningQueries())
                .additionalInfo(result.getMetrics())
                .tags(dsConfig != null && dsConfig.getTags() != null ? 
                      new ArrayList<>(dsConfig.getTags()) : new ArrayList<>())
                .metrics(extractMetrics(result))
                .build();
        
        // 根据规则发送到对应的端点
        List<MonitorProperties.AlertEndpoint> endpoints = determineEndpoints(result, message.getSeverity());
        
        List<AlertSendException> failures = new ArrayList<>();
        
        for (MonitorProperties.AlertEndpoint endpoint : endpoints) {
            try {
                httpUtil.sendAlert(endpoint.getUrl(), message);
                log.info("Alert sent to endpoint: {} for datasource: {}", 
                    endpoint.getName(), result.getDataSourceName());
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
    
    private String determineAlertType(MonitorResult result) {
        if (!result.isHealthy()) return "DATABASE_UNHEALTHY";
        if (!result.getLongRunningQueries().isEmpty()) return "LONG_RUNNING_QUERIES";
        if (!result.getIssues().isEmpty()) return "PERFORMANCE_ISSUES";
        return "GENERAL";
    }
    
    private String determineSeverity(MonitorResult result) {
        if (!result.isHealthy()) return "critical";
        
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
        StringBuilder sb = new StringBuilder();
        sb.append("数据库: ").append(result.getDataSourceName()).append("\n");
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
}