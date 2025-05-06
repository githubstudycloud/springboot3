package com.platform.visualization.domain.repository;

import com.platform.visualization.domain.model.chart.Chart;
import com.platform.visualization.domain.model.dataset.DataSet;

import java.util.List;
import java.util.Optional;

/**
 * 图表仓储接口
 */
public interface ChartRepository {
    /**
     * 保存图表
     * 
     * @param chart 图表实体
     * @return 保存后的图表
     */
    Chart save(Chart chart);
    
    /**
     * 根据ID查找图表
     * 
     * @param id 图表ID
     * @return 图表实体
     */
    Optional<Chart> findById(Chart.ChartId id);
    
    /**
     * 查找所有图表
     * 
     * @return 图表列表
     */
    List<Chart> findAll();
    
    /**
     * 根据数据集查找图表
     * 
     * @param dataSetId 数据集ID
     * @return 图表列表
     */
    List<Chart> findByDataSet(DataSet.DataSetId dataSetId);
    
    /**
     * 删除图表
     * 
     * @param id 图表ID
     */
    void delete(Chart.ChartId id);
}