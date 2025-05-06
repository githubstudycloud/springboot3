package com.platform.scheduler.register.application.dto;

import com.platform.scheduler.register.domain.model.version.JobVersionDependency.DependencyType;
import lombok.Builder;
import lombok.Data;

/**
 * 作业版本依赖DTO
 * 
 * @author platform
 */
@Data
@Builder
public class JobVersionDependencyDTO {
    
    /**
     * 依赖作业ID
     */
    private String dependencyJobId;
    
    /**
     * 依赖作业名称
     */
    private String dependencyJobName;
    
    /**
     * 依赖类型
     */
    private DependencyType type;
    
    /**
     * 依赖类型显示名称
     */
    private String typeDisplayName;
    
    /**
     * 条件表达式
     */
    private String condition;
    
    /**
     * 描述
     */
    private String description;
}
