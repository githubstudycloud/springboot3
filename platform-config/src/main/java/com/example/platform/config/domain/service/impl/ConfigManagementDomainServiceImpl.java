package com.example.platform.config.domain.service.impl;

import com.example.platform.config.domain.model.ConfigChangeEvent;
import com.example.platform.config.domain.model.ConfigItem;
import com.example.platform.config.domain.model.ConfigVersionHistory;
import com.example.platform.config.domain.repository.ConfigChangeEventRepository;
import com.example.platform.config.domain.repository.ConfigItemRepository;
import com.example.platform.config.domain.repository.ConfigVersionHistoryRepository;
import com.example.platform.config.domain.service.ConfigEncryptionDomainService;
import com.example.platform.config.domain.service.ConfigManagementDomainService;
import com.example.platform.config.domain.service.ConfigNotificationDomainService;
import com.example.platform.config.domain.service.ConfigVersionDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 配置管理领域服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigManagementDomainServiceImpl implements ConfigManagementDomainService {

    private final ConfigItemRepository configItemRepository;
    private final ConfigVersionHistoryRepository versionHistoryRepository;
    private final ConfigVersionDomainService versionDomainService;
    private final ConfigNotificationDomainService notificationDomainService;
    private final ConfigEncryptionDomainService encryptionDomainService;

    @Override
    @Transactional
    public ConfigItem createConfig(ConfigItem configItem, String operator) {
        log.info("Creating new config: {}, group: {}, env: {}", 
                configItem.getDataId(), configItem.getGroup(), configItem.getEnvironment());
        
        // 检查是否已存在
        Optional<ConfigItem> existingConfig = configItemRepository.findByDataIdAndGroupAndEnvironment(
                configItem.getDataId(), configItem.getGroup(), configItem.getEnvironment());
        if (existingConfig.isPresent()) {
            throw new IllegalArgumentException("Config already exists");
        }
        
        // 设置基本信息
        configItem.setCreatedTime(LocalDateTime.now());
        configItem.setLastModifiedTime(LocalDateTime.now());
        configItem.setCreatedBy(operator);
        configItem.setLastModifiedBy(operator);
        
        // 处理加密
        if (configItem.isEncrypted()) {
            configItem.setContent(encryptionDomainService.handleEncryption(
                    configItem.getContent(), configItem.isEncrypted()));
        }
        
        // 保存配置
        ConfigItem savedConfig = configItemRepository.save(configItem);
        
        // 创建版本历史
        ConfigVersionHistory versionHistory = versionDomainService.createVersionHistory(
                savedConfig.getId(),
                savedConfig.getDataId(),
                savedConfig.getGroup(),
                savedConfig.getEnvironment(),
                savedConfig.getContent(),
                savedConfig.isEncrypted(),
                "初始创建",
                operator);
        
        savedConfig.addVersionHistory(versionHistory);
        
        // 发布通知事件
        notificationDomainService.notifyConfigCreated(savedConfig, operator);
        
        return savedConfig;
    }

    @Override
    @Transactional
    public ConfigItem updateConfig(ConfigItem configItem, String changeReason, String operator) {
        log.info("Updating config: {}, group: {}, env: {}", 
                configItem.getDataId(), configItem.getGroup(), configItem.getEnvironment());
        
        // 检查是否存在
        ConfigItem existingConfig = configItemRepository.findByDataIdAndGroupAndEnvironment(
                configItem.getDataId(), configItem.getGroup(), configItem.getEnvironment())
                .orElseThrow(() -> new IllegalArgumentException("Config not found"));
        
        // 更新基本信息
        existingConfig.setContent(configItem.getContent());
        existingConfig.setType(configItem.getType());
        existingConfig.setDescription(configItem.getDescription());
        existingConfig.setEncrypted(configItem.isEncrypted());
        existingConfig.setLastModifiedTime(LocalDateTime.now());
        existingConfig.setLastModifiedBy(operator);
        
        // 处理加密
        if (existingConfig.isEncrypted()) {
            existingConfig.setContent(encryptionDomainService.handleEncryption(
                    existingConfig.getContent(), existingConfig.isEncrypted()));
        }
        
        // 保存配置
        ConfigItem updatedConfig = configItemRepository.save(existingConfig);
        
        // 创建版本历史
        ConfigVersionHistory versionHistory = versionDomainService.createVersionHistory(
                updatedConfig.getId(),
                updatedConfig.getDataId(),
                updatedConfig.getGroup(),
                updatedConfig.getEnvironment(),
                updatedConfig.getContent(),
                updatedConfig.isEncrypted(),
                changeReason,
                operator);
        
        updatedConfig.addVersionHistory(versionHistory);
        
        // 发布通知事件
        notificationDomainService.notifyConfigUpdated(updatedConfig, operator);
        
        return updatedConfig;
    }

    @Override
    @Transactional
    public void deleteConfig(String dataId, String group, String environment, String operator) {
        log.info("Deleting config: {}, group: {}, env: {}", dataId, group, environment);
        
        // 检查是否存在
        ConfigItem existingConfig = configItemRepository.findByDataIdAndGroupAndEnvironment(
                dataId, group, environment)
                .orElseThrow(() -> new IllegalArgumentException("Config not found"));
        
        // 发布通知事件
        notificationDomainService.notifyConfigDeleted(existingConfig, operator);
        
        // 删除配置
        configItemRepository.delete(existingConfig);
    }

    @Override
    public Optional<ConfigItem> getConfig(String dataId, String group, String environment) {
        log.info("Getting config: {}, group: {}, env: {}", dataId, group, environment);
        return configItemRepository.findByDataIdAndGroupAndEnvironment(dataId, group, environment);
    }

    @Override
    public Optional<ConfigItem> getConfigById(Long id) {
        log.info("Getting config by id: {}", id);
        return configItemRepository.findById(id);
    }

    @Override
    public List<ConfigItem> getConfigsByGroup(String group, String environment) {
        log.info("Getting configs by group: {}, env: {}", group, environment);
        return configItemRepository.findByGroupAndEnvironment(group, environment);
    }

    @Override
    public List<ConfigItem> getConfigsByEnvironment(String environment) {
        log.info("Getting configs by environment: {}", environment);
        return configItemRepository.findByEnvironment(environment);
    }

    @Override
    public List<ConfigItem> searchConfigs(String keyword, String environment) {
        log.info("Searching configs with keyword: {}, env: {}", keyword, environment);
        return configItemRepository.search(keyword, environment);
    }

    @Override
    @Transactional
    public ConfigItem rollbackConfig(String dataId, String group, String environment, Integer version, String operator) {
        log.info("Rolling back config: {}, group: {}, env: {} to version: {}", 
                dataId, group, environment, version);
        
        // 获取当前配置
        ConfigItem currentConfig = configItemRepository.findByDataIdAndGroupAndEnvironment(
                dataId, group, environment)
                .orElseThrow(() -> new IllegalArgumentException("Config not found"));
        
        // 获取历史版本
        ConfigVersionHistory targetVersion = versionHistoryRepository
                .findByConfigItemIdAndVersion(currentConfig.getId(), version)
                .orElseThrow(() -> new IllegalArgumentException("Version not found"));
        
        // 更新为历史版本内容
        currentConfig.setContent(targetVersion.getContent());
        currentConfig.setEncrypted(targetVersion.isEncrypted());
        currentConfig.setLastModifiedTime(LocalDateTime.now());
        currentConfig.setLastModifiedBy(operator);
        
        // 保存配置
        ConfigItem updatedConfig = configItemRepository.save(currentConfig);
        
        // 创建版本历史
        ConfigVersionHistory versionHistory = versionDomainService.createVersionHistory(
                updatedConfig.getId(),
                updatedConfig.getDataId(),
                updatedConfig.getGroup(),
                updatedConfig.getEnvironment(),
                updatedConfig.getContent(),
                updatedConfig.isEncrypted(),
                "回滚到版本 " + version,
                operator);
        
        updatedConfig.addVersionHistory(versionHistory);
        
        // 发布通知事件
        notificationDomainService.notifyConfigUpdated(updatedConfig, operator);
        
        return updatedConfig;
    }
}
