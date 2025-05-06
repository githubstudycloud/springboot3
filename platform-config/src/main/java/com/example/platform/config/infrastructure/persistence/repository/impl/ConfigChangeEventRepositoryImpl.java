package com.example.platform.config.infrastructure.persistence.repository.impl;

import com.example.platform.config.domain.model.ConfigChangeEvent;
import com.example.platform.config.domain.repository.ConfigChangeEventRepository;
import com.example.platform.config.infrastructure.mapper.ConfigChangeEventMapper;
import com.example.platform.config.infrastructure.persistence.entity.ConfigChangeEventEntity;
import com.example.platform.config.infrastructure.persistence.repository.ConfigChangeEventJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 配置变更事件仓储实现
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ConfigChangeEventRepositoryImpl implements ConfigChangeEventRepository {

    private final ConfigChangeEventJpaRepository changeEventJpaRepository;
    private final ConfigChangeEventMapper changeEventMapper;

    @Override
    public ConfigChangeEvent save(ConfigChangeEvent event) {
        log.debug("Saving change event for config: {}, {}, {}, type: {}", 
                event.getDataId(), event.getGroup(), event.getEnvironment(), event.getEventType());
        
        ConfigChangeEventEntity entity = changeEventMapper.toEntity(event);
        ConfigChangeEventEntity savedEntity = changeEventJpaRepository.save(entity);
        return changeEventMapper.toDomain(savedEntity);
    }

    @Override
    public List<ConfigChangeEvent> findByDataIdAndGroupAndEnvironment(String dataId, String group, String environment) {
        log.debug("Finding change events for config: {}, {}, {}", dataId, group, environment);
        
        List<ConfigChangeEventEntity> entities = 
                changeEventJpaRepository.findByDataIdAndGroupAndEnvironmentOrderByOccurredTimeDesc(
                        dataId, group, environment);
        return changeEventMapper.toDomainList(entities);
    }

    @Override
    public List<ConfigChangeEvent> findRecent(int limit) {
        log.debug("Finding recent {} change events", limit);
        
        List<ConfigChangeEventEntity> entities = changeEventJpaRepository.findRecentLimited(limit);
        return changeEventMapper.toDomainList(entities);
    }

    @Override
    public List<ConfigChangeEvent> findRecentByEnvironment(String environment, int limit) {
        log.debug("Finding recent {} change events for environment: {}", limit, environment);
        
        List<ConfigChangeEventEntity> entities = 
                changeEventJpaRepository.findRecentByEnvironmentLimited(environment, limit);
        return changeEventMapper.toDomainList(entities);
    }
}
