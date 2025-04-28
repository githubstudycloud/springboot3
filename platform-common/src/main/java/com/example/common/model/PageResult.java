package com.example.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果
 *
 * @param <T> 数据类型
 */
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 结果集
     */
    private List<T> list;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    public PageResult() {
        this.pageNum = 1;
        this.pageSize = 10;
        this.total = 0L;
        this.pages = 0;
        this.list = Collections.emptyList();
        this.hasPrevious = false;
        this.hasNext = false;
    }

    public PageResult(final List<T> list, final Long total, final Integer pageNum, final Integer pageSize) {
        // 防止内部表示暴露
        this.list = list != null ? new ArrayList<>(list) : Collections.emptyList();
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;

        // 计算总页数
        this.pages = (int) ((total + pageSize - 1) / pageSize);

        // 计算是否有上一页、下一页
        this.hasPrevious = pageNum > 1;
        this.hasNext = pageNum < pages;
    }

    /**
     * 创建空分页结果
     *
     * @param <T> 数据类型
     * @return 空分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>();
    }

    /**
     * 创建分页结果
     *
     * @param list     结果集
     * @param total    总数
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param <T>      数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(final List<T> list, final Long total, final Integer pageNum, final Integer pageSize) {
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(final Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(final Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(final Long total) {
        this.total = total;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(final Integer pages) {
        this.pages = pages;
    }

    /**
     * 获取列表数据（解决内部表示暴露问题）
     *
     * @return 数据列表的副本
     */
    public List<T> getList() {
        return list != null ? new ArrayList<>(list) : null;
    }

    /**
     * 设置列表数据（解决内部表示暴露问题）
     *
     * @param list 数据列表
     */
    public void setList(final List<T> list) {
        this.list = list != null ? new ArrayList<>(list) : null;
    }

    public Boolean getHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(final Boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(final Boolean hasNext) {
        this.hasNext = hasNext;
    }
}