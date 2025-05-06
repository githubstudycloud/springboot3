package com.platform.auth.infrastructure.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT属性配置类
 * <p>
 * 从application.yml加载JWT相关配置
 * </p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
    /**
     * JWT密钥
     */
    private String secret;
    
    /**
     * 令牌有效期（毫秒）
     */
    private Long expiration;
    
    /**
     * 令牌签发者
     */
    private String issuer;
    
    /**
     * 令牌头部名称
     */
    private String header;
    
    /**
     * 令牌前缀
     */
    private String tokenPrefix;
}
