package com.example.demo.infrastructure.mapper;

import com.example.demo.domain.model.inventory.Inventory;
import com.example.demo.domain.model.inventory.InventoryId;
import com.example.demo.domain.model.inventory.InventoryStatus;
import com.example.demo.domain.model.order.ProductId;
import com.example.demo.infrastructure.entity.InventoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

/**
 * 库存对象映射器
 */
@Mapper(componentModel = "spring")
@Component
public interface InventoryMapper {
    
    @Mapping(target = "id", source = "id", qualifiedByName = "toInventoryId")
    @Mapping(target = "productId", source = "productId", qualifiedByName = "toProductId")
    @Mapping(target = "status", ignore = true) // 忽略状态映射，通过调用updateStatus()方法更新
    Inventory toDomain(InventoryEntity entity);
    
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "productId", source = "productId.value")
    @Mapping(target = "status", expression = "java(inventory.getStatus().name())")
    InventoryEntity toEntity(Inventory inventory);
    
    @Named("toInventoryId")
    default InventoryId toInventoryId(Long id) {
        if (id == null) {
            return null;
        }
        return new InventoryId(id);
    }
    
    @Named("toProductId")
    default ProductId toProductId(String productId) {
        if (productId == null) {
            return null;
        }
        return new ProductId(productId);
    }
}