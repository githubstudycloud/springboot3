package com.platform.visualization.domain.service;

import com.platform.visualization.domain.model.chart.Chart;
import com.platform.visualization.domain.model.dashboard.Dashboard;
import com.platform.visualization.domain.model.dashboard.Position;
import com.platform.visualization.domain.repository.ChartRepository;
import com.platform.visualization.domain.repository.DashboardRepository;

/**
 * 仪表板领域服务
 */
public class DashboardService {
    private final DashboardRepository dashboardRepository;
    private final ChartRepository chartRepository;

    public DashboardService(DashboardRepository dashboardRepository, ChartRepository chartRepository) {
        this.dashboardRepository = dashboardRepository;
        this.chartRepository = chartRepository;
    }

    /**
     * 添加图表到仪表板
     * 
     * @param dashboardId 仪表板ID
     * @param chartId 图表ID
     * @param position 位置信息
     * @return 更新后的仪表板
     */
    public Dashboard addChartToDashboard(Dashboard.DashboardId dashboardId, 
                                        Chart.ChartId chartId, 
                                        Position position) {
        // 领域逻辑
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new IllegalArgumentException("仪表板不存在"));
        Chart chart = chartRepository.findById(chartId)
                .orElseThrow(() -> new IllegalArgumentException("图表不存在"));
        
        dashboard.addChart(chart, position);
        return dashboardRepository.save(dashboard);
    }

    /**
     * 移除仪表板中的图表
     * 
     * @param dashboardId 仪表板ID
     * @param itemId 仪表板项ID
     * @return 更新后的仪表板
     */
    public Dashboard removeChartFromDashboard(Dashboard.DashboardId dashboardId, String itemId) {
        // 领域逻辑
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new IllegalArgumentException("仪表板不存在"));
        
        if (!dashboard.removeItem(itemId)) {
            throw new IllegalArgumentException("仪表板项不存在");
        }
        
        return dashboardRepository.save(dashboard);
    }
}