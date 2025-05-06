package com.platform.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * 限流配置
 * <p>
 * 基于Redis的令牌桶算法实现
 * </p>
 */
@Configuration
public class RateLimiterConfig {

    /**
     * IP地址限流键解析器
     * <p>
     * 根据请求IP地址进行限流
     * </p>
     *
     * @return KeyResolver 键解析器
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            return Mono.just(ip);
        };
    }

    /**
     * 用户ID限流键解析器
     * <p>
     * 根据请求头中的用户ID进行限流
     * </p>
     *
     * @return KeyResolver 键解析器
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            return Mono.just(userId == null ? "anonymous" : userId);
        };
    }

    /**
     * 接口路径限流键解析器
     * <p>
     * 根据请求路径进行限流
     * </p>
     *
     * @return KeyResolver 键解析器
     */
    @Bean
    public KeyResolver apiPathKeyResolver() {
        return exchange -> {
            String path = exchange.getRequest().getPath().value();
            return Mono.just(path);
        };
    }

    /**
     * 默认Redis限流器
     *
     * @return RedisRateLimiter Redis限流器
     */
    @Bean
    public RedisRateLimiter defaultRedisRateLimiter() {
        return new RedisRateLimiter(100, 200);
    }

    /**
     * 低频限流器（针对敏感接口）
     *
     * @return RedisRateLimiter Redis限流器
     */
    @Bean
    public RedisRateLimiter lowRateLimiter() {
        return new RedisRateLimiter(10, 20);
    }

    /**
     * 高频限流器（针对高频接口）
     *
     * @return RedisRateLimiter Redis限流器
     */
    @Bean
    public RedisRateLimiter highRateLimiter() {
        return new RedisRateLimiter(500, 1000);
    }
}
