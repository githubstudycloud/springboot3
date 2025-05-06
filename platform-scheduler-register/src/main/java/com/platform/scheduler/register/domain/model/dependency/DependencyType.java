package com.platform.scheduler.register.domain.model.dependency;

/**
 * 依赖类型枚举
 * 定义不同类型的作业依赖关系
 * 
 * @author platform
 */
public enum DependencyType {
    
    /**
     * 前置依赖，表示源作业必须在目标作业成功执行后才能执行
     */
    REQUIRE_PREVIOUS_SUCCESS("前置依赖"),
    
    /**
     * 条件依赖，表示源作业的执行取决于目标作业的执行结果和特定条件
     */
    CONDITIONAL("条件依赖"),
    
    /**
     * 弱依赖，表示作业可以不依赖执行，但会记录依赖关系
     */
    WEAK("弱依赖");
    
    private final String displayName;
    
    DependencyType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 获取依赖类型的显示名称
     * 
     * @return 依赖类型显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 判断是否需要条件表达式
     * 
     * @return 如果是条件依赖则返回true
     */
    public boolean requiresCondition() {
        return this == CONDITIONAL;
    }
}
