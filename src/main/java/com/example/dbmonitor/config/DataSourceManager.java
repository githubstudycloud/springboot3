package com.example.dbmonitor.config;

import com.example.dbmonitor.exception.ConfigurationException;
import com.example.dbmonitor.exception.DatabaseConnectionException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSourceManager {
    
    private final MonitorProperties monitorProperties;
    private final Map<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();
    private final Map<String, JdbcTemplate> jdbcTemplates = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void initialize() {
        log.info("Initializing {} data sources", monitorProperties.getDatasources().size());
        
        for (MonitorProperties.DataSourceConfig config : monitorProperties.getDatasources()) {
            if (config.isEnabled()) {
                try {
                    createDataSource(config);
                    log.info("Successfully initialized data source: {}", config.getName());
                } catch (DatabaseConnectionException e) {
                    log.error("Failed to initialize data source: {}", config.getName(), e);
                    // 继续初始化其他数据源
                } catch (IllegalArgumentException e) {
                    log.error("Invalid configuration for data source: {}", config.getName(), e);
                }
            }
        }
        
        if (dataSources.isEmpty()) {
            log.warn("No data sources were successfully initialized!");
        }
    }
    
    private void createDataSource(MonitorProperties.DataSourceConfig config) throws DatabaseConnectionException {
        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getUrl());
            hikariConfig.setUsername(config.getUsername());
            hikariConfig.setPassword(config.getPassword());
            
            // 使用MariaDB驱动
            hikariConfig.setDriverClassName(config.getDriverClassName() != null ? 
                config.getDriverClassName() : "org.mariadb.jdbc.Driver");
            
            // 基本连接池参数
            hikariConfig.setMaximumPoolSize(config.getEffectivePoolSize(monitorProperties.getDefaults()));
            hikariConfig.setMinimumIdle(2); // 保持最少2个空闲连接
            hikariConfig.setConnectionTimeout(config.getEffectiveConnectionTimeout(monitorProperties.getDefaults()));
            
            // 重要：设置连接生命周期和空闲超时
            hikariConfig.setIdleTimeout(600000); // 10分钟
            hikariConfig.setMaxLifetime(1800000); // 30分钟
            hikariConfig.setKeepaliveTime(300000); // 5分钟 - 防止连接被MySQL服务器关闭
            
            // 连接验证
            hikariConfig.setConnectionTestQuery("SELECT 1");
            hikariConfig.setValidationTimeout(5000); // 5秒验证超时
            
            // 泄漏检测
            hikariConfig.setLeakDetectionThreshold(60000); // 1分钟
            
            // 连接池名称
            hikariConfig.setPoolName("Monitor-" + config.getName());
            
            // MariaDB特定的连接属性
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
            hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
            hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
            hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
            hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
            hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
            hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
            
            // 创建数据源
            HikariDataSource dataSource = new HikariDataSource(hikariConfig);
            
            // 测试连接
            try (var connection = dataSource.getConnection()) {
                log.debug("Connection test successful for: {}", config.getName());
            }
            
            // 创建JdbcTemplate并设置查询超时
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.setQueryTimeout(10); // 设置默认查询超时为10秒
            
            dataSources.put(config.getName(), dataSource);
            jdbcTemplates.put(config.getName(), jdbcTemplate);
            
        } catch (HikariPool.PoolInitializationException e) {
            throw new DatabaseConnectionException(
                "Failed to create connection pool for: " + config.getName(), 
                config.getName(), e);
        } catch (SQLException e) {
            throw new DatabaseConnectionException(
                "Failed to test connection for: " + config.getName(), 
                config.getName(), e);
        }
    }
    
    public JdbcTemplate getJdbcTemplate(String name) {
        JdbcTemplate template = jdbcTemplates.get(name);
        if (template == null) {
            throw new ConfigurationException("No JdbcTemplate found for datasource: " + name);
        }
        return template;
    }
    
    public Map<String, JdbcTemplate> getAllJdbcTemplates() {
        return new ConcurrentHashMap<>(jdbcTemplates);
    }
    
    public MonitorProperties.DataSourceConfig getDataSourceConfig(String name) {
        return monitorProperties.getDatasources().stream()
                .filter(ds -> ds.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new ConfigurationException("No configuration found for datasource: " + name));
    }
    
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down data sources");
        dataSources.forEach((name, ds) -> {
            try {
                ds.close();
                log.info("Closed data source: {}", name);
            } catch (RuntimeException e) {
                log.error("Error closing data source: {}", name, e);
            }
        });
    }
}