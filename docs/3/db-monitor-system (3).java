// 1. pom.xml - 使用JDK 21
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>db-monitor</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>21</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>

// 2. application.yml - 灵活的多数据源配置
spring:
  application:
    name: db-monitor

# 监控配置
monitor:
  # 全局默认配置
  defaults:
    check-interval: 60  # 检测间隔（秒）
    sql-timeout-threshold: 30  # SQL超时阈值（秒）
    connection-timeout: 10000  # 连接超时（毫秒）- 增加到10秒
    pool-size: 5  # 连接池大小 - 增加到5
    # 新增连接池配置
    validation-timeout: 5000  # 连接验证超时（毫秒）
    keep-alive-time: 600000  # 连接保活时间（毫秒）- 10分钟
    query-timeout: 10  # 查询超时（秒）
  
  # 数据源列表 - 示例配置，注释掉不需要的数据源
  datasources:
    # 单个数据源示例
    - name: main-db
      url: jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC&connectTimeout=20000&socketTimeout=60000&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8
      username: root
      password: password
      enabled: true
      check-interval: 30
      sql-timeout-threshold: 30
      tags: [main]
    
    # 下面是多个数据源的示例，使用时取消注释
#    - name: production-db1
#      url: jdbc:mysql://prod1.example.com:3306/db1?useSSL=false&serverTimezone=UTC&connectTimeout=20000&socketTimeout=60000&autoReconnect=true
#      username: monitor_user
#      password: password
#      enabled: true
#      check-interval: 30
#      sql-timeout-threshold: 20
#      tags: [production, primary]
#    
#    - name: production-db2
#      url: jdbc:mysql://prod2.example.com:3306/db2?useSSL=false&serverTimezone=UTC&connectTimeout=20000&socketTimeout=60000&autoReconnect=true
#      username: monitor_user
#      password: password
#      enabled: true
#      tags: [production, replica]
#    
#    - name: analytics-db
#      url: jdbc:mysql://analytics.example.com:3306/analytics?useSSL=false&serverTimezone=UTC&connectTimeout=20000&socketTimeout=60000&autoReconnect=true
#      username: monitor_user
#      password: password
#      enabled: true
#      sql-timeout-threshold: 120  # 分析库允许更长时间
#      tags: [analytics]
#    
#    - name: user-service-db
#      url: jdbc:mysql://user.example.com:3306/users?useSSL=false&serverTimezone=UTC&connectTimeout=20000&socketTimeout=60000&autoReconnect=true
#      username: monitor_user
#      password: password
#      enabled: true
#      tags: [microservice, user]
#    
#    - name: order-service-db
#      url: jdbc:mysql://order.example.com:3306/orders?useSSL=false&serverTimezone=UTC&connectTimeout=20000&socketTimeout=60000&autoReconnect=true
#      username: monitor_user
#      password: password
#      enabled: true
#      tags: [microservice, order]
#    
#    - name: inventory-db
#      url: jdbc:mysql://inventory.example.com:3306/inventory?useSSL=false&serverTimezone=UTC&connectTimeout=20000&socketTimeout=60000&autoReconnect=true
#      username: monitor_user
#      password: password
#      enabled: true
#      tags: [microservice, inventory]
#    
#    - name: reporting-db
#      url: jdbc:mysql://reporting.example.com:3306/reports?useSSL=false&serverTimezone=UTC&connectTimeout=20000&socketTimeout=60000&autoReconnect=true
#      username: monitor_user
#      password: password
#      enabled: true
#      sql-timeout-threshold: 300  # 报表库允许5分钟
#      tags: [reporting]
  
  # 报警配置
  alert:
    enabled: true
    endpoints:
      - name: default
        url: http://localhost:8080/webhook  # 测试用本地地址
        type: webhook
#      - name: ops-team
#        url: http://ops-alert.example.com/critical
#        type: webhook
#        tags: [production]
    
    # 报警规则
    rules:
      - name: long-running-query
        condition: sql_time > 30
        severity: warning
        endpoint: default
      
      - name: critical-long-query
        condition: sql_time > 120
        severity: critical
        endpoint: default

// 3. 主应用类
package com.example.dbmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
public class DbMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(DbMonitorApplication.class, args);
    }
}

// 3.1 自定义异常类
package com.example.dbmonitor.exception;

// 基础异常类
public class DbMonitorException extends Exception {
    private final String dataSource;
    
    public DbMonitorException(String message, String dataSource) {
        super(message);
        this.dataSource = dataSource;
    }
    
    public DbMonitorException(String message, String dataSource, Throwable cause) {
        super(message, cause);
        this.dataSource = dataSource;
    }
    
    public String getDataSource() {
        return dataSource;
    }
}

// 数据库连接异常
package com.example.dbmonitor.exception;

