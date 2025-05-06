package com.platform.monitor.domain.model;

/**
 * 通知渠道类型枚举
 */
public enum NotificationChannelType {
    
    /**
     * 电子邮件
     */
    EMAIL("EMAIL", "电子邮件"),
    
    /**
     * 短信
     */
    SMS("SMS", "短信"),
    
    /**
     * WebHook
     */
    WEBHOOK("WEBHOOK", "WebHook"),
    
    /**
     * 企业微信
     */
    WECHAT_WORK("WECHAT_WORK", "企业微信"),
    
    /**
     * 钉钉
     */
    DINGTALK("DINGTALK", "钉钉"),
    
    /**
     * 飞书
     */
    FEISHU("FEISHU", "飞书"),
    
    /**
     * Slack
     */
    SLACK("SLACK", "Slack"),
    
    /**
     * Kafka消息
     */
    KAFKA("KAFKA", "Kafka消息"),
    
    /**
     * 系统消息
     */
    SYSTEM_MESSAGE("SYSTEM_MESSAGE", "系统消息"),
    
    /**
     * 自定义脚本
     */
    CUSTOM_SCRIPT("CUSTOM_SCRIPT", "自定义脚本"),
    
    /**
     * HTTP接口
     */
    HTTP_API("HTTP_API", "HTTP接口");
    
    private final String code;
    private final String displayName;
    
    NotificationChannelType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    /**
     * 获取通知渠道类型代码
     * 
     * @return 通知渠道类型代码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取通知渠道类型显示名称
     * 
     * @return 通知渠道类型显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根据代码获取通知渠道类型
     * 
     * @param code 通知渠道类型代码
     * @return 通知渠道类型枚举值，如果找不到则返回EMAIL
     */
    public static NotificationChannelType fromCode(String code) {
        for (NotificationChannelType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return EMAIL;
    }
}
