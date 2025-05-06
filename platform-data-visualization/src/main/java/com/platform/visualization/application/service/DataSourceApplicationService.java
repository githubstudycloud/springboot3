package com.platform.visualization.application.service;

import com.platform.visualization.application.assembler.DataSourceAssembler;
import com.platform.visualization.application.dto.DataSourceDTO;
import com.platform.visualization.domain.model.datasource.ConnectionTestResult;
import com.platform.visualization.domain.model.datasource.DataSource;
import com.platform.visualization.domain.repository.DataSourceRepository;
import com.platform.visualization.domain.service.DataSourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据源应用服务
 */
@Service
public class DataSourceApplicationService {
    
    private final DataSourceRepository dataSourceRepository;
    private final DataSourceService dataSourceService;
    
    public DataSourceApplicationService(DataSourceRepository dataSourceRepository, 
                                        DataSourceService dataSourceService) {
        this.dataSourceRepository = dataSourceRepository;
        this.dataSourceService = dataSourceService;
    }
    
    /**
     * 获取所有数据源
     * 
     * @return 数据源DTO列表
     */
    @Transactional(readOnly = true)
    public List<DataSourceDTO> findAll() {
        List<DataSource> dataSources = dataSourceRepository.findAll();
        return DataSourceAssembler.toDTOList(dataSources);
    }
    
    /**
     * 根据ID查找数据源
     * 
     * @param id 数据源ID
     * @return 数据源DTO
     */
    @Transactional(readOnly = true)
    public DataSourceDTO findById(String id) {
        DataSource dataSource = dataSourceRepository.findById(new DataSource.DataSourceId(id))
                .orElseThrow(() -> new IllegalArgumentException("数据源不存在"));
        return DataSourceAssembler.toDTO(dataSource);
    }
    
    /**
     * 创建数据源
     * 
     * @param dataSourceDTO 数据源DTO
     * @return 创建后的数据源DTO
     */
    @Transactional
    public DataSourceDTO create(DataSourceDTO dataSourceDTO) {
        // 将DTO转换为领域模型
        DataSource dataSource = DataSourceAssembler.toDomain(dataSourceDTO);
        
        // 注册数据源（包含连接测试）
        DataSource registeredDataSource = dataSourceService.register(dataSource);
        
        // 将结果转换回DTO
        return DataSourceAssembler.toDTO(registeredDataSource);
    }
    
    /**
     * 更新数据源
     * 
     * @param id 数据源ID
     * @param dataSourceDTO 数据源DTO
     * @return 更新后的数据源DTO
     */
    @Transactional
    public DataSourceDTO update(String id, DataSourceDTO dataSourceDTO) {
        // 查找现有数据源
        DataSource existingDataSource = dataSourceRepository.findById(new DataSource.DataSourceId(id))
                .orElseThrow(() -> new IllegalArgumentException("数据源不存在"));
        
        // 更新属性
        existingDataSource.setName(dataSourceDTO.getName());
        existingDataSource.setDescription(dataSourceDTO.getDescription());
        
        // 其他属性更新
        
        // 测试连接
        ConnectionTestResult result = dataSourceService.testConnection(existingDataSource);
        if (!result.isSuccess()) {
            throw new IllegalStateException("数据源连接失败: " + result.getMessage());
        }
        
        // 保存更新
        DataSource updatedDataSource = dataSourceRepository.save(existingDataSource);
        
        // 转换回DTO
        return DataSourceAssembler.toDTO(updatedDataSource);
    }
    
    /**
     * 删除数据源
     * 
     * @param id 数据源ID
     */
    @Transactional
    public void delete(String id) {
        dataSourceRepository.delete(new DataSource.DataSourceId(id));
    }
    
    /**
     * 测试数据源连接
     * 
     * @param dataSourceDTO 数据源DTO
     * @return 连接测试结果
     */
    public ConnectionTestResult testConnection(DataSourceDTO dataSourceDTO) {
        DataSource dataSource = DataSourceAssembler.toDomain(dataSourceDTO);
        return dataSourceService.testConnection(dataSource);
    }
}