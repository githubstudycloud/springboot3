package com.framework.excel.entity;

/**
 * Excel字段配置实体类
 *
 * @author Framework
 * @since 1.0.0
 */
public class ExcelFieldConfig extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 配置ID
     */
    private Long id;
    
    /**
     * 模板配置ID
     */
    private Long templateId;
    
    /**
     * 字段名（对应实体类属性）
     */
    private String fieldName;
    
    /**
     * 列名（Excel表头）
     */
    private String columnName;
    
    /**
     * 列索引（从0开始）
     */
    private Integer columnIndex;
    
    /**
     * 是否必填
     */
    private Boolean required;
    
    /**
     * 是否可见
     */
    private Boolean visible;
    
    /**
     * 数据类型
     */
    private String dataType;
    
    /**
     * 格式化规则
     */
    private String formatPattern;
    
    /**
     * 下拉配置JSON
     */
    private String dropdownConfig;
    
    /**
     * 验证规则JSON
     */
    private String validationRule;
    
    /**
     * 排序
     */
    private Integer sort;
    
    // Getters and setters
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
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public String getFormatPattern() {
        return formatPattern;
    }
    
    public void setFormatPattern(String formatPattern) {
        this.formatPattern = formatPattern;
    }
    
    public String getDropdownConfig() {
        return dropdownConfig;
    }
    
    public void setDropdownConfig(String dropdownConfig) {
        this.dropdownConfig = dropdownConfig;
    }
    
    public String getValidationRule() {
        return validationRule;
    }
    
    public void setValidationRule(String validationRule) {
        this.validationRule = validationRule;
    }
    
    public Integer getSort() {
        return sort;
    }
    
    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
