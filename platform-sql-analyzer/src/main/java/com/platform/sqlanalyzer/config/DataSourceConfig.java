package com.platform.sqlanalyzer.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源配置类
 * 支持配置多个数据源，用于查询不同的数据库
 */
@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    /**
     * 默认数据源配置（主要用于查询information_schema）
     */
    @Bean
    @ConfigurationProperties(prefix = "datasource.default")
    public DataSource defaultDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    /**
     * 默认JdbcTemplate
     */
    @Bean
    @Primary
    public JdbcTemplate defaultJdbcTemplate(@Qualifier("defaultDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * 创建动态数据源管理器
     * 用于运行时动态添加数据源
     */
    @Bean
    public DynamicDataSourceManager dynamicDataSourceManager() {
        return new DynamicDataSourceManager();
    }

    /**
     * 动态数据源管理器
     * 支持在运行时添加、移除数据源
     */
    public static class DynamicDataSourceManager {
        private final Map<String, DataSource> dataSources = new HashMap<>();
        private final Map<String, JdbcTemplate> jdbcTemplates = new HashMap<>();

        /**
         * 添加新的数据源
         *
         * @param name 数据源名称
         * @param url JDBC URL
         * @param username 用户名
         * @param password 密码
         * @return 对应的JdbcTemplate
         */
        public synchronized JdbcTemplate addDataSource(String name, String url, String username, String password) {
            if (jdbcTemplates.containsKey(name)) {
                return jdbcTemplates.get(name);
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setMaximumPoolSize(5);
            config.setMinimumIdle(1);
            config.setPoolName("HikariPool-" + name);

            DataSource dataSource = new HikariDataSource(config);
            dataSources.put(name, dataSource);

            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplates.put(name, jdbcTemplate);

            return jdbcTemplate;
        }

        /**
         * 获取指定名称的JdbcTemplate
         */
        public JdbcTemplate getJdbcTemplate(String name) {
            return jdbcTemplates.get(name);
        }
        
        /**
         * 获取指定名称的DataSource
         */
        public DataSource getDataSource(String name) {
            return dataSources.get(name);
        }

        /**
         * 获取所有数据源名称
         */
        public Set<String> getDataSourceNames() {
            return jdbcTemplates.keySet();
        }

        /**
         * 移除数据源
         */
        public synchronized void removeDataSource(String name) {
            JdbcTemplate jdbcTemplate = jdbcTemplates.remove(name);
            DataSource dataSource = dataSources.remove(name);
            if (dataSource instanceof HikariDataSource) {
                ((HikariDataSource) dataSource).close();
            }
        }
    }
}
