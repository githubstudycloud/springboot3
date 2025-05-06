package com.platform.visualization.infrastructure.repository;

import com.platform.visualization.domain.model.dataset.*;
import com.platform.visualization.domain.model.datasource.DataSource;
import com.platform.visualization.domain.repository.DataSetRepository;
import com.platform.visualization.infrastructure.persistence.DataSetEntity;
import com.platform.visualization.infrastructure.persistence.DataSourceEntity;
import com.platform.visualization.infrastructure.persistence.FieldEntity;
import com.platform.visualization.infrastructure.persistence.repository.DataSetJpaRepository;
import com.platform.visualization.infrastructure.persistence.repository.DataSourceJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据集仓储实现
 */
@Repository
public class DataSetRepositoryImpl implements DataSetRepository {

    private final DataSetJpaRepository dataSetJpaRepository;
    private final DataSourceJpaRepository dataSourceJpaRepository;
    private final DataSourceRepositoryImpl dataSourceRepository;

    public DataSetRepositoryImpl(DataSetJpaRepository dataSetJpaRepository,
                              DataSourceJpaRepository dataSourceJpaRepository,
                              DataSourceRepositoryImpl dataSourceRepository) {
        this.dataSetJpaRepository = dataSetJpaRepository;
        this.dataSourceJpaRepository = dataSourceJpaRepository;
        this.dataSourceRepository = dataSourceRepository;
    }

    @Override
    public DataSet save(DataSet dataSet) {
        // 将领域模型转换为实体
        DataSetEntity entity = toEntity(dataSet);
        // 保存实体
        DataSetEntity savedEntity = dataSetJpaRepository.save(entity);
        // 将保存后的实体转换回领域模型
        return toDomain(savedEntity);
    }

    @Override
    public Optional<DataSet> findById(DataSet.DataSetId id) {
        return dataSetJpaRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<DataSet> findAll() {
        return dataSetJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataSet> findByDataSource(DataSource.DataSourceId dataSourceId) {
        Optional<DataSourceEntity> dataSourceEntity = dataSourceJpaRepository.findById(dataSourceId.getValue());
        
        if (dataSourceEntity.isPresent()) {
            return dataSetJpaRepository.findByDataSource(dataSourceEntity.get()).stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }

    @Override
    public void delete(DataSet.DataSetId id) {
        dataSetJpaRepository.deleteById(id.getValue());
    }

    /**
     * 将实体转换为领域模型
     * 
     * @param entity 数据集实体
     * @return 数据集领域模型
     */
    private DataSet toDomain(DataSetEntity entity) {
        if (entity == null) {
            return null;
        }

        DataSet dataSet = new DataSet();
        dataSet.setId(new DataSet.DataSetId(entity.getId()));
        dataSet.setName(entity.getName());
        dataSet.setDescription(entity.getDescription());
        
        // 关联数据源
        if (entity.getDataSource() != null) {
            DataSource dataSource = dataSourceRepository.findById(
                    new DataSource.DataSourceId(entity.getDataSource().getId())).orElse(null);
            dataSet.setDataSource(dataSource);
        }
        
        // 查询设置
        if (entity.getQueryText() != null && entity.getQueryType() != null) {
            Query query = new Query(
                    entity.getQueryText(),
                    Query.QueryType.valueOf(entity.getQueryType()),
                    entity.getQueryTimeout()
            );
            dataSet.setQuery(query);
        }
        
        // 字段
        if (entity.getFields() != null && !entity.getFields().isEmpty()) {
            List<Field> fields = entity.getFields().stream()
                    .map(fieldEntity -> new Field(
                            fieldEntity.getName(),
                            fieldEntity.getLabel(),
                            Field.FieldType.valueOf(fieldEntity.getType()),
                            fieldEntity.getFormat(),
                            fieldEntity.isCalculated(),
                            fieldEntity.getExpression()
                    ))
                    .collect(Collectors.toList());
            dataSet.setFields(fields);
        }
        
        // 刷新策略
        if (entity.getRefreshType() != null) {
            RefreshStrategy refreshStrategy;
            
            switch (RefreshStrategy.RefreshType.valueOf(entity.getRefreshType())) {
                case SCHEDULED:
                    refreshStrategy = RefreshStrategy.scheduled(entity.getCronExpression());
                    break;
                case INTERVAL:
                    if (entity.getIntervalSeconds() != null) {
                        refreshStrategy = RefreshStrategy.interval(
                                Duration.ofSeconds(entity.getIntervalSeconds())
                        );
                    } else {
                        refreshStrategy = RefreshStrategy.manual();
                    }
                    break;
                case MANUAL:
                default:
                    refreshStrategy = RefreshStrategy.manual();
                    break;
            }
            
            dataSet.setRefreshStrategy(refreshStrategy);
        }
        
        dataSet.setLastRefreshedAt(entity.getLastRefreshedAt());
        
        if (entity.getStatus() != null) {
            dataSet.setStatus(DataSetStatus.valueOf(entity.getStatus()));
        }
        
        return dataSet;
    }

    /**
     * 将领域模型转换为实体
     * 
     * @param dataSet 数据集领域模型
     * @return 数据集实体
     */
    private DataSetEntity toEntity(DataSet dataSet) {
        if (dataSet == null) {
            return null;
        }

        DataSetEntity entity = new DataSetEntity();
        entity.setId(dataSet.getId().getValue());
        entity.setName(dataSet.getName());
        entity.setDescription(dataSet.getDescription());
        
        // 关联数据源
        if (dataSet.getDataSource() != null) {
            DataSourceEntity dataSourceEntity = dataSourceJpaRepository
                    .findById(dataSet.getDataSource().getId().getValue())
                    .orElseThrow(() -> new IllegalArgumentException("数据源不存在"));
            entity.setDataSource(dataSourceEntity);
        }
        
        // 查询设置
        if (dataSet.getQuery() != null) {
            entity.setQueryText(dataSet.getQuery().getQueryText());
            entity.setQueryType(dataSet.getQuery().getQueryType().name());
            entity.setQueryTimeout(dataSet.getQuery().getTimeout());
        }
        
        // 字段
        if (dataSet.getFields() != null && !dataSet.getFields().isEmpty()) {
            Set<FieldEntity> fieldEntities = dataSet.getFields().stream()
                    .map(field -> {
                        FieldEntity fieldEntity = new FieldEntity();
                        fieldEntity.setName(field.getName());
                        fieldEntity.setLabel(field.getLabel());
                        fieldEntity.setType(field.getType().name());
                        fieldEntity.setFormat(field.getFormat());
                        fieldEntity.setCalculated(field.isCalculated());
                        fieldEntity.setExpression(field.getExpression());
                        fieldEntity.setDataSet(entity);
                        return fieldEntity;
                    })
                    .collect(Collectors.toSet());
            entity.setFields(fieldEntities);
        }
        
        // 刷新策略
        if (dataSet.getRefreshStrategy() != null) {
            entity.setRefreshType(dataSet.getRefreshStrategy().getType().name());
            entity.setCronExpression(dataSet.getRefreshStrategy().getCronExpression());
            
            if (dataSet.getRefreshStrategy().getInterval() != null) {
                entity.setIntervalSeconds(dataSet.getRefreshStrategy().getInterval().getSeconds());
            }
        }
        
        entity.setLastRefreshedAt(dataSet.getLastRefreshedAt());
        
        if (dataSet.getStatus() != null) {
            entity.setStatus(dataSet.getStatus().name());
        }
        
        return entity;
    }
}