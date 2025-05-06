package com.platform.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * 全局响应过滤器
 * <p>
 * 对所有经过网关的响应进行后置处理
 * </p>
 */
@Component
public class GlobalResponseFilter implements GlobalFilter, Ordered {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalResponseFilter.class);
    
    /**
     * 请求ID的Header名称
     */
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    
    /**
     * 请求开始时间的属性名
     */
    private static final String START_TIME_ATTR = "requestStartTime";
    
    /**
     * 过滤处理
     *
     * @param exchange 当前服务器Web交换
     * @param chain 网关过滤器链
     * @return Mono<Void> 异步结果
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 记录请求开始时间
        exchange.getAttributes().put(START_TIME_ATTR, Instant.now());
        
        // 继续执行过滤器链
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    // 响应完成后执行
                    logResponse(exchange);
                }));
    }
    
    /**
     * 记录响应日志
     *
     * @param exchange 当前服务器Web交换
     */
    private void logResponse(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getPath().value();
        String method = request.getMethod().name();
        int statusCode = response.getStatusCode() != null 
                ? response.getStatusCode().value() 
                : 0;
        
        // 获取请求ID
        String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
        
        // 计算请求耗时
        Instant startTime = exchange.getAttribute(START_TIME_ATTR);
        long duration = startTime != null 
                ? Duration.between(startTime, Instant.now()).toMillis() 
                : -1;
        
        // 记录响应日志
        log.info("Gateway response: {} {} - status: {} - time: {}ms - {}", 
                method, path, statusCode, duration, requestId);
    }
    
    /**
     * 过滤器顺序
     * <p>
     * 值越大优先级越低，确保此过滤器最后执行
     * </p>
     *
     * @return int 顺序值
     */
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
