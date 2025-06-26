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
    connection-timeout: 5000  # 连接超时（毫秒）
    pool-size: 3  # 连接池大小
  
  # 数据源列表
  datasources:
    - name: production-db1
      url: jdbc:mysql://prod1.example.com:3306/db1?useSSL=false&serverTimezone=UTC
      username: monitor_user
      password: password
      enabled: true
      check-interval: 30  # 覆盖默认值
      sql-timeout-threshold: 20
      tags: [production, primary]
    
    - name: production-db2
      url: jdbc:mysql://prod2.example.com:3306/db2?useSSL=false&serverTimezone=UTC
      username: monitor_user
      password: password
      enabled: true
      tags: [production, replica]
    
    - name: analytics-db
      url: jdbc:mysql://analytics.example.com:3306/analytics?useSSL=false&serverTimezone=UTC
      username: monitor_user
      password: password
      enabled: true
      sql-timeout-threshold: 120  # 分析库允许更长时间
      tags: [analytics]
    
    - name: user-service-db
      url: jdbc:mysql://user.example.com:3306/users?useSSL=false&serverTimezone=UTC
      username: monitor_user
      password: password
      enabled: true
      tags: [microservice, user]
    
    - name: order-service-db
      url: jdbc:mysql://order.example.com:3306/orders?useSSL=false&serverTimezone=UTC
      username: monitor_user
      password: password
      enabled: true
      tags: [microservice, order]
    
    - name: inventory-db
      url: jdbc:mysql://inventory.example.com:3306/inventory?useSSL=false&serverTimezone=UTC
      username: monitor_user
      password: password
      enabled: true
      tags: [microservice, inventory]
    
    - name: reporting-db
      url: jdbc:mysql://reporting.example.com:3306/reports?useSSL=false&serverTimezone=UTC
      username: monitor_user
      password: password
      enabled: true
      sql-timeout-threshold: 300  # 报表库允许5分钟
      tags: [reporting]
    
    - name: test-db
      url: jdbc:mysql://test.example.com:3306/test?useSSL=false&serverTimezone=UTC
      username: monitor_user
      password: password
      enabled: false  # 可以禁用某些数据源
      tags: [test]
  
  # 报警配置
  alert:
    enabled: true
    endpoints:
      - name: default
        url: http://alert-system.example.com/webhook
        type: webhook
      - name: ops-team
        url: http://ops-alert.example.com/critical
        type: webhook
        # 只接收特定标签的报警
        tags: [production]
    
    # 报警规则
    rules:
      - name: long-running-query
        condition: sql_time > 30
        severity: warning
        endpoint: default
      
      - name: critical-long-query
        condition: sql_time > 120
        severity: critical
        endpoint: ops-team
      
      - name: too-many-connections
        condition: connection_usage > 80
        severity: warning
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

