package com.platform.monitor.domain.model;

/**
 * 告警状态枚举
 */
public enum AlertStatus {
    
    /**
     * 活跃 - 告警正在发生
     */
    ACTIVE("ACTIVE", "活跃"),
    
    /**
     * 已确认 - 告警已被确认但未解决
     */
    ACKNOWLEDGED("ACKNOWLEDGED", "已确认"),
    
    /**
     * 已解决 - 告警已被解决
     */
    RESOLVED("RESOLVED", "已解决"),
    
    /**
     * 已关闭 - 告警已被手动关闭
     */
    CLOSED("CLOSED", "已关闭"),
    
    /**
     * 已过期 - 告警已过期
     */
    EXPIRED("EXPIRED", "已过期"),
    
    /**
     * 已抑制 - 告警被指定规则抑制
     */
    SUPPRESSED("SUPPRESSED", "已抑制");
    
    private final String code;
    private final String displayName;
    
    AlertStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    /**
     * 获取告警状态代码
     * 
     * @return 告警状态代码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取告警状态显示名称
     * 
     * @return 告警状态显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根据代码获取告警状态
     * 
     * @param code 告警状态代码
     * @return 告警状态枚举值，如果找不到则返回ACTIVE
     */
    public static AlertStatus fromCode(String code) {
        for (AlertStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return ACTIVE;
    }
    
    /**
     * 判断告警是否为活跃状态
     * 
     * @return 如果是活跃状态则返回true，否则返回false
     */
    public boolean isActive() {
        return this == ACTIVE || this == ACKNOWLEDGED;
    }
    
    /**
     * 判断告警是否为非活跃状态
     * 
     * @return 如果是非活跃状态则返回true，否则返回false
     */
    public boolean isInactive() {
        return !isActive();
    }
}
