package com.platform.scheduler.domain.repository;

import com.platform.scheduler.domain.model.job.Job;
import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.job.JobStatus;
import com.platform.scheduler.domain.model.job.JobType;

import java.util.List;
import java.util.Optional;

/**
 * 作业仓储接口
 * 定义对作业聚合根的持久化操作
 * 
 * @author platform
 */
public interface JobRepository {
    
    /**
     * 保存作业
     *
     * @param job 作业实体
     * @return 保存后的作业实体
     */
    Job save(Job job);
    
    /**
     * 根据ID查找作业
     *
     * @param jobId 作业ID
     * @return 作业实体，如果不存在则返回空
     */
    Optional<Job> findById(JobId jobId);
    
    /**
     * 根据名称查找作业
     *
     * @param name 作业名称
     * @return 作业实体，如果不存在则返回空
     */
    Optional<Job> findByName(String name);
    
    /**
     * 获取所有作业
     *
     * @return 作业列表
     */
    List<Job> findAll();
    
    /**
     * 根据状态查找作业
     *
     * @param status 作业状态
     * @return 作业列表
     */
    List<Job> findByStatus(JobStatus status);
    
    /**
     * 根据类型查找作业
     *
     * @param type 作业类型
     * @return 作业列表
     */
    List<Job> findByType(JobType type);
    
    /**
     * 根据状态和类型查找作业
     *
     * @param status 作业状态
     * @param type 作业类型
     * @return 作业列表
     */
    List<Job> findByStatusAndType(JobStatus status, JobType type);
    
    /**
     * 查找可执行的作业（状态为已启用）
     *
     * @return 可执行作业列表
     */
    List<Job> findExecutableJobs();
    
    /**
     * 根据依赖作业ID查找依赖于该作业的所有作业
     *
     * @param dependencyJobId 依赖作业ID
     * @return 依赖于该作业的作业列表
     */
    List<Job> findByDependencyJobId(JobId dependencyJobId);
    
    /**
     * 删除作业
     *
     * @param jobId 作业ID
     */
    void delete(JobId jobId);
    
    /**
     * 检查作业名称是否已存在
     *
     * @param name 作业名称
     * @return 如果名称已存在，则返回true
     */
    boolean existsByName(String name);
}
