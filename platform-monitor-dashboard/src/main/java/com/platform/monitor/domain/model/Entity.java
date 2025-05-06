package com.platform.monitor.domain.model;

import java.io.Serializable;

/**
 * 实体接口，定义所有领域实体的基本行为
 * 
 * @param <ID> 实体ID类型
 */
public interface Entity<ID extends Serializable> {
    
    /**
     * 获取实体ID
     * 
     * @return 实体ID
     */
    ID getId();
    
    /**
     * 判断是否为新创建的实体（未持久化）
     * 
     * @return 如果实体未被持久化则返回true，否则返回false
     */
    boolean isNew();
    
    /**
     * 判断两个实体是否相等
     * 
     * @param other 另一个实体
     * @return 如果两个实体相等则返回true，否则返回false
     */
    boolean sameIdentityAs(Entity<ID> other);
}
