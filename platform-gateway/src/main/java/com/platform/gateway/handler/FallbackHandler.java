package com.platform.gateway.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 熔断降级处理器
 * <p>
 * 当服务熔断时提供统一的降级响应
 * </p>
 */
@Component
public class FallbackHandler implements HandlerFunction<ServerResponse> {

    private static final Logger log = LoggerFactory.getLogger(FallbackHandler.class);

    /**
     * 处理熔断请求
     * <p>
     * 返回标准格式的降级响应
     * </p>
     *
     * @param request 服务请求
     * @return ServerResponse 服务响应
     */
    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        String path = request.path();
        log.error("Gateway service fallback, path: {}", path);
        
        // 构建降级响应数据
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        responseData.put("message", "服务暂时不可用，请稍后再试");
        responseData.put("path", path);
        
        // 返回统一格式的JSON响应
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(responseData));
    }
}
