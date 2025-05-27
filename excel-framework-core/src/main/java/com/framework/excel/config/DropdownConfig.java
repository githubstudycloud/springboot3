package com.framework.excel.config;

import lombok.Data;

/**
 * 下拉配置
 *
 * @author framework
 * @since 1.0.0
 */
@Data
public class DropdownConfig {

    /**
     * 下拉类型：RELATED_TABLE, STATIC, DYNAMIC
     */
    private String type;

    /**
     * 关联表名（RELATED_TABLE类型）
     */
    private String tableName;

    /**
     * 值字段（RELATED_TABLE类型）
     */
    private String valueField;

    /**
     * 显示字段（RELATED_TABLE类型）
     */
    private String displayField;

    /**
     * 查询条件（RELATED_TABLE类型）
     */
    private String whereClause;

    /**
     * 是否允许为空
     */
    private Boolean allowEmpty;

    /**
     * 静态选项（STATIC类型）
     */
    private String staticOptions;

    /**
     * 动态提供者类名（DYNAMIC类型）
     */
    private String providerClass;

    public DropdownConfig() {
        this.allowEmpty = true;
    }
}