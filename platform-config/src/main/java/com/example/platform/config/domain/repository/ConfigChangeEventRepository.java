package com.example.platform.config.domain.repository;

import com.example.platform.config.domain.model.ConfigChangeEvent;

import java.util.List;

/**
 * 配置变更事件仓储接口
 * 
 * <p>定义配置变更事件的存储和查询操作</p>
 */
public interface ConfigChangeEventRepository {
    
    /**
     * 保存配置变更事件
     *
     * @param event 配置变更事件
     * @return 保存后的配置变更事件
     */
    ConfigChangeEvent save(ConfigChangeEvent event);
    
    /**
     * 查询特定配置的变更事件
     *
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @return 变更事件列表
     */
    List<ConfigChangeEvent> findByDataIdAndGroupAndEnvironment(String dataId, String group, String environment);
    
    /**
     * 查询最近的变更事件
     *
     * @param limit 查询数量
     * @return 变更事件列表
     */
    List<ConfigChangeEvent> findRecent(int limit);
    
    /**
     * 按环境查询最近的变更事件
     *
     * @param environment 环境标识
     * @param limit 查询数量
     * @return 变更事件列表
     */
    List<ConfigChangeEvent> findRecentByEnvironment(String environment, int limit);
}
