package com.platform.scheduler.config.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zaxxer.hikari.HikariDataSource;

/**
 * 动态数据源配置
 * 
 * @author platform
 */
@Configuration
public class DynamicDataSourceConfig {
    
    /**
     * 主数据源属性配置
     */
    @Primary
    @Bean(name = "masterDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSourceProperties masterDataSourceProperties() {
        return new DataSourceProperties();
    }
    
    /**
     * 主数据源
     */
    @Primary
    @Bean(name = "masterDataSource")
    public DataSource masterDataSource(@Qualifier("masterDataSourceProperties") DataSourceProperties properties) {
        return createHikariDataSource(properties);
    }
    
    /**
     * 从数据源0属性配置
     */
    @Bean(name = "slave0DataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.slave0")
    public DataSourceProperties slave0DataSourceProperties() {
        return new DataSourceProperties();
    }
    
    /**
     * 从数据源0
     */
    @Bean(name = "slave0DataSource")
    public DataSource slave0DataSource(@Qualifier("slave0DataSourceProperties") DataSourceProperties properties) {
        return createHikariDataSource(properties);
    }
    
    /**
     * 日志数据源0属性配置
     */
    @Bean(name = "log0DataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.log0")
    public DataSourceProperties log0DataSourceProperties() {
        return new DataSourceProperties();
    }
    
    /**
     * 日志数据源0
     */
    @Bean(name = "log0DataSource")
    public DataSource log0DataSource(@Qualifier("log0DataSourceProperties") DataSourceProperties properties) {
        return createHikariDataSource(properties);
    }
    
    /**
     * 日志数据源1属性配置
     */
    @Bean(name = "log1DataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.log1")
    public DataSourceProperties log1DataSourceProperties() {
        return new DataSourceProperties();
    }
    
    /**
     * 日志数据源1
     */
    @Bean(name = "log1DataSource")
    public DataSource log1DataSource(@Qualifier("log1DataSourceProperties") DataSourceProperties properties) {
        return createHikariDataSource(properties);
    }
    
    /**
     * 动态数据源
     */
    @Primary
    @Bean(name = "dynamicDataSource")
    public DynamicDataSource dynamicDataSource(
            @Qualifier("masterDataSource") DataSource masterDataSource,
            @Qualifier("slave0DataSource") DataSource slave0DataSource,
            @Qualifier("log0DataSource") DataSource log0DataSource,
            @Qualifier("log1DataSource") DataSource log1DataSource) {
        
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        
        // 设置默认数据源
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource);
        
        // 配置数据源映射
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", masterDataSource);
        targetDataSources.put("slave0", slave0DataSource);
        targetDataSources.put("log0", log0DataSource);
        targetDataSources.put("log1", log1DataSource);
        
        dynamicDataSource.setTargetDataSources(targetDataSources);
        
        return dynamicDataSource;
    }
    
    /**
     * JdbcTemplate配置，使用动态数据源
     */
    @Primary
    @Bean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("dynamicDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    
    /**
     * NamedParameterJdbcTemplate配置，使用动态数据源
     */
    @Primary
    @Bean(name = "namedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(@Qualifier("dynamicDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
    
    /**
     * 创建Hikari数据源
     */
    private HikariDataSource createHikariDataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
        dataSource.setPoolName(properties.getName());
        return dataSource;
    }
}
