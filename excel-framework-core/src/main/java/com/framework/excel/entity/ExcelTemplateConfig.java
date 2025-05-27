package com.framework.excel.entity;

import com.framework.excel.enums.UpdateMode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Excel模板配置实体
 * 
 * @author framework
 * @since 1.0.0
 */
public class ExcelTemplateConfig {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 模板标识Key
     */
    private String templateKey;
    
    /**
     * 模板名称
     */
    private String templateName;
    
    /**
     * 实体类全限定名
     */
    private String entityClass;
    
    /**
     * 数据库表名
     */
    private String tableName;
    
    /**
     * Excel Sheet名称
     */
    private String sheetName;
    
    /**
     * 主键字段列表(JSON格式)
     */
    private List<String> primaryKeyFields;
    
    /**
     * 更新模式
     */
    private UpdateMode updateMode;
    
    /**
     * 模板描述
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
    
    /**
     * 字段配置列表
     */
    private List<ExcelFieldConfig> fields;
    
    // 构造函数
    public ExcelTemplateConfig() {}
    
    public ExcelTemplateConfig(String templateKey, String templateName) {
        this.templateKey = templateKey;
        this.templateName = templateName;
        this.status = 1;
        this.updateMode = UpdateMode.INSERT_OR_UPDATE;
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
    
    public String getTemplateKey() {
        return templateKey;
    }
    
    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
    }
    
    public String getTemplateName() {
        return templateName;
    }
    
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    
    public String getEntityClass() {
        return entityClass;
    }
    
    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getSheetName() {
        return sheetName;
    }
    
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    
    public List<String> getPrimaryKeyFields() {
        return primaryKeyFields;
    }
    
    public void setPrimaryKeyFields(List<String> primaryKeyFields) {
        this.primaryKeyFields = primaryKeyFields;
    }
    
    public UpdateMode getUpdateMode() {
        return updateMode;
    }
    
    public void setUpdateMode(UpdateMode updateMode) {
        this.updateMode = updateMode;
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
    
    public List<ExcelFieldConfig> getFields() {
        return fields;
    }
    
    public void setFields(List<ExcelFieldConfig> fields) {
        this.fields = fields;
    }
    
    @Override
    public String toString() {
        return "ExcelTemplateConfig{" +
                "id=" + id +
                ", templateKey='" + templateKey + '\'' +
                ", templateName='" + templateName + '\'' +
                ", entityClass='" + entityClass + '\'' +
                ", tableName='" + tableName + '\'' +
                ", sheetName='" + sheetName + '\'' +
                ", primaryKeyFields=" + primaryKeyFields +
                ", updateMode=" + updateMode +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
} 