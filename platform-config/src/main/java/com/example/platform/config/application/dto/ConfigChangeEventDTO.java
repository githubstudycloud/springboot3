package com.example.platform.config.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配置变更事件数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigChangeEventDTO {
    
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
    private String eventType;
    
    /**
     * 变更内容摘要
     */
    private String changeSummary;
    
    /**
     * 事件发生时间
     */
    private String occurredTime;
    
    /**
     * 操作人
     */
    private String operator;
}
