package com.example.common.model;

import com.example.common.constant.CommonConstants;

import java.io.Serializable;

/**
 * 分页查询参数
 */
public class PageQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 当前页码
     */
    private Integer pageNum;
    
    /**
     * 每页数量
     */
    private Integer pageSize;
    
    /**
     * 排序字段
     */
    private String orderBy;
    
    /**
     * 排序方向（asc/desc）
     */
    private String orderDirection;
    
    /**
     * 是否统计总数
     */
    private Boolean count;

    public PageQuery() {
        this.pageNum = CommonConstants.DEFAULT_PAGE_INDEX;
        this.pageSize = CommonConstants.DEFAULT_PAGE_SIZE;
        this.count = Boolean.TRUE;
    }
    
    public PageQuery(Integer pageNum, Integer pageSize) {
        this.pageNum = (pageNum == null || pageNum < 1) ? CommonConstants.DEFAULT_PAGE_INDEX : pageNum;
        this.pageSize = (pageSize == null || pageSize < 1) ? CommonConstants.DEFAULT_PAGE_SIZE : pageSize;
        this.count = Boolean.TRUE;
    }
    
    /**
     * 获取跳过的记录数
     *
     * @return 跳过的记录数
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
    
    public Integer getPageNum() {
        return pageNum;
    }
    
    public void setPageNum(Integer pageNum) {
        this.pageNum = (pageNum == null || pageNum < 1) ? CommonConstants.DEFAULT_PAGE_INDEX : pageNum;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = (pageSize == null || pageSize < 1) ? CommonConstants.DEFAULT_PAGE_SIZE : 
                        (pageSize > CommonConstants.MAX_PAGE_SIZE ? CommonConstants.MAX_PAGE_SIZE : pageSize);
    }
    
    public String getOrderBy() {
        return orderBy;
    }
    
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
    
    public String getOrderDirection() {
        return orderDirection;
    }
    
    public void setOrderDirection(String orderDirection) {
        this.orderDirection = "desc".equalsIgnoreCase(orderDirection) ? "desc" : "asc";
    }
    
    public Boolean getCount() {
        return count;
    }
    
    public void setCount(Boolean count) {
        this.count = count == null ? Boolean.TRUE : count;
    }
    
    /**
     * 生成排序字符串
     *
     * @return 排序字符串，例如：name asc
     */
    public String getOrderByClause() {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            return null;
        }
        
        return orderBy + " " + (("desc".equalsIgnoreCase(orderDirection)) ? "desc" : "asc");
    }
} 