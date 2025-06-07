package com.platform.config.service;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.platform.config.event.ConfigChangeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ConfigCacheService 单元测试
 *
 * @author Platform Team
 * @since 2.0.0
 */
@ExtendWith(MockitoExtension.class)
class ConfigCacheServiceTest {

    private ConfigCacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new ConfigCacheService(100, 60, 30);
    }

    @Test
    void testPutAndGetConfig() {
        // Given
        String key = "test.app:dev";
        String value = "test-config-value";

        // When
        cacheService.putConfig(key, value);
        Object result = cacheService.getConfig(key);

        // Then
        assertThat(result).isEqualTo(value);
    }

    @Test
    void testGetConfigWithLoader() {
        // Given
        String key = "test.app:prod";
        String expectedValue = "loaded-config-value";
        ConfigCacheService.ConfigLoader loader = k -> expectedValue;

        // When
        Object result = cacheService.getConfig(key, loader);

        // Then
        assertThat(result).isEqualTo(expectedValue);
        
        // 验证缓存是否生效
        Object cachedResult = cacheService.getConfig(key);
        assertThat(cachedResult).isEqualTo(expectedValue);
    }

    @Test
    void testPutAllConfigs() {
        // Given
        Map<String, Object> configs = Map.of(
            "app1:dev", "config1",
            "app2:test", "config2",
            "app3:prod", "config3"
        );

        // When
        cacheService.putAllConfigs(configs);

        // Then
        configs.forEach((key, expectedValue) -> {
            Object actualValue = cacheService.getConfig(key);
            assertThat(actualValue).isEqualTo(expectedValue);
        });
    }

    @Test
    void testEvictConfig() {
        // Given
        String key = "test.app:staging";
        String value = "staging-config";
        cacheService.putConfig(key, value);
        assertThat(cacheService.getConfig(key)).isEqualTo(value);

        // When
        cacheService.evictConfig(key);

        // Then
        assertThat(cacheService.getConfig(key)).isNull();
    }

    @Test
    void testEvictAllConfigs() {
        // Given
        Map<String, Object> configs = Map.of(
            "app1:dev", "config1",
            "app2:test", "config2"
        );
        cacheService.putAllConfigs(configs);

        // When
        cacheService.evictAllConfigs();

        // Then
        configs.keySet().forEach(key -> {
            assertThat(cacheService.getConfig(key)).isNull();
        });
    }

    @Test
    void testWarmUpCache() {
        // Given
        Map<String, Object> configs = Map.of(
            "warm.app1:dev", "warm-config1",
            "warm.app2:test", "warm-config2"
        );

        // When
        cacheService.warmUpCache(configs);

        // Then
        configs.forEach((key, expectedValue) -> {
            Object actualValue = cacheService.getConfig(key);
            assertThat(actualValue).isEqualTo(expectedValue);
        });
    }

    @Test
    void testGetCacheStats() {
        // Given
        String key = "stats.app:dev";
        String value = "stats-config";
        
        // When
        cacheService.putConfig(key, value);
        cacheService.getConfig(key); // 产生一次命中
        cacheService.getConfig("non-existent-key"); // 产生一次未命中

        // Then
        CacheStats stats = cacheService.getCacheStats();
        assertThat(stats.hitCount()).isGreaterThan(0);
        assertThat(stats.missCount()).isGreaterThan(0);
        assertThat(stats.requestCount()).isGreaterThan(0);
    }

    @Test
    void testGetCacheInfo() {
        // Given
        cacheService.putConfig("info.app:dev", "info-config");

        // When
        Map<String, Object> cacheInfo = cacheService.getCacheInfo();

        // Then
        assertThat(cacheInfo).containsKeys(
            "size", "hitCount", "missCount", "hitRate", 
            "evictionCount", "averageLoadTime", "requestCount"
        );
        assertThat((Long) cacheInfo.get("size")).isGreaterThan(0);
    }

    @Test
    void testHandleConfigChangeEvent_SpecificApplication() {
        // Given
        String key = "event.app:dev";
        String value = "event-config";
        cacheService.putConfig(key, value);
        assertThat(cacheService.getConfig(key)).isEqualTo(value);

        ConfigChangeEvent event = new ConfigChangeEvent(
            "event.app", "dev", "UPDATE", "test-operator"
        );

        // When
        cacheService.handleConfigChange(event);

        // Then
        assertThat(cacheService.getConfig(key)).isNull();
    }

    @Test
    void testHandleConfigChangeEvent_GlobalRefresh() {
        // Given
        Map<String, Object> configs = Map.of(
            "app1:dev", "config1",
            "app2:test", "config2"
        );
        cacheService.putAllConfigs(configs);

        ConfigChangeEvent event = new ConfigChangeEvent(
            "*", "all", "REFRESH_ALL", "test-operator"
        );

        // When
        cacheService.handleConfigChange(event);

        // Then
        configs.keySet().forEach(key -> {
            assertThat(cacheService.getConfig(key)).isNull();
        });
    }

    @Test
    void testCacheConfiguration() {
        // Given & When
        ConfigCacheService customCacheService = new ConfigCacheService(500, 120, 60);

        // Then - 通过反射验证配置是否正确设置
        // 注意：Caffeine的内部配置不易直接访问，这里主要验证服务能正常创建和工作
        customCacheService.putConfig("config.test", "test-value");
        assertThat(customCacheService.getConfig("config.test")).isEqualTo("test-value");
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        // Given
        String key = "concurrent.app:dev";
        String value = "concurrent-config";

        // When - 模拟并发访问
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                cacheService.putConfig(key + i, value + i);
            }
        });

        Thread reader = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                cacheService.getConfig(key + i);
            }
        });

        writer.start();
        reader.start();
        writer.join();
        reader.join();

        // Then - 验证缓存在并发环境下正常工作
        CacheStats stats = cacheService.getCacheStats();
        assertThat(stats.requestCount()).isGreaterThan(0);
    }
} 