package com.platform.scheduler.domain.model.job;

import com.platform.scheduler.domain.event.common.DomainEventPublisher;
import com.platform.scheduler.domain.model.common.AggregateRoot;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 作业聚合根
 * 代表调度系统的核心业务实体，包含作业的所有信息和行为
 * 
 * @author platform
 */
@Getter
public class Job implements AggregateRoot<JobId> {
    
    private final JobId id;
    private String name;
    private String description;
    private JobType type;
    private JobStatus status;
    private JobPriority priority;
    private ScheduleStrategy scheduleStrategy;
    private final Map<String, JobParameter> parameters;
    private String handlerName;
    private Integer maxRetryCount;
    private Integer retryInterval;
    private Integer timeout;
    private final List<JobDependency> dependencies;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedAt;
    
    /**
     * 私有构造方法，通过Builder创建实例
     */
    private Job(Builder builder) {
        this.id = builder.id != null ? builder.id : JobId.newId();
        this.name = builder.name;
        this.description = builder.description;
        this.type = builder.type;
        this.status = builder.status != null ? builder.status : JobStatus.CREATED;
        this.priority = builder.priority != null ? builder.priority : JobPriority.defaultPriority();
        this.scheduleStrategy = builder.scheduleStrategy;
        this.parameters = new HashMap<>(builder.parameters);
        this.handlerName = builder.handlerName;
        this.maxRetryCount = builder.maxRetryCount;
        this.retryInterval = builder.retryInterval;
        this.timeout = builder.timeout;
        this.dependencies = new ArrayList<>(builder.dependencies);
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.lastModifiedBy = builder.lastModifiedBy;
        this.lastModifiedAt = builder.lastModifiedAt;
        
        validate();
    }
    
