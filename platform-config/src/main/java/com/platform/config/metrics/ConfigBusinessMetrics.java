package com.platform.config.metrics;

import com.platform.config.entity.ConfigAudit;
import com.platform.config.entity.ConfigVersion;
import com.platform.config.service.ConfigAuditService;
import com.platform.config.service.ConfigVersionService;
import io.micrometer.core.instrument.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 配置服务业务指标监控
 * 专注于版本控制、审计日志、配置热点等业务相关监控
 *
 * @author Platform Team
 * @since 2.0.0
 */
@Component
@Slf4j
public class ConfigBusinessMetrics {

    private final MeterRegistry meterRegistry;
    private final ConfigVersionService versionService;
    private final ConfigAuditService auditService;

    // 版本控制指标
    private final Counter versionCreateCounter;
    private final Counter versionActivateCounter;
    private final Counter versionRollbackCounter;
    private final Timer versionOperationTimer;
    
    // 审计指标
    private final Counter auditOperationCounter;
    private final Counter auditFailureCounter;
    private final Gauge activeUsersGauge;
    
    // 配置热点分析
    private final Map<String, AtomicLong> configHotspots = new ConcurrentHashMap<>();
    private final AtomicLong totalConfigRequests = new AtomicLong(0);
    
    // 性能指标
    private final Timer responseTimeTimer;
    private final Counter concurrentRequestCounter;
    
    // 业务统计
    private final AtomicLong totalApplications = new AtomicLong(0);
    private final AtomicLong totalProfiles = new AtomicLong(0);
    private final AtomicLong totalVersions = new AtomicLong(0);

    public ConfigBusinessMetrics(MeterRegistry meterRegistry, 
                                ConfigVersionService versionService,
                                ConfigAuditService auditService) {
        this.meterRegistry = meterRegistry;
        this.versionService = versionService;
        this.auditService = auditService;

        // 初始化版本控制指标
        this.versionCreateCounter = Counter.builder("config.version.create.total")
            .description("配置版本创建总数")
            .register(meterRegistry);

        this.versionActivateCounter = Counter.builder("config.version.activate.total")
            .description("配置版本激活总数")
            .register(meterRegistry);

        this.versionRollbackCounter = Counter.builder("config.version.rollback.total")
            .description("配置版本回滚总数")
            .register(meterRegistry);

        this.versionOperationTimer = Timer.builder("config.version.operation.duration")
            .description("版本操作耗时")
            .register(meterRegistry);

        // 初始化审计指标
        this.auditOperationCounter = Counter.builder("config.audit.operation.total")
            .description("配置审计操作总数")
            .register(meterRegistry);

        this.auditFailureCounter = Counter.builder("config.audit.failure.total")
            .description("配置操作失败总数")
            .register(meterRegistry);

        this.activeUsersGauge = Gauge.builder("config.users.active")
            .description("活跃用户数量")
            .register(meterRegistry, this, metrics -> getActiveUsersCount());

        // 初始化性能指标
        this.responseTimeTimer = Timer.builder("config.response.time")
            .description("配置响应时间")
            .register(meterRegistry);

        this.concurrentRequestCounter = Counter.builder("config.request.concurrent.total")
            .description("并发请求总数")
            .register(meterRegistry);

        // 初始化业务统计指标
        Gauge.builder("config.applications.total")
            .description("应用总数")
            .register(meterRegistry, totalApplications, AtomicLong::get);

        Gauge.builder("config.profiles.total")
            .description("环境总数")
            .register(meterRegistry, totalProfiles, AtomicLong::get);

        Gauge.builder("config.versions.total")
            .description("版本总数")
            .register(meterRegistry, totalVersions, AtomicLong::get);

        log.info("配置业务监控指标初始化完成");
    }

    /**
     * 记录版本创建操作
     */
    public void recordVersionCreate(String application, String profile, String operator) {
        versionCreateCounter.increment(
            Tags.of(
                "application", application,
                "profile", profile,
                "operator", operator
            )
        );
        
        updateBusinessStatistics();
        log.debug("记录版本创建: application={}, profile={}, operator={}", 
                 application, profile, operator);
    }

    /**
     * 记录版本激活操作
     */
    public void recordVersionActivate(String application, String profile, 
                                     String operator, String versionId) {
        versionActivateCounter.increment(
            Tags.of(
                "application", application,
                "profile", profile,
                "operator", operator,
                "version_id", versionId
            )
        );
        
        log.debug("记录版本激活: application={}, profile={}, version={}", 
                 application, profile, versionId);
    }

