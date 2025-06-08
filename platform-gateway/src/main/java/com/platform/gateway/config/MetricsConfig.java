package com.platform.gateway.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 监控指标配置类
 * 配置网关的各种监控指标和度量
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Configuration
@Slf4j
public class MetricsConfig {
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    /**
     * 网关监控过滤器
     * 记录请求次数、响应时间、错误率等指标
     */
    @Bean
    public GlobalFilter gatewayMetricsFilter() {
        return new GatewayMetricsFilter(meterRegistry);
    }
    
    /**
     * 网关监控过滤器实现
     */
    public static class GatewayMetricsFilter implements GlobalFilter, Ordered {
        
        private final MeterRegistry meterRegistry;
        private final Counter totalRequests;
        private final Counter successRequests;
        private final Counter errorRequests;
        private final Timer requestTimer;
        
        public GatewayMetricsFilter(MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
            
            // 初始化监控指标
            this.totalRequests = Counter.builder("gateway_requests_total")
                    .description("网关总请求数")
                    .register(meterRegistry);
                    
            this.successRequests = Counter.builder("gateway_requests_success")
                    .description("网关成功请求数")
                    .register(meterRegistry);
                    
            this.errorRequests = Counter.builder("gateway_requests_error")
                    .description("网关错误请求数")
                    .register(meterRegistry);
                    
            this.requestTimer = Timer.builder("gateway_request_duration")
                    .description("网关请求处理时间")
                    .register(meterRegistry);
        }
        
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            long startTime = System.currentTimeMillis();
            
            ServerHttpRequest request = exchange.getRequest();
            String method = request.getMethod().name();
            String uri = request.getURI().getPath();
            
            // 记录总请求数
            totalRequests.increment();
            
            return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    // 记录成功请求
                    ServerHttpResponse response = exchange.getResponse();
                    HttpStatus statusCode = response.getStatusCode();
                    
                    long duration = System.currentTimeMillis() - startTime;
                    
                    // 记录请求时间
                    requestTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
                    
                    // 记录成功或错误请求
                    if (statusCode != null && statusCode.is2xxSuccessful()) {
                        successRequests.increment();
                        log.debug("成功请求: {} {}, 耗时: {}ms, 状态码: {}", 
                                method, uri, duration, statusCode.value());
                    } else if (statusCode != null && statusCode.isError()) {
                        errorRequests.increment();
                        log.warn("错误请求: {} {}, 耗时: {}ms, 状态码: {}", 
                                method, uri, duration, statusCode.value());
                    }
                    
                    // 记录自定义指标
                    recordCustomMetrics(request, response, duration);
                })
                .doOnError(throwable -> {
                    // 记录异常请求
                    errorRequests.increment();
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("异常请求: {} {}, 耗时: {}ms, 异常: {}", 
                            method, uri, duration, throwable.getMessage());
                });
        }
        
        /**
         * 记录自定义监控指标
         */
        private void recordCustomMetrics(ServerHttpRequest request, ServerHttpResponse response, long duration) {
            String method = request.getMethod().name();
            String uri = request.getURI().getPath();
            String statusCode = response.getStatusCode() != null ? 
                String.valueOf(response.getStatusCode().value()) : "unknown";
            
            // 按路径和方法记录请求数
            Counter.builder("gateway_requests_by_path")
                    .description("按路径统计的请求数")
                    .tag("method", method)
                    .tag("path", getPathPattern(uri))
                    .tag("status", statusCode)
                    .register(meterRegistry)
                    .increment();
                    
            // 记录响应时间分布
            Timer.builder("gateway_response_time_by_path")
                    .description("按路径统计的响应时间")
                    .tag("method", method)
                    .tag("path", getPathPattern(uri))
                    .register(meterRegistry)
                    .record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
        
        /**
         * 获取路径模式（用于指标标签）
         */
        private String getPathPattern(String path) {
            if (path.startsWith("/config/")) {
                return "/config/**";
            } else if (path.startsWith("/api/users/")) {
                return "/api/users/**";
            } else if (path.startsWith("/actuator/")) {
                return "/actuator/**";
            } else if (path.startsWith("/fallback/")) {
                return "/fallback/**";
            }
            return path;
        }
        
        @Override
        public int getOrder() {
            // 设置较高优先级，但在日志过滤器之后
            return Ordered.HIGHEST_PRECEDENCE + 10;
        }
    }
} 