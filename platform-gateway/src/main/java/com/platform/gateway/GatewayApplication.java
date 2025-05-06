package com.platform.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API网关应用入口
 * <p>
 * 基于Spring Cloud Gateway实现，提供路由、限流、熔断等能力
 * </p>
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {
    
    /**
     * 应用程序入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
