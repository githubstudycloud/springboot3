package com.platform.visualization.infrastructure.persistence.repository;

import com.platform.visualization.infrastructure.persistence.DataSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 数据源JPA存储库
 */
@Repository
public interface DataSourceJpaRepository extends JpaRepository<DataSourceEntity, String> {
    
    /**
     * 根据类型查找数据源
     * 
     * @param type 数据源类型
     * @return 数据源实体列表
     */
    List<DataSourceEntity> findByType(String type);
    
    /**
     * 根据名称查找数据源
     * 
     * @param name 数据源名称
     * @return 数据源实体列表
     */
    List<DataSourceEntity> findByNameContainingIgnoreCase(String name);
    
    /**
     * 查询活跃的数据源
     * 
     * @return 活跃的数据源实体列表
     */
    List<DataSourceEntity> findByActiveTrue();
}