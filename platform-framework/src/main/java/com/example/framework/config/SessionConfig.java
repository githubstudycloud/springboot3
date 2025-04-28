package com.example.framework.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 会话配置，启用事务支持
 *
 * @author platform
 * @since 1.0.0
 */
@Configuration
@EnableTransactionManagement
@ConditionalOnProperty(name = "platform.session.jdbc.enabled", havingValue = "true", matchIfMissing = false)
public class SessionConfig {
    // 此类主要用于启用相关注解，无需额外方法
    // 移除了 EnableJdbcHttpSession 注解，因为缺少依赖
}
