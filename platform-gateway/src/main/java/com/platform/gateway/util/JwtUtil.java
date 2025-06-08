package com.platform.gateway.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * JWT工具类
 * 用于JWT token的生成、解析和验证
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Component
@Slf4j
public class JwtUtil {
    
    @Value("${security.jwt.secret}")
    private String secret;
    
    @Value("${security.jwt.expiration}")
    private Long expiration;
    
    /**
     * 从token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    /**
     * 从token中获取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * 从token中获取指定的claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * 从token中获取所有claims
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            log.warn("JWT解析失败: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * 检查token是否已过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (JwtException e) {
            log.warn("JWT过期检查失败: {}", e.getMessage());
            return true;
        }
    }
    
    /**
     * 验证token
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = getUsernameFromToken(token);
            return (username.equals(tokenUsername) && !isTokenExpired(token));
        } catch (JwtException e) {
            log.warn("JWT验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 验证token是否有效（不检查用户名）
     */
    public Boolean validateToken(String token) {
        try {
            getAllClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (JwtException e) {
            log.warn("JWT验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 从token中获取用户ID
     */
    public String getUserIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("userId", String.class));
    }
    
    /**
     * 从token中获取用户角色
     */
    public String getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }
    
    /**
     * 从token中获取权限列表
     */
    @SuppressWarnings("unchecked")
    public java.util.List<String> getAuthoritiesFromToken(String token) {
        return getClaimFromToken(token, claims -> 
            (java.util.List<String>) claims.get("authorities"));
    }
    
    /**
     * 从Authorization header中提取token
     */
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
    
    /**
     * 检查token格式是否正确
     */
    public Boolean isValidTokenFormat(String token) {
        return token != null && token.split("\\.").length == 3;
    }
} 