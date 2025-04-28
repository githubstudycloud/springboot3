package com.example.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
     * 创建空分页结果
     *
     * @param pageRequest 分页请求
     * @param <T>         数据类型
     * @return 空分页结果
     */
    public static <T> PageResponse<T> empty(final PageRequest pageRequest) {
        return PageResponse.<T>builder()
                .total(0L)
                .list(Collections.emptyList())
                .pageNum(pageRequest.getPageNum())
                .pageSize(pageRequest.getPageSize())
                .pages(0)
                .build();
    }

    /**
     * 创建分页结果
     *
     * @param list        数据列表
     * @param total       总记录数
     * @param pageRequest 分页请求
     * @param <T>         数据类型
     * @return 分页结果
     */
    public static <T> PageResponse<T> of(final List<T> list, final Long total, final PageRequest pageRequest) {
        int pages = (int) (total / pageRequest.getPageSize() + (total % pageRequest.getPageSize() > 0 ? 1 : 0));
        return PageResponse.<T>builder()
                .total(total)
                .list(list != null ? new ArrayList<>(list) : null)
                .pageNum(pageRequest.getPageNum())
                .pageSize(pageRequest.getPageSize())
                .pages(pages)
                .build();
    }
    
    /**
     * 解决Builder中的内部表示暴露问题
     */
    public static class PageResponseBuilder<T> {
        public PageResponseBuilder<T> list(final List<T> list) {
            this.list = list != null ? new ArrayList<>(list) : null;
            return this;
        }
    }
}