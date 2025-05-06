package com.example.platform.governance.core.domain.model.rule.cleansing;

/**
 * 清洗结果领域模型
 * 
 * 表示清洗规则应用后的结果
 */
public class CleansingResult {
    
    private final Object originalValue;
    private final Object cleanedValue;
    private final CleansingResultStatus status;
    private final String message;
    private final String ruleId;
    
    /**
     * 创建成功的清洗结果
     * 
     * @param originalValue 原始值
     * @param cleanedValue 清洗后的值
     * @param ruleId 应用的规则ID
     * @return 清洗结果实例
     */
    public static CleansingResult success(Object originalValue, Object cleanedValue, String ruleId) {
        return new CleansingResult(originalValue, cleanedValue, CleansingResultStatus.SUCCESS, null, ruleId);
    }
    
    /**
     * 创建成功但有警告的清洗结果
     * 
     * @param originalValue 原始值
     * @param cleanedValue 清洗后的值
     * @param message 警告消息
     * @param ruleId 应用的规则ID
     * @return 清洗结果实例
     */
    public static CleansingResult warning(Object originalValue, Object cleanedValue, String message, String ruleId) {
        return new CleansingResult(originalValue, cleanedValue, CleansingResultStatus.WARNING, message, ruleId);
    }
    
    /**
     * 创建失败的清洗结果
     * 
     * @param originalValue 原始值
     * @param message 错误消息
     * @param ruleId 应用的规则ID
     * @return 清洗结果实例
     */
    public static CleansingResult failure(Object originalValue, String message, String ruleId) {
        return new CleansingResult(originalValue, originalValue, CleansingResultStatus.FAILURE, message, ruleId);
    }
    
    /**
     * 创建跳过的清洗结果（规则不适用）
     * 
     * @param originalValue 原始值
     * @param message 跳过原因
     * @param ruleId 应用的规则ID
     * @return 清洗结果实例
     */
    public static CleansingResult skipped(Object originalValue, String message, String ruleId) {
        return new CleansingResult(originalValue, originalValue, CleansingResultStatus.SKIPPED, message, ruleId);
    }
    
    /**
     * 获取原始值
     * 
     * @return 原始值
     */
    public Object getOriginalValue() {
        return originalValue;
    }
    
    /**
     * 获取清洗后的值
     * 
     * @return 清洗后的值
     */
    public Object getCleanedValue() {
        return cleanedValue;
    }
    
    /**
     * 获取清洗状态
     * 
     * @return 清洗状态
     */
    public CleansingResultStatus getStatus() {
        return status;
    }
    
    /**
     * 获取消息
     * 
     * @return 消息
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * 获取规则ID
     * 
     * @return 规则ID
     */
    public String getRuleId() {
        return ruleId;
    }
    
    /**
     * 检查清洗是否成功（包括有警告的成功）
     * 
     * @return 是否成功
     */
    public boolean isSuccess() {
        return status == CleansingResultStatus.SUCCESS || status == CleansingResultStatus.WARNING;
    }
    
    /**
     * 检查清洗是否有实际变更值
     * 
     * @return 是否有变更
     */
    public boolean hasChanged() {
        if (originalValue == null && cleanedValue == null) {
            return false;
        }
        if (originalValue == null || cleanedValue == null) {
            return true;
        }
        return !originalValue.equals(cleanedValue);
    }
    
    // 私有构造函数，强制使用工厂方法
    private CleansingResult(Object originalValue, Object cleanedValue, CleansingResultStatus status, String message, String ruleId) {
        this.originalValue = originalValue;
        this.cleanedValue = cleanedValue;
        this.status = status;
        this.message = message;
        this.ruleId = ruleId;
    }
}
