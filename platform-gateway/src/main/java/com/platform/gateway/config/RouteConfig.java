package com.platform.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 路由配置
 * <p>
 * 定义API网关的路由规则，支持代码方式和配置方式同时工作
 * </p>
 */
@Configuration
public class RouteConfig {

    /**
     * 路由定位器
     * <p>
     * 定义路由规则，可与配置文件中的路由规则共存
     * </p>
     *
     * @param builder 路由构建器
     * @return RouteLocator 路由定位器
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 健康检查路由
                .route("health_route", r -> r.path("/actuator/**")
                        .uri("lb://platform-gateway"))
                
                // 示例：带有熔断和超时的路由
                .route("example_service_route", r -> r.path("/example/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("exampleCircuitBreaker")
                                        .setFallbackUri("forward:/fallback"))
                                .rewritePath("/example/(?<segment>.*)", "/${segment}"))
                        .uri("lb://example-service"))
                        
                // 可以根据需要添加更多路由
                
                .build();
    }
}
