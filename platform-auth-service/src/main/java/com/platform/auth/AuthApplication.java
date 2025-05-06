package com.platform.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证与授权服务应用入口
 * <p>
 * 提供用户认证、授权、用户管理和权限管理功能
 * </p>
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthApplication {

    /**
     * 应用程序入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
