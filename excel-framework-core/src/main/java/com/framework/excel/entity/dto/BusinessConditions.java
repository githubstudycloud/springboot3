package com.framework.excel.entity.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 业务条件DTO
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Data
public class BusinessConditions {
    private Map<String, Object> conditions;      // 业务查询条件
    private Map<String, Object> dropdownParams;  // 下拉框参数覆盖
    private Map<String, Object> defaultValues;   // 默认值设置
    private Boolean validateOnly = false;        // 是否仅验证(导入用)
    private Boolean skipDuplicates = false;      // 是否跳过重复(导入用)
    private List<String> visibleFields;          // 可见字段(导出用)
    private String orderBy;                      // 排序条件(导出用)
    private Integer limit;                       // 行数限制(导出用)
} 