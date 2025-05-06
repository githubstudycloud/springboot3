package com.platform.scheduler.domain.model.job;

/**
 * 作业类型枚举
 * 定义系统支持的不同类型的调度作业
 * 
 * @author platform
 */
public enum JobType {
    
    /**
     * 简单作业 - 单次执行，无复杂依赖
     */
    SIMPLE("simple", "简单作业"),
    
    /**
     * 定时作业 - 按照定时表达式执行
     */
    CRON("cron", "定时作业"),
    
    /**
     * 数据处理作业 - 特化的数据ETL处理作业
     */
    DATA_PROCESS("data_process", "数据处理作业"),
    
    /**
     * 工作流作业 - 包含多个步骤和依赖的复杂作业
     */
    WORKFLOW("workflow", "工作流作业"),
    
    /**
     * 报表作业 - 生成报表的专用作业
     */
    REPORT("report", "报表作业"),
    
    /**
     * 事件触发作业 - 由特定事件触发执行的作业
     */
    EVENT_DRIVEN("event_driven", "事件触发作业");
    
    private final String code;
    private final String description;
    
    JobType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码查找作业类型
     *
     * @param code 类型代码
     * @return 对应的作业类型，如果未找到则返回null
     */
    public static JobType fromCode(String code) {
        for (JobType type : JobType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
