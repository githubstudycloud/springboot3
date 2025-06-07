package com.platform.config.service;

import com.platform.config.event.ConfigChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 配置刷新服务
 * 专门负责配置的刷新和热更新
 *
 * @author Platform Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigRefreshService {

    private final ContextRefresher contextRefresher;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 刷新指定应用的配置
     *
     * @param application 应用名称
     * @param operator 操作人员
     * @return 刷新的配置键列表
     */
    public Set<String> refreshConfig(String application, String operator) {
        log.info("开始刷新配置，应用: {}, 操作人: {}", application, operator);
        
        try {
            // 执行配置刷新
            Set<String> refreshedKeys = contextRefresher.refresh();
            
            // 发布配置变更事件
            publishConfigChangeEvent(application, "REFRESH", operator, refreshedKeys);
            
            log.info("配置刷新完成，应用: {}, 刷新的配置项数量: {}", application, refreshedKeys.size());
            return refreshedKeys;
            
        } catch (Exception e) {
            log.error("配置刷新失败，应用: {}, 错误: {}", application, e.getMessage(), e);
            
            // 发布配置变更失败事件
            publishConfigChangeEvent(application, "REFRESH_FAILED", operator, null);
            
            throw new RuntimeException("配置刷新失败: " + e.getMessage(), e);
        }
    }

    /**
     * 刷新所有应用配置
     *
     * @param operator 操作人员
     * @return 刷新的配置键列表
     */
    public Set<String> refreshAllConfigs(String operator) {
        log.info("开始刷新所有配置，操作人: {}", operator);
        
        try {
            Set<String> refreshedKeys = contextRefresher.refresh();
            
            // 发布全局配置变更事件
            publishConfigChangeEvent("*", "REFRESH_ALL", operator, refreshedKeys);
            
            log.info("所有配置刷新完成，刷新的配置项数量: {}", refreshedKeys.size());
            return refreshedKeys;
            
        } catch (Exception e) {
            log.error("所有配置刷新失败，错误: {}", e.getMessage(), e);
            
            publishConfigChangeEvent("*", "REFRESH_ALL_FAILED", operator, null);
            
            throw new RuntimeException("所有配置刷新失败: " + e.getMessage(), e);
        }
    }

    /**
     * 发布配置变更事件
     */
    private void publishConfigChangeEvent(String application, String operation, String operator, Set<String> refreshedKeys) {
        try {
            String keysString = refreshedKeys != null ? String.join(",", refreshedKeys) : "";
            
            ConfigChangeEvent event = new ConfigChangeEvent(
                this,
                application,
                "all", // 刷新操作涉及所有profile
                operation,
                "", // 刷新操作没有旧值
                keysString, // 新值为刷新的键列表
                operator
            );
            
            eventPublisher.publishEvent(event);
            log.debug("配置变更事件已发布: {}", event);
            
        } catch (Exception e) {
            log.warn("发布配置变更事件失败: {}", e.getMessage());
        }
    }
} 