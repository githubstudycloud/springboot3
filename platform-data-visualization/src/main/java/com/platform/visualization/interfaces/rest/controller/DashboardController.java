package com.platform.visualization.interfaces.rest.controller;

import com.platform.visualization.application.dto.DashboardDTO;
import com.platform.visualization.application.service.DashboardApplicationService;
import com.platform.visualization.interfaces.rest.request.DashboardCreateRequest;
import com.platform.visualization.interfaces.rest.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 仪表板控制器
 */
@RestController
@RequestMapping("/api/v1/dashboards")
public class DashboardController {
    
    private final DashboardApplicationService dashboardApplicationService;
    
    public DashboardController(DashboardApplicationService dashboardApplicationService) {
        this.dashboardApplicationService = dashboardApplicationService;
    }
    
    /**
     * 获取所有仪表板
     * 
     * @return 仪表板列表
     */
    @GetMapping
    public ApiResponse<List<DashboardDTO>> findAll() {
        List<DashboardDTO> dashboards = dashboardApplicationService.findAll();
        return ApiResponse.success(dashboards);
    }
    
    /**
     * 获取所有公开仪表板
     * 
     * @return 公开仪表板列表
     */
    @GetMapping("/public")
    public ApiResponse<List<DashboardDTO>> findPublic() {
        List<DashboardDTO> dashboards = dashboardApplicationService.findPublic();
        return ApiResponse.success(dashboards);
    }
    
    /**
     * 根据ID获取仪表板
     * 
     * @param id 仪表板ID
     * @return 仪表板详情
     */
    @GetMapping("/{id}")
    public ApiResponse<DashboardDTO> findById(@PathVariable String id) {
        DashboardDTO dashboard = dashboardApplicationService.findById(id);
        return ApiResponse.success(dashboard);
    }
    
    /**
     * 创建仪表板
     * 
     * @param request 仪表板创建请求
     * @return 创建后的仪表板
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DashboardDTO> create(@Valid @RequestBody DashboardCreateRequest request) {
        // 转换请求为DTO
        DashboardDTO dashboardDTO = convertToDTO(request);
        
        // 调用应用服务创建仪表板
        DashboardDTO createdDashboard = dashboardApplicationService.create(dashboardDTO);
        
        return ApiResponse.success(createdDashboard);
    }
    
    /**
     * 更新仪表板
     * 
     * @param id 仪表板ID
     * @param request 仪表板更新请求
     * @return 更新后的仪表板
     */
    @PutMapping("/{id}")
    public ApiResponse<DashboardDTO> update(@PathVariable String id, 
                                       @Valid @RequestBody DashboardCreateRequest request) {
        // 转换请求为DTO
        DashboardDTO dashboardDTO = convertToDTO(request);
        
        // 调用应用服务更新仪表板
        DashboardDTO updatedDashboard = dashboardApplicationService.update(id, dashboardDTO);
        
        return ApiResponse.success(updatedDashboard);
    }
    
    /**
     * 删除仪表板
     * 
     * @param id 仪表板ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable String id) {
        dashboardApplicationService.delete(id);
        return ApiResponse.success();
    }
    
    /**
     * 添加图表到仪表板
     * 
     * @param id 仪表板ID
     * @param chartId 图表ID
     * @param position 位置信息
     * @return 更新后的仪表板
     */
    @PostMapping("/{id}/charts/{chartId}")
    public ApiResponse<DashboardDTO> addChart(@PathVariable String id, 
                                         @PathVariable String chartId, 
                                         @Valid @RequestBody DashboardCreateRequest.PositionRequest position) {
        // 转换请求为DTO
        DashboardDTO.PositionDTO positionDTO = new DashboardDTO.PositionDTO();
        positionDTO.setX(position.getX());
        positionDTO.setY(position.getY());
        positionDTO.setWidth(position.getWidth());
        positionDTO.setHeight(position.getHeight());
        
        // 调用应用服务添加图表
        DashboardDTO updatedDashboard = dashboardApplicationService.addChart(id, chartId, positionDTO);
        
        return ApiResponse.success(updatedDashboard);
    }
    
    /**
     * 从仪表板移除图表
     * 
     * @param id 仪表板ID
     * @param itemId 仪表板项ID
     * @return 更新后的仪表板
     */
    @DeleteMapping("/{id}/items/{itemId}")
    public ApiResponse<DashboardDTO> removeChart(@PathVariable String id, 
                                            @PathVariable String itemId) {
        DashboardDTO updatedDashboard = dashboardApplicationService.removeChart(id, itemId);
        return ApiResponse.success(updatedDashboard);
    }
    
    /**
     * 将创建请求转换为DTO
     * 
     * @param request 仪表板创建请求
     * @return 仪表板DTO
     */
    private DashboardDTO convertToDTO(DashboardCreateRequest request) {
        DashboardDTO dto = new DashboardDTO();
        dto.setName(request.getName());
        dto.setDescription(request.getDescription());
        dto.setPublic(request.isPublic());
        
        if (request.getTheme() != null) {
            dto.setTheme(request.getTheme());
        }
        
        // 设置布局
        if (request.getLayout() != null) {
            DashboardDTO.LayoutDTO layoutDTO = new DashboardDTO.LayoutDTO();
            layoutDTO.setType(request.getLayout().getType());
            layoutDTO.setColumns(request.getLayout().getColumns());
            layoutDTO.setRowHeight(request.getLayout().getRowHeight());
            layoutDTO.setMargin(request.getLayout().getMargin());
            dto.setLayout(layoutDTO);
        }
        
        // 设置仪表板项
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            List<DashboardDTO.DashboardItemDTO> itemDTOs = request.getItems().stream()
                    .map(itemRequest -> {
                        DashboardDTO.DashboardItemDTO itemDTO = new DashboardDTO.DashboardItemDTO();
                        itemDTO.setId(itemRequest.getId());
                        itemDTO.setChartId(itemRequest.getChartId());
                        
                        if (itemRequest.getPosition() != null) {
                            DashboardDTO.PositionDTO positionDTO = new DashboardDTO.PositionDTO();
                            positionDTO.setX(itemRequest.getPosition().getX());
                            positionDTO.setY(itemRequest.getPosition().getY());
                            positionDTO.setWidth(itemRequest.getPosition().getWidth());
                            positionDTO.setHeight(itemRequest.getPosition().getHeight());
                            itemDTO.setPosition(positionDTO);
                        }
                        
                        return itemDTO;
                    })
                    .collect(Collectors.toList());
            dto.setItems(itemDTOs);
        }
        
        return dto;
    }
}