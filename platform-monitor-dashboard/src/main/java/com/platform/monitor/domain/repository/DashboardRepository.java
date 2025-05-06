package com.platform.monitor.domain.repository;

import com.platform.monitor.domain.model.Dashboard;

import java.util.List;
import java.util.Optional;

/**
 * 仪表板仓储接口
 */
public interface DashboardRepository {
    
    /**
     * 保存仪表板
     *
     * @param dashboard 仪表板
     * @return 保存后的仪表板
     */
    Dashboard save(Dashboard dashboard);
    
    /**
     * 根据ID查找仪表板
     *
     * @param id 仪表板ID
     * @return 仪表板可选结果
     */
    Optional<Dashboard> findById(String id);
    
    /**
     * 根据创建人查找仪表板
     *
     * @param createdBy 创建人
     * @return 仪表板列表
     */
    List<Dashboard> findByCreatedBy(String createdBy);
    
    /**
     * 获取所有公开仪表板
     *
     * @return 公开仪表板列表
     */
    List<Dashboard> findAllPublic();
    
    /**
     * 根据用户ID查找有权访问的仪表板
     *
     * @param userId 用户ID
     * @return 有权访问的仪表板列表
     */
    List<Dashboard> findAccessibleByUserId(String userId);
    
    /**
     * 获取所有仪表板
     *
     * @return 所有仪表板列表
     */
    List<Dashboard> findAll();
    
    /**
     * 删除仪表板
     *
     * @param id 仪表板ID
     */
    void deleteById(String id);
    
    /**
     * 根据面板ID查找所属的仪表板
     *
     * @param panelId 面板ID
     * @return 仪表板可选结果
     */
    Optional<Dashboard> findByPanelId(String panelId);
    
    /**
     * 根据名称查找仪表板
     *
     * @param name 仪表板名称
     * @return 仪表板可选结果
     */
    Optional<Dashboard> findByName(String name);
    
    /**
     * 获取仪表板数量
     *
     * @return 仪表板数量
     */
    long count();
    
    /**
     * 获取用户的仪表板数量
     *
     * @param userId 用户ID
     * @return 用户的仪表板数量
     */
    long countByUserId(String userId);
}
