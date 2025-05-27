package com.framework.excel.controller;

import com.framework.excel.entity.ExcelTemplateConfig;
import com.framework.excel.enums.ConfigSourceType;
import com.framework.excel.service.ExcelConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Excel配置管理控制器
 * 
 * @author framework
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/excel/config")
public class ExcelConfigController {
    
    @Autowired
    private ExcelConfigService excelConfigService;
    
    /**
     * 获取所有配置
     */
    @GetMapping("/list")
    public ResponseEntity<List<ExcelTemplateConfig>> getAllConfigs() {
        List<ExcelTemplateConfig> configs = excelConfigService.getAllConfigs();
        return ResponseEntity.ok(configs);
    }
    
    /**
     * 获取启用的配置
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<ExcelTemplateConfig>> getEnabledConfigs() {
        List<ExcelTemplateConfig> configs = excelConfigService.getEnabledConfigs();
        return ResponseEntity.ok(configs);
    }
    
    /**
     * 根据模板Key获取配置
     */
    @GetMapping("/{templateKey}")
    public ResponseEntity<ExcelTemplateConfig> getConfig(@PathVariable String templateKey) {
        ExcelTemplateConfig config = excelConfigService.getConfig(templateKey);
        if (config != null) {
            return ResponseEntity.ok(config);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 保存配置
     */
    @PostMapping
    public ResponseEntity<String> saveConfig(@RequestBody ExcelTemplateConfig config) {
        boolean success = excelConfigService.saveConfig(config);
        if (success) {
            return ResponseEntity.ok("配置保存成功");
        } else {
            return ResponseEntity.badRequest().body("配置保存失败");
        }
    }
    
    /**
     * 更新配置
     */
    @PutMapping("/{templateKey}")
    public ResponseEntity<String> updateConfig(@PathVariable String templateKey, 
                                             @RequestBody ExcelTemplateConfig config) {
        config.setTemplateKey(templateKey);
        boolean success = excelConfigService.updateConfig(config);
        if (success) {
            return ResponseEntity.ok("配置更新成功");
        } else {
            return ResponseEntity.badRequest().body("配置更新失败");
        }
    }
    
    /**
     * 删除配置
     */
    @DeleteMapping("/{templateKey}")
    public ResponseEntity<String> deleteConfig(@PathVariable String templateKey) {
        boolean success = excelConfigService.deleteConfig(templateKey);
        if (success) {
            return ResponseEntity.ok("配置删除成功");
        } else {
            return ResponseEntity.badRequest().body("配置删除失败");
        }
    }
    
    /**
     * 启用配置
     */
    @PutMapping("/{templateKey}/enable")
    public ResponseEntity<String> enableConfig(@PathVariable String templateKey) {
        boolean success = excelConfigService.enableConfig(templateKey);
        if (success) {
            return ResponseEntity.ok("配置已启用");
        } else {
            return ResponseEntity.badRequest().body("配置启用失败");
        }
    }
    
    /**
     * 禁用配置
     */
    @PutMapping("/{templateKey}/disable")
    public ResponseEntity<String> disableConfig(@PathVariable String templateKey) {
        boolean success = excelConfigService.disableConfig(templateKey);
        if (success) {
            return ResponseEntity.ok("配置已禁用");
        } else {
            return ResponseEntity.badRequest().body("配置禁用失败");
        }
    }
    
    /**
     * 切换配置源
     */
    @PutMapping("/source/{sourceType}")
    public ResponseEntity<String> switchConfigSource(@PathVariable String sourceType) {
        ConfigSourceType type = ConfigSourceType.fromCode(sourceType);
        boolean success = excelConfigService.switchConfigSource(type);
        if (success) {
            return ResponseEntity.ok("配置源切换成功: " + type.getDescription());
        } else {
            return ResponseEntity.badRequest().body("配置源切换失败");
        }
    }
    
    /**
     * 获取当前配置源状态
     */
    @GetMapping("/source/status")
    public ResponseEntity<String> getConfigSourceStatus() {
        String status = excelConfigService.getConfigSourceStatus();
        return ResponseEntity.ok(status);
    }
    
    /**
     * 获取当前配置源类型
     */
    @GetMapping("/source/current")
    public ResponseEntity<ConfigSourceType> getCurrentConfigSourceType() {
        ConfigSourceType type = excelConfigService.getCurrentConfigSourceType();
        return ResponseEntity.ok(type);
    }
    
    /**
     * 同步文件配置到数据库
     */
    @PostMapping("/sync")
    public ResponseEntity<String> syncFileConfigToDatabase() {
        boolean success = excelConfigService.syncFileConfigToDatabase();
        if (success) {
            return ResponseEntity.ok("配置同步成功");
        } else {
            return ResponseEntity.badRequest().body("配置同步失败");
        }
    }
    
    /**
     * 刷新配置缓存
     */
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshCache() {
        boolean success = excelConfigService.refreshCache();
        if (success) {
            return ResponseEntity.ok("缓存刷新成功");
        } else {
            return ResponseEntity.badRequest().body("缓存刷新失败");
        }
    }
} 