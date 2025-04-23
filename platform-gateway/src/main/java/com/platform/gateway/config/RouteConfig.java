package com.platform.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for API Gateway routes.
 * 
 * Defines routes to various microservices, including:
 * - Business dashboard
 * - Monitoring dashboard
 * - Data collection
 * - Core services
 * - Scheduler services
 */
@Configuration
public class RouteConfig {

    /**
     * Configure routes for the API Gateway.
     * 
     * @param builder RouteLocatorBuilder
     * @return RouteLocator
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Business Dashboard
                .route("platform-buss-dashboard", r -> r.path("/api/dashboard/**")
                        .filters(f -> f.stripPrefix(2)
                                .retry(config -> config.setRetries(3)
                                        .setMethods("GET")
                                        .setBackoff(50, 500, 2, true)))
                        .uri("lb://platform-buss-dashboard"))
                
                // Monitoring Dashboard
                .route("platform-monitor-dashboard", r -> r.path("/api/monitor/**")
                        .filters(f -> f.stripPrefix(2)
                                .retry(config -> config.setRetries(2)
                                        .setMethods("GET")
                                        .setBackoff(50, 500, 2, true)))
                        .uri("lb://platform-monitor-dashboard"))
                
                // Data Collection Service
                .route("platform-collect", r -> r.path("/api/collect/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://platform-collect"))
                
                // Core Processing Service
                .route("platform-fluxcore", r -> r.path("/api/flux/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://platform-fluxcore"))
                
                // Scheduler Service
                .route("platform-scheduler", r -> r.path("/api/scheduler/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://platform-scheduler"))
                
                // Scheduler Register Service
                .route("platform-scheduler-register", r -> r.path("/api/scheduler/register/**")
                        .filters(f -> f.stripPrefix(3))
                        .uri("lb://platform-scheduler-register"))
                
                // Scheduler Query Service
                .route("platform-scheduler-query", r -> r.path("/api/scheduler/query/**")
                        .filters(f -> f.stripPrefix(3))
                        .uri("lb://platform-scheduler-query"))
                
                // Fallback route
                .route("fallback", r -> r.path("/**")
                        .filters(f -> f.setFallbackUri("forward:/fallback"))
                        .uri("lb://platform-gateway"))
                
                .build();
    }
}
