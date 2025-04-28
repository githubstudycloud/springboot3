package com.example.demo.infrastructure.repository.jpa;

import com.example.demo.infrastructure.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 库存JPA资源库接口
 */
@Repository
public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, Long> {
    
    /**
     * 根据商品ID查找库存
     * 
     * @param productId 商品ID
     * @return 库存信息
     */
    Optional<InventoryEntity> findByProductId(String productId);
}