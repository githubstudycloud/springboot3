package com.example.framework.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 平台自动配置类
 * 集中导入和管理平台核心组件
 */
@Configuration
@ComponentScan(basePackages = {"com.example.common", "com.example.framework"})
@Import({
    JacksonConfig.class,
    WebMvcConfig.class,
    WebFluxConfig.class,
    GlobalExceptionHandler.class
})
public class PlatformAutoConfiguration {
    
    // 自动配置逻辑
    
} 