package com.framework.excel.config;

import com.framework.excel.enums.DataType;
import lombok.Data;

import java.util.List;

/**
 * Excel字段配置
 *
 * @author framework
 * @since 1.0.0
 */
@Data
public class ExcelFieldConfig {

    /**
     * 实体字段名
     */
    private String fieldName;

    /**
     * Excel列名
     */
    private String columnName;

    /**
     * 列索引
     */
    private Integer columnIndex;

    /**
     * 数据类型
     */
    private DataType dataType;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 是否可见（导出时）
     */
    private Boolean visible;

    /**
     * 列宽
     */
    private Integer width;

    /**
     * 下拉数据提供者配置
     */
    private DropdownConfig dropdownProvider;

    /**
     * 日期格式
     */
    private String dateFormat;

    /**
     * 验证器列表
     */
    private List<FieldValidator> validators;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 字段描述
     */
    private String description;
}