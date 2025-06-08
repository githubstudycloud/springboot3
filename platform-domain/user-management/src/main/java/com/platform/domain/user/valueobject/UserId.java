package com.platform.domain.user.valueobject;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

/**
 * 用户ID值对象
 * DDD设计模式 - 不可变值对象
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Embeddable
public class UserId {
    
    private String value;
    
    // JPA required
    protected UserId() {}
    
    private UserId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        this.value = value;
    }
    
    /**
     * 生成新的用户ID
     */
    public static UserId generate() {
        return new UserId("USR-" + UUID.randomUUID().toString().replace("-", "").toUpperCase());
    }
    
    /**
     * 从字符串创建用户ID
     */
    public static UserId of(String value) {
        return new UserId(value);
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
} 