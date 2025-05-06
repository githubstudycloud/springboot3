package com.platform.scheduler.register.application.dto;

import com.platform.scheduler.domain.model.job.JobType;
import com.platform.scheduler.register.domain.model.template.TemplateCategory;
import com.platform.scheduler.register.domain.model.template.TemplateStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 作业模板DTO
 * 
 * @author platform
 */
@Data
@Builder
public class JobTemplateDTO {
    
    /**
     * 模板ID
     */
    private String id;
    
    /**
     * 模板名称
     */
    private String name;
    
    /**
     * 模板描述
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
     * 模板分类
     */
    private TemplateCategory category;
    
    /**
     * 模板分类显示名称
     */
    private String categoryDisplayName;
    
    /**
     * 参数模板列表
     */
    @Builder.Default
    private List<JobParameterTemplateDTO> parameterTemplates = new ArrayList<>();
    
    /**
     * 标签列表
     */
    @Builder.Default
    private List<String> labels = new ArrayList<>();
    
    /**
     * 模板状态
     */
    private TemplateStatus status;
    
    /**
     * 模板状态显示名称
     */
    private String statusDisplayName;
    
    /**
     * 是否为内置模板
     */
    private boolean builtIn;
    
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
     * 是否可编辑
     */
    private boolean editable;
    
    /**
     * 是否可用
     */
    private boolean available;
    
    /**
     * 引用次数
     */
    private long usageCount;
}
