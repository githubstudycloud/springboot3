package com.example.demo.domain.model.inventory;

import com.example.demo.domain.event.DomainEvent;
import com.example.demo.domain.model.common.AggregateRoot;
import com.example.demo.domain.model.order.ProductId;
import lombok.Getter;

import java.util.UUID;

/**
 * 库存聚合根
 */
@Getter
public class Inventory extends AggregateRoot<InventoryId> {
    // 库存ID
    private final InventoryId id;
    
    // 产品ID
    private final ProductId productId;
    
    // 可用数量
    private int availableQuantity;
    
    // 预留数量
    private int reservedQuantity;
    
    private Inventory(InventoryId id, ProductId productId, int availableQuantity) {
        if (productId == null) {
            throw new IllegalArgumentException("产品ID不能为空");
        }
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("可用数量不能为负数");
        }
        
        this.id = id;
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = 0;
    }
    
    /**
     * 创建新库存的工厂方法
     */
    public static Inventory create(ProductId productId, int initialQuantity) {
        return new Inventory(new InventoryId(UUID.randomUUID().toString()), productId, initialQuantity);
    }
    
    /**
     * 获取实体的唯一标识
     */
    @Override
    public InventoryId getId() {
        return this.id;
    }
    
    /**
     * 增加库存
     */
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("增加的数量必须大于0");
        }
        
        this.availableQuantity += quantity;
    }
    
    /**
     * 减少库存
     */
    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("减少的数量必须大于0");
        }
        
        if (this.availableQuantity < quantity) {
            throw new IllegalStateException("可用库存不足");
        }
        
        this.availableQuantity -= quantity;
    }
    
    /**
     * 预留库存
     */
    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("预留的数量必须大于0");
        }
        
        if (this.availableQuantity < quantity) {
            throw new IllegalStateException("可用库存不足，无法预留");
        }
        
        this.availableQuantity -= quantity;
        this.reservedQuantity += quantity;
    }
    
    /**
     * 取消预留
     */
    public void cancelReservation(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("取消预留的数量必须大于0");
        }
        
        if (this.reservedQuantity < quantity) {
            throw new IllegalStateException("预留库存不足，无法取消");
        }
        
        this.reservedQuantity -= quantity;
        this.availableQuantity += quantity;
    }
    
    /**
     * 确认预留（从预留中扣减）
     */
    public void confirmReservation(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("确认的数量必须大于0");
        }
        
        if (this.reservedQuantity < quantity) {
            throw new IllegalStateException("预留库存不足，无法确认");
        }
        
        this.reservedQuantity -= quantity;
    }
    
    /**
     * 检查是否有足够的可用库存
     */
    public boolean hasEnoughStock(int quantity) {
        return this.availableQuantity >= quantity;
    }
}
