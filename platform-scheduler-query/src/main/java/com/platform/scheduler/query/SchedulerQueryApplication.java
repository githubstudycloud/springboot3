package com.platform.scheduler.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 调度查询服务启动类
 * 提供任务执行状态查询、历史记录以及统计分析功能
 * 
 * @author platform
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class SchedulerQueryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerQueryApplication.class, args);
    }
}
