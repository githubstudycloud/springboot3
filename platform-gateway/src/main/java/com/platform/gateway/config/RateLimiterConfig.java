package com.platform.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Configuration for API rate limiting.
 * 
 * Uses Redis to implement distributed rate limiting.
 * Different limits can be applied to different endpoints and users.
 */
@Configuration
public class RateLimiterConfig {

    /**
     * Default rate limiter for anonymous requests.
     * 
     * @return RedisRateLimiter configured for anonymous users
     */
    @Bean
    public RedisRateLimiter defaultRateLimiter() {
        // Allow 50 requests per second with a burst of 100
        return new RedisRateLimiter(50, 100);
    }

    /**
     * Rate limiter for authenticated users.
     * 
     * @return RedisRateLimiter configured for authenticated users
     */
    @Bean
    public RedisRateLimiter authenticatedRateLimiter() {
        // Allow 100 requests per second with a burst of 200
        return new RedisRateLimiter(100, 200);
    }

    /**
     * Rate limiter for admin users.
     * 
     * @return RedisRateLimiter configured for admin users
     */
    @Bean
    public RedisRateLimiter adminRateLimiter() {
        // Allow 200 requests per second with a burst of 500
        return new RedisRateLimiter(200, 500);
    }

    /**
     * Key resolver that uses the request remote address as the rate limit key.
     * This applies rate limits per client IP address.
     * 
     * @return KeyResolver that resolves to client IP
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest().getRemoteAddress().getHostString()
        );
    }

    /**
     * Key resolver that uses the user ID from the request header as the rate limit key.
     * This applies rate limits per user.
     * 
     * Falls back to IP address if no user ID is present.
     * 
     * @return KeyResolver that resolves to user ID or IP
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
            if (userId != null && !userId.isEmpty()) {
                return Mono.just(userId);
            }
            
            // Fall back to IP if no user ID
            return Mono.just(exchange.getRequest().getRemoteAddress().getHostString());
        };
    }
}
