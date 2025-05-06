package com.platform.scheduler.register.domain.model.version;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 作业版本ID值对象
 * 
 * @author platform
 */
@Getter
@EqualsAndHashCode
public class JobVersionId {
    
    private final String value;
    
    private JobVersionId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("JobVersionId value cannot be null or empty");
        }
        this.value = value;
    }
    
    /**
     * 创建新的JobVersionId
     * 
     * @return 新的唯一标识符
     */
    public static JobVersionId newId() {
        return new JobVersionId(UUID.randomUUID().toString());
    }
    
    /**
     * 从现有值创建JobVersionId
     * 
     * @param id 标识符字符串
     * @return JobVersionId实例
     */
    public static JobVersionId of(String id) {
        return new JobVersionId(id);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
