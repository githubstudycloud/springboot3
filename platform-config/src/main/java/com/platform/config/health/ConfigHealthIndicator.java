package com.platform.config.health;

import com.platform.config.metrics.ConfigMetrics;
import com.platform.config.service.ConfigCacheService;
import com.platform.config.service.ConfigSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.boot.actuator.health.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 配置服务健康检查指示器
 * 增强版健康检查，包含详细的服务状态信息
 *
 * @author Platform Team
 * @since 2.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ConfigHealthIndicator implements HealthIndicator {

    private final ConfigSyncService syncService;
    private final ConfigCacheService cacheService;
    private final ConfigMetrics configMetrics;

    @Override
    public Health health() {
        try {
            Health.Builder builder = new Health.Builder();
            
            // 检查整体健康状态
            boolean isHealthy = checkOverallHealth(builder);
            
            if (isHealthy) {
                builder.status(Status.UP);
            } else {
                builder.status(Status.DOWN);
            }
            
            // 添加通用信息
            builder.withDetail("timestamp", LocalDateTime.now())
                   .withDetail("version", "2.0.0")
                   .withDetail("metrics_summary", configMetrics.getMetricsSummary());
                   
            return builder.build();
            
        } catch (Exception e) {
            log.error("健康检查执行失败", e);
            return Health.down()
                .withDetail("error", e.getMessage())
                .withDetail("timestamp", LocalDateTime.now())
                .build();
        }
    }

    /**
     * 检查整体健康状态
     *
     * @param builder 健康状态构建器
     * @return 是否健康
     */
    private boolean checkOverallHealth(Health.Builder builder) {
        boolean isHealthy = true;

        // 检查GitLab连接状态
        boolean gitlabAvailable = checkGitlabHealth(builder);
        
        // 检查缓存健康状态
        boolean cacheHealthy = checkCacheHealth(builder);
        
        // 检查系统资源状态
        boolean systemHealthy = checkSystemHealth(builder);
        
        // 只有当至少有一个配置源可用时，系统才算健康
        if (!gitlabAvailable && !systemHealthy) {
            isHealthy = false;
            builder.withDetail("status_message", "没有可用的配置源");
        }
        
        if (!cacheHealthy) {
            // 缓存不健康不会影响整体状态，但会记录警告
            builder.withDetail("cache_warning", "缓存服务异常，可能影响性能");
        }

        return isHealthy;
    }

    /**
     * 检查GitLab健康状态
     *
     * @param builder 健康状态构建器
     * @return GitLab是否可用
     */
    private boolean checkGitlabHealth(Health.Builder builder) {
        try {
            boolean gitlabAvailable = syncService.isGitlabAvailable();
            
            if (gitlabAvailable) {
                builder.withDetail("gitlab.status", "UP")
                       .withDetail("gitlab.message", "GitLab连接正常");
            } else {
                builder.withDetail("gitlab.status", "DOWN")
                       .withDetail("gitlab.message", "GitLab连接失败或未配置");
            }
            
            // 更新指标
            configMetrics.updateGitlabAvailability(gitlabAvailable);
            
            return gitlabAvailable;
            
        } catch (Exception e) {
            log.warn("GitLab健康检查失败", e);
            builder.withDetail("gitlab.status", "ERROR")
                   .withDetail("gitlab.error", e.getMessage());
            
            configMetrics.updateGitlabAvailability(false);
            return false;
        }
    }

    /**
     * 检查缓存健康状态
     *
     * @param builder 健康状态构建器
     * @return 缓存是否健康
     */
    private boolean checkCacheHealth(Health.Builder builder) {
        try {
            Map<String, Object> cacheInfo = cacheService.getCacheInfo();
            
            long cacheSize = (Long) cacheInfo.get("size");
            double hitRate = (Double) cacheInfo.get("hitRate");
            long requestCount = (Long) cacheInfo.get("requestCount");
            
            boolean cacheHealthy = true;
            String cacheStatus = "UP";
            String cacheMessage = "缓存运行正常";
            
            // 检查缓存健康度
            if (requestCount > 1000 && hitRate < 0.5) {
                cacheHealthy = false;
                cacheStatus = "WARN";
                cacheMessage = "缓存命中率过低";
            } else if (cacheSize == 0 && requestCount > 100) {
                cacheStatus = "WARN";
                cacheMessage = "缓存为空但有请求";
            }
            
            builder.withDetail("cache.status", cacheStatus)
                   .withDetail("cache.message", cacheMessage)
                   .withDetail("cache.size", cacheSize)
                   .withDetail("cache.hit_rate", String.format("%.2f%%", hitRate * 100))
                   .withDetail("cache.request_count", requestCount)
                   .withDetail("cache.eviction_count", cacheInfo.get("evictionCount"));
                   
            return cacheHealthy;
            
        } catch (Exception e) {
            log.warn("缓存健康检查失败", e);
            builder.withDetail("cache.status", "ERROR")
                   .withDetail("cache.error", e.getMessage());
            return false;
        }
    }

    /**
     * 检查系统健康状态
     *
     * @param builder 健康状态构建器
     * @return 系统是否健康
     */
    private boolean checkSystemHealth(Health.Builder builder) {
        try {
            // 检查内存使用情况
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
            
            boolean systemHealthy = true;
            String systemStatus = "UP";
            String systemMessage = "系统资源正常";
            
            if (memoryUsagePercent > 90) {
                systemHealthy = false;
                systemStatus = "DOWN";
                systemMessage = "内存使用率过高";
            } else if (memoryUsagePercent > 80) {
                systemStatus = "WARN";
                systemMessage = "内存使用率较高";
            }
            
            builder.withDetail("system.status", systemStatus)
                   .withDetail("system.message", systemMessage)
                   .withDetail("system.memory_usage", String.format("%.1f%%", memoryUsagePercent))
                   .withDetail("system.memory_used_mb", usedMemory / 1024 / 1024)
                   .withDetail("system.memory_max_mb", maxMemory / 1024 / 1024);
                   
            // 检查CPU核心数
            int availableProcessors = runtime.availableProcessors();
            builder.withDetail("system.cpu_cores", availableProcessors);
            
            return systemHealthy;
            
        } catch (Exception e) {
            log.warn("系统健康检查失败", e);
            builder.withDetail("system.status", "ERROR")
                   .withDetail("system.error", e.getMessage());
            return false;
        }
    }
} 