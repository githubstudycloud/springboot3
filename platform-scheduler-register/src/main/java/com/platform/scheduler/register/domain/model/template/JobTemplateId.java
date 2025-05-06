package com.platform.scheduler.register.domain.model.template;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 作业模板ID值对象
 * 
 * @author platform
 */
@Getter
@EqualsAndHashCode
public class JobTemplateId {
    
    private final String value;
    
    private JobTemplateId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("JobTemplateId value cannot be null or empty");
        }
        this.value = value;
    }
    
    /**
     * 创建新的JobTemplateId
     * 
     * @return 新的唯一标识符
     */
    public static JobTemplateId newId() {
        return new JobTemplateId(UUID.randomUUID().toString());
    }
    
    /**
     * 从现有值创建JobTemplateId
     * 
     * @param id 标识符字符串
     * @return JobTemplateId实例
     */
    public static JobTemplateId of(String id) {
        return new JobTemplateId(id);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
