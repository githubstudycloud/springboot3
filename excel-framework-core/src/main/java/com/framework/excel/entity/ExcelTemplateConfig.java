package com.framework.excel.entity;

/**
 * Excel模板配置实体类
 *
 * @author Framework
 * @since 1.0.0
 */
public class ExcelTemplateConfig extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 配置ID
     */
    private Long id;
    
    /**
     * 模板Key（唯一标识）
     */
    private String templateKey;
    
    /**
     * 模板名称
     */
    private String templateName;
    
    /**
     * 实体类名
     */
    private String entityClass;
    
    /**
     * 数据表名
     */
    private String tableName;
    
    /**
     * Excel表单名称
     */
    private String sheetName;
    
    /**
     * 主键策略JSON
     */
    private String primaryKeyStrategy;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    // Getters and setters
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
    
    public String getPrimaryKeyStrategy() {
        return primaryKeyStrategy;
    }
    
    public void setPrimaryKeyStrategy(String primaryKeyStrategy) {
        this.primaryKeyStrategy = primaryKeyStrategy;
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
}
