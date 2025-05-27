package com.framework.excel.entity.business;

import lombok.Data;
import java.util.Date;

/**
 * 模型实体
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Data
public class Model {
    private Long id;
    private String code;           // 模型编码
    private String name;           // 模型名称
    private Long categoryId;       // 模型分类ID (可为空)
    private String description;    // 模型描述
    private String version;        // 模型版本
    private String modelType;      // 模型类型
    private Integer status;        // 状态 (1: Active, 0: Inactive)
    private Integer priority;      // 优先级 1-10
    private String tags;           // 模型标签 (JSON格式)
    private String createUser;     // 创建人
    private Date createTime;       // 创建时间
    private Date updateTime;       // 更新时间
} 