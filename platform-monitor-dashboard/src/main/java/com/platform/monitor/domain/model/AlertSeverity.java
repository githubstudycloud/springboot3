package com.platform.monitor.domain.model;

/**
 * 告警级别枚举
 */
public enum AlertSeverity {
    
    /**
     * 严重告警 - 需要立即处理的紧急问题
     */
    CRITICAL("CRITICAL", "严重", 1),
    
    /**
     * 重要告警 - 需要尽快处理的重要问题
     */
    HIGH("HIGH", "重要", 2),
    
    /**
     * 中等告警 - 需要关注但不急迫的问题
     */
    MEDIUM("MEDIUM", "中等", 3),
    
    /**
     * 低级告警 - 需要注意但优先级较低的问题
     */
    LOW("LOW", "低级", 4),
    
    /**
     * 信息通知 - 仅供参考的信息性通知
     */
    INFO("INFO", "信息", 5);
    
    private final String code;
    private final String displayName;
    private final int priority;
    
    AlertSeverity(String code, String displayName, int priority) {
        this.code = code;
        this.displayName = displayName;
        this.priority = priority;
    }
    
    /**
     * 获取告警级别代码
     * 
     * @return 告警级别代码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取告警级别显示名称
     * 
     * @return 告警级别显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取告警级别优先级
     * 
     * @return 告警级别优先级(数字越小优先级越高)
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * 根据代码获取告警级别
     * 
     * @param code 告警级别代码
     * @return 告警级别枚举值，如果找不到则返回INFO
     */
    public static AlertSeverity fromCode(String code) {
        for (AlertSeverity severity : values()) {
            if (severity.getCode().equals(code)) {
                return severity;
            }
        }
        return INFO;
    }
    
    /**
     * 根据优先级获取告警级别
     * 
     * @param priority 优先级值
     * @return 告警级别枚举值，如果找不到则返回INFO
     */
    public static AlertSeverity fromPriority(int priority) {
        for (AlertSeverity severity : values()) {
            if (severity.getPriority() == priority) {
                return severity;
            }
        }
        return INFO;
    }
}
