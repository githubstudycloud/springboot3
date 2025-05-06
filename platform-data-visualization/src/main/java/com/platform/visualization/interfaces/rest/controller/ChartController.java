package com.platform.visualization.interfaces.rest.controller;

import com.platform.visualization.application.dto.ChartDTO;
import com.platform.visualization.application.service.ChartApplicationService;
import com.platform.visualization.interfaces.rest.request.ChartCreateRequest;
import com.platform.visualization.interfaces.rest.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 图表控制器
 */
@RestController
@RequestMapping("/api/v1/charts")
public class ChartController {
    
    private final ChartApplicationService chartApplicationService;
    
    public ChartController(ChartApplicationService chartApplicationService) {
        this.chartApplicationService = chartApplicationService;
    }
    
    /**
     * 获取所有图表
     * 
     * @return 图表列表
     */
    @GetMapping
    public ApiResponse<List<ChartDTO>> findAll() {
        List<ChartDTO> charts = chartApplicationService.findAll();
        return ApiResponse.success(charts);
    }
    
    /**
     * 根据ID获取图表
     * 
     * @param id 图表ID
     * @return 图表详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ChartDTO> findById(@PathVariable String id) {
        ChartDTO chart = chartApplicationService.findById(id);
        return ApiResponse.success(chart);
    }
    
    /**
     * 根据数据集ID获取图表
     * 
     * @param dataSetId 数据集ID
     * @return 图表列表
     */
    @GetMapping("/by-data-set/{dataSetId}")
    public ApiResponse<List<ChartDTO>> findByDataSet(@PathVariable String dataSetId) {
        List<ChartDTO> charts = chartApplicationService.findByDataSet(dataSetId);
        return ApiResponse.success(charts);
    }
    
    /**
     * 创建图表
     * 
     * @param request 图表创建请求
     * @return 创建后的图表
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChartDTO> create(@Valid @RequestBody ChartCreateRequest request) {
        // 转换请求为DTO
        ChartDTO chartDTO = convertToDTO(request);
        
        // 调用应用服务创建图表
        ChartDTO createdChart = chartApplicationService.create(chartDTO);
        
        return ApiResponse.success(createdChart);
    }
    
    /**
     * 更新图表
     * 
     * @param id 图表ID
     * @param request 图表更新请求
     * @return 更新后的图表
     */
    @PutMapping("/{id}")
    public ApiResponse<ChartDTO> update(@PathVariable String id, 
                                    @Valid @RequestBody ChartCreateRequest request) {
        // 转换请求为DTO
        ChartDTO chartDTO = convertToDTO(request);
        
        // 调用应用服务更新图表
        ChartDTO updatedChart = chartApplicationService.update(id, chartDTO);
        
        return ApiResponse.success(updatedChart);
    }
    
    /**
     * 删除图表
     * 
     * @param id 图表ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable String id) {
        chartApplicationService.delete(id);
        return ApiResponse.success();
    }
    
    /**
     * 更新图表配置选项
     * 
     * @param id 图表ID
     * @param options 配置选项
     * @return 更新后的图表
     */
    @PatchMapping("/{id}/options")
    public ApiResponse<ChartDTO> updateOptions(@PathVariable String id, 
                                          @RequestBody Map<String, String> options) {
        ChartDTO updatedChart = chartApplicationService.updateOptions(id, options);
        return ApiResponse.success(updatedChart);
    }
    
    /**
     * 将创建请求转换为DTO
     * 
     * @param request 图表创建请求
     * @return 图表DTO
     */
    private ChartDTO convertToDTO(ChartCreateRequest request) {
        ChartDTO dto = new ChartDTO();
        dto.setName(request.getName());
        dto.setDescription(request.getDescription());
        dto.setType(request.getType());
        dto.setDataSetId(request.getDataSetId());
        dto.setOptions(request.getOptions());
        
        // 设置维度
        if (request.getDimensions() != null && !request.getDimensions().isEmpty()) {
            Map<String, ChartDTO.ChartDimensionDTO> dimensionDTOs = request.getDimensions().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> {
                                ChartCreateRequest.ChartDimensionRequest dimensionRequest = entry.getValue();
                                ChartDTO.ChartDimensionDTO dimensionDTO = new ChartDTO.ChartDimensionDTO();
                                dimensionDTO.setName(dimensionRequest.getName());
                                dimensionDTO.setFieldName(dimensionRequest.getFieldName());
                                dimensionDTO.setAggregation(dimensionRequest.getAggregation());
                                dimensionDTO.setAlias(dimensionRequest.getAlias());
                                return dimensionDTO;
                            }
                    ));
            dto.setDimensions(dimensionDTOs);
        }
        
        return dto;
    }
}