package com.example.demo.domain.model.order;

import com.example.demo.domain.model.common.ValueObject;
import lombok.Getter;

/**
 * 产品ID值对象
 */
@Getter
public class ProductId extends ValueObject {
    private final String value;
    
    public ProductId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("产品ID不能为空");
        }
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ProductId productId = (ProductId) o;
        return value.equals(productId.value);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public String toString() {
        return "ProductId{" + value + '}';
    }
}
