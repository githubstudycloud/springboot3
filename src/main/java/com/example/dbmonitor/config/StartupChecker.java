package com.example.dbmonitor.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupChecker {
    
    private final DataSourceManager dataSourceManager;
    
    @PostConstruct
    public void checkEnvironment() {
        log.info("=== Database Monitor Startup Check ===");
        
        Map<String, JdbcTemplate> templates = dataSourceManager.getAllJdbcTemplates();
        
        if (templates.isEmpty()) {
            log.error("No data sources configured! Please check your application.yml");
            return;
        }
        
        templates.forEach((name, template) -> {
            log.info("Checking data source: {}", name);
            checkDataSourceEnvironment(name, template);
        });
        
        log.info("=== Startup Check Complete ===");
    }
    
    private void checkDataSourceEnvironment(String name, JdbcTemplate template) {
        try {
            // 1. 检查连接
            template.queryForObject("SELECT 1", Integer.class);
            log.info("  ✓ Connection successful");
            
            // 2. 检查MySQL/MariaDB版本
            String version = template.queryForObject("SELECT VERSION()", String.class);
            log.info("  ✓ Database Version: {}", version);
            
            // 3. 检查权限
            checkPrivileges(template);
            
            // 4. 检查超时设置
            checkTimeoutSettings(template);
            
        } catch (org.springframework.dao.DataAccessException e) {
            log.error("  ✗ Failed to check {}: {}", name, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("  ✗ Failed to check {} (参数错误): {}", name, e.getMessage());
        }
    }
    
    private void checkPrivileges(JdbcTemplate template) {
        try {
            // 测试PROCESS权限
            template.queryForList("SHOW PROCESSLIST");
            log.info("  ✓ PROCESS privilege: OK");
        } catch (org.springframework.dao.DataAccessException e) {
            log.warn("  ✗ PROCESS privilege: Missing (GRANT PROCESS ON *.* TO user)");
        }
        
        try {
            // 测试information_schema访问
            template.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'information_schema'", 
                Integer.class
            );
            log.info("  ✓ information_schema access: OK");
        } catch (org.springframework.dao.DataAccessException e) {
            log.warn("  ✗ information_schema access: Limited");
        }
    }
    
    private void checkTimeoutSettings(JdbcTemplate template) {
        try {
            Map<String, Object> timeouts = template.queryForMap(
                "SELECT @@wait_timeout as wait_timeout, @@interactive_timeout as interactive_timeout"
            );
            
            Integer waitTimeout = ((Number) timeouts.get("wait_timeout")).intValue();
            Integer interactiveTimeout = ((Number) timeouts.get("interactive_timeout")).intValue();
            
            log.info("  - wait_timeout: {}s", waitTimeout);
            log.info("  - interactive_timeout: {}s", interactiveTimeout);
            
            if (waitTimeout < 300) {
                log.warn("  ⚠ wait_timeout is too low ({}s), recommend at least 300s", waitTimeout);
            }
            
        } catch (org.springframework.dao.DataAccessException e) {
            log.warn("  ⚠ Could not check timeout settings: {}", e.getMessage());
        } catch (ClassCastException e) {
            log.warn("  ⚠ Unexpected data type in timeout settings: {}", e.getMessage());
        } catch (NullPointerException e) {
            log.warn("  ⚠ Null value in timeout settings: {}", e.getMessage());
        }
    }
}