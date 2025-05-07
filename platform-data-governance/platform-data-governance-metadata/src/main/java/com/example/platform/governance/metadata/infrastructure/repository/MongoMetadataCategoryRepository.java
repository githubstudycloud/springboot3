package com.example.platform.governance.metadata.infrastructure.repository;

import com.example.platform.governance.metadata.domain.model.MetadataCategory;
import com.example.platform.governance.metadata.domain.repository.MetadataCategoryRepository;
import com.example.platform.governance.metadata.infrastructure.repository.entity.MetadataCategoryEntity;
import com.example.platform.governance.metadata.infrastructure.repository.entity.MetadataEntity;
import com.example.platform.governance.metadata.infrastructure.repository.mapper.MetadataCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MongoDB实现的元数据分类存储库
 */
@Repository
public class MongoMetadataCategoryRepository implements MetadataCategoryRepository {
    
    private final MongoTemplate mongoTemplate;
    private final MetadataCategoryMapper categoryMapper;
    
    @Autowired
    public MongoMetadataCategoryRepository(MongoTemplate mongoTemplate, MetadataCategoryMapper categoryMapper) {
        this.mongoTemplate = mongoTemplate;
        this.categoryMapper = categoryMapper;
    }
    
    @Override
    public MetadataCategory save(MetadataCategory category) {
        MetadataCategoryEntity entity = categoryMapper.toEntity(category);
        entity = mongoTemplate.save(entity);
        return categoryMapper.toDomain(entity);
    }
    
    @Override
    public Optional<MetadataCategory> findById(String id) {
        MetadataCategoryEntity entity = mongoTemplate.findById(id, MetadataCategoryEntity.class);
        return entity != null ? Optional.of(categoryMapper.toDomain(entity)) : Optional.empty();
    }
    
    @Override
    public Optional<MetadataCategory> findByCode(String code) {
        Query query = new Query(Criteria.where("code").is(code));
        MetadataCategoryEntity entity = mongoTemplate.findOne(query, MetadataCategoryEntity.class);
        return entity != null ? Optional.of(categoryMapper.toDomain(entity)) : Optional.empty();
    }
    
    @Override
    public List<MetadataCategory> findAll() {
        List<MetadataCategoryEntity> entities = mongoTemplate.findAll(MetadataCategoryEntity.class);
        return entities.stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MetadataCategory> findRoots() {
        Query query = new Query(Criteria.where("parentId").is(null));
        List<MetadataCategoryEntity> entities = mongoTemplate.find(query, MetadataCategoryEntity.class);
        return entities.stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MetadataCategory> findByParentId(String parentId) {
        Query query = new Query(Criteria.where("parentId").is(parentId));
        List<MetadataCategoryEntity> entities = mongoTemplate.find(query, MetadataCategoryEntity.class);
        return entities.stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<MetadataCategory> findByPath(String path) {
        Query query = new Query(Criteria.where("path").is(path));
        MetadataCategoryEntity entity = mongoTemplate.findOne(query, MetadataCategoryEntity.class);
        return entity != null ? Optional.of(categoryMapper.toDomain(entity)) : Optional.empty();
    }
    
    @Override
    public List<MetadataCategory> findByLevel(int level) {
        Query query = new Query(Criteria.where("level").is(level));
        List<MetadataCategoryEntity> entities = mongoTemplate.find(query, MetadataCategoryEntity.class);
        return entities.stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MetadataCategory> findLeaves() {
        Query query = new Query(Criteria.where("leaf").is(true));
        List<MetadataCategoryEntity> entities = mongoTemplate.find(query, MetadataCategoryEntity.class);
        return entities.stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MetadataCategory> findByNameLike(String nameLike) {
        Query query = new Query(Criteria.where("name").regex(nameLike, "i"));
        List<MetadataCategoryEntity> entities = mongoTemplate.find(query, MetadataCategoryEntity.class);
        return entities.stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public MetadataCategory getTreeById(String rootId) {
        // 如果rootId为null，则获取所有顶级分类
        if (rootId == null) {
            List<MetadataCategory> roots = findRoots();
            if (roots.isEmpty()) {
                return null;
            }
            
            // 创建一个虚拟根节点，将所有顶级分类作为其子节点
            MetadataCategory virtualRoot = MetadataCategory.createRoot("Root", "root");
            for (MetadataCategory root : roots) {
                // 这里需要手动构建树，不能直接添加
                // 实际实现可能需要更复杂的逻辑
                appendChildrenRecursively(root);
            }
            
            return virtualRoot;
        }
        
        // 获取指定根节点
        Optional<MetadataCategory> optionalRoot = findById(rootId);
        if (!optionalRoot.isPresent()) {
            return null;
        }
        
        // 构建树
        MetadataCategory root = optionalRoot.get();
        appendChildrenRecursively(root);
        
        return root;
    }
    
    /**
     * 递归添加子节点
     * 
     * @param parent 父节点
     */
    private void appendChildrenRecursively(MetadataCategory parent) {
        List<MetadataCategory> children = findByParentId(parent.getId());
        for (MetadataCategory child : children) {
            // 这里不能直接添加子节点，因为领域模型的createChild方法会自动建立父子关系
            // 需要手动处理
            try {
                // 使用反射修改parent的children集合，添加child
                java.lang.reflect.Field childrenField = MetadataCategory.class.getDeclaredField("children");
                childrenField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<MetadataCategory> parentChildren = (List<MetadataCategory>) childrenField.get(parent);
                parentChildren.add(child);
                
                // 设置child的parentId
                java.lang.reflect.Field parentIdField = MetadataCategory.class.getDeclaredField("parentId");
                parentIdField.setAccessible(true);
                parentIdField.set(child, parent.getId());
            } catch (Exception e) {
                throw new RuntimeException("Error appending children", e);
            }
            
            // 递归处理子节点
            appendChildrenRecursively(child);
        }
    }
    
    @Override
    public long count() {
        return mongoTemplate.count(new Query(), MetadataCategoryEntity.class);
    }
    
    @Override
    public boolean deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.remove(query, MetadataCategoryEntity.class).getDeletedCount() > 0;
    }
    
    @Override
    public boolean hasChildrenOrItems(String id) {
        // 检查是否有子分类
        Query childQuery = new Query(Criteria.where("parentId").is(id));
        boolean hasChildren = mongoTemplate.exists(childQuery, MetadataCategoryEntity.class);
        
        if (hasChildren) {
            return true;
        }
        
        // 检查是否有关联的元数据项
        Query itemQuery = new Query(Criteria.where("categoryId").is(id));
        return mongoTemplate.exists(itemQuery, MetadataEntity.class);
    }
}
