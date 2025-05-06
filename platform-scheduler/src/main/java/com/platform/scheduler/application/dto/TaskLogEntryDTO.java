package com.platform.scheduler.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务日志条目数据传输对象
 * 
 * @author platform
 */
@Data
public class TaskLogEntryDTO {
    
    /**
     * 日志级别
     */
    private String level;
    
    /**
     * 日志消息
     */
    private String message;
    
    /**
     * 日志时间戳
     */
    private LocalDateTime timestamp;
}
