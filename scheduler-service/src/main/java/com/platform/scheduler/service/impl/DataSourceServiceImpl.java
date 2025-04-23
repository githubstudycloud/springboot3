package com.platform.scheduler.service.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.scheduler.config.datasource.multi.DataSourceDefinition;
import com.platform.scheduler.config.datasource.multi.DataSourceRegistry;
import com.platform.scheduler.service.DataSourceService;

/**
 * 数据源服务实现类
 * 
 * @author platform
 */
@Service
public class DataSourceServiceImpl implements DataSourceService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSourceServiceImpl.class);
    
    @Autowired
    private DataSourceRegistry dataSourceRegistry;
    
    @Value("${scheduler.datasource.config-file:./config/datasources.json}")
    private String configFilePath;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean registerDataSource(DataSourceDefinition definition) {
        boolean result = dataSourceRegistry.registerDataSource(definition);
        if (result) {
            // 持久化配置
            saveDataSourceConfig();
        }
        return result;
    }
    
    @Override
    public boolean updateDataSource(DataSourceDefinition definition) {
        boolean result = dataSourceRegistry.updateDataSource(definition);
        if (result) {
            // 持久化配置
            saveDataSourceConfig();
        }
        return result;
    }
    
    @Override
    public boolean removeDataSource(String id) {
        boolean result = dataSourceRegistry.removeDataSource(id);
        if (result) {
            // 持久化配置
            saveDataSourceConfig();
        }
        return result;
    }
    
    @Override
    public DataSourceDefinition getDataSource(String id) {
        return dataSourceRegistry.getDataSourceDefinition(id);
    }
    
    @Override
    public List<DataSourceDefinition> getAllDataSources() {
        return dataSourceRegistry.getAllDataSourceDefinitions();
    }
    
    @Override
    public String getDefaultDataSourceId() {
        return dataSourceRegistry.getDefaultDataSourceId();
    }
    
    @Override
    public boolean setDefaultDataSource(String id) {
        boolean result = dataSourceRegistry.setDefaultDataSourceId(id);
        if (result) {
            // 持久化配置
            saveDataSourceConfig();
        }
        return result;
    }
    
    @Override
    public boolean testConnection(DataSourceDefinition definition) {
        try {
            // 创建临时数据源定义用于测试
            DataSourceDefinition testDefinition = new DataSourceDefinition();
            testDefinition.setId("_test_" + System.currentTimeMillis());
            testDefinition.setName(definition.getName());
            testDefinition.setType(definition.getType());
            testDefinition.setDriverClassName(definition.getDriverClassName());
            testDefinition.setUrl(definition.getUrl());
            testDefinition.setUsername(definition.getUsername());
            testDefinition.setPassword(definition.getPassword());
            testDefinition.setPoolConfig(definition.getPoolConfig());
            testDefinition.setProperties(definition.getProperties());
            
            // 注册临时数据源
            boolean registered = dataSourceRegistry.registerDataSource(testDefinition);
            if (!registered) {
                return false;
            }
            
            // 测试连接
            boolean result = dataSourceRegistry.checkDataSource(testDefinition.getId());
            
            // 移除临时数据源
            dataSourceRegistry.removeDataSource(testDefinition.getId());
            
            return result;
        } catch (Exception e) {
            logger.error("测试数据源连接失败", e);
            return false;
        }
    }
    
    @Override
    public boolean checkDataSource(String id) {
        return dataSourceRegistry.checkDataSource(id);
    }
    
    @Override
    public Map<String, Boolean> checkAllDataSources() {
        return dataSourceRegistry.checkAllDataSources();
    }
    
    @Override
    public boolean saveDataSourceConfig() {
        try {
            // 获取所有数据源定义
            List<DataSourceDefinition> dataSources = dataSourceRegistry.getAllDataSourceDefinitions();
            
            // 创建配置对象
            Map<String, Object> config = Map.of(
                    "default", dataSourceRegistry.getDefaultDataSourceId(),
                    "dataSources", dataSources
            );
            
            // 创建配置文件目录
            File configFile = new File(configFilePath);
            File configDir = configFile.getParentFile();
            if (configDir != null && !configDir.exists()) {
                if (!configDir.mkdirs()) {
                    logger.error("创建配置文件目录失败: {}", configDir.getAbsolutePath());
                    return false;
                }
            }
            
            // 写入配置文件
            try (FileWriter writer = new FileWriter(configFile)) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, config);
            }
            
            logger.info("数据源配置已保存: {}", configFilePath);
            return true;
            
        } catch (Exception e) {
            logger.error("保存数据源配置失败", e);
            return false;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean loadDataSourceConfig() {
        try {
            File configFile = new File(configFilePath);
            if (!configFile.exists()) {
                logger.warn("数据源配置文件不存在: {}", configFilePath);
                return false;
            }
            
            // 读取配置文件
            Map<String, Object> config;
            try (FileReader reader = new FileReader(configFile)) {
                config = objectMapper.readValue(reader, Map.class);
            }
            
            // 获取默认数据源ID
            String defaultId = (String) config.get("default");
            
            // 获取数据源定义列表
            List<DataSourceDefinition> dataSources = objectMapper.convertValue(
                    config.get("dataSources"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, DataSourceDefinition.class)
            );
            
            // 先移除所有的数据源（除了默认数据源）
            List<DataSourceDefinition> existingDataSources = dataSourceRegistry.getAllDataSourceDefinitions();
            String currentDefaultId = dataSourceRegistry.getDefaultDataSourceId();
            
            for (DataSourceDefinition ds : existingDataSources) {
                if (!ds.getId().equals(currentDefaultId)) {
                    dataSourceRegistry.removeDataSource(ds.getId());
                }
            }
            
            // 注册新的数据源
            for (DataSourceDefinition ds : dataSources) {
                // 如果是默认数据源，先保存再注册其他数据源
                if (ds.getId().equals(defaultId)) {
                    continue;
                }
                dataSourceRegistry.registerDataSource(ds);
            }
            
            // 如果默认数据源发生变化，设置新的默认数据源
            if (defaultId != null && !defaultId.equals(currentDefaultId)) {
                dataSourceRegistry.setDefaultDataSourceId(defaultId);
            }
            
            logger.info("数据源配置已加载: {}", configFilePath);
            return true;
            
        } catch (Exception e) {
            logger.error("加载数据源配置失败", e);
            return false;
        }
    }
}
