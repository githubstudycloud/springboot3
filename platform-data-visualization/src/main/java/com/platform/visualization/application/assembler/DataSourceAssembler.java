package com.platform.visualization.application.assembler;

import com.platform.visualization.application.dto.DataSourceDTO;
import com.platform.visualization.domain.model.datasource.ConnectionProperties;
import com.platform.visualization.domain.model.datasource.DataSource;
import com.platform.visualization.domain.model.datasource.DataSourceType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据源DTO与领域模型转换器
 */
public class DataSourceAssembler {
    
    /**
     * 将领域模型转换为DTO
     * 
     * @param dataSource 数据源领域模型
     * @return 数据源DTO
     */
    public static DataSourceDTO toDTO(DataSource dataSource) {
        if (dataSource == null) {
            return null;
        }
        
        DataSourceDTO dto = new DataSourceDTO();
        // 获取领域模型的ID并设置到DTO
        dto.setId(dataSource.getId().getValue());
        dto.setName(dataSource.getName());
        dto.setDescription(dataSource.getDescription());
        dto.setType(dataSource.getType().name());
        dto.setConnectionProperties(dataSource.getConnectionProperties().getAllProperties());
        dto.setActive(dataSource.isActive());
        dto.setMetadata(dataSource.getMetadata());
        
        return dto;
    }
    
    /**
     * 将DTO列表转换为领域模型列表
     * 
     * @param dataSourceDTOs 数据源DTO列表
     * @return 数据源领域模型列表
     */
    public static List<DataSourceDTO> toDTOList(List<DataSource> dataSources) {
        return dataSources.stream()
                .map(DataSourceAssembler::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将DTO转换为领域模型
     * 
     * @param dto 数据源DTO
     * @return 数据源领域模型
     */
    public static DataSource toDomain(DataSourceDTO dto) {
        if (dto == null) {
            return null;
        }
        
        DataSource dataSource = new DataSource();
        
        // 如果DTO中有ID，则设置到领域模型，否则创建新ID
        if (dto.getId() != null && !dto.getId().isEmpty()) {
            dataSource.setId(new DataSource.DataSourceId(dto.getId()));
        } else {
            dataSource.setId(new DataSource.DataSourceId());
        }
        
        dataSource.setName(dto.getName());
        dataSource.setDescription(dto.getDescription());
        dataSource.setType(DataSourceType.valueOf(dto.getType()));
        
        ConnectionProperties connectionProperties = new ConnectionProperties(dto.getConnectionProperties());
        dataSource.setConnectionProperties(connectionProperties);
        
        dataSource.setActive(dto.isActive());
        dataSource.setMetadata(dto.getMetadata());
        
        return dataSource;
    }
}