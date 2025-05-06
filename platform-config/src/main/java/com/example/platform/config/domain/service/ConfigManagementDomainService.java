package com.example.platform.config.domain.service;

import com.example.platform.config.domain.model.ConfigItem;

import java.util.List;
import java.util.Optional;

/**
 * 配置管理领域服务接口
 * 
 * <p>定义配置管理的核心业务逻辑</p>
 */
public interface ConfigManagementDomainService {
    
    /**
     * 创建新配置
     *
     * @param configItem 配置项
     * @param operator 操作人
     * @return 创建后的配置项
     */
    ConfigItem createConfig(ConfigItem configItem, String operator);
    
    /**
     * 更新配置
     *
     * @param configItem 配置项
     * @param changeReason 变更原因
     * @param operator 操作人
     * @return 更新后的配置项
     */
    ConfigItem updateConfig(ConfigItem configItem, String changeReason, String operator);
    
    /**
     * 删除配置
     *
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @param operator 操作人
     */
    void deleteConfig(String dataId, String group, String environment, String operator);
    
    /**
     * 获取配置
     *
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @return 配置项Optional包装
     */
    Optional<ConfigItem> getConfig(String dataId, String group, String environment);
    
    /**
     * 根据ID获取配置
     *
     * @param id 配置项ID
     * @return 配置项Optional包装
     */
    Optional<ConfigItem> getConfigById(Long id);
    
    /**
     * 查询分组下的所有配置
     *
     * @param group 配置分组
     * @param environment 环境标识
     * @return 配置项列表
     */
    List<ConfigItem> getConfigsByGroup(String group, String environment);
    
    /**
     * 查询环境下的所有配置
     *
     * @param environment 环境标识
     * @return 配置项列表
     */
    List<ConfigItem> getConfigsByEnvironment(String environment);
    
    /**
     * 搜索配置
     *
     * @param keyword 关键字
     * @param environment 环境标识
     * @return 配置项列表
     */
    List<ConfigItem> searchConfigs(String keyword, String environment);
    
    /**
     * 回滚配置到指定版本
     *
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @param version 目标版本号
     * @param operator 操作人
     * @return 回滚后的配置项
     */
    ConfigItem rollbackConfig(String dataId, String group, String environment, Integer version, String operator);
}
