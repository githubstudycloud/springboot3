package com.platform.sqlanalyzer.controller;

import com.platform.sqlanalyzer.config.SqlScriptConfig.SqlScript;
import com.platform.sqlanalyzer.model.DatabaseInfo;
import com.platform.sqlanalyzer.model.QueryRequest;
import com.platform.sqlanalyzer.model.QueryResult;
import com.platform.sqlanalyzer.service.MySqlAnalysisService;
import com.platform.sqlanalyzer.service.SqlExecutionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SQL分析器控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/sql-analyzer")
@RequiredArgsConstructor
public class SqlAnalyzerController {
    
    private final SqlExecutionService sqlExecutionService;
    private final MySqlAnalysisService mySqlAnalysisService;
    
    /**
     * 执行SQL查询
     */
    @PostMapping("/query")
    public ResponseEntity<QueryResult> executeQuery(@Valid @RequestBody QueryRequest request) {
        log.info("执行SQL查询: {}", request);
        QueryResult result = sqlExecutionService.executeQuery(request);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取所有脚本分类
     */
    @GetMapping("/scripts/categories")
    public ResponseEntity<List<String>> getScriptCategories() {
        List<String> categories = sqlExecutionService.getScriptCategories();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * 获取指定分类下的所有脚本
     */
    @GetMapping("/scripts/{category}")
    public ResponseEntity<List<Map<String, Object>>> getScriptsByCategory(@PathVariable String category) {
        List<Map<String, Object>> scripts = sqlExecutionService.getScriptsByCategory(category);
        return ResponseEntity.ok(scripts);
    }
    
    /**
     * 获取脚本详情
     */
    @GetMapping("/scripts/{category}/{name}")
    public ResponseEntity<SqlScript> getScriptDetails(
            @PathVariable String category, 
            @PathVariable String name) {
        SqlScript script = sqlExecutionService.getScriptDetails(category, name);
        if (script == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(script);
    }
    
    /**
     * 添加数据源
     */
    @PostMapping("/datasources")
    public ResponseEntity<Boolean> addDataSource(@Valid @RequestBody DataSourceRequest request) {
        boolean success = sqlExecutionService.addDataSource(
                request.getName(), 
                request.getUrl(), 
                request.getUsername(), 
                request.getPassword());
        return ResponseEntity.ok(success);
    }
    
    /**
     * 获取所有数据源名称
     */
    @GetMapping("/datasources")
    public ResponseEntity<Set<String>> getDataSourceNames() {
        Set<String> dataSourceNames = sqlExecutionService.getDataSourceNames();
        return ResponseEntity.ok(dataSourceNames);
    }
    
    /**
     * 移除数据源
     */
    @DeleteMapping("/datasources/{name}")
    public ResponseEntity<Boolean> removeDataSource(@PathVariable String name) {
        boolean success = sqlExecutionService.removeDataSource(name);
        return ResponseEntity.ok(success);
    }
    
    /**
     * 获取数据库信息
     */
    @GetMapping("/database/{dataSource}/{database}")
    public ResponseEntity<DatabaseInfo> getDatabaseInfo(
            @PathVariable String dataSource, 
            @PathVariable String database) {
        DatabaseInfo databaseInfo = mySqlAnalysisService.getDatabaseInfo(dataSource, database);
        return ResponseEntity.ok(databaseInfo);
    }
    
    /**
     * 分析表字段
     */
    @GetMapping("/database/{dataSource}/{database}/table/{table}/columns")
    public ResponseEntity<List<Map<String, Object>>> analyzeTableColumns(
            @PathVariable String dataSource, 
            @PathVariable String database,
            @PathVariable String table) {
        List<Map<String, Object>> columns = mySqlAnalysisService.analyzeTableColumns(dataSource, database, table);
        return ResponseEntity.ok(columns);
    }
    
    /**
     * 分析表索引
     */
    @GetMapping("/database/{dataSource}/{database}/table/{table}/indexes")
    public ResponseEntity<List<Map<String, Object>>> analyzeTableIndexes(
            @PathVariable String dataSource, 
            @PathVariable String database,
            @PathVariable String table) {
        List<Map<String, Object>> indexes = mySqlAnalysisService.analyzeTableIndexes(dataSource, database, table);
        return ResponseEntity.ok(indexes);
    }
    
    /**
     * 查找未使用索引
     */
    @GetMapping("/database/{dataSource}/{database}/unused-indexes")
    public ResponseEntity<List<Map<String, Object>>> findUnusedIndexes(
            @PathVariable String dataSource, 
            @PathVariable String database) {
        List<Map<String, Object>> unusedIndexes = mySqlAnalysisService.findUnusedIndexes(dataSource, database);
        return ResponseEntity.ok(unusedIndexes);
    }
    
    /**
     * 数据源请求
     */
    @Data
    public static class DataSourceRequest {
        @NotBlank(message = "数据源名称不能为空")
        private String name;
        
        @NotBlank(message = "数据库URL不能为空")
        private String url;
        
        @NotBlank(message = "用户名不能为空")
        private String username;
        
        private String password;
    }
}
