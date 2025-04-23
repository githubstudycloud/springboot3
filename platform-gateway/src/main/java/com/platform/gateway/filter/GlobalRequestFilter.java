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
 * Global filter to handle request tracing and logging.
 * 
 * - Generates a unique request ID for each incoming request
 * - Adds request ID to response headers
 * - Logs request information
 */
@Component
@Slf4j
public class GlobalRequestFilter implements GlobalFilter, Ordered {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String START_TIME = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Record start time
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
        
        // Generate request ID if not present
        ServerHttpRequest request = exchange.getRequest();
        String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        
        // Add request ID to request headers for downstream services
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(REQUEST_ID_HEADER, requestId)
                .build();
        
        // Log request information
        log.info("Incoming request: method={}, path={}, requestId={}, remoteAddress={}",
                mutatedRequest.getMethod(),
                mutatedRequest.getURI().getPath(),
                requestId,
                mutatedRequest.getRemoteAddress());
        
        // Add request ID to response headers
        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .then(Mono.fromRunnable(() -> {
                    // Calculate and log request processing time
                    Long startTime = exchange.getAttribute(START_TIME);
                    if (startTime != null) {
                        long processingTime = System.currentTimeMillis() - startTime;
                        log.info("Request completed: requestId={}, processingTime={}ms, status={}",
                                requestId,
                                processingTime,
                                exchange.getResponse().getStatusCode());
                    }
                    
                    // Add request ID to response headers
                    exchange.getResponse().getHeaders().add(REQUEST_ID_HEADER, requestId);
                }));
    }

    @Override
    public int getOrder() {
        // Execute this filter early in the chain
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
