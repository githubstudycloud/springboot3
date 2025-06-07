package com.platform.config.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.config.event.ConfigChangeEvent;
import com.platform.config.exception.GitSyncException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Git配置同步服务
 * 专门负责配置与Git仓库的同步操作
 *
 * @author Platform Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigSyncService {

    private final ApplicationEventPublisher eventPublisher;

    @Value("${spring.cloud.config.server.git.uri:}")
    private String gitUri;

    @Value("${spring.cloud.config.server.native.search-locations:}")
    private String nativeSearchLocations;

    @Value("${platform.config.gitlab.enabled:true}")
    private boolean gitlabEnabled;

    /**
     * 检查GitLab是否可用
     *
     * @return true if GitLab is available
     */
    public boolean isGitlabAvailable() {
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
     * 同步配置到GitLab
     * 使用事务确保操作的原子性
     *
     * @param operator 操作人员
     * @return 同步结果信息
     */
    @Transactional(rollbackFor = Exception.class)
    public String syncConfigToGitlab(String operator) {
        if (!gitlabEnabled || StrUtil.isBlank(gitUri)) {
            throw new GitSyncException("SYNC", gitUri, "GitLab未启用或未配置Git URI");
        }

        // 检查本地配置目录
        Path localConfigPath = Paths.get(nativeSearchLocations);
        if (!Files.exists(localConfigPath)) {
            throw new GitSyncException("SYNC", gitUri, "本地配置目录不存在: " + localConfigPath);
        }

        // 创建临时Git目录
        Path tempGitPath = Paths.get(System.getProperty("java.io.tmpdir"), "config-sync-" + System.currentTimeMillis());
        
        try {
            log.info("开始同步配置到GitLab，操作人: {}", operator);
            
            // 克隆远程仓库
            try (Git git = Git.cloneRepository()
                    .setURI(gitUri)
                    .setDirectory(tempGitPath.toFile())
                    .call()) {

                // 复制本地配置到Git目录
                FileUtil.copyContent(localConfigPath.toFile(), tempGitPath.toFile(), true);

                // 检查是否有变更
                boolean hasChanges = git.status().call().hasUncommittedChanges();
                
                if (!hasChanges) {
                    log.info("没有配置变更需要同步");
                    return "没有配置变更需要同步";
                }

                // 添加所有变更
                git.add().addFilepattern(".").call();

                // 提交变更
                String commitMessage = String.format("Config sync from local at %s by %s", DateUtil.now(), operator);
                git.commit()
                    .setMessage(commitMessage)
                    .setAuthor(operator, operator + "@platform.com")
                    .call();

                // 推送到远程
                git.push().call();

                // 发布同步成功事件
                publishSyncEvent("SYNC_SUCCESS", operator, commitMessage);

                log.info("配置已成功同步到GitLab，提交信息: {}", commitMessage);
                return commitMessage;
            }

        } catch (GitAPIException e) {
            String errorMsg = "Git操作失败: " + e.getMessage();
            log.error(errorMsg, e);
            
            publishSyncEvent("SYNC_FAILED", operator, errorMsg);
            throw new GitSyncException("SYNC", gitUri, errorMsg, e);
            
        } catch (IOException e) {
            String errorMsg = "文件操作失败: " + e.getMessage();
            log.error(errorMsg, e);
            
            publishSyncEvent("SYNC_FAILED", operator, errorMsg);
            throw new GitSyncException("SYNC", gitUri, errorMsg, e);
            
        } finally {
            // 清理临时目录
            try {
                if (Files.exists(tempGitPath)) {
                    FileUtil.del(tempGitPath.toFile());
                }
            } catch (Exception e) {
                log.warn("清理临时目录失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 从GitLab拉取最新配置
     *
     * @param operator 操作人员
     * @return 拉取结果信息
     */
    @Transactional(rollbackFor = Exception.class)
    public String pullConfigFromGitlab(String operator) {
        if (!gitlabEnabled || StrUtil.isBlank(gitUri)) {
            throw new GitSyncException("PULL", gitUri, "GitLab未启用或未配置Git URI");
        }

        Path tempGitPath = Paths.get(System.getProperty("java.io.tmpdir"), "config-pull-" + System.currentTimeMillis());
        
        try {
            log.info("开始从GitLab拉取配置，操作人: {}", operator);
            
            // 克隆最新的远程仓库
            try (Git git = Git.cloneRepository()
                    .setURI(gitUri)
                    .setDirectory(tempGitPath.toFile())
                    .call()) {

                // 确保本地配置目录存在
                Path localConfigPath = Paths.get(nativeSearchLocations);
                Files.createDirectories(localConfigPath);

                // 备份现有本地配置
                String backupDir = backupLocalConfig();

                // 复制Git配置到本地目录
                FileUtil.copyContent(tempGitPath.toFile(), localConfigPath.toFile(), true);

                // 发布拉取成功事件
                publishSyncEvent("PULL_SUCCESS", operator, "从GitLab拉取配置成功");

                log.info("配置已成功从GitLab拉取到本地，备份目录: {}", backupDir);
                return "配置已成功从GitLab拉取到本地，备份目录: " + backupDir;
            }

        } catch (GitAPIException e) {
            String errorMsg = "Git拉取操作失败: " + e.getMessage();
            log.error(errorMsg, e);
            
            publishSyncEvent("PULL_FAILED", operator, errorMsg);
            throw new GitSyncException("PULL", gitUri, errorMsg, e);
            
        } catch (IOException e) {
            String errorMsg = "文件操作失败: " + e.getMessage();
            log.error(errorMsg, e);
            
            publishSyncEvent("PULL_FAILED", operator, errorMsg);
            throw new GitSyncException("PULL", gitUri, errorMsg, e);
            
        } finally {
            // 清理临时目录
            try {
                if (Files.exists(tempGitPath)) {
                    FileUtil.del(tempGitPath.toFile());
                }
            } catch (Exception e) {
                log.warn("清理临时目录失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 备份本地配置
     */
    private String backupLocalConfig() throws IOException {
        String timestamp = DateUtil.format(DateUtil.date(), "yyyyMMdd_HHmmss");
        String backupDir = "/app/config-backup/pull_backup_" + timestamp;
        
        Path localConfigPath = Paths.get(nativeSearchLocations);
        if (Files.exists(localConfigPath)) {
            Path backupPath = Paths.get(backupDir);
            Files.createDirectories(backupPath);
            FileUtil.copyContent(localConfigPath.toFile(), backupPath.toFile(), true);
        }
        
        return backupDir;
    }

    /**
     * 发布同步事件
     */
    private void publishSyncEvent(String operation, String operator, String message) {
        try {
            ConfigChangeEvent event = new ConfigChangeEvent(
                this,
                "system",
                "all",
                operation,
                "",
                message,
                operator
            );
            
            eventPublisher.publishEvent(event);
            log.debug("同步事件已发布: {}", event);
            
        } catch (Exception e) {
            log.warn("发布同步事件失败: {}", e.getMessage());
        }
    }
} 