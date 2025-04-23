package com.platform.scheduler.service;

import java.util.List;
import java.util.Map;

import com.platform.scheduler.config.datasource.multi.DataSourceDefinition;

/**
 * 数据源服务接口
 * 
 * @author platform
 */
public interface DataSourceService {
    
    /**
     * 注册数据源
     * 
     * @param definition 数据源定义
     * @return 是否成功
     */
    boolean registerDataSource(DataSourceDefinition definition);
    
    /**
     * 更新数据源
     * 
     * @param definition 数据源定义
     * @return 是否成功
     */
    boolean updateDataSource(DataSourceDefinition definition);
    
    /**
     * 删除数据源
     * 
     * @param id 数据源ID
     * @return 是否成功
     */
    boolean removeDataSource(String id);
    
    /**
     * 获取数据源定义
     * 
     * @param id 数据源ID
     * @return 数据源定义
     */
    DataSourceDefinition getDataSource(String id);
    
    /**
     * 获取所有数据源定义
     * 
     * @return 数据源定义列表
     */
    List<DataSourceDefinition> getAllDataSources();
    
    /**
     * 获取默认数据源ID
     * 
     * @return 默认数据源ID
     */
    String getDefaultDataSourceId();
    
    /**
     * 设置默认数据源ID
     * 
     * @param id 数据源ID
     * @return 是否成功
     */
    boolean setDefaultDataSource(String id);
    
    /**
     * 测试数据源连接
     * 
     * @param definition 数据源定义
     * @return 是否连接成功
     */
    boolean testConnection(DataSourceDefinition definition);
    
    /**
     * 检查数据源连接状态
     * 
     * @param id 数据源ID
     * @return 是否连接正常
     */
    boolean checkDataSource(String id);
    
    /**
     * 检查所有数据源连接状态
     * 
     * @return 检查结果，键为数据源ID，值为是否连接正常
     */
    Map<String, Boolean> checkAllDataSources();
    
    /**
     * 持久化数据源配置
     * 
     * @return 是否成功
     */
    boolean saveDataSourceConfig();
    
    /**
     * 加载数据源配置
     * 
     * @return 是否成功
     */
    boolean loadDataSourceConfig();
}
