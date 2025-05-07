package com.example.platform.governance.metadata.interfaces.rest.mapper;

import com.example.platform.governance.metadata.domain.model.MetadataCategory;
import com.example.platform.governance.metadata.interfaces.rest.dto.CategoryResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 元数据分类API映射器，负责领域模型和API响应对象之间的转换
 */
@Component
public class CategoryApiMapper {
    
    /**
     * 将领域模型转换为API响应对象
     * 
     * @param domain 领域模型
     * @return API响应对象
     */
    public CategoryResponse toResponse(MetadataCategory domain) {
        if (domain == null) {
            return null;
        }
        
        CategoryResponse response = new CategoryResponse();
        response.setId(domain.getId());
        response.setName(domain.getName());
        response.setCode(domain.getCode());
        response.setDescription(domain.getDescription());
        response.setParentId(domain.getParentId());
        response.setPath(domain.getPath());
        response.setLevel(domain.getLevel());
        response.setLeaf(domain.isLeaf());
        response.setCreatedAt(domain.getCreatedAt());
        response.setUpdatedAt(domain.getUpdatedAt());
        
        // 递归处理子分类
        if (!domain.getChildren().isEmpty()) {
            List<CategoryResponse> childResponses = domain.getChildren().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            response.setChildren(childResponses);
        } else {
            response.setChildren(new ArrayList<>());
        }
        
        return response;
    }
}
