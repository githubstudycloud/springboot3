package com.example.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 平台配置属性
 *
 * @author platform
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "platform")
public class PlatformProperties {

    /**
     * 线程配置
     */
    private final ThreadConfig threadConfig = new ThreadConfig();
    /**
     * 会话配置
     */
    private final SessionConfig sessionConfig = new SessionConfig();
    /**
     * 是否启用平台功能
     */
    @SuppressWarnings("unused")
    private boolean enabled = true;

    /**
     * 线程配置类
     */
    @Data
    public static class ThreadConfig {
        /**
         * 虚拟线程配置
         */
        private final VirtualThreadConfig virtualConfig = new VirtualThreadConfig();

        /**
         * 虚拟线程配置类
         */
        @Data
        public static class VirtualThreadConfig {
            /**
             * 是否启用虚拟线程
             */
            @SuppressWarnings("unused")
            private boolean enabled = false;
        }
    }

    /**
     * 会话配置类
     */
    @Data
    public static class SessionConfig {
        /**
         * JDBC会话配置
         */
        private final JdbcSessionConfig jdbcConfig = new JdbcSessionConfig();

        /**
         * JDBC会话配置类
         */
        @Data
        public static class JdbcSessionConfig {
            /**
             * 是否启用JDBC会话
             */
            @SuppressWarnings("unused")
            private boolean enabled = false;
        }
    }
}