package com.platform.visualization.infrastructure.repository;

import com.platform.visualization.domain.model.chart.Chart;
import com.platform.visualization.domain.model.chart.ChartDimension;
import com.platform.visualization.domain.model.chart.ChartType;
import com.platform.visualization.domain.model.dataset.DataSet;
import com.platform.visualization.domain.model.dataset.Field;
import com.platform.visualization.domain.repository.ChartRepository;
import com.platform.visualization.infrastructure.persistence.ChartDimensionEntity;
import com.platform.visualization.infrastructure.persistence.ChartEntity;
import com.platform.visualization.infrastructure.persistence.DataSetEntity;
import com.platform.visualization.infrastructure.persistence.repository.ChartJpaRepository;
import com.platform.visualization.infrastructure.persistence.repository.DataSetJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 图表仓储实现
 */
@Repository
public class ChartRepositoryImpl implements ChartRepository {

    private final ChartJpaRepository chartJpaRepository;
    private final DataSetJpaRepository dataSetJpaRepository;
    private final DataSetRepositoryImpl dataSetRepository;

    public ChartRepositoryImpl(ChartJpaRepository chartJpaRepository,
                            DataSetJpaRepository dataSetJpaRepository,
                            DataSetRepositoryImpl dataSetRepository) {
        this.chartJpaRepository = chartJpaRepository;
        this.dataSetJpaRepository = dataSetJpaRepository;
        this.dataSetRepository = dataSetRepository;
    }

    @Override
    public Chart save(Chart chart) {
        // 将领域模型转换为实体
        ChartEntity entity = toEntity(chart);
        // 保存实体
        ChartEntity savedEntity = chartJpaRepository.save(entity);
        // 将保存后的实体转换回领域模型
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Chart> findById(Chart.ChartId id) {
        return chartJpaRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<Chart> findAll() {
        return chartJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Chart> findByDataSet(DataSet.DataSetId dataSetId) {
        Optional<DataSetEntity> dataSetEntity = dataSetJpaRepository.findById(dataSetId.getValue());
        
        if (dataSetEntity.isPresent()) {
            return chartJpaRepository.findByDataSet(dataSetEntity.get()).stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }

    @Override
    public void delete(Chart.ChartId id) {
        chartJpaRepository.deleteById(id.getValue());
    }

    /**
     * 将实体转换为领域模型
     * 
     * @param entity 图表实体
     * @return 图表领域模型
     */
    private Chart toDomain(ChartEntity entity) {
        if (entity == null) {
            return null;
        }

        Chart chart = new Chart();
        chart.setId(new Chart.ChartId(entity.getId()));
        chart.setName(entity.getName());
        chart.setDescription(entity.getDescription());
        
        if (entity.getType() != null) {
            chart.setType(ChartType.valueOf(entity.getType()));
        }
        
        // 关联数据集
        if (entity.getDataSet() != null) {
            DataSet dataSet = dataSetRepository.findById(
                    new DataSet.DataSetId(entity.getDataSet().getId())).orElse(null);
            chart.setDataSet(dataSet);
        }
        
        chart.setOptions(entity.getOptions());
        
        // 图表维度
        if (entity.getDimensions() != null && !entity.getDimensions().isEmpty() && chart.getDataSet() != null) {
            Map<String, ChartDimension> dimensions = new HashMap<>();
            
            for (ChartDimensionEntity dimensionEntity : entity.getDimensions()) {
                // 查找对应的字段
                Field field = null;
                if (dimensionEntity.getFieldName() != null && chart.getDataSet().getFields() != null) {
                    field = chart.getDataSet().getFields().stream()
                            .filter(f -> f.getName().equals(dimensionEntity.getFieldName()))
                            .findFirst()
                            .orElse(null);
                }
                
                ChartDimension.AggregationType aggregation = null;
                if (dimensionEntity.getAggregation() != null) {
                    aggregation = ChartDimension.AggregationType.valueOf(dimensionEntity.getAggregation());
                }
                
                ChartDimension dimension = new ChartDimension(
                        dimensionEntity.getName(),
                        field,
                        aggregation,
                        dimensionEntity.getAlias()
                );
                
                dimensions.put(dimensionEntity.getDimensionKey(), dimension);
            }
            
            chart.setDimensions(dimensions);
        }
        
        return chart;
    }

    /**
     * 将领域模型转换为实体
     * 
     * @param chart 图表领域模型
     * @return 图表实体
     */
    private ChartEntity toEntity(Chart chart) {
        if (chart == null) {
            return null;
        }

        ChartEntity entity = new ChartEntity();
        entity.setId(chart.getId().getValue());
        entity.setName(chart.getName());
        entity.setDescription(chart.getDescription());
        
        if (chart.getType() != null) {
            entity.setType(chart.getType().name());
        }
        
        // 关联数据集
        if (chart.getDataSet() != null) {
            DataSetEntity dataSetEntity = dataSetJpaRepository
                    .findById(chart.getDataSet().getId().getValue())
                    .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));
            entity.setDataSet(dataSetEntity);
        }
        
        entity.setOptions(chart.getOptions());
        
        // 图表维度
        if (chart.getDimensions() != null && !chart.getDimensions().isEmpty()) {
            Set<ChartDimensionEntity> dimensionEntities = new HashSet<>();
            
            for (Map.Entry<String, ChartDimension> entry : chart.getDimensions().entrySet()) {
                ChartDimension dimension = entry.getValue();
                
                ChartDimensionEntity dimensionEntity = new ChartDimensionEntity();
                dimensionEntity.setChart(entity);
                dimensionEntity.setName(dimension.getName());
                dimensionEntity.setDimensionKey(entry.getKey());
                
                if (dimension.getField() != null) {
                    dimensionEntity.setFieldName(dimension.getField().getName());
                }
                
                if (dimension.getAggregation() != null) {
                    dimensionEntity.setAggregation(dimension.getAggregation().name());
                }
                
                dimensionEntity.setAlias(dimension.getAlias());
                dimensionEntities.add(dimensionEntity);
            }
            
            entity.setDimensions(dimensionEntities);
        }
        
        return entity;
    }
}