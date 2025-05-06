package com.platform.visualization.infrastructure.persistence.repository;

import com.platform.visualization.infrastructure.persistence.DataSetEntity;
import com.platform.visualization.infrastructure.persistence.DataSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 数据集JPA存储库
 */
@Repository
public interface DataSetJpaRepository extends JpaRepository<DataSetEntity, String> {
    
    /**
     * 根据数据源查找数据集
     * 
     * @param dataSource 数据源实体
     * @return 数据集实体列表
     */
    List<DataSetEntity> findByDataSource(DataSourceEntity dataSource);
    
    /**
     * 根据名称查找数据集
     * 
     * @param name 数据集名称
     * @return 数据集实体列表
     */
    List<DataSetEntity> findByNameContainingIgnoreCase(String name);
    
    /**
     * 根据状态查找数据集
     * 
     * @param status 数据集状态
     * @return 数据集实体列表
     */
    List<DataSetEntity> findByStatus(String status);
}