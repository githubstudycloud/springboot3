package com.platform.scheduler.domain.model.executor;

/**
 * 执行器状态枚举
 * 定义执行器的工作状态
 * 
 * @author platform
 */
public enum ExecutorStatus {
    
    /**
     * 在线 - 执行器正常工作中
     */
    ONLINE("online", "在线"),
    
    /**
     * 离线 - 执行器已断开连接
     */
    OFFLINE("offline", "离线"),
    
    /**
     * 繁忙 - 执行器负载过高
     */
    BUSY("busy", "繁忙"),
    
    /**
     * 暂停 - 执行器暂停接收新任务
     */
    PAUSED("paused", "暂停"),
    
    /**
     * 正在维护 - 执行器进入维护模式
     */
    MAINTENANCE("maintenance", "正在维护"),
    
    /**
     * 异常 - 执行器处于异常状态
     */
    ERROR("error", "异常");
    
    private final String code;
    private final String description;
    
    ExecutorStatus(String code, String description) {
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
     * 根据代码查找执行器状态
     *
     * @param code 状态代码
     * @return 对应的执行器状态，如果未找到则返回null
     */
    public static ExecutorStatus fromCode(String code) {
        for (ExecutorStatus status : ExecutorStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 判断执行器是否处于可用状态
     *
     * @return 如果执行器可以接收新任务，则返回true
     */
    public boolean isAvailable() {
        return this == ONLINE;
    }
}
