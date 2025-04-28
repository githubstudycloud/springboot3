package com.example.common.model;

import com.example.common.constant.SystemConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 分页请求
 *
 * @author platform
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码不能小于1")
    @Builder.Default
    private Integer pageNum = SystemConstants.DEFAULT_PAGE_NUM;

    /**
     * 每页条数
     */
    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 1000, message = "每页条数不能超过1000")
    @Builder.Default
    private Integer pageSize = SystemConstants.DEFAULT_PAGE_SIZE;

    /**
     * 排序字段
     */
    @Builder.Default
    private String sortField = SystemConstants.DEFAULT_SORT_FIELD;

    /**
     * 排序方向（asc/desc）
     */
    @Builder.Default
    private String sortDirection = SystemConstants.DEFAULT_SORT_DIRECTION;

    /**
     * 获取偏移量
     *
     * @return 偏移量
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}