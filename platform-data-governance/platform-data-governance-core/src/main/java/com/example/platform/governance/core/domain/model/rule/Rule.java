package com.example.platform.governance.core.domain.model.rule;

/**
 * 规则接口
 * 
 * 定义规则的基本属性和行为
 */
public interface Rule {
    
    /**
     * 获取规则ID
     * 
     * @return 规则ID
     */
    String getId();
    
    /**
     * 获取规则名称
     * 
     * @return 规则名称
     */
    String getName();
    
    /**
     * 获取规则描述
     * 
     * @return 规则描述
     */
    String getDescription();
    
    /**
     * 获取规则类型
     * 
     * @return 规则类型
     */
    RuleType getType();
    
    /**
     * 获取规则状态
     * 
     * @return 规则状态
     */
    RuleStatus getStatus();
    
    /**
     * 获取规则版本
     * 
     * @return 规则版本
     */
    String getVersion();
    
    /**
     * 获取规则创建者
     * 
     * @return 规则创建者ID
     */
    String getCreatedBy();
    
    /**
     * 检查规则是否启用
     * 
     * @return 是否启用
     */
    boolean isEnabled();
    
    /**
     * 获取规则优先级
     * 
     * @return 优先级（数值越小优先级越高）
     */
    int getPriority();
}
