package com.example.platform.governance.metadata.infrastructure.repository.mapper;

import com.example.platform.governance.metadata.domain.model.MetadataCategory;
import com.example.platform.governance.metadata.infrastructure.repository.entity.MetadataCategoryEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 元数据分类映射器，负责领域模型和实体模型之间的转换
 */
@Component
public class MetadataCategoryMapper {
    
    /**
     * 将领域模型转换为实体模型
     * 
     * @param domain 领域模型
     * @return 实体模型
     */
    public MetadataCategoryEntity toEntity(MetadataCategory domain) {
        if (domain == null) {
            return null;
        }
        
        MetadataCategoryEntity entity = new MetadataCategoryEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setCode(domain.getCode());
        entity.setDescription(domain.getDescription());
        entity.setParentId(domain.getParentId());
        entity.setPath(domain.getPath());
        entity.setLevel(domain.getLevel());
        entity.setLeaf(domain.isLeaf());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        if (!domain.getChildren().isEmpty()) {
            entity.setChildren(domain.getChildren().stream()
                    .map(this::toEntity)
                    .collect(Collectors.toList()));
        } else {
            entity.setChildren(new ArrayList<>());
        }
        
        return entity;
    }
    
    /**
     * 将实体模型转换为领域模型
     * 
     * @param entity 实体模型
     * @return 领域模型
     */
    public MetadataCategory toDomain(MetadataCategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        
        // 创建分类（基于是否为根节点）
        MetadataCategory domain;
        if (entity.getParentId() == null) {
            domain = MetadataCategory.createRoot(entity.getName(), entity.getCode());
        } else {
            // 为了创建子分类，我们需要父分类的领域模型
            // 这里提供一个简化的方法，实际情况下可能需要递归处理
            MetadataCategory parentStub = createParentStub(entity.getParentId(), entity.getPath());
            domain = MetadataCategory.createChild(entity.getName(), entity.getCode(), parentStub);
        }
        
        // 通过反射或其他方式设置ID（通常应该通过抽象工厂模式，这里简化处理）
        try {
            java.lang.reflect.Field idField = MetadataCategory.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(domain, entity.getId());
            
            java.lang.reflect.Field createdAtField = MetadataCategory.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(domain, entity.getCreatedAt());
            
            java.lang.reflect.Field updatedAtField = MetadataCategory.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(domain, entity.getUpdatedAt());
            
            java.lang.reflect.Field pathField = MetadataCategory.class.getDeclaredField("path");
            pathField.setAccessible(true);
            pathField.set(domain, entity.getPath());
            
            java.lang.reflect.Field levelField = MetadataCategory.class.getDeclaredField("level");
            levelField.setAccessible(true);
            levelField.set(domain, entity.getLevel());
            
            java.lang.reflect.Field leafField = MetadataCategory.class.getDeclaredField("leaf");
            leafField.setAccessible(true);
            leafField.set(domain, entity.isLeaf());
        } catch (Exception e) {
            throw new RuntimeException("Error mapping entity to domain", e);
        }
        
        // 设置其他属性
        domain.setDescription(entity.getDescription());
        
        // 递归处理子分类
        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            for (MetadataCategoryEntity childEntity : entity.getChildren()) {
                // 这里需要特殊处理，因为createChild会自动添加到父节点
                // 所以我们不能简单地调用toDomain，需要手动创建和添加
                try {
                    MetadataCategory child = MetadataCategory.createChild(
                            childEntity.getName(), childEntity.getCode(), domain);
                    child.setDescription(childEntity.getDescription());
                    
                    // 递归处理子分类的子分类
                    MapChildrenRecursively(child, childEntity.getChildren());
                } catch (Exception e) {
                    // 忽略子分类创建错误，避免整个转换失败
                    e.printStackTrace();
                }
            }
        }
        
        return domain;
    }
    
    /**
     * 递归映射子分类
     * 
     * @param parent 父分类领域模型
     * @param childrenEntities 子分类实体列表
     */
    private void MapChildrenRecursively(MetadataCategory parent, List<MetadataCategoryEntity> childrenEntities) {
        if (childrenEntities == null || childrenEntities.isEmpty()) {
            return;
        }
        
        for (MetadataCategoryEntity childEntity : childrenEntities) {
            try {
                MetadataCategory child = MetadataCategory.createChild(
                        childEntity.getName(), childEntity.getCode(), parent);
                child.setDescription(childEntity.getDescription());
                
                // 递归处理
                MapChildrenRecursively(child, childEntity.getChildren());
            } catch (Exception e) {
                // 忽略子分类创建错误
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 创建父分类存根，仅用于映射
     * 
     * @param parentId 父分类ID
     * @param childPath 子分类路径
     * @return 父分类存根
     */
    private MetadataCategory createParentStub(String parentId, String childPath) {
        String parentPath = childPath.substring(0, childPath.lastIndexOf("/"));
        String parentCode = parentPath.substring(parentPath.lastIndexOf("/") + 1);
        
        MetadataCategory parentStub = MetadataCategory.createRoot("Stub", parentCode);
        
        try {
            java.lang.reflect.Field idField = MetadataCategory.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(parentStub, parentId);
            
            java.lang.reflect.Field pathField = MetadataCategory.class.getDeclaredField("path");
            pathField.setAccessible(true);
            pathField.set(parentStub, parentPath);
        } catch (Exception e) {
            throw new RuntimeException("Error creating parent stub", e);
        }
        
        return parentStub;
    }
}
