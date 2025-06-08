package com.platform.domain.config.approval.valueobject;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

/**
 * 审批ID值对象
 * DDD值对象 - 不可变的审批标识符
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Embeddable
public class ApprovalId {
    
    private String value;
    
    // JPA required
    protected ApprovalId() {}
    
    private ApprovalId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("审批ID不能为空");
        }
        this.value = value;
    }
    
    /**
     * 生成新的审批ID
     */
    public static ApprovalId generate() {
        return new ApprovalId("APV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
    }
    
    /**
     * 从字符串创建审批ID
     */
    public static ApprovalId of(String value) {
        return new ApprovalId(value);
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApprovalId that = (ApprovalId) o;
        return Objects.equals(value, that.value);
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