public class DatabaseConnectionException extends DbMonitorException {
    public DatabaseConnectionException(String message, String dataSource) {
        super(message, dataSource);
    }
    
    public DatabaseConnectionException(String message, String dataSource, Throwable cause) {
        super(message, dataSource, cause);
    }
}

// SQL执行异常
package com.example.dbmonitor.exception;

public class SqlExecutionException extends DbMonitorException {
    private final String sql;
    
    public SqlExecutionException(String message, String dataSource, String sql) {
        super(message, dataSource);
        this.sql = sql;
    }
    
    public SqlExecutionException(String message, String dataSource, String sql, Throwable cause) {
        super(message, dataSource, cause);
        this.sql = sql;
    }
    
    public String getSql() {
        return sql;
    }
}

// 配置异常
package com.example.dbmonitor.exception;

public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String message) {
        super(message);
    }
    
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

// 报警发送异常
package com.example.dbmonitor.exception;

public class AlertSendException extends Exception {
    private final String endpoint;
    
    public AlertSendException(String message, String endpoint) {
        super(message);
        this.endpoint = endpoint;
    }
    
    public AlertSendException(String message, String endpoint, Throwable cause) {
        super(message, endpoint, cause);
        this.endpoint = endpoint;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
}

// 3.2 全局异常处理器
package com.example.dbmonitor.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DbMonitorException.class)
    public ResponseEntity<Map<String, Object>> handleDbMonitorException(
            DbMonitorException ex, WebRequest request) {
        log.error("Database monitoring error for datasource: {}", ex.getDataSource(), ex);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Database Monitoring Error");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("dataSource", ex.getDataSource());
        errorResponse.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(ConfigurationException.class)
    public ResponseEntity<Map<String, Object>> handleConfigurationException(
            ConfigurationException ex, WebRequest request) {
        log.error("Configuration error", ex);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Configuration Error");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        log.error("Invalid argument", ex);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Invalid Argument");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}

// 4. 监控配置类
package com.example.dbmonitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "monitor")
public class MonitorProperties {
    
    private DefaultConfig defaults = new DefaultConfig();
    private List<DataSourceConfig> datasources = new ArrayList<>();
    private AlertConfig alert = new AlertConfig();
    
    @Data
    public static class DefaultConfig {
        private int checkInterval = 60;
        private int sqlTimeoutThreshold = 30;
        private int connectionTimeout = 5000;
        private int poolSize = 3;
    }
    
    @Data
    public static class DataSourceConfig {
        private String name;
        private String url;
        private String username;
        private String password;
        private boolean enabled = true;
        private Integer checkInterval;
        private Integer sqlTimeoutThreshold;
        private Integer connectionTimeout;
        private Integer poolSize;
        private Set<String> tags;
        
        // 获取配置值，如果没有设置则使用默认值
        public int getEffectiveCheckInterval(DefaultConfig defaults) {
            return checkInterval != null ? checkInterval : defaults.getCheckInterval();
        }
        
        public int getEffectiveSqlTimeoutThreshold(DefaultConfig defaults) {
            return sqlTimeoutThreshold != null ? sqlTimeoutThreshold : defaults.getSqlTimeoutThreshold();
        }
        
        public int getEffectiveConnectionTimeout(DefaultConfig defaults) {
            return connectionTimeout != null ? connectionTimeout : defaults.getConnectionTimeout();
        }
        
        public int getEffectivePoolSize(DefaultConfig defaults) {
            return poolSize != null ? poolSize : defaults.getPoolSize();
        }
    }
    
    @Data
    public static class AlertConfig {
        private boolean enabled = true;
        private List<AlertEndpoint> endpoints = new ArrayList<>();
        private List<AlertRule> rules = new ArrayList<>();
    }
    
    @Data
    public static class AlertEndpoint {
        private String name;
        private String url;
        private String type;
        private Set<String> tags;
    }
    
    @Data
    public static class AlertRule {
        private String name;
        private String condition;
        private String severity;
        private String endpoint;
    }
}

// 5. 动态数据源管理器 - 增强连接池配置
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
            hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
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
            
            // 添加连接属性，确保连接稳定
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

// 6. 增强的ProcessInfo实体
package com.example.dbmonitor.entity;

import lombok.Data;

@Data
public class ProcessInfo {
    private Long id;
    private String user;
    private String host;
    private String db;
    private String command;
    private Long time;
    private String state;
    private String info;
    
    // 额外的诊断信息
    private Long rowsSent;
    private Long rowsExamined;
    
    public boolean isLongRunning(int threshold) {
        return time != null && time > threshold && 
               command != null && "Query".equals(command) &&
               info != null && !info.isEmpty();
    }
    
    public String getSqlPreview() {
        if (info == null) return "";
        // 返回SQL的前100个字符
        return info.length() > 100 ? info.substring(0, 100) + "..." : info;
    }
    
