package com.framework.excel.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 故障实体类
 *
 * @author framework
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Fault extends BaseEntity {

    /**
     * 故障编码（功能路径+故障名称的Hash码）
     */
    private String code;

    /**
     * 故障名称
     */
    private String name;

    /**
     * 功能ID
     */
    private Long functionId;

    /**
     * 故障分类ID
     */
    private Long classificationId;

    /**
     * 故障分类名称（关联查询）
     */
    private String classificationName;

    /**
     * 系统元素ID
     */
    private Long systemElementId;
}