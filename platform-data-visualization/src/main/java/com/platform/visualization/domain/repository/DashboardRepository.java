package com.platform.visualization.domain.repository;

import com.platform.visualization.domain.model.dashboard.Dashboard;

import java.util.List;
import java.util.Optional;

/**
 * 仪表板仓储接口
 */
public interface DashboardRepository {
    /**
     * 保存仪表板
     * 
     * @param dashboard 仪表板实体
     * @return 保存后的仪表板
     */
    Dashboard save(Dashboard dashboard);
    
    /**
     * 根据ID查找仪表板
     * 
     * @param id 仪表板ID
     * @return 仪表板实体
     */
    Optional<Dashboard> findById(Dashboard.DashboardId id);
    
    /**
     * 查找所有仪表板
     * 
     * @return 仪表板列表
     */
    List<Dashboard> findAll();
    
    /**
     * 查找公开的仪表板
     * 
     * @return 公开仪表板列表
     */
    List<Dashboard> findPublic();
    
    /**
     * 删除仪表板
     * 
     * @param id 仪表板ID
     */
    void delete(Dashboard.DashboardId id);
}