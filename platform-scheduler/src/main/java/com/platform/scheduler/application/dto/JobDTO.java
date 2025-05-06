package com.platform.scheduler.application.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业数据传输对象
 * 
 * @author platform
 */
@Data
public class JobDTO {
    
    /**
     * 作业ID
     */
    private String id;
    
    /**
     * 作业名称
     */
    private String name;
    
    /**
     * 作业描述
     */
    private String description;
    
    /**
     * 作业类型
     */
    private String type;
    
    /**
     * 作业状态
     */
    private String status;
    
    /**
     * 作业优先级（1-10）
     */
    private Integer priority;
    
    /**
     * 调度策略类型
     */
    private String scheduleType;
    
    /**
     * Cron表达式（当scheduleType为CRON时有效）
     */
    private String cronExpression;
    
    /**
     * 固定延迟时间（秒）（当scheduleType为FIXED_DELAY时有效）
     */
    private Long fixedDelay;
    
    /**
     * 固定频率时间（秒）（当scheduleType为FIXED_RATE时有效）
     */
    private Long fixedRate;
    
    /**
     * 首次执行时间
     */
    private LocalDateTime startAt;
    
    /**
     * 结束时间
     */
    private LocalDateTime endAt;
    
    /**
     * 最大执行次数
     */
    private Integer maxExecutions;
    
    /**
     * 作业参数
     */
    private Map<String, JobParameterDTO> parameters = new HashMap<>();
    
    /**
     * 作业处理器名称
     */
    private String handlerName;
    
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
     * 作业依赖列表
     */
    private List<JobDependencyDTO> dependencies = new ArrayList<>();
    
    /**
     * 创建者
     */
    private String createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 最后修改者
     */
    private String lastModifiedBy;
    
    /**
     * 最后修改时间
     */
    private LocalDateTime lastModifiedAt;
    
    /**
     * 下次执行时间
     */
    private LocalDateTime nextExecutionTime;
    
    /**
     * 最后执行状态
     */
    private String lastExecutionStatus;
    
    /**
     * 最后执行时间
     */
    private LocalDateTime lastExecutionTime;
}
