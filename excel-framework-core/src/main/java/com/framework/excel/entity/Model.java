package com.framework.excel.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型实体类
 *
 * @author framework
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Model extends BaseEntity {

    /**
     * 模型编码
     */
    private String code;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 模型分类ID
     */
    private Long categoryId;

    /**
     * 模型分类名称（关联查询）
     */
    private String categoryName;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 模型版本
     */
    private String version;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
}