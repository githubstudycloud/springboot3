package com.example.framework.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 平台自动配置类
 * 启用DDD架构和相关功能
 *
 * @author platform
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(PlatformProperties.class)
@ComponentScan({
    "com.example.framework.application",
    "com.example.framework.domain",
    "com.example.framework.infrastructure",
    "com.example.framework.interfaces"
})
@ConditionalOnProperty(name = "platform.enabled", havingValue = "true", matchIfMissing = true)
public class PlatformAutoConfiguration {
    // 配置逻辑
}
