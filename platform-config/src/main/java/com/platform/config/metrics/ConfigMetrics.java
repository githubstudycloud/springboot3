package com.platform.config.metrics;

import com.platform.config.event.ConfigChangeEvent;
import com.platform.config.service.ConfigCacheService;
import io.micrometer.core.instrument.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 配置服务监控指标
 * 集成Micrometer，提供丰富的监控指标
 *
 * @author Platform Team
 * @since 2.0.0
 */
@Component
@Slf4j
public class ConfigMetrics {

    private final MeterRegistry meterRegistry;
    private final ConfigCacheService cacheService;

    // 配置请求计数器
    private final Counter configRequestCounter;
    private final Counter configRefreshCounter;
    private final Counter configSyncCounter;
    private final Counter configBackupCounter;
    
    // 配置操作计时器
    private final Timer configLoadTimer;
    private final Timer configSyncTimer;
    private final Timer configBackupTimer;
    
    // 配置状态计量器
    private final AtomicLong gitlabAvailableGauge = new AtomicLong(0);
    private final AtomicLong configSourceGauge = new AtomicLong(0); // 0=native, 1=gitlab
    
    // 错误计数器
    private final Counter configErrorCounter;
    private final Counter gitSyncErrorCounter;
    private final Counter backupErrorCounter;

    public ConfigMetrics(MeterRegistry meterRegistry, ConfigCacheService cacheService) {
        this.meterRegistry = meterRegistry;
        this.cacheService = cacheService;

        // 初始化计数器
        this.configRequestCounter = Counter.builder("config.requests.total")
            .description("配置请求总数")
            .tag("type", "request")
            .register(meterRegistry);

        this.configRefreshCounter = Counter.builder("config.refresh.total")
            .description("配置刷新总数")
            .register(meterRegistry);

        this.configSyncCounter = Counter.builder("config.sync.total")
            .description("配置同步总数")
            .register(meterRegistry);

        this.configBackupCounter = Counter.builder("config.backup.total")
            .description("配置备份总数")
            .register(meterRegistry);

        // 初始化计时器
        this.configLoadTimer = Timer.builder("config.load.duration")
            .description("配置加载耗时")
            .register(meterRegistry);

        this.configSyncTimer = Timer.builder("config.sync.duration")
            .description("配置同步耗时")
            .register(meterRegistry);

        this.configBackupTimer = Timer.builder("config.backup.duration")
            .description("配置备份耗时")
            .register(meterRegistry);

        // 初始化状态计量器
        Gauge.builder("config.gitlab.available")
            .description("GitLab可用状态 (0=不可用, 1=可用)")
            .register(meterRegistry, gitlabAvailableGauge, AtomicLong::get);

        Gauge.builder("config.source.type")
            .description("当前配置源类型 (0=native, 1=gitlab)")
            .register(meterRegistry, configSourceGauge, AtomicLong::get);

        // 初始化缓存相关指标
        Gauge.builder("config.cache.size")
            .description("配置缓存大小")
            .register(meterRegistry, cacheService, service -> service.getCacheStats().estimatedSize());

        Gauge.builder("config.cache.hit.rate")
            .description("配置缓存命中率")
            .register(meterRegistry, cacheService, service -> service.getCacheStats().hitRate());

        Gauge.builder("config.cache.miss.count")
            .description("配置缓存未命中次数")
            .register(meterRegistry, cacheService, service -> service.getCacheStats().missCount());

        Gauge.builder("config.cache.eviction.count")
            .description("配置缓存驱逐次数")
            .register(meterRegistry, cacheService, service -> service.getCacheStats().evictionCount());

        // 初始化错误计数器
        this.configErrorCounter = Counter.builder("config.errors.total")
            .description("配置服务错误总数")
            .register(meterRegistry);

        this.gitSyncErrorCounter = Counter.builder("config.git.sync.errors.total")
            .description("Git同步错误总数")
            .register(meterRegistry);

        this.backupErrorCounter = Counter.builder("config.backup.errors.total")
            .description("配置备份错误总数")
            .register(meterRegistry);

        log.info("配置服务监控指标初始化完成");
    }