// 5. 动态数据源管理器
package com.example.dbmonitor.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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
                } catch (Exception e) {
                    log.error("Failed to initialize data source: {}", config.getName(), e);
                }
            }
        }
    }
    
    private void createDataSource(MonitorProperties.DataSourceConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getUrl());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // 设置连接池参数
        hikariConfig.setMaximumPoolSize(config.getEffectivePoolSize(monitorProperties.getDefaults()));
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setConnectionTimeout(config.getEffectiveConnectionTimeout(monitorProperties.getDefaults()));
        hikariConfig.setIdleTimeout(300000);
        hikariConfig.setMaxLifetime(600000);
        hikariConfig.setPoolName("Monitor-" + config.getName());
        
        // 连接测试查询
        hikariConfig.setConnectionTestQuery("SELECT 1");
        
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        dataSources.put(config.getName(), dataSource);
        jdbcTemplates.put(config.getName(), new JdbcTemplate(dataSource));
    }
    
    public JdbcTemplate getJdbcTemplate(String name) {
        return jdbcTemplates.get(name);
    }
    
    public Map<String, JdbcTemplate> getAllJdbcTemplates() {
        return new ConcurrentHashMap<>(jdbcTemplates);
    }
    
    public MonitorProperties.DataSourceConfig getDataSourceConfig(String name) {
        return monitorProperties.getDatasources().stream()
                .filter(ds -> ds.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down data sources");
        dataSources.forEach((name, ds) -> {
            try {
                ds.close();
                log.info("Closed data source: {}", name);
            } catch (Exception e) {
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

// 7. 监控结果实体
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
                        () -> checkDataSource(entry.getKey(), entry.getValue()),
                        executor
                ))
                .toList();
        
        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
    
    /**
     * 检查单个数据源
     */
    public MonitorResult checkDataSource(String name, JdbcTemplate jdbcTemplate) {
        long startTime = System.currentTimeMillis();
        List<String> issues = new ArrayList<>();
        List<ProcessInfo> longRunningQueries = new ArrayList<>();
        Map<String, Object> metrics = new HashMap<>();
        boolean healthy = true;
        
        try {
            // 1. 检查连接
            checkConnection(jdbcTemplate);
            
            // 2. 获取数据源配置
            var config = dataSourceManager.getDataSourceConfig(name);
            int threshold = config.getEffectiveSqlTimeoutThreshold(monitorProperties.getDefaults());
            
            // 3. 检查长时间运行的SQL（核心功能）
            longRunningQueries = checkLongRunningQueries(jdbcTemplate, threshold);
            if (!longRunningQueries.isEmpty()) {
                issues.add(String.format("发现 %d 个长时间运行的SQL", longRunningQueries.size()));
            }
            
            // 4. 收集性能指标
            metrics = collectPerformanceMetrics(jdbcTemplate);
            
            // 5. 分析指标
            analyzeMetrics(metrics, issues);
            
        } catch (Exception e) {
            healthy = false;
            issues.add("数据库连接异常: " + e.getMessage());
            log.error("Error monitoring database: {}", name, e);
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
            alertService.sendAlert(result);
        }
        
        return result;
    }
    
    private void checkConnection(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.queryForObject("SELECT 1", Integer.class);
    }
    
    /**
     * 检查长时间运行的SQL - 使用 SHOW FULL PROCESSLIST
     */
    private List<ProcessInfo> checkLongRunningQueries(JdbcTemplate jdbcTemplate, int threshold) {
        String sql = "SHOW FULL PROCESSLIST";
        
        List<ProcessInfo> allProcesses = jdbcTemplate.query(sql, (rs, rowNum) -> {
            ProcessInfo process = new ProcessInfo();
            process.setId(rs.getLong("Id"));
            process.setUser(rs.getString("User"));
            process.setHost(rs.getString("Host"));
            process.setDb(rs.getString("db"));
            process.setCommand(rs.getString("Command"));
            process.setTime(rs.getLong("Time"));
            process.setState(rs.getString("State"));
            process.setInfo(rs.getString("Info"));
            return process;
        });
        
        // 筛选长时间运行的查询
        List<ProcessInfo> longRunning = allProcesses.stream()
                .filter(p -> p.isLongRunning(threshold))
                .sorted((a, b) -> Long.compare(b.getTime(), a.getTime())) // 按运行时间降序
                .collect(Collectors.toList());
        
        // 记录详细信息
        for (ProcessInfo process : longRunning) {
            log.warn("Long running query detected - ID: {}, Time: {}s, User: {}, DB: {}, SQL: {}", 
                process.getId(), 
                process.getTime(), 
                process.getUser(), 
                process.getDb(), 
                process.getSqlPreview()
            );
        }
        
        return longRunning;
    }
    
    /**
     * 收集性能指标
     */
    private Map<String, Object> collectPerformanceMetrics(JdbcTemplate jdbcTemplate) {
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            // 连接数统计
            String connSql = """
                SELECT 
                    VARIABLE_VALUE 
                FROM performance_schema.global_status 
                WHERE VARIABLE_NAME IN ('Threads_connected', 'Max_used_connections', 'Threads_running')
                """;
            
            List<Map<String, Object>> connStats = jdbcTemplate.queryForList(connSql);
            metrics.put("connections", connStats);
            
            // 查询统计
            String querySql = """
                SELECT 
                    VARIABLE_NAME,
                    VARIABLE_VALUE 
                FROM performance_schema.global_status 
                WHERE VARIABLE_NAME IN ('Questions', 'Slow_queries', 'Com_select', 'Com_insert', 'Com_update', 'Com_delete')
                """;
            
            List<Map<String, Object>> queryStats = jdbcTemplate.queryForList(querySql);
            metrics.put("queries", queryStats);
            
            // InnoDB缓冲池状态
            String innodbSql = """
                SELECT 
                    VARIABLE_NAME,
                    VARIABLE_VALUE 
                FROM performance_schema.global_status 
                WHERE VARIABLE_NAME LIKE 'Innodb_buffer_pool%'
                """;
            
            List<Map<String, Object>> innodbStats = jdbcTemplate.queryForList(innodbSql);
            metrics.put("innodb", innodbStats);
            
            // 表锁等待
            String lockSql = """
                SELECT COUNT(*) as lock_waits 
                FROM information_schema.innodb_lock_waits
                """;
            
            Integer lockWaits = jdbcTemplate.queryForObject(lockSql, Integer.class);
            metrics.put("lockWaits", lockWaits);
            
        } catch (Exception e) {
            log.error("Error collecting metrics", e);
        }
        
        return metrics;
    }
    
    private void analyzeMetrics(Map<String, Object> metrics, List<String> issues) {
        // 分析连接数
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> connections = (List<Map<String, Object>>) metrics.get("connections");
        if (connections != null) {
            // 这里可以添加更多的分析逻辑
        }
        
        // 分析锁等待
        Integer lockWaits = (Integer) metrics.get("lockWaits");
        if (lockWaits != null && lockWaits > 0) {
            issues.add(String.format("检测到 %d 个锁等待", lockWaits));
        }
    }
}

// 9. 报警服务
package com.example.dbmonitor.service;

import com.example.dbmonitor.config.MonitorProperties;
import com.example.dbmonitor.entity.AlertMessage;
import com.example.dbmonitor.entity.MonitorResult;
import com.example.dbmonitor.util.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    
    private final MonitorProperties monitorProperties;
    private final HttpUtil httpUtil;
    
    public void sendAlert(MonitorResult result) {
        if (!monitorProperties.getAlert().isEnabled()) {
            return;
        }
        
        // 构建报警消息
        AlertMessage message = AlertMessage.builder()
                .alertType(determineAlertType(result))
                .dataSource(result.getDataSourceName())
                .message(buildAlertMessage(result))
                .timestamp(LocalDateTime.now())
                .longRunningQueries(result.getLongRunningQueries())
                .additionalInfo(result.getMetrics())
                .build();
        
        // 根据规则发送到对应的端点
        List<MonitorProperties.AlertEndpoint> endpoints = determineEndpoints(result);
        
        for (MonitorProperties.AlertEndpoint endpoint : endpoints) {
            try {
                httpUtil.sendAlert(endpoint.getUrl(), message);
                log.info("Alert sent to endpoint: {}", endpoint.getName());
            } catch (Exception e) {
                log.error("Failed to send alert to endpoint: {}", endpoint.getName(), e);
            }
        }
    }
    
    private String determineAlertType(MonitorResult result) {
        if (!result.isHealthy()) return "DATABASE_UNHEALTHY";
        if (!result.getLongRunningQueries().isEmpty()) return "LONG_RUNNING_QUERIES";
        if (!result.getIssues().isEmpty()) return "PERFORMANCE_ISSUES";
        return "GENERAL";
    }
    
    private String buildAlertMessage(MonitorResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("数据库: ").append(result.getDataSourceName()).append("\n");
        
        if (!result.getLongRunningQueries().isEmpty()) {
            sb.append("长时间运行SQL: ").append(result.getLongRunningQueries().size()).append(" 个\n");
            
            // 添加TOP 3详细信息
            result.getLongRunningQueries().stream()
                    .limit(3)
                    .forEach(q -> {
                        sb.append(String.format("  - ID:%d, 时间:%ds, SQL:%s\n", 
                            q.getId(), q.getTime(), q.getSqlPreview()));
                    });
        }
        
        if (!result.getIssues().isEmpty()) {
            sb.append("问题: \n");
            result.getIssues().forEach(issue -> sb.append("  - ").append(issue).append("\n"));
        }
        
        return sb.toString();
    }
    
    private List<MonitorProperties.AlertEndpoint> determineEndpoints(MonitorResult result) {
        // 这里可以根据规则和标签来决定发送到哪些端点
        return monitorProperties.getAlert().getEndpoints().stream()
                .filter(endpoint -> shouldSendToEndpoint(endpoint, result))
                .collect(Collectors.toList());
    }
    
    private boolean shouldSendToEndpoint(MonitorProperties.AlertEndpoint endpoint, MonitorResult result) {
        // 简单实现：如果端点没有标签限制，或者数据源的标签匹配，则发送
        if (endpoint.getTags() == null || endpoint.getTags().isEmpty()) {
            return true;
        }
        
        // 获取数据源配置并检查标签
        var dsConfig = monitorProperties.getDatasources().stream()
                .filter(ds -> ds.getName().equals(result.getDataSourceName()))
                .findFirst()
                .orElse(null);
        
        if (dsConfig != null && dsConfig.getTags() != null) {
            return endpoint.getTags().stream()
                    .anyMatch(tag -> dsConfig.getTags().contains(tag));
        }
        
        return false;
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

import com.example.dbmonitor.entity.MonitorResult;
import com.example.dbmonitor.service.DatabaseMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {
    
    private final DatabaseMonitorService monitorService;
    
    @GetMapping("/check/all")
    public List<MonitorResult> checkAll() {
        return monitorService.checkAllDataSources();
    }
    
    @GetMapping("/check/{dataSource}")
    public MonitorResult checkDataSource(@PathVariable String dataSource) {
        // 实现单个数据源检查
        return null; // 需要实现
    }
    
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        List<MonitorResult> results = monitorService.checkAllDataSources();
        
        status.put("totalDataSources", results.size());
        status.put("healthyDataSources", results.stream().filter(MonitorResult::isHealthy).count());
        status.put("results", results);
        
        return status;
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