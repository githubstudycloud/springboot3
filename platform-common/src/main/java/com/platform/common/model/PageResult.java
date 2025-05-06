package com.platform.common.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 分页结果封装
 *
 * @param <T> 数据项类型
 */
@Data
public class PageResult<T> {
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页码
     */
    private Integer pageNum;
    
    /**
     * 每页记录数
     */
    private Integer pageSize;
    
    /**
     * 总页数
     */
    private Integer pages;
    
    /**
     * 数据列表
     */
    private List<T> list;
    
    /**
     * 是否有下一页
     */
    private Boolean hasNextPage;
    
    /**
     * 是否有上一页
     */
    private Boolean hasPreviousPage;
    
    /**
     * 私有构造函数
     */
    private PageResult() {
    }
    
    /**
     * 创建空分页结果
     *
     * @param <T> 数据项类型
     * @return 空分页结果
     */
    public static <T> PageResult<T> empty() {
        return of(Collections.emptyList(), 0L, 1, 10);
    }
    
    /**
     * 创建分页结果
     *
     * @param list     数据列表
     * @param total    总记录数
     * @param pageNum  当前页码
     * @param pageSize 每页记录数
     * @param <T>      数据项类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        
        // 计算总页数
        long pages = total / pageSize;
        if (total % pageSize != 0) {
            pages++;
        }
        result.setPages((int) pages);
        
        // 设置是否有上一页和下一页
        result.setHasNextPage(pageNum < pages);
        result.setHasPreviousPage(pageNum > 1);
        
        return result;
    }
    
    /**
     * 基于PageRequest创建分页结果
     *
     * @param list        数据列表
     * @param total       总记录数
     * @param pageRequest 分页请求
     * @param <T>         数据项类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> list, Long total, PageRequest pageRequest) {
        return of(list, total, pageRequest.getPageNum(), pageRequest.getPageSize());
    }
}