    /**
     * 验证作业信息的完整性和有效性
     */
    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Job name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Job type cannot be null");
        }
        if (scheduleStrategy == null) {
            throw new IllegalArgumentException("Schedule strategy cannot be null");
        }
        if (handlerName == null || handlerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Job handler name cannot be null or empty");
        }
        
        // 验证必需参数是否都已提供
        List<String> missingParams = parameters.values().stream()
                .filter(JobParameter::isRequired)
                .filter(p -> p.getValue() == null || p.getValue().trim().isEmpty())
                .map(JobParameter::getName)
                .collect(Collectors.toList());
        
        if (!missingParams.isEmpty()) {
            throw new IllegalArgumentException("Missing required parameters: " + String.join(", ", missingParams));
        }
    }
    
    /**
     * 启用作业，使其可被调度执行
     *
     * @param modifier 修改者
     * @param eventPublisher 事件发布器
     */
    public void enable(String modifier, DomainEventPublisher eventPublisher) {
        if (this.status == JobStatus.ENABLED) {
            return;
        }
        
        this.status = JobStatus.ENABLED;
        updateModificationInfo(modifier);
        
        if (eventPublisher != null) {
            // 发布作业启用事件
            eventPublisher.publish(new JobEnabledEvent(this.id));
        }
    }
    
    /**
     * 禁用作业，暂停调度
     *
     * @param modifier 修改者
     * @param eventPublisher 事件发布器
     */
    public void disable(String modifier, DomainEventPublisher eventPublisher) {
        if (this.status == JobStatus.DISABLED) {
            return;
        }
        
        this.status = JobStatus.DISABLED;
        updateModificationInfo(modifier);
        
        if (eventPublisher != null) {
            // 发布作业禁用事件
            eventPublisher.publish(new JobDisabledEvent(this.id));
        }
    }
    
    /**
     * 归档作业，不再使用但保留记录
     *
     * @param modifier 修改者
     * @param eventPublisher 事件发布器
     */
    public void archive(String modifier, DomainEventPublisher eventPublisher) {
        if (this.status == JobStatus.ARCHIVED) {
            return;
        }
        
        this.status = JobStatus.ARCHIVED;
        updateModificationInfo(modifier);
        
        if (eventPublisher != null) {
            // 发布作业归档事件
            eventPublisher.publish(new JobArchivedEvent(this.id));
        }
    }
    
    /**
     * 删除作业（逻辑删除）
     *
     * @param modifier 修改者
     * @param eventPublisher 事件发布器
     */
    public void delete(String modifier, DomainEventPublisher eventPublisher) {
        if (this.status == JobStatus.DELETED) {
            return;
        }
        
        this.status = JobStatus.DELETED;
        updateModificationInfo(modifier);
        
        if (eventPublisher != null) {
            // 发布作业删除事件
            eventPublisher.publish(new JobDeletedEvent(this.id));
        }
    }
    
    /**
     * 更新作业基本信息
     *
     * @param name 名称
     * @param description 描述
     * @param priority 优先级
     * @param modifier 修改者
     */
    public void updateBasicInfo(String name, String description, JobPriority priority, String modifier) {
        if (this.status == JobStatus.DELETED || this.status == JobStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot update deleted or archived job");
        }
        
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (priority != null) {
            this.priority = priority;
        }
        
        updateModificationInfo(modifier);
    }
    
    /**
     * 更新调度策略
     *
     * @param scheduleStrategy 新的调度策略
     * @param modifier 修改者
     * @param eventPublisher 事件发布器
     */
    public void updateScheduleStrategy(ScheduleStrategy scheduleStrategy, String modifier, DomainEventPublisher eventPublisher) {
        if (this.status == JobStatus.DELETED || this.status == JobStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot update deleted or archived job");
        }
        
        if (scheduleStrategy == null) {
            throw new IllegalArgumentException("Schedule strategy cannot be null");
        }
        
        this.scheduleStrategy = scheduleStrategy;
        updateModificationInfo(modifier);
        
        if (eventPublisher != null) {
            // 发布调度策略更新事件
            eventPublisher.publish(new JobScheduleUpdatedEvent(this.id, scheduleStrategy));
        }
    }
    
    /**
     * 更新作业参数
     *
     * @param parameters 新的参数列表
     * @param modifier 修改者
     */
    public void updateParameters(List<JobParameter> parameters, String modifier) {
        if (this.status == JobStatus.DELETED || this.status == JobStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot update deleted or archived job");
        }
        
        if (parameters == null) {
            return;
        }
        
        for (JobParameter parameter : parameters) {
            this.parameters.put(parameter.getName(), parameter);
        }
        
        updateModificationInfo(modifier);
    }
    
    /**
     * 添加作业依赖
     *
     * @param dependency 新的依赖
     * @param modifier 修改者
     */
    public void addDependency(JobDependency dependency, String modifier) {
        if (this.status == JobStatus.DELETED || this.status == JobStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot update deleted or archived job");
        }
        
        if (dependency == null) {
            throw new IllegalArgumentException("Dependency cannot be null");
        }
        
        // 检查是否与已有依赖重复
        for (JobDependency existingDep : this.dependencies) {
            if (existingDep.getDependencyJobId().equals(dependency.getDependencyJobId())) {
                throw new IllegalArgumentException("Dependency already exists");
            }
        }
        
        // 检查是否形成依赖循环
        if (dependency.getDependencyJobId().equals(this.id)) {
            throw new IllegalArgumentException("Job cannot depend on itself");
        }
        
        this.dependencies.add(dependency);
        updateModificationInfo(modifier);
    }
    
    /**
     * 移除作业依赖
     *
     * @param dependencyJobId 要移除的依赖作业ID
     * @param modifier 修改者
     */
    public void removeDependency(JobId dependencyJobId, String modifier) {
        if (this.status == JobStatus.DELETED || this.status == JobStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot update deleted or archived job");
        }
        
        if (dependencyJobId == null) {
            throw new IllegalArgumentException("Dependency job ID cannot be null");
        }
        
        boolean removed = this.dependencies.removeIf(dep -> dep.getDependencyJobId().equals(dependencyJobId));
        
        if (removed) {
            updateModificationInfo(modifier);
        }
    }
    
    /**
     * 更新失败处理策略
     *
     * @param maxRetryCount 最大重试次数
     * @param retryInterval 重试间隔（秒）
     * @param timeout 超时时间（秒）
     * @param modifier 修改者
     */
    public void updateFailurePolicy(Integer maxRetryCount, Integer retryInterval, Integer timeout, String modifier) {
        if (this.status == JobStatus.DELETED || this.status == JobStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot update deleted or archived job");
        }
        
        if (maxRetryCount != null && maxRetryCount >= 0) {
            this.maxRetryCount = maxRetryCount;
        }
        
        if (retryInterval != null && retryInterval >= 0) {
            this.retryInterval = retryInterval;
        }
        
        if (timeout != null && timeout >= 0) {
            this.timeout = timeout;
        }
        
        updateModificationInfo(modifier);
    }
    
    /**
     * 获取作业参数列表
     *
     * @return 参数列表的不可修改视图
     */
    public List<JobParameter> getParameterList() {
        return Collections.unmodifiableList(new ArrayList<>(parameters.values()));
    }
    
    /**
     * 获取作业依赖列表
     *
     * @return 依赖列表的不可修改视图
     */
    public List<JobDependency> getDependencyList() {
        return Collections.unmodifiableList(dependencies);
    }
    
    /**
     * 判断作业是否处于可执行状态
     *
     * @return 如果作业状态为已启用，则返回true
     */
    public boolean isExecutable() {
        return this.status.isExecutable();
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
     * 获取构建器
     *
     * @return 作业构建器
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 作业构建器
     */
    public static class Builder {
        private JobId id;
        private String name;
        private String description;
        private JobType type;
        private JobStatus status;
        private JobPriority priority;
        private ScheduleStrategy scheduleStrategy;
        private final Map<String, JobParameter> parameters = new HashMap<>();
        private String handlerName;
        private Integer maxRetryCount;
        private Integer retryInterval;
        private Integer timeout;
        private final List<JobDependency> dependencies = new ArrayList<>();
        private String createdBy;
        private LocalDateTime createdAt;
        private String lastModifiedBy;
        private LocalDateTime lastModifiedAt;
        
        public Builder withId(JobId id) {
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
        
        public Builder withStatus(JobStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder withPriority(JobPriority priority) {
            this.priority = priority;
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
        
        public Builder withHandlerName(String handlerName) {
            this.handlerName = handlerName;
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
        
        public Builder withDependency(JobDependency dependency) {
            if (dependency != null) {
                this.dependencies.add(dependency);
            }
            return this;
        }
        
        public Builder withDependencies(List<JobDependency> dependencies) {
            if (dependencies != null) {
                this.dependencies.addAll(dependencies);
            }
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
        
        public Job build() {
            return new Job(this);
        }
    }
}
