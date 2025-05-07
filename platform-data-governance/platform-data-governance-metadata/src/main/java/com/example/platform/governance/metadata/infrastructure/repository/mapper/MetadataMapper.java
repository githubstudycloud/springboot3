package com.example.platform.governance.metadata.infrastructure.repository.mapper;

import com.example.platform.governance.metadata.domain.model.MetadataItem;
import com.example.platform.governance.metadata.domain.model.MetadataStatus;
import com.example.platform.governance.metadata.domain.model.MetadataType;
import com.example.platform.governance.metadata.infrastructure.repository.entity.MetadataEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 元数据映射器，负责领域模型和实体模型之间的转换
 */
@Component
public class MetadataMapper {
    
    /**
     * 将领域模型转换为实体模型
     * 
     * @param domain 领域模型
     * @return 实体模型
     */
    public MetadataEntity toEntity(MetadataItem domain) {
        if (domain == null) {
            return null;
        }
        
        MetadataEntity entity = new MetadataEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setCode(domain.getCode());
        entity.setDescription(domain.getDescription());
        entity.setType(domain.getType().name());
        entity.setStatus(domain.getStatus().name());
        entity.setOwnerId(domain.getOwnerId());
        entity.setOwnerName(domain.getOwnerName());
        entity.setCategoryId(domain.getCategoryId());
        entity.setCategoryName(domain.getCategoryName());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setProperties(new HashMap<>(domain.getProperties()));
        
        return entity;
    }
    
    /**
     * 将实体模型转换为领域模型
     * 
     * @param entity 实体模型
     * @return 领域模型
     */
    public MetadataItem toDomain(MetadataEntity entity) {
        if (entity == null) {
            return null;
        }
        
        // 创建元数据项
        MetadataItem domain = MetadataItem.create(
                entity.getName(),
                entity.getCode(),
                MetadataType.valueOf(entity.getType()));
        
        // 通过反射或其他方式设置ID（通常应该通过抽象工厂模式，这里简化处理）
        try {
            java.lang.reflect.Field idField = MetadataItem.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(domain, entity.getId());
            
            java.lang.reflect.Field createdAtField = MetadataItem.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(domain, entity.getCreatedAt());
            
            java.lang.reflect.Field updatedAtField = MetadataItem.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(domain, entity.getUpdatedAt());
            
            java.lang.reflect.Field statusField = MetadataItem.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(domain, MetadataStatus.valueOf(entity.getStatus()));
        } catch (Exception e) {
            throw new RuntimeException("Error mapping entity to domain", e);
        }
        
        // 设置其他属性
        domain.setDescription(entity.getDescription());
        if (entity.getOwnerId() != null && entity.getOwnerName() != null) {
            domain.setOwner(entity.getOwnerId(), entity.getOwnerName());
        }
        if (entity.getCategoryId() != null) {
            domain.setCategory(entity.getCategoryId(), entity.getCategoryName());
        }
        
        // 设置属性
        if (entity.getProperties() != null) {
            entity.getProperties().forEach(domain::addProperty);
        }
        
        return domain;
    }
}
