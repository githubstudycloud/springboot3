package com.example.demo.domain.model.inventory;

import java.util.Objects;

/**
 * 库存ID值对象
 */
public class InventoryId {
    private final Long value;
    
    public InventoryId(Long value) {
        this.value = value;
    }
    
    public Long getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryId that = (InventoryId) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}