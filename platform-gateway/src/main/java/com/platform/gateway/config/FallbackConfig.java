package com.platform.gateway.config;

import com.platform.gateway.handler.FallbackHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * 降级路由配置
 * <p>
 * 配置熔断降级的路由规则
 * </p>
 */
@Configuration
public class FallbackConfig {

    /**
     * 降级路由函数
     *
     * @param fallbackHandler 降级处理器
     * @return RouterFunction<ServerResponse> 路由函数
     */
    @Bean
    public RouterFunction<ServerResponse> fallbackRoutes(FallbackHandler fallbackHandler) {
        return RouterFunctions.route(
                RequestPredicates.path("/fallback"), fallbackHandler);
    }
}
