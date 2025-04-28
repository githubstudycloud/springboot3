package com.example.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应
 *
 * @author platform
 * @since 1.0.0
 */
public class PageResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 无参构造函数
     */
    public PageResponse() {
    }

    /**
     * 带参构造函数
     *
     * @param total    总记录数
     * @param list     数据列表
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param pages    总页数
     */
    public PageResponse(Long total, List<T> list, Integer pageNum, Integer pageSize, Integer pages) {
        this.total = total;
        this.list = list != null ? new ArrayList<>(list) : null;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pages;
    }

    /**
     * 获取总记录数
     *
     * @return 总记录数
     */
    public Long getTotal() {
        return total;
    }

    /**
     * 设置总记录数
     *
     * @param total 总记录数
     */
    public void setTotal(Long total) {
        this.total = total;
    }

    /**
     * 获取列表（解决内部表示暴露问题）
     *
     * @return 数据列表的副本
     */
    public List<T> getList() {
        return list != null ? new ArrayList<>(list) : null;
    }

    /**
     * 设置列表（解决内部表示暴露问题）
     *
     * @param list 数据列表
     */
    public void setList(final List<T> list) {
        this.list = list != null ? new ArrayList<>(list) : null;
    }

    /**
     * 获取当前页码
     *
     * @return 当前页码
     */
    public Integer getPageNum() {
        return pageNum;
    }

    /**
     * 设置当前页码
     *
     * @param pageNum 当前页码
     */
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * 获取每页大小
     *
     * @return 每页大小
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页大小
     *
     * @param pageSize 每页大小
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获取总页数
     *
     * @return 总页数
     */
    public Integer getPages() {
        return pages;
    }

    /**
     * 设置总页数
     *
     * @param pages 总页数
     */
    public void setPages(Integer pages) {
        this.pages = pages;
    }

    /**
     * 创建空分页结果
     *
     * @param pageRequest 分页请求
     * @param <E>         数据类型
     * @return 空分页结果
     */
    public static <E> PageResponse<E> empty(final PageRequest pageRequest) {
        PageResponse<E> response = new PageResponse<>();
        response.setTotal(0L);
        response.setList(Collections.emptyList());
        response.setPageNum(pageRequest.getPageNum());
        response.setPageSize(pageRequest.getPageSize());
        response.setPages(0);
        return response;
    }

    /**
     * 创建分页结果
     *
     * @param list        数据列表
     * @param total       总记录数
     * @param pageRequest 分页请求
     * @param <E>         数据类型
     * @return 分页结果
     */
    public static <E> PageResponse<E> of(final List<E> list, final Long total, final PageRequest pageRequest) {
        int pages = (int) (total / pageRequest.getPageSize() + (total % pageRequest.getPageSize() > 0 ? 1 : 0));
        PageResponse<E> response = new PageResponse<>();
        response.setTotal(total);
        response.setList(list != null ? new ArrayList<>(list) : null);
        response.setPageNum(pageRequest.getPageNum());
        response.setPageSize(pageRequest.getPageSize());
        response.setPages(pages);
        return response;
    }
}