    public String getSeverity(int warningThreshold, int criticalThreshold) {
        if (time == null) return "normal";
        if (time >= criticalThreshold) return "critical";
        if (time >= warningThreshold) return "warning";
        return "normal";
    }
}

// 7. 报警消息实体
package com.example.dbmonitor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertMessage {
    private String alertType;
    private String dataSource;
    private String message;
    private LocalDateTime timestamp;
    private String severity;
    private List<ProcessInfo> longRunningQueries;
    private Map<String, Object> additionalInfo;
    
    // 数据源标签
    private List<String> tags;
    
    // 监控指标快照
    private AlertMetrics metrics;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertMetrics {
        private Integer currentConnections;
        private Integer maxConnections;
        private Integer slowQueries;
        private Integer lockWaits;
        private Double connectionUsagePercent;
        private Long totalQueries;
    }
}

// 8. 监控结果实体
package com.example.dbmonitor.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class MonitorResult {
    private String dataSourceName;
    private LocalDateTime timestamp;
    private boolean healthy;
    private List<ProcessInfo> longRunningQueries;
    private Map<String, Object> metrics;
    private List<String> issues;
    private long checkDuration; // 检查耗时（毫秒）
}

// 8. 增强的数据库监控服务
package com.example.dbmonitor.service;

