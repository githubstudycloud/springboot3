package com.example.platform.config.domain.repository;

import com.example.platform.config.domain.model.ConfigVersionHistory;

import java.util.List;
import java.util.Optional;

/**
 * 配置版本历史仓储接口
 * 
 * <p>定义配置版本历史的存储和查询操作</p>
 */
public interface ConfigVersionHistoryRepository {
    
    /**
     * 保存配置版本历史
     *
     * @param versionHistory 配置版本历史
     * @return 保存后的配置版本历史
     */
    ConfigVersionHistory save(ConfigVersionHistory versionHistory);
    
    /**
     * 根据配置项ID查询版本历史
     *
     * @param configItemId 配置项ID
     * @return 版本历史列表
     */
    List<ConfigVersionHistory> findByConfigItemId(Long configItemId);
    
    /**
     * 根据配置项dataId和group查询版本历史
     *
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @return 版本历史列表
     */
    List<ConfigVersionHistory> findByDataIdAndGroupAndEnvironment(String dataId, String group, String environment);
    
    /**
     * 查询特定版本的配置历史
     *
     * @param configItemId 配置项ID
     * @param version 版本号
     * @return 版本历史Optional包装
     */
    Optional<ConfigVersionHistory> findByConfigItemIdAndVersion(Long configItemId, Integer version);
    
    /**
     * 获取配置项的最新版本号
     *
     * @param configItemId 配置项ID
     * @return 最新版本号
     */
    Integer getLatestVersionNumber(Long configItemId);
    
    /**
     * 批量删除配置版本历史
     *
     * @param configItemId 配置项ID
     * @param keepVersionsCount 保留的版本数量
     */
    void deleteOldVersions(Long configItemId, Integer keepVersionsCount);
}
