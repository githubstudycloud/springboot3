package com.platform.config.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.platform.config.event.ConfigChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 配置缓存服务
 * 使用Caffeine实现高性能配置缓存
 *
 * @author Platform Team
 * @since 2.0.0
 */
@Service
@Slf4j
public class ConfigCacheService {

    private final Cache<String, Object> configCache;

    public ConfigCacheService(
            @Value("${platform.config.cache.maximum-size:1000}") int maximumSize,
            @Value("${platform.config.cache.expire-after-write:300}") int expireAfterWriteSeconds,
            @Value("${platform.config.cache.expire-after-access:60}") int expireAfterAccessSeconds) {
        
        this.configCache = Caffeine.newBuilder()
            .maximumSize(maximumSize)
            .expireAfterWrite(Duration.ofSeconds(expireAfterWriteSeconds))
            .expireAfterAccess(Duration.ofSeconds(expireAfterAccessSeconds))
            .recordStats() // 启用统计
            .build();

        log.info("配置缓存初始化完成 - 最大容量: {}, 写入过期: {}秒, 访问过期: {}秒", 
                maximumSize, expireAfterWriteSeconds, expireAfterAccessSeconds);
    }

    /**
     * 获取缓存配置
     *
     * @param key 配置键
     * @return 配置值，如果不存在返回null
     */
    public Object getConfig(String key) {
        Object value = configCache.getIfPresent(key);
        log.debug("从缓存获取配置: {} = {}", key, value != null ? "Hit" : "Miss");
        return value;
    }

    /**
     * 获取配置，如果不存在则通过loader加载
     *
     * @param key 配置键
     * @param loader 配置加载器
     * @return 配置值
     */
    public Object getConfig(String key, ConfigLoader loader) {
        return configCache.get(key, k -> {
            log.debug("缓存未命中，加载配置: {}", k);
            return loader.load(k);
        });
    }

    /**
     * 放入缓存
     *
     * @param key 配置键
     * @param value 配置值
     */
    public void putConfig(String key, Object value) {
        configCache.put(key, value);
        log.debug("配置已放入缓存: {}", key);
    }

    /**
     * 批量放入缓存
     *
     * @param configs 配置映射
     */
    public void putAllConfigs(Map<String, Object> configs) {
        configCache.putAll(configs);
        log.debug("批量配置已放入缓存，数量: {}", configs.size());
    }

    /**
     * 使指定配置缓存失效
     *
     * @param key 配置键
     */
    public void evictConfig(String key) {
        configCache.invalidate(key);
        log.debug("配置缓存已失效: {}", key);
    }

    /**
     * 使所有配置缓存失效
     */
    public void evictAllConfigs() {
        long size = configCache.estimatedSize();
        configCache.invalidateAll();
        log.info("所有配置缓存已清空，清空数量: {}", size);
    }

    /**
     * 预热缓存
     *
     * @param configs 预热配置
     */
    public void warmUpCache(Map<String, Object> configs) {
        log.info("开始预热配置缓存，预热数量: {}", configs.size());
        putAllConfigs(configs);
        log.info("配置缓存预热完成");
    }

    /**
     * 获取缓存统计信息
     *
     * @return 缓存统计
     */
    public CacheStats getCacheStats() {
        return configCache.stats();
    }

    /**
     * 获取缓存详细信息
     *
     * @return 缓存信息映射
     */
    public Map<String, Object> getCacheInfo() {
        CacheStats stats = getCacheStats();
        return Map.of(
            "size", configCache.estimatedSize(),
            "hitCount", stats.hitCount(),
            "missCount", stats.missCount(),
            "hitRate", stats.hitRate(),
            "evictionCount", stats.evictionCount(),
            "averageLoadTime", stats.averageLoadTime(),
            "requestCount", stats.requestCount()
        );
    }

    /**
     * 监听配置变更事件，自动清理相关缓存
     *
     * @param event 配置变更事件
     */
    @EventListener
    public void handleConfigChange(ConfigChangeEvent event) {
        try {
            String cacheKey = buildCacheKey(event.getApplication(), event.getProfile());
            
            // 清理具体的配置缓存
            evictConfig(cacheKey);
            
            // 如果是全局配置变更，清理所有缓存
            if ("*".equals(event.getApplication()) || "REFRESH_ALL".equals(event.getOperation())) {
                evictAllConfigs();
                log.info("检测到全局配置变更，已清空所有缓存");
            } else {
                log.info("检测到配置变更，已清理相关缓存: {}", cacheKey);
            }
            
        } catch (Exception e) {
            log.warn("处理配置变更事件时清理缓存失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 构建缓存键
     *
     * @param application 应用名
     * @param profile 环境
     * @return 缓存键
     */
    private String buildCacheKey(String application, String profile) {
        return String.format("%s:%s", application, profile);
    }

    /**
     * 配置加载器接口
     */
    @FunctionalInterface
    public interface ConfigLoader {
        /**
         * 加载配置
         *
         * @param key 配置键
         * @return 配置值
         */
        Object load(String key);
    }
} 