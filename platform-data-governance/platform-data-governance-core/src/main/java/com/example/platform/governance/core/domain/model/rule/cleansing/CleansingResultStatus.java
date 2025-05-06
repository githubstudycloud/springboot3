package com.example.platform.governance.core.domain.model.rule.cleansing;

/**
 * 清洗结果状态枚举
 * 
 * 定义清洗规则应用后的状态
 */
public enum CleansingResultStatus {
    
    /**
     * 成功 - 清洗成功完成
     */
    SUCCESS("成功"),
    
    /**
     * 警告 - 清洗完成但有警告
     */
    WARNING("警告"),
    
    /**
     * 失败 - 清洗失败
     */
    FAILURE("失败"),
    
    /**
     * 跳过 - 规则被跳过未应用
     */
    SKIPPED("跳过");
    
    private final String displayName;
    
    CleansingResultStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 获取状态显示名称
     * 
     * @return
     */
    public String getDisplayName() {
        return displayName;
    }
}
