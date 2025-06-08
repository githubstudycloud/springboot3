package com.platform.gateway.controller;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 降级处理控制器
 * 当后端服务不可用时，提供统一的降级响应
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {
    
    /**
     * 配置服务降级处理
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> configFallback(ServerWebExchange exchange) {
        log.warn("配置服务熔断降级，请求路径: {}", exchange.getRequest().getURI());
        
        Map<String, Object> result = createFallbackResponse(
            "配置服务暂时不可用",
            "CONFIG_SERVICE_UNAVAILABLE",
            exchange.getRequest().getURI().getPath()
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }
    
    /**
     * 用户服务降级处理
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> userFallback(ServerWebExchange exchange) {
        log.warn("用户服务熔断降级，请求路径: {}", exchange.getRequest().getURI());
        
        Map<String, Object> result = createFallbackResponse(
            "用户服务暂时不可用",
            "USER_SERVICE_UNAVAILABLE",
            exchange.getRequest().getURI().getPath()
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }
    
    /**
     * 通用服务降级处理
     */
    @GetMapping("/default")
    public ResponseEntity<Map<String, Object>> defaultFallback(ServerWebExchange exchange) {
        log.warn("服务熔断降级，请求路径: {}", exchange.getRequest().getURI());
        
        Map<String, Object> result = createFallbackResponse(
            "服务暂时不可用，请稍后重试",
            "SERVICE_UNAVAILABLE",
            exchange.getRequest().getURI().getPath()
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }
    
    /**
     * 网关健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "platform-gateway");
        result.put("timestamp", LocalDateTime.now());
        result.put("version", "1.0.0");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 创建统一的降级响应格式
     */
    private Map<String, Object> createFallbackResponse(String message, String errorCode, String path) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", 503);
        result.put("message", message);
        result.put("errorCode", errorCode);
        result.put("timestamp", LocalDateTime.now());
        result.put("path", path);
        
        // 添加重试建议
        Map<String, Object> suggestions = new HashMap<>();
        suggestions.put("retryAfter", "30s");
        suggestions.put("contact", "请联系系统管理员");
        suggestions.put("documentation", "/api/docs");
        result.put("suggestions", suggestions);
        
        return result;
    }
} 