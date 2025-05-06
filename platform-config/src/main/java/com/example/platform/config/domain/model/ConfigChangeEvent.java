package com.example.platform.config.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 配置通知事件领域模型
 * 
 * <p>当配置变更时，生成通知事件，用于推送给订阅服务</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigChangeEvent {
    /**
     * 事件ID
     */
    private Long id;
    
    /**
     * 配置项dataId
     */
    private String dataId;
    
    /**
     * 配置项分组
     */
    private String group;
    
    /**
     * 环境标识：dev, test, prod等
     */
    private String environment;
    
    /**
     * 事件类型：CREATE, UPDATE, DELETE
     */
    private EventType eventType;
    
    /**
     * 变更内容摘要
     */
    private String changeSummary;
    
    /**
     * 事件发生时间
     */
    private LocalDateTime occurredTime;
    
    /**
     * 操作人
     */
    private String operator;
    
    /**
     * 事件类型枚举
     */
    public enum EventType {
        /**
         * 创建配置
         */
        CREATE,
        
        /**
         * 更新配置
         */
        UPDATE,
        
        /**
         * 删除配置
         */
        DELETE
    }
    
    /**
     * 创建配置新增事件
     *
     * @param configItem 配置项
     * @param operator 操作人
     * @return 配置变更事件
     */
    public static ConfigChangeEvent createEvent(ConfigItem configItem, String operator) {
        return ConfigChangeEvent.builder()
                .dataId(configItem.getDataId())
                .group(configItem.getGroup())
                .environment(configItem.getEnvironment())
                .eventType(EventType.CREATE)
                .changeSummary("新增配置: " + configItem.getDataId())
                .occurredTime(LocalDateTime.now())
                .operator(operator)
                .build();
    }
    
    /**
     * 创建配置更新事件
     *
     * @param configItem 配置项
     * @param operator 操作人
     * @return 配置变更事件
     */
    public static ConfigChangeEvent updateEvent(ConfigItem configItem, String operator) {
        return ConfigChangeEvent.builder()
                .dataId(configItem.getDataId())
                .group(configItem.getGroup())
                .environment(configItem.getEnvironment())
                .eventType(EventType.UPDATE)
                .changeSummary("更新配置: " + configItem.getDataId())
                .occurredTime(LocalDateTime.now())
                .operator(operator)
                .build();
    }
    
    /**
     * 创建配置删除事件
     *
     * @param configItem 配置项
     * @param operator 操作人
     * @return 配置变更事件
     */
    public static ConfigChangeEvent deleteEvent(ConfigItem configItem, String operator) {
        return ConfigChangeEvent.builder()
                .dataId(configItem.getDataId())
                .group(configItem.getGroup())
                .environment(configItem.getEnvironment())
                .eventType(EventType.DELETE)
                .changeSummary("删除配置: " + configItem.getDataId())
                .occurredTime(LocalDateTime.now())
                .operator(operator)
                .build();
    }
}
