package com.example.platform.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 配置中心应用启动类
 * 
 * <p>基于Nacos实现的配置中心，支持多环境配置管理、配置加密解密、配置变更推送等功能</p>
 *
 * @author example
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
    }
}
