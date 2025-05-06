package com.platform.scheduler.register.domain.model.template;

import com.platform.scheduler.domain.model.job.JobParameter;
import com.platform.scheduler.domain.model.job.JobType;
import com.platform.scheduler.register.domain.event.template.JobTemplateCreatedEvent;
import com.platform.scheduler.register.domain.event.template.JobTemplateUpdatedEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业模板聚合根
 * 定义了一类作业的通用配置，可用于快速创建具体的作业实例
 * 
 * @author platform
 */
@Getter
public class JobTemplate {
    
    private final JobTemplateId id;
    private String name;
    private String description;
    private JobType type;
    private String handlerName;
    private TemplateCategory category;
    private final Map<String, JobParameterTemplate> parameterTemplates;
    private final List<String> labels;
    private TemplateStatus status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedAt;
    private boolean isBuiltIn;
    
    /**
     * 私有构造方法，通过Builder创建
     */
    private JobTemplate(Builder builder) {
        this.id = builder.id != null ? builder.id : JobTemplateId.newId();
        this.name = builder.name;
        this.description = builder.description;
        this.type = builder.type;
        this.handlerName = builder.handlerName;
        this.category = builder.category;
        this.parameterTemplates = new HashMap<>(builder.parameterTemplates);
        this.labels = new ArrayList<>(builder.labels);
        this.status = builder.status != null ? builder.status : TemplateStatus.DRAFT;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.lastModifiedBy = builder.lastModifiedBy;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.isBuiltIn = builder.isBuiltIn;
        
        validate();
    }
    
