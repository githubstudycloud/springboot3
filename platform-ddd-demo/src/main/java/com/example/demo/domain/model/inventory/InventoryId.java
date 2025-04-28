package com.example.demo.domain.model.inventory;

import com.example.demo.domain.model.common.ValueObject;
import lombok.Getter;

/**
 * 库存ID值对象
 */
@Getter
public class InventoryId extends ValueObject {
    private final String value;
    
    public InventoryId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("库存ID不能为空");
        }
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        InventoryId inventoryId = (InventoryId) o;
        return value.equals(inventoryId.value);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public String toString() {
        return "InventoryId{" + value + '}';
    }
}
