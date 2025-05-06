package com.platform.monitor.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知渠道领域模型
 */
@Getter
public class NotificationChannel extends AbstractEntity<String> {
    
    /**
     * 通知渠道ID
     */
    private final String id;
    
    /**
     * 通知渠道名称
     */
    private String name;
    
    /**
     * 通知渠道类型
     */
    private NotificationChannelType type;
    
    /**
     * 通知渠道配置
     */
    private final Map<String, String> config;
    
    /**
     * 是否启用
     */
    private boolean enabled;
    
    /**
     * 创建时间
     */
    private final LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 通知渠道描述
     */
    private String description;
    
    /**
     * 构造函数
     *
     * @param id 通知渠道ID
     * @param name 通知渠道名称
     * @param type 通知渠道类型
     * @param description 通知渠道描述
     * @param enabled 是否启用
     */
    public NotificationChannel(String id, String name, NotificationChannelType type, String description, boolean enabled) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.enabled = enabled;
        this.config = new HashMap<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新通知渠道基本信息
     *
     * @param name 通知渠道名称
     * @param type 通知渠道类型
     * @param description 通知渠道描述
     * @return 当前通知渠道实例
     */
    public NotificationChannel updateBasicInfo(String name, NotificationChannelType type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 启用通知渠道
     *
     * @return 当前通知渠道实例
     */
    public NotificationChannel enable() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 禁用通知渠道
     *
     * @return 当前通知渠道实例
     */
    public NotificationChannel disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 添加配置项
     *
     * @param key 配置键
     * @param value 配置值
     * @return 当前通知渠道实例
     */
    public NotificationChannel addConfig(String key, String value) {
        this.config.put(key, value);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 移除配置项
     *
     * @param key 配置键
     * @return 当前通知渠道实例
     */
    public NotificationChannel removeConfig(String key) {
        this.config.remove(key);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 获取配置项值
     *
     * @param key 配置键
     * @return 配置值，如果不存在则返回null
     */
    public String getConfigValue(String key) {
        return config.get(key);
    }
    
    /**
     * 获取配置项值，如果不存在则返回默认值
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值，如果不存在则返回默认值
     */
    public String getConfigValue(String key, String defaultValue) {
        return config.getOrDefault(key, defaultValue);
    }
    
    /**
     * 验证配置是否有效
     *
     * @return 如果配置有效则返回true，否则返回false
     */
    public boolean validateConfig() {
        // 根据不同的通知渠道类型，验证必要的配置项
        switch (type) {
            case EMAIL:
                return validateEmailConfig();
            case SMS:
                return validateSmsConfig();
            case WEBHOOK:
                return validateWebhookConfig();
            case WECHAT_WORK:
                return validateWechatWorkConfig();
            case DINGTALK:
                return validateDingtalkConfig();
            case FEISHU:
                return validateFeishuConfig();
            case SLACK:
                return validateSlackConfig();
            case KAFKA:
                return validateKafkaConfig();
            case HTTP_API:
                return validateHttpApiConfig();
            case CUSTOM_SCRIPT:
                return validateCustomScriptConfig();
            default:
                return false;
        }
    }
    
    /**
     * 验证电子邮件配置
     *
     * @return 如果配置有效则返回true，否则返回false
     */
    private boolean validateEmailConfig() {
        return config.containsKey("smtp_server") && 
               config.containsKey("smtp_port") && 
               config.containsKey("sender_email") && 
               config.containsKey("recipients");
    }
    
    /**
     * 验证短信配置
     *
     * @return 如果配置有效则返回true，否则返回false
     */
    private boolean validateSmsConfig() {
        return config.containsKey("api_key") && 
               config.containsKey("api_secret") && 
               config.containsKey("phone_numbers");
    }
    
    /**
     * 验证Webhook配置
     *
     * @return 如果配置有效则返回true，否则返回false
     */
    private boolean validateWebhookConfig() {
        return config.containsKey("url") && 
               config.containsKey("method");
    }
    
    /**
     * 验证企业微信配置
     *
     * @return 如果配置有效则返回true，否则返回false
     */
    private boolean validateWechatWorkConfig() {
        return config.containsKey("corp_id") && 
               config.containsKey("corp_secret") && 
               config.containsKey("agent_id") && 
               config.containsKey("to_user");
    }
    
    /**
     * 验证钉钉配置
     *
     * @return 如果配置有效则返回true，否则返回false
     */
    private boolean validateDingtalkConfig() {
        return config.containsKey("access_token") && 
               config.containsKey("secret");
    }
    
    /**
     * 验证飞书配置
     *
     * @return 如果配置有效则返回true，否则返回false
     */
    private boolean validateFeishuConfig() {
        return config.containsKey("app_id") && 
               config.containsKey("app_secret") && 
               config.containsKey("receive_id_type") && 
               config.containsKey("receive_id");
    }
    
    /**
     * 验证Slack配置
     *
     * @return 如果配置有效则返回true，否则返回false
     */
    private boolean validateSlackConfig() {
        return config.containsKey("webhook_url");
    }
    
    /**
     * 验证Kafka配置
     *
     * @return 如果配置有效则返回true，否则返回false
     */
    private boolean validateKafkaConfig() {
        return config.containsKey("bootstrap_servers") && 
               config.containsKey("topic");
    }
    
    /**
     * 验证HTTP API配置
     *
     * @return 如果配置有效则返回true，否则返回false
     */
    private boolean validateHttpApiConfig() {
        return config.containsKey("url") && 
               config.containsKey("method") && 
               config.containsKey("content_type");
    }
    
    /**
     * 验证自定义脚本配置
     *
     * @return 如果配置有效则返回true，否则返回false
     */
    private boolean validateCustomScriptConfig() {
        return config.containsKey("script_path") || 
               config.containsKey("script_content");
    }
}
