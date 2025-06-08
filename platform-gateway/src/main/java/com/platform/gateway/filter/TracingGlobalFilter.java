package com.platform.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 请求追踪全局过滤器
 * 实现分布式追踪，为每个请求生成唯一的追踪ID
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Component
@Slf4j
public class TracingGlobalFilter implements GlobalFilter, Ordered {
    
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String SPAN_ID_HEADER = "X-Span-Id";
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 获取或生成追踪ID
        String traceId = getOrGenerateTraceId(request);
        String spanId = generateSpanId();
        String requestId = generateRequestId();
        
        log.info("开始处理请求 - TraceId: {}, SpanId: {}, RequestId: {}, Path: {}", 
                traceId, spanId, requestId, request.getURI().getPath());
        
        // 构建新的请求，添加追踪头信息
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(TRACE_ID_HEADER, traceId)
                .header(SPAN_ID_HEADER, spanId)
                .header(REQUEST_ID_HEADER, requestId)
                .build();
        
        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .doOnSuccess(aVoid -> {
                    log.info("请求处理完成 - TraceId: {}, SpanId: {}", traceId, spanId);
                })
                .doOnError(throwable -> {
                    log.error("请求处理异常 - TraceId: {}, SpanId: {}, Error: {}", 
                            traceId, spanId, throwable.getMessage());
                });
    }
    
    /**
     * 获取或生成追踪ID
     */
    private String getOrGenerateTraceId(ServerHttpRequest request) {
        String traceId = request.getHeaders().getFirst(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = generateTraceId();
        }
        return traceId;
    }
    
    /**
     * 生成追踪ID
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 生成Span ID
     */
    private String generateSpanId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    /**
     * 生成请求ID
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    @Override
    public int getOrder() {
        // 设置为最高优先级+1
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
} 