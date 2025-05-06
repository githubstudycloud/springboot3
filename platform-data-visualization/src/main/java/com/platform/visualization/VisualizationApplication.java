package com.platform.visualization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 数据可视化模块主应用程序
 */
@SpringBootApplication
@EnableScheduling
public class VisualizationApplication {

    public static void main(String[] args) {
        SpringApplication.run(VisualizationApplication.class, args);
    }
}
