package com.platform.scheduler.register.domain.model.version;

import com.platform.scheduler.domain.model.job.JobId;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 作业版本依赖值对象
 * 记录历史版本中的作业依赖关系
 * 
 * @author platform
 */
@Getter
@Builder
@ToString
public class JobVersionDependency {
    
    /**
     * 依赖类型枚举
     */
    public enum DependencyType {
        /**
         * 前置依赖，表示当前作业必须在依赖作业成功执行后才能执行
         */
        REQUIRE_PREVIOUS_SUCCESS,
        
        /**
         * 条件依赖，表示当前作业的执行取决于依赖作业的执行结果和特定条件
         */
        CONDITIONAL,
        
        /**
         * 弱依赖，表示作业可以不依赖执行，但会记录依赖关系
         */
        WEAK
    }
    
    private final JobId dependencyJobId;
    private final String dependencyJobName;
    private final DependencyType type;
    private final String condition;
    private final String description;
    
    /**
     * 创建前置依赖关系
     * 
     * @param dependencyJobId 依赖作业ID
     * @param dependencyJobName 依赖作业名称
     * @return 作业版本依赖实例
     */
    public static JobVersionDependency requirePrevious(JobId dependencyJobId, String dependencyJobName) {
        return JobVersionDependency.builder()
                .dependencyJobId(dependencyJobId)
                .dependencyJobName(dependencyJobName)
                .type(DependencyType.REQUIRE_PREVIOUS_SUCCESS)
                .build();
    }
    
    /**
     * 创建条件依赖关系
     * 
     * @param dependencyJobId 依赖作业ID
     * @param dependencyJobName 依赖作业名称
     * @param condition 条件表达式
     * @param description 描述
     * @return 作业版本依赖实例
     */
    public static JobVersionDependency conditional(JobId dependencyJobId, String dependencyJobName, 
                                                  String condition, String description) {
        return JobVersionDependency.builder()
                .dependencyJobId(dependencyJobId)
                .dependencyJobName(dependencyJobName)
                .type(DependencyType.CONDITIONAL)
                .condition(condition)
                .description(description)
                .build();
    }
    
    /**
     * 创建弱依赖关系
     * 
     * @param dependencyJobId 依赖作业ID
     * @param dependencyJobName 依赖作业名称
     * @param description 描述
     * @return 作业版本依赖实例
     */
    public static JobVersionDependency weak(JobId dependencyJobId, String dependencyJobName, String description) {
        return JobVersionDependency.builder()
                .dependencyJobId(dependencyJobId)
                .dependencyJobName(dependencyJobName)
                .type(DependencyType.WEAK)
                .description(description)
                .build();
    }
}
