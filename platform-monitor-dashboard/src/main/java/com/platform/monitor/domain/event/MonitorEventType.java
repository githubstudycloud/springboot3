package com.platform.monitor.domain.event;

/**
 * 监控事件类型枚举
 */
public enum MonitorEventType {
    
    /**
     * 服务实例注册事件
     */
    SERVICE_INSTANCE_REGISTERED("SERVICE_INSTANCE_REGISTERED", "服务实例注册"),
    
    /**
     * 服务实例注销事件
     */
    SERVICE_INSTANCE_DEREGISTERED("SERVICE_INSTANCE_DEREGISTERED", "服务实例注销"),
    
    /**
     * 服务实例健康状态变更事件
     */
    SERVICE_INSTANCE_HEALTH_CHANGED("SERVICE_INSTANCE_HEALTH_CHANGED", "服务实例健康状态变更"),
    
    /**
     * 指标阈值超出事件
     */
    METRIC_THRESHOLD_EXCEEDED("METRIC_THRESHOLD_EXCEEDED", "指标阈值超出"),
    
    /**
     * 指标阈值恢复事件
     */
    METRIC_THRESHOLD_RECOVERED("METRIC_THRESHOLD_RECOVERED", "指标阈值恢复"),
    
    /**
     * 告警创建事件
     */
    ALERT_CREATED("ALERT_CREATED", "告警创建"),
    
    /**
     * 告警状态变更事件
     */
    ALERT_STATUS_CHANGED("ALERT_STATUS_CHANGED", "告警状态变更"),
    
    /**
     * 告警通知发送事件
     */
    ALERT_NOTIFICATION_SENT("ALERT_NOTIFICATION_SENT", "告警通知发送"),
    
    /**
     * 系统异常事件
     */
    SYSTEM_EXCEPTION("SYSTEM_EXCEPTION", "系统异常"),
    
    /**
     * 配置变更事件
     */
    CONFIGURATION_CHANGED("CONFIGURATION_CHANGED", "配置变更"),
    
    /**
     * 系统自动恢复事件
     */
    SYSTEM_AUTO_RECOVERY("SYSTEM_AUTO_RECOVERY", "系统自动恢复"),
    
    /**
     * 用户操作事件
     */
    USER_OPERATION("USER_OPERATION", "用户操作");
    
    private final String code;
    private final String displayName;
    
    MonitorEventType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    /**
     * 获取事件类型代码
     * 
     * @return 事件类型代码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取事件类型显示名称
     * 
     * @return 事件类型显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根据代码获取事件类型
     * 
     * @param code 事件类型代码
     * @return 事件类型枚举值，如果找不到则返回USER_OPERATION
     */
    public static MonitorEventType fromCode(String code) {
        for (MonitorEventType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return USER_OPERATION;
    }
}
