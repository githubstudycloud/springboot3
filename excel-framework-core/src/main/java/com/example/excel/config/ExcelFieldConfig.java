package com.example.excel.config;

import com.example.excel.provider.DropdownProvider;
import com.example.excel.validator.FieldValidator;

import java.util.List;

/**
 * Excel字段配置
 * 用于配置单个字段的导入导出行为
 */
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
    private Boolean required = false;
    
    /**
     * 是否可见(导出时)
     */
    private Boolean visible = true;
    
    /**
     * 列宽
     */
    private Integer width = 15;
    
    /**
     * 下拉数据提供者
     */
    private DropdownProvider dropdownProvider;
    
    /**
     * 日期格式
     */
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 验证器列表
     */
    private List<FieldValidator> validators;

    // Getters and Setters
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Integer getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public DropdownProvider getDropdownProvider() {
        return dropdownProvider;
    }

    public void setDropdownProvider(DropdownProvider dropdownProvider) {
        this.dropdownProvider = dropdownProvider;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public List<FieldValidator> getValidators() {
        return validators;
    }

    public void setValidators(List<FieldValidator> validators) {
        this.validators = validators;
    }
}
