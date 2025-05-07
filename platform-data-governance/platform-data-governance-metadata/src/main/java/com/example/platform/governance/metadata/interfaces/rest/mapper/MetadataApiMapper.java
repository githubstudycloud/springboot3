package com.example.platform.governance.metadata.interfaces.rest.mapper;

import com.example.platform.governance.metadata.domain.model.MetadataItem;
import com.example.platform.governance.metadata.interfaces.rest.dto.MetadataResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 元数据API映射器，负责领域模型和API响应对象之间的转换
 */
@Component
public class MetadataApiMapper {
    
    /**
     * 将领域模型转换为API响应对象
     * 
     * @param domain 领域模型
     * @return API响应对象
     */
    public MetadataResponse toResponse(MetadataItem domain) {
        if (domain == null) {
            return null;
        }
        
        MetadataResponse response = new MetadataResponse();
        response.setId(domain.getId());
        response.setName(domain.getName());
        response.setCode(domain.getCode());
        response.setDescription(domain.getDescription());
        response.setType(domain.getType().name());
        response.setStatus(domain.getStatus().name());
        response.setOwnerId(domain.getOwnerId());
        response.setOwnerName(domain.getOwnerName());
        response.setCategoryId(domain.getCategoryId());
        response.setCategoryName(domain.getCategoryName());
        response.setCreatedAt(domain.getCreatedAt());
        response.setUpdatedAt(domain.getUpdatedAt());
        response.setProperties(new HashMap<>(domain.getProperties()));
        
        return response;
    }
}
