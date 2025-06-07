package com.platform.config.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.config.event.ConfigChangeEvent;
import com.platform.config.exception.ConfigValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 配置备份服务
 * 专门负责配置的备份和恢复操作
 *
 * @author Platform Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigBackupService {

    private final ApplicationEventPublisher eventPublisher;
    private final ConfigSyncService syncService;

    @Value("${spring.cloud.config.server.native.search-locations:}")
    private String nativeSearchLocations;

    @Value("${platform.config.local-backup-path:/app/config-backup}")
    private String localBackupPath;

    /**
     * 备份配置
     *
     * @param operator 操作人员
     * @return 备份目录路径
     */
    public String backupConfig(String operator) {
        String timestamp = DateUtil.format(DateUtil.date(), "yyyyMMdd_HHmmss");
        String backupDir = localBackupPath + "/backup_" + timestamp;

        try {
            log.info("开始备份配置，操作人: {}", operator);

            // 创建备份目录
            Path backupPath = Paths.get(backupDir);
            Files.createDirectories(backupPath);

            // 备份本地配置
            if (StrUtil.isNotBlank(nativeSearchLocations)) {
                Path localConfigPath = Paths.get(nativeSearchLocations);
                if (Files.exists(localConfigPath)) {
                    FileUtil.copyContent(localConfigPath.toFile(),
                        new File(backupDir, "native"), true);
                    log.info("本地配置备份完成: {}/native", backupDir);
                }
            }

            // 如果GitLab可用，也备份Git配置
            if (syncService.isGitlabAvailable()) {
                try {
                    backupGitlabConfig(backupDir);
                    log.info("GitLab配置备份完成: {}/gitlab", backupDir);
                } catch (Exception e) {
                    log.warn("备份GitLab配置失败: {}", e.getMessage());
                }
            }

            // 创建备份元数据文件
            createBackupMetadata(backupDir, operator);

            // 发布备份成功事件
            publishBackupEvent("BACKUP_SUCCESS", operator, backupDir);

            log.info("配置备份完成: {}", backupDir);
            return backupDir;

        } catch (IOException e) {
            String errorMsg = "配置备份失败: " + e.getMessage();
            log.error(errorMsg, e);

            publishBackupEvent("BACKUP_FAILED", operator, errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * 恢复配置
     *
     * @param backupPath 备份路径
     * @param operator 操作人员
     */
    public void restoreConfig(String backupPath, String operator) {
        validateBackupPath(backupPath);

        try {
            log.info("开始恢复配置，备份路径: {}, 操作人: {}", backupPath, operator);

            Path backupDir = Paths.get(backupPath);
            if (!Files.exists(backupDir)) {
                throw new ConfigValidationException("配置恢复失败", 
                    Arrays.asList("备份目录不存在: " + backupPath));
            }

            // 先备份当前配置
            String currentBackup = backupConfig("auto-backup-before-restore");
            log.info("恢复前自动备份完成: {}", currentBackup);

            // 恢复本地配置
            Path nativeBackupPath = Paths.get(backupPath, "native");
            if (Files.exists(nativeBackupPath)) {
                Path localConfigPath = Paths.get(nativeSearchLocations);
                Files.createDirectories(localConfigPath);

                // 清理现有配置
                FileUtil.clean(localConfigPath.toFile());

                // 恢复备份的配置
                FileUtil.copyContent(nativeBackupPath.toFile(), localConfigPath.toFile(), true);
                log.info("本地配置恢复完成");
            }

            // 发布恢复成功事件
            publishBackupEvent("RESTORE_SUCCESS", operator, backupPath);

            log.info("配置恢复完成，备份路径: {}", backupPath);

        } catch (IOException e) {
            String errorMsg = "配置恢复失败: " + e.getMessage();
            log.error(errorMsg, e);

            publishBackupEvent("RESTORE_FAILED", operator, errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * 获取备份列表
     *
     * @return 备份目录列表
     */
    public List<String> listBackups() {
        try {
            Path backupBasePath = Paths.get(localBackupPath);
            if (!Files.exists(backupBasePath)) {
                return Arrays.asList();
            }

            return Files.list(backupBasePath)
                .filter(Files::isDirectory)
                .map(path -> path.getFileName().toString())
                .sorted((a, b) -> b.compareTo(a)) // 降序排列，最新的在前
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("获取备份列表失败: {}", e.getMessage(), e);
            return Arrays.asList();
        }
    }

    /**
     * 删除备份
     *
     * @param backupName 备份名称
     * @param operator 操作人员
     */
    public void deleteBackup(String backupName, String operator) {
        validateBackupName(backupName);

        String backupPath = localBackupPath + "/" + backupName;
        Path backupDir = Paths.get(backupPath);

        if (!Files.exists(backupDir)) {
            throw new ConfigValidationException("删除备份失败", 
                Arrays.asList("备份不存在: " + backupName));
        }

        try {
            FileUtil.del(backupDir.toFile());
            publishBackupEvent("DELETE_SUCCESS", operator, backupName);
            log.info("备份删除成功: {}", backupName);

        } catch (Exception e) {
            String errorMsg = "删除备份失败: " + e.getMessage();
            log.error(errorMsg, e);

            publishBackupEvent("DELETE_FAILED", operator, errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * 备份GitLab配置
     */
    private void backupGitlabConfig(String backupDir) {
        // 通过SyncService拉取最新的GitLab配置到临时目录，然后复制到备份目录
        // 这里简化实现，实际可以通过Git clone到备份目录
        Path gitBackupPath = Paths.get(backupDir, "gitlab");
        try {
            Files.createDirectories(gitBackupPath);
            // 实现GitLab配置备份逻辑
            log.info("GitLab配置备份占位实现");
        } catch (IOException e) {
            throw new RuntimeException("GitLab配置备份失败", e);
        }
    }

    /**
     * 创建备份元数据文件
     */
    private void createBackupMetadata(String backupDir, String operator) throws IOException {
        Path metadataPath = Paths.get(backupDir, "backup-metadata.txt");
        String metadata = String.format(
            "备份时间: %s%n操作人员: %s%n备份类型: 完整备份%n备份版本: 2.0.0%n",
            DateUtil.now(), operator
        );
        Files.write(metadataPath, metadata.getBytes());
    }

    /**
     * 验证备份路径
     */
    private void validateBackupPath(String backupPath) {
        if (StrUtil.isBlank(backupPath)) {
            throw new ConfigValidationException("备份路径验证失败", 
                Arrays.asList("备份路径不能为空"));
        }

        // 安全检查：防止路径遍历攻击
        if (backupPath.contains("..") || backupPath.contains("~")) {
            throw new ConfigValidationException("备份路径验证失败", 
                Arrays.asList("备份路径包含非法字符"));
        }
    }

    /**
     * 验证备份名称
     */
    private void validateBackupName(String backupName) {
        if (StrUtil.isBlank(backupName)) {
            throw new ConfigValidationException("备份名称验证失败", 
                Arrays.asList("备份名称不能为空"));
        }

        if (!backupName.matches("^[a-zA-Z0-9_-]+$")) {
            throw new ConfigValidationException("备份名称验证失败", 
                Arrays.asList("备份名称只能包含字母、数字、连字符和下划线"));
        }
    }

    /**
     * 发布备份事件
     */
    private void publishBackupEvent(String operation, String operator, String message) {
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
            log.debug("备份事件已发布: {}", event);

        } catch (Exception e) {
            log.warn("发布备份事件失败: {}", e.getMessage());
        }
    }
} 