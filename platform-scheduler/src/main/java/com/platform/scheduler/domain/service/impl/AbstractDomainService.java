package com.platform.scheduler.domain.service.impl;

import com.platform.scheduler.domain.event.common.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 领域服务抽象基类
 * 为所有领域服务提供基础功能
 *
 * @author platform
 */
public abstract class AbstractDomainService {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 领域事件发布器
     */
    protected final DomainEventPublisher eventPublisher;
    
    /**
     * 构造方法
     *
     * @param eventPublisher 领域事件发布器
     */
    protected AbstractDomainService(DomainEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * 检查参数非空
     *
     * @param value 待检查的值
     * @param paramName 参数名称
     * @throws IllegalArgumentException 如果值为null则抛出此异常
     */
    protected void requireNonNull(Object value, String paramName) {
        if (value == null) {
            throw new IllegalArgumentException(paramName + " cannot be null");
        }
    }
    
    /**
     * 检查字符串非空
     *
     * @param value 待检查的字符串
     * @param paramName 参数名称
     * @throws IllegalArgumentException 如果字符串为null或空则抛出此异常
     */
    protected void requireNonEmpty(String value, String paramName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(paramName + " cannot be null or empty");
        }
    }
    
    /**
     * 检查数值为正数
     *
     * @param value 待检查的数值
     * @param paramName 参数名称
     * @throws IllegalArgumentException 如果数值不是正数则抛出此异常
     */
    protected void requirePositive(Number value, String paramName) {
        if (value == null) {
            throw new IllegalArgumentException(paramName + " cannot be null");
        }
        
        if (value instanceof Integer && (Integer) value <= 0) {
            throw new IllegalArgumentException(paramName + " must be positive");
        } else if (value instanceof Long && (Long) value <= 0) {
            throw new IllegalArgumentException(paramName + " must be positive");
        } else if (value instanceof Double && (Double) value <= 0) {
            throw new IllegalArgumentException(paramName + " must be positive");
        }
    }
}
