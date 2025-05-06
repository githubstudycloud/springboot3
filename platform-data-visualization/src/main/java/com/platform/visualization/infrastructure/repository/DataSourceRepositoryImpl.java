package com.platform.visualization.infrastructure.repository;

import com.platform.visualization.domain.model.datasource.ConnectionProperties;
import com.platform.visualization.domain.model.datasource.DataSource;
import com.platform.visualization.domain.model.datasource.DataSourceType;
import com.platform.visualization.domain.repository.DataSourceRepository;
import com.platform.visualization.infrastructure.persistence.DataSourceEntity;
import com.platform.visualization.infrastructure.persistence.repository.DataSourceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 数据源仓储实现
 */
@Repository
public class DataSourceRepositoryImpl implements DataSourceRepository {

    private final DataSourceJpaRepository dataSourceJpaRepository;

    public DataSourceRepositoryImpl(DataSourceJpaRepository dataSourceJpaRepository) {
        this.dataSourceJpaRepository = dataSourceJpaRepository;
    }

    @Override
    public DataSource save(DataSource dataSource) {
        // 将领域模型转换为实体
        DataSourceEntity entity = toEntity(dataSource);
        // 保存实体
        DataSourceEntity savedEntity = dataSourceJpaRepository.save(entity);
        // 将保存后的实体转换回领域模型
        return toDomain(savedEntity);
    }

    @Override
    public Optional<DataSource> findById(DataSource.DataSourceId id) {
        return dataSourceJpaRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<DataSource> findAll() {
        return dataSourceJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataSource> findByType(String type) {
        return dataSourceJpaRepository.findByType(type).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(DataSource.DataSourceId id) {
        dataSourceJpaRepository.deleteById(id.getValue());
    }

    /**
     * 将实体转换为领域模型
     * 
     * @param entity 数据源实体
     * @return 数据源领域模型
     */
    private DataSource toDomain(DataSourceEntity entity) {
        if (entity == null) {
            return null;
        }

        DataSource dataSource = new DataSource();
        dataSource.setId(new DataSource.DataSourceId(entity.getId()));
        dataSource.setName(entity.getName());
        dataSource.setDescription(entity.getDescription());
        dataSource.setType(DataSourceType.valueOf(entity.getType()));
        dataSource.setConnectionProperties(new ConnectionProperties(entity.getConnectionProperties()));
        dataSource.setActive(entity.isActive());
        dataSource.setMetadata(entity.getMetadata());

        return dataSource;
    }

    /**
     * 将领域模型转换为实体
     * 
     * @param dataSource 数据源领域模型
     * @return 数据源实体
     */
    private DataSourceEntity toEntity(DataSource dataSource) {
        if (dataSource == null) {
            return null;
        }

        DataSourceEntity entity = new DataSourceEntity();
        entity.setId(dataSource.getId().getValue());
        entity.setName(dataSource.getName());
        entity.setDescription(dataSource.getDescription());
        entity.setType(dataSource.getType().name());
        entity.setConnectionProperties(dataSource.getConnectionProperties().getAllProperties());
        entity.setActive(dataSource.isActive());
        entity.setMetadata(dataSource.getMetadata());

        return entity;
    }
}