package com.platform.scheduler.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.scheduler.config.datasource.multi.DataSourceDefinition;
import com.platform.scheduler.service.DataSourceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 数据源管理控制器
 * 
 * @author platform
 */
@Tag(name = "数据源管理", description = "数据源管理相关API")
@RestController
@RequestMapping("/api/datasources")
public class DataSourceController {
    
    @Autowired
    private DataSourceService dataSourceService;
    
    @Operation(summary = "获取所有数据源")
    @GetMapping
    public ResponseEntity<List<DataSourceDefinition>> getAllDataSources() {
        return ResponseEntity.ok(dataSourceService.getAllDataSources());
    }
    
    @Operation(summary = "获取数据源")
    @GetMapping("/{id}")
    public ResponseEntity<DataSourceDefinition> getDataSource(@PathVariable String id) {
        DataSourceDefinition definition = dataSourceService.getDataSource(id);
        if (definition != null) {
            return ResponseEntity.ok(definition);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "注册数据源")
    @PostMapping
    public ResponseEntity<Boolean> registerDataSource(@RequestBody DataSourceDefinition definition) {
        boolean result = dataSourceService.registerDataSource(definition);
        if (result) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }
    
    @Operation(summary = "更新数据源")
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateDataSource(
            @PathVariable String id, 
            @RequestBody DataSourceDefinition definition) {
        
        // 检查ID是否一致
        if (!id.equals(definition.getId())) {
            return ResponseEntity.badRequest().body(false);
        }
        
        boolean result = dataSourceService.updateDataSource(definition);
        if (result) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }
    
    @Operation(summary = "删除数据源")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> removeDataSource(@PathVariable String id) {
        boolean result = dataSourceService.removeDataSource(id);
        if (result) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }
    
    @Operation(summary = "测试数据源连接")
    @PostMapping("/test")
    public ResponseEntity<Boolean> testConnection(@RequestBody DataSourceDefinition definition) {
        boolean result = dataSourceService.testConnection(definition);
        return ResponseEntity.ok(result);
    }
    
    @Operation(summary = "检查数据源连接")
    @GetMapping("/{id}/check")
    public ResponseEntity<Boolean> checkDataSource(@PathVariable String id) {
        boolean result = dataSourceService.checkDataSource(id);
        return ResponseEntity.ok(result);
    }
    
    @Operation(summary = "检查所有数据源连接")
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkAllDataSources() {
        return ResponseEntity.ok(dataSourceService.checkAllDataSources());
    }
    
    @Operation(summary = "获取默认数据源")
    @GetMapping("/default")
    public ResponseEntity<String> getDefaultDataSource() {
        return ResponseEntity.ok(dataSourceService.getDefaultDataSourceId());
    }
    
    @Operation(summary = "设置默认数据源")
    @PutMapping("/default/{id}")
    public ResponseEntity<Boolean> setDefaultDataSource(@PathVariable String id) {
        boolean result = dataSourceService.setDefaultDataSource(id);
        if (result) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }
    
    @Operation(summary = "保存数据源配置")
    @PostMapping("/save-config")
    public ResponseEntity<Boolean> saveDataSourceConfig() {
        boolean result = dataSourceService.saveDataSourceConfig();
        if (result) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }
    
    @Operation(summary = "加载数据源配置")
    @PostMapping("/load-config")
    public ResponseEntity<Boolean> loadDataSourceConfig() {
        boolean result = dataSourceService.loadDataSourceConfig();
        if (result) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }
}
