package com.framework.excel.dto;

import lombok.Data;

/**
 * 导入错误信息
 *
 * @author framework
 * @since 1.0.0
 */
@Data
public class ImportError {

    /**
     * 行号
     */
    private Integer row;

    /**
     * 字段名
     */
    private String field;

    /**
     * 字段值
     */
    private Object value;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 错误类型
     */
    private String type;

    public ImportError() {
    }

    public ImportError(Integer row, String field, Object value, String message) {
        this.row = row;
        this.field = field;
        this.value = value;
        this.message = message;
    }

    public ImportError(Integer row, String field, Object value, String message, String type) {
        this.row = row;
        this.field = field;
        this.value = value;
        this.message = message;
        this.type = type;
    }
}