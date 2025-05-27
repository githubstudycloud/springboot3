package com.framework.excel.config;

import lombok.Data;

import java.util.List;

/**
 * Excel模板配置
 *
 * @author framework
 * @since 1.0.0
 */
@Data
public class ExcelTemplateConfig {

    /**
     * 模板标识
     */
    private String templateKey;

    /**
     * 实体类
     */
    private String entityClass;

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

    /**
     * 模板描述
     */
    private String description;

    /**
     * 设置字段可见性
     *
     * @param fieldName 字段名
     * @param visible   是否可见
     */
    public void setFieldVisible(String fieldName, Boolean visible) {
        if (fields != null) {
            fields.stream()
                    .filter(field -> fieldName.equals(field.getFieldName()))
                    .findFirst()
                    .ifPresent(field -> field.setVisible(visible));
        }
    }

    /**
     * 设置主键字段
     *
     * @param keyFields 主键字段列表
     */
    public void setPrimaryKeyFields(List<String> keyFields) {
        if (primaryKeyStrategy == null) {
            primaryKeyStrategy = new PrimaryKeyStrategy();
        }
        primaryKeyStrategy.setKeyFields(keyFields);
    }

    /**
     * 获取可见字段列表
     *
     * @return 可见字段列表
     */
    public List<ExcelFieldConfig> getVisibleFields() {
        if (fields == null) {
            return null;
        }
        return fields.stream()
                .filter(field -> Boolean.TRUE.equals(field.getVisible()))
                .sorted((f1, f2) -> Integer.compare(f1.getColumnIndex(), f2.getColumnIndex()))
                .toList();
    }

    /**
     * 根据字段名获取字段配置
     *
     * @param fieldName 字段名
     * @return 字段配置
     */
    public ExcelFieldConfig getFieldConfig(String fieldName) {
        if (fields == null) {
            return null;
        }
        return fields.stream()
                .filter(field -> fieldName.equals(field.getFieldName()))
                .findFirst()
                .orElse(null);
    }
}