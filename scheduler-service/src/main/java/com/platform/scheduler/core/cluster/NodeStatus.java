package com.platform.scheduler.core.cluster;

/**
 * 节点状态枚举
 * 
 * @author platform
 */
public enum NodeStatus {
    
    /**
     * 在线状态
     */
    ONLINE("ONLINE", "在线"),
    
    /**
     * 离线状态
     */
    OFFLINE("OFFLINE", "离线"),
    
    /**
     * 超时状态
     */
    TIMEOUT("TIMEOUT", "超时"),
    
    /**
     * 故障状态
     */
    FAULT("FAULT", "故障"),
    
    /**
     * 忙碌状态
     */
    BUSY("BUSY", "忙碌"),
    
    /**
     * 空闲状态
     */
    IDLE("IDLE", "空闲"),
    
    /**
     * 维护状态
     */
    MAINTENANCE("MAINTENANCE", "维护"),
    
    /**
     * 禁用状态
     */
    DISABLED("DISABLED", "禁用"),
    
    /**
     * 启动中状态
     */
    STARTING("STARTING", "启动中"),
    
    /**
     * 关闭中状态
     */
    STOPPING("STOPPING", "关闭中");
    
    private final String code;
    private final String desc;
    
    NodeStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    /**
     * 根据状态码获取状态枚举
     * 
     * @param code 状态码
     * @return 状态枚举
     */
    public static NodeStatus fromCode(String code) {
        for (NodeStatus status : NodeStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 判断是否是活跃状态
     * 
     * @return 是否活跃
     */
    public boolean isActive() {
        return this == ONLINE || this == IDLE || this == BUSY;
    }
    
    /**
     * 判断是否可分配任务
     * 
     * @return 是否可分配
     */
    public boolean isAssignable() {
        return this == ONLINE || this == IDLE;
    }
}
