package com.example.platform.governance.core.domain.model.rule.cleansing;

import com.example.platform.governance.core.domain.model.rule.BaseRule;
import com.example.platform.governance.core.domain.model.rule.RuleType;

/**
 * 抽象基础清洗规则类
 * 
 * 实现共通的清洗规则功能
 */
public abstract class BaseCleansingRule extends BaseRule implements CleansingRule {
    
    private final String targetField;
    private final Class<?>[] supportedTypes;
    
    /**
     * 构造函数
     * 
     * @param name 规则名称
     * @param targetField 目标字段
     * @param supportedTypes 支持的数据类型
     */
    protected BaseCleansingRule(String name, String targetField, Class<?>... supportedTypes) {
        super(name, RuleType.CLEANSING);
        
        if (targetField == null || targetField.trim().isEmpty()) {
            throw new IllegalArgumentException("Target field cannot be null or empty");
        }
        
        this.targetField = targetField;
        this.supportedTypes = supportedTypes != null ? supportedTypes : new Class<?>[0];
    }
    
    /**
     * 构造函数
     * 
     * @param id 规则ID
     * @param name 规则名称
     * @param targetField 目标字段
     * @param supportedTypes 支持的数据类型
     */
    protected BaseCleansingRule(String id, String name, String targetField, Class<?>... supportedTypes) {
        super(id, name, RuleType.CLEANSING);
        
        if (targetField == null || targetField.trim().isEmpty()) {
            throw new IllegalArgumentException("Target field cannot be null or empty");
        }
        
        this.targetField = targetField;
        this.supportedTypes = supportedTypes != null ? supportedTypes : new Class<?>[0];
    }
    
    @Override
    public String getTargetField() {
        return targetField;
    }
    
    @Override
    public boolean canApplyTo(Class<?> valueClass) {
        if (valueClass == null || supportedTypes.length == 0) {
            return false;
        }
        
        for (Class<?> supportedType : supportedTypes) {
            if (supportedType.isAssignableFrom(valueClass)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 辅助方法：验证字段值是否存在且类型匹配
     * 
     * @param fieldValue 字段值
     * @param context 清洗上下文
     * @return 如果验证不通过则返回跳过结果，否则返回null
     */
    protected CleansingResult validateField(Object fieldValue, CleansingContext context) {
        // 检查字段是否存在
        if (!context.hasField(targetField)) {
            return CleansingResult.skipped(null, "Field " + targetField + " does not exist", getId());
        }
        
        // 检查字段值是否为null
        if (fieldValue == null) {
            return CleansingResult.skipped(null, "Field " + targetField + " has null value", getId());
        }
        
        // 检查字段类型是否支持
        if (!canApplyTo(fieldValue.getClass())) {
            return CleansingResult.skipped(
                fieldValue, 
                "Field " + targetField + " has unsupported type: " + fieldValue.getClass().getName(),
                getId()
            );
        }
        
        return null; // 验证通过
    }
}
