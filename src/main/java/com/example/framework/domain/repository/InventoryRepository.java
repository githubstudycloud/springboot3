package com.example.framework.domain.repository;

import com.example.framework.domain.entity.Inventory;
import org.springframework.stereotype.Repository;

/**
 * 库存资源库接口
 */
@Repository
public interface InventoryRepository extends DomainRepository<Inventory, Long> {
    
    /**
     * 根据商品ID查找库存
     * 
     * @param productId 商品ID
     * @return 库存信息
     */
    Inventory findByProductId(Long productId);
}
