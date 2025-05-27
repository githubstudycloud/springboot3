package com.framework.excel.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型分类实体类
 *
 * @author framework
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModelCategory extends BaseEntity {

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 父分类名称（关联查询）
     */
    private String parentName;
}