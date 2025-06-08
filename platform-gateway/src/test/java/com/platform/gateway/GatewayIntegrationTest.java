package com.platform.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * 网关集成测试
 * 测试网关的核心功能和端点
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.cloud.nacos.discovery.enabled=false",
    "spring.cloud.nacos.config.enabled=false",
    "spring.data.redis.host=localhost",
    "management.endpoints.web.exposure.include=health,info,metrics,prometheus"
})
class GatewayIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    /**
     * 测试健康检查端点
     */
    @Test
    void testHealthEndpoint() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }
    
    /**
     * 测试网关信息端点
     */
    @Test
    void testInfoEndpoint() {
        webTestClient.get()
                .uri("/actuator/info")
                .exchange()
                .expectStatus().isOk();
    }
    
    /**
     * 测试Prometheus指标端点
     */
    @Test
    void testPrometheusEndpoint() {
        webTestClient.get()
                .uri("/actuator/prometheus")
                .exchange()
                .expectStatus().isOk();
    }
    
    /**
     * 测试网关路由端点
     */
    @Test
    void testGatewayRoutesEndpoint() {
        webTestClient.get()
                .uri("/actuator/gateway/routes")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }
    
    /**
     * 测试降级端点
     */
    @Test
    void testFallbackEndpoint() {
        webTestClient.get()
                .uri("/fallback/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP")
                .jsonPath("$.service").isEqualTo("platform-gateway");
    }
    
    /**
     * 测试无效路径返回404
     */
    @Test
    void testInvalidPath() {
        webTestClient.get()
                .uri("/invalid/path")
                .exchange()
                .expectStatus().isNotFound();
    }
} 