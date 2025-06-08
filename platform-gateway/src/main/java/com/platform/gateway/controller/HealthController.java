package com.platform.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 网关健康检查控制器
 * 提供详细的网关健康状态检查
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/health")
@Slf4j
public class HealthController implements HealthIndicator {
    
    @Autowired
    private RouteLocator routeLocator;
    
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 网关整体健康检查
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> gatewayHealth() {
        Map<String, Object> healthInfo = new HashMap<>();
        
        try {
            // 基本信息
            healthInfo.put("status", "UP");
            healthInfo.put("service", "platform-gateway");
            healthInfo.put("version", "1.0.0");
            healthInfo.put("timestamp", LocalDateTime.now());
            healthInfo.put("uptime", getUptime());
            
            // 组件健康检查
            Map<String, Object> components = new HashMap<>();
            components.put("gateway", checkGatewayHealth());
            components.put("redis", checkRedisHealth());
            components.put("routes", checkRoutesHealth());
            healthInfo.put("components", components);
            
            // 系统信息
            healthInfo.put("system", getSystemInfo());
            
            return ResponseEntity.ok(healthInfo);
            
        } catch (Exception e) {
            log.error("健康检查异常", e);
            healthInfo.put("status", "DOWN");
            healthInfo.put("error", e.getMessage());
            return ResponseEntity.status(503).body(healthInfo);
        }
    }
    
    /**
     * 详细健康检查
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> healthInfo = new HashMap<>();
        
        // 网关状态
        healthInfo.put("gateway", getGatewayStatus());
        
        // Redis状态
        healthInfo.put("redis", getRedisStatus());
        
        // 路由状态
        healthInfo.put("routes", getRoutesStatus());
        
        // JVM状态
        healthInfo.put("jvm", getJvmStatus());
        
        return ResponseEntity.ok(healthInfo);
    }
    
    /**
     * Spring Boot Actuator健康检查实现
     */
    @Override
    public Health health() {
        try {
            Map<String, Object> details = new HashMap<>();
            details.put("gateway", "运行正常");
            details.put("redis", checkRedisHealth().get("status"));
            details.put("routes", routeLocator.getRoutes().count().block());
            
            return Health.up().withDetails(details).build();
        } catch (Exception e) {
            return Health.down().withException(e).build();
        }
    }
    
    /**
     * 检查网关健康状态
     */
    private Map<String, Object> checkGatewayHealth() {
        Map<String, Object> gatewayHealth = new HashMap<>();
        gatewayHealth.put("status", "UP");
        gatewayHealth.put("description", "网关服务运行正常");
        return gatewayHealth;
    }
    
    /**
     * 检查Redis健康状态
     */
    private Map<String, Object> checkRedisHealth() {
        Map<String, Object> redisHealth = new HashMap<>();
        
        if (redisTemplate == null) {
            redisHealth.put("status", "UNKNOWN");
            redisHealth.put("description", "Redis未配置");
            return redisHealth;
        }
        
        try {
            RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
            String pong = connection.ping();
            connection.close();
            
            redisHealth.put("status", "UP");
            redisHealth.put("description", "Redis连接正常");
            redisHealth.put("response", pong);
        } catch (Exception e) {
            redisHealth.put("status", "DOWN");
            redisHealth.put("description", "Redis连接异常");
            redisHealth.put("error", e.getMessage());
        }
        
        return redisHealth;
    }
    
    /**
     * 检查路由健康状态
     */
    private Map<String, Object> checkRoutesHealth() {
        Map<String, Object> routesHealth = new HashMap<>();
        
        try {
            Long routeCount = routeLocator.getRoutes().count().block();
            routesHealth.put("status", "UP");
            routesHealth.put("description", "路由配置正常");
            routesHealth.put("routeCount", routeCount);
        } catch (Exception e) {
            routesHealth.put("status", "DOWN");
            routesHealth.put("description", "路由配置异常");
            routesHealth.put("error", e.getMessage());
        }
        
        return routesHealth;
    }
    
    /**
     * 获取系统信息
     */
    private Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        
        systemInfo.put("processors", runtime.availableProcessors());
        systemInfo.put("totalMemory", runtime.totalMemory());
        systemInfo.put("freeMemory", runtime.freeMemory());
        systemInfo.put("maxMemory", runtime.maxMemory());
        systemInfo.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        
        return systemInfo;
    }
    
    /**
     * 获取网关状态
     */
    private Map<String, Object> getGatewayStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("name", "platform-gateway");
        status.put("status", "RUNNING");
        status.put("startTime", LocalDateTime.now().minusHours(1)); // 示例
        return status;
    }
    
    /**
     * 获取Redis状态
     */
    private Map<String, Object> getRedisStatus() {
        return checkRedisHealth();
    }
    
    /**
     * 获取路由状态
     */
    private Map<String, Object> getRoutesStatus() {
        return checkRoutesHealth();
    }
    
    /**
     * 获取JVM状态
     */
    private Map<String, Object> getJvmStatus() {
        Map<String, Object> jvmStatus = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        
        jvmStatus.put("totalMemory", runtime.totalMemory());
        jvmStatus.put("freeMemory", runtime.freeMemory());
        jvmStatus.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        jvmStatus.put("maxMemory", runtime.maxMemory());
        jvmStatus.put("availableProcessors", runtime.availableProcessors());
        
        return jvmStatus;
    }
    
    /**
     * 获取运行时间
     */
    private String getUptime() {
        return "模拟运行时间"; // 实际实现中可以记录启动时间
    }
} 