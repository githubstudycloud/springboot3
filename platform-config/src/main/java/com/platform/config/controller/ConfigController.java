package com.platform.config.controller;

import com.platform.config.service.ConfigManagementService;
import com.platform.config.service.ConfigVersionService;
import com.platform.config.service.ConfigAuditService;
import com.platform.config.entity.ConfigVersion;
import com.platform.config.event.ConfigChangeEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;

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
@Tag(name = "配置管理", description = "配置中心管理API v2.0 - 集成版本控制和审计功能")
public class ConfigController {
    
    private final ConfigManagementService configManagementService;
    private final ConfigVersionService versionService;
    private final ConfigAuditService auditService;
    private final ApplicationEventPublisher eventPublisher;
    
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
    
    // ============= 缓存管理相关API =============
    
    @Operation(summary = "获取缓存信息", description = "获取配置缓存的详细信息和统计")
    @GetMapping("/cache/info")
    public ResponseEntity<Map<String, Object>> getCacheInfo() {
        Map<String, Object> cacheInfo = configManagementService.getCacheInfo();
        
        return ResponseEntity.ok(Map.of(
            "message", "获取缓存信息成功",
            "cache", cacheInfo
        ));
    }
    
    @Operation(summary = "清理所有缓存", description = "清理配置服务的所有缓存")
    @PostMapping("/cache/clear")
    public ResponseEntity<Map<String, Object>> clearCache(
            @Parameter(description = "操作人员") @RequestParam(defaultValue = "system") String operator) {
        
        configManagementService.clearCache(operator);
        
        return ResponseEntity.ok(Map.of(
            "message", "缓存清理成功",
            "operator", operator
        ));
    }
    
    @Operation(summary = "清理指定配置缓存", description = "清理指定应用和环境的配置缓存")
    @PostMapping("/cache/clear/{application}")
    public ResponseEntity<Map<String, Object>> clearSpecificCache(
            @Parameter(description = "应用名称") @PathVariable String application,
            @Parameter(description = "环境名称") @RequestParam(defaultValue = "all") String profile,
            @Parameter(description = "操作人员") @RequestParam(defaultValue = "system") String operator) {
        
        configManagementService.clearCache(application, profile, operator);
        
        return ResponseEntity.ok(Map.of(
            "message", "指定配置缓存清理成功",
            "application", application,
            "profile", profile,
            "operator", operator
        ));
    }
    
