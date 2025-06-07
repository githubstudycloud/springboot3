package com.platform.config.controller;

import com.platform.config.entity.ConfigVersion;
import com.platform.config.service.ConfigVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 配置版本控制API
 * 提供配置版本管理的完整REST接口
 *
 * @author Platform Team
 * @since 2.0.0
 */
@RestController
@RequestMapping("/config/versions")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "配置版本控制", description = "配置版本管理API - 版本历史、回滚、比较等功能")
public class ConfigVersionController {

    private final ConfigVersionService versionService;

    @Operation(summary = "创建配置版本", description = "为指定应用和环境创建新的配置版本")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createVersion(
            @Parameter(description = "应用名称", required = true) 
            @RequestParam @NotBlank String application,
            
            @Parameter(description = "环境配置", required = true) 
            @RequestParam @NotBlank String profile,
            
            @Parameter(description = "配置内容", required = true) 
            @RequestBody @NotBlank String content,
            
            @Parameter(description = "操作人员") 
            @RequestParam(defaultValue = "system") String operator,
            
            @Parameter(description = "版本描述") 
            @RequestParam(required = false) String description,
            
            @Parameter(description = "版本标签") 
            @RequestParam(required = false) String tag) {

        ConfigVersion version = versionService.saveVersion(
                application, profile, content, operator, description, tag);

        return ResponseEntity.ok(Map.of(
                "message", "配置版本创建成功",
                "version", buildVersionResponse(version)
        ));
    }

    @Operation(summary = "获取版本历史", description = "分页查询指定应用环境的版本历史")
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getVersionHistory(
            @Parameter(description = "应用名称", required = true) 
            @RequestParam @NotBlank String application,
            
            @Parameter(description = "环境配置", required = true) 
            @RequestParam @NotBlank String profile,
            
            @Parameter(description = "分页参数") 
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ConfigVersion> versionPage = versionService.getVersionHistory(application, profile, pageable);

        return ResponseEntity.ok(Map.of(
                "message", "版本历史查询成功",
                "data", versionPage.map(this::buildVersionResponse),
                "pagination", Map.of(
                        "totalElements", versionPage.getTotalElements(),
                        "totalPages", versionPage.getTotalPages(),
                        "currentPage", versionPage.getNumber(),
                        "size", versionPage.getSize()
                )
        ));
    }

