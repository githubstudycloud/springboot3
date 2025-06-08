package com.platform.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * 网关应用集成测试
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@SpringBootTest(
    classes = GatewayApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.cloud.nacos.discovery.enabled=false",
    "spring.cloud.nacos.config.enabled=false",
    "management.endpoints.web.exposure.include=health,info"
})
class GatewayApplicationTest {
    
    @Test
    void contextLoads() {
        // 测试Spring上下文是否能正常加载
    }
} 