package com.platform.scheduler.register.domain.repository;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.register.domain.model.version.JobVersion;
import com.platform.scheduler.register.domain.model.version.JobVersionId;

import java.util.List;
import java.util.Optional;

/**
 * 作业版本仓储接口
 * 定义作业版本的持久化操作
 * 
 * @author platform
 */
public interface JobVersionRepository {
    
    /**
     * 保存作业版本
     * 
     * @param version 要保存的作业版本
     * @return 保存后的作业版本
     */
    JobVersion save(JobVersion version);
    
    /**
     * 根据ID查找作业版本
     * 
     * @param id 版本ID
     * @return 包含查找结果的Optional
     */
    Optional<JobVersion> findById(JobVersionId id);
    
    /**
     * 根据作业ID查找当前版本
     * 
     * @param jobId 作业ID
     * @return 包含查找结果的Optional
     */
    Optional<JobVersion> findCurrentByJobId(JobId jobId);
    
    /**
     * 查找作业的所有版本
     * 
     * @param jobId 作业ID
     * @return 版本列表，按版本号降序排序
     */
    List<JobVersion> findAllByJobId(JobId jobId);
    
    /**
     * 根据作业ID和版本号查找版本
     * 
     * @param jobId 作业ID
     * @param version 版本号
     * @return 包含查找结果的Optional
     */
    Optional<JobVersion> findByJobIdAndVersion(JobId jobId, int version);
    
    /**
     * 获取作业的最新版本号
     * 
     * @param jobId 作业ID
     * @return 最新版本号，如果没有版本则返回0
     */
    int getLatestVersionNumber(JobId jobId);
    
    /**
     * 重置当前版本标记
     * 
     * @param jobId 作业ID
     */
    void resetCurrentFlag(JobId jobId);
    
    /**
     * 设置版本为当前版本
     * 
     * @param versionId 版本ID
     */
    void setAsCurrent(JobVersionId versionId);
    
    /**
     * 删除作业的所有版本
     * 
     * @param jobId 作业ID
     * @return 删除的版本数量
     */
    int deleteAllByJobId(JobId jobId);
    
    /**
     * 删除指定版本
     * 
     * @param versionId 版本ID
     * @return 是否成功删除
     */
    boolean delete(JobVersionId versionId);
    
    /**
     * 分页查询作业版本
     * 
     * @param jobId 作业ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 版本列表
     */
    List<JobVersion> findByJobId(JobId jobId, int page, int size);
    
    /**
     * 获取作业版本总数
     * 
     * @param jobId 作业ID
     * @return 版本总数
     */
    long countByJobId(JobId jobId);
}
