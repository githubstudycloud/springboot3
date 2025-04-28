package com.example.framework.core;

import java.io.Serializable;

/**
 * 基础实体接口
 * 所有领域模型实体应实现此接口
 *
 * @param <ID> 实体ID类型
 */
public interface BaseEntity<ID> extends Serializable {
    
    /**
     * 获取实体ID
     *
     * @return 实体ID
     */
    ID getId();
    
    /**
     * 设置实体ID
     *
     * @param id 实体ID
     */
    void setId(ID id);
    
    /**
     * 判断是否为新建实体（未持久化）
     *
     * @return 如果是新建实体返回true，否则返回false
     */
    default boolean isNew() {
        return getId() == null;
    }
    
    /**
     * 实体是否有效
     *
     * @return 有效返回true，否则返回false
     */
    default boolean isValid() {
        return true;
    }
} 