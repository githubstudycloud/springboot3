package com.example.demo.domain.model.inventory;

/**
 * 库存状态枚举
 */
public enum InventoryStatus {
    /**
     * 正常库存
     */
    IN_STOCK,
    
    /**
     * 库存不足（低于最小阈值）
     */
    LOW_STOCK,
    
    /**
     * 售罄
     */
    OUT_OF_STOCK
}