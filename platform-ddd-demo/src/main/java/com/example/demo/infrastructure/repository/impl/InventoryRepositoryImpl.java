package com.example.demo.infrastructure.repository.impl;

import com.example.demo.domain.model.inventory.Inventory;
import com.example.demo.domain.model.inventory.InventoryId;
import com.example.demo.domain.model.inventory.InventoryStatus;
import com.example.demo.domain.model.order.ProductId;
import com.example.demo.domain.repository.InventoryRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 库存仓储实现类 - 内存版本
 */
@Repository
@Primary
public class InventoryRepositoryImpl implements InventoryRepository {
    
    private final Map<Long, Inventory> inventories = new HashMap<>();
    private final Map<String, Long> productIdToInventoryId = new HashMap<>();
    private long nextId = 1;

    public InventoryRepositoryImpl() {
        // 初始化一些测试数据
        addInventory("P001", 100, 10);
        addInventory("P002", 50, 5);
        addInventory("P003", 20, 15);
        addInventory("P004", 0, 5);
    }

    private void addInventory(String productId, int quantity, int minThreshold) {
        InventoryId inventoryId = new InventoryId(nextId++);
        Inventory inventory = new Inventory(
                inventoryId,
                new ProductId(productId),
                quantity,
                minThreshold
        );
        inventories.put(inventoryId.getValue(), inventory);
        productIdToInventoryId.put(productId, inventoryId.getValue());
    }

    @Override
    public void save(Inventory inventory) {
        inventories.put(inventory.getId().getValue(), inventory);
        productIdToInventoryId.put(
                inventory.getProductId().getValue(),
                inventory.getId().getValue()
        );
    }

    @Override
    public Optional<Inventory> findById(InventoryId inventoryId) {
        return Optional.ofNullable(inventories.get(inventoryId.getValue()));
    }

    @Override
    public Optional<Inventory> findByProductId(ProductId productId) {
        Long inventoryId = productIdToInventoryId.get(productId.getValue());
        if (inventoryId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(inventories.get(inventoryId));
    }
}