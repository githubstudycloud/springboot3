package com.example.demo.domain.model.common;

import java.io.Serializable;

/**
 * 实体基类
 * 所有实体应继承此类，实现实体的基于标识的相等性比较
 */
public abstract class Entity<ID> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 获取实体的唯一标识
     */
    public abstract ID getId();
    
    /**
     * 实体相等性比较仅基于其标识符
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Entity<?> entity = (Entity<?>) obj;
        
        return getId() != null && getId().equals(entity.getId());
    }
    
    /**
     * 实体的hashCode应该基于其标识符
     */
    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
    
    /**
     * 提供人类可读的实体字符串表示
     */
    @Override
    public String toString() {
        return String.format("%s[id=%s]", getClass().getSimpleName(), getId());
    }
}
