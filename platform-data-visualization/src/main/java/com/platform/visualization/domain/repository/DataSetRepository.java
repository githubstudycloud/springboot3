package com.platform.visualization.domain.repository;

import com.platform.visualization.domain.model.dataset.DataSet;
import com.platform.visualization.domain.model.datasource.DataSource;

import java.util.List;
import java.util.Optional;

/**
 * 数据集仓储接口
 */
public interface DataSetRepository {
    /**
     * 保存数据集
     * 
     * @param dataSet 数据集实体
     * @return 保存后的数据集
     */
    DataSet save(DataSet dataSet);
    
    /**
     * 根据ID查找数据集
     * 
     * @param id 数据集ID
     * @return 数据集实体
     */
    Optional<DataSet> findById(DataSet.DataSetId id);
    
    /**
     * 查找所有数据集
     * 
     * @return 数据集列表
     */
    List<DataSet> findAll();
    
    /**
     * 根据数据源查找数据集
     * 
     * @param dataSourceId 数据源ID
     * @return 数据集列表
     */
    List<DataSet> findByDataSource(DataSource.DataSourceId dataSourceId);
    
    /**
     * 删除数据集
     * 
     * @param id 数据集ID
     */
    void delete(DataSet.DataSetId id);
}