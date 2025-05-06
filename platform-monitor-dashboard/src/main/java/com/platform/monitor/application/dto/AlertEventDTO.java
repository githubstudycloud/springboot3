package com.platform.monitor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 告警事件DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertEventDTO {
    
    /**
     * 事件类型
     */
    private String eventType;
    
    /**
     * 事件名称
     */
    private String eventName;
    
    /**
     * 操作人
     */
    private String operator;
    
    /**
     * 事件描述
     */
    private String description;
    
    /**
     * 事件时间
     */
    private LocalDateTime eventTime;
}
