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
     * 是否启用平台功能
     */
    private boolean enabled = true;

    /**
     * 线程配置
     */
    private final Thread thread = new Thread();

    /**
     * 会话配置
     */
    private final Session session = new Session();

    /**
     * 线程配置类
     */
    @Data
    public static class Thread {
        /**
         * 虚拟线程配置
         */
        private final Virtual virtual = new Virtual();

        /**
         * 虚拟线程配置类
         */
        @Data
        public static class Virtual {
            /**
             * 是否启用虚拟线程
             */
            private boolean enabled = false;
        }
    }

    /**
     * 会话配置类
     */
    @Data
    public static class Session {
        /**
         * JDBC会话配置
         */
        private final Jdbc jdbc = new Jdbc();

        /**
         * JDBC会话配置类
         */
        @Data
        public static class Jdbc {
            /**
             * 是否启用JDBC会话
             */
            private boolean enabled = false;
        }
    }
}