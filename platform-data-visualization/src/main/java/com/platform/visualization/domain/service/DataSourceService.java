package com.platform.visualization.domain.service;

import com.platform.visualization.domain.model.datasource.ConnectionTestResult;
import com.platform.visualization.domain.model.datasource.DataSource;
import com.platform.visualization.domain.repository.DataSourceRepository;

/**
 * 数据源领域服务
 */
public class DataSourceService {
    private final DataSourceRepository dataSourceRepository;

    public DataSourceService(DataSourceRepository dataSourceRepository) {
        this.dataSourceRepository = dataSourceRepository;
    }

    /**
     * 测试数据源连接
     * 
     * @param dataSource 数据源
     * @return 连接测试结果
     */
    public ConnectionTestResult testConnection(DataSource dataSource) {
        // 领域逻辑
        return dataSource.testConnection();
    }

    /**
     * 注册数据源
     * 
     * @param dataSource 数据源
     * @return 注册后的数据源
     */
    public DataSource register(DataSource dataSource) {
        // 领域逻辑
        ConnectionTestResult result = testConnection(dataSource);
        if (!result.isSuccess()) {
            throw new IllegalStateException("数据源连接失败: " + result.getMessage());
        }
        return dataSourceRepository.save(dataSource);
    }
}