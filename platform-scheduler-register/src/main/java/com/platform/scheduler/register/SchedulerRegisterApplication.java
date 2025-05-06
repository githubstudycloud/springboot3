package com.platform.scheduler.register;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 任务注册模块主应用程序
 * 提供任务注册、配置管理、任务模板和依赖关系管理功能
 * 
 * @author platform
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SchedulerRegisterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerRegisterApplication.class, args);
    }
}
