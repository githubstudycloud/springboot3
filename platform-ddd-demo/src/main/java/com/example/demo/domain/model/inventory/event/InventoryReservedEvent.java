package com.example.demo.domain.model.inventory.event;

import com.example.demo.domain.event.DomainEvent;
import com.example.demo.domain.model.inventory.InventoryId;
import com.example.demo.domain.model.order.ProductId;

import java.time.LocalDateTime;

/**
 * 库存预留事件
 */
public class InventoryReservedEvent implements DomainEvent {
    private final InventoryId inventoryId;
    private final ProductId productId;
    private final int reservedQuantity;
    private final int availableQuantity;
    private final int totalReservedQuantity;
    private final LocalDateTime occurredOn;

    public InventoryReservedEvent(InventoryId inventoryId, ProductId productId, 
                                 int reservedQuantity, int availableQuantity, 
                                 int totalReservedQuantity) {
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.reservedQuantity = reservedQuantity;
        this.availableQuantity = availableQuantity;
        this.totalReservedQuantity = totalReservedQuantity;
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

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public int getTotalReservedQuantity() {
        return totalReservedQuantity;
    }
}