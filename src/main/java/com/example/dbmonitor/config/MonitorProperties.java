package com.example.dbmonitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "monitor")
public class MonitorProperties {
    
    private DefaultConfig defaults = new DefaultConfig();
    private List<DataSourceConfig> datasources = new ArrayList<>();
    private AlertConfig alert = new AlertConfig();
    
    @Data
    public static class DefaultConfig {
        private int checkInterval = 60;
        private int sqlTimeoutThreshold = 30;
        private int connectionTimeout = 5000;
        private int poolSize = 3;
        private int validationTimeout = 5000;
        private long keepAliveTime = 600000;
        private int queryTimeout = 10;
    }
    
    @Data
    public static class DataSourceConfig {
        private String name;
        private String url;
        private String username;
        private String password;
        private String driverClassName = "org.mariadb.jdbc.Driver";
        private boolean enabled = true;
        private Integer checkInterval;
        private Integer sqlTimeoutThreshold;
        private Integer connectionTimeout;
        private Integer poolSize;
        private Set<String> tags;
        
        // 获取配置值，如果没有设置则使用默认值
        public int getEffectiveCheckInterval(DefaultConfig defaults) {
            return checkInterval != null ? checkInterval : defaults.getCheckInterval();
        }
        
        public int getEffectiveSqlTimeoutThreshold(DefaultConfig defaults) {
            return sqlTimeoutThreshold != null ? sqlTimeoutThreshold : defaults.getSqlTimeoutThreshold();
        }
        
        public int getEffectiveConnectionTimeout(DefaultConfig defaults) {
            return connectionTimeout != null ? connectionTimeout : defaults.getConnectionTimeout();
        }
        
        public int getEffectivePoolSize(DefaultConfig defaults) {
            return poolSize != null ? poolSize : defaults.getPoolSize();
        }
    }
    
    @Data
    public static class AlertConfig {
        private boolean enabled = true;
        private List<AlertEndpoint> endpoints = new ArrayList<>();
        private List<AlertRule> rules = new ArrayList<>();
    }
    
    @Data
    public static class AlertEndpoint {
        private String name;
        private String url;
        private String type;
        private Set<String> tags;
    }
    
    @Data
    public static class AlertRule {
        private String name;
        private String condition;
        private String severity;
        private String endpoint;
    }
}