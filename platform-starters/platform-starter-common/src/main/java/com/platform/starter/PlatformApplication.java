package com.platform.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 平台通用启动器
 * 提供统一的应用启动逻辑和配置
 */
@SpringBootApplication
@EnableDiscoveryClient
public class PlatformApplication {

    /**
     * 通用启动方法
     * @param primarySource 主启动类
     * @param args 启动参数
     * @return 应用上下文
     */
    public static ConfigurableApplicationContext run(Class<?> primarySource, String[] args) {
        return run(new Class[]{primarySource}, args);
    }

    /**
     * 通用启动方法（多源）
     * @param primarySources 主启动类数组
     * @param args 启动参数
     * @return 应用上下文
     */
    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
        // 自定义应用构建器
        SpringApplicationBuilder builder = new SpringApplicationBuilder(primarySources);
        
        // 设置自定义Banner
        builder.banner(new PlatformBanner());
        
        // 添加启动监听器
        builder.listeners(new PlatformApplicationStartupListener());
        
        // 构建并运行应用
        SpringApplication app = builder.build();
        
        // 启动应用
        ConfigurableApplicationContext context = app.run(args);
        
        // 打印启动信息
        logStartupInfo(context);
        
        return context;
    }

    /**
     * 打印启动信息
     */
    private static void logStartupInfo(ConfigurableApplicationContext context) {
        Environment env = context.getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        
        String serverPort = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String hostAddress = "localhost";
        
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // 忽略异常，使用默认值
        }
        
        String appName = env.getProperty("spring.application.name", "Platform Application");
        String profile = String.join(",", env.getActiveProfiles());
        if (profile.isEmpty()) {
            profile = "default";
        }
        
        System.out.println("\n----------------------------------------------------------");
        System.out.println("\t🚀 " + appName + " 启动成功!");
        System.out.println("\t📱 应用名称: " + appName);
        System.out.println("\t🌍 环境配置: " + profile);
        System.out.println("\t🏠 本地访问: " + protocol + "://localhost:" + serverPort + contextPath);
        System.out.println("\t🌐 网络访问: " + protocol + "://" + hostAddress + ":" + serverPort + contextPath);
        if (env.getProperty("spring.cloud.nacos.discovery.server-addr") != null) {
            System.out.println("\t📡 注册中心: " + env.getProperty("spring.cloud.nacos.discovery.server-addr"));
        }
        if (env.getProperty("management.endpoints.web.exposure.include") != null) {
            System.out.println("\t📊 监控端点: " + protocol + "://" + hostAddress + ":" + serverPort + contextPath + "/actuator");
        }
        System.out.println("----------------------------------------------------------\n");
    }

    /**
     * 标准main方法
     */
    public static void main(String[] args) {
        run(PlatformApplication.class, args);
    }
} 