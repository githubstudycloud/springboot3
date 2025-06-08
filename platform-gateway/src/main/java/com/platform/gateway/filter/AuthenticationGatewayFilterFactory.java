package com.platform.gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.platform.gateway.util.JwtUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * JWT认证网关过滤器工厂
 * 负责验证JWT token并设置用户信息到请求头
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Component
@Slf4j
public class AuthenticationGatewayFilterFactory 
    extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Value("#{'${security.whitelist.paths}'.split(',')}")
    private List<String> whitelistPaths;
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    public AuthenticationGatewayFilterFactory() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            
            String path = request.getURI().getPath();
            log.debug("处理请求路径: {}", path);
            
            // 检查是否在白名单中
            if (isWhitelistPath(path)) {
                log.debug("白名单路径，跳过认证: {}", path);
                return chain.filter(exchange);
            }
            
            // 获取Authorization header
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || authHeader.isEmpty()) {
                log.warn("缺少Authorization header，路径: {}", path);
                return handleUnauthorized(response, "缺少认证信息");
            }
            
            // 提取JWT token
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null || !jwtUtil.isValidTokenFormat(token)) {
                log.warn("无效的token格式，路径: {}", path);
                return handleUnauthorized(response, "无效的token格式");
            }
            
            // 验证JWT token
            if (!jwtUtil.validateToken(token)) {
                log.warn("token验证失败，路径: {}", path);
                return handleUnauthorized(response, "token验证失败");
            }
            
            try {
                // 提取用户信息
                String username = jwtUtil.getUsernameFromToken(token);
                String userId = jwtUtil.getUserIdFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);
                List<String> authorities = jwtUtil.getAuthoritiesFromToken(token);
                
                log.debug("认证成功，用户: {}, 角色: {}", username, role);
                
                // 构建新的请求，添加用户信息到header
                ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId != null ? userId : "")
                    .header("X-Username", username != null ? username : "")
                    .header("X-User-Role", role != null ? role : "")
                    .header("X-User-Authorities", authorities != null ? String.join(",", authorities) : "")
                    .build();
                
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
                
            } catch (Exception e) {
                log.error("处理认证信息时发生错误，路径: {}, 错误: {}", path, e.getMessage());
                return handleUnauthorized(response, "认证信息处理失败");
            }
        };
    }
    
    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhitelistPath(String path) {
        return whitelistPaths.stream()
            .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
    
    /**
     * 处理未授权响应
     */
    private Mono<Void> handleUnauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> result = Map.of(
            "code", 401,
            "message", message,
            "timestamp", System.currentTimeMillis(),
            "path", response.getHeaders().getFirst("X-Request-Path")
        );
        
        String body = JSON.toJSONString(result);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        
        return response.writeWith(Mono.just(buffer));
    }
    
    @Data
    public static class Config {
        // 配置类，目前为空，后续可以添加配置参数
        private boolean enabled = true;
        private String message = "Authentication required";
    }
} 