    @Operation(summary = "预热缓存", description = "使用指定配置预热缓存")
    @PostMapping("/cache/warmup")
    public ResponseEntity<Map<String, Object>> warmUpCache(
            @Parameter(description = "预热配置") @RequestBody Map<String, Object> configs,
            @Parameter(description = "操作人员") @RequestParam(defaultValue = "system") String operator) {
        
        configManagementService.warmUpCache(configs, operator);
        
        return ResponseEntity.ok(Map.of(
            "message", "缓存预热成功",
            "configCount", configs.size(),
            "operator", operator
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

    // ============= 版本控制集成API =============
    
    @Operation(summary = "配置更新并创建版本", description = "更新配置的同时自动创建版本记录")
    @PostMapping("/update-with-version")
    public ResponseEntity<Map<String, Object>> updateConfigWithVersion(
            @Parameter(description = "应用名称", required = true) @RequestParam String application,
            @Parameter(description = "环境配置", required = true) @RequestParam String profile,
            @Parameter(description = "配置内容", required = true) @RequestBody String configContent,
            @Parameter(description = "操作人员") @RequestParam(defaultValue = "system") String operator,
            @Parameter(description = "版本描述") @RequestParam(required = false) String description,
            @Parameter(description = "自动激活") @RequestParam(defaultValue = "true") boolean autoActivate) {

        try {
            // 获取当前配置作为旧值
            String oldContent = null;
            Optional<ConfigVersion> currentVersion = versionService.getCurrentActiveVersion(application, profile);
            if (currentVersion.isPresent()) {
                oldContent = currentVersion.get().getContent();
            }

            // 创建新版本
            ConfigVersion newVersion = versionService.saveVersion(
                    application, profile, configContent, operator, description, null);

            // 如果需要自动激活
            if (autoActivate) {
                newVersion = versionService.activateVersion(newVersion.getId(), operator);
            }

            // 发布配置变更事件
            ConfigChangeEvent event = ConfigChangeEvent.builder()
                    .application(application)
                    .profile(profile)
                    .operation("UPDATE_WITH_VERSION")
                    .configKey("全量配置")
                    .oldValue(oldContent)
                    .newValue(configContent)
                    .operator(operator)
                    .versionId(newVersion.getId())
                    .source("API")
                    .build();
            
            eventPublisher.publishEvent(event);

            return ResponseEntity.ok(Map.of(
                    "message", "配置更新并创建版本成功",
                    "version", buildVersionResponse(newVersion),
                    "autoActivated", autoActivate
            ));

        } catch (Exception e) {
            log.error("配置更新失败: {}", e.getMessage(), e);
            
            // 记录失败审计
            auditService.recordFailureAudit(application, profile, 
                    "UPDATE_WITH_VERSION", operator, e.getMessage());
            
            throw e;
        }
    }

    @Operation(summary = "快速回滚配置", description = "快速回滚到上一个版本")
    @PostMapping("/quick-rollback")
    public ResponseEntity<Map<String, Object>> quickRollback(
            @Parameter(description = "应用名称", required = true) @RequestParam String application,
            @Parameter(description = "环境配置", required = true) @RequestParam String profile,
            @Parameter(description = "操作人员") @RequestParam(defaultValue = "system") String operator) {

        try {
            // 获取当前激活版本
            Optional<ConfigVersion> currentVersion = versionService.getCurrentActiveVersion(application, profile);
            if (currentVersion.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "未找到当前激活版本",
                        "application", application,
                        "profile", profile
                ));
            }

            // 获取父版本（上一个版本）
            Long parentVersionId = currentVersion.get().getParentVersionId();
            if (parentVersionId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "没有可回滚的版本",
                        "currentVersion", currentVersion.get().getVersion()
                ));
            }

            // 激活父版本
            ConfigVersion rolledBackVersion = versionService.activateVersion(parentVersionId, operator);

            // 发布回滚事件
            ConfigChangeEvent event = ConfigChangeEvent.builder()
                    .application(application)
                    .profile(profile)
                    .operation("QUICK_ROLLBACK")
                    .configKey("全量配置")
                    .oldValue(currentVersion.get().getContent())
                    .newValue(rolledBackVersion.getContent())
                    .operator(operator)
                    .versionId(rolledBackVersion.getId())
                    .source("API")
                    .build();
            
            eventPublisher.publishEvent(event);

            return ResponseEntity.ok(Map.of(
                    "message", "快速回滚成功",
                    "fromVersion", currentVersion.get().getVersion(),
                    "toVersion", rolledBackVersion.getVersion(),
                    "operator", operator
            ));

        } catch (Exception e) {
            log.error("快速回滚失败: {}", e.getMessage(), e);
            
            // 记录失败审计
            auditService.recordFailureAudit(application, profile, 
                    "QUICK_ROLLBACK", operator, e.getMessage());
            
            throw e;
        }
    }

    @Operation(summary = "获取配置和版本信息", description = "同时获取配置内容和版本信息")
    @GetMapping("/config-with-version")
    public ResponseEntity<Map<String, Object>> getConfigWithVersion(
            @Parameter(description = "应用名称", required = true) @RequestParam String application,
            @Parameter(description = "环境配置", required = true) @RequestParam String profile) {

        // 获取当前激活版本
        Optional<ConfigVersion> activeVersion = versionService.getCurrentActiveVersion(application, profile);
        
        if (activeVersion.isPresent()) {
            ConfigVersion version = activeVersion.get();
            return ResponseEntity.ok(Map.of(
                    "message", "配置和版本信息获取成功",
                    "application", application,
                    "profile", profile,
                    "configContent", version.getContent(),
                    "version", buildVersionResponse(version)
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "message", "未找到激活版本",
                    "application", application,
                    "profile", profile,
                    "configContent", null,
                    "version", null
            ));
        }
    }

    @Operation(summary = "批量操作状态", description = "获取批量操作的执行状态")
    @GetMapping("/batch-status")
    public ResponseEntity<Map<String, Object>> getBatchOperationStatus() {
        // 这里可以扩展批量操作状态跟踪功能
        return ResponseEntity.ok(Map.of(
                "message", "批量操作状态查询成功",
                "note", "批量操作状态跟踪功能正在完善中..."
        ));
    }

    /**
     * 构建版本响应对象
     */
    private Map<String, Object> buildVersionResponse(ConfigVersion version) {
        return Map.of(
                "id", version.getId(),
                "version", version.getVersion(),
                "application", version.getApplication(),
                "profile", version.getProfile(),
                "description", version.getDescription() != null ? version.getDescription() : "",
                "operator", version.getOperator(),
                "createTime", version.getCreateTime(),
                "active", version.getActive(),
                "contentHash", version.getContentHash()
        );
    }
} 