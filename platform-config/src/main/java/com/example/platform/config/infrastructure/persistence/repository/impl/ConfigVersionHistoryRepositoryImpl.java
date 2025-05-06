package com.example.platform.config.infrastructure.persistence.repository.impl;

import com.example.platform.config.domain.model.ConfigVersionHistory;
import com.example.platform.config.domain.repository.ConfigVersionHistoryRepository;
import com.example.platform.config.infrastructure.mapper.ConfigVersionHistoryMapper;
import com.example.platform.config.infrastructure.persistence.entity.ConfigVersionHistoryEntity;
import com.example.platform.config.infrastructure.persistence.repository.ConfigVersionHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 配置版本历史仓储实现
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ConfigVersionHistoryRepositoryImpl implements ConfigVersionHistoryRepository {

    private final ConfigVersionHistoryJpaRepository versionHistoryJpaRepository;
    private final ConfigVersionHistoryMapper versionHistoryMapper;

    @Override
    public ConfigVersionHistory save(ConfigVersionHistory versionHistory) {
        log.debug("Saving version history for config item: {}, version: {}", 
                versionHistory.getConfigItemId(), versionHistory.getVersion());
        
        ConfigVersionHistoryEntity entity = versionHistoryMapper.toEntity(versionHistory);
        ConfigVersionHistoryEntity savedEntity = versionHistoryJpaRepository.save(entity);
        return versionHistoryMapper.toDomain(savedEntity);
    }

    @Override
    public List<ConfigVersionHistory> findByConfigItemId(Long configItemId) {
        log.debug("Finding version histories by config item id: {}", configItemId);
        
        List<ConfigVersionHistoryEntity> entities = 
                versionHistoryJpaRepository.findByConfigItemIdOrderByVersionDesc(configItemId);
        return versionHistoryMapper.toDomainList(entities);
    }

    @Override
    public List<ConfigVersionHistory> findByDataIdAndGroupAndEnvironment(String dataId, String group, String environment) {
        log.debug("Finding version histories by dataId: {}, group: {}, env: {}", 
                dataId, group, environment);
        
        List<ConfigVersionHistoryEntity> entities = 
                versionHistoryJpaRepository.findByDataIdAndGroupAndEnvironmentOrderByVersionDesc(
                        dataId, group, environment);
        return versionHistoryMapper.toDomainList(entities);
    }

    @Override
    public Optional<ConfigVersionHistory> findByConfigItemIdAndVersion(Long configItemId, Integer version) {
        log.debug("Finding version history by config item id: {} and version: {}", 
                configItemId, version);
        
        return versionHistoryJpaRepository.findByConfigItemIdAndVersion(configItemId, version)
                .map(versionHistoryMapper::toDomain);
    }

    @Override
    public Integer getLatestVersionNumber(Long configItemId) {
        log.debug("Getting latest version number for config item id: {}", configItemId);
        
        return versionHistoryJpaRepository.getLatestVersionNumber(configItemId);
    }

    @Override
    @Transactional
    public void deleteOldVersions(Long configItemId, Integer keepVersionsCount) {
        log.debug("Deleting old versions for config item id: {}, keeping: {}", 
                configItemId, keepVersionsCount);
        
        versionHistoryJpaRepository.deleteOldVersions(configItemId, keepVersionsCount);
    }
}
