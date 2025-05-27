package com.framework.excel.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 导出请求DTO
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Data
public class ExportRequest {
    private Map<String, Object> conditions;     // 业务查询条件（必填）
    private List<String> visibleFields;         // 导出字段（可选）
    private String orderBy;                     // 排序条件（可选）
    private Integer limit;                      // 行数限制（可选）
}
