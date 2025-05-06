package com.platform.visualization.domain.service;

import com.platform.visualization.domain.model.dataset.DataSet;
import com.platform.visualization.domain.model.dataset.ValidationResult;
import com.platform.visualization.domain.repository.DataSetRepository;

/**
 * 数据集领域服务
 */
public class DataSetService {
    private final DataSetRepository dataSetRepository;

    public DataSetService(DataSetRepository dataSetRepository) {
        this.dataSetRepository = dataSetRepository;
    }

    /**
     * 刷新数据集
     * 
     * @param dataSetId 数据集ID
     * @return 刷新后的数据集
     */
    public DataSet refreshDataSet(DataSet.DataSetId dataSetId) {
        // 领域逻辑
        DataSet dataSet = dataSetRepository.findById(dataSetId)
                .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));
        dataSet.refresh();
        return dataSetRepository.save(dataSet);
    }

    /**
     * 验证数据集配置
     * 
     * @param dataSet 数据集
     * @return 验证结果
     */
    public ValidationResult validateDataSet(DataSet dataSet) {
        // 领域逻辑
        return dataSet.validate();
    }
}