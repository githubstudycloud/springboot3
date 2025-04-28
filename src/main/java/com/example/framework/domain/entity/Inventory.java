package com.example.framework.domain.entity;

import com.example.framework.domain.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 库存实体
 */
@Entity
@Table(name = "t_inventory")
@Getter
@Setter
public class Inventory extends AbstractEntity<Long> {

    /** 商品ID */
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    /** 库存数量 */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    /** 库存状态 (0-正常, 1-不足, 2-售罄) */
    @Column(name = "status", nullable = false)
    private Integer status;
    
    /** 仓库ID */
    @Column(name = "warehouse_id")
    private Long warehouseId;
}