    /**
     * 验证模板的完整性和有效性
     */
    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Template name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Template job type cannot be null");
        }
        if (handlerName == null || handlerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Template handler name cannot be null or empty");
        }
        if (category == null) {
            throw new IllegalArgumentException("Template category cannot be null");
        }
    }
    
    /**
     * 更新模板基本信息
     *
     * @param name 名称
     * @param description 描述
     * @param category 分类
     * @param labels 标签列表
     * @param modifier 修改者
     */
    public void updateBasicInfo(String name, String description, TemplateCategory category, 
                                List<String> labels, String modifier) {
        if (this.status == TemplateStatus.DISABLED || this.isBuiltIn) {
            throw new IllegalStateException("Cannot update disabled or built-in template");
        }
        
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (category != null) {
            this.category = category;
        }
        if (labels != null) {
            this.labels.clear();
            this.labels.addAll(labels);
        }
        
        updateModificationInfo(modifier);
    }
    
    /**
     * 添加参数模板
     *
     * @param parameterTemplate 参数模板
     * @param modifier 修改者
     */
    public void addParameterTemplate(JobParameterTemplate parameterTemplate, String modifier) {
        if (this.status == TemplateStatus.DISABLED || this.isBuiltIn) {
            throw new IllegalStateException("Cannot update disabled or built-in template");
        }
        
        if (parameterTemplate == null) {
            throw new IllegalArgumentException("Parameter template cannot be null");
        }
        
        this.parameterTemplates.put(parameterTemplate.getName(), parameterTemplate);
        updateModificationInfo(modifier);
    }
    
    /**
     * 移除参数模板
     *
     * @param parameterName 参数名称
     * @param modifier 修改者
     * @return 是否成功移除
     */
    public boolean removeParameterTemplate(String parameterName, String modifier) {
        if (this.status == TemplateStatus.DISABLED || this.isBuiltIn) {
            throw new IllegalStateException("Cannot update disabled or built-in template");
        }
        
        if (parameterName == null || parameterName.trim().isEmpty()) {
            return false;
        }
        
        JobParameterTemplate removed = this.parameterTemplates.remove(parameterName);
        if (removed != null) {
            updateModificationInfo(modifier);
            return true;
        }
        return false;
    }
    
    /**
     * 发布模板
     *
     * @param modifier 修改者
     */
    public void publish(String modifier) {
        if (this.status == TemplateStatus.PUBLISHED) {
            return;
        }
        
        if (this.status == TemplateStatus.DISABLED) {
            throw new IllegalStateException("Cannot publish disabled template");
        }
        
        this.status = TemplateStatus.PUBLISHED;
        updateModificationInfo(modifier);
    }
    
    /**
     * 禁用模板
     *
     * @param modifier 修改者
     */
    public void disable(String modifier) {
        if (this.status == TemplateStatus.DISABLED) {
            return;
        }
        
        if (this.isBuiltIn) {
            throw new IllegalStateException("Cannot disable built-in template");
        }
        
        this.status = TemplateStatus.DISABLED;
        updateModificationInfo(modifier);
    }
    
    /**
     * 获取参数模板列表
     *
     * @return 参数模板列表
     */
    public List<JobParameterTemplate> getParameterTemplateList() {
        return Collections.unmodifiableList(new ArrayList<>(parameterTemplates.values()));
    }
    
    /**
     * 从模板创建作业参数
     *
     * @return 作业参数列表
     */
    public List<JobParameter> createJobParameters() {
        List<JobParameter> parameters = new ArrayList<>();
        for (JobParameterTemplate template : parameterTemplates.values()) {
            parameters.add(JobParameter.builder()
                    .withName(template.getName())
                    .withDisplayName(template.getDisplayName())
                    .withDescription(template.getDescription())
                    .withType(template.getType())
                    .withDefaultValue(template.getDefaultValue())
                    .withRequired(template.isRequired())
                    .build());
        }
        return parameters;
    }
    
    /**
     * 更新修改信息
     *
     * @param modifier 修改者
     */
    private void updateModificationInfo(String modifier) {
        this.lastModifiedBy = modifier;
        this.lastModifiedAt = LocalDateTime.now();
    }
    
    /**
     * 判断模板是否可用
     *
     * @return 如果模板状态为已发布，则返回true
     */
    public boolean isAvailable() {
        return this.status == TemplateStatus.PUBLISHED;
    }
    
    /**
     * 获取构建器
     *
     * @return 作业模板构建器
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 作业模板构建器
     */
    public static class Builder {
        private JobTemplateId id;
        private String name;
        private String description;
        private JobType type;
        private String handlerName;
        private TemplateCategory category;
        private final Map<String, JobParameterTemplate> parameterTemplates = new HashMap<>();
        private final List<String> labels = new ArrayList<>();
        private TemplateStatus status;
        private String createdBy;
        private LocalDateTime createdAt;
        private String lastModifiedBy;
        private LocalDateTime lastModifiedAt;
        private boolean isBuiltIn;
        
        public Builder withId(JobTemplateId id) {
            this.id = id;
            return this;
        }
        
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }
        
        public Builder withType(JobType type) {
            this.type = type;
            return this;
        }
        
        public Builder withHandlerName(String handlerName) {
            this.handlerName = handlerName;
            return this;
        }
        
        public Builder withCategory(TemplateCategory category) {
            this.category = category;
            return this;
        }
        
        public Builder withParameterTemplate(JobParameterTemplate parameterTemplate) {
            if (parameterTemplate != null) {
                this.parameterTemplates.put(parameterTemplate.getName(), parameterTemplate);
            }
            return this;
        }
        
        public Builder withParameterTemplates(List<JobParameterTemplate> parameterTemplates) {
            if (parameterTemplates != null) {
                for (JobParameterTemplate template : parameterTemplates) {
                    this.parameterTemplates.put(template.getName(), template);
                }
            }
            return this;
        }
        
        public Builder withLabel(String label) {
            if (label != null && !label.trim().isEmpty()) {
                this.labels.add(label);
            }
            return this;
        }
        
        public Builder withLabels(List<String> labels) {
            if (labels != null) {
                this.labels.addAll(labels);
            }
            return this;
        }
        
        public Builder withStatus(TemplateStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder withCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }
        
        public Builder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder withLastModifiedBy(String lastModifiedBy) {
            this.lastModifiedBy = lastModifiedBy;
            return this;
        }
        
        public Builder withLastModifiedAt(LocalDateTime lastModifiedAt) {
            this.lastModifiedAt = lastModifiedAt;
            return this;
        }
        
        public Builder isBuiltIn(boolean isBuiltIn) {
            this.isBuiltIn = isBuiltIn;
            return this;
        }
        
        public JobTemplate build() {
            return new JobTemplate(this);
        }
    }
}
