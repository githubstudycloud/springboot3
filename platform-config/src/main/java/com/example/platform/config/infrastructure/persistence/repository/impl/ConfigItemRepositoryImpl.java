package com.example.platform.config.infrastructure.persistence.repository.impl;

import com.example.platform.config.domain.model.ConfigItem;
import com.example.platform.config.domain.repository.ConfigItemRepository;
import com.example.platform.config.infrastructure.mapper.ConfigItemMapper;
import com.example.platform.config.infrastructure.persistence.entity.ConfigItemEntity;
import com.example.platform.config.infrastructure.persistence.repository.ConfigItemJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 配置项仓储实现
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ConfigItemRepositoryImpl implements ConfigItemRepository {

    private final ConfigItemJpaRepository configItemJpaRepository;
    private final ConfigItemMapper configItemMapper;

    @Override
    public ConfigItem save(ConfigItem configItem) {
        log.debug("Saving config item: {}, {}, {}", 
                configItem.getDataId(), configItem.getGroup(), configItem.getEnvironment());
        
        ConfigItemEntity entity = configItemMapper.toEntity(configItem);
        ConfigItemEntity savedEntity = configItemJpaRepository.save(entity);
        return configItemMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ConfigItem> findByDataIdAndGroupAndEnvironment(String dataId, String group, String environment) {
        log.debug("Finding config item by dataId: {}, group: {}, env: {}", dataId, group, environment);
        
        return configItemJpaRepository.findByDataIdAndGroupAndEnvironment(dataId, group, environment)
                .map(configItemMapper::toDomain);
    }

    @Override
    public Optional<ConfigItem> findById(Long id) {
        log.debug("Finding config item by id: {}", id);
        
        return configItemJpaRepository.findById(id)
                .map(configItemMapper::toDomain);
    }

    @Override
    public void delete(ConfigItem configItem) {
        log.debug("Deleting config item: {}, {}, {}", 
                configItem.getDataId(), configItem.getGroup(), configItem.getEnvironment());
        
        ConfigItemEntity entity = configItemMapper.toEntity(configItem);
        configItemJpaRepository.delete(entity);
    }

    @Override
    public List<ConfigItem> findByGroupAndEnvironment(String group, String environment) {
        log.debug("Finding config items by group: {}, env: {}", group, environment);
        
        List<ConfigItemEntity> entities = configItemJpaRepository.findByGroupAndEnvironment(group, environment);
        return configItemMapper.toDomainList(entities);
    }

    @Override
    public List<ConfigItem> findByEnvironment(String environment) {
        log.debug("Finding config items by environment: {}", environment);
        
        List<ConfigItemEntity> entities = configItemJpaRepository.findByEnvironment(environment);
        return configItemMapper.toDomainList(entities);
    }

    @Override
    public List<ConfigItem> search(String keyword, String environment) {
        log.debug("Searching config items with keyword: {}, env: {}", keyword, environment);
        
        List<ConfigItemEntity> entities = configItemJpaRepository.search(keyword, environment);
        return configItemMapper.toDomainList(entities);
    }
}
