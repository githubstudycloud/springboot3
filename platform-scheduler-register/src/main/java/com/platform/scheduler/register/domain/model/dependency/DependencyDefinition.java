package com.platform.scheduler.register.domain.model.dependency;

import com.platform.scheduler.domain.model.job.JobId;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 依赖关系定义聚合根
 * 定义了作业之间的依赖关系和约束条件
 * 
 * @author platform
 */
@Getter
public class DependencyDefinition {
    
    private final DependencyDefinitionId id;
    private final JobId sourceJobId;
    private final String sourceJobName;
    private final JobId targetJobId;
    private final String targetJobName;
    private DependencyType type;
    private String condition;
    private String description;
    private DependencyStatus status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedAt;
    
    /**
     * 私有构造方法，通过Builder创建
     */
    private DependencyDefinition(Builder builder) {
        this.id = builder.id != null ? builder.id : DependencyDefinitionId.newId();
        this.sourceJobId = builder.sourceJobId;
        this.sourceJobName = builder.sourceJobName;
        this.targetJobId = builder.targetJobId;
        this.targetJobName = builder.targetJobName;
        this.type = builder.type != null ? builder.type : DependencyType.REQUIRE_PREVIOUS_SUCCESS;
        this.condition = builder.condition;
        this.description = builder.description;
        this.status = builder.status != null ? builder.status : DependencyStatus.ACTIVE;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.lastModifiedBy = builder.lastModifiedBy;
        this.lastModifiedAt = builder.lastModifiedAt;
        
        validate();
    }
    
    /**
     * 验证依赖关系定义的完整性和有效性
     */
    private void validate() {
        if (sourceJobId == null) {
            throw new IllegalArgumentException("Source job ID cannot be null");
        }
        if (sourceJobName == null || sourceJobName.trim().isEmpty()) {
            throw new IllegalArgumentException("Source job name cannot be null or empty");
        }
        if (targetJobId == null) {
            throw new IllegalArgumentException("Target job ID cannot be null");
        }
        if (targetJobName == null || targetJobName.trim().isEmpty()) {
            throw new IllegalArgumentException("Target job name cannot be null or empty");
        }
        if (sourceJobId.equals(targetJobId)) {
            throw new IllegalArgumentException("Source and target jobs cannot be the same");
        }
        if (type == DependencyType.CONDITIONAL && (condition == null || condition.trim().isEmpty())) {
            throw new IllegalArgumentException("Condition must be provided for conditional dependency");
        }
    }
    
    /**
     * 更新依赖关系类型
     *
     * @param type 新的依赖类型
     * @param condition 条件表达式（仅适用于条件依赖）
     * @param modifier 修改者
     */
    public void updateType(DependencyType type, String condition, String modifier) {
        if (this.status == DependencyStatus.DISABLED) {
            throw new IllegalStateException("Cannot update disabled dependency");
        }
        
        if (type == null) {
            throw new IllegalArgumentException("Dependency type cannot be null");
        }
        
        if (type == DependencyType.CONDITIONAL && (condition == null || condition.trim().isEmpty())) {
            throw new IllegalArgumentException("Condition must be provided for conditional dependency");
        }
        
        this.type = type;
        this.condition = condition;
        updateModificationInfo(modifier);
    }
    
    /**
     * 更新描述信息
     *
     * @param description 新的描述信息
     * @param modifier 修改者
     */
    public void updateDescription(String description, String modifier) {
        if (this.status == DependencyStatus.DISABLED) {
            throw new IllegalStateException("Cannot update disabled dependency");
        }
        
        this.description = description;
        updateModificationInfo(modifier);
    }
    
    /**
     * 启用依赖关系
     *
     * @param modifier 修改者
     */
    public void enable(String modifier) {
        if (this.status == DependencyStatus.ACTIVE) {
            return;
        }
        
        this.status = DependencyStatus.ACTIVE;
        updateModificationInfo(modifier);
    }
    
    /**
     * 禁用依赖关系
     *
     * @param modifier 修改者
     */
    public void disable(String modifier) {
        if (this.status == DependencyStatus.DISABLED) {
            return;
        }
        
        this.status = DependencyStatus.DISABLED;
        updateModificationInfo(modifier);
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
     * 判断依赖关系是否处于活动状态
     *
     * @return 如果依赖关系状态为活动，则返回true
     */
    public boolean isActive() {
        return this.status == DependencyStatus.ACTIVE;
    }
    
    /**
     * 获取构建器
     *
     * @return 依赖关系定义构建器
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 依赖关系定义构建器
     */
    public static class Builder {
        private DependencyDefinitionId id;
        private JobId sourceJobId;
        private String sourceJobName;
        private JobId targetJobId;
        private String targetJobName;
        private DependencyType type;
        private String condition;
        private String description;
        private DependencyStatus status;
        private String createdBy;
        private LocalDateTime createdAt;
        private String lastModifiedBy;
        private LocalDateTime lastModifiedAt;
        
        public Builder withId(DependencyDefinitionId id) {
            this.id = id;
            return this;
        }
        
        public Builder withSourceJobId(JobId sourceJobId) {
            this.sourceJobId = sourceJobId;
            return this;
        }
        
        public Builder withSourceJobName(String sourceJobName) {
            this.sourceJobName = sourceJobName;
            return this;
        }
        
        public Builder withTargetJobId(JobId targetJobId) {
            this.targetJobId = targetJobId;
            return this;
        }
        
        public Builder withTargetJobName(String targetJobName) {
            this.targetJobName = targetJobName;
            return this;
        }
        
        public Builder withType(DependencyType type) {
            this.type = type;
            return this;
        }
        
        public Builder withCondition(String condition) {
            this.condition = condition;
            return this;
        }
        
        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }
        
        public Builder withStatus(DependencyStatus status) {
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
        
        public DependencyDefinition build() {
            return new DependencyDefinition(this);
        }
    }
}
