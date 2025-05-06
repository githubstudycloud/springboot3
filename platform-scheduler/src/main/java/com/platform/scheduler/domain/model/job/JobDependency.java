package com.platform.scheduler.domain.model.job;

import com.platform.scheduler.domain.model.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 作业依赖关系值对象
 * 定义作业之间的依赖关系，包括依赖类型和执行条件
 * 
 * @author platform
 */
@Getter
@EqualsAndHashCode
@ToString
public class JobDependency implements ValueObject {
    
    /**
     * 依赖的作业ID
     */
    private final JobId dependencyJobId;
    
    /**
     * 依赖类型
     */
    private final DependencyType type;
    
    /**
     * 依赖条件
     */
    private final String condition;
    
    /**
     * 是否强制依赖（如果为true，则前置作业失败时本作业不执行）
     */
    private final boolean required;
    
    public JobDependency(JobId dependencyJobId, DependencyType type, String condition, boolean required) {
        if (dependencyJobId == null) {
            throw new IllegalArgumentException("Dependency job ID cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Dependency type cannot be null");
        }
        
        this.dependencyJobId = dependencyJobId;
        this.type = type;
        this.condition = condition;
        this.required = required;
    }
    
    /**
     * 创建前置依赖（必须在本作业之前执行完毕）
     *
     * @param dependencyJobId 依赖作业ID
     * @param required 是否为强制依赖
     * @return 新的作业依赖关系
     */
    public static JobDependency precedenceOf(JobId dependencyJobId, boolean required) {
        return new JobDependency(dependencyJobId, DependencyType.PRECEDENCE, null, required);
    }
    
    /**
     * 创建触发依赖（用于事件触发类型的作业）
     *
     * @param dependencyJobId 依赖作业ID
     * @param condition 触发条件表达式
     * @return 新的作业依赖关系
     */
    public static JobDependency triggerOf(JobId dependencyJobId, String condition) {
        return new JobDependency(dependencyJobId, DependencyType.TRIGGER, condition, true);
    }
    
    /**
     * 创建父子依赖（用于工作流中的嵌套作业）
     *
     * @param parentJobId 父作业ID
     * @return 新的作业依赖关系
     */
    public static JobDependency childOf(JobId parentJobId) {
        return new JobDependency(parentJobId, DependencyType.PARENT_CHILD, null, true);
    }
    
    /**
     * 依赖类型枚举
     */
    public enum DependencyType {
        /**
         * 前置依赖 - 依赖作业必须在本作业之前执行完毕
         */
        PRECEDENCE("precedence", "前置依赖"),
        
        /**
         * 触发依赖 - 依赖作业满足特定条件时触发本作业执行
         */
        TRIGGER("trigger", "触发依赖"),
        
        /**
         * 父子依赖 - 本作业是依赖作业的子作业，通常用于工作流
         */
        PARENT_CHILD("parent_child", "父子依赖");
        
        private final String code;
        private final String description;
        
        DependencyType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        /**
         * 根据代码查找依赖类型
         *
         * @param code 类型代码
         * @return 对应的依赖类型，如果未找到则返回null
         */
        public static DependencyType fromCode(String code) {
            for (DependencyType type : DependencyType.values()) {
                if (type.getCode().equals(code)) {
                    return type;
                }
            }
            return null;
        }
    }
}
