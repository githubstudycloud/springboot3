package com.platform.config.controller;

import com.platform.config.service.ConfigManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 配置管理控制器 - 重构版本
 * 
 * @author Platform Team
 * @since 2.0.0
 */
@RestController
@RequestMapping("/config/management")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "配置管理", description = "配置中心管理API v2.0")
public class ConfigController {
    
    private final ConfigManagementService configManagementService;
    
    @Operation(summary = "刷新配置", description = "刷新指定应用的配置")
    @PostMapping("/refresh/{application}")
    public ResponseEntity<Map<String, Object>> refreshConfig(
            @Parameter(description = "应用名称") @PathVariable String application,
            @Parameter(description = "操作人员") @RequestParam(defaultValue = "system") String operator) {
        
        Set<String> refreshedKeys = configManagementService.refreshConfig(application, operator);
        
        return ResponseEntity.ok(Map.of(
            "message", "配置刷新成功",
            "application", application,
            "operator", operator,
            "refreshedKeys", refreshedKeys,
            "refreshedCount", refreshedKeys.size()
        ));
    }
    
    @Operation(summary = "切换配置源", description = "在GitLab和本地配置源之间切换")
    @PostMapping("/switch-source")
    public ResponseEntity<Map<String, Object>> switchConfigSource(
            @Parameter(description = "配置源类型：gitlab/native") @RequestParam String source,
            @Parameter(description = "操作人员") @RequestParam(defaultValue = "system") String operator) {
        
        String oldSource = configManagementService.getCurrentConfigSource();
        configManagementService.switchConfigSource(source, operator);
        
        return ResponseEntity.ok(Map.of(
            "message", "配置源切换成功",
            "oldSource", oldSource,
            "newSource", source,
            "operator", operator
        ));
    }
    
    @Operation(summary = "获取配置状态", description = "获取配置中心当前状态")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getConfigStatus() {
        Map<String, Object> status = configManagementService.getConfigStatus();
        return ResponseEntity.ok(status);
    }
    
    @Operation(summary = "同步配置到GitLab", description = "将本地配置同步到GitLab")
    @PostMapping("/sync-to-gitlab")
    public ResponseEntity<Map<String, Object>> syncToGitlab(
            @Parameter(description = "操作人员") @RequestParam(defaultValue = "system") String operator) {
        
        String result = configManagementService.syncConfigToGitlab(operator);
        
        return ResponseEntity.ok(Map.of(
            "message", "配置同步到GitLab成功",
            "result", result,
            "operator", operator
        ));
    }
    
    @Operation(summary = "从GitLab拉取配置", description = "从GitLab拉取最新配置到本地")
    @PostMapping("/pull-from-gitlab")
    public ResponseEntity<Map<String, Object>> pullFromGitlab(
            @Parameter(description = "操作人员") @RequestParam(defaultValue = "system") String operator) {
        
        String result = configManagementService.pullConfigFromGitlab(operator);
        
        return ResponseEntity.ok(Map.of(
            "message", "从GitLab拉取配置成功",
            "result", result,
            "operator", operator
        ));
    }
    
    @Operation(summary = "备份配置", description = "备份当前所有配置")
    @PostMapping("/backup")
    public ResponseEntity<Map<String, Object>> backupConfig(
            @Parameter(description = "操作人员") @RequestParam(defaultValue = "system") String operator) {
        
        String backupPath = configManagementService.backupConfig(operator);
        
        return ResponseEntity.ok(Map.of(
            "message", "配置备份成功",
            "backupPath", backupPath,
            "operator", operator
        ));
    }
    
    @Operation(summary = "恢复配置", description = "从备份恢复配置")
    @PostMapping("/restore")
    public ResponseEntity<Map<String, Object>> restoreConfig(
            @Parameter(description = "备份路径") @RequestParam String backupPath,
            @Parameter(description = "操作人员") @RequestParam(defaultValue = "system") String operator) {
        
        configManagementService.restoreConfig(backupPath, operator);
        
        return ResponseEntity.ok(Map.of(
            "message", "配置恢复成功",
            "backupPath", backupPath,
            "operator", operator
        ));
    }
    
    @Operation(summary = "获取备份列表", description = "获取所有可用的配置备份")
    @GetMapping("/backups")
    public ResponseEntity<Map<String, Object>> listBackups() {
        List<String> backups = configManagementService.listBackups();
        
        return ResponseEntity.ok(Map.of(
            "message", "获取备份列表成功",
            "backups", backups,
            "count", backups.size()
        ));
    }
    
    @Operation(summary = "健康检查", description = "检查配置服务健康状态")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> status = configManagementService.getConfigStatus();
        
        // 简单的健康检查逻辑
        boolean isHealthy = status.get("gitlabAvailable") != null;
        
        Map<String, Object> health = Map.of(
            "status", isHealthy ? "UP" : "DOWN",
            "version", "2.0.0",
            "details", status
        );
        
        return ResponseEntity.ok(health);
    }
} 