package com.platform.scheduler.query.domain.model.task;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * 任务搜索结果值对象
 * 封装任务查询结果，包含分页信息
 * 
 * @author platform
 */
@Getter
@Builder
@ToString
public class TaskSearchResult<T> {

    /**
     * 结果列表
     */
    private final List<T> content;
    
    /**
     * 总记录数
     */
    private final long totalElements;
    
    /**
     * 总页数
     */
    private final int totalPages;
    
    /**
     * 当前页
     */
    private final int pageNumber;
    
    /**
     * 每页大小
     */
    private final int pageSize;
    
    /**
     * 查询耗时(毫秒)
     */
    private final long queryTimeMillis;
    
    /**
     * 是否有下一页
     *
     * @return 是否有下一页
     */
    public boolean hasNext() {
        return pageNumber < totalPages;
    }
    
    /**
     * 是否有上一页
     *
     * @return 是否有上一页
     */
    public boolean hasPrevious() {
        return pageNumber > 1;
    }
    
    /**
     * 快速创建空结果
     *
     * @param <T> 结果类型
     * @return 空结果
     */
    public static <T> TaskSearchResult<T> empty() {
        return TaskSearchResult.<T>builder()
                .content(List.of())
                .totalElements(0)
                .totalPages(0)
                .pageNumber(1)
                .pageSize(20)
                .queryTimeMillis(0)
                .build();
    }
}
