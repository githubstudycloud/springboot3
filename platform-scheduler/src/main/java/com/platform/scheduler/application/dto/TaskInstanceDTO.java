package com.platform.scheduler.application.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务实例数据传输对象
 * 
 * @author platform
 */
@Data
public class TaskInstanceDTO {
    
    /**
     * 任务实例ID
     */
    private String id;
    
    /**
     * 作业ID
     */
    private String jobId;
    
    /**
     * 作业名称
     */
    private String jobName;
    
    /**
     * 调度计划
     */
    private String schedulePlan;
    
    /**
     * 执行器ID
     */
    private String executorId;
    
    /**
     * 执行器名称
     */
    private String executorName;
    
    /**
     * 任务状态
     */
    private String status;
    
    /**
     * 重试次数
     */
    private Integer retryCount;
    
    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;
    
    /**
     * 重试间隔（秒）
     */
    private Integer retryInterval;
    
    /**
     * 超时时间（秒）
     */
    private Integer timeout;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    /**
     * 任务参数
     */
    private Map<String, String> parameters = new HashMap<>();
    
    /**
     * 计划开始时间
     */
    private LocalDateTime scheduledStartTime;
    
    /**
     * 实际开始时间
     */
    private LocalDateTime actualStartTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 持续时间（毫秒）
     */
    private Long durationInMillis;
    
    /**
     * 执行结果
     */
    private String result;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 创建者
     */
    private String createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 日志条目列表
     */
    private List<TaskLogEntryDTO> logEntries = new ArrayList<>();
    
    /**
     * 下次重试时间
     */
    private LocalDateTime nextRetryTime;
    
    /**
     * 进度百分比（0-100）
     */
    private Integer progressPercentage;
}
