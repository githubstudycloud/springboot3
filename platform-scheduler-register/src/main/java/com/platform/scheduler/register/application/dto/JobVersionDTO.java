package com.platform.scheduler.register.application.dto;

import com.platform.scheduler.domain.model.job.JobParameter;
import com.platform.scheduler.domain.model.job.JobType;
import com.platform.scheduler.register.domain.model.version.VersionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作业版本DTO
 * 
 * @author platform
 */
@Data
@Builder
public class JobVersionDTO {
    
    /**
     * 版本ID
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
     * 描述
     */
    private String description;
    
    /**
     * 作业类型
     */
    private JobType type;
    
    /**
     * 作业类型显示名称
     */
    private String typeDisplayName;
    
    /**
     * 处理器名称
     */
    private String handlerName;
    
    /**
     * 版本号
     */
    private Integer version;
    
    /**
     * 调度策略JSON
     */
    private String scheduleStrategyJson;
    
    /**
     * 调度策略描述
     */
    private String scheduleStrategyDescription;
    
    /**
     * 参数列表
     */
    @Builder.Default
    private List<JobParameter> parameters = new ArrayList<>();
    
    /**
     * 参数Map
     */
    private Map<String, String> parameterMap;
    
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
     * 依赖关系列表
     */
    @Builder.Default
    private List<JobVersionDependencyDTO> dependencies = new ArrayList<>();
    
    /**
     * 版本状态
     */
    private VersionStatus status;
    
    /**
     * 版本状态显示名称
     */
    private String statusDisplayName;
    
    /**
     * 是否为当前版本
     */
    private boolean current;
    
    /**
     * 创建者
     */
    private String createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 版本备注
     */
    private String comment;
    
    /**
     * 是否可恢复
     */
    private boolean restorable;
}
