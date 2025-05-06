package com.platform.scheduler.register.domain.model.version;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.job.JobParameter;
import com.platform.scheduler.domain.model.job.JobType;
import com.platform.scheduler.domain.model.job.ScheduleStrategy;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业版本聚合根
 * 保存作业的一个历史版本，支持版本管理和回滚
 * 
 * @author platform
 */
@Getter
public class JobVersion {
    
    private final JobVersionId id;
    private final JobId jobId;
    private final String jobName;
    private final String description;
    private final JobType type;
    private final String handlerName;
    private final Integer version;
    private final ScheduleStrategy scheduleStrategy;
    private final Map<String, JobParameter> parameters;
    private final Integer maxRetryCount;
    private final Integer retryInterval;
    private final Integer timeout;
    private final List<JobVersionDependency> dependencies;
    private final VersionStatus status;
    private final boolean isCurrent;
    private final String createdBy;
    private final LocalDateTime createdAt;
    private String comment;
    
    /**
     * 私有构造方法，通过Builder创建
     */
    private JobVersion(Builder builder) {
        this.id = builder.id != null ? builder.id : JobVersionId.newId();
        this.jobId = builder.jobId;
        this.jobName = builder.jobName;
        this.description = builder.description;
        this.type = builder.type;
        this.handlerName = builder.handlerName;
        this.version = builder.version;
        this.scheduleStrategy = builder.scheduleStrategy;
        this.parameters = new HashMap<>(builder.parameters);
        this.maxRetryCount = builder.maxRetryCount;
        this.retryInterval = builder.retryInterval;
        this.timeout = builder.timeout;
        this.dependencies = new ArrayList<>(builder.dependencies);
        this.status = builder.status != null ? builder.status : VersionStatus.CREATED;
        this.isCurrent = builder.isCurrent;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.comment = builder.comment;
        
        validate();
    }
    
    /**
     * 验证版本信息的完整性和有效性
     */
    private void validate() {
        if (jobId == null) {
            throw new IllegalArgumentException("Job ID cannot be null");
        }
        if (jobName == null || jobName.trim().isEmpty()) {
            throw new IllegalArgumentException("Job name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Job type cannot be null");
        }
        if (handlerName == null || handlerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Job handler name cannot be null or empty");
        }
        if (version == null || version < 1) {
            throw new IllegalArgumentException("Version number must be positive");
        }
        if (scheduleStrategy == null) {
            throw new IllegalArgumentException("Schedule strategy cannot be null");
        }
    }
    
    /**
     * 更新版本备注
     *
     * @param comment 新的备注信息
     */
    public void updateComment(String comment) {
        this.comment = comment;
    }
    
    /**
     * 获取参数列表
     *
     * @return 参数列表的不可修改视图
     */
    public List<JobParameter> getParameterList() {
        return Collections.unmodifiableList(new ArrayList<>(parameters.values()));
    }
    
    /**
     * 获取依赖列表
     *
     * @return 依赖列表的不可修改视图
     */
    public List<JobVersionDependency> getDependencyList() {
        return Collections.unmodifiableList(dependencies);
    }
    
    /**
     * 获取构建器
     *
     * @return 作业版本构建器
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 作业版本构建器
     */
    public static class Builder {
        private JobVersionId id;
        private JobId jobId;
        private String jobName;
        private String description;
        private JobType type;
        private String handlerName;
        private Integer version;
        private ScheduleStrategy scheduleStrategy;
        private final Map<String, JobParameter> parameters = new HashMap<>();
        private Integer maxRetryCount;
        private Integer retryInterval;
        private Integer timeout;
        private final List<JobVersionDependency> dependencies = new ArrayList<>();
        private VersionStatus status;
        private boolean isCurrent;
        private String createdBy;
        private LocalDateTime createdAt;
        private String comment;
        
        public Builder withId(JobVersionId id) {
            this.id = id;
            return this;
        }
        
        public Builder withJobId(JobId jobId) {
            this.jobId = jobId;
            return this;
        }
        
        public Builder withJobName(String jobName) {
            this.jobName = jobName;
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
        
        public Builder withVersion(Integer version) {
            this.version = version;
            return this;
        }
        
        public Builder withScheduleStrategy(ScheduleStrategy scheduleStrategy) {
            this.scheduleStrategy = scheduleStrategy;
            return this;
        }
        
        public Builder withParameter(JobParameter parameter) {
            if (parameter != null) {
                this.parameters.put(parameter.getName(), parameter);
            }
            return this;
        }
        
        public Builder withParameters(List<JobParameter> parameters) {
            if (parameters != null) {
                for (JobParameter parameter : parameters) {
                    this.parameters.put(parameter.getName(), parameter);
                }
            }
            return this;
        }
        
        public Builder withMaxRetryCount(Integer maxRetryCount) {
            this.maxRetryCount = maxRetryCount;
            return this;
        }
        
        public Builder withRetryInterval(Integer retryInterval) {
            this.retryInterval = retryInterval;
            return this;
        }
        
        public Builder withTimeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }
        
        public Builder withDependency(JobVersionDependency dependency) {
            if (dependency != null) {
                this.dependencies.add(dependency);
            }
            return this;
        }
        
        public Builder withDependencies(List<JobVersionDependency> dependencies) {
            if (dependencies != null) {
                this.dependencies.addAll(dependencies);
            }
            return this;
        }
        
        public Builder withStatus(VersionStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder isCurrent(boolean isCurrent) {
            this.isCurrent = isCurrent;
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
        
        public Builder withComment(String comment) {
            this.comment = comment;
            return this;
        }
        
        public JobVersion build() {
            return new JobVersion(this);
        }
    }
}
