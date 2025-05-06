package com.platform.visualization.domain.repository;

import com.platform.visualization.domain.model.datasource.DataSource;

import java.util.List;
import java.util.Optional;

/**
 * 数据源仓储接口
 */
public interface DataSourceRepository {
    /**
     * 保存数据源
     * 
     * @param dataSource 数据源实体
     * @return 保存后的数据源
     */
    DataSource save(DataSource dataSource);
    
    /**
     * 根据ID查找数据源
     * 
     * @param id 数据源ID
     * @return 数据源实体
     */
    Optional<DataSource> findById(DataSource.DataSourceId id);
    
    /**
     * 查找所有数据源
     * 
     * @return 数据源列表
     */
    List<DataSource> findAll();
    
    /**
     * 根据类型查找数据源
     * 
     * @param type 数据源类型
     * @return 数据源列表
     */
    List<DataSource> findByType(String type);
    
    /**
     * 删除数据源
     * 
     * @param id 数据源ID
     */
    void delete(DataSource.DataSourceId id);
}