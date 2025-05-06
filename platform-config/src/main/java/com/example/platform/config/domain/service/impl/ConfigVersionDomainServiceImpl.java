package com.example.platform.config.domain.service.impl;

import com.example.platform.config.domain.model.ConfigVersionHistory;
import com.example.platform.config.domain.repository.ConfigVersionHistoryRepository;
import com.example.platform.config.domain.service.ConfigVersionDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 配置版本管理领域服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigVersionDomainServiceImpl implements ConfigVersionDomainService {

    private final ConfigVersionHistoryRepository versionHistoryRepository;

    @Override
    @Transactional
    public ConfigVersionHistory createVersionHistory(
            Long configItemId,
            String dataId,
            String group,
            String environment,
            String content,
            boolean encrypted,
            String changeReason,
            String operator) {
        
        log.info("Creating version history for config: {}, group: {}, env: {}", 
                dataId, group, environment);
        
        // 获取最新版本号
        Integer latestVersion = versionHistoryRepository.getLatestVersionNumber(configItemId);
        Integer newVersion = latestVersion == null ? 1 : latestVersion + 1;
        
        // 创建版本历史对象
        ConfigVersionHistory versionHistory = ConfigVersionHistory.builder()
                .configItemId(configItemId)
                .dataId(dataId)
                .group(group)
                .version(newVersion)
                .content(content)
                .environment(environment)
                .encrypted(encrypted)
                .createdTime(LocalDateTime.now())
                .createdBy(operator)
                .changeReason(changeReason)
                .build();
        
        // 保存版本历史
        return versionHistoryRepository.save(versionHistory);
    }

    @Override
    public List<ConfigVersionHistory> getVersionHistories(Long configItemId) {
        log.info("Getting version histories for config item: {}", configItemId);
        return versionHistoryRepository.findByConfigItemId(configItemId);
    }

    @Override
    public List<ConfigVersionHistory> getVersionHistories(String dataId, String group, String environment) {
        log.info("Getting version histories for config: {}, group: {}, env: {}", 
                dataId, group, environment);
        return versionHistoryRepository.findByDataIdAndGroupAndEnvironment(dataId, group, environment);
    }

    @Override
    public Optional<ConfigVersionHistory> getVersionHistory(Long configItemId, Integer version) {
        log.info("Getting version history for config item: {}, version: {}", configItemId, version);
        return versionHistoryRepository.findByConfigItemIdAndVersion(configItemId, version);
    }

    @Override
    @Transactional
    public void cleanupOldVersions(Long configItemId, Integer keepVersionCount) {
        log.info("Cleaning up old versions for config item: {}, keeping: {}", configItemId, keepVersionCount);
        versionHistoryRepository.deleteOldVersions(configItemId, keepVersionCount);
    }

    @Override
    public Integer getLatestVersionNumber(Long configItemId) {
        log.info("Getting latest version number for config item: {}", configItemId);
        return versionHistoryRepository.getLatestVersionNumber(configItemId);
    }
}
