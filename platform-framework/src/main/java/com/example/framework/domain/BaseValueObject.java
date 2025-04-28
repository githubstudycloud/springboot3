package com.example.framework.domain;

import java.io.Serializable;

/**
 * 值对象基类
 * 值对象是没有唯一标识符的对象，使用对象中的所有属性来判断相等性
 *
 * @author platform
 * @since 1.0.0
 */
public abstract class BaseValueObject implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 判断相等性
     * 所有属性都相等时，两个值对象相等
     *
     * @param o 待比较对象
     * @return 是否相等
     */
    @Override
    public abstract boolean equals(Object o);

    /**
     * 计算哈希码
     * 必须和equals方法一致
     *
     * @return 哈希码
     */
    @Override
    public abstract int hashCode();
}