package com.example.dbmonitor.scheduler;

import com.example.dbmonitor.config.DataSourceManager;
import com.example.dbmonitor.config.MonitorProperties;
import com.example.dbmonitor.exception.ConfigurationException;
import com.example.dbmonitor.exception.DbMonitorException;
import com.example.dbmonitor.service.DatabaseMonitorService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
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
        scheduler.setRejectedExecutionHandler((r, executor) -> {
            log.error("Task rejected by scheduler. Pool may be full.");
        });
        
        try {
            scheduler.initialize();
            this.taskScheduler = scheduler;
            
            // 为每个数据源创建独立的调度任务
            scheduleMonitorTasks();
        } catch (IllegalStateException e) {
            log.error("Failed to initialize task scheduler", e);
            throw new ConfigurationException("Cannot initialize monitor scheduler", e);
        }
    }
    
    private void scheduleMonitorTasks() {
        for (MonitorProperties.DataSourceConfig config : monitorProperties.getDatasources()) {
            if (config.isEnabled()) {
                try {
                    scheduleDataSourceMonitor(config);
                } catch (RejectedExecutionException e) {
                    log.error("Failed to schedule monitoring for: {}", config.getName(), e);
                } catch (IllegalArgumentException e) {
                    log.error("Invalid scheduling configuration for: {}", config.getName(), e);
                }
            }
        }
    }
    
    private void scheduleDataSourceMonitor(MonitorProperties.DataSourceConfig config) {
        String name = config.getName();
        int interval = config.getEffectiveCheckInterval(monitorProperties.getDefaults());
        
        if (interval <= 0) {
            throw new IllegalArgumentException("Check interval must be positive for: " + name);
        }
        
        ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(() -> {
            try {
                log.debug("Starting scheduled monitoring for: {}", name);
                JdbcTemplate template = dataSourceManager.getJdbcTemplate(name);
                monitorService.checkDataSource(name, template);
            } catch (DbMonitorException e) {
                log.error("Database monitoring error for: {}", name, e);
            } catch (ConfigurationException e) {
                log.error("Configuration error for datasource: {}", name, e);
                // 配置错误可能需要停止该数据源的监控
                cancelScheduledTask(name);
            } catch (RuntimeException e) {
                log.error("Unexpected error monitoring: {}", name, e);
            }
        }, Duration.ofSeconds(interval));
        
        scheduledTasks.put(name, future);
        log.info("Scheduled monitoring for {} with interval {}s", name, interval);
    }
    
    // 支持动态更新调度
    public void reschedule(String dataSourceName, int newInterval) {
        if (newInterval <= 0) {
            throw new IllegalArgumentException("Check interval must be positive");
        }
        
        // 取消现有任务
        cancelScheduledTask(dataSourceName);
        
        // 重新调度
        var config = monitorProperties.getDatasources().stream()
                .filter(ds -> ds.getName().equals(dataSourceName))
                .findFirst()
                .orElseThrow(() -> new ConfigurationException(
                    "No configuration found for datasource: " + dataSourceName));
        
        if (config.isEnabled()) {
            config.setCheckInterval(newInterval);
            try {
                scheduleDataSourceMonitor(config);
            } catch (RejectedExecutionException e) {
                log.error("Failed to reschedule monitoring for: {}", dataSourceName, e);
                throw new ConfigurationException("Cannot reschedule monitoring task", e);
            }
        }
    }
    
    private void cancelScheduledTask(String dataSourceName) {
        ScheduledFuture<?> existingTask = scheduledTasks.remove(dataSourceName);
        if (existingTask != null) {
            boolean cancelled = existingTask.cancel(false);
            log.info("Cancelled scheduled task for {}: {}", dataSourceName, cancelled);
        }
    }
    
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down monitor scheduler");
        
        // 取消所有任务
        scheduledTasks.forEach((name, future) -> {
            boolean cancelled = future.cancel(false);
            log.debug("Cancelled task for {}: {}", name, cancelled);
        });
        
        // 关闭调度器
        if (taskScheduler instanceof ThreadPoolTaskScheduler) {
            ((ThreadPoolTaskScheduler) taskScheduler).shutdown();
        }
    }
}