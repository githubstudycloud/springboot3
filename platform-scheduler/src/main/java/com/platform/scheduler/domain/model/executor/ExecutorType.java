package com.platform.scheduler.domain.model.executor;

/**
 * 执行器类型枚举
 * 定义系统支持的不同类型的任务执行器
 * 
 * @author platform
 */
public enum ExecutorType {
    
    /**
     * 通用执行器 - 可以执行各种类型的作业
     */
    GENERAL("general", "通用执行器"),
    
    /**
     * 数据处理执行器 - 专门执行数据处理作业
     */
    DATA_PROCESS("data_process", "数据处理执行器"),
    
    /**
     * 报表执行器 - 专门执行报表生成作业
     */
    REPORT("report", "报表执行器"),
    
    /**
     * 消息发送执行器 - 专门执行消息通知类作业
     */
    NOTIFICATION("notification", "消息发送执行器"),
    
    /**
     * 数据同步执行器 - 专门执行数据同步类作业
     */
    DATA_SYNC("data_sync", "数据同步执行器");
    
    private final String code;
    private final String description;
    
    ExecutorType(String code, String description) {
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
     * 根据代码查找执行器类型
     *
     * @param code 类型代码
     * @return 对应的执行器类型，如果未找到则返回null
     */
    public static ExecutorType fromCode(String code) {
        for (ExecutorType type : ExecutorType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
