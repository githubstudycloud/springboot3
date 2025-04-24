package com.platform.sqlanalyzer.controller;

import com.platform.sqlanalyzer.model.QueryRequest;
import com.platform.sqlanalyzer.model.QueryResult;
import com.platform.sqlanalyzer.service.SqlExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL优化建议控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/sql-analyzer/optimization")
@RequiredArgsConstructor
public class SqlOptimizationController {
    
    private final SqlExecutionService sqlExecutionService;
    
    /**
     * 获取缺失索引建议
     */
    @GetMapping("/missing-indexes/{dataSource}/{database}")
    public ResponseEntity<QueryResult> getMissingIndexes(
            @PathVariable String dataSource, 
            @PathVariable String database) {
        
        QueryRequest request = new QueryRequest();
        request.setDataSource(dataSource);
        request.setCategory("optimization");
        request.setScriptName("missing_indexes");
        
        Map<String, Object> params = new HashMap<>();
        params.put("dbName", database);
        request.setParameters(params);
        
        QueryResult result = sqlExecutionService.executeQuery(request);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取冗余索引建议
     */
    @GetMapping("/redundant-indexes/{dataSource}/{database}")
    public ResponseEntity<QueryResult> getRedundantIndexes(
            @PathVariable String dataSource, 
            @PathVariable String database) {
        
        QueryRequest request = new QueryRequest();
        request.setDataSource(dataSource);
        request.setCategory("optimization");
        request.setScriptName("redundant_indexes");
        
        Map<String, Object> params = new HashMap<>();
        params.put("dbName", database);
        request.setParameters(params);
        
        QueryResult result = sqlExecutionService.executeQuery(request);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取表优化建议
     */
    @GetMapping("/table-optimization/{dataSource}/{database}")
    public ResponseEntity<QueryResult> getTableOptimization(
            @PathVariable String dataSource, 
            @PathVariable String database) {
        
        QueryRequest request = new QueryRequest();
        request.setDataSource(dataSource);
        request.setCategory("optimization");
        request.setScriptName("table_optimization");
        
        Map<String, Object> params = new HashMap<>();
        params.put("dbName", database);
        request.setParameters(params);
        
        QueryResult result = sqlExecutionService.executeQuery(request);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 生成优化报告
     */
    @GetMapping("/report/{dataSource}/{database}")
    public ResponseEntity<Map<String, Object>> generateOptimizationReport(
            @PathVariable String dataSource, 
            @PathVariable String database) {
        
        Map<String, Object> report = new HashMap<>();
        
        try {
            // 1. 获取表优化建议
            QueryRequest tableOptRequest = new QueryRequest();
            tableOptRequest.setDataSource(dataSource);
            tableOptRequest.setCategory("optimization");
            tableOptRequest.setScriptName("table_optimization");
            
            Map<String, Object> params = new HashMap<>();
            params.put("dbName", database);
            tableOptRequest.setParameters(params);
            
            QueryResult tableOptResult = sqlExecutionService.executeQuery(tableOptRequest);
            report.put("tableOptimization", tableOptResult);
            
            // 2. 获取缺失索引建议
            QueryRequest missingIdxRequest = new QueryRequest();
            missingIdxRequest.setDataSource(dataSource);
            missingIdxRequest.setCategory("optimization");
            missingIdxRequest.setScriptName("missing_indexes");
            missingIdxRequest.setParameters(params);
            
            QueryResult missingIdxResult = sqlExecutionService.executeQuery(missingIdxRequest);
            report.put("missingIndexes", missingIdxResult);
            
            // 3. 获取冗余索引建议
            QueryRequest redundantIdxRequest = new QueryRequest();
            redundantIdxRequest.setDataSource(dataSource);
            redundantIdxRequest.setCategory("optimization");
            redundantIdxRequest.setScriptName("redundant_indexes");
            redundantIdxRequest.setParameters(params);
            
            QueryResult redundantIdxResult = sqlExecutionService.executeQuery(redundantIdxRequest);
            report.put("redundantIndexes", redundantIdxResult);
            
            // 4. 统计结果
            report.put("database", database);
            report.put("dataSource", dataSource);
            report.put("generatedTime", System.currentTimeMillis());
            report.put("status", "success");
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("生成优化报告失败", e);
            report.put("status", "error");
            report.put("error", e.getMessage());
            return ResponseEntity.ok(report);
        }
    }
}
