package com.platform.visualization.infrastructure.persistence.repository;

import com.platform.visualization.infrastructure.persistence.ChartEntity;
import com.platform.visualization.infrastructure.persistence.DataSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 图表JPA存储库
 */
@Repository
public interface ChartJpaRepository extends JpaRepository<ChartEntity, String> {
    
    /**
     * 根据数据集查找图表
     * 
     * @param dataSet 数据集实体
     * @return 图表实体列表
     */
    List<ChartEntity> findByDataSet(DataSetEntity dataSet);
    
    /**
     * 根据图表类型查找图表
     * 
     * @param type 图表类型
     * @return 图表实体列表
     */
    List<ChartEntity> findByType(String type);
    
    /**
     * 根据名称查找图表
     * 
     * @param name 图表名称
     * @return 图表实体列表
     */
    List<ChartEntity> findByNameContainingIgnoreCase(String name);
}