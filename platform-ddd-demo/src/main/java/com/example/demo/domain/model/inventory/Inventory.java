package com.example.demo.domain.model.inventory;

import com.example.demo.domain.event.DomainEvent;
import com.example.demo.domain.model.inventory.event.InventoryReservedEvent;
import com.example.demo.domain.model.inventory.event.InventoryUpdatedEvent;
import com.example.demo.domain.model.order.ProductId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 库存领域实体
 */
public class Inventory {
    private InventoryId id;
    private ProductId productId;
    private int availableQuantity;
    private int reservedQuantity;
    private int minThreshold;
    private InventoryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 初始化库存
     */
    public Inventory(InventoryId id, ProductId productId, int availableQuantity, int minThreshold) {
        this.id = id;
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = 0;
        this.minThreshold = minThreshold;
        updateStatus();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * JPA存储使用的构造函数
     */
    protected Inventory() {
    }

    /**
     * 检查是否有足够库存
     */
    public boolean hasEnoughStock(int quantity) {
        return availableQuantity >= quantity;
    }

    /**
     * 预留库存
     */
    public void reserve(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalStateException("库存不足，无法预留: " + productId.getValue());
        }

        availableQuantity -= quantity;
        reservedQuantity += quantity;
        updateStatus();
        updatedAt = LocalDateTime.now();

        // 发布库存预留事件
        domainEvents.add(new InventoryReservedEvent(
                this.id,
                this.productId,
                quantity,
                this.availableQuantity,
                this.reservedQuantity
        ));
    }

    /**
     * 取消预留
     */
    public void cancelReservation(int quantity) {
        if (reservedQuantity < quantity) {
            throw new IllegalStateException("预留库存不足，无法取消: " + productId.getValue());
        }

        availableQuantity += quantity;
        reservedQuantity -= quantity;
        updateStatus();
        updatedAt = LocalDateTime.now();

        // 发布库存更新事件
        domainEvents.add(new InventoryUpdatedEvent(
                this.id,
                this.productId,
                this.availableQuantity,
                this.reservedQuantity,
                this.status
        ));
    }

    /**
     * 确认预留（实际扣减）
     */
    public void confirmReservation(int quantity) {
        if (reservedQuantity < quantity) {
            throw new IllegalStateException("预留库存不足，无法确认: " + productId.getValue());
        }

        reservedQuantity -= quantity;
        updateStatus();
        updatedAt = LocalDateTime.now();

        // 发布库存更新事件
        domainEvents.add(new InventoryUpdatedEvent(
                this.id,
                this.productId,
                this.availableQuantity,
                this.reservedQuantity,
                this.status
        ));
    }

    /**
     * 补充库存
     */
    public void restock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("补充库存量必须大于0");
        }

        availableQuantity += quantity;
        updateStatus();
        updatedAt = LocalDateTime.now();

        // 发布库存更新事件
        domainEvents.add(new InventoryUpdatedEvent(
                this.id,
                this.productId,
                this.availableQuantity,
                this.reservedQuantity,
                this.status
        ));
    }

    /**
     * 更新库存状态
     */
    private void updateStatus() {
        if (availableQuantity <= 0) {
            status = InventoryStatus.OUT_OF_STOCK;
        } else if (availableQuantity < minThreshold) {
            status = InventoryStatus.LOW_STOCK;
        } else {
            status = InventoryStatus.IN_STOCK;
        }
    }

    /**
     * 获取并清空领域事件
     */
    public List<DomainEvent> popDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    // Getters
    public InventoryId getId() {
        return id;
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

    public int getMinThreshold() {
        return minThreshold;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters for JPA
    public void setId(InventoryId id) {
        this.id = id;
    }

    public void setProductId(ProductId productId) {
        this.productId = productId;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public void setReservedQuantity(int reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public void setMinThreshold(int minThreshold) {
        this.minThreshold = minThreshold;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}