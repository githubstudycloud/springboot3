package com.example.framework.service;

import com.example.framework.domain.entity.Inventory;
import com.example.framework.domain.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 库存服务实现类
 */
@Service
public class InventoryService {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryService.class);
    
    private final InventoryRepository inventoryRepository;
    
    /**
     * 构造方法
     * 
     * @param inventoryRepository 库存资源库
     */
    public InventoryService(final InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    
    /**
     * 根据商品ID获取库存
     * 
     * @param productId 商品ID
     * @return 库存信息
     */
    public Inventory getByProductId(final Long productId) {
        LOG.info("获取商品库存信息，商品ID: {}", productId);
        return inventoryRepository.findByProductId(productId);
    }
    
    /**
     * 更新库存
     * 
     * @param inventory 库存信息
     * @return 更新后的库存
     */
    public Inventory updateInventory(final Inventory inventory) {
        LOG.info("更新商品库存信息，商品ID: {}", inventory.getProductId());
        return inventoryRepository.save(inventory);
    }
}
