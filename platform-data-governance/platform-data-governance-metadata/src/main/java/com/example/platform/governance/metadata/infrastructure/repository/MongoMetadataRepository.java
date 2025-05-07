package com.example.platform.governance.metadata.infrastructure.repository;

import com.example.platform.governance.metadata.domain.model.MetadataItem;
import com.example.platform.governance.metadata.domain.model.MetadataStatus;
import com.example.platform.governance.metadata.domain.model.MetadataType;
import com.example.platform.governance.metadata.domain.repository.MetadataRepository;
import com.example.platform.governance.metadata.infrastructure.repository.entity.MetadataEntity;
import com.example.platform.governance.metadata.infrastructure.repository.mapper.MetadataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MongoDB实现的元数据存储库
 */
@Repository
public class MongoMetadataRepository implements MetadataRepository {
    
    private final MongoTemplate mongoTemplate;
    private final MetadataMapper metadataMapper;
    
    @Autowired
    public MongoMetadataRepository(MongoTemplate mongoTemplate, MetadataMapper metadataMapper) {
        this.mongoTemplate = mongoTemplate;
        this.metadataMapper = metadataMapper;
    }
    
    @Override
    public MetadataItem save(MetadataItem metadata) {
        MetadataEntity entity = metadataMapper.toEntity(metadata);
        entity = mongoTemplate.save(entity);
        return metadataMapper.toDomain(entity);
    }
    
    @Override
    public Optional<MetadataItem> findById(String id) {
        MetadataEntity entity = mongoTemplate.findById(id, MetadataEntity.class);
        return entity != null ? Optional.of(metadataMapper.toDomain(entity)) : Optional.empty();
    }
    
    @Override
    public Optional<MetadataItem> findByCode(String code) {
        Query query = new Query(Criteria.where("code").is(code));
        MetadataEntity entity = mongoTemplate.findOne(query, MetadataEntity.class);
        return entity != null ? Optional.of(metadataMapper.toDomain(entity)) : Optional.empty();
    }
    
    @Override
    public List<MetadataItem> findAll() {
        List<MetadataEntity> entities = mongoTemplate.findAll(MetadataEntity.class);
        return entities.stream()
                .map(metadataMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MetadataItem> findByType(MetadataType type) {
        Query query = new Query(Criteria.where("type").is(type.name()));
        List<MetadataEntity> entities = mongoTemplate.find(query, MetadataEntity.class);
        return entities.stream()
                .map(metadataMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MetadataItem> findByStatus(MetadataStatus status) {
        Query query = new Query(Criteria.where("status").is(status.name()));
        List<MetadataEntity> entities = mongoTemplate.find(query, MetadataEntity.class);
        return entities.stream()
                .map(metadataMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MetadataItem> findByCategoryId(String categoryId) {
        Query query = new Query(Criteria.where("categoryId").is(categoryId));
        List<MetadataEntity> entities = mongoTemplate.find(query, MetadataEntity.class);
        return entities.stream()
                .map(metadataMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MetadataItem> findByNameLike(String nameLike) {
        Query query = new Query(Criteria.where("name").regex(nameLike, "i"));
        List<MetadataEntity> entities = mongoTemplate.find(query, MetadataEntity.class);
        return entities.stream()
                .map(metadataMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MetadataItem> findByProperty(String propertyKey, String propertyValue) {
        Query query = new Query(Criteria.where("properties." + propertyKey).is(propertyValue));
        List<MetadataEntity> entities = mongoTemplate.find(query, MetadataEntity.class);
        return entities.stream()
                .map(metadataMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MetadataItem> findWithPagination(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Query query = new Query().with(pageable);
        List<MetadataEntity> entities = mongoTemplate.find(query, MetadataEntity.class);
        return entities.stream()
                .map(metadataMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public long count() {
        return mongoTemplate.count(new Query(), MetadataEntity.class);
    }
    
    @Override
    public boolean deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.remove(query, MetadataEntity.class).getDeletedCount() > 0;
    }
}
