package com.platform.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 全局请求过滤器
 * <p>
 * 对所有经过网关的请求进行前置处理
 * </p>
 */
@Component
public class GlobalRequestFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GlobalRequestFilter.class);
    
    /**
     * 请求ID的Header名称
     */
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    
    /**
     * 过滤处理
     *
     * @param exchange 当前服务器Web交换
     * @param chain 网关过滤器链
     * @return Mono<Void> 异步结果
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();
        
        // 生成或获取请求ID
        String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }
        
        // 记录请求日志
        log.info("Gateway request: {} {} - {}", method, path, requestId);
        
        // 添加请求ID
        ServerHttpRequest modifiedRequest = request.mutate()
                .header(REQUEST_ID_HEADER, requestId)
                .build();
        
        // 传递修改后的请求
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }
    
    /**
     * 过滤器顺序
     * <p>
     * 值越小优先级越高
     * </p>
     *
     * @return int 顺序值
     */
    @Override
    public int getOrder() {
        return -1; // 确保此过滤器最先执行
    }
}
