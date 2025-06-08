package com.platform.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 请求追踪过滤器
 * 为每个请求生成追踪ID，支持分布式链路追踪
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Component
@Slf4j
public class TraceFilter implements GlobalFilter, Ordered {
    
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String SPAN_ID_HEADER = "X-Span-Id";
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 获取或生成追踪ID
        String traceId = getOrGenerateTraceId(request);
        String spanId = generateSpanId();
        
        log.debug("请求追踪 - TraceId: {}, SpanId: {}, Path: {}", 
                traceId, spanId, request.getURI().getPath());
        
        // 构建新的请求，添加追踪头信息
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(TRACE_ID_HEADER, traceId)
                .header(SPAN_ID_HEADER, spanId)
                .build();
        
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
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
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
} 