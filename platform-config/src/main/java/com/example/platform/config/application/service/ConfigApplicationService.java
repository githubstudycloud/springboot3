package com.example.platform.config.application.service;

import com.example.platform.config.application.dto.*;

import java.util.List;

/**
 * 配置中心应用服务接口
 */
public interface ConfigApplicationService {
    
    /**
     * 创建配置项
     *
     * @param request 创建请求
     * @param operator 操作人
     * @return 创建的配置项
     */
    ConfigItemDTO createConfig(CreateConfigItemRequest request, String operator);
    
    /**
     * 更新配置项
     *
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @param request 更新请求
     * @param operator 操作人
     * @return 更新后的配置项
     */
    ConfigItemDTO updateConfig(String dataId, String group, String environment, 
                               UpdateConfigItemRequest request, String operator);
    
    /**
     * 删除配置项
     *
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @param operator 操作人
     */
    void deleteConfig(String dataId, String group, String environment, String operator);
    
    /**
     * 获取配置项
     *
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @return 配置项
     */
    ConfigItemDTO getConfig(String dataId, String group, String environment);
    
    /**
     * 获取配置历史版本
     *
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @return 版本历史列表
     */
    List<ConfigVersionHistoryDTO> getConfigVersions(String dataId, String group, String environment);
    
    /**
     * 回滚配置项到指定版本
     *
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @param request 回滚请求
     * @param operator 操作人
     * @return 回滚后的配置项
     */
    ConfigItemDTO rollbackConfig(String dataId, String group, String environment, 
                                RollbackConfigItemRequest request, String operator);
    
    /**
     * 查询分组下的所有配置
     *
     * @param group 配置分组
     * @param environment 环境标识
     * @return 配置项列表
     */
    List<ConfigItemDTO> getConfigsByGroup(String group, String environment);
    
    /**
     * 查询环境下的所有配置
     *
     * @param environment 环境标识
     * @return 配置项列表
     */
    List<ConfigItemDTO> getConfigsByEnvironment(String environment);
    
    /**
     * 搜索配置
     *
     * @param keyword 关键字
     * @param environment 环境标识
     * @return 配置项列表
     */
    List<ConfigItemDTO> searchConfigs(String keyword, String environment);
    
    /**
     * 获取最近的变更事件
     *
     * @param limit 查询数量
     * @return 变更事件列表
     */
    List<ConfigChangeEventDTO> getRecentChangeEvents(int limit);
    
    /**
     * 获取特定配置的变更事件
     *
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @return 变更事件列表
     */
    List<ConfigChangeEventDTO> getConfigChangeEvents(String dataId, String group, String environment);
}
