package com.platform.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 注册中心应用入口
 * <p>
 * 基于Nacos实现的服务注册与发现中心
 * </p>
 */
@SpringBootApplication
@EnableDiscoveryClient
public class RegistryApplication {

    /**
     * 应用程序入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(RegistryApplication.class, args);
    }
}
