package com.platform.scheduler.model;

import java.util.Date;

import lombok.Data;

/**
 * 任务日志实体类
 * 
 * @author platform
 */
@Data
public class TaskLog {
    
    /**
     * 日志ID
     */
    private Long id;
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 执行ID
     */
    private Long executionId;
    
    /**
     * 节点ID
     */
    private String nodeId;
    
    /**
     * 日志级别(INFO/WARN/ERROR)
     */
    private String level;
    
    /**
     * 日志内容
     */
    private String message;
    
    /**
     * 创建时间
     */
    private Date createdTime;
    
    /**
     * 判断是否为INFO级别
     */
    public boolean isInfo() {
        return "INFO".equals(level);
    }
    
    /**
     * 判断是否为WARN级别
     */
    public boolean isWarn() {
        return "WARN".equals(level);
    }
    
    /**
     * 判断是否为ERROR级别
     */
    public boolean isError() {
        return "ERROR".equals(level);
    }
}
