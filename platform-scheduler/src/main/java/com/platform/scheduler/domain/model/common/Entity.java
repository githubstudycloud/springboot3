package com.platform.scheduler.domain.model.common;

/**
 * 实体接口
 * 实体具有唯一标识，可以改变状态，相等性通过标识确定。
 * 
 * @author platform
 */
public interface Entity<ID> {
    
    /**
     * 获取实体唯一标识
     *
     * @return 唯一标识
     */
    ID getId();
}
