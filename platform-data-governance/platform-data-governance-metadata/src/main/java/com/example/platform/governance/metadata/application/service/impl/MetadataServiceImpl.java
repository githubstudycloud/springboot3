package com.example.platform.governance.metadata.application.service.impl;

import com.example.platform.governance.metadata.application.service.MetadataService;
import com.example.platform.governance.metadata.domain.model.MetadataItem;
import com.example.platform.governance.metadata.domain.model.MetadataStatus;
import com.example.platform.governance.metadata.domain.model.MetadataType;
import com.example.platform.governance.metadata.domain.repository.MetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 元数据服务实现
 */
@Service
@Transactional
public class MetadataServiceImpl implements MetadataService {
    
    private final MetadataRepository metadataRepository;
    
    @Autowired
    public MetadataServiceImpl(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }
    
    @Override
    public MetadataItem createMetadata(String name, String code, MetadataType type, String description) {
        // 检查编码是否已存在
        if (metadataRepository.findByCode(code).isPresent()) {
            throw new IllegalArgumentException("Metadata with code " + code + " already exists");
        }
        
        // 创建元数据项
        MetadataItem metadata = MetadataItem.create(name, code, type);
        metadata.setDescription(description);
        
        // 保存并返回
        return metadataRepository.save(metadata);
    }
    
    @Override
    public boolean updateMetadata(String id, String name, String description) {
        // 查找元数据项
        Optional<MetadataItem> optionalMetadata = metadataRepository.findById(id);
        if (!optionalMetadata.isPresent()) {
            return false;
        }
        
        // 更新元数据项
        MetadataItem metadata = optionalMetadata.get();
        metadata.updateBasicInfo(name, description);
        
        // 保存并返回
        metadataRepository.save(metadata);
        return true;
    }
    
    @Override
    public boolean updateStatus(String id, MetadataStatus status) {
        // 查找元数据项
        Optional<MetadataItem> optionalMetadata = metadataRepository.findById(id);
        if (!optionalMetadata.isPresent()) {
            return false;
        }
        
        // 更新状态
        MetadataItem metadata = optionalMetadata.get();
        metadata.updateStatus(status);
        
        // 保存并返回
        metadataRepository.save(metadata);
        return true;
    }
    
    @Override
    public boolean deleteMetadata(String id) {
        // 检查元数据项是否存在
        if (!metadataRepository.findById(id).isPresent()) {
            return false;
        }
        
        // 删除元数据项
        return metadataRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MetadataItem> getMetadata(String id) {
        return metadataRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MetadataItem> getMetadataByCode(String code) {
        return metadataRepository.findByCode(code);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MetadataItem> getAllMetadata(int page, int size) {
        int offset = page * size;
        return metadataRepository.findWithPagination(offset, size);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MetadataItem> getMetadataByType(MetadataType type) {
        return metadataRepository.findByType(type);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MetadataItem> getMetadataByStatus(MetadataStatus status) {
        return metadataRepository.findByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MetadataItem> getMetadataByCategory(String categoryId) {
        return metadataRepository.findByCategoryId(categoryId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MetadataItem> searchMetadata(String keyword) {
        return metadataRepository.findByNameLike(keyword);
    }
    
    @Override
    public boolean addProperty(String id, String key, String value) {
        // 查找元数据项
        Optional<MetadataItem> optionalMetadata = metadataRepository.findById(id);
        if (!optionalMetadata.isPresent()) {
            return false;
        }
        
        // 添加属性
        MetadataItem metadata = optionalMetadata.get();
        metadata.addProperty(key, value);
        
        // 保存并返回
        metadataRepository.save(metadata);
        return true;
    }
    
    @Override
    public boolean removeProperty(String id, String key) {
        // 查找元数据项
        Optional<MetadataItem> optionalMetadata = metadataRepository.findById(id);
        if (!optionalMetadata.isPresent()) {
            return false;
        }
        
        // 移除属性
        MetadataItem metadata = optionalMetadata.get();
        boolean removed = metadata.removeProperty(key);
        
        // 如果移除成功，保存并返回
        if (removed) {
            metadataRepository.save(metadata);
        }
        return removed;
    }
    
    @Override
    public boolean setOwner(String id, String ownerId, String ownerName) {
        // 查找元数据项
        Optional<MetadataItem> optionalMetadata = metadataRepository.findById(id);
        if (!optionalMetadata.isPresent()) {
            return false;
        }
        
        // 设置所有者
        MetadataItem metadata = optionalMetadata.get();
        metadata.setOwner(ownerId, ownerName);
        
        // 保存并返回
        metadataRepository.save(metadata);
        return true;
    }
    
    @Override
    public boolean setCategory(String id, String categoryId, String categoryName) {
        // 查找元数据项
        Optional<MetadataItem> optionalMetadata = metadataRepository.findById(id);
        if (!optionalMetadata.isPresent()) {
            return false;
        }
        
        // 设置分类
        MetadataItem metadata = optionalMetadata.get();
        metadata.setCategory(categoryId, categoryName);
        
        // 保存并返回
        metadataRepository.save(metadata);
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countMetadata() {
        return metadataRepository.count();
    }
}
