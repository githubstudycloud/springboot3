package com.example.demo.domain.model.order;

import com.example.demo.domain.model.common.ValueObject;
import lombok.Getter;

/**
 * 订单ID值对象
 */
@Getter
public class OrderId extends ValueObject {
    private final String value;

    public OrderId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        this.value = value;
    }

    /**
     * 获取订单ID值
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderId orderId = (OrderId) o;
        return value.equals(orderId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "OrderId{" + value + '}';
    }
}
