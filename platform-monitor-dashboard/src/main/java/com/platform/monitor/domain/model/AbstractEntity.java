package com.platform.monitor.domain.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * 实体抽象基类，实现通用的实体功能
 * 
 * @param <ID> 实体ID类型
 */
public abstract class AbstractEntity<ID extends Serializable> implements Entity<ID> {
    
    @Override
    public boolean isNew() {
        return getId() == null;
    }
    
    @Override
    public boolean sameIdentityAs(Entity<ID> other) {
        if (other == null || !this.getClass().equals(other.getClass())) {
            return false;
        }
        
        if (this.isNew() || other.isNew()) {
            return false;
        }
        
        return Objects.equals(this.getId(), other.getId());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        @SuppressWarnings("unchecked")
        Entity<ID> other = (Entity<ID>) obj;
        return sameIdentityAs(other);
    }
    
    @Override
    public int hashCode() {
        return getId() == null ? super.hashCode() : getId().hashCode();
    }
}
