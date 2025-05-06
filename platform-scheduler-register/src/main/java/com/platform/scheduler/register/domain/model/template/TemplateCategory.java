package com.platform.scheduler.register.domain.model.template;

/**
 * 模板分类枚举
 * 用于对模板进行分类管理
 * 
 * @author platform
 */
public enum TemplateCategory {
    
    /**
     * 数据处理类模板
     */
    DATA_PROCESSING("数据处理"),
    
    /**
     * 系统维护类模板
     */
    SYSTEM_MAINTENANCE("系统维护"),
    
    /**
     * 通知提醒类模板
     */
    NOTIFICATION("通知提醒"),
    
    /**
     * 报表类模板
     */
    REPORT("报表"),
    
    /**
     * 数据分析类模板
     */
    DATA_ANALYSIS("数据分析"),
    
    /**
     * 接口集成类模板
     */
    INTEGRATION("接口集成"),
    
    /**
     * 其他类型模板
     */
    OTHER("其他");
    
    private final String displayName;
    
    TemplateCategory(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 获取分类的显示名称
     * 
     * @return 分类显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根据名称获取模板分类
     * 
     * @param name 分类名称
     * @return 模板分类枚举值，如果未找到则返回OTHER
     */
    public static TemplateCategory fromName(String name) {
        for (TemplateCategory category : values()) {
            if (category.name().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return OTHER;
    }
}
