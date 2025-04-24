package com.platform.sqlanalyzer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 性能数据收集服务
 * 定期收集数据库性能指标，并存储为历史数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceDataCollectorService {
    
    private final SqlExecutionService sqlExecutionService;
    private final ObjectMapper objectMapper;
    
    @Value("${sql-analyzer.history-data-path:./data/history}")
    private String historyDataPath;
    
    @Value("${sql-analyzer.data-collection.enabled:false}")
    private boolean dataCollectionEnabled;
    
    /**
     * 缓存最近一次收集的性能数据
     */
    private final Map<String, Map<String, Object>> lastCollectedData = new ConcurrentHashMap<>();
    
    /**
     * 每小时收集一次性能数据
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void collectHourlyPerformanceData() {
        if (!dataCollectionEnabled) {
            return;
        }
        
        log.info("开始收集每小时性能数据...");
        try {
            // 获取所有数据源
            for (String dataSource : sqlExecutionService.getDataSourceNames()) {
                collectDataSourcePerformanceData(dataSource, "hourly");
            }
            log.info("每小时性能数据收集完成");
        } catch (Exception e) {
            log.error("收集每小时性能数据失败", e);
        }
    }
    
    /**
     * 每天收集一次性能数据
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void collectDailyPerformanceData() {
        if (!dataCollectionEnabled) {
            return;
        }
        
        log.info("开始收集每日性能数据...");
        try {
            // 获取所有数据源
            for (String dataSource : sqlExecutionService.getDataSourceNames()) {
                collectDataSourcePerformanceData(dataSource, "daily");
            }
            log.info("每日性能数据收集完成");
        } catch (Exception e) {
            log.error("收集每日性能数据失败", e);
        }
    }
    
    /**
     * 收集指定数据源的性能数据
     */
    private void collectDataSourcePerformanceData(String dataSource, String frequency) {
        try {
            // 1. 收集内存使用情况
            Map<String, Object> memoryUsage = collectMemoryUsage(dataSource);
            
            // 2. 收集连接状态
            Map<String, Object> connectionStatus = collectConnectionStatus(dataSource);
            
            // 3. 收集系统状态
            Map<String, Object> systemStatus = collectSystemStatus(dataSource);
            
            // 4. 构建性能数据
            Map<String, Object> performanceData = new HashMap<>();
            performanceData.put("timestamp", LocalDateTime.now().toString());
            performanceData.put("dataSource", dataSource);
            performanceData.put("memoryUsage", memoryUsage);
            performanceData.put("connectionStatus", connectionStatus);
            performanceData.put("systemStatus", systemStatus);
            
            // 5. 保存到缓存
            lastCollectedData.put(dataSource, performanceData);
            
            // 6. 保存到文件
            savePerformanceData(dataSource, performanceData, frequency);
        } catch (Exception e) {
            log.error("收集数据源[{}]性能数据失败", dataSource, e);
        }
    }
    
    /**
     * 收集内存使用情况
     */
    private Map<String, Object> collectMemoryUsage(String dataSource) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 使用memory_usage脚本
            Map<String, Object> params = new HashMap<>();
            result.put("memoryUsageData", sqlExecutionService.executeQuery(createQueryRequest(
                    dataSource, "performance_analysis", "memory_usage", params)));
            
            return result;
        } catch (Exception e) {
            log.error("收集内存使用情况失败", e);
            return new HashMap<>();
        }
    }
    
    /**
     * 收集连接状态
     */
    private Map<String, Object> collectConnectionStatus(String dataSource) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 使用connection_status脚本
            Map<String, Object> params = new HashMap<>();
            result.put("connectionData", sqlExecutionService.executeQuery(createQueryRequest(
                    dataSource, "performance_analysis", "connection_status", params)));
            
            return result;
        } catch (Exception e) {
            log.error("收集连接状态失败", e);
            return new HashMap<>();
        }
    }
    
    /**
     * 收集系统状态
     */
    private Map<String, Object> collectSystemStatus(String dataSource) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 查询全局状态变量
            String sql = "SHOW GLOBAL STATUS";
            JdbcTemplate jdbcTemplate = getJdbcTemplate(dataSource);
            if (jdbcTemplate != null) {
                List<Map<String, Object>> statusVariables = jdbcTemplate.queryForList(sql);
                
                // 转换为Key-Value形式
                Map<String, Object> statusMap = new HashMap<>();
                for (Map<String, Object> row : statusVariables) {
                    statusMap.put(String.valueOf(row.get("Variable_name")), 
                                 row.get("Value"));
                }
                
                result.put("globalStatus", statusMap);
            }
            
            return result;
        } catch (Exception e) {
            log.error("收集系统状态失败", e);
            return new HashMap<>();
        }
    }
    
    /**
     * 保存性能数据到文件
     */
    private void savePerformanceData(String dataSource, Map<String, Object> data, String frequency) {
        try {
            // 确保目录存在
            String dirPath = historyDataPath + "/" + dataSource + "/" + frequency;
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 生成文件名
            String timestamp = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = dirPath + "/perf_" + timestamp + ".json";
            
            // 写入文件
            Path path = Paths.get(fileName);
            String jsonData = objectMapper.writeValueAsString(data);
            Files.write(path, jsonData.getBytes(), StandardOpenOption.CREATE);
            
            log.debug("性能数据已保存到文件: {}", fileName);
        } catch (IOException e) {
            log.error("保存性能数据到文件失败", e);
        }
    }
    
    /**
     * 获取最近一次收集的性能数据
     */
    public Map<String, Object> getLastCollectedData(String dataSource) {
        return lastCollectedData.getOrDefault(dataSource, new HashMap<>());
    }
    
    /**
     * 获取指定数据源的JdbcTemplate
     */
    private JdbcTemplate getJdbcTemplate(String dataSource) {
        try {
            // 这里是一个简化的实现，实际应该通过SqlExecutionService获取
            return new JdbcTemplate(sqlExecutionService.getDataSource(dataSource));
        } catch (Exception e) {
            log.error("获取JdbcTemplate失败", e);
            return null;
        }
    }
    
    /**
     * 创建查询请求
     */
    private com.platform.sqlanalyzer.model.QueryRequest createQueryRequest(
            String dataSource, String category, String scriptName, Map<String, Object> params) {
        com.platform.sqlanalyzer.model.QueryRequest request = 
                new com.platform.sqlanalyzer.model.QueryRequest();
        request.setDataSource(dataSource);
        request.setCategory(category);
        request.setScriptName(scriptName);
        request.setParameters(params);
        return request;
    }
}