import com.example.dbmonitor.config.DataSourceManager;
import com.example.dbmonitor.config.MonitorProperties;
import com.example.dbmonitor.entity.MonitorResult;
import com.example.dbmonitor.entity.ProcessInfo;
import com.example.dbmonitor.exception.DatabaseConnectionException;
import com.example.dbmonitor.exception.DbMonitorException;
import com.example.dbmonitor.exception.SqlExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseMonitorService {
    
    private final DataSourceManager dataSourceManager;
    private final MonitorProperties monitorProperties;
    private final AlertService alertService;
    
    // 使用虚拟线程执行器（JDK 21特性）
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    
    /**
     * 并行检查所有数据源
     */
    public List<MonitorResult> checkAllDataSources() {
        Map<String, JdbcTemplate> templates = dataSourceManager.getAllJdbcTemplates();
        
        List<CompletableFuture<MonitorResult>> futures = templates.entrySet().stream()
                .map(entry -> CompletableFuture.supplyAsync(
                        () -> {
                            try {
                                return checkDataSource(entry.getKey(), entry.getValue());
                            } catch (DbMonitorException e) {
                                log.error("Error checking datasource: {}", entry.getKey(), e);
                                return createErrorResult(entry.getKey(), e);
                            }
                        },
                        executor
                ))
                .toList();
        
        return futures.stream()
                .map(future -> {
                    try {
                        return future.join();
                    } catch (CompletionException e) {
                        log.error("Async execution error", e);
                        return createErrorResult("unknown", e.getCause());
                    }
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 检查单个数据源
     */
    public MonitorResult checkDataSource(String name, JdbcTemplate jdbcTemplate) throws DbMonitorException {
        long startTime = System.currentTimeMillis();
        List<String> issues = new ArrayList<>();
        List<ProcessInfo> longRunningQueries = new ArrayList<>();
        Map<String, Object> metrics = new HashMap<>();
        boolean healthy = true;
        
        try {
            // 1. 检查连接
            checkConnection(name, jdbcTemplate);
            
            // 2. 获取数据源配置
            var config = dataSourceManager.getDataSourceConfig(name);
            int threshold = config.getEffectiveSqlTimeoutThreshold(monitorProperties.getDefaults());
            
            // 3. 检查长时间运行的SQL（核心功能）
            longRunningQueries = checkLongRunningQueries(name, jdbcTemplate, threshold);
            if (!longRunningQueries.isEmpty()) {
                issues.add(String.format("发现 %d 个长时间运行的SQL", longRunningQueries.size()));
            }
            
            // 4. 收集性能指标
            metrics = collectPerformanceMetrics(name, jdbcTemplate);
            
            // 5. 分析指标
            analyzeMetrics(metrics, issues);
            
        } catch (DatabaseConnectionException | SqlExecutionException e) {
            healthy = false;
            issues.add("数据库异常: " + e.getMessage());
            throw e;
        }
        
        long checkDuration = System.currentTimeMillis() - startTime;
        
        MonitorResult result = MonitorResult.builder()
                .dataSourceName(name)
                .timestamp(LocalDateTime.now())
                .healthy(healthy && issues.isEmpty())
                .longRunningQueries(longRunningQueries)
                .metrics(metrics)
                .issues(issues)
                .checkDuration(checkDuration)
                .build();
        
        // 发送报警
        if (!result.isHealthy()) {
            try {
                alertService.sendAlert(result);
            } catch (Exception e) {
                log.error("Failed to send alert for datasource: {}", name, e);
                // 报警失败不影响监控结果
            }
        }
        
        return result;
    }
    
    private void checkConnection(String dataSourceName, JdbcTemplate jdbcTemplate) 
            throws DatabaseConnectionException {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DatabaseConnectionException(
                "Cannot get connection from pool", dataSourceName, e);
        } catch (QueryTimeoutException e) {
            throw new DatabaseConnectionException(
                "Connection test query timeout", dataSourceName, e);
        } catch (DataAccessException e) {
            throw new DatabaseConnectionException(
                "Failed to execute connection test", dataSourceName, e);
        }
    }
    
    /**
     * 检查长时间运行的SQL - 使用 SHOW FULL PROCESSLIST
     * 优化版本：处理大量进程和长SQL的情况
     */
    private List<ProcessInfo> checkLongRunningQueries(String dataSourceName, 
                                                      JdbcTemplate jdbcTemplate, 
                                                      int threshold) throws SqlExecutionException {
        String sql = "SHOW FULL PROCESSLIST";
        
        try {
            // 设置较长的查询超时，因为PROCESSLIST可能返回大量数据
            int originalTimeout = jdbcTemplate.getQueryTimeout();
            try {
                jdbcTemplate.setQueryTimeout(30); // 30秒超时
                
                List<ProcessInfo> allProcesses = jdbcTemplate.query(sql, new ProcessListRowMapper());
                
                // 筛选长时间运行的查询
                List<ProcessInfo> longRunning = allProcesses.stream()
                        .filter(p -> p.isLongRunning(threshold))
                        .sorted((a, b) -> Long.compare(b.getTime(), a.getTime())) // 按运行时间降序
                        .limit(100) // 限制最多返回100个长时间查询，避免内存问题
                        .collect(Collectors.toList());
                
                // 记录详细信息
                if (!longRunning.isEmpty()) {
                    log.warn("Found {} long running queries in {}, showing top {}", 
                        longRunning.size(), dataSourceName, Math.min(longRunning.size(), 10));
                    
                    // 只记录前10个查询的详细信息，避免日志过大
                    longRunning.stream()
                            .limit(10)
                            .forEach(process -> {
                                log.warn("Long running query - ID: {}, Time: {}s, User: {}, DB: {}, State: {}, SQL: {}", 
                                    process.getId(), 
                                    process.getTime(), 
                                    process.getUser(), 
                                    process.getDb(),
                                    process.getState(),
                                    process.getSqlPreview()
                                );
                            });
                }
                
                return longRunning;
                
            } finally {
                // 恢复原始超时设置
                jdbcTemplate.setQueryTimeout(originalTimeout);
            }
            
        } catch (DataAccessException e) {
            // 特殊处理：如果是权限问题，给出更清晰的错误信息
            if (e.getMessage() != null && e.getMessage().contains("command denied")) {
                throw new SqlExecutionException(
                    "Permission denied for SHOW PROCESSLIST. Please grant PROCESS privilege to user.", 
                    dataSourceName, sql, e);
            }
            throw new SqlExecutionException(
                "Failed to execute SHOW FULL PROCESSLIST", dataSourceName, sql, e);
        }
    }
    
    /**
     * ProcessList行映射器 - 增强版，处理可能的NULL值
     */
    private static class ProcessListRowMapper implements RowMapper<ProcessInfo> {
        @Override
        public ProcessInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProcessInfo process = new ProcessInfo();
            
            // 安全地获取值，处理可能的NULL
            process.setId(getLongValue(rs, "Id"));
            process.setUser(getStringValue(rs, "User"));
            process.setHost(getStringValue(rs, "Host"));
            process.setDb(getStringValue(rs, "db"));
            process.setCommand(getStringValue(rs, "Command"));
            process.setTime(getLongValue(rs, "Time"));
            process.setState(getStringValue(rs, "State"));
            process.setInfo(getStringValue(rs, "Info"));
            
            return process;
        }
        
        private Long getLongValue(ResultSet rs, String columnName) throws SQLException {
            long value = rs.getLong(columnName);
            return rs.wasNull() ? null : value;
        }
        
        private String getStringValue(ResultSet rs, String columnName) throws SQLException {
            String value = rs.getString(columnName);
            // 处理过长的SQL语句，避免内存问题
            if (value != null && value.length() > 1000) {
                return value.substring(0, 1000) + "...";
            }
            return value;
        }
    }
    
    /**
     * 收集性能指标
     */
    private Map<String, Object> collectPerformanceMetrics(String dataSourceName, 
                                                         JdbcTemplate jdbcTemplate) 
            throws SqlExecutionException {
        Map<String, Object> metrics = new HashMap<>();
        
        // 连接数统计
        collectConnectionStats(dataSourceName, jdbcTemplate, metrics);
        
        // 查询统计
        collectQueryStats(dataSourceName, jdbcTemplate, metrics);
        
        // InnoDB缓冲池状态
        collectInnoDbStats(dataSourceName, jdbcTemplate, metrics);
        
        // 表锁等待
        collectLockWaits(dataSourceName, jdbcTemplate, metrics);
        
        return metrics;
    }
    
    private void collectConnectionStats(String dataSourceName, JdbcTemplate jdbcTemplate, 
                                       Map<String, Object> metrics) throws SqlExecutionException {
        String sql = """
            SELECT 
                VARIABLE_NAME,
                VARIABLE_VALUE 
            FROM performance_schema.global_status 
            WHERE VARIABLE_NAME IN ('Threads_connected', 'Max_used_connections', 'Threads_running')
            """;
        
        try {
            List<Map<String, Object>> connStats = jdbcTemplate.queryForList(sql);
            metrics.put("connections", connStats);
        } catch (DataAccessException e) {
            throw new SqlExecutionException(
                "Failed to collect connection statistics", dataSourceName, sql, e);
        }
    }
    
    private void collectQueryStats(String dataSourceName, JdbcTemplate jdbcTemplate, 
                                  Map<String, Object> metrics) throws SqlExecutionException {
        String sql = """
            SELECT 
                VARIABLE_NAME,
                VARIABLE_VALUE 
            FROM performance_schema.global_status 
            WHERE VARIABLE_NAME IN ('Questions', 'Slow_queries', 'Com_select', 
                                   'Com_insert', 'Com_update', 'Com_delete')
            """;
        
        try {
            List<Map<String, Object>> queryStats = jdbcTemplate.queryForList(sql);
            metrics.put("queries", queryStats);
        } catch (DataAccessException e) {
            throw new SqlExecutionException(
                "Failed to collect query statistics", dataSourceName, sql, e);
        }
    }
    
    private void collectInnoDbStats(String dataSourceName, JdbcTemplate jdbcTemplate, 
                                   Map<String, Object> metrics) throws SqlExecutionException {
        String sql = """
            SELECT 
                VARIABLE_NAME,
                VARIABLE_VALUE 
            FROM performance_schema.global_status 
            WHERE VARIABLE_NAME LIKE 'Innodb_buffer_pool%'
            """;
        
        try {
            List<Map<String, Object>> innodbStats = jdbcTemplate.queryForList(sql);
            metrics.put("innodb", innodbStats);
        } catch (DataAccessException e) {
            // InnoDB统计失败不是致命错误，记录日志但不抛出异常
            log.warn("Failed to collect InnoDB statistics for {}: {}", dataSourceName, e.getMessage());
        }
    }
    
    private void collectLockWaits(String dataSourceName, JdbcTemplate jdbcTemplate, 
                                 Map<String, Object> metrics) throws SqlExecutionException {
        String sql = """
            SELECT COUNT(*) as lock_waits 
            FROM information_schema.innodb_lock_waits
            """;
        
        try {
            Integer lockWaits = jdbcTemplate.queryForObject(sql, Integer.class);
            metrics.put("lockWaits", lockWaits);
        } catch (DataAccessException e) {
            // 锁等待统计失败不是致命错误
            log.warn("Failed to collect lock wait statistics for {}: {}", dataSourceName, e.getMessage());
            metrics.put("lockWaits", -1); // 使用-1表示无法获取
        }
    }
    
    private void analyzeMetrics(Map<String, Object> metrics, List<String> issues) {
        // 分析连接数
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> connections = (List<Map<String, Object>>) metrics.get("connections");
        if (connections != null) {
            analyzeConnections(connections, issues);
        }
        
        // 分析锁等待
        Integer lockWaits = (Integer) metrics.get("lockWaits");
        if (lockWaits != null && lockWaits > 0) {
            issues.add(String.format("检测到 %d 个锁等待", lockWaits));
        }
    }
    
    private void analyzeConnections(List<Map<String, Object>> connections, List<String> issues) {
        Integer currentConnections = null;
        Integer maxConnections = null;
        
        for (Map<String, Object> stat : connections) {
            String varName = (String) stat.get("VARIABLE_NAME");
            String varValue = (String) stat.get("VARIABLE_VALUE");
            
            if ("Threads_connected".equals(varName)) {
                currentConnections = Integer.parseInt(varValue);
            } else if ("Max_used_connections".equals(varName)) {
                maxConnections = Integer.parseInt(varValue);
            }
        }
        
        // 如果连接数超过历史最大值的90%，发出警告
        if (currentConnections != null && maxConnections != null && 
            currentConnections > maxConnections * 0.9) {
            issues.add(String.format("连接数接近历史峰值: 当前 %d, 历史最大 %d", 
                currentConnections, maxConnections));
        }
    }
    
    private MonitorResult createErrorResult(String dataSourceName, Throwable error) {
        return MonitorResult.builder()
                .dataSourceName(dataSourceName)
                .timestamp(LocalDateTime.now())
                .healthy(false)
                .longRunningQueries(new ArrayList<>())
                .metrics(new HashMap<>())
                .issues(List.of("监控失败: " + error.getMessage()))
                .checkDuration(0)
                .build();
    }
}

// 9. 报警服务
package com.example.dbmonitor.service;

import com.example.dbmonitor.config.MonitorProperties;
import com.example.dbmonitor.entity.AlertMessage;
import com.example.dbmonitor.entity.MonitorResult;
import com.example.dbmonitor.exception.AlertSendException;
import com.example.dbmonitor.util.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.http.HttpTimeoutException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    
    private final MonitorProperties monitorProperties;
    private final HttpUtil httpUtil;
    
    public void sendAlert(MonitorResult result) throws AlertSendException {
        if (!monitorProperties.getAlert().isEnabled()) {
            return;
        }
        
        // 获取数据源配置
        var dsConfig = monitorProperties.getDatasources().stream()
                .filter(ds -> ds.getName().equals(result.getDataSourceName()))
                .findFirst()
                .orElse(null);
        
        // 构建报警消息
        AlertMessage message = AlertMessage.builder()
                .alertType(determineAlertType(result))
                .dataSource(result.getDataSourceName())
                .message(buildAlertMessage(result))
                .timestamp(LocalDateTime.now())
                .severity(determineSeverity(result))
                .longRunningQueries(result.getLongRunningQueries())
                .additionalInfo(result.getMetrics())
                .tags(dsConfig != null && dsConfig.getTags() != null ? 
                      new ArrayList<>(dsConfig.getTags()) : new ArrayList<>())
                .metrics(extractMetrics(result))
                .build();
        
        // 根据规则发送到对应的端点
        List<MonitorProperties.AlertEndpoint> endpoints = determineEndpoints(result, message.getSeverity());
        
        List<AlertSendException> failures = new ArrayList<>();
        
        for (MonitorProperties.AlertEndpoint endpoint : endpoints) {
            try {
                httpUtil.sendAlert(endpoint.getUrl(), message);
                log.info("Alert sent to endpoint: {} for datasource: {}", 
                    endpoint.getName(), result.getDataSourceName());
            } catch (HttpTimeoutException e) {
                String msg = String.format("Alert timeout for endpoint %s: %s", 
                    endpoint.getName(), e.getMessage());
                log.error(msg, e);
                failures.add(new AlertSendException(msg, endpoint.getName(), e));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复中断状态
                String msg = "Alert sending interrupted for endpoint: " + endpoint.getName();
                log.error(msg, e);
                failures.add(new AlertSendException(msg, endpoint.getName(), e));
            } catch (IllegalArgumentException e) {
                String msg = String.format("Invalid alert configuration for endpoint %s: %s", 
                    endpoint.getName(), e.getMessage());
                log.error(msg, e);
                failures.add(new AlertSendException(msg, endpoint.getName(), e));
            }
        }
        
        // 如果所有端点都失败，抛出异常
        if (!failures.isEmpty() && failures.size() == endpoints.size()) {
            throw failures.get(0); // 抛出第一个异常
        }
    }
    
    private String determineAlertType(MonitorResult result) {
        if (!result.isHealthy()) return "DATABASE_UNHEALTHY";
        if (!result.getLongRunningQueries().isEmpty()) return "LONG_RUNNING_QUERIES";
        if (!result.getIssues().isEmpty()) return "PERFORMANCE_ISSUES";
        return "GENERAL";
    }
    
    private String determineSeverity(MonitorResult result) {
        if (!result.isHealthy()) return "critical";
        
        // 检查长时间运行的SQL
        if (!result.getLongRunningQueries().isEmpty()) {
            boolean hasCritical = result.getLongRunningQueries().stream()
                    .anyMatch(q -> q.getTime() != null && q.getTime() > 120);
            return hasCritical ? "critical" : "warning";
        }
        
        return "info";
    }
    
    private AlertMessage.AlertMetrics extractMetrics(MonitorResult result) {
        AlertMessage.AlertMetrics.AlertMetricsBuilder builder = AlertMessage.AlertMetrics.builder();
        Map<String, Object> metrics = result.getMetrics();
        
        if (metrics != null) {
            // 从metrics中提取连接数等信息
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> connections = (List<Map<String, Object>>) metrics.get("connections");
            if (connections != null) {
                parseConnectionMetrics(connections, builder);
            }
            
            // 提取其他指标
            Integer lockWaits = (Integer) metrics.get("lockWaits");
            if (lockWaits != null) {
                builder.lockWaits(lockWaits);
            }
        }
        
        return builder.build();
    }
    
    private void parseConnectionMetrics(List<Map<String, Object>> connections, 
                                       AlertMessage.AlertMetrics.AlertMetricsBuilder builder) {
        for (Map<String, Object> conn : connections) {
            String varName = (String) conn.get("VARIABLE_NAME");
            String varValue = (String) conn.get("VARIABLE_VALUE");
            
            if (varName == null || varValue == null) continue;
            
            try {
                switch (varName) {
                    case "Threads_connected" -> builder.currentConnections(Integer.parseInt(varValue));
                    case "Max_used_connections" -> builder.maxConnections(Integer.parseInt(varValue));
                }
            } catch (NumberFormatException e) {
                log.warn("Failed to parse connection metric {}: {}", varName, varValue);
            }
        }
    }
    
    private String buildAlertMessage(MonitorResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("数据库: ").append(result.getDataSourceName()).append("\n");
        sb.append("检查耗时: ").append(result.getCheckDuration()).append("ms\n");
        
        if (!result.getLongRunningQueries().isEmpty()) {
            sb.append("长时间运行SQL: ").append(result.getLongRunningQueries().size()).append(" 个\n");
            
            // 添加TOP 3详细信息
            result.getLongRunningQueries().stream()
                    .limit(3)
                    .forEach(q -> {
                        sb.append(String.format("  - ID:%d, 时间:%ds, 严重程度:%s, SQL:%s\n", 
                            q.getId(), 
                            q.getTime(), 
                            q.getSeverity(30, 120),
                            q.getSqlPreview()));
                    });
        }
        
        if (!result.getIssues().isEmpty()) {
            sb.append("检测到的问题: \n");
            result.getIssues().forEach(issue -> sb.append("  - ").append(issue).append("\n"));
        }
        
        return sb.toString();
    }
    
    private List<MonitorProperties.AlertEndpoint> determineEndpoints(MonitorResult result, String severity) {
        return monitorProperties.getAlert().getEndpoints().stream()
                .filter(endpoint -> shouldSendToEndpoint(endpoint, result, severity))
                .collect(Collectors.toList());
    }
    
    private boolean shouldSendToEndpoint(MonitorProperties.AlertEndpoint endpoint, 
                                       MonitorResult result, String severity) {
        // 检查标签匹配
        if (endpoint.getTags() != null && !endpoint.getTags().isEmpty()) {
            var dsConfig = monitorProperties.getDatasources().stream()
                    .filter(ds -> ds.getName().equals(result.getDataSourceName()))
                    .findFirst()
                    .orElse(null);
            
            if (dsConfig == null || dsConfig.getTags() == null) {
                return false;
            }
            
            boolean tagMatch = endpoint.getTags().stream()
                    .anyMatch(tag -> dsConfig.getTags().contains(tag));
            
            if (!tagMatch) {
                return false;
            }
        }
        
        // 可以根据severity进一步过滤
        // 例如：某些端点只接收critical级别的报警
        
        return true;
    }
}

