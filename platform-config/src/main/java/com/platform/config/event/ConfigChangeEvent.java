package com.platform.config.event;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * 配置变更事件
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class ConfigChangeEvent extends ApplicationEvent {

    private final String application;
    private final String profile;
    private final String operation;
    private final String oldValue;
    private final String newValue;
    private final String operator;
    private final LocalDateTime timestamp;

    public ConfigChangeEvent(Object source, String application, String profile, 
                           String operation, String oldValue, String newValue, String operator) {
        super(source);
        this.application = application;
        this.profile = profile;
        this.operation = operation;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.operator = operator;
        this.timestamp = LocalDateTime.now();
    }

    public String getApplication() {
        return application;
    }

    public String getProfile() {
        return profile;
    }

    public String getOperation() {
        return operation;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public String getOperator() {
        return operator;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getConfigKey() {
        return String.format("%s-%s", application, profile);
    }

    @Override
    public String toString() {
        return String.format("ConfigChangeEvent{application='%s', profile='%s', operation='%s', operator='%s', timestamp=%s}", 
                           application, profile, operation, operator, timestamp);
    }
} 