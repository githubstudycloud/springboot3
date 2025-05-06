package com.platform.scheduler.register.application.service;

import com.platform.scheduler.register.application.dto.JobVersionDTO;

import java.util.List;

/**
 * 作业版本应用服务接口
 * 定义作业版本相关的业务操作
 * 
 * @author platform
 */
public interface JobVersionApplicationService {
    
    /**
     * 创建作业版本快照
     * 
     * @param jobId 作业ID
     * @param comment 版本备注
     * @param operator 操作者
     * @return 创建的版本DTO
     */
    JobVersionDTO createSnapshot(String jobId, String comment, String operator);
    
    /**
     * 获取作业版本详情
     * 
     * @param versionId 版本ID
     * @return 版本DTO
     */
    JobVersionDTO getVersion(String versionId);
    
    /**
     * 获取作业的当前版本
     * 
     * @param jobId 作业ID
     * @return 当前版本DTO
     */
    JobVersionDTO getCurrentVersion(String jobId);
    
    /**
     * 获取作业的所有版本
     * 
     * @param jobId 作业ID
     * @return 版本DTO列表，按版本号降序排序
     */
    List<JobVersionDTO> getAllVersionsByJobId(String jobId);
    
    /**
     * 获取作业的特定版本
     * 
     * @param jobId 作业ID
     * @param version 版本号
     * @return 版本DTO
     */
    JobVersionDTO getVersionByJobIdAndVersion(String jobId, int version);
    
    /**
     * 回滚到指定版本
     * 
     * @param versionId 要回滚到的版本ID
     * @param operator 操作者
     * @return 回滚后的当前版本DTO
     */
    JobVersionDTO rollbackToVersion(String versionId, String operator);
    
    /**
     * 比较两个版本的差异
     * 
     * @param versionId1 版本1的ID
     * @param versionId2 版本2的ID
     * @return 差异信息
     */
    String compareVersions(String versionId1, String versionId2);
    
    /**
     * 更新版本备注
     * 
     * @param versionId 版本ID
     * @param comment 新的备注信息
     * @return 更新后的版本DTO
     */
    JobVersionDTO updateVersionComment(String versionId, String comment);
    
    /**
     * 删除作业版本
     * 
     * @param versionId 版本ID
     * @return 是否成功删除
     */
    boolean deleteVersion(String versionId);
    
    /**
     * 分页查询作业版本
     * 
     * @param jobId 作业ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 版本DTO列表
     */
    List<JobVersionDTO> getVersionsByJobId(String jobId, int page, int size);
    
    /**
     * 获取作业版本总数
     * 
     * @param jobId 作业ID
     * @return 版本总数
     */
    long countVersionsByJobId(String jobId);
}
