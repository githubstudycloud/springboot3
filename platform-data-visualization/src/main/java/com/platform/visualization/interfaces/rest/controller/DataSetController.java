package com.platform.visualization.interfaces.rest.controller;

import com.platform.visualization.application.dto.DataSetDTO;
import com.platform.visualization.application.service.DataSetApplicationService;
import com.platform.visualization.domain.model.dataset.ValidationResult;
import com.platform.visualization.interfaces.rest.request.DataSetCreateRequest;
import com.platform.visualization.interfaces.rest.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据集控制器
 */
@RestController
@RequestMapping("/api/v1/data-sets")
public class DataSetController {
    
    private final DataSetApplicationService dataSetApplicationService;
    
    public DataSetController(DataSetApplicationService dataSetApplicationService) {
        this.dataSetApplicationService = dataSetApplicationService;
    }
    
    /**
     * 获取所有数据集
     * 
     * @return 数据集列表
     */
    @GetMapping
    public ApiResponse<List<DataSetDTO>> findAll() {
        List<DataSetDTO> dataSets = dataSetApplicationService.findAll();
        return ApiResponse.success(dataSets);
    }
    
    /**
     * 根据ID获取数据集
     * 
     * @param id 数据集ID
     * @return 数据集详情
     */
    @GetMapping("/{id}")
    public ApiResponse<DataSetDTO> findById(@PathVariable String id) {
        DataSetDTO dataSet = dataSetApplicationService.findById(id);
        return ApiResponse.success(dataSet);
    }
    
    /**
     * 根据数据源ID获取数据集
     * 
     * @param dataSourceId 数据源ID
     * @return 数据集列表
     */
    @GetMapping("/by-data-source/{dataSourceId}")
    public ApiResponse<List<DataSetDTO>> findByDataSource(@PathVariable String dataSourceId) {
        List<DataSetDTO> dataSets = dataSetApplicationService.findByDataSource(dataSourceId);
        return ApiResponse.success(dataSets);
    }
    
    /**
     * 创建数据集
     * 
     * @param request 数据集创建请求
     * @return 创建后的数据集
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DataSetDTO> create(@Valid @RequestBody DataSetCreateRequest request) {
        // 转换请求为DTO
        DataSetDTO dataSetDTO = convertToDTO(request);
        
        // 调用应用服务创建数据集
        DataSetDTO createdDataSet = dataSetApplicationService.create(dataSetDTO);
        
        return ApiResponse.success(createdDataSet);
    }
    
    /**
     * 更新数据集
     * 
     * @param id 数据集ID
     * @param request 数据集更新请求
     * @return 更新后的数据集
     */
    @PutMapping("/{id}")
    public ApiResponse<DataSetDTO> update(@PathVariable String id, 
                                     @Valid @RequestBody DataSetCreateRequest request) {
        // 转换请求为DTO
        DataSetDTO dataSetDTO = convertToDTO(request);
        
        // 调用应用服务更新数据集
        DataSetDTO updatedDataSet = dataSetApplicationService.update(id, dataSetDTO);
        
        return ApiResponse.success(updatedDataSet);
    }
    
    /**
     * 删除数据集
     * 
     * @param id 数据集ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable String id) {
        dataSetApplicationService.delete(id);
        return ApiResponse.success();
    }
    
    /**
     * 刷新数据集
     * 
     * @param id 数据集ID
     * @return 刷新后的数据集
     */
    @PostMapping("/{id}/refresh")
    public ApiResponse<DataSetDTO> refresh(@PathVariable String id) {
        DataSetDTO refreshedDataSet = dataSetApplicationService.refresh(id);
        return ApiResponse.success(refreshedDataSet);
    }
    
    /**
     * 验证数据集配置
     * 
     * @param request 数据集创建请求
     * @return 验证结果
     */
    @PostMapping("/validate")
    public ApiResponse<ValidationResult> validate(@Valid @RequestBody DataSetCreateRequest request) {
        // 转换请求为DTO
        DataSetDTO dataSetDTO = convertToDTO(request);
        
        // 调用应用服务验证数据集
        ValidationResult result = dataSetApplicationService.validateDataSet(dataSetDTO);
        
        return ApiResponse.success(result);
    }
    
    /**
     * 将创建请求转换为DTO
     * 
     * @param request 数据集创建请求
     * @return 数据集DTO
     */
    private DataSetDTO convertToDTO(DataSetCreateRequest request) {
        DataSetDTO dto = new DataSetDTO();
        dto.setName(request.getName());
        dto.setDescription(request.getDescription());
        dto.setDataSourceId(request.getDataSourceId());
        
        // 设置查询
        if (request.getQuery() != null) {
            DataSetDTO.QueryDTO queryDTO = new DataSetDTO.QueryDTO();
            queryDTO.setQueryText(request.getQuery().getQueryText());
            queryDTO.setQueryType(request.getQuery().getQueryType());
            queryDTO.setTimeout(request.getQuery().getTimeout());
            dto.setQuery(queryDTO);
        }
        
        // 设置字段
        if (request.getFields() != null && !request.getFields().isEmpty()) {
            List<DataSetDTO.FieldDTO> fieldDTOs = request.getFields().stream()
                    .map(fieldRequest -> {
                        DataSetDTO.FieldDTO fieldDTO = new DataSetDTO.FieldDTO();
                        fieldDTO.setName(fieldRequest.getName());
                        fieldDTO.setLabel(fieldRequest.getLabel());
                        fieldDTO.setType(fieldRequest.getType());
                        fieldDTO.setFormat(fieldRequest.getFormat());
                        fieldDTO.setCalculated(fieldRequest.isCalculated());
                        fieldDTO.setExpression(fieldRequest.getExpression());
                        return fieldDTO;
                    })
                    .collect(Collectors.toList());
            dto.setFields(fieldDTOs);
        }
        
        // 设置刷新策略
        if (request.getRefreshStrategy() != null) {
            DataSetDTO.RefreshStrategyDTO refreshStrategyDTO = new DataSetDTO.RefreshStrategyDTO();
            refreshStrategyDTO.setType(request.getRefreshStrategy().getType());
            refreshStrategyDTO.setCronExpression(request.getRefreshStrategy().getCronExpression());
            refreshStrategyDTO.setIntervalSeconds(request.getRefreshStrategy().getIntervalSeconds());
            dto.setRefreshStrategy(refreshStrategyDTO);
        }
        
        return dto;
    }
}