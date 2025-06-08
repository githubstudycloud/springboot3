package com.platform.gateway.config;

import com.platform.gateway.filter.AuthenticationGatewayFilterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关配置类
 * 配置路由规则和过滤器
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Configuration
@Slf4j
public class GatewayConfig {
    
    @Autowired
    private AuthenticationGatewayFilterFactory authenticationGatewayFilterFactory;
    
    /**
     * 自定义路由配置
     * 这里可以补充application.yml中没有的复杂路由规则
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // 健康检查路由 - 无需认证
            .route("health-check", r -> r
                .path("/actuator/health", "/health")
                .uri("forward:/fallback/health"))
                
            // API文档聚合路由
            .route("swagger-aggregation", r -> r
                .path("/swagger-ui/**", "/v3/api-docs/**")
                .filters(f -> f
                    .addRequestHeader("X-Gateway-Routed", "true")
                    .rewritePath("/v3/api-docs/(?<segment>.*)", "/${segment}/v3/api-docs"))
                .uri("lb://platform-config"))
                
            // 监控指标路由 - 需要管理员权限
            .route("metrics-route", r -> r
                .path("/actuator/prometheus", "/actuator/metrics/**")
                .filters(f -> f
                    .filter(authenticationGatewayFilterFactory.apply(
                        new AuthenticationGatewayFilterFactory.Config()))
                    .addRequestHeader("X-Admin-Required", "true"))
                .uri("lb://platform-config"))
                
            .build();
    }
} 