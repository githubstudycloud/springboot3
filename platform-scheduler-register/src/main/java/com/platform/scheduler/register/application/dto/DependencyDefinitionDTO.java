package com.platform.scheduler.register.application.dto;

import com.platform.scheduler.register.domain.model.dependency.DependencyStatus;
import com.platform.scheduler.register.domain.model.dependency.DependencyType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 依赖关系定义DTO
 * 
 * @author platform
 */
@Data
@Builder
public class DependencyDefinitionDTO {
    
    /**
     * 依赖关系定义ID
     */
    private String id;
    
    /**
     * 源作业ID
     */
    private String sourceJobId;
    
    /**
     * 源作业名称
     */
    private String sourceJobName;
    
    /**
     * 目标作业ID
     */
    private String targetJobId;
    
    /**
     * 目标作业名称
     */
    private String targetJobName;
    
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
    
    /**
     * 依赖状态
     */
    private DependencyStatus status;
    
    /**
     * 依赖状态显示名称
     */
    private String statusDisplayName;
    
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
     * 是否处于活动状态
     */
    private boolean active;
}
