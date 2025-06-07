package com.platform.config.controller;

import com.platform.config.service.ConfigManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 配置管理控制器
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/config/management")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "配置管理", description = "配置中心管理API")
public class ConfigController {
    
    private final ConfigManagementService configManagementService;
    
    @Operation(summary = "刷新配置", description = "刷新指定应用的配置")
    @PostMapping("/refresh/{application}")
    public ResponseEntity<String> refreshConfig(@PathVariable String application) {
        try {
            configManagementService.refreshConfig(application);
            return ResponseEntity.ok("配置刷新成功");
        } catch (Exception e) {
            log.error("配置刷新失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("配置刷新失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "切换配置源", description = "在GitLab和本地配置源之间切换")
    @PostMapping("/switch-source")
    public ResponseEntity<String> switchConfigSource(@RequestParam String source) {
        try {
            configManagementService.switchConfigSource(source);
            return ResponseEntity.ok("配置源切换成功: " + source);
        } catch (Exception e) {
            log.error("配置源切换失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("配置源切换失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取配置状态", description = "获取配置中心当前状态")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getConfigStatus() {
        try {
            Map<String, Object> status = configManagementService.getConfigStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("获取配置状态失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "同步配置到GitLab", description = "将本地配置同步到GitLab")
    @PostMapping("/sync-to-gitlab")
    public ResponseEntity<String> syncToGitlab() {
        try {
            configManagementService.syncConfigToGitlab();
            return ResponseEntity.ok("配置同步到GitLab成功");
        } catch (Exception e) {
            log.error("同步配置到GitLab失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("同步失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "备份配置", description = "备份当前所有配置")
    @PostMapping("/backup")
    public ResponseEntity<String> backupConfig() {
        try {
            String backupPath = configManagementService.backupConfig();
            return ResponseEntity.ok("配置备份成功: " + backupPath);
        } catch (Exception e) {
            log.error("配置备份失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("备份失败: " + e.getMessage());
        }
    }
} 