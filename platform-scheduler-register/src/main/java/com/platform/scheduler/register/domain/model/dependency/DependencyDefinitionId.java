package com.platform.scheduler.register.domain.model.dependency;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 依赖关系定义ID值对象
 * 
 * @author platform
 */
@Getter
@EqualsAndHashCode
public class DependencyDefinitionId {
    
    private final String value;
    
    private DependencyDefinitionId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("DependencyDefinitionId value cannot be null or empty");
        }
        this.value = value;
    }
    
    /**
     * 创建新的DependencyDefinitionId
     * 
     * @return 新的唯一标识符
     */
    public static DependencyDefinitionId newId() {
        return new DependencyDefinitionId(UUID.randomUUID().toString());
    }
    
    /**
     * 从现有值创建DependencyDefinitionId
     * 
     * @param id 标识符字符串
     * @return DependencyDefinitionId实例
     */
    public static DependencyDefinitionId of(String id) {
        return new DependencyDefinitionId(id);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
