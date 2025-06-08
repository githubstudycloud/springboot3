package com.platform.domain.config.template.valueobject;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

/**
 * 模板ID值对象
 * DDD值对象 - 不可变的模板标识符
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Embeddable
public class TemplateId {
    
    private String value;
    
    // JPA required
    protected TemplateId() {}
    
    private TemplateId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("模板ID不能为空");
        }
        this.value = value;
    }
    
    /**
     * 生成新的模板ID
     */
    public static TemplateId generate() {
        return new TemplateId("TPL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
    }
    
    /**
     * 从字符串创建模板ID
     */
    public static TemplateId of(String value) {
        return new TemplateId(value);
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateId that = (TemplateId) o;
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