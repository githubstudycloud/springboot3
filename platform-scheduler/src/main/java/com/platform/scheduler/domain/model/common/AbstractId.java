package com.platform.scheduler.domain.model.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 领域ID抽象基类
 * 提供基于String的ID封装和相等性判断
 * 
 * @author platform
 */
@Getter
@EqualsAndHashCode
@ToString
public abstract class AbstractId implements Serializable, ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String id;
    
    protected AbstractId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        this.id = id;
    }
}
