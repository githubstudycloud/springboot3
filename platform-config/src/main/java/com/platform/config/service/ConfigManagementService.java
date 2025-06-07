package com.platform.config.service;

import cn.hutool.core.date.DateUtil;
import com.platform.config.exception.ConfigValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 配置管理服务 - 重构版本
 * 作为配置管理的门面服务，协调各个专门的服务
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigManagementService {
    
    private final ConfigRefreshService refreshService;
    private final ConfigSyncService syncService;
    private final ConfigBackupService backupService;
    
    private volatile String currentConfigSource = "gitlab";
    
    /**
     * 刷新配置 - 委托给RefreshService
     */
    public Set<String> refreshConfig(String application) {
        return refreshConfig(application, "system");
    }
    
    /**
     * 刷新配置 - 带操作人员信息
     */
    public Set<String> refreshConfig(String application, String operator) {
        validateApplication(application);
        return refreshService.refreshConfig(application, operator);
    }
    
    /**
     * 切换配置源
     */
    public void switchConfigSource(String source, String operator) {
        validateConfigSource(source);
        
        if ("gitlab".equals(source) && !syncService.isGitlabAvailable()) {
            throw new ConfigValidationException("配置源切换失败", 
                Arrays.asList("GitLab不可用，无法切换到GitLab配置源"));
        }
        
        String oldSource = currentConfigSource;
        currentConfigSource = source;
        log.info("配置源已切换: {} -> {}, 操作人: {}", oldSource, source, operator);
        
        // 切换后自动刷新配置
        refreshService.refreshAllConfigs(operator);
    }
    
    /**
     * 获取配置状态
     */
    public Map<String, Object> getConfigStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("currentSource", currentConfigSource);
        status.put("gitlabAvailable", syncService.isGitlabAvailable());
        status.put("timestamp", DateUtil.now());
        status.put("version", "2.0.0");
        
        return status;
    }
    
    /**
     * 同步配置到GitLab - 委托给SyncService
     */
    public String syncConfigToGitlab(String operator) {
        return syncService.syncConfigToGitlab(operator);
    }
    
    /**
     * 从GitLab拉取配置 - 委托给SyncService
     */
    public String pullConfigFromGitlab(String operator) {
        return syncService.pullConfigFromGitlab(operator);
    }
    
    /**
     * 备份配置 - 委托给BackupService
     */
    public String backupConfig(String operator) {
        return backupService.backupConfig(operator);
    }
    
    /**
     * 恢复配置 - 委托给BackupService
     */
    public void restoreConfig(String backupPath, String operator) {
        backupService.restoreConfig(backupPath, operator);
    }
    
    /**
     * 获取备份列表 - 委托给BackupService
     */
    public List<String> listBackups() {
        return backupService.listBackups();
    }
    
    /**
     * 验证应用名称
     */
    private void validateApplication(String application) {
        if (application == null || application.trim().isEmpty()) {
            throw new ConfigValidationException("应用名称验证失败", 
                Arrays.asList("应用名称不能为空"));
        }
        
        // 可以添加更多验证规则
        if (!application.matches("^[a-zA-Z0-9-_]+$")) {
            throw new ConfigValidationException("应用名称验证失败", 
                Arrays.asList("应用名称只能包含字母、数字、连字符和下划线"));
        }
    }
    
    /**
     * 验证配置源
     */
    private void validateConfigSource(String source) {
        if (!"gitlab".equals(source) && !"native".equals(source)) {
            throw new ConfigValidationException("配置源验证失败", 
                Arrays.asList("不支持的配置源: " + source + "，支持的配置源: gitlab, native"));
        }
    }
    
    /**
     * 获取当前配置源
     */
    public String getCurrentConfigSource() {
        return currentConfigSource;
    }
} 