package com.framework.excel.entity.business;

import lombok.Data;
import java.util.Date;

/**
 * 模型分类实体
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Data
public class ModelCategory {
    private Long id;
    private String name;           // 分类名称
    private String description;    // 分类描述
    private Long parentId;         // 父分类ID
    private Integer levelNo;       // 分类层级
    private Integer sortOrder;     // 排序顺序
    private Boolean enabled;       // 是否启用
    private Date createTime;       // 创建时间
    private Date updateTime;       // 更新时间
} 