package com.example.platform.config.domain.service;

import com.example.platform.config.domain.model.ConfigChangeEvent;
import com.example.platform.config.domain.model.ConfigItem;

import java.util.List;

/**
 * 配置通知服务接口
 * 
 * <p>定义配置变更通知的核心业务逻辑</p>
 */
public interface ConfigNotificationDomainService {
    
    /**
     * 发布配置变更事件
     *
     * @param event 配置变更事件
     */
    void publishEvent(ConfigChangeEvent event);
    
    /**
     * 创建并发布配置创建事件
     *
     * @param configItem 配置项
     * @param operator 操作人
     */
    void notifyConfigCreated(ConfigItem configItem, String operator);
    
    /**
     * 创建并发布配置更新事件
     *
     * @param configItem 配置项
     * @param operator 操作人
     */
    void notifyConfigUpdated(ConfigItem configItem, String operator);
    
    /**
     * 创建并发布配置删除事件
     *
     * @param configItem 配置项
     * @param operator 操作人
     */
    void notifyConfigDeleted(ConfigItem configItem, String operator);
    
    /**
     * 获取特定配置的变更记录
     *
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @return 变更事件列表
     */
    List<ConfigChangeEvent> getChangeEvents(String dataId, String group, String environment);
    
    /**
     * 获取最近的变更事件
     *
     * @param limit 查询数量
     * @return 变更事件列表
     */
    List<ConfigChangeEvent> getRecentEvents(int limit);
    
    /**
     * 获取特定环境的最近变更事件
     *
     * @param environment 环境标识
     * @param limit 查询数量
     * @return 变更事件列表
     */
    List<ConfigChangeEvent> getRecentEventsByEnvironment(String environment, int limit);
}
