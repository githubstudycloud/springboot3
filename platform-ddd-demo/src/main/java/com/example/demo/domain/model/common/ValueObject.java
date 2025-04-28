package com.example.demo.domain.model.common;

import java.io.Serializable;

/**
 * 值对象基类
 * 所有值对象应继承此类，实现值对象的不可变性和基于属性的相等性比较
 */
public abstract class ValueObject implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 值对象相等性比较基于其所有属性值
     */
    @Override
    public abstract boolean equals(Object obj);
    
    /**
     * 值对象的hashCode应该基于equals使用的相同属性
     */
    @Override
    public abstract int hashCode();
    
    /**
     * 提供人类可读的值对象字符串表示
     */
    @Override
    public abstract String toString();
}
