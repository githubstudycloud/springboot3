package com.platform.scheduler.register.domain.repository;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.register.domain.model.dependency.DependencyDefinition;
import com.platform.scheduler.register.domain.model.dependency.DependencyDefinitionId;
import com.platform.scheduler.register.domain.model.dependency.DependencyType;

import java.util.List;
import java.util.Optional;

/**
 * 依赖关系定义仓储接口
 * 定义依赖关系的持久化操作
 * 
 * @author platform
 */
public interface DependencyDefinitionRepository {
    
    /**
     * 保存依赖关系定义
     * 
     * @param definition 要保存的依赖关系定义
     * @return 保存后的依赖关系定义
     */
    DependencyDefinition save(DependencyDefinition definition);
    
    /**
     * 根据ID查找依赖关系定义
     * 
     * @param id 依赖关系定义ID
     * @return 包含查找结果的Optional
     */
    Optional<DependencyDefinition> findById(DependencyDefinitionId id);
    
    /**
     * 查找源作业的所有依赖关系
     * 
     * @param sourceJobId 源作业ID
     * @return 依赖关系定义列表
     */
    List<DependencyDefinition> findBySourceJobId(JobId sourceJobId);
    
    /**
     * 查找目标作业的所有依赖关系
     * 
     * @param targetJobId 目标作业ID
     * @return 依赖关系定义列表
     */
    List<DependencyDefinition> findByTargetJobId(JobId targetJobId);
    
    /**
     * 查找源作业和目标作业之间的依赖关系
     * 
     * @param sourceJobId 源作业ID
     * @param targetJobId 目标作业ID
     * @return 包含查找结果的Optional
     */
    Optional<DependencyDefinition> findBySourceAndTargetJobId(JobId sourceJobId, JobId targetJobId);
    
    /**
     * 根据依赖类型查找依赖关系
     * 
     * @param type 依赖类型
     * @return 指定类型的依赖关系列表
     */
    List<DependencyDefinition> findByType(DependencyType type);
    
    /**
     * 删除依赖关系定义
     * 
     * @param id 要删除的依赖关系定义ID
     * @return 是否成功删除
     */
    boolean delete(DependencyDefinitionId id);
    
    /**
     * 删除作业的所有依赖关系
     * 
     * @param jobId 作业ID
     * @return 删除的依赖关系数量
     */
    int deleteAllByJobId(JobId jobId);
    
    /**
     * 检测作业依赖关系中是否存在循环
     * 
     * @param jobId 作业ID
     * @return 如果存在循环则返回true
     */
    boolean hasCyclicDependency(JobId jobId);
    
    /**
     * 查找直接依赖于指定作业的所有作业
     * 
     * @param jobId 作业ID
     * @return 依赖于指定作业的作业ID列表
     */
    List<JobId> findDependentJobs(JobId jobId);
    
    /**
     * 查找指定作业直接依赖的所有作业
     * 
     * @param jobId 作业ID
     * @return 指定作业依赖的作业ID列表
     */
    List<JobId> findDependencyJobs(JobId jobId);
    
    /**
     * 分页查询依赖关系定义
     * 
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 依赖关系定义列表
     */
    List<DependencyDefinition> findAll(int page, int size);
    
    /**
     * 获取依赖关系定义总数
     * 
     * @return 依赖关系定义总数
     */
    long count();
}
