package com.platform.monitor.domain.model;

/**
 * 健康状态枚举
 */
public enum HealthStatus {
    
    /**
     * 正常
     */
    UP("UP", "正常"),
    
    /**
     * 降级
     */
    DOWN("DOWN", "宕机"),
    
    /**
     * 未知
     */
    UNKNOWN("UNKNOWN", "未知"),
    
    /**
     * 超时
     */
    TIMEOUT("TIMEOUT", "超时"),
    
    /**
     * 异常
     */
    ERROR("ERROR", "异常"),
    
    /**
     * 警告
     */
    WARNING("WARNING", "警告"),
    
    /**
     * 离线
     */
    OFFLINE("OFFLINE", "离线");
    
    private final String code;
    private final String displayName;
    
    HealthStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    /**
     * 获取健康状态代码
     * 
     * @return 健康状态代码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取健康状态显示名称
     * 
     * @return 健康状态显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根据代码获取健康状态
     * 
     * @param code 健康状态代码
     * @return 健康状态枚举值，如果找不到则返回UNKNOWN
     */
    public static HealthStatus fromCode(String code) {
        for (HealthStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return UNKNOWN;
    }
}
