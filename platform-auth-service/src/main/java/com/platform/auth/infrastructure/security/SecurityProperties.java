package com.platform.auth.infrastructure.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全属性配置类
 * <p>
 * 从application.yml加载安全相关配置
 * </p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    
    /**
     * 忽略的URL列表（不需要身份验证）
     */
    private List<String> ignoredUrls = new ArrayList<>();
}
