package com.platform.visualization.interfaces.rest.controller;

import com.platform.visualization.application.dto.DataSourceDTO;
import com.platform.visualization.application.service.DataSourceApplicationService;
import com.platform.visualization.domain.model.datasource.ConnectionTestResult;
import com.platform.visualization.interfaces.rest.request.DataSourceCreateRequest;
import com.platform.visualization.interfaces.rest.request.DataSourceUpdateRequest;
import com.platform.visualization.interfaces.rest.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 数据源控制器
 */
@RestController
@RequestMapping("/api/v1/data-sources")
public class DataSourceController {
    
    private final DataSourceApplicationService dataSourceApplicationService;
    
    public DataSourceController(DataSourceApplicationService dataSourceApplicationService) {
        this.dataSourceApplicationService = dataSourceApplicationService;
    }
    
    /**
     * 获取所有数据源
     * 
     * @return 数据源列表
     */
    @GetMapping
    public ApiResponse<List<DataSourceDTO>> findAll() {
        List<DataSourceDTO> dataSources = dataSourceApplicationService.findAll();
        return ApiResponse.success(dataSources);
    }
    
    /**
     * 根据ID获取数据源
     * 
     * @param id 数据源ID
     * @return 数据源详情
     */
    @GetMapping("/{id}")
    public ApiResponse<DataSourceDTO> findById(@PathVariable String id) {
        DataSourceDTO dataSource = dataSourceApplicationService.findById(id);
        return ApiResponse.success(dataSource);
    }
    
    /**
     * 创建数据源
     * 
     * @param request 数据源创建请求
     * @return 创建后的数据源
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DataSourceDTO> create(@Valid @RequestBody DataSourceCreateRequest request) {
        // 转换请求为DTO
        DataSourceDTO dataSourceDTO = convertToDTO(request);
        
        // 调用应用服务创建数据源
        DataSourceDTO createdDataSource = dataSourceApplicationService.create(dataSourceDTO);
        
        return ApiResponse.success(createdDataSource);
    }
    
    /**
     * 更新数据源
     * 
     * @param id 数据源ID
     * @param request 数据源更新请求
     * @return 更新后的数据源
     */
    @PutMapping("/{id}")
    public ApiResponse<DataSourceDTO> update(@PathVariable String id, 
                                         @Valid @RequestBody DataSourceUpdateRequest request) {
        // 转换请求为DTO
        DataSourceDTO dataSourceDTO = convertToDTO(request);
        
        // 调用应用服务更新数据源
        DataSourceDTO updatedDataSource = dataSourceApplicationService.update(id, dataSourceDTO);
        
        return ApiResponse.success(updatedDataSource);
    }
    
    /**
     * 删除数据源
     * 
     * @param id 数据源ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable String id) {
        dataSourceApplicationService.delete(id);
        return ApiResponse.success();
    }
    
    /**
     * 测试数据源连接
     * 
     * @param request 数据源创建请求
     * @return 连接测试结果
     */
    @PostMapping("/test-connection")
    public ApiResponse<ConnectionTestResult> testConnection(@Valid @RequestBody DataSourceCreateRequest request) {
        // 转换请求为DTO
        DataSourceDTO dataSourceDTO = convertToDTO(request);
        
        // 调用应用服务测试连接
        ConnectionTestResult result = dataSourceApplicationService.testConnection(dataSourceDTO);
        
        return ApiResponse.success(result);
    }
    
    /**
     * 将创建请求转换为DTO
     * 
     * @param request 数据源创建请求
     * @return 数据源DTO
     */
    private DataSourceDTO convertToDTO(DataSourceCreateRequest request) {
        DataSourceDTO dto = new DataSourceDTO();
        dto.setName(request.getName());
        dto.setDescription(request.getDescription());
        dto.setType(request.getType());
        dto.setConnectionProperties(request.getConnectionProperties());
        dto.setActive(request.isActive());
        dto.setMetadata(request.getMetadata());
        return dto;
    }
    
    /**
     * 将更新请求转换为DTO
     * 
     * @param request 数据源更新请求
     * @return 数据源DTO
     */
    private DataSourceDTO convertToDTO(DataSourceUpdateRequest request) {
        DataSourceDTO dto = new DataSourceDTO();
        dto.setName(request.getName());
        dto.setDescription(request.getDescription());
        dto.setConnectionProperties(request.getConnectionProperties());
        dto.setActive(request.isActive());
        dto.setMetadata(request.getMetadata());
        return dto;
    }
}