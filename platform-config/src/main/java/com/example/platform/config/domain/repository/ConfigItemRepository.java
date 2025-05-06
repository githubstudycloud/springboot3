package com.example.platform.config.domain.repository;

import com.example.platform.config.domain.model.ConfigItem;

import java.util.List;
import java.util.Optional;

/**
 * 配置项仓储接口
 * 
 * <p>定义配置项的存储和查询操作</p>
 */
public interface ConfigItemRepository {
    
    /**
     * 保存配置项
     *
     * @param configItem 配置项
     * @return 保存后的配置项
     */
    ConfigItem save(ConfigItem configItem);
    
    /**
     * 根据dataId和group查找配置项
     *
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @return 配置项Optional包装
     */
    Optional<ConfigItem> findByDataIdAndGroupAndEnvironment(String dataId, String group, String environment);
    
    /**
     * 根据ID查找配置项
     *
     * @param id 配置项ID
     * @return 配置项Optional包装
     */
    Optional<ConfigItem> findById(Long id);
    
    /**
     * 删除配置项
     *
     * @param configItem 配置项
     */
    void delete(ConfigItem configItem);
    
    /**
     * 根据配置分组查询配置列表
     *
     * @param group 配置分组
     * @param environment 环境标识
     * @return 配置项列表
     */
    List<ConfigItem> findByGroupAndEnvironment(String group, String environment);
    
    /**
     * 根据环境查询所有配置
     *
     * @param environment 环境标识
     * @return 配置项列表
     */
    List<ConfigItem> findByEnvironment(String environment);
    
    /**
     * 模糊查询配置
     *
     * @param keyword 关键字
     * @param environment 环境标识
     * @return 配置项列表
     */
    List<ConfigItem> search(String keyword, String environment);
}