    /**
     * 记录配置请求
     */
    public void recordConfigRequest(String application, String profile) {
        configRequestCounter.increment(
            Tags.of(
                "application", application,
                "profile", profile
            )
        );
    }

    /**
     * 记录配置加载耗时
     */
    public Timer.Sample startConfigLoadTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * 结束配置加载计时
     */
    public void stopConfigLoadTimer(Timer.Sample sample, String application, String result) {
        sample.stop(Timer.builder("config.load.duration")
            .tag("application", application)
            .tag("result", result)
            .register(meterRegistry));
    }

    /**
     * 记录配置同步操作
     */
    public void recordConfigSync(String operation, String result) {
        configSyncCounter.increment(
            Tags.of(
                "operation", operation,
                "result", result
            )
        );
    }

    /**
     * 记录配置备份操作
     */
    public void recordConfigBackup(String operation, String result) {
        configBackupCounter.increment(
            Tags.of(
                "operation", operation,
                "result", result
            )
        );
    }

    /**
     * 更新GitLab可用状态
     */
    public void updateGitlabAvailability(boolean available) {
        gitlabAvailableGauge.set(available ? 1 : 0);
        log.debug("GitLab可用状态更新: {}", available);
    }

    /**
     * 更新配置源类型
     */
    public void updateConfigSourceType(String sourceType) {
        long value = "gitlab".equals(sourceType) ? 1 : 0;
        configSourceGauge.set(value);
        log.debug("配置源类型更新: {}", sourceType);
    }

    /**
     * 记录配置错误
     */
    public void recordConfigError(String errorType, String application) {
        configErrorCounter.increment(
            Tags.of(
                "error_type", errorType,
                "application", application != null ? application : "unknown"
            )
        );
    }

    /**
     * 记录Git同步错误
     */
    public void recordGitSyncError(String operation, String errorType) {
        gitSyncErrorCounter.increment(
            Tags.of(
                "operation", operation,
                "error_type", errorType
            )
        );
    }

    /**
     * 记录备份错误
     */
    public void recordBackupError(String operation, String errorType) {
        backupErrorCounter.increment(
            Tags.of(
                "operation", operation,
                "error_type", errorType
            )
        );
    }

    /**
     * 监听配置变更事件，记录相关指标
     */
    @EventListener
    public void handleConfigChangeEvent(ConfigChangeEvent event) {
        try {
            // 记录配置变更操作
            String operation = event.getOperation();
            
            if (operation.contains("REFRESH")) {
                configRefreshCounter.increment(
                    Tags.of(
                        "application", event.getApplication(),
                        "profile", event.getProfile(),
                        "operator", event.getOperator()
                    )
                );
            } else if (operation.contains("SYNC")) {
                recordConfigSync(operation, operation.contains("SUCCESS") ? "success" : "failure");
            } else if (operation.contains("BACKUP")) {
                recordConfigBackup(operation, operation.contains("SUCCESS") ? "success" : "failure");
            }

            // 记录错误指标
            if (operation.contains("FAILED") || operation.contains("ERROR")) {
                recordConfigError(operation, event.getApplication());
            }

            log.debug("配置变更事件指标记录完成: {}", event);

        } catch (Exception e) {
            log.warn("记录配置变更事件指标失败: {}", e.getMessage());
        }
    }

    /**
     * 获取指标摘要
     */
    public String getMetricsSummary() {
        return String.format(
            "配置请求总数: %.0f, 刷新次数: %.0f, 同步次数: %.0f, 备份次数: %.0f, 缓存命中率: %.2f",
            configRequestCounter.count(),
            configRefreshCounter.count(),
            configSyncCounter.count(),
            configBackupCounter.count(),
            cacheService.getCacheStats().hitRate()
        );
    }
} 