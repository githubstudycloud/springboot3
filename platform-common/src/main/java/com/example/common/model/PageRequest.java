package com.example.common.model;

import com.example.common.constant.SystemConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.io.Serializable;

/**
 * 分页请求
 *
 * @author platform
 * @since 1.0.0
 */
public class PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = SystemConstants.DEFAULT_PAGE_NUM;

    /**
     * 每页条数
     */
    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 1000, message = "每页条数不能超过1000")
    private Integer pageSize = SystemConstants.DEFAULT_PAGE_SIZE;

    /**
     * 排序字段
     */
    private String sortField = SystemConstants.DEFAULT_SORT_FIELD;

    /**
     * 排序方向（asc/desc）
     */
    private String sortDirection = SystemConstants.DEFAULT_SORT_DIRECTION;

    /**
     * 无参构造函数
     */
    public PageRequest() {
    }

    /**
     * 带参构造函数
     *
     * @param pageNum       页码（从1开始）
     * @param pageSize      每页条数
     * @param sortField     排序字段
     * @param sortDirection 排序方向（asc/desc）
     */
    public PageRequest(Integer pageNum, Integer pageSize, String sortField, String sortDirection) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.sortField = sortField;
        this.sortDirection = sortDirection;
    }

    /**
     * 获取页码
     *
     * @return 页码
     */
    public Integer getPageNum() {
        return pageNum;
    }

    /**
     * 设置页码
     *
     * @param pageNum 页码
     */
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * 获取每页条数
     *
     * @return 每页条数
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页条数
     *
     * @param pageSize 每页条数
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获取排序字段
     *
     * @return 排序字段
     */
    public String getSortField() {
        return sortField;
    }

    /**
     * 设置排序字段
     *
     * @param sortField 排序字段
     */
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    /**
     * 获取排序方向
     *
     * @return 排序方向
     */
    public String getSortDirection() {
        return sortDirection;
    }

    /**
     * 设置排序方向
     *
     * @param sortDirection 排序方向
     */
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    /**
     * 获取偏移量
     *
     * @return 偏移量
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}