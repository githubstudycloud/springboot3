package com.platform.visualization.application.service;

import com.platform.visualization.application.assembler.DashboardAssembler;
import com.platform.visualization.application.dto.DashboardDTO;
import com.platform.visualization.domain.model.chart.Chart;
import com.platform.visualization.domain.model.dashboard.Dashboard;
import com.platform.visualization.domain.model.dashboard.Position;
import com.platform.visualization.domain.repository.ChartRepository;
import com.platform.visualization.domain.repository.DashboardRepository;
import com.platform.visualization.domain.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仪表板应用服务
 */
@Service
public class DashboardApplicationService {
    
    private final DashboardRepository dashboardRepository;
    private final ChartRepository chartRepository;
    private final DashboardService dashboardService;
    
    public DashboardApplicationService(DashboardRepository dashboardRepository,
                                      ChartRepository chartRepository,
                                      DashboardService dashboardService) {
        this.dashboardRepository = dashboardRepository;
        this.chartRepository = chartRepository;
        this.dashboardService = dashboardService;
    }
    
    /**
     * 获取所有仪表板
     * 
     * @return 仪表板DTO列表
     */
    @Transactional(readOnly = true)
    public List<DashboardDTO> findAll() {
        List<Dashboard> dashboards = dashboardRepository.findAll();
        return DashboardAssembler.toDTOList(dashboards);
    }
    
    /**
     * 获取所有公开仪表板
     * 
     * @return 仪表板DTO列表
     */
    @Transactional(readOnly = true)
    public List<DashboardDTO> findPublic() {
        List<Dashboard> dashboards = dashboardRepository.findPublic();
        return DashboardAssembler.toDTOList(dashboards);
    }
    
    /**
     * 根据ID查找仪表板
     * 
     * @param id 仪表板ID
     * @return 仪表板DTO
     */
    @Transactional(readOnly = true)
    public DashboardDTO findById(String id) {
        Dashboard dashboard = dashboardRepository.findById(new Dashboard.DashboardId(id))
                .orElseThrow(() -> new IllegalArgumentException("仪表板不存在"));
        return DashboardAssembler.toDTO(dashboard);
    }
    
    /**
     * 创建仪表板
     * 
     * @param dashboardDTO 仪表板DTO
     * @return 创建后的仪表板DTO
     */
    @Transactional
    public DashboardDTO create(DashboardDTO dashboardDTO) {
        // 构建图表ID到图表对象的映射
        Map<String, Chart> chartMap = new HashMap<>();
        
        if (dashboardDTO.getItems() != null) {
            List<String> chartIds = dashboardDTO.getItems().stream()
                    .map(DashboardDTO.DashboardItemDTO::getChartId)
                    .collect(Collectors.toList());
            
            // 获取所有需要的图表
            for (String chartId : chartIds) {
                chartRepository.findById(new Chart.ChartId(chartId))
                        .ifPresent(chart -> chartMap.put(chartId, chart));
            }
        }
        
        // 将DTO转换为领域模型
        Dashboard dashboard = DashboardAssembler.toDomain(dashboardDTO, chartMap);
        
        // 保存仪表板
        Dashboard savedDashboard = dashboardRepository.save(dashboard);
        
        // 将结果转换回DTO
        return DashboardAssembler.toDTO(savedDashboard);
    }
    
    /**
     * 更新仪表板
     * 
     * @param id 仪表板ID
     * @param dashboardDTO 仪表板DTO
     * @return 更新后的仪表板DTO
     */
    @Transactional
    public DashboardDTO update(String id, DashboardDTO dashboardDTO) {
        // 查找现有仪表板
        Dashboard existingDashboard = dashboardRepository.findById(new Dashboard.DashboardId(id))
                .orElseThrow(() -> new IllegalArgumentException("仪表板不存在"));
        
        // 构建图表ID到图表对象的映射
        Map<String, Chart> chartMap = new HashMap<>();
        
        if (dashboardDTO.getItems() != null) {
            List<String> chartIds = dashboardDTO.getItems().stream()
                    .map(DashboardDTO.DashboardItemDTO::getChartId)
                    .collect(Collectors.toList());
            
            // 获取所有需要的图表
            for (String chartId : chartIds) {
                chartRepository.findById(new Chart.ChartId(chartId))
                        .ifPresent(chart -> chartMap.put(chartId, chart));
            }
        }
        
        // 将DTO转换为领域模型（保留原ID）
        Dashboard updatedDashboard = DashboardAssembler.toDomain(dashboardDTO, chartMap);
        updatedDashboard.setId(existingDashboard.getId());
        
        // 保存更新
        Dashboard savedDashboard = dashboardRepository.save(updatedDashboard);
        
        // 转换回DTO
        return DashboardAssembler.toDTO(savedDashboard);
    }
    
    /**
     * 删除仪表板
     * 
     * @param id 仪表板ID
     */
    @Transactional
    public void delete(String id) {
        dashboardRepository.delete(new Dashboard.DashboardId(id));
    }
    
    /**
     * 添加图表到仪表板
     * 
     * @param dashboardId 仪表板ID
     * @param chartId 图表ID
     * @param positionDTO 位置DTO
     * @return 更新后的仪表板DTO
     */
    @Transactional
    public DashboardDTO addChart(String dashboardId, String chartId, DashboardDTO.PositionDTO positionDTO) {
        // 创建位置对象
        Position position = new Position(
                positionDTO.getX(),
                positionDTO.getY(),
                positionDTO.getWidth(),
                positionDTO.getHeight()
        );
        
        // 添加图表到仪表板
        Dashboard updatedDashboard = dashboardService.addChartToDashboard(
                new Dashboard.DashboardId(dashboardId),
                new Chart.ChartId(chartId),
                position
        );
        
        // 转换回DTO
        return DashboardAssembler.toDTO(updatedDashboard);
    }
    
    /**
     * 从仪表板移除图表
     * 
     * @param dashboardId 仪表板ID
     * @param itemId 仪表板项ID
     * @return 更新后的仪表板DTO
     */
    @Transactional
    public DashboardDTO removeChart(String dashboardId, String itemId) {
        // 从仪表板移除图表
        Dashboard updatedDashboard = dashboardService.removeChartFromDashboard(
                new Dashboard.DashboardId(dashboardId),
                itemId
        );
        
        // 转换回DTO
        return DashboardAssembler.toDTO(updatedDashboard);
    }
}