package com.example.demo.domain.repository;

import com.example.demo.domain.model.inventory.Inventory;
import com.example.demo.domain.model.inventory.InventoryId;
import com.example.demo.domain.model.order.ProductId;

import java.util.Optional;

/**
 * 库存仓储接口
 */
public interface InventoryRepository {
    /**
     * 保存库存
     */
    void save(Inventory inventory);
    
    /**
     * 根据库存ID查找库存
     */
    Optional<Inventory> findById(InventoryId inventoryId);
    
    /**
     * 根据产品ID查找库存
     */
    Optional<Inventory> findByProductId(ProductId productId);
}
