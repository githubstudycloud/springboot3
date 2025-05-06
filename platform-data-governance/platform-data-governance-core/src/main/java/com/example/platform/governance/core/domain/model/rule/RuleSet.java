package com.example.platform.governance.core.domain.model.rule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 规则集领域模型
 * 
 * 表示一组相关规则的集合，可以作为一个整体进行管理和应用
 */
public class RuleSet {
    
    private final String id;
    private String name;
    private String description;
    private RuleType type;
    private RuleStatus status;
    private String version;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean enabled;
    private List<Rule> rules;
    
    /**
     * 创建新的规则集
     * 
     * @param name 规则集名称
     * @param type 规则集类型
     * @return 新创建的规则集实例
     */
    public static RuleSet create(String name, RuleType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule set name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Rule set type cannot be null");
        }
        
        RuleSet ruleSet = new RuleSet();
        ruleSet.id = UUID.randomUUID().toString();
        ruleSet.name = name;
        ruleSet.type = type;
        ruleSet.status = RuleStatus.DRAFT;
        ruleSet.version = "1.0.0";
        ruleSet.createdAt = LocalDateTime.now();
        ruleSet.updatedAt = LocalDateTime.now();
        ruleSet.enabled = false;
        ruleSet.rules = new ArrayList<>();
        
        return ruleSet;
    }
    
    /**
     * 添加规则到规则集
     * 
     * @param rule 规则实例
     */
    public void addRule(Rule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("Rule cannot be null");
        }
        
        // 验证规则类型是否匹配规则集类型
        if (rule.getType() != this.type) {
            throw new IllegalArgumentException(
                "Rule type " + rule.getType() + " does not match rule set type " + this.type);
        }
        
        // 检查规则ID是否已存在
        boolean exists = this.rules.stream()
                .anyMatch(r -> r.getId().equals(rule.getId()));
                
        if (exists) {
            throw new IllegalArgumentException("Rule with ID " + rule.getId() + " already exists in the rule set");
        }
        
        this.rules.add(rule);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 从规则集中移除规则
     * 
     * @param ruleId 规则ID
     * @return 是否成功移除
     */
    public boolean removeRule(String ruleId) {
        boolean removed = this.rules.removeIf(rule -> rule.getId().equals(ruleId));
        if (removed) {
            this.updatedAt = LocalDateTime.now();
        }
        return removed;
    }
    
    /**
     * 更新规则集状态
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
     * 启用规则集
     */
    public void enable() {
        if (this.status != RuleStatus.PUBLISHED) {
            throw new IllegalStateException("Can only enable rule sets in PUBLISHED status");
        }
        
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 禁用规则集
     */
    public void disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新规则集版本
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
     * 设置规则集描述
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
     * 获取规则集中的所有规则
     * 
     * @return 不可修改的规则列表
     */
    public List<Rule> getRules() {
        return Collections.unmodifiableList(rules);
    }
    
    /**
     * 按优先级排序获取规则列表
     * 
     * @return 按优先级排序的规则列表（优先级数值小的在前）
     */
    public List<Rule> getRulesByPriority() {
        List<Rule> sortedRules = new ArrayList<>(this.rules);
        sortedRules.sort((r1, r2) -> Integer.compare(r1.getPriority(), r2.getPriority()));
        return Collections.unmodifiableList(sortedRules);
    }
    
    /**
     * 获取规则集中启用的规则数量
     * 
     * @return 启用的规则数量
     */
    public int getEnabledRuleCount() {
        return (int) this.rules.stream()
                .filter(Rule::isEnabled)
                .count();
    }
    
    /**
     * 检查规则集是否为空
     * 
     * @return 是否为空
     */
    public boolean isEmpty() {
        return this.rules.isEmpty();
    }
    
    /**
     * 获取规则集中的规则数量
     * 
     * @return 规则数量
     */
    public int getRuleCount() {
        return this.rules.size();
    }
    
    // Getters
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public RuleType getType() {
        return type;
    }
    
    public RuleStatus getStatus() {
        return status;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public boolean isEnabled() {
        return enabled && status == RuleStatus.PUBLISHED;
    }
    
    // 私有构造函数，强制使用工厂方法
    private RuleSet() {
    }
}
