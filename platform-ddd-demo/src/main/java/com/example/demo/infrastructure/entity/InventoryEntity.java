package com.example.demo.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 库存实体类 - JPA实体
 */
@Entity
@Table(name = "inventories")
@Getter
@Setter
public class InventoryEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_id", nullable = false, unique = true)
    private String productId;
    
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;
    
    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;
    
    @Column(name = "min_threshold")
    private Integer minThreshold;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 默认构造函数
     */
    public InventoryEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.reservedQuantity = 0;
    }
}