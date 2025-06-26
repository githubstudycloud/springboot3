package com.example.dbmonitor.controller;

import com.example.dbmonitor.config.DataSourceManager;
import com.example.dbmonitor.entity.MonitorResult;
import com.example.dbmonitor.exception.ConfigurationException;
import com.example.dbmonitor.exception.DbMonitorException;
import com.example.dbmonitor.scheduler.DynamicMonitorScheduler;
import com.example.dbmonitor.service.DatabaseMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {
    
    private final DatabaseMonitorService monitorService;
    private final DataSourceManager dataSourceManager;
    private final DynamicMonitorScheduler scheduler;
    
    /**
     * 检查所有数据源
     */
    @GetMapping("/check/all")
    public ResponseEntity<List<MonitorResult>> checkAll() {
        List<MonitorResult> results = monitorService.checkAllDataSources();
        return ResponseEntity.ok(results);
    }
    
    /**
     * 检查单个数据源
     */
    @GetMapping("/check/{dataSource}")
    public ResponseEntity<MonitorResult> checkDataSource(@PathVariable String dataSource) 
            throws DbMonitorException {
        JdbcTemplate template = dataSourceManager.getJdbcTemplate(dataSource);
        if (template == null) {
            throw new ConfigurationException("Data source not found: " + dataSource);
        }
        
        MonitorResult result = monitorService.checkDataSource(dataSource, template);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取监控状态汇总
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        List<MonitorResult> results = monitorService.checkAllDataSources();
        
        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", System.currentTimeMillis());
        status.put("totalDataSources", results.size());
        status.put("healthyDataSources", results.stream().filter(MonitorResult::isHealthy).count());
        status.put("unhealthyDataSources", results.stream().filter(r -> !r.isHealthy()).count());
        
        // 按健康状态分组
        Map<String, List<String>> dataSourcesByHealth = new HashMap<>();
        dataSourcesByHealth.put("healthy", 
            results.stream()
                .filter(MonitorResult::isHealthy)
                .map(MonitorResult::getDataSourceName)
                .toList());
        dataSourcesByHealth.put("unhealthy", 
            results.stream()
                .filter(r -> !r.isHealthy())
                .map(MonitorResult::getDataSourceName)
                .toList());
        
        status.put("dataSourcesByHealth", dataSourcesByHealth);
        
        // 汇总问题
        long totalLongRunningQueries = results.stream()
                .mapToLong(r -> r.getLongRunningQueries().size())
                .sum();
        status.put("totalLongRunningQueries", totalLongRunningQueries);
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * 获取详细的监控结果
     */
    @GetMapping("/status/detailed")
    public ResponseEntity<Map<String, Object>> getDetailedStatus() {
        Map<String, Object> status = new HashMap<>();
        List<MonitorResult> results = monitorService.checkAllDataSources();
        
        status.put("totalDataSources", results.size());
        status.put("healthyDataSources", results.stream().filter(MonitorResult::isHealthy).count());
        status.put("results", results);
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Database Monitor");
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }
    
    /**
     * 动态调整监控间隔
     */
    @PutMapping("/schedule/{dataSource}")
    public ResponseEntity<Map<String, String>> updateSchedule(
            @PathVariable String dataSource,
            @RequestParam int interval) {
        
        if (interval < 10) {
            throw new IllegalArgumentException("Check interval must be at least 10 seconds");
        }
        
        scheduler.reschedule(dataSource, interval);
        
        Map<String, String> response = new HashMap<>();
        response.put("dataSource", dataSource);
        response.put("newInterval", String.valueOf(interval));
        response.put("status", "updated");
        
        return ResponseEntity.ok(response);
    }
}