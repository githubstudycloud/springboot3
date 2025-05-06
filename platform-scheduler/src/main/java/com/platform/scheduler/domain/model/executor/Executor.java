package com.platform.scheduler.domain.model.executor;

import com.platform.scheduler.domain.event.common.DomainEventPublisher;
import com.platform.scheduler.domain.model.common.AggregateRoot;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 执行器聚合根
 * 代表调度系统中负责执行作业的节点
 * 
 * @author platform
 */
@Getter
public class Executor implements AggregateRoot<ExecutorId> {
    
    private final ExecutorId id;
    private String name;
    private String description;
    private ExecutorType type;
    private ExecutorStatus status;
    private String host;
    private Integer port;
    private String version;
    private final Set<String> tags;
    private Integer maxConcurrency;
    private Integer currentLoad;
    private Long totalMemory;
    private Long freeMemory;
    private Integer cpuCores;
    private Double cpuUsage;
    private LocalDateTime lastHeartbeatTime;
    private LocalDateTime registrationTime;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedAt;
    
    /**
     * 私有构造方法，通过Builder创建实例
     */
    private Executor(Builder builder) {
        this.id = builder.id != null ? builder.id : ExecutorId.newId();
        this.name = builder.name;
        this.description = builder.description;
        this.type = builder.type;
        this.status = builder.status != null ? builder.status : ExecutorStatus.ONLINE;
        this.host = builder.host;
        this.port = builder.port;
        this.version = builder.version;
        this.tags = new HashSet<>(builder.tags);
        this.maxConcurrency = builder.maxConcurrency;
        this.currentLoad = builder.currentLoad != null ? builder.currentLoad : 0;
        this.totalMemory = builder.totalMemory;
        this.freeMemory = builder.freeMemory;
        this.cpuCores = builder.cpuCores;
        this.cpuUsage = builder.cpuUsage;
        this.lastHeartbeatTime = builder.lastHeartbeatTime;
        this.registrationTime = builder.registrationTime != null ? builder.registrationTime : LocalDateTime.now();
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.lastModifiedBy = builder.lastModifiedBy;
        this.lastModifiedAt = builder.lastModifiedAt;
        
        validate();
    }
    