    /**
     * 记录版本回滚操作
     */
    public void recordVersionRollback(String application, String profile, 
                                     String operator, String fromVersion, 
                                     String toVersion, String reason) {
        versionRollbackCounter.increment(
            Tags.of(
                "application", application,
                "profile", profile,
                "operator", operator,
                "from_version", fromVersion,
                "to_version", toVersion,
                "reason", reason
            )
        );
        
        log.warn("记录版本回滚: application={}, profile={}, from={}, to={}, reason={}", 
                application, profile, fromVersion, toVersion, reason);
    }

    /**
     * 开始版本操作计时
     */
    public Timer.Sample startVersionOperationTimer(String operation) {
        return Timer.start(meterRegistry);
    }

    /**
     * 结束版本操作计时
     */
    public void stopVersionOperationTimer(Timer.Sample sample, String operation, 
                                         String application, String result) {
        sample.stop(Timer.builder("config.version.operation.duration")
            .tag("operation", operation)
            .tag("application", application)
            .tag("result", result)
            .register(meterRegistry));
    }

    /**
     * 记录审计操作
     */
    public void recordAuditOperation(String operation, String application, 
                                   String operator, String result) {
        auditOperationCounter.increment(
            Tags.of(
                "operation", operation,
                "application", application,
                "operator", operator,
                "result", result
            )
        );
        
        if ("FAILURE".equals(result)) {
            auditFailureCounter.increment(
                Tags.of(
                    "operation", operation,
                    "application", application,
                    "operator", operator
                )
            );
        }
    }

    /**
     * 记录配置访问热点
     */
    public void recordConfigAccess(String application, String profile, String configKey) {
        String hotspotKey = String.format("%s:%s:%s", application, profile, configKey);
        configHotspots.computeIfAbsent(hotspotKey, k -> new AtomicLong(0)).incrementAndGet();
        totalConfigRequests.incrementAndGet();
        
        // 注册或更新热点指标
        String gaugeName = "config.hotspot." + sanitizeMetricName(hotspotKey);
        Gauge.builder(gaugeName)
            .description("配置热点访问次数: " + hotspotKey)
            .register(meterRegistry, configHotspots.get(hotspotKey), AtomicLong::get);
    }

    /**
     * 记录响应时间
     */
    public Timer.Sample startResponseTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * 结束响应时间计时
     */
    public void stopResponseTimer(Timer.Sample sample, String endpoint, String method) {
        sample.stop(Timer.builder("config.response.time")
            .tag("endpoint", endpoint)
            .tag("method", method)
            .register(meterRegistry));
    }

    /**
     * 记录并发请求
     */
    public void recordConcurrentRequest(String endpoint) {
        concurrentRequestCounter.increment(
            Tags.of("endpoint", endpoint)
        );
    }

    /**
     * 获取活跃用户数量
     */
    private double getActiveUsersCount() {
        try {
            // 获取最近1小时内的活跃用户
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            return auditService.getActiveUsersCount(oneHourAgo);
        } catch (Exception e) {
            log.warn("获取活跃用户数量失败", e);
            return 0;
        }
    }

    /**
     * 更新业务统计
     */
    private void updateBusinessStatistics() {
        try {
            totalApplications.set(versionService.getTotalApplicationsCount());
            totalProfiles.set(versionService.getTotalProfilesCount());
            totalVersions.set(versionService.getTotalVersionsCount());
        } catch (Exception e) {
            log.warn("更新业务统计失败", e);
        }
    }

    /**
     * 清理指标名称，确保符合Prometheus规范
     */
    private String sanitizeMetricName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_:]", "_");
    }

    /**
     * 定期更新业务统计
     */
    @Scheduled(fixedRate = 300000) // 每5分钟更新一次
    public void updatePeriodicStatistics() {
        updateBusinessStatistics();
        
        // 记录热点配置TOP10
        configHotspots.entrySet().stream()
            .sorted((e1, e2) -> Long.compare(e2.getValue().get(), e1.getValue().get()))
            .limit(10)
            .forEach(entry -> {
                log.debug("热点配置: {} = {} 次访问", entry.getKey(), entry.getValue().get());
            });
    }

    /**
     * 获取业务指标摘要
     */
    public Map<String, Object> getBusinessMetricsSummary() {
        return Map.of(
            "total_applications", totalApplications.get(),
            "total_profiles", totalProfiles.get(),
            "total_versions", totalVersions.get(),
            "total_config_requests", totalConfigRequests.get(),
            "active_users", getActiveUsersCount(),
            "version_creates", versionCreateCounter.count(),
            "version_activates", versionActivateCounter.count(),
            "version_rollbacks", versionRollbackCounter.count(),
            "audit_operations", auditOperationCounter.count(),
            "audit_failures", auditFailureCounter.count(),
            "hotspot_configs", configHotspots.size()
        );
    }
} 