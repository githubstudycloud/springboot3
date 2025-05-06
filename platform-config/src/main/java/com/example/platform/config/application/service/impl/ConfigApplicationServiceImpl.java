package com.example.platform.config.application.service.impl;

import com.example.platform.config.application.dto.*;
import com.example.platform.config.application.service.ConfigApplicationService;
import com.example.platform.config.domain.model.ConfigChangeEvent;
import com.example.platform.config.domain.model.ConfigItem;
import com.example.platform.config.domain.model.ConfigVersionHistory;
import com.example.platform.config.domain.service.ConfigEncryptionDomainService;
import com.example.platform.config.domain.service.ConfigManagementDomainService;
import com.example.platform.config.domain.service.ConfigNotificationDomainService;
import com.example.platform.config.domain.service.ConfigVersionDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 配置中心应用服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigApplicationServiceImpl implements ConfigApplicationService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ConfigManagementDomainService configManagementDomainService;
    private final ConfigVersionDomainService configVersionDomainService;
    private final ConfigNotificationDomainService configNotificationDomainService;
    private final ConfigEncryptionDomainService configEncryptionDomainService;

    @Override
    @Transactional
    public ConfigItemDTO createConfig(CreateConfigItemRequest request, String operator) {
        log.info("Creating config: {}, group: {}, env: {}, operator: {}", 
                request.getDataId(), request.getGroup(), request.getEnvironment(), operator);
        
        // 构建领域模型
        ConfigItem configItem = ConfigItem.builder()
                .dataId(request.getDataId())
                .group(request.getGroup())
                .content(request.getContent())
                .type(request.getType())
                .environment(request.getEnvironment())
                .description(request.getDescription())
                .encrypted(request.isEncrypted())
                .build();
        
        // 调用领域服务
        ConfigItem savedConfig = configManagementDomainService.createConfig(configItem, operator);
        
        // 转换为DTO
        return convertToDTO(savedConfig);
    }

    @Override
    @Transactional
    public ConfigItemDTO updateConfig(String dataId, String group, String environment, 
                                     UpdateConfigItemRequest request, String operator) {
        log.info("Updating config: {}, group: {}, env: {}, operator: {}", 
                dataId, group, environment, operator);
        
        // 获取现有配置
        ConfigItem existingConfig = configManagementDomainService.getConfig(dataId, group, environment)
                .orElseThrow(() -> new IllegalArgumentException("Config not found"));
        
        // 更新配置信息
        existingConfig.setContent(request.getContent());
        existingConfig.setType(request.getType());
        existingConfig.setDescription(request.getDescription());
        existingConfig.setEncrypted(request.isEncrypted());
        
        // 调用领域服务
        ConfigItem updatedConfig = configManagementDomainService.updateConfig(
                existingConfig, request.getChangeReason(), operator);
        
        // 转换为DTO
        return convertToDTO(updatedConfig);
    }

    @Override
    @Transactional
    public void deleteConfig(String dataId, String group, String environment, String operator) {
        log.info("Deleting config: {}, group: {}, env: {}, operator: {}", 
                dataId, group, environment, operator);
        
        configManagementDomainService.deleteConfig(dataId, group, environment, operator);
    }

    @Override
    public ConfigItemDTO getConfig(String dataId, String group, String environment) {
        log.info("Getting config: {}, group: {}, env: {}", dataId, group, environment);
        
        return configManagementDomainService.getConfig(dataId, group, environment)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Config not found"));
    }

    @Override
    public List<ConfigVersionHistoryDTO> getConfigVersions(String dataId, String group, String environment) {
        log.info("Getting config versions: {}, group: {}, env: {}", dataId, group, environment);
        
        ConfigItem configItem = configManagementDomainService.getConfig(dataId, group, environment)
                .orElseThrow(() -> new IllegalArgumentException("Config not found"));
        
        List<ConfigVersionHistory> versionHistories = configVersionDomainService.getVersionHistories(configItem.getId());
        
        // 获取最新版本号
        Integer latestVersion = configVersionDomainService.getLatestVersionNumber(configItem.getId());
        
        return versionHistories.stream()
                .map(history -> convertToVersionDTO(history, history.getVersion().equals(latestVersion)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConfigItemDTO rollbackConfig(String dataId, String group, String environment, 
                                       RollbackConfigItemRequest request, String operator) {
        log.info("Rolling back config: {}, group: {}, env: {}, to version: {}, operator: {}", 
                dataId, group, environment, request.getVersion(), operator);
        
        ConfigItem rolledBackConfig = configManagementDomainService.rollbackConfig(
                dataId, group, environment, request.getVersion(), operator);
        
        return convertToDTO(rolledBackConfig);
    }

    @Override
    public List<ConfigItemDTO> getConfigsByGroup(String group, String environment) {
        log.info("Getting configs by group: {}, env: {}", group, environment);
        
        List<ConfigItem> configItems = configManagementDomainService.getConfigsByGroup(group, environment);
        
        return configItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConfigItemDTO> getConfigsByEnvironment(String environment) {
        log.info("Getting configs by environment: {}", environment);
        
        List<ConfigItem> configItems = configManagementDomainService.getConfigsByEnvironment(environment);
        
        return configItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConfigItemDTO> searchConfigs(String keyword, String environment) {
        log.info("Searching configs with keyword: {}, env: {}", keyword, environment);
        
        List<ConfigItem> configItems = configManagementDomainService.searchConfigs(keyword, environment);
        
        return configItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConfigChangeEventDTO> getRecentChangeEvents(int limit) {
        log.info("Getting recent {} change events", limit);
        
        List<ConfigChangeEvent> events = configNotificationDomainService.getRecentEvents(limit);
        
        return events.stream()
                .map(this::convertToEventDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConfigChangeEventDTO> getConfigChangeEvents(String dataId, String group, String environment) {
        log.info("Getting change events for config: {}, group: {}, env: {}", dataId, group, environment);
        
        List<ConfigChangeEvent> events = configNotificationDomainService.getChangeEvents(dataId, group, environment);
        
        return events.stream()
                .map(this::convertToEventDTO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为配置项DTO
     */
    private ConfigItemDTO convertToDTO(ConfigItem configItem) {
        String content = configItem.getContent();
        
        // 如果内容已加密，则解密显示
        if (configItem.isEncrypted() && configEncryptionDomainService.isEncrypted(content)) {
            content = configEncryptionDomainService.handleDecryption(content);
        }
        
        return ConfigItemDTO.builder()
                .id(configItem.getId())
                .dataId(configItem.getDataId())
                .group(configItem.getGroup())
                .content(content)
                .type(configItem.getType())
                .environment(configItem.getEnvironment())
                .description(configItem.getDescription())
                .encrypted(configItem.isEncrypted())
                .createdTime(formatDateTime(configItem.getCreatedTime()))
                .lastModifiedTime(formatDateTime(configItem.getLastModifiedTime()))
                .createdBy(configItem.getCreatedBy())
                .lastModifiedBy(configItem.getLastModifiedBy())
                .build();
    }

    /**
     * 转换为版本历史DTO
     */
    private ConfigVersionHistoryDTO convertToVersionDTO(ConfigVersionHistory history, boolean isCurrentVersion) {
        String content = history.getContent();
        
        // 如果内容已加密，则解密显示
        if (history.isEncrypted() && configEncryptionDomainService.isEncrypted(content)) {
            content = configEncryptionDomainService.handleDecryption(content);
        }
        
        return ConfigVersionHistoryDTO.builder()
                .id(history.getId())
                .configItemId(history.getConfigItemId())
                .dataId(history.getDataId())
                .group(history.getGroup())
                .version(history.getVersion())
                .content(content)
                .environment(history.getEnvironment())
                .createdTime(formatDateTime(history.getCreatedTime()))
                .createdBy(history.getCreatedBy())
                .changeReason(history.getChangeReason())
                .currentVersion(isCurrentVersion)
                .build();
    }

    /**
     * 转换为变更事件DTO
     */
    private ConfigChangeEventDTO convertToEventDTO(ConfigChangeEvent event) {
        return ConfigChangeEventDTO.builder()
                .id(event.getId())
                .dataId(event.getDataId())
                .group(event.getGroup())
                .environment(event.getEnvironment())
                .eventType(event.getEventType().name())
                .changeSummary(event.getChangeSummary())
                .occurredTime(formatDateTime(event.getOccurredTime()))
                .operator(event.getOperator())
                .build();
    }

    /**
     * 格式化日期时间
     */
    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }
}
