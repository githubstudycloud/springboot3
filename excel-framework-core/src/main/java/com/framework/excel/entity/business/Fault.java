package com.framework.excel.entity.business;

import lombok.Data;

import java.util.Date;

/**
 * 故障实体
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Data
public class Fault {
    private Long id;
    private String code;                 // 故障编码 (Hash code of function path + fault name)
    private String name;                 // 故障名称
    private Long functionId;             // 功能ID
    private Long classificationId;       // 故障分类ID
    private Long systemElementId;        // 系统元素ID
    private Date createTime;             // 创建时间
    private Date updateTime;             // 更新时间
}
