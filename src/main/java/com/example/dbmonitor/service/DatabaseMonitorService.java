package com.example.dbmonitor.service;

import com.example.dbmonitor.config.DataSourceManager;
import com.example.dbmonitor.config.MonitorProperties;
import com.example.dbmonitor.entity.MonitorResult;
import com.example.dbmonitor.entity.ProcessInfo;
import com.example.dbmonitor.exception.DatabaseConnectionException;
import com.example.dbmonitor.exception.DbMonitorException;
import com.example.dbmonitor.exception.SqlExecutionException;
import com.example.dbmonitor.util.ProcessListMapper;
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

                List<ProcessInfo> allProcesses = jdbcTemplate.query(sql, new ProcessListMapper());

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
                FROM information_schema.SESSION_STATUS 
                WHERE VARIABLE_NAME IN ('Threads_connected', 'Max_used_connections', 'Threads_running')
                """;

        try {
            List<Map<String, Object>> connStats = jdbcTemplate.queryForList(sql);
            metrics.put("connections", connStats);
        } catch (DataAccessException e) {
            // MariaDB可能使用不同的表，尝试备选方案
            try {
                String altSql = "SHOW STATUS WHERE Variable_name IN ('Threads_connected', 'Max_used_connections', 'Threads_running')";
                List<Map<String, Object>> connStats = jdbcTemplate.queryForList(altSql);
                metrics.put("connections", connStats);
            } catch (DataAccessException ex) {
                throw new SqlExecutionException(
                        "Failed to collect connection statistics", dataSourceName, sql, e);
            }
        }
    }

    private void collectQueryStats(String dataSourceName, JdbcTemplate jdbcTemplate,
                                   Map<String, Object> metrics) throws SqlExecutionException {
        String sql = "SHOW STATUS WHERE Variable_name IN ('Questions', 'Slow_queries', 'Com_select', 'Com_insert', 'Com_update', 'Com_delete')";

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
        String sql = "SHOW STATUS LIKE 'Innodb_buffer_pool%'";

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
            String varName = (String) stat.get("Variable_name");
            if (varName == null) varName = (String) stat.get("VARIABLE_NAME");

            String varValue = (String) stat.get("Value");
            if (varValue == null) varValue = (String) stat.get("VARIABLE_VALUE");

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