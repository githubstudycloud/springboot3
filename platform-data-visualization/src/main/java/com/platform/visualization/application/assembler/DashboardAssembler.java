package com.platform.visualization.application.assembler;

import com.platform.visualization.application.dto.DashboardDTO;
import com.platform.visualization.domain.model.chart.Chart;
import com.platform.visualization.domain.model.dashboard.Dashboard;
import com.platform.visualization.domain.model.dashboard.DashboardItem;
import com.platform.visualization.domain.model.dashboard.Layout;
import com.platform.visualization.domain.model.dashboard.Position;
import com.platform.visualization.domain.model.dashboard.Theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仪表板DTO与领域模型转换器
 */
public class DashboardAssembler {
    
    /**
     * 将领域模型转换为DTO
     * 
     * @param dashboard 仪表板领域模型
     * @return 仪表板DTO
     */
    public static DashboardDTO toDTO(Dashboard dashboard) {
        if (dashboard == null) {
            return null;
        }
        
        DashboardDTO dto = new DashboardDTO();
        dto.setId(dashboard.getId().getValue());
        dto.setName(dashboard.getName());
        dto.setDescription(dashboard.getDescription());
        
        // 设置仪表板项
        if (dashboard.getItems() != null) {
            List<DashboardDTO.DashboardItemDTO> itemDTOs = dashboard.getItems().stream()
                    .map(item -> {
                        DashboardDTO.DashboardItemDTO itemDTO = new DashboardDTO.DashboardItemDTO();
                        itemDTO.setId(item.getId());
                        
                        if (item.getChart() != null) {
                            itemDTO.setChartId(item.getChart().getId().getValue());
                        }
                        
                        if (item.getPosition() != null) {
                            DashboardDTO.PositionDTO positionDTO = new DashboardDTO.PositionDTO();
                            positionDTO.setX(item.getPosition().getX());
                            positionDTO.setY(item.getPosition().getY());
                            positionDTO.setWidth(item.getPosition().getWidth());
                            positionDTO.setHeight(item.getPosition().getHeight());
                            itemDTO.setPosition(positionDTO);
                        }
                        
                        return itemDTO;
                    })
                    .collect(Collectors.toList());
            
            dto.setItems(itemDTOs);
        }
        
        // 设置布局
        if (dashboard.getLayout() != null) {
            DashboardDTO.LayoutDTO layoutDTO = new DashboardDTO.LayoutDTO();
            layoutDTO.setType(dashboard.getLayout().getType().name());
            layoutDTO.setColumns(dashboard.getLayout().getColumns());
            layoutDTO.setRowHeight(dashboard.getLayout().getRowHeight());
            layoutDTO.setMargin(dashboard.getLayout().getMargin());
            dto.setLayout(layoutDTO);
        }
        
        if (dashboard.getTheme() != null) {
            dto.setTheme(dashboard.getTheme().name());
        }
        
        dto.setPublic(dashboard.isPublic());
        
        return dto;
    }
    
    /**
     * 将DTO列表转换为领域模型列表
     * 
     * @param dashboards 仪表板领域模型列表
     * @return 仪表板DTO列表
     */
    public static List<DashboardDTO> toDTOList(List<Dashboard> dashboards) {
        return dashboards.stream()
                .map(DashboardAssembler::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将DTO转换为领域模型
     * 
     * @param dto 仪表板DTO
     * @param chartMap 图表ID到图表对象的映射（需要外部提供）
     * @return 仪表板领域模型
     */
    public static Dashboard toDomain(DashboardDTO dto, Map<String, Chart> chartMap) {
        if (dto == null) {
            return null;
        }
        
        Dashboard dashboard = new Dashboard();
        
        // 设置ID
        if (dto.getId() != null && !dto.getId().isEmpty()) {
            dashboard.setId(new Dashboard.DashboardId(dto.getId()));
        } else {
            dashboard.setId(new Dashboard.DashboardId());
        }
        
        dashboard.setName(dto.getName());
        dashboard.setDescription(dto.getDescription());
        
        // 设置仪表板项
        if (dto.getItems() != null) {
            List<DashboardItem> items = new ArrayList<>();
            
            for (DashboardDTO.DashboardItemDTO itemDTO : dto.getItems()) {
                Chart chart = null;
                if (itemDTO.getChartId() != null && chartMap != null) {
                    chart = chartMap.get(itemDTO.getChartId());
                }
                
                if (chart != null) {
                    Position position = null;
                    
                    if (itemDTO.getPosition() != null) {
                        position = new Position(
                                itemDTO.getPosition().getX(),
                                itemDTO.getPosition().getY(),
                                itemDTO.getPosition().getWidth(),
                                itemDTO.getPosition().getHeight()
                        );
                    }
                    
                    DashboardItem item = new DashboardItem(chart, position);
                    items.add(item);
                }
            }
            
            dashboard.setItems(items);
        }
        
        // 设置布局
        if (dto.getLayout() != null) {
            Layout layout = new Layout();
            layout.setType(Layout.LayoutType.valueOf(dto.getLayout().getType()));
            layout.setColumns(dto.getLayout().getColumns());
            layout.setRowHeight(dto.getLayout().getRowHeight());
            layout.setMargin(dto.getLayout().getMargin());
            dashboard.setLayout(layout);
        }
        
        if (dto.getTheme() != null) {
            dashboard.setTheme(Theme.valueOf(dto.getTheme()));
        }
        
        dashboard.setPublic(dto.isPublic());
        
        return dashboard;
    }
}