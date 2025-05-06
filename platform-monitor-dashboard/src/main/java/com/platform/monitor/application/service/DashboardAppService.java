package com.platform.monitor.application.service;

import com.platform.monitor.application.dto.DashboardDTO;
import com.platform.monitor.application.dto.DashboardPanelDTO;
import com.platform.monitor.application.dto.MetricQueryDTO;

import java.util.List;
import java.util.Map;

/**
 * 仪表板管理应用服务接口
 */
public interface DashboardAppService {
    
    /**
     * 创建仪表板
     *
     * @param dashboardDTO 仪表板DTO
     * @return 创建的仪表板DTO
     */
    DashboardDTO createDashboard(DashboardDTO dashboardDTO);
    
    /**
     * 更新仪表板
     *
     * @param dashboardDTO 仪表板DTO
     * @return 更新后的仪表板DTO
     */
    DashboardDTO updateDashboard(DashboardDTO dashboardDTO);
    
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
     * @return 仪表板DTO
     */
    DashboardDTO getDashboard(String dashboardId);
    
    /**
     * 获取所有仪表板
     *
     * @return 所有仪表板DTO列表
     */
    List<DashboardDTO> getAllDashboards();
    
    /**
     * 获取公开仪表板
     *
     * @return 公开仪表板DTO列表
     */
    List<DashboardDTO> getPublicDashboards();
    
    /**
     * 获取用户有权访问的仪表板
     *
     * @param userId 用户ID
     * @return 有权访问的仪表板DTO列表
     */
    List<DashboardDTO> getAccessibleDashboards(String userId);
    
    /**
     * 获取用户创建的仪表板
     *
     * @param userId 用户ID
     * @return 用户创建的仪表板DTO列表
     */
    List<DashboardDTO> getUserDashboards(String userId);
    
    /**
     * 创建面板
     *
     * @param dashboardId 仪表板ID
     * @param panelDTO 面板DTO
     * @return 创建的面板DTO
     */
    DashboardPanelDTO createPanel(String dashboardId, DashboardPanelDTO panelDTO);
    
    /**
     * 更新面板
     *
     * @param dashboardId 仪表板ID
     * @param panelDTO 面板DTO
     * @return 更新后的面板DTO
     */
    DashboardPanelDTO updatePanel(String dashboardId, DashboardPanelDTO panelDTO);
    
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
     * @return 面板DTO
     */
    DashboardPanelDTO getPanel(String dashboardId, String panelId);
    
    /**
     * 获取仪表板的所有面板
     *
     * @param dashboardId 仪表板ID
     * @return 面板DTO列表
     */
    List<DashboardPanelDTO> getDashboardPanels(String dashboardId);
    
    /**
     * 创建指标查询
     *
     * @param dashboardId 仪表板ID
     * @param panelId 面板ID
     * @param queryDTO 指标查询DTO
     * @return 创建的指标查询DTO
     */
    MetricQueryDTO createMetricQuery(String dashboardId, String panelId, MetricQueryDTO queryDTO);
    
    /**
     * 更新指标查询
     *
     * @param dashboardId 仪表板ID
     * @param panelId 面板ID
     * @param queryDTO 指标查询DTO
     * @return 更新后的指标查询DTO
     */
    MetricQueryDTO updateMetricQuery(String dashboardId, String panelId, MetricQueryDTO queryDTO);
    
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
     * @return 更新后的仪表板DTO
     */
    DashboardDTO updateDashboardLayout(String dashboardId, String layout);
    
    /**
     * 分享仪表板
     *
     * @param dashboardId 仪表板ID
     * @param userId 用户ID
     * @return 更新后的仪表板DTO
     */
    DashboardDTO shareDashboard(String dashboardId, String userId);
    
    /**
     * 取消分享仪表板
     *
     * @param dashboardId 仪表板ID
     * @param userId 用户ID
     * @return 更新后的仪表板DTO
     */
    DashboardDTO unshareDashboard(String dashboardId, String userId);
    
    /**
     * 设置仪表板公开状态
     *
     * @param dashboardId 仪表板ID
     * @param isPublic 是否公开
     * @return 更新后的仪表板DTO
     */
    DashboardDTO setDashboardPublic(String dashboardId, boolean isPublic);
    
    /**
     * 复制仪表板
     *
     * @param dashboardId 源仪表板ID
     * @param newName 新仪表板名称
     * @param createdBy 创建人
     * @return 复制的仪表板DTO
     */
    DashboardDTO copyDashboard(String dashboardId, String newName, String createdBy);
    
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
    List<Map<String, String>> getPanelTypes();
    
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
     * @return 导入的仪表板DTO
     */
    DashboardDTO importDashboard(String dashboardJson, String createdBy);
    
    /**
     * 获取系统默认仪表板
     *
     * @return 默认仪表板DTO
     */
    DashboardDTO getDefaultDashboard();
    
    /**
     * 创建系统默认仪表板
     *
     * @param createdBy 创建人
     * @return 创建的默认仪表板DTO
     */
    DashboardDTO createDefaultDashboard(String createdBy);
    
    /**
     * 获取服务监控仪表板
     *
     * @param serviceName 服务名称
     * @return 服务监控仪表板DTO
     */
    DashboardDTO getServiceDashboard(String serviceName);
    
    /**
     * 创建服务监控仪表板
     *
     * @param serviceName 服务名称
     * @param createdBy 创建人
     * @return 创建的服务监控仪表板DTO
     */
    DashboardDTO createServiceDashboard(String serviceName, String createdBy);
    
    /**
     * 刷新面板数据
     *
     * @param dashboardId 仪表板ID
     * @param panelId 面板ID
     * @return 刷新后的面板数据
     */
    Map<String, Object> refreshPanelData(String dashboardId, String panelId);
}
