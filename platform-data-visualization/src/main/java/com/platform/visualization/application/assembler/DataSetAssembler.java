package com.platform.visualization.application.assembler;

import com.platform.visualization.application.dto.DataSetDTO;
import com.platform.visualization.domain.model.dataset.*;
import com.platform.visualization.domain.model.datasource.DataSource;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据集DTO与领域模型转换器
 */
public class DataSetAssembler {
    
    /**
     * 将领域模型转换为DTO
     * 
     * @param dataSet 数据集领域模型
     * @return 数据集DTO
     */
    public static DataSetDTO toDTO(DataSet dataSet) {
        if (dataSet == null) {
            return null;
        }
        
        DataSetDTO dto = new DataSetDTO();
        dto.setId(dataSet.getId().getValue());
        dto.setName(dataSet.getName());
        dto.setDescription(dataSet.getDescription());
        
        // 设置数据源ID
        if (dataSet.getDataSource() != null) {
            dto.setDataSourceId(dataSet.getDataSource().getId().getValue());
        }
        
        // 设置查询
        if (dataSet.getQuery() != null) {
            DataSetDTO.QueryDTO queryDTO = new DataSetDTO.QueryDTO();
            queryDTO.setQueryText(dataSet.getQuery().getQueryText());
            queryDTO.setQueryType(dataSet.getQuery().getQueryType().name());
            queryDTO.setTimeout(dataSet.getQuery().getTimeout());
            dto.setQuery(queryDTO);
        }
        
        // 设置字段
        if (dataSet.getFields() != null) {
            List<DataSetDTO.FieldDTO> fieldDTOs = dataSet.getFields().stream()
                    .map(field -> {
                        DataSetDTO.FieldDTO fieldDTO = new DataSetDTO.FieldDTO();
                        fieldDTO.setName(field.getName());
                        fieldDTO.setLabel(field.getLabel());
                        fieldDTO.setType(field.getType().name());
                        fieldDTO.setFormat(field.getFormat());
                        fieldDTO.setCalculated(field.isCalculated());
                        fieldDTO.setExpression(field.getExpression());
                        return fieldDTO;
                    })
                    .collect(Collectors.toList());
            dto.setFields(fieldDTOs);
        }
        
        // 设置刷新策略
        if (dataSet.getRefreshStrategy() != null) {
            DataSetDTO.RefreshStrategyDTO refreshStrategyDTO = new DataSetDTO.RefreshStrategyDTO();
            refreshStrategyDTO.setType(dataSet.getRefreshStrategy().getType().name());
            refreshStrategyDTO.setCronExpression(dataSet.getRefreshStrategy().getCronExpression());
            
            if (dataSet.getRefreshStrategy().getInterval() != null) {
                refreshStrategyDTO.setIntervalSeconds(dataSet.getRefreshStrategy().getInterval().getSeconds());
            }
            
            dto.setRefreshStrategy(refreshStrategyDTO);
        }
        
        dto.setLastRefreshedAt(dataSet.getLastRefreshedAt());
        
        if (dataSet.getStatus() != null) {
            dto.setStatus(dataSet.getStatus().name());
        }
        
        return dto;
    }
    
    /**
     * 将DTO列表转换为领域模型列表
     * 
     * @param dataSets 数据集领域模型列表
     * @return 数据集DTO列表
     */
    public static List<DataSetDTO> toDTOList(List<DataSet> dataSets) {
        return dataSets.stream()
                .map(DataSetAssembler::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将DTO转换为领域模型
     * 
     * @param dto 数据集DTO
     * @param dataSource 数据源领域模型（需要外部提供）
     * @return 数据集领域模型
     */
    public static DataSet toDomain(DataSetDTO dto, DataSource dataSource) {
        if (dto == null) {
            return null;
        }
        
        DataSet dataSet = new DataSet();
        
        // 设置ID
        if (dto.getId() != null && !dto.getId().isEmpty()) {
            dataSet.setId(new DataSet.DataSetId(dto.getId()));
        } else {
            dataSet.setId(new DataSet.DataSetId());
        }
        
        dataSet.setName(dto.getName());
        dataSet.setDescription(dto.getDescription());
        dataSet.setDataSource(dataSource);
        
        // 设置查询
        if (dto.getQuery() != null) {
            Query query = new Query(
                dto.getQuery().getQueryText(),
                Query.QueryType.valueOf(dto.getQuery().getQueryType()),
                dto.getQuery().getTimeout()
            );
            dataSet.setQuery(query);
        }
        
        // 设置字段
        if (dto.getFields() != null) {
            List<Field> fields = dto.getFields().stream()
                    .map(fieldDTO -> new Field(
                            fieldDTO.getName(),
                            fieldDTO.getLabel(),
                            Field.FieldType.valueOf(fieldDTO.getType()),
                            fieldDTO.getFormat(),
                            fieldDTO.isCalculated(),
                            fieldDTO.getExpression()
                    ))
                    .collect(Collectors.toList());
            dataSet.setFields(fields);
        }
        
        // 设置刷新策略
        if (dto.getRefreshStrategy() != null) {
            RefreshStrategy refreshStrategy;
            
            switch (RefreshStrategy.RefreshType.valueOf(dto.getRefreshStrategy().getType())) {
                case SCHEDULED:
                    refreshStrategy = RefreshStrategy.scheduled(dto.getRefreshStrategy().getCronExpression());
                    break;
                case INTERVAL:
                    refreshStrategy = RefreshStrategy.interval(
                            Duration.ofSeconds(dto.getRefreshStrategy().getIntervalSeconds())
                    );
                    break;
                case MANUAL:
                default:
                    refreshStrategy = RefreshStrategy.manual();
                    break;
            }
            
            dataSet.setRefreshStrategy(refreshStrategy);
        }
        
        dataSet.setLastRefreshedAt(dto.getLastRefreshedAt());
        
        if (dto.getStatus() != null) {
            dataSet.setStatus(DataSetStatus.valueOf(dto.getStatus()));
        }
        
        return dataSet;
    }
}