    /**
     * 验证执行器信息的完整性和有效性
     */
    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Executor name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Executor type cannot be null");
        }
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("Executor host cannot be null or empty");
        }
        if (port == null || port <= 0) {
            throw new IllegalArgumentException("Executor port must be a positive number");
        }
        if (maxConcurrency != null && maxConcurrency <= 0) {
            throw new IllegalArgumentException("Max concurrency must be a positive number");
        }
    }
    
    /**
     * 更新执行器状态
     *
     * @param status 新状态
     * @param reason 状态变更原因
     * @param modifier 修改者
     * @param eventPublisher 事件发布器
     */
    public void updateStatus(ExecutorStatus status, String reason, String modifier, DomainEventPublisher eventPublisher) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        ExecutorStatus oldStatus = this.status;
        this.status = status;
        this.lastHeartbeatTime = LocalDateTime.now();
        updateModificationInfo(modifier);
        
        if (eventPublisher != null) {
            // 发布执行器状态变更事件
            eventPublisher.publish(new ExecutorStatusChangedEvent(this.id, oldStatus, status, reason));
        }
    }
    
    /**
     * 更新执行器心跳时间
     *
     * @param eventPublisher 事件发布器
     */
    public void updateHeartbeat(DomainEventPublisher eventPublisher) {
        LocalDateTime now = LocalDateTime.now();
        this.lastHeartbeatTime = now;
        
        // 如果执行器之前是离线状态，则自动变为在线状态
        if (this.status == ExecutorStatus.OFFLINE) {
            this.status = ExecutorStatus.ONLINE;
            
            if (eventPublisher != null) {
                // 发布执行器状态变更事件
                eventPublisher.publish(new ExecutorStatusChangedEvent(
                        this.id, ExecutorStatus.OFFLINE, ExecutorStatus.ONLINE, "执行器恢复在线"));
            }
        }
    }
    
    /**
     * 更新执行器资源信息
     *
     * @param currentLoad 当前负载
     * @param totalMemory 总内存
     * @param freeMemory 空闲内存
     * @param cpuUsage CPU使用率
     * @param modifier 修改者
     */
    public void updateResourceInfo(Integer currentLoad, Long totalMemory, Long freeMemory, Double cpuUsage, String modifier) {
        if (currentLoad != null) {
            this.currentLoad = currentLoad;
        }
        if (totalMemory != null) {
            this.totalMemory = totalMemory;
        }
        if (freeMemory != null) {
            this.freeMemory = freeMemory;
        }
        if (cpuUsage != null) {
            this.cpuUsage = cpuUsage;
        }
        
        updateModificationInfo(modifier);
    }
    
    /**
     * 更新执行器基本信息
     *
     * @param name 名称
     * @param description 描述
     * @param type 类型
     * @param modifier 修改者
     */
    public void updateBasicInfo(String name, String description, ExecutorType type, String modifier) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (type != null) {
            this.type = type;
        }
        
        updateModificationInfo(modifier);
    }
    
    /**
     * 更新执行器配置信息
     *
     * @param maxConcurrency 最大并发数
     * @param modifier 修改者
     */
    public void updateConfiguration(Integer maxConcurrency, String modifier) {
        if (maxConcurrency != null && maxConcurrency > 0) {
            this.maxConcurrency = maxConcurrency;
        }
        
        updateModificationInfo(modifier);
    }
    
    /**
     * 添加执行器标签
     *
     * @param tag 标签
     * @param modifier 修改者
     */
    public void addTag(String tag, String modifier) {
        if (tag != null && !tag.trim().isEmpty()) {
            this.tags.add(tag);
            updateModificationInfo(modifier);
        }
    }
    
    /**
     * 移除执行器标签
     *
     * @param tag 标签
     * @param modifier 修改者
     */
    public void removeTag(String tag, String modifier) {
        if (tag != null && this.tags.remove(tag)) {
            updateModificationInfo(modifier);
        }
    }
    
    /**
     * 判断执行器是否可用
     *
     * @return 如果执行器状态为在线且负载未满，则返回true
     */
    public boolean isAvailable() {
        return this.status.isAvailable() 
                && (this.maxConcurrency == null || this.currentLoad < this.maxConcurrency);
    }
    
    /**
     * 判断执行器是否离线
     *
     * @param heartbeatTimeoutSeconds 心跳超时时间（秒）
     * @return 如果执行器最后心跳时间超过超时时间，则返回true
     */
    public boolean isOffline(int heartbeatTimeoutSeconds) {
        if (this.lastHeartbeatTime == null) {
            return true;
        }
        
        return LocalDateTime.now().minusSeconds(heartbeatTimeoutSeconds).isAfter(this.lastHeartbeatTime);
    }
    
    /**
     * 获取负载百分比
     *
     * @return 负载百分比，如果最大并发数未设置则返回0
     */
    public int getLoadPercentage() {
        if (this.maxConcurrency == null || this.maxConcurrency <= 0) {
            return 0;
        }
        
        return (int) (((double) this.currentLoad / this.maxConcurrency) * 100);
    }
    
    /**
     * 获取内存使用百分比
     *
     * @return 内存使用百分比，如果总内存未设置则返回0
     */
    public int getMemoryUsagePercentage() {
        if (this.totalMemory == null || this.totalMemory <= 0 || this.freeMemory == null) {
            return 0;
        }
        
        return (int) (((double) (this.totalMemory - this.freeMemory) / this.totalMemory) * 100);
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
     * @return 执行器构建器
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 执行器构建器
     */
    public static class Builder {
        private ExecutorId id;
        private String name;
        private String description;
        private ExecutorType type;
        private ExecutorStatus status;
        private String host;
        private Integer port;
        private String version;
        private final Set<String> tags = new HashSet<>();
        private Integer maxConcurrency;
        private Integer currentLoad;
        private Long totalMemory;
        private Long freeMemory;
        private Integer cpuCores;
        private Double cpuUsage;
        private LocalDateTime lastHeartbeatTime;
        private LocalDateTime registrationTime;
        private String createdBy;
        private LocalDateTime createdAt;
        private String lastModifiedBy;
        private LocalDateTime lastModifiedAt;
        
        public Builder withId(ExecutorId id) {
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
        
        public Builder withType(ExecutorType type) {
            this.type = type;
            return this;
        }
        
        public Builder withStatus(ExecutorStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder withHost(String host) {
            this.host = host;
            return this;
        }
        
        public Builder withPort(Integer port) {
            this.port = port;
            return this;
        }
        
        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }
        
        public Builder withTag(String tag) {
            if (tag != null && !tag.trim().isEmpty()) {
                this.tags.add(tag);
            }
            return this;
        }
        
        public Builder withTags(Set<String> tags) {
            if (tags != null) {
                this.tags.addAll(tags);
            }
            return this;
        }
        
        public Builder withMaxConcurrency(Integer maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
            return this;
        }
        
        public Builder withCurrentLoad(Integer currentLoad) {
            this.currentLoad = currentLoad;
            return this;
        }
        
        public Builder withTotalMemory(Long totalMemory) {
            this.totalMemory = totalMemory;
            return this;
        }
        
        public Builder withFreeMemory(Long freeMemory) {
            this.freeMemory = freeMemory;
            return this;
        }
        
        public Builder withCpuCores(Integer cpuCores) {
            this.cpuCores = cpuCores;
            return this;
        }
        
        public Builder withCpuUsage(Double cpuUsage) {
            this.cpuUsage = cpuUsage;
            return this;
        }
        
        public Builder withLastHeartbeatTime(LocalDateTime lastHeartbeatTime) {
            this.lastHeartbeatTime = lastHeartbeatTime;
            return this;
        }
        
        public Builder withRegistrationTime(LocalDateTime registrationTime) {
            this.registrationTime = registrationTime;
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
        
        public Executor build() {
            return new Executor(this);
        }
    }
}
