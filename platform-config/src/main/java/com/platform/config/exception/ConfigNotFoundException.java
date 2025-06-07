package com.platform.config.exception;

/**
 * 配置未找到异常
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class ConfigNotFoundException extends RuntimeException {

    private final String application;
    private final String profile;

    public ConfigNotFoundException(String application, String profile) {
        super(String.format("配置未找到: application=%s, profile=%s", application, profile));
        this.application = application;
        this.profile = profile;
    }

    public ConfigNotFoundException(String application, String profile, Throwable cause) {
        super(String.format("配置未找到: application=%s, profile=%s", application, profile), cause);
        this.application = application;
        this.profile = profile;
    }

    public String getApplication() {
        return application;
    }

    public String getProfile() {
        return profile;
    }
} 