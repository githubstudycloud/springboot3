package com.framework.excel.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 导出请求
 *
 * @author framework
 * @since 1.0.0
 */
@Data
public class ExportRequest {

    /**
     * 查询条件
     */
    private Map<String, Object> conditions;

    /**
     * 可见字段列表
     */
    private List<String> visibleFields;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方向：ASC, DESC
     */
    private String orderDirection;

    /**
     * 页码（分页导出时使用）
     */
    private Integer pageNum;

    /**
     * 页大小（分页导出时使用）
     */
    private Integer pageSize;

    /**
     * 是否包含表头
     */
    private Boolean includeHeader;

    /**
     * 文件名
     */
    private String fileName;

    public ExportRequest() {
        this.includeHeader = true;
        this.orderDirection = "ASC";
    }
}