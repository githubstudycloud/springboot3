package com.example.demo.domain.model.order;

import com.example.demo.domain.model.common.ValueObject;
import lombok.Getter;

/**
 * 客户ID值对象
 */
@Getter
public class CustomerId extends ValueObject {
    private final String value;

    public CustomerId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("客户ID不能为空");
        }
        this.value = value;
    }

    /**
     * 获取客户ID值
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerId customerId = (CustomerId) o;
        return value.equals(customerId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "CustomerId{" + value + '}';
    }
}
