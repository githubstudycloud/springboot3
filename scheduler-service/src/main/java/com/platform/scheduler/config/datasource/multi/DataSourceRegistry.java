package com.platform.scheduler.config.datasource.multi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.platform.scheduler.config.datasource.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 数据源注册表
 * 
 * @author platform
 */
@Component
@ConfigurationProperties(prefix = "scheduler.datasource")
public class DataSourceRegistry implements InitializingBean, DisposableBean {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSourceRegistry.class);
    
    /**
     * 数据源定义列表
     */
    private List<DataSourceDefinition> dataSources = new ArrayList<>();
    
    /**
     * 数据源实例缓存
     */
    private final Map<String, DataSource> dataSourceCache = new ConcurrentHashMap<>();
    
    /**
     * 默认数据源
     */
    private String defaultDataSourceId;
    
    @Autowired
    private DynamicDataSource dynamicDataSource;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化数据源
        initDataSources();
    }
    
    @Override
    public void destroy() throws Exception {
        // 关闭数据源
        closeDataSources();
    }
    
    /**
     * 初始化数据源
     */
    private void initDataSources() {
        // 获取动态数据源中已注册的数据源
        Map<Object, DataSource> existingDataSources = new HashMap<>();
        
        // 初始化配置的数据源
        for (DataSourceDefinition definition : dataSources) {
            try {
                // 创建数据源
                DataSource dataSource = createDataSource(definition);
                
                // 保存到缓存
                dataSourceCache.put(definition.getId(), dataSource);
                
                // 添加到已存在数据源集合
                existingDataSources.put(definition.getId(), dataSource);
                
                // 如果是默认数据源，设置默认ID
                if (definition.isDefaultDataSource()) {
                    defaultDataSourceId = definition.getId();
                }
                
                // 更新状态为激活
                definition.setStatus(DataSourceDefinition.DataSourceStatus.ACTIVE);
                definition.setLastCheckTime(System.currentTimeMillis());
                
                logger.info("数据源已初始化: {}", definition.getId());
                
            } catch (Exception e) {
                logger.error("初始化数据源失败: " + definition.getId(), e);
                definition.setStatus(DataSourceDefinition.DataSourceStatus.FAILED);
            }
        }
        
        // 更新动态数据源
        updateDynamicDataSource(existingDataSources);
    }
    
    /**
     * 更新动态数据源
     * 
     * @param dataSources 数据源集合
     */
    private void updateDynamicDataSource(Map<Object, DataSource> dataSources) {
        // 确保有默认数据源
        if (defaultDataSourceId != null && dataSources.containsKey(defaultDataSourceId)) {
            dynamicDataSource.setDefaultTargetDataSource(dataSources.get(defaultDataSourceId));
        } else if (!dataSources.isEmpty()) {
            // 如果没有指定默认数据源，使用第一个
            Map.Entry<Object, DataSource> first = dataSources.entrySet().iterator().next();
            defaultDataSourceId = (String) first.getKey();
            dynamicDataSource.setDefaultTargetDataSource(first.getValue());
        }
        
        // 设置目标数据源
        dynamicDataSource.setTargetDataSources(dataSources);
        
        // 刷新数据源
        dynamicDataSource.afterPropertiesSet();
        
        logger.info("动态数据源已更新，默认数据源: {}", defaultDataSourceId);
    }
    
    /**
     * 关闭数据源
     */
    private void closeDataSources() {
        for (Map.Entry<String, DataSource> entry : dataSourceCache.entrySet()) {
            try {
                DataSource dataSource = entry.getValue();
                if (dataSource instanceof HikariDataSource) {
                    ((HikariDataSource) dataSource).close();
                    logger.info("数据源已关闭: {}", entry.getKey());
                }
            } catch (Exception e) {
                logger.error("关闭数据源失败: " + entry.getKey(), e);
            }
        }
        dataSourceCache.clear();
    }
    
    /**
     * 创建数据源
     * 
     * @param definition 数据源定义
     * @return 数据源实例
     */
    private DataSource createDataSource(DataSourceDefinition definition) {
        HikariDataSource dataSource = new HikariDataSource();
        
        // 设置基本属性
        dataSource.setDriverClassName(definition.getDriverClassName());
        dataSource.setJdbcUrl(definition.getUrl());
        dataSource.setUsername(definition.getUsername());
        dataSource.setPassword(definition.getPassword());
        
        // 设置连接池配置
        Map<String, Object> poolConfig = definition.getPoolConfig();
        if (poolConfig.containsKey("minimumIdle")) {
            dataSource.setMinimumIdle((Integer) poolConfig.get("minimumIdle"));
        } else {
            dataSource.setMinimumIdle(5);
        }
        
        if (poolConfig.containsKey("maximumPoolSize")) {
            dataSource.setMaximumPoolSize((Integer) poolConfig.get("maximumPoolSize"));
        } else {
            dataSource.setMaximumPoolSize(20);
        }
        
        if (poolConfig.containsKey("idleTimeout")) {
            dataSource.setIdleTimeout((Long) poolConfig.get("idleTimeout"));
        } else {
            dataSource.setIdleTimeout(30000);
        }
        
        if (poolConfig.containsKey("maxLifetime")) {
            dataSource.setMaxLifetime((Long) poolConfig.get("maxLifetime"));
        } else {
            dataSource.setMaxLifetime(1800000);
        }
        
        if (poolConfig.containsKey("connectionTimeout")) {
            dataSource.setConnectionTimeout((Long) poolConfig.get("connectionTimeout"));
        } else {
            dataSource.setConnectionTimeout(30000);
        }
        
        // 设置连接池名称
        dataSource.setPoolName("HikariCP-" + definition.getId());
        
        // 设置连接测试查询
        dataSource.setConnectionTestQuery("SELECT 1");
        
        return dataSource;
    }
    
    /**
     * 注册数据源
     * 
     * @param definition 数据源定义
     * @return 是否成功
     */
    public boolean registerDataSource(DataSourceDefinition definition) {
        try {
            // 检查ID是否重复
            if (dataSourceCache.containsKey(definition.getId())) {
                logger.error("注册数据源失败: ID已存在: {}", definition.getId());
                return false;
            }
            
            // 创建数据源
            DataSource dataSource = createDataSource(definition);
            
            // 测试连接
            testConnection(dataSource);
            
            // 保存到缓存
            dataSourceCache.put(definition.getId(), dataSource);
            
            // 更新状态
            definition.setStatus(DataSourceDefinition.DataSourceStatus.ACTIVE);
            definition.setLastCheckTime(System.currentTimeMillis());
            definition.setCreatedTime(System.currentTimeMillis());
            definition.setUpdatedTime(System.currentTimeMillis());
            
            // 添加到配置
            dataSources.add(definition);
            
            // 更新动态数据源
            Map<Object, DataSource> existingDataSources = new HashMap<>();
            for (Map.Entry<String, DataSource> entry : dataSourceCache.entrySet()) {
                existingDataSources.put(entry.getKey(), entry.getValue());
            }
            updateDynamicDataSource(existingDataSources);
            
            logger.info("数据源注册成功: {}", definition.getId());
            return true;
            
        } catch (Exception e) {
            logger.error("注册数据源失败: " + definition.getId(), e);
            return false;
        }
    }
    
    /**
     * 测试连接
     * 
     * @param dataSource 数据源
     * @throws Exception 连接异常
     */
    private void testConnection(DataSource dataSource) throws Exception {
        try (java.sql.Connection conn = dataSource.getConnection()) {
            try (java.sql.Statement stmt = conn.createStatement()) {
                try (java.sql.ResultSet rs = stmt.executeQuery("SELECT 1")) {
                    if (!rs.next() || rs.getInt(1) != 1) {
                        throw new Exception("测试连接失败: 查询结果不正确");
                    }
                }
            }
        }
    }
    
    /**
     * 更新数据源
     * 
     * @param definition 数据源定义
     * @return 是否成功
     */
    public boolean updateDataSource(DataSourceDefinition definition) {
        try {
            // 检查ID是否存在
            if (!dataSourceCache.containsKey(definition.getId())) {
                logger.error("更新数据源失败: ID不存在: {}", definition.getId());
                return false;
            }
            
            // 关闭旧数据源
            DataSource oldDataSource = dataSourceCache.get(definition.getId());
            if (oldDataSource instanceof HikariDataSource) {
                ((HikariDataSource) oldDataSource).close();
            }
            
            // 创建新数据源
            DataSource newDataSource = createDataSource(definition);
            
            // 测试连接
            testConnection(newDataSource);
            
            // 更新缓存
            dataSourceCache.put(definition.getId(), newDataSource);
            
            // 更新状态
            definition.setStatus(DataSourceDefinition.DataSourceStatus.ACTIVE);
            definition.setLastCheckTime(System.currentTimeMillis());
            definition.setUpdatedTime(System.currentTimeMillis());
            
            // 更新配置
            for (int i = 0; i < dataSources.size(); i++) {
                if (dataSources.get(i).getId().equals(definition.getId())) {
                    dataSources.set(i, definition);
                    break;
                }
            }
            
            // 更新动态数据源
            Map<Object, DataSource> existingDataSources = new HashMap<>();
            for (Map.Entry<String, DataSource> entry : dataSourceCache.entrySet()) {
                existingDataSources.put(entry.getKey(), entry.getValue());
            }
            updateDynamicDataSource(existingDataSources);
            
            logger.info("数据源更新成功: {}", definition.getId());
            return true;
            
        } catch (Exception e) {
            logger.error("更新数据源失败: " + definition.getId(), e);
            return false;
        }
    }
    
    /**
     * 删除数据源
     * 
     * @param id 数据源ID
     * @return 是否成功
     */
    public boolean removeDataSource(String id) {
        try {
            // 检查ID是否存在
            if (!dataSourceCache.containsKey(id)) {
                logger.error("删除数据源失败: ID不存在: {}", id);
                return false;
            }
            
            // 检查是否为默认数据源
            if (id.equals(defaultDataSourceId)) {
                logger.error("删除数据源失败: 不能删除默认数据源: {}", id);
                return false;
            }
            
            // 关闭数据源
            DataSource dataSource = dataSourceCache.get(id);
            if (dataSource instanceof HikariDataSource) {
                ((HikariDataSource) dataSource).close();
            }
            
            // 从缓存中移除
            dataSourceCache.remove(id);
            
            // 从配置中移除
            dataSources.removeIf(ds -> ds.getId().equals(id));
            
            // 更新动态数据源
            Map<Object, DataSource> existingDataSources = new HashMap<>();
            for (Map.Entry<String, DataSource> entry : dataSourceCache.entrySet()) {
                existingDataSources.put(entry.getKey(), entry.getValue());
            }
            updateDynamicDataSource(existingDataSources);
            
            logger.info("数据源删除成功: {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("删除数据源失败: " + id, e);
            return false;
        }
    }
    
    /**
     * 获取数据源
     * 
     * @param id 数据源ID
     * @return 数据源实例
     */
    public DataSource getDataSource(String id) {
        return dataSourceCache.get(id);
    }
    
    /**
     * 获取数据源定义
     * 
     * @param id 数据源ID
     * @return 数据源定义
     */
    public DataSourceDefinition getDataSourceDefinition(String id) {
        for (DataSourceDefinition ds : dataSources) {
            if (ds.getId().equals(id)) {
                return ds;
            }
        }
        return null;
    }
    
    /**
     * 获取所有数据源定义
     * 
     * @return 数据源定义列表
     */
    public List<DataSourceDefinition> getAllDataSourceDefinitions() {
        return Collections.unmodifiableList(dataSources);
    }
    
    /**
     * 获取默认数据源ID
     * 
     * @return 默认数据源ID
     */
    public String getDefaultDataSourceId() {
        return defaultDataSourceId;
    }
    
    /**
     * 设置默认数据源ID
     * 
     * @param id 数据源ID
     * @return 是否成功
     */
    public boolean setDefaultDataSourceId(String id) {
        if (!dataSourceCache.containsKey(id)) {
            logger.error("设置默认数据源失败: ID不存在: {}", id);
            return false;
        }
        
        // 更新默认数据源ID
        this.defaultDataSourceId = id;
        
        // 更新数据源定义
        for (DataSourceDefinition ds : dataSources) {
            ds.setDefaultDataSource(ds.getId().equals(id));
        }
        
        // 更新动态数据源
        dynamicDataSource.setDefaultTargetDataSource(dataSourceCache.get(id));
        dynamicDataSource.afterPropertiesSet();
        
        logger.info("默认数据源已设置: {}", id);
        return true;
    }
    
    /**
     * 检查数据源连接
     * 
     * @param id 数据源ID
     * @return 是否连接正常
     */
    public boolean checkDataSource(String id) {
        try {
            DataSource dataSource = dataSourceCache.get(id);
            if (dataSource == null) {
                logger.error("检查数据源失败: ID不存在: {}", id);
                return false;
            }
            
            // 测试连接
            testConnection(dataSource);
            
            // 更新状态
            for (DataSourceDefinition ds : dataSources) {
                if (ds.getId().equals(id)) {
                    ds.setStatus(DataSourceDefinition.DataSourceStatus.ACTIVE);
                    ds.setLastCheckTime(System.currentTimeMillis());
                    break;
                }
            }
            
            logger.info("数据源连接正常: {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("数据源连接异常: " + id, e);
            
            // 更新状态
            for (DataSourceDefinition ds : dataSources) {
                if (ds.getId().equals(id)) {
                    ds.setStatus(DataSourceDefinition.DataSourceStatus.FAILED);
                    ds.setLastCheckTime(System.currentTimeMillis());
                    break;
                }
            }
            
            return false;
        }
    }
    
    /**
     * 检查所有数据源连接
     * 
     * @return 检查结果，键为数据源ID，值为是否连接正常
     */
    public Map<String, Boolean> checkAllDataSources() {
        Map<String, Boolean> results = new HashMap<>();
        
        for (String id : dataSourceCache.keySet()) {
            results.put(id, checkDataSource(id));
        }
        
        return results;
    }
    
    /**
     * 设置数据源列表
     * 
     * @param dataSources 数据源列表
     */
    public void setDataSources(List<DataSourceDefinition> dataSources) {
        this.dataSources = dataSources;
    }
}
