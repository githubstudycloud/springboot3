package com.platform.monitor.domain.service;

import com.platform.monitor.domain.model.Dashboard;
import com.platform.monitor.domain.model.DashboardPanel;
import com.platform.monitor.domain.model.MetricQuery;
import com.platform.monitor.domain.model.PanelType;

import java.util.List;
import java.util.Map;

/**
 * 仪表板领域服务接口
 */
public interface DashboardService {
    
    /**
     * 创建仪表板
     * 
     * @param dashboard 仪表板
     * @return 创建的仪表板
     */
    Dashboard createDashboard(Dashboard dashboard);
    
    /**
     * 更新仪表板
     * 
     * @param dashboard 仪表板
     * @return 更新后的仪表板
     */
    Dashboard updateDashboard(Dashboard dashboard);
    
    /**
     * 删除仪表板
     * 
     * @param dashboardId 仪表板ID
     * @return 操作是否成功
     */
    boolean deleteDashboard(String dashboardId);
    
    /**
     * 获取仪表板
     * 
     * @param dashboardId 仪表板ID
     * @return 仪表板
     */
    Dashboard getDashboard(String dashboardId);
    
    /**
     * 获取所有仪表板
     * 
     * @return 所有仪表板列表
     */
    List<Dashboard> getAllDashboards();
    
    /**
     * 获取公开仪表板
     * 
     * @return 公开仪表板列表
     */
    List<Dashboard> getPublicDashboards();
    
    /**
     * 获取用户有权访问的仪表板
     * 
     * @param userId 用户ID
     * @return 有权访问的仪表板列表
     */
    List<Dashboard> getAccessibleDashboards(String userId);
    
    /**
     * 获取用户创建的仪表板
     * 
     * @param userId 用户ID
     * @return 用户创建的仪表板列表
     */
    List<Dashboard> getUserDashboards(String userId);
    
    /**
     * 创建面板
     * 
     * @param dashboardId 仪表板ID
     * @param panel 面板
     * @return 创建的面板
     */
    DashboardPanel createPanel(String dashboardId, DashboardPanel panel);
    
    /**
     * 更新面板
     * 
     * @param dashboardId 仪表板ID
     * @param panel 面板
     * @return 更新后的面板
     */
    DashboardPanel updatePanel(String dashboardId, DashboardPanel panel);
    
    /**
     * 删除面板
     * 
     * @param dashboardId 仪表板ID
     * @param panelId 面板ID
     * @return 操作是否成功
     */
    boolean deletePanel(String dashboardId, String panelId);
    
    /**
     * 获取面板
     * 
     * @param dashboardId 仪表板ID
     * @param panelId 面板ID
     * @return 面板
     */
    DashboardPanel getPanel(String dashboardId, String panelId);
    
    /**
     * 获取仪表板的所有面板
     * 
     * @param dashboardId 仪表板ID
     * @return 面板列表
     */
    List<DashboardPanel> getDashboardPanels(String dashboardId);
    
    /**
     * 创建指标查询
     * 
     * @param dashboardId 仪表板ID
     * @param panelId 面板ID
     * @param metricQuery 指标查询
     * @return 创建的指标查询
     */
    MetricQuery createMetricQuery(String dashboardId, String panelId, MetricQuery metricQuery);
    
    /**
     * 更新指标查询
     * 
     * @param dashboardId 仪表板ID
     * @param panelId 面板ID
     * @param metricQuery 指标查询
     * @return 更新后的指标查询
     */
    MetricQuery updateMetricQuery(String dashboardId, String panelId, MetricQuery metricQuery);
    
    /**
     * 删除指标查询
     * 
     * @param dashboardId 仪表板ID
     * @param panelId 面板ID
     * @param queryId 查询ID
     * @return 操作是否成功
     */
    boolean deleteMetricQuery(String dashboardId, String panelId, String queryId);
    
    /**
     * 更新仪表板布局
     * 
     * @param dashboardId 仪表板ID
     * @param layout 布局信息（JSON格式）
     * @return 更新后的仪表板
     */
    Dashboard updateDashboardLayout(String dashboardId, String layout);
    
    /**
     * 分享仪表板
     * 
     * @param dashboardId 仪表板ID
     * @param userId 用户ID
     * @return 更新后的仪表板
     */
    Dashboard shareDashboard(String dashboardId, String userId);
    
    /**
     * 取消分享仪表板
     * 
     * @param dashboardId 仪表板ID
     * @param userId 用户ID
     * @return 更新后的仪表板
     */
    Dashboard unshareDashboard(String dashboardId, String userId);
    
    /**
     * 设置仪表板公开状态
     * 
     * @param dashboardId 仪表板ID
     * @param isPublic 是否公开
     * @return 更新后的仪表板
     */
    Dashboard setDashboardPublic(String dashboardId, boolean isPublic);
    
    /**
     * 复制仪表板
     * 
     * @param dashboardId 源仪表板ID
     * @param newName 新仪表板名称
     * @param createdBy 创建人
     * @return 复制的仪表板
     */
    Dashboard copyDashboard(String dashboardId, String newName, String createdBy);
    
    /**
     * 获取面板数据
     * 
     * @param dashboardId 仪表板ID
     * @param panelId 面板ID
     * @return 面板数据
     */
    Map<String, Object> getPanelData(String dashboardId, String panelId);
    
    /**
     * 获取仪表板所有面板数据
     * 
     * @param dashboardId 仪表板ID
     * @return 所有面板数据（面板ID -> 数据）
     */
    Map<String, Map<String, Object>> getDashboardData(String dashboardId);
    
    /**
     * 获取面板类型列表
     * 
     * @return 面板类型列表
     */
    List<PanelType> getPanelTypes();
    
    /**
     * 导出仪表板
     * 
     * @param dashboardId 仪表板ID
     * @return 导出的仪表板JSON
     */
    String exportDashboard(String dashboardId);
    
    /**
     * 导入仪表板
     * 
     * @param dashboardJson 仪表板JSON
     * @param createdBy 创建人
     * @return 导入的仪表板
     */
    Dashboard importDashboard(String dashboardJson, String createdBy);
}
