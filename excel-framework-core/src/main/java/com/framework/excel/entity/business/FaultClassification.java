package com.framework.excel.entity.business;

import lombok.Data;

import java.util.Date;

/**
 * 故障分类实体
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Data
public class FaultClassification {
    private Long id;
    private String name;           // 分类名称
    private Integer scopeType;     // 1: Global, 2: Local
    private Long functionTreeId;   // Function/Domain ID if local
    private Date createTime;       // 创建时间
    private Date updateTime;       // 更新时间
}
