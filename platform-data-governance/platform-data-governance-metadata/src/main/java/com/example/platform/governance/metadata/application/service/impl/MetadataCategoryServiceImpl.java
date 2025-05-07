package com.example.platform.governance.metadata.application.service.impl;

import com.example.platform.governance.metadata.application.service.MetadataCategoryService;
import com.example.platform.governance.metadata.domain.model.MetadataCategory;
import com.example.platform.governance.metadata.domain.repository.MetadataCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 元数据分类服务实现
 */
@Service
@Transactional
public class MetadataCategoryServiceImpl implements MetadataCategoryService {
    
    private final MetadataCategoryRepository categoryRepository;
    
    @Autowired
    public MetadataCategoryServiceImpl(MetadataCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public MetadataCategory createRootCategory(String name, String code, String description) {
        // 检查编码是否已存在
        if (categoryRepository.findByCode(code).isPresent()) {
            throw new IllegalArgumentException("Category with code " + code + " already exists");
        }
        
        // 创建顶级分类
        MetadataCategory category = MetadataCategory.createRoot(name, code);
        category.setDescription(description);
        
        // 保存并返回
        return categoryRepository.save(category);
    }
    
    @Override
    public MetadataCategory createChildCategory(String name, String code, String parentId, String description) {
        // 检查编码是否已存在
        if (categoryRepository.findByCode(code).isPresent()) {
            throw new IllegalArgumentException("Category with code " + code + " already exists");
        }
        
        // 查找父分类
        Optional<MetadataCategory> optionalParent = categoryRepository.findById(parentId);
        if (!optionalParent.isPresent()) {
            throw new IllegalArgumentException("Parent category with id " + parentId + " not found");
        }
        
        // 创建子分类
        MetadataCategory parent = optionalParent.get();
        MetadataCategory category = MetadataCategory.createChild(name, code, parent);
        category.setDescription(description);
        
        // 保存并返回
        return categoryRepository.save(category);
    }
    
    @Override
    public boolean updateCategory(String id, String name, String description) {
        // 查找分类
        Optional<MetadataCategory> optionalCategory = categoryRepository.findById(id);
        if (!optionalCategory.isPresent()) {
            return false;
        }
        
        // 更新分类
        MetadataCategory category = optionalCategory.get();
        if (name != null && !name.trim().isEmpty()) {
            category.setName(name);
        }
        category.setDescription(description);
        
        // 保存并返回
        categoryRepository.save(category);
        return true;
    }
    
    @Override
    public boolean deleteCategory(String id) {
        // 检查分类是否存在
        Optional<MetadataCategory> optionalCategory = categoryRepository.findById(id);
        if (!optionalCategory.isPresent()) {
            return false;
        }
        
        // 检查分类是否有子分类或关联的元数据项
        if (categoryRepository.hasChildrenOrItems(id)) {
            throw new IllegalStateException("Category has children or associated metadata items and cannot be deleted");
        }
        
        // 删除分类
        return categoryRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MetadataCategory> getCategory(String id) {
        return categoryRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MetadataCategory> getCategoryByCode(String code) {
        return categoryRepository.findByCode(code);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MetadataCategory> getRootCategories() {
        return categoryRepository.findRoots();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MetadataCategory> getChildCategories(String parentId) {
        return categoryRepository.findByParentId(parentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public MetadataCategory getCategoryTree(String rootId) {
        return categoryRepository.getTreeById(rootId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MetadataCategory> getCategoryByPath(String path) {
        return categoryRepository.findByPath(path);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MetadataCategory> searchCategories(String keyword) {
        return categoryRepository.findByNameLike(keyword);
    }
    
    @Override
    public boolean moveCategory(String categoryId, String newParentId) {
        // 检查要移动的分类是否存在
        Optional<MetadataCategory> optionalCategory = categoryRepository.findById(categoryId);
        if (!optionalCategory.isPresent()) {
            return false;
        }
        
        MetadataCategory category = optionalCategory.get();
        
        // 如果新父分类ID为null，则移动到顶级
        if (newParentId == null) {
            // 检查当前是否已经是顶级分类
            if (category.isRoot()) {
                return true; // 已经是顶级分类，无需移动
            }
            
            // 获取当前父分类并从中移除
            String currentParentId = category.getParentId();
            Optional<MetadataCategory> optionalCurrentParent = categoryRepository.findById(currentParentId);
            if (optionalCurrentParent.isPresent()) {
                MetadataCategory currentParent = optionalCurrentParent.get();
                currentParent.removeChild(categoryId);
                categoryRepository.save(currentParent);
            }
            
            // 创建新的顶级分类
            MetadataCategory newCategory = MetadataCategory.createRoot(
                    category.getName(), category.getCode());
            newCategory.setDescription(category.getDescription());
            
            // 保存新分类并删除旧分类
            categoryRepository.save(newCategory);
            categoryRepository.deleteById(categoryId);
            
            return true;
        }
        
        // 检查新父分类是否存在
        Optional<MetadataCategory> optionalNewParent = categoryRepository.findById(newParentId);
        if (!optionalNewParent.isPresent()) {
            return false;
        }
        
        MetadataCategory newParent = optionalNewParent.get();
        
        // 检查是否将分类移动到其自身或其子分类下
        if (categoryId.equals(newParentId) || 
                newParent.getPath().startsWith(category.getPath() + "/")) {
            throw new IllegalArgumentException("Cannot move category to itself or its child");
        }
        
        // 获取当前父分类并从中移除
        if (!category.isRoot()) {
            String currentParentId = category.getParentId();
            Optional<MetadataCategory> optionalCurrentParent = categoryRepository.findById(currentParentId);
            if (optionalCurrentParent.isPresent()) {
                MetadataCategory currentParent = optionalCurrentParent.get();
                currentParent.removeChild(categoryId);
                categoryRepository.save(currentParent);
            }
        }
        
        // 创建新的子分类
        MetadataCategory newCategory = MetadataCategory.createChild(
                category.getName(), category.getCode(), newParent);
        newCategory.setDescription(category.getDescription());
        
        // 保存新分类并删除旧分类
        categoryRepository.save(newCategory);
        categoryRepository.deleteById(categoryId);
        
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countMetadataInCategory(String categoryId) {
        // 假设通过path前缀匹配的方式统计分类及其所有子分类下的元数据数量
        Optional<MetadataCategory> optionalCategory = categoryRepository.findById(categoryId);
        if (!optionalCategory.isPresent()) {
            return 0;
        }
        
        // 实际实现可能需要调用元数据仓库的特定方法
        // 这里只是一个示例逻辑
        return 0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countCategories() {
        return categoryRepository.count();
    }
}
