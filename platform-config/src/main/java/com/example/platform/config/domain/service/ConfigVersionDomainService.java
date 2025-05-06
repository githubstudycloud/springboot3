package com.example.platform.config.domain.service;

import com.example.platform.config.domain.model.ConfigVersionHistory;

import java.util.List;
import java.util.Optional;

/**
 * 配置版本管理领域服务接口
 * 
 * <p>定义配置版本管理的核心业务逻辑</p>
 */
public interface ConfigVersionDomainService {
    
    /**
     * 创建新版本历史记录
     *
     * @param configItemId 配置项ID
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @param content 配置内容
     * @param encrypted 是否加密
     * @param changeReason 变更原因
     * @param operator 操作人
     * @return 创建的版本历史记录
     */
    ConfigVersionHistory createVersionHistory(
            Long configItemId,
            String dataId,
            String group,
            String environment,
            String content,
            boolean encrypted,
            String changeReason,
            String operator);
    
    /**
     * 获取配置的版本历史列表
     *
     * @param configItemId 配置项ID
     * @return 版本历史列表
     */
    List<ConfigVersionHistory> getVersionHistories(Long configItemId);
    
    /**
     * 获取配置的版本历史列表
     *
     * @param dataId 配置ID
     * @param group 配置分组
     * @param environment 环境标识
     * @return 版本历史列表
     */
    List<ConfigVersionHistory> getVersionHistories(String dataId, String group, String environment);
    
    /**
     * 获取特定版本的配置历史
     *
     * @param configItemId 配置项ID
     * @param version 版本号
     * @return 版本历史Optional包装
     */
    Optional<ConfigVersionHistory> getVersionHistory(Long configItemId, Integer version);
    
    /**
     * 清理旧版本，只保留指定数量的最新版本
     *
     * @param configItemId 配置项ID
     * @param keepVersionCount 保留的版本数量
     */
    void cleanupOldVersions(Long configItemId, Integer keepVersionCount);
    
    /**
     * 获取最新版本号
     *
     * @param configItemId 配置项ID
     * @return 最新版本号
     */
    Integer getLatestVersionNumber(Long configItemId);
}
