package com.example.platform.governance.metadata.interfaces.rest;

import com.example.platform.governance.metadata.application.service.MetadataCategoryService;
import com.example.platform.governance.metadata.domain.model.MetadataCategory;
import com.example.platform.governance.metadata.interfaces.rest.dto.CategoryCreateRequest;
import com.example.platform.governance.metadata.interfaces.rest.dto.CategoryResponse;
import com.example.platform.governance.metadata.interfaces.rest.dto.CategoryUpdateRequest;
import com.example.platform.governance.metadata.interfaces.rest.mapper.CategoryApiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 元数据分类REST控制器
 */
@RestController
@RequestMapping("/api/v1/metadata/categories")
public class MetadataCategoryController {
    
    private final MetadataCategoryService categoryService;
    private final CategoryApiMapper apiMapper;
    
    @Autowired
    public MetadataCategoryController(MetadataCategoryService categoryService, CategoryApiMapper apiMapper) {
        this.categoryService = categoryService;
        this.apiMapper = apiMapper;
    }
    
    /**
     * 创建顶级分类
     * 
     * @param request 创建请求
     * @return 创建的分类
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createRootCategory(@RequestBody CategoryCreateRequest request) {
        MetadataCategory category = categoryService.createRootCategory(
                request.getName(), 
                request.getCode(), 
                request.getDescription());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiMapper.toResponse(category));
    }
    
    /**
     * 创建子分类
     * 
     * @param parentId 父分类ID
     * @param request 创建请求
     * @return 创建的分类
     */
    @PostMapping("/{parentId}/children")
    public ResponseEntity<CategoryResponse> createChildCategory(
            @PathVariable String parentId, 
            @RequestBody CategoryCreateRequest request) {
        
        MetadataCategory category = categoryService.createChildCategory(
                request.getName(), 
                request.getCode(), 
                parentId, 
                request.getDescription());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiMapper.toResponse(category));
    }
    
    /**
     * 更新分类
     * 
     * @param id 分类ID
     * @param request 更新请求
     * @return 更新后的分类
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable String id, 
            @RequestBody CategoryUpdateRequest request) {
        
        boolean updated = categoryService.updateCategory(
                id, 
                request.getName(), 
                request.getDescription());
        
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        
        return categoryService.getCategory(id)
                .map(category -> ResponseEntity.ok(apiMapper.toResponse(category)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 删除分类
     * 
     * @param id 分类ID
     * @return 是否成功删除
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        try {
            boolean deleted = categoryService.deleteCategory(id);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    /**
     * 获取分类
     * 
     * @param id 分类ID
     * @return 分类
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable String id) {
        return categoryService.getCategory(id)
                .map(category -> ResponseEntity.ok(apiMapper.toResponse(category)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 获取分类树
     * 
     * @param rootId 根分类ID，如果为null则获取全部树结构
     * @return 包含完整子树的分类
     */
    @GetMapping("/{rootId}/tree")
    public ResponseEntity<CategoryResponse> getCategoryTree(@PathVariable String rootId) {
        MetadataCategory category = categoryService.getCategoryTree(rootId);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(apiMapper.toResponse(category));
    }
    
    /**
     * 获取所有顶级分类
     * 
     * @return 顶级分类列表
     */
    @GetMapping("/roots")
    public ResponseEntity<List<CategoryResponse>> getRootCategories() {
        List<MetadataCategory> categories = categoryService.getRootCategories();
        List<CategoryResponse> responses = categories.stream()
                .map(apiMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 获取子分类
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<CategoryResponse>> getChildCategories(@PathVariable String parentId) {
        List<MetadataCategory> categories = categoryService.getChildCategories(parentId);
        List<CategoryResponse> responses = categories.stream()
                .map(apiMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 根据编码获取分类
     * 
     * @param code 分类编码
     * @return 分类
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<CategoryResponse> getCategoryByCode(@PathVariable String code) {
        return categoryService.getCategoryByCode(code)
                .map(category -> ResponseEntity.ok(apiMapper.toResponse(category)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据路径获取分类
     * 
     * @param path 分类路径
     * @return 分类
     */
    @GetMapping("/path")
    public ResponseEntity<CategoryResponse> getCategoryByPath(@RequestParam String path) {
        return categoryService.getCategoryByPath(path)
                .map(category -> ResponseEntity.ok(apiMapper.toResponse(category)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 搜索分类
     * 
     * @param keyword 关键词
     * @return 分类列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponse>> searchCategories(@RequestParam String keyword) {
        List<MetadataCategory> categories = categoryService.searchCategories(keyword);
        List<CategoryResponse> responses = categories.stream()
                .map(apiMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 移动分类
     * 
     * @param categoryId 要移动的分类ID
     * @param newParentId 新父分类ID，如果为null则移动到顶级
     * @return 是否移动成功
     */
    @PutMapping("/{categoryId}/move")
    public ResponseEntity<Void> moveCategory(
            @PathVariable String categoryId,
            @RequestParam(required = false) String newParentId) {
        
        try {
            boolean moved = categoryService.moveCategory(categoryId, newParentId);
            return moved ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取分类下的元数据数量
     * 
     * @param categoryId 分类ID
     * @return 元数据数量
     */
    @GetMapping("/{categoryId}/metadata/count")
    public ResponseEntity<Long> countMetadataInCategory(@PathVariable String categoryId) {
        long count = categoryService.countMetadataInCategory(categoryId);
        return ResponseEntity.ok(count);
    }
    
    /**
     * 统计分类总数
     * 
     * @return 分类总数
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCategories() {
        long count = categoryService.countCategories();
        return ResponseEntity.ok(count);
    }
}
