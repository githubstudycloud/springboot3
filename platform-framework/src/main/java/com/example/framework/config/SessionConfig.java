package com.example.framework.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 会话配置，启用JDBC会话存储和事务支持
 *
 * @author platform
 * @since 1.0.0
 */
@Configuration
@EnableTransactionManagement
@EnableJdbcHttpSession
@ConditionalOnProperty(name = "platform.session.jdbc.enabled", havingValue = "true", matchIfMissing = false)
public class SessionConfig {
    // 此类主要用于启用相关注解，无需额外方法
}
