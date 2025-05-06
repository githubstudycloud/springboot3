package com.platform.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 平台监控仪表板应用程序
 * 
 * @author platform
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class MonitorDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorDashboardApplication.class, args);
    }
}
