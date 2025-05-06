package com.platform.common.model;

import com.platform.common.constants.CommonConstants;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 分页请求参数封装
 */
@Data
public class PageRequest {
    
    /**
     * 当前页码（从1开始）
     */
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;
    
    /**
     * 每页记录数
     */
    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = CommonConstants.System.MAX_PAGE_SIZE, message = "每页条数最大为" + CommonConstants.System.MAX_PAGE_SIZE)
    private Integer pageSize = CommonConstants.System.DEFAULT_PAGE_SIZE;
    
    /**
     * 排序字段（驼峰格式，多个字段用逗号分隔）
     */
    private String sortField;
    
    /**
     * 排序方式（asc或desc，默认升序）
     */
    private String sortOrder = "asc";
    
    /**
     * 是否进行计数查询（默认为true）
     */
    private Boolean count = true;
    
    /**
     * 查询偏移量
     *
     * @return 偏移量
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
