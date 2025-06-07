package com.platform.config.exception;

import java.util.List;

/**
 * 配置验证异常
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class ConfigValidationException extends RuntimeException {

    private final List<String> validationErrors;

    public ConfigValidationException(String message, List<String> validationErrors) {
        super(String.format("配置验证失败: %s, 错误详情: %s", message, String.join(", ", validationErrors)));
        this.validationErrors = validationErrors;
    }

    public ConfigValidationException(String message, List<String> validationErrors, Throwable cause) {
        super(String.format("配置验证失败: %s, 错误详情: %s", message, String.join(", ", validationErrors)), cause);
        this.validationErrors = validationErrors;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
} 