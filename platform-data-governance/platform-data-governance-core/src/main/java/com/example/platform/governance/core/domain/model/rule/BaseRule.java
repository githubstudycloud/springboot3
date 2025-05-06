package com.example.platform.governance.core.domain.model.rule;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 规则基础抽象类
 * 
 * 实现Rule接口并提供通用功能
 */
public abstract class BaseRule implements Rule {
    
    private final String id;
    private final RuleType type;
    private String name;
    private String description;
    private RuleStatus status;
    private String version;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean enabled;
    private int priority;
    private Map<String, String> properties;
    
    /**
     * 构造函数
     * 
     * @param name 规则名称
     * @param type 规则类型
     */
    protected BaseRule(String name, RuleType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Rule type cannot be null");
        }
        
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.status = RuleStatus.DRAFT;
        this.version = "1.0.0";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.enabled = false;
        this.priority = 100; // 默认中等优先级
        this.properties = new HashMap<>();
    }
    
    /**
     * 构造函数
     * 
     * @param id 规则ID
     * @param name 规则名称
     * @param type 规则类型
     */
    protected BaseRule(String id, String name, RuleType type) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule id cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Rule type cannot be null");
        }
        
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = RuleStatus.DRAFT;
        this.version = "1.0.0";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.enabled = false;
        this.priority = 100; // 默认中等优先级
        this.properties = new HashMap<>();
    }
    
    /**
     * 更新规则状态
     * 
     * @param newStatus 新状态
     */
    public void updateStatus(RuleStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                "Cannot transition from " + this.status + " to " + newStatus);
        }
        
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 启用规则
     */
    public void enable() {
        if (this.status != RuleStatus.PUBLISHED) {
            throw new IllegalStateException("Can only enable rules in PUBLISHED status");
        }
        
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 禁用规则
     */
    public void disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新规则版本
     * 
     * @param newVersion 新版本号
     */
    public void updateVersion(String newVersion) {
        if (newVersion == null || newVersion.trim().isEmpty()) {
            throw new IllegalArgumentException("Version cannot be null or empty");
        }
        
        this.version = newVersion;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 设置规则优先级
     * 
     * @param priority 优先级（数值越小优先级越高）
     */
    public void setPriority(int priority) {
        if (priority < 0) {
            throw new IllegalArgumentException("Priority cannot be negative");
        }
        
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 设置规则描述
     * 
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 设置创建者
     * 
     * @param createdBy 创建者ID
     */
    public void setCreatedBy(String createdBy) {
        if (createdBy == null || createdBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Creator ID cannot be null or empty");
        }
        
        this.createdBy = createdBy;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 添加规则属性
     * 
     * @param key 属性键
     * @param value 属性值
     */
    public void addProperty(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Property key cannot be null or empty");
        }
        
        this.properties.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 删除规则属性
     * 
     * @param key 属性键
     * @return 是否成功删除
     */
    public boolean removeProperty(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        boolean removed = this.properties.remove(key) != null;
        if (removed) {
            this.updatedAt = LocalDateTime.now();
        }
        return removed;
    }
    
    /**
     * 获取规则属性
     * 
     * @param key 属性键
     * @return 属性值，如果不存在则返回null
     */
    public String getProperty(String key) {
        return this.properties.get(key);
    }
    
    /**
     * 获取所有规则属性
     * 
     * @return 不可修改的属性Map
     */
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }
    
    // Rule接口实现
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public RuleType getType() {
        return type;
    }
    
    @Override
    public RuleStatus getStatus() {
        return status;
    }
    
    @Override
    public String getVersion() {
        return version;
    }
    
    @Override
    public String getCreatedBy() {
        return createdBy;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && status == RuleStatus.PUBLISHED;
    }
    
    @Override
    public int getPriority() {
        return priority;
    }
    
    /**
     * 获取创建时间
     * 
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * 获取更新时间
     * 
     * @return 更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
