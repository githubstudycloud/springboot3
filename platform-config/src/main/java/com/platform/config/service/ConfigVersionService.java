package com.platform.config.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.platform.config.entity.ConfigVersion;
import com.platform.config.exception.ConfigNotFoundException;
import com.platform.config.exception.ConfigValidationException;
import com.platform.config.repository.ConfigVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 配置版本管理服务
 * 专门负责配置的版本控制功能
 *
 * @author Platform Team
 * @since 2.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigVersionService {

    private final ConfigVersionRepository versionRepository;

    /**
     * 保存配置版本
     *
     * @param application 应用名称
     * @param profile 环境配置
     * @param content 配置内容
     * @param operator 操作人员
     * @return 保存的版本信息
     */
    @Transactional(rollbackFor = Exception.class)
    public ConfigVersion saveVersion(String application, String profile, 
                                   String content, String operator) {
        return saveVersion(application, profile, content, operator, null, null);
    }

    /**
     * 保存配置版本（带描述和标签）
     *
     * @param application 应用名称
     * @param profile 环境配置
     * @param content 配置内容
     * @param operator 操作人员
     * @param description 版本描述
     * @param tag 版本标签
     * @return 保存的版本信息
     */
    @Transactional(rollbackFor = Exception.class)
    public ConfigVersion saveVersion(String application, String profile, 
                                   String content, String operator,
                                   String description, String tag) {
        validateInput(application, profile, content, operator);

        // 计算内容哈希值
        String contentHash = DigestUtil.md5Hex(content);
        
        // 检查是否已存在相同内容的版本
        List<ConfigVersion> existingVersions = versionRepository.findByContentHashOrderByCreateTimeDesc(contentHash);
        if (!existingVersions.isEmpty()) {
            log.info("配置内容未发生变化，应用: {}, 环境: {}", application, profile);
            return existingVersions.get(0);
        }

        // 生成版本号
        String version = generateVersion(application, profile);

        // 获取当前激活版本作为父版本
        Long parentVersionId = getCurrentActiveVersion(application, profile)
                .map(ConfigVersion::getId)
                .orElse(null);

        // 创建新版本
        ConfigVersion configVersion = ConfigVersion.builder()
                .application(application)
                .profile(profile)
                .version(version)
                .content(content)
                .contentHash(contentHash)
                .description(description)
                .operator(operator)
                .tag(tag)
                .parentVersionId(parentVersionId)
                .active(false)
                .build();

        configVersion = versionRepository.save(configVersion);
        log.info("配置版本已保存，应用: {}, 环境: {}, 版本: {}, 操作人: {}", 
                application, profile, version, operator);

        return configVersion;
    }

    /**
     * 激活指定版本
     *
     * @param versionId 版本ID
     * @param operator 操作人员
     * @return 激活的版本信息
     */
    @Transactional(rollbackFor = Exception.class)
    public ConfigVersion activateVersion(Long versionId, String operator) {
        ConfigVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new ConfigNotFoundException("版本不存在: " + versionId));

        // 先将同应用同环境的所有版本设置为非激活状态
        versionRepository.deactivateAllVersions(version.getApplication(), version.getProfile());

        // 激活指定版本
        version.setActive(true);
        version = versionRepository.save(version);

        log.info("配置版本已激活，应用: {}, 环境: {}, 版本: {}, 操作人: {}", 
                version.getApplication(), version.getProfile(), version.getVersion(), operator);

        return version;
    }

    /**
     * 回滚到指定版本
     *
     * @param application 应用名称
     * @param profile 环境配置
     * @param targetVersion 目标版本号
     * @param operator 操作人员
     * @return 回滚后的版本信息
     */
    @Transactional(rollbackFor = Exception.class)
    public ConfigVersion rollback(String application, String profile, 
                                String targetVersion, String operator) {
        // 查找目标版本
        ConfigVersion targetConfigVersion = versionRepository.findByVersion(targetVersion)
                .orElseThrow(() -> new ConfigNotFoundException("目标版本不存在: " + targetVersion));

        // 验证版本匹配
        if (!application.equals(targetConfigVersion.getApplication()) || 
            !profile.equals(targetConfigVersion.getProfile())) {
            throw new ConfigValidationException("版本信息不匹配，应用: " + application + 
                    ", 环境: " + profile + ", 版本: " + targetVersion);
        }

        // 激活目标版本
        return activateVersion(targetConfigVersion.getId(), operator);
    }

    /**
     * 获取当前激活的版本
     *
     * @param application 应用名称
     * @param profile 环境配置
     * @return 当前激活的版本
     */
    public Optional<ConfigVersion> getCurrentActiveVersion(String application, String profile) {
        return versionRepository.findByApplicationAndProfileAndActiveTrue(application, profile);
    }

    /**
     * 获取版本历史列表
     *
     * @param application 应用名称
     * @param profile 环境配置
     * @param pageable 分页参数
     * @return 版本历史分页数据
     */
    public Page<ConfigVersion> getVersionHistory(String application, String profile, Pageable pageable) {
        return versionRepository.findByApplicationAndProfileOrderByCreateTimeDesc(application, profile, pageable);
    }

    /**
     * 根据版本号获取版本信息
     *
     * @param version 版本号
     * @return 版本信息
     */
    public Optional<ConfigVersion> getVersionByNumber(String version) {
        return versionRepository.findByVersion(version);
    }

    /**
     * 比较两个版本的差异
     *
     * @param fromVersion 源版本号
     * @param toVersion 目标版本号
     * @return 差异信息
     */
    public String compareVersions(String fromVersion, String toVersion) {
        ConfigVersion from = versionRepository.findByVersion(fromVersion)
                .orElseThrow(() -> new ConfigNotFoundException("源版本不存在: " + fromVersion));
        
        ConfigVersion to = versionRepository.findByVersion(toVersion)
                .orElseThrow(() -> new ConfigNotFoundException("目标版本不存在: " + toVersion));

        // 简单的字符串差异比较，实际项目中可以使用更高级的diff算法
        return generateDiffReport(from, to);
    }

    /**
     * 根据标签获取版本
     *
     * @param tag 版本标签
     * @return 版本列表
     */
    public List<ConfigVersion> getVersionsByTag(String tag) {
        return versionRepository.findByTagOrderByCreateTimeDesc(tag);
    }

    /**
     * 清理历史版本（保留指定数量的版本）
     *
     * @param application 应用名称
     * @param profile 环境配置
     * @param keepCount 保留的版本数量
     * @return 清理的版本数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int cleanupVersionHistory(String application, String profile, int keepCount) {
        List<ConfigVersion> versions = versionRepository
                .findByApplicationAndProfileOrderByCreateTimeDesc(application, profile);
        
        if (versions.size() <= keepCount) {
            return 0;
        }

        // 保留最近的版本和当前激活版本
        List<ConfigVersion> toDelete = versions.stream()
                .skip(keepCount)
                .filter(v -> !v.getActive())
                .toList();

        versionRepository.deleteAll(toDelete);
        
        log.info("已清理历史版本，应用: {}, 环境: {}, 清理数量: {}", application, profile, toDelete.size());
        return toDelete.size();
    }

    /**
     * 验证输入参数
     */
    private void validateInput(String application, String profile, String content, String operator) {
        if (!StringUtils.hasText(application)) {
            throw new ConfigValidationException("应用名称不能为空");
        }
        if (!StringUtils.hasText(profile)) {
            throw new ConfigValidationException("环境配置不能为空");
        }
        if (!StringUtils.hasText(content)) {
            throw new ConfigValidationException("配置内容不能为空");
        }
        if (!StringUtils.hasText(operator)) {
            throw new ConfigValidationException("操作人员不能为空");
        }
    }

    /**
     * 生成版本号
     */
    private String generateVersion(String application, String profile) {
        String timestamp = DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss");
        long count = versionRepository.countByApplicationAndProfile(application, profile);
        return String.format("v%s_%d", timestamp, count + 1);
    }

    /**
     * 生成差异报告
     */
    private String generateDiffReport(ConfigVersion from, ConfigVersion to) {
        StringBuilder diff = new StringBuilder();
        diff.append("版本比较报告\n");
        diff.append("源版本: ").append(from.getVersion()).append(" (").append(from.getCreateTime()).append(")\n");
        diff.append("目标版本: ").append(to.getVersion()).append(" (").append(to.getCreateTime()).append(")\n");
        diff.append("操作人: ").append(from.getOperator()).append(" -> ").append(to.getOperator()).append("\n");
        diff.append("\n内容差异:\n");
        
        // 简单的内容比较，实际可以使用更复杂的diff算法
        if (from.getContent().equals(to.getContent())) {
            diff.append("配置内容无变化");
        } else {
            diff.append("配置内容已变更");
            diff.append("\n源内容长度: ").append(from.getContent().length());
            diff.append("\n目标内容长度: ").append(to.getContent().length());
        }
        
        return diff.toString();
    }
} 