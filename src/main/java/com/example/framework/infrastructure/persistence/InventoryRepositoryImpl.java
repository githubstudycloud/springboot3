package com.example.framework.infrastructure.persistence;

import com.example.framework.domain.entity.Inventory;
import com.example.framework.domain.repository.InventoryRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 库存资源库实现类
 */
@Repository
@Transactional(readOnly = true)
public class InventoryRepositoryImpl extends JpaRepositoryImpl<Inventory, Long> implements InventoryRepository {
    
    /**
     * 根据商品ID查找库存
     * 
     * @param productId 商品ID
     * @return 库存信息
     */
    @Override
    public Inventory findByProductId(final Long productId) {
        return entityManager
                .createQuery("SELECT i FROM Inventory i WHERE i.productId = :productId", Inventory.class)
                .setParameter("productId", productId)
                .getSingleResult();
    }
}
