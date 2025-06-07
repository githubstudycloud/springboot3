package com.platform.config.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置管理服务
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigManagementService {
    
    private final ContextRefresher contextRefresher;
    
    @Value("${spring.cloud.config.server.git.uri:}")
    private String gitUri;
    
    @Value("${spring.cloud.config.server.native.search-locations:}")
    private String nativeSearchLocations;
    
    @Value("${platform.config.local-backup-path:/app/config-backup}")
    private String localBackupPath;
    
    @Value("${platform.config.gitlab.enabled:true}")
    private boolean gitlabEnabled;
    
    private volatile String currentConfigSource = "gitlab";
    
    /**
     * 刷新配置
     */
    public void refreshConfig(String application) {
        log.info("开始刷新配置，应用: {}", application);
        contextRefresher.refresh();
        log.info("配置刷新完成，应用: {}", application);
    }
    
    /**
     * 切换配置源
     */
    public void switchConfigSource(String source) {
        if (!"gitlab".equals(source) && !"native".equals(source)) {
            throw new IllegalArgumentException("不支持的配置源: " + source);
        }
        
        if ("gitlab".equals(source) && !isGitlabAvailable()) {
            throw new RuntimeException("GitLab不可用，无法切换到GitLab配置源");
        }
        
        currentConfigSource = source;
        log.info("配置源已切换到: {}", source);
        
        // 刷新配置
        contextRefresher.refresh();
    }
    
    /**
     * 获取配置状态
     */
    public Map<String, Object> getConfigStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("currentSource", currentConfigSource);
        status.put("gitlabEnabled", gitlabEnabled);
        status.put("gitlabAvailable", isGitlabAvailable());
        status.put("gitUri", gitUri);
        status.put("nativeSearchLocations", nativeSearchLocations);
        status.put("timestamp", DateUtil.now());
        
        return status;
    }
    
    /**
     * 同步配置到GitLab
     */
    public void syncConfigToGitlab() throws GitAPIException, IOException {
        if (!gitlabEnabled || StrUtil.isBlank(gitUri)) {
            throw new RuntimeException("GitLab未启用或未配置Git URI");
        }
        
        // 检查本地配置目录
        Path localConfigPath = Paths.get(nativeSearchLocations);
        if (!Files.exists(localConfigPath)) {
            throw new RuntimeException("本地配置目录不存在: " + localConfigPath);
        }
        
        // 克隆或更新Git仓库
        Path tempGitPath = Paths.get(System.getProperty("java.io.tmpdir"), "config-sync");
        if (Files.exists(tempGitPath)) {
            FileUtil.del(tempGitPath.toFile());
        }
        
        try (Git git = Git.cloneRepository()
                .setURI(gitUri)
                .setDirectory(tempGitPath.toFile())
                .call()) {
            
            // 复制本地配置到Git目录
            FileUtil.copyContent(localConfigPath.toFile(), tempGitPath.toFile(), true);
            
            // 提交更改
            git.add().addFilepattern(".").call();
            git.commit()
                .setMessage("Config sync from local at " + DateUtil.now())
                .call();
            
            // 推送到远程
            git.push().call();
            
            log.info("配置已成功同步到GitLab");
        } finally {
            // 清理临时目录
            FileUtil.del(tempGitPath.toFile());
        }
    }
    
    /**
     * 备份配置
     */
    public String backupConfig() throws IOException {
        String timestamp = DateUtil.format(DateUtil.date(), "yyyyMMdd_HHmmss");
        String backupDir = localBackupPath + "/backup_" + timestamp;
        
        // 创建备份目录
        Path backupPath = Paths.get(backupDir);
        Files.createDirectories(backupPath);
        
        // 备份本地配置
        if (StrUtil.isNotBlank(nativeSearchLocations)) {
            Path localConfigPath = Paths.get(nativeSearchLocations);
            if (Files.exists(localConfigPath)) {
                FileUtil.copyContent(localConfigPath.toFile(), 
                    new File(backupDir, "native"), true);
            }
        }
        
        // 如果GitLab可用，也备份Git配置
        if (isGitlabAvailable()) {
            try {
                backupGitlabConfig(backupDir);
            } catch (Exception e) {
                log.warn("备份GitLab配置失败: {}", e.getMessage());
            }
        }
        
        log.info("配置备份完成: {}", backupDir);
        return backupDir;
    }
    
    /**
     * 检查GitLab是否可用
     */
    private boolean isGitlabAvailable() {
        if (!gitlabEnabled || StrUtil.isBlank(gitUri)) {
            return false;
        }
        
        try {
            // 尝试连接GitLab
            Git.lsRemoteRepository()
                .setRemote(gitUri)
                .call();
            return true;
        } catch (Exception e) {
            log.debug("GitLab不可用: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 备份GitLab配置
     */
    private void backupGitlabConfig(String backupDir) throws GitAPIException {
        Path gitBackupPath = Paths.get(backupDir, "gitlab");
        
        try (Git git = Git.cloneRepository()
                .setURI(gitUri)
                .setDirectory(gitBackupPath.toFile())
                .call()) {
            log.info("GitLab配置备份完成: {}", gitBackupPath);
        }
    }
} 