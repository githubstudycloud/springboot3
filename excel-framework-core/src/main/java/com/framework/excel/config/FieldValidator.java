package com.framework.excel.config;

import lombok.Data;

/**
 * 字段验证器配置
 *
 * @author framework
 * @since 1.0.0
 */
@Data
public class FieldValidator {

    /**
     * 验证器类型
     */
    private String type;

    /**
     * 验证参数
     */
    private String params;

    /**
     * 错误消息
     */
    private String message;

    public FieldValidator() {
    }

    public FieldValidator(String type, String params, String message) {
        this.type = type;
        this.params = params;
        this.message = message;
    }
}