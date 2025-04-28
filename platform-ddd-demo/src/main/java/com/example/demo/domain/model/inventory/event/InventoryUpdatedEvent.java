package com.example.demo.domain.model.inventory.event;

import com.example.demo.domain.event.DomainEvent;
import com.example.demo.domain.model.inventory.InventoryId;
import com.example.demo.domain.model.inventory.InventoryStatus;
import com.example.demo.domain.model.order.ProductId;

import java.time.LocalDateTime;

/**
 * 库存更新事件
 */
public class InventoryUpdatedEvent implements DomainEvent {
    private final InventoryId inventoryId;
    private final ProductId productId;
    private final int availableQuantity;
    private final int reservedQuantity;
    private final InventoryStatus status;
    private final LocalDateTime occurredOn;

    public InventoryUpdatedEvent(InventoryId inventoryId, ProductId productId, 
                                int availableQuantity, int reservedQuantity, 
                                InventoryStatus status) {
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
        this.status = status;
        this.occurredOn = LocalDateTime.now();
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    public InventoryId getInventoryId() {
        return inventoryId;
    }

    public ProductId getProductId() {
        return productId;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public InventoryStatus getStatus() {
        return status;
    }
}