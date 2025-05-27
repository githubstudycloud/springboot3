package com.framework.excel.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 故障分类实体类
 *
 * @author framework
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FaultClassification extends BaseEntity {

    /**
     * 分类名称
     */
    private String name;

    /**
     * 范围类型：1-全局，2-本地
     */
    private Integer scopeType;

    /**
     * 功能树ID（如果是本地范围）
     */
    private Long functionTreeId;
}