// 10. 调度器 - 支持不同检查间隔
package com.example.dbmonitor.scheduler;

import com.example.dbmonitor.config.DataSourceManager;
import com.example.dbmonitor.config.MonitorProperties;
import com.example.dbmonitor.service.DatabaseMonitorService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicMonitorScheduler {
    
    private final DatabaseMonitorService monitorService;
    private final DataSourceManager dataSourceManager;
    private final MonitorProperties monitorProperties;
    
    private TaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void initialize() {
        // 初始化调度器
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("db-monitor-");
        scheduler.initialize();
        this.taskScheduler = scheduler;
        
        // 为每个数据源创建独立的调度任务
        scheduleMonitorTasks();
    }
    
    private void scheduleMonitorTasks() {
        for (MonitorProperties.DataSourceConfig config : monitorProperties.getDatasources()) {
            if (config.isEnabled()) {
                scheduleDataSourceMonitor(config);
            }
        }
    }
    
    private void scheduleDataSourceMonitor(MonitorProperties.DataSourceConfig config) {
        String name = config.getName();
        int interval = config.getEffectiveCheckInterval(monitorProperties.getDefaults());
        
        ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(() -> {
            try {
                log.debug("Monitoring database: {}", name);
                JdbcTemplate template = dataSourceManager.getJdbcTemplate(name);
                if (template != null) {
                    monitorService.checkDataSource(name, template);
                }
            } catch (Exception e) {
                log.error("Error in scheduled monitoring for: {}", name, e);
            }
        }, Duration.ofSeconds(interval));
        
        scheduledTasks.put(name, future);
        log.info("Scheduled monitoring for {} with interval {}s", name, interval);
    }
    
    // 支持动态更新调度
    public void reschedule(String dataSourceName, int newInterval) {
        ScheduledFuture<?> existingTask = scheduledTasks.get(dataSourceName);
        if (existingTask != null) {
            existingTask.cancel(false);
        }
        
        // 重新调度
        var config = monitorProperties.getDatasources().stream()
                .filter(ds -> ds.getName().equals(dataSourceName))
                .findFirst()
                .orElse(null);
        
        if (config != null && config.isEnabled()) {
            config.setCheckInterval(newInterval);
            scheduleDataSourceMonitor(config);
        }
    }
}