    @Operation(summary = "获取当前激活版本", description = "获取指定应用环境当前激活的版本")
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getCurrentActiveVersion(
            @Parameter(description = "应用名称", required = true) 
            @RequestParam @NotBlank String application,
            
            @Parameter(description = "环境配置", required = true) 
            @RequestParam @NotBlank String profile) {

        Optional<ConfigVersion> activeVersion = versionService.getCurrentActiveVersion(application, profile);

        if (activeVersion.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "message", "当前激活版本查询成功",
                    "version", buildVersionResponse(activeVersion.get())
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "message", "未找到激活版本",
                    "version", null
            ));
        }
    }

    @Operation(summary = "激活指定版本", description = "将指定版本设置为当前激活版本")
    @PostMapping("/{versionId}/activate")
    public ResponseEntity<Map<String, Object>> activateVersion(
            @Parameter(description = "版本ID", required = true) 
            @PathVariable @NotNull @Positive Long versionId,
            
            @Parameter(description = "操作人员") 
            @RequestParam(defaultValue = "system") String operator) {

        ConfigVersion activatedVersion = versionService.activateVersion(versionId, operator);

        return ResponseEntity.ok(Map.of(
                "message", "版本激活成功",
                "version", buildVersionResponse(activatedVersion),
                "operator", operator
        ));
    }

    @Operation(summary = "版本回滚", description = "将配置回滚到指定版本")
    @PostMapping("/rollback")
    public ResponseEntity<Map<String, Object>> rollbackVersion(
            @Parameter(description = "应用名称", required = true) 
            @RequestParam @NotBlank String application,
            
            @Parameter(description = "环境配置", required = true) 
            @RequestParam @NotBlank String profile,
            
            @Parameter(description = "目标版本号", required = true) 
            @RequestParam @NotBlank String targetVersion,
            
            @Parameter(description = "操作人员") 
            @RequestParam(defaultValue = "system") String operator) {

        ConfigVersion rolledBackVersion = versionService.rollback(application, profile, targetVersion, operator);

        return ResponseEntity.ok(Map.of(
                "message", "版本回滚成功",
                "version", buildVersionResponse(rolledBackVersion),
                "operator", operator
        ));
    }

    @Operation(summary = "版本比较", description = "比较两个版本之间的差异")
    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareVersions(
            @Parameter(description = "源版本号", required = true) 
            @RequestParam @NotBlank String fromVersion,
            
            @Parameter(description = "目标版本号", required = true) 
            @RequestParam @NotBlank String toVersion) {

        String diffReport = versionService.compareVersions(fromVersion, toVersion);

        return ResponseEntity.ok(Map.of(
                "message", "版本比较成功",
                "fromVersion", fromVersion,
                "toVersion", toVersion,
                "diffReport", diffReport
        ));
    }

    @Operation(summary = "根据版本号查询", description = "根据版本号获取版本详情")
    @GetMapping("/{version}")
    public ResponseEntity<Map<String, Object>> getVersionByNumber(
            @Parameter(description = "版本号", required = true) 
            @PathVariable @NotBlank String version) {

        Optional<ConfigVersion> configVersion = versionService.getVersionByNumber(version);

        if (configVersion.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "message", "版本查询成功",
                    "version", buildVersionResponse(configVersion.get())
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "根据标签查询版本", description = "根据版本标签获取版本列表")
    @GetMapping("/by-tag/{tag}")
    public ResponseEntity<Map<String, Object>> getVersionsByTag(
            @Parameter(description = "版本标签", required = true) 
            @PathVariable @NotBlank String tag) {

        List<ConfigVersion> versions = versionService.getVersionsByTag(tag);

        return ResponseEntity.ok(Map.of(
                "message", "标签版本查询成功",
                "tag", tag,
                "versions", versions.stream().map(this::buildVersionResponse).toList(),
                "count", versions.size()
        ));
    }

    @Operation(summary = "清理历史版本", description = "清理指定应用环境的历史版本，保留指定数量的最新版本")
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupVersionHistory(
            @Parameter(description = "应用名称", required = true) 
            @RequestParam @NotBlank String application,
            
            @Parameter(description = "环境配置", required = true) 
            @RequestParam @NotBlank String profile,
            
            @Parameter(description = "保留版本数量", required = true) 
            @RequestParam @Positive int keepCount,
            
            @Parameter(description = "操作人员") 
            @RequestParam(defaultValue = "system") String operator) {

        int cleanedCount = versionService.cleanupVersionHistory(application, profile, keepCount);

        return ResponseEntity.ok(Map.of(
                "message", "版本清理成功",
                "application", application,
                "profile", profile,
                "cleanedCount", cleanedCount,
                "keepCount", keepCount,
                "operator", operator
        ));
    }

    @Operation(summary = "版本统计信息", description = "获取版本管理的统计信息")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getVersionStatistics(
            @Parameter(description = "应用名称") 
            @RequestParam(required = false) String application,
            
            @Parameter(description = "环境配置") 
            @RequestParam(required = false) String profile) {

        // 这里可以扩展更多统计功能
        Map<String, Object> statistics = Map.of(
                "message", "版本统计查询成功",
                "note", "版本统计功能正在完善中..."
        );

        return ResponseEntity.ok(statistics);
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
                "tag", version.getTag() != null ? version.getTag() : "",
                "operator", version.getOperator(),
                "createTime", version.getCreateTime(),
                "active", version.getActive(),
                "contentSize", version.getContentSize() != null ? version.getContentSize() : 0,
                "contentHash", version.getContentHash(),
                "parentVersionId", version.getParentVersionId()
        );
    }
} 