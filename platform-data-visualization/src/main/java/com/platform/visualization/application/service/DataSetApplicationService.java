package com.platform.visualization.application.service;

import com.platform.visualization.application.assembler.DataSetAssembler;
import com.platform.visualization.application.dto.DataSetDTO;
import com.platform.visualization.domain.model.dataset.DataSet;
import com.platform.visualization.domain.model.dataset.ValidationResult;
import com.platform.visualization.domain.model.datasource.DataSource;
import com.platform.visualization.domain.repository.DataSetRepository;
import com.platform.visualization.domain.repository.DataSourceRepository;
import com.platform.visualization.domain.service.DataSetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据集应用服务
 */
@Service
public class DataSetApplicationService {
    
    private final DataSetRepository dataSetRepository;
    private final DataSourceRepository dataSourceRepository;
    private final DataSetService dataSetService;
    
    public DataSetApplicationService(DataSetRepository dataSetRepository,
                                   DataSourceRepository dataSourceRepository,
                                   DataSetService dataSetService) {
        this.dataSetRepository = dataSetRepository;
        this.dataSourceRepository = dataSourceRepository;
        this.dataSetService = dataSetService;
    }
    
    /**
     * 获取所有数据集
     * 
     * @return 数据集DTO列表
     */
    @Transactional(readOnly = true)
    public List<DataSetDTO> findAll() {
        List<DataSet> dataSets = dataSetRepository.findAll();
        return DataSetAssembler.toDTOList(dataSets);
    }
    
    /**
     * 根据ID查找数据集
     * 
     * @param id 数据集ID
     * @return 数据集DTO
     */
    @Transactional(readOnly = true)
    public DataSetDTO findById(String id) {
        DataSet dataSet = dataSetRepository.findById(new DataSet.DataSetId(id))
                .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));
        return DataSetAssembler.toDTO(dataSet);
    }
    
    /**
     * 根据数据源查找数据集
     * 
     * @param dataSourceId 数据源ID
     * @return 数据集DTO列表
     */
    @Transactional(readOnly = true)
    public List<DataSetDTO> findByDataSource(String dataSourceId) {
        List<DataSet> dataSets = dataSetRepository.findByDataSource(
                new DataSource.DataSourceId(dataSourceId));
        return DataSetAssembler.toDTOList(dataSets);
    }
    
    /**
     * 创建数据集
     * 
     * @param dataSetDTO 数据集DTO
     * @return 创建后的数据集DTO
     */
    @Transactional
    public DataSetDTO create(DataSetDTO dataSetDTO) {
        // 获取关联的数据源
        DataSource dataSource = dataSourceRepository.findById(
                new DataSource.DataSourceId(dataSetDTO.getDataSourceId()))
                .orElseThrow(() -> new IllegalArgumentException("数据源不存在"));
        
        // 将DTO转换为领域模型
        DataSet dataSet = DataSetAssembler.toDomain(dataSetDTO, dataSource);
        
        // 验证数据集
        ValidationResult validationResult = dataSetService.validateDataSet(dataSet);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("数据集验证失败: " + validationResult.getMessage());
        }
        
        // 保存数据集
        DataSet savedDataSet = dataSetRepository.save(dataSet);
        
        // 将结果转换回DTO
        return DataSetAssembler.toDTO(savedDataSet);
    }
    
    /**
     * 更新数据集
     * 
     * @param id 数据集ID
     * @param dataSetDTO 数据集DTO
     * @return 更新后的数据集DTO
     */
    @Transactional
    public DataSetDTO update(String id, DataSetDTO dataSetDTO) {
        // 查找现有数据集
        DataSet existingDataSet = dataSetRepository.findById(new DataSet.DataSetId(id))
                .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));
        
        // 获取关联的数据源
        DataSource dataSource = dataSourceRepository.findById(
                new DataSource.DataSourceId(dataSetDTO.getDataSourceId()))
                .orElseThrow(() -> new IllegalArgumentException("数据源不存在"));
        
        // 将DTO转换为领域模型（保留原ID）
        DataSet updatedDataSet = DataSetAssembler.toDomain(dataSetDTO, dataSource);
        updatedDataSet.setId(existingDataSet.getId());
        
        // 验证数据集
        ValidationResult validationResult = dataSetService.validateDataSet(updatedDataSet);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("数据集验证失败: " + validationResult.getMessage());
        }
        
        // 保存更新
        DataSet savedDataSet = dataSetRepository.save(updatedDataSet);
        
        // 转换回DTO
        return DataSetAssembler.toDTO(savedDataSet);
    }
    
    /**
     * 删除数据集
     * 
     * @param id 数据集ID
     */
    @Transactional
    public void delete(String id) {
        dataSetRepository.delete(new DataSet.DataSetId(id));
    }
    
    /**
     * 刷新数据集
     * 
     * @param id 数据集ID
     * @return 刷新后的数据集DTO
     */
    @Transactional
    public DataSetDTO refresh(String id) {
        // 执行刷新
        DataSet refreshedDataSet = dataSetService.refreshDataSet(new DataSet.DataSetId(id));
        
        // 转换回DTO
        return DataSetAssembler.toDTO(refreshedDataSet);
    }
    
    /**
     * 验证数据集配置
     * 
     * @param dataSetDTO 数据集DTO
     * @return 验证结果
     */
    public ValidationResult validateDataSet(DataSetDTO dataSetDTO) {
        // 获取关联的数据源
        DataSource dataSource = dataSourceRepository.findById(
                new DataSource.DataSourceId(dataSetDTO.getDataSourceId()))
                .orElseThrow(() -> new IllegalArgumentException("数据源不存在"));
        
        // 将DTO转换为领域模型
        DataSet dataSet = DataSetAssembler.toDomain(dataSetDTO, dataSource);
        
        // 验证数据集
        return dataSetService.validateDataSet(dataSet);
    }
}