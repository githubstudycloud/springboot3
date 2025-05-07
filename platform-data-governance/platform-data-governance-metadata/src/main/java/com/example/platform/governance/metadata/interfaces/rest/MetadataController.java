package com.example.platform.governance.metadata.interfaces.rest;

import com.example.platform.governance.metadata.application.service.MetadataService;
import com.example.platform.governance.metadata.domain.model.MetadataItem;
import com.example.platform.governance.metadata.domain.model.MetadataStatus;
import com.example.platform.governance.metadata.domain.model.MetadataType;
import com.example.platform.governance.metadata.interfaces.rest.dto.MetadataCreateRequest;
import com.example.platform.governance.metadata.interfaces.rest.dto.MetadataResponse;
import com.example.platform.governance.metadata.interfaces.rest.dto.MetadataUpdateRequest;
import com.example.platform.governance.metadata.interfaces.rest.mapper.MetadataApiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 元数据REST控制器
 */
@RestController
@RequestMapping("/api/v1/metadata")
public class MetadataController {
    
    private final MetadataService metadataService;
    private final MetadataApiMapper apiMapper;
    
    @Autowired
    public MetadataController(MetadataService metadataService, MetadataApiMapper apiMapper) {
        this.metadataService = metadataService;
        this.apiMapper = apiMapper;
    }
    
    /**
     * 创建元数据项
     * 
     * @param request 创建请求
     * @return 创建的元数据项
     */
    @PostMapping
    public ResponseEntity<MetadataResponse> createMetadata(@RequestBody MetadataCreateRequest request) {
        MetadataItem metadata = metadataService.createMetadata(
                request.getName(), 
                request.getCode(), 
                MetadataType.valueOf(request.getType()), 
                request.getDescription());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiMapper.toResponse(metadata));
    }
    
    /**
     * 更新元数据项
     * 
     * @param id 元数据项ID
     * @param request 更新请求
     * @return 更新后的元数据项
     */
    @PutMapping("/{id}")
    public ResponseEntity<MetadataResponse> updateMetadata(
            @PathVariable String id, 
            @RequestBody MetadataUpdateRequest request) {
        
        boolean updated = metadataService.updateMetadata(
                id, 
                request.getName(), 
                request.getDescription());
        
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        
        return metadataService.getMetadata(id)
                .map(metadata -> ResponseEntity.ok(apiMapper.toResponse(metadata)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 获取元数据项
     * 
     * @param id 元数据项ID
     * @return 元数据项
     */
    @GetMapping("/{id}")
    public ResponseEntity<MetadataResponse> getMetadata(@PathVariable String id) {
        return metadataService.getMetadata(id)
                .map(metadata -> ResponseEntity.ok(apiMapper.toResponse(metadata)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 删除元数据项
     * 
     * @param id 元数据项ID
     * @return 是否成功删除
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMetadata(@PathVariable String id) {
        boolean deleted = metadataService.deleteMetadata(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 更新元数据项状态
     * 
     * @param id 元数据项ID
     * @param status 新状态
     * @return 更新后的元数据项
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<MetadataResponse> updateStatus(
            @PathVariable String id, 
            @RequestParam String status) {
        
        try {
            MetadataStatus metadataStatus = MetadataStatus.valueOf(status);
            boolean updated = metadataService.updateStatus(id, metadataStatus);
            
            if (!updated) {
                return ResponseEntity.notFound().build();
            }
            
            return metadataService.getMetadata(id)
                    .map(metadata -> ResponseEntity.ok(apiMapper.toResponse(metadata)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 设置元数据所有者
     * 
     * @param id 元数据项ID
     * @param ownerId 所有者ID
     * @param ownerName 所有者名称
     * @return 是否设置成功
     */
    @PutMapping("/{id}/owner")
    public ResponseEntity<Void> setOwner(
            @PathVariable String id, 
            @RequestParam String ownerId, 
            @RequestParam String ownerName) {
        
        boolean updated = metadataService.setOwner(id, ownerId, ownerName);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 设置元数据分类
     * 
     * @param id 元数据项ID
     * @param categoryId 分类ID
     * @param categoryName 分类名称
     * @return 是否设置成功
     */
    @PutMapping("/{id}/category")
    public ResponseEntity<Void> setCategory(
            @PathVariable String id, 
            @RequestParam String categoryId, 
            @RequestParam String categoryName) {
        
        boolean updated = metadataService.setCategory(id, categoryId, categoryName);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 添加元数据属性
     * 
     * @param id 元数据项ID
     * @param key 属性键
     * @param value 属性值
     * @return 是否添加成功
     */
    @PutMapping("/{id}/properties")
    public ResponseEntity<Void> addProperty(
            @PathVariable String id, 
            @RequestParam String key, 
            @RequestParam String value) {
        
        boolean updated = metadataService.addProperty(id, key, value);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 移除元数据属性
     * 
     * @param id 元数据项ID
     * @param key 属性键
     * @return 是否移除成功
     */
    @DeleteMapping("/{id}/properties")
    public ResponseEntity<Void> removeProperty(
            @PathVariable String id, 
            @RequestParam String key) {
        
        boolean updated = metadataService.removeProperty(id, key);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 获取所有元数据项
     * 
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 元数据项列表
     */
    @GetMapping
    public ResponseEntity<List<MetadataResponse>> getAllMetadata(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<MetadataItem> metadataItems = metadataService.getAllMetadata(page, size);
        List<MetadataResponse> responses = metadataItems.stream()
                .map(apiMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 根据类型获取元数据项
     * 
     * @param type 元数据类型
     * @return 元数据项列表
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<MetadataResponse>> getMetadataByType(
            @PathVariable String type) {
        
        try {
            MetadataType metadataType = MetadataType.valueOf(type);
            List<MetadataItem> metadataItems = metadataService.getMetadataByType(metadataType);
            List<MetadataResponse> responses = metadataItems.stream()
                    .map(apiMapper::toResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 根据状态获取元数据项
     * 
     * @param status 元数据状态
     * @return 元数据项列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MetadataResponse>> getMetadataByStatus(
            @PathVariable String status) {
        
        try {
            MetadataStatus metadataStatus = MetadataStatus.valueOf(status);
            List<MetadataItem> metadataItems = metadataService.getMetadataByStatus(metadataStatus);
            List<MetadataResponse> responses = metadataItems.stream()
                    .map(apiMapper::toResponse)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 根据编码获取元数据项
     * 
     * @param code 元数据编码
     * @return 元数据项
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<MetadataResponse> getMetadataByCode(
            @PathVariable String code) {
        
        return metadataService.getMetadataByCode(code)
                .map(metadata -> ResponseEntity.ok(apiMapper.toResponse(metadata)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据分类获取元数据项
     * 
     * @param categoryId 分类ID
     * @return 元数据项列表
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MetadataResponse>> getMetadataByCategory(
            @PathVariable String categoryId) {
        
        List<MetadataItem> metadataItems = metadataService.getMetadataByCategory(categoryId);
        List<MetadataResponse> responses = metadataItems.stream()
                .map(apiMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 搜索元数据项
     * 
     * @param keyword 关键词
     * @return 元数据项列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<MetadataResponse>> searchMetadata(
            @RequestParam String keyword) {
        
        List<MetadataItem> metadataItems = metadataService.searchMetadata(keyword);
        List<MetadataResponse> responses = metadataItems.stream()
                .map(apiMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 统计元数据数量
     * 
     * @return 元数据总数
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countMetadata() {
        long count = metadataService.countMetadata();
        return ResponseEntity.ok(count);
    }
}
