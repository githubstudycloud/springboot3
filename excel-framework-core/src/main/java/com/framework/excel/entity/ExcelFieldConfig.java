package com.framework.excel.entity;

import com.framework.excel.enums.DataType;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Excel字段配置实体
 * 
 * @author framework
 * @since 1.0.0
 */
public class ExcelFieldConfig {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * Excel模板配置ID
     */
    private Long templateId;
    
    /**
     * 实体字段名
     */
    private String fieldName;
    
    /**
     * Excel列名
     */
    private String columnName;
    
    /**
     * 列索引(从0开始)
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
     * 是否可见(导出时)
     */
    private Boolean visible;
    
    /**
     * 列宽
     */
    private Integer width;
    
    /**
     * 日期格式
     */
    private String dateFormat;
    
    /**
     * 下拉配置(JSON格式)
     */
    private Map<String, Object> dropdownConfig;
    
    /**
     * 验证规则(JSON格式)
     */
    private Map<String, Object> validationRules;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 字段描述
     */
    private String description;
    
    /**
     * 状态: 1-启用, 0-禁用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    // 构造函数
    public ExcelFieldConfig() {}
    
    public ExcelFieldConfig(String fieldName, String columnName, Integer columnIndex, DataType dataType) {
        this.fieldName = fieldName;
        this.columnName = columnName;
        this.columnIndex = columnIndex;
        this.dataType = dataType;
        this.required = false;
        this.visible = true;
        this.width = 15;
        this.status = 1;
        this.sortOrder = columnIndex;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    // Getter and Setter methods
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
    
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
    
    public String getDateFormat() {
        return dateFormat;
    }
    
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    public Map<String, Object> getDropdownConfig() {
        return dropdownConfig;
    }
    
    public void setDropdownConfig(Map<String, Object> dropdownConfig) {
        this.dropdownConfig = dropdownConfig;
    }
    
    public Map<String, Object> getValidationRules() {
        return validationRules;
    }
    
    public void setValidationRules(Map<String, Object> validationRules) {
        this.validationRules = validationRules;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    @Override
    public String toString() {
        return "ExcelFieldConfig{" +
                "id=" + id +
                ", templateId=" + templateId +
                ", fieldName='" + fieldName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", columnIndex=" + columnIndex +
                ", dataType=" + dataType +
                ", required=" + required +
                ", visible=" + visible +
                ", width=" + width +
                ", dateFormat='" + dateFormat + '\'' +
                ", dropdownConfig=" + dropdownConfig +
                ", validationRules=" + validationRules +
                ", sortOrder=" + sortOrder +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
} 