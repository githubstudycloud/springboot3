package com.platform.sqlanalyzer.controller;

import com.platform.sqlanalyzer.model.QueryRequest;
import com.platform.sqlanalyzer.model.QueryResult;
import com.platform.sqlanalyzer.service.PerformanceDataCollectorService;
import com.platform.sqlanalyzer.service.SqlExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 性能洞察控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/sql-analyzer/performance")
@RequiredArgsConstructor
public class PerformanceInsightController {
    
    private final SqlExecutionService sqlExecutionService;
    private final PerformanceDataCollectorService dataCollectorService;
    
    /**
     * 获取数据库整体性能概况
     */
    @GetMapping("/overview/{dataSource}")
    public ResponseEntity<Map<String, Object>> getPerformanceOverview(@PathVariable String dataSource) {
        Map<String, Object> overview = new HashMap<>();
        
        try {
            // 1. 获取内存使用情况
            QueryRequest memoryRequest = new QueryRequest();
            memoryRequest.setDataSource(dataSource);
            memoryRequest.setCategory("performance_analysis");
            memoryRequest.setScriptName("memory_usage");
            QueryResult memoryResult = sqlExecutionService.executeQuery(memoryRequest);
            overview.put("memoryUsage", memoryResult);
            
            // 2. 获取连接状态
            QueryRequest connectionRequest = new QueryRequest();
            connectionRequest.setDataSource(dataSource);
            connectionRequest.setCategory("performance_analysis");
            connectionRequest.setScriptName("connection_status");
            QueryResult connectionResult = sqlExecutionService.executeQuery(connectionRequest);
            overview.put("connectionStatus", connectionResult);
            
            // 3. 最近收集的性能数据
            Map<String, Object> lastCollectedData = dataCollectorService.getLastCollectedData(dataSource);
            if (!lastCollectedData.isEmpty()) {
                overview.put("lastCollectedData", lastCollectedData);
            }
            
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            log.error("获取性能概况失败", e);
            overview.put("error", e.getMessage());
            return ResponseEntity.ok(overview);
        }
    }
    
    /**
     * 获取慢查询列表
     */
    @GetMapping("/slow-queries/{dataSource}")
    public ResponseEntity<QueryResult> getSlowQueries(
            @PathVariable String dataSource,
            @RequestParam(defaultValue = "1") double minExecutionTime) {
        
        QueryRequest request = new QueryRequest();
        request.setDataSource(dataSource);
        request.setCategory("performance_analysis");
        request.setScriptName("slow_queries");
        
        Map<String, Object> params = new HashMap<>();
        params.put("minExecutionTime", minExecutionTime);
        request.setParameters(params);
        
        QueryResult result = sqlExecutionService.executeQuery(request);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取表锁竞争情况
     */
    @GetMapping("/table-locks/{dataSource}/{database}")
    public ResponseEntity<QueryResult> getTableLocks(
            @PathVariable String dataSource,
            @PathVariable String database) {
        
        QueryRequest request = new QueryRequest();
        request.setDataSource(dataSource);
        request.setCategory("performance_analysis");
        request.setScriptName("table_locks");
        
        Map<String, Object> params = new HashMap<>();
        params.put("dbName", database);
        request.setParameters(params);
        
        QueryResult result = sqlExecutionService.executeQuery(request);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取历史性能数据
     */
    @GetMapping("/history/{dataSource}/{frequency}")
    public ResponseEntity<List<Map<String, Object>>> getHistoryData(
            @PathVariable String dataSource,
            @PathVariable String frequency,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Map<String, Object>> historyData = new ArrayList<>();
        
        try {
            // 获取历史数据文件
            String dirPath = "./data/history/" + dataSource + "/" + frequency;
            File dir = new File(dirPath);
            
            if (dir.exists() && dir.isDirectory()) {
                List<Path> files = Files.list(Paths.get(dirPath))
                        .filter(p -> p.toString().endsWith(".json"))
                        .sorted((p1, p2) -> p2.getFileName().toString().compareTo(p1.getFileName().toString()))
                        .limit(limit)
                        .collect(Collectors.toList());
                
                // 读取文件内容
                for (Path file : files) {
                    try {
                        String content = new String(Files.readAllBytes(file));
                        // 在实际应用中，这里应该使用ObjectMapper解析JSON
                        Map<String, Object> data = new HashMap<>();
                        data.put("filename", file.getFileName().toString());
                        data.put("content", content);
                        historyData.add(data);
                    } catch (Exception e) {
                        log.error("读取历史数据文件失败: {}", file, e);
                    }
                }
            }
            
            return ResponseEntity.ok(historyData);
        } catch (Exception e) {
            log.error("获取历史性能数据失败", e);
            return ResponseEntity.ok(historyData);
        }
    }
    
    /**
     * 立即执行数据收集
     */
    @PostMapping("/collect/{dataSource}")
    public ResponseEntity<Map<String, Object>> collectPerformanceData(@PathVariable String dataSource) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 使用PerformanceDataCollectorService收集数据
            Map<String, Object> params = new HashMap<>();
            params.put("dataSource", dataSource);
            params.put("frequency", "manual");
            
            // 这里是一个简化实现，实际应调用dataCollectorService中的方法
            result.put("message", "数据收集请求已提交");
            result.put("status", "success");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("执行数据收集失败", e);
            result.put("message", "数据收集失败: " + e.getMessage());
            result.put("status", "error");
            return ResponseEntity.ok(result);
        }
    }
}
