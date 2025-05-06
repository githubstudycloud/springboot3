package com.platform.scheduler.domain.model.task;

import com.platform.scheduler.domain.model.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 任务日志条目值对象
 * 记录任务执行过程中的状态变更和重要信息
 * 
 * @author platform
 */
@Getter
@EqualsAndHashCode
@ToString
public class TaskLogEntry implements ValueObject {
    
    /**
     * 日志级别
     */
    private final LogLevel level;
    
    /**
     * 日志消息
     */
    private final String message;
    
    /**
     * 日志时间戳
     */
    private final LocalDateTime timestamp;
    
    public TaskLogEntry(LogLevel level, String message) {
        this(level, message, LocalDateTime.now());
    }
    
    public TaskLogEntry(LogLevel level, String message, LocalDateTime timestamp) {
        if (level == null) {
            throw new IllegalArgumentException("Log level cannot be null");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Log message cannot be null or empty");
        }
        
        this.level = level;
        this.message = message;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }
    
    /**
     * 创建INFO级别日志
     *
     * @param message 日志消息
     * @return 日志条目
     */
    public static TaskLogEntry info(String message) {
        return new TaskLogEntry(LogLevel.INFO, message);
    }
    
    /**
     * 创建WARNING级别日志
     *
     * @param message 日志消息
     * @return 日志条目
     */
    public static TaskLogEntry warn(String message) {
        return new TaskLogEntry(LogLevel.WARNING, message);
    }
    
    /**
     * 创建ERROR级别日志
     *
     * @param message 日志消息
     * @return 日志条目
     */
    public static TaskLogEntry error(String message) {
        return new TaskLogEntry(LogLevel.ERROR, message);
    }
    
    /**
     * 创建DEBUG级别日志
     *
     * @param message 日志消息
     * @return 日志条目
     */
    public static TaskLogEntry debug(String message) {
        return new TaskLogEntry(LogLevel.DEBUG, message);
    }
    
    /**
     * 日志级别枚举
     */
    public enum LogLevel {
        DEBUG("debug", "调试"),
        INFO("info", "信息"),
        WARNING("warning", "警告"),
        ERROR("error", "错误");
        
        private final String code;
        private final String description;
        
        LogLevel(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
