package com.example.platform.config.domain.service.impl;

import com.example.platform.config.domain.model.ConfigChangeEvent;
import com.example.platform.config.domain.model.ConfigItem;
import com.example.platform.config.domain.repository.ConfigChangeEventRepository;
import com.example.platform.config.domain.service.ConfigNotificationDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 配置通知服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigNotificationDomainServiceImpl implements ConfigNotificationDomainService {

    private final ConfigChangeEventRepository changeEventRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void publishEvent(ConfigChangeEvent event) {
        log.info("Publishing config change event: {}, {}, {}, type: {}", 
                event.getDataId(), event.getGroup(), event.getEnvironment(), event.getEventType());
        
        // 保存事件记录
        ConfigChangeEvent savedEvent = changeEventRepository.save(event);
        
        // 发布Spring事件
        eventPublisher.publishEvent(savedEvent);
    }

    @Override
    @Async
    @Transactional
    public void notifyConfigCreated(ConfigItem configItem, String operator) {
        log.info("Notifying config created: {}, {}, {}", 
                configItem.getDataId(), configItem.getGroup(), configItem.getEnvironment());
        
        ConfigChangeEvent event = ConfigChangeEvent.createEvent(configItem, operator);
        publishEvent(event);
    }

    @Override
    @Async
    @Transactional
    public void notifyConfigUpdated(ConfigItem configItem, String operator) {
        log.info("Notifying config updated: {}, {}, {}", 
                configItem.getDataId(), configItem.getGroup(), configItem.getEnvironment());
        
        ConfigChangeEvent event = ConfigChangeEvent.updateEvent(configItem, operator);
        publishEvent(event);
    }

    @Override
    @Async
    @Transactional
    public void notifyConfigDeleted(ConfigItem configItem, String operator) {
        log.info("Notifying config deleted: {}, {}, {}", 
                configItem.getDataId(), configItem.getGroup(), configItem.getEnvironment());
        
        ConfigChangeEvent event = ConfigChangeEvent.deleteEvent(configItem, operator);
        publishEvent(event);
    }

    @Override
    public List<ConfigChangeEvent> getChangeEvents(String dataId, String group, String environment) {
        log.info("Getting change events for config: {}, {}, {}", dataId, group, environment);
        return changeEventRepository.findByDataIdAndGroupAndEnvironment(dataId, group, environment);
    }

    @Override
    public List<ConfigChangeEvent> getRecentEvents(int limit) {
        log.info("Getting recent {} change events", limit);
        return changeEventRepository.findRecent(limit);
    }

    @Override
    public List<ConfigChangeEvent> getRecentEventsByEnvironment(String environment, int limit) {
        log.info("Getting recent {} change events for environment: {}", limit, environment);
        return changeEventRepository.findRecentByEnvironment(environment, limit);
    }
}
