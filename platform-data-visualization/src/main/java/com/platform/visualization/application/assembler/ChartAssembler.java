package com.platform.visualization.application.assembler;

import com.platform.visualization.application.dto.ChartDTO;
import com.platform.visualization.domain.model.chart.Chart;
import com.platform.visualization.domain.model.chart.ChartDimension;
import com.platform.visualization.domain.model.chart.ChartType;
import com.platform.visualization.domain.model.dataset.DataSet;
import com.platform.visualization.domain.model.dataset.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 图表DTO与领域模型转换器
 */
public class ChartAssembler {
    
    /**
     * 将领域模型转换为DTO
     * 
     * @param chart 图表领域模型
     * @return 图表DTO
     */
    public static ChartDTO toDTO(Chart chart) {
        if (chart == null) {
            return null;
        }
        
        ChartDTO dto = new ChartDTO();
        dto.setId(chart.getId().getValue());
        dto.setName(chart.getName());
        dto.setDescription(chart.getDescription());
        
        if (chart.getType() != null) {
            dto.setType(chart.getType().name());
        }
        
        if (chart.getDataSet() != null) {
            dto.setDataSetId(chart.getDataSet().getId().getValue());
        }
        
        dto.setOptions(chart.getOptions());
        
        // 转换维度
        if (chart.getDimensions() != null) {
            Map<String, ChartDTO.ChartDimensionDTO> dimensionDTOs = new HashMap<>();
            
            for (Map.Entry<String, ChartDimension> entry : chart.getDimensions().entrySet()) {
                ChartDimension dimension = entry.getValue();
                ChartDTO.ChartDimensionDTO dimensionDTO = new ChartDTO.ChartDimensionDTO();
                
                dimensionDTO.setName(dimension.getName());
                
                if (dimension.getField() != null) {
                    dimensionDTO.setFieldName(dimension.getField().getName());
                }
                
                if (dimension.getAggregation() != null) {
                    dimensionDTO.setAggregation(dimension.getAggregation().name());
                }
                
                dimensionDTO.setAlias(dimension.getAlias());
                dimensionDTOs.put(entry.getKey(), dimensionDTO);
            }
            
            dto.setDimensions(dimensionDTOs);
        }
        
        return dto;
    }
    
    /**
     * 将DTO列表转换为领域模型列表
     * 
     * @param charts 图表领域模型列表
     * @return 图表DTO列表
     */
    public static List<ChartDTO> toDTOList(List<Chart> charts) {
        return charts.stream()
                .map(ChartAssembler::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将DTO转换为领域模型
     * 
     * @param dto 图表DTO
     * @param dataSet 数据集领域模型（需要外部提供）
     * @return 图表领域模型
     */
    public static Chart toDomain(ChartDTO dto, DataSet dataSet) {
        if (dto == null) {
            return null;
        }
        
        Chart chart = new Chart();
        
        // 设置ID
        if (dto.getId() != null && !dto.getId().isEmpty()) {
            chart.setId(new Chart.ChartId(dto.getId()));
        } else {
            chart.setId(new Chart.ChartId());
        }
        
        chart.setName(dto.getName());
        chart.setDescription(dto.getDescription());
        
        if (dto.getType() != null) {
            chart.setType(ChartType.valueOf(dto.getType()));
        }
        
        chart.setDataSet(dataSet);
        chart.setOptions(dto.getOptions());
        
        // 转换维度
        if (dto.getDimensions() != null && dataSet != null && dataSet.getFields() != null) {
            Map<String, ChartDimension> dimensions = new HashMap<>();
            
            for (Map.Entry<String, ChartDTO.ChartDimensionDTO> entry : dto.getDimensions().entrySet()) {
                ChartDTO.ChartDimensionDTO dimensionDTO = entry.getValue();
                
                // 查找对应的字段
                Field field = null;
                if (dimensionDTO.getFieldName() != null) {
                    field = dataSet.getFields().stream()
                            .filter(f -> f.getName().equals(dimensionDTO.getFieldName()))
                            .findFirst()
                            .orElse(null);
                }
                
                ChartDimension.AggregationType aggregation = null;
                if (dimensionDTO.getAggregation() != null) {
                    aggregation = ChartDimension.AggregationType.valueOf(dimensionDTO.getAggregation());
                }
                
                ChartDimension dimension = new ChartDimension(
                        dimensionDTO.getName(),
                        field,
                        aggregation,
                        dimensionDTO.getAlias()
                );
                
                dimensions.put(entry.getKey(), dimension);
            }
            
            chart.setDimensions(dimensions);
        }
        
        return chart;
    }
}