// 11. REST控制器
package com.example.dbmonitor.controller;

import com.example.dbmonitor.config.DataSourceManager;
import com.example.dbmonitor.entity.MonitorResult;
import com.example.dbmonitor.exception.ConfigurationException;
import com.example.dbmonitor.exception.DbMonitorException;
import com.example.dbmonitor.scheduler.DynamicMonitorScheduler;
import com.example.dbmonitor.service.DatabaseMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {
    
    private final DatabaseMonitorService monitorService;
    private final DataSourceManager dataSourceManager;
    private final DynamicMonitorScheduler scheduler;
    
    /**
     * 检查所有数据源
     */
    @GetMapping("/check/all")
    public ResponseEntity<List<MonitorResult>> checkAll() {
        List<MonitorResult> results = monitorService.checkAllDataSources();
        return ResponseEntity.ok(results);
    }
    
    /**
     * 检查单个数据源
     */
    @GetMapping("/check/{dataSource}")
    public ResponseEntity<MonitorResult> checkDataSource(@PathVariable String dataSource) 
            throws DbMonitorException {
        JdbcTemplate template = dataSourceManager.getJdbcTemplate(dataSource);
        if (template == null) {
            throw new ConfigurationException("Data source not found: " + dataSource);
        }
        
        MonitorResult result = monitorService.checkDataSource(dataSource, template);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取监控状态汇总
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        List<MonitorResult> results = monitorService.checkAllDataSources();
        
        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", System.currentTimeMillis());
        status.put("totalDataSources", results.size());
        status.put("healthyDataSources", results.stream().filter(MonitorResult::isHealthy).count());
        status.put("unhealthyDataSources", results.stream().filter(r -> !r.isHealthy()).count());
        
        // 按健康状态分组
        Map<String, List<String>> dataSourcesByHealth = new HashMap<>();
        dataSourcesByHealth.put("healthy", 
            results.stream()
                .filter(MonitorResult::isHealthy)
                .map(MonitorResult::getDataSourceName)
                .toList());
        dataSourcesByHealth.put("unhealthy", 
            results.stream()
                .filter(r -> !r.isHealthy())
                .map(MonitorResult::getDataSourceName)
                .toList());
        
        status.put("dataSourcesByHealth", dataSourcesByHealth);
        
        // 汇总问题
        long totalLongRunningQueries = results.stream()
                .mapToLong(r -> r.getLongRunningQueries().size())
                .sum();
        status.put("totalLongRunningQueries", totalLongRunningQueries);
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * 获取详细的监控结果
     */
    @GetMapping("/status/detailed")
    public ResponseEntity<Map<String, Object>> getDetailedStatus() {
        Map<String, Object> status = new HashMap<>();
        List<MonitorResult> results = monitorService.checkAllDataSources();
        
        status.put("totalDataSources", results.size());
        status.put("healthyDataSources", results.stream().filter(MonitorResult::isHealthy).count());
        status.put("results", results);
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Database Monitor");
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }
    
    /**
     * 动态调整监控间隔
     */
    @PutMapping("/schedule/{dataSource}")
    public ResponseEntity<Map<String, String>> updateSchedule(
            @PathVariable String dataSource,
            @RequestParam int interval) {
        
        if (interval < 10) {
            throw new IllegalArgumentException("Check interval must be at least 10 seconds");
        }
        
        scheduler.reschedule(dataSource, interval);
        
        Map<String, String> response = new HashMap<>();
        response.put("dataSource", dataSource);
        response.put("newInterval", String.valueOf(interval));
        response.put("status", "updated");
        
        return ResponseEntity.ok(response);
    }
}

// 12. HTTP工具类 - 使用JDK 21特性
package com.example.dbmonitor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class HttpUtil {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public HttpUtil() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }
    
    public void sendAlert(String url, Object data) {
        sendAlertAsync(url, data).exceptionally(throwable -> {
            log.error("Failed to send alert", throwable);
            return null;
        });
    }
    
    public CompletableFuture<Void> sendAlertAsync(String url, Object data) {
        try {
            String jsonBody = objectMapper.writeValueAsString(data);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            log.debug("Alert sent successfully: {}", response.statusCode());
                        } else {
                            log.error("Failed to send alert. Status: {}, Body: {}", 
                                response.statusCode(), response.body());
                        }
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}