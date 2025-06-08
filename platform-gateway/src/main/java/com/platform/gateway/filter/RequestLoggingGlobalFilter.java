package com.platform.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 全局请求日志过滤器
 * 记录请求和响应的基本信息，用于链路追踪和调试
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Component
@Slf4j
public class RequestLoggingGlobalFilter implements GlobalFilter, Ordered {
    
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String REQUEST_TIME_ATTR = "request_time";
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        
        // 生成请求ID
        String requestId = generateRequestId();
        
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        exchange.getAttributes().put(REQUEST_TIME_ATTR, startTime);
        
        // 添加请求ID到响应头
        response.getHeaders().add(REQUEST_ID_HEADER, requestId);
        
        // 记录请求信息
        logRequest(request, requestId);
        
        // 继续执行过滤器链
        return chain.filter(exchange).then(
            Mono.fromRunnable(() -> {
                // 记录响应信息
                logResponse(request, response, requestId, startTime);
            })
        );
    }
    
    /**
     * 记录请求信息
     */
    private void logRequest(ServerHttpRequest request, String requestId) {
        String method = request.getMethod().name();
        String uri = request.getURI().toString();
        String clientIp = getClientIp(request);
        String userAgent = request.getHeaders().getFirst("User-Agent");
        
        log.info("Request Start - ID: {}, Method: {}, URI: {}, ClientIP: {}, UserAgent: {}", 
                requestId, method, uri, clientIp, userAgent);
        
        // 记录请求头（仅在DEBUG级别）
        if (log.isDebugEnabled()) {
            request.getHeaders().forEach((name, values) -> {
                if (!name.equalsIgnoreCase("Authorization")) { // 不记录敏感信息
                    log.debug("Request Header - ID: {}, {}: {}", requestId, name, values);
                }
            });
        }
    }
    
    /**
     * 记录响应信息
     */
    private void logResponse(ServerHttpRequest request, ServerHttpResponse response, 
                           String requestId, long startTime) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        String method = request.getMethod().name();
        String uri = request.getURI().getPath();
        int statusCode = response.getStatusCode() != null ? response.getStatusCode().value() : 0;
        
        log.info("Request End - ID: {}, Method: {}, URI: {}, Status: {}, Duration: {}ms", 
                requestId, method, uri, statusCode, duration);
        
        // 记录慢请求
        if (duration > 1000) {
            log.warn("Slow Request - ID: {}, Method: {}, URI: {}, Duration: {}ms", 
                    requestId, method, uri, duration);
        }
        
        // 记录错误响应
        if (statusCode >= 400) {
            log.warn("Error Response - ID: {}, Method: {}, URI: {}, Status: {}", 
                    requestId, method, uri, statusCode);
        }
    }
    
    /**
     * 生成请求ID
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    /**
     * 获取客户端真实IP
     */
    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddress() != null ? 
            request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }
    
    @Override
    public int getOrder() {
        // 设置为最高优先级，确保最先执行
        return Ordered.HIGHEST_PRECEDENCE;
    }
} 