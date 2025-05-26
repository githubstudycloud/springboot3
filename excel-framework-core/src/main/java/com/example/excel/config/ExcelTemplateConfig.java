package com.example.excel.config;

import java.util.List;

/**
 * Excel模板配置
 * 用于配置Excel导入导出的模板信息
 */
public class ExcelTemplateConfig {
    /**
     * 模板标识
     */
    private String templateKey;
    
    /**
     * 实体类
     */
    private Class<?> entityClass;
    
    /**
     * 表名
     */
    private String tableName;
    
    /**
     * Sheet名称
     */
    private String sheetName;
    
    /**
     * 主键策略
     */
    private PrimaryKeyStrategy primaryKeyStrategy;
    
    /**
     * 字段配置列表
     */
    private List<ExcelFieldConfig> fields;

    public String getTemplateKey() {
        return templateKey;
    }

    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
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

    public PrimaryKeyStrategy getPrimaryKeyStrategy() {
        return primaryKeyStrategy;
    }

    public void setPrimaryKeyStrategy(PrimaryKeyStrategy primaryKeyStrategy) {
        this.primaryKeyStrategy = primaryKeyStrategy;
    }

    public List<ExcelFieldConfig> getFields() {
        return fields;
    }

    public void setFields(List<ExcelFieldConfig> fields) {
        this.fields = fields;
    }
    
    /**
     * 设置字段可见性
     */
    public void setFieldVisible(String fieldName, boolean visible) {
        if (fields != null) {
            fields.stream()
                    .filter(field -> field.getFieldName().equals(fieldName))
                    .findFirst()
                    .ifPresent(field -> field.setVisible(visible));
        }
    }
    
    /**
     * 设置主键字段
     */
    public void setPrimaryKeyFields(List<String> keyFields) {
        if (primaryKeyStrategy == null) {
            primaryKeyStrategy = new PrimaryKeyStrategy();
        }
        primaryKeyStrategy.setKeyFields(keyFields);
    }
}
