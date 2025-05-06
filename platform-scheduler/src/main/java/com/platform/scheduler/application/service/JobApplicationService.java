package com.platform.scheduler.application.service;

import com.platform.scheduler.application.command.CreateJobCommand;
import com.platform.scheduler.application.command.TriggerJobCommand;
import com.platform.scheduler.application.command.UpdateJobCommand;
import com.platform.scheduler.application.dto.JobDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 作业应用服务接口
 * 
 * @author platform
 */
public interface JobApplicationService {
    
    /**
     * 创建作业
     *
     * @param command 创建作业命令
     * @return 创建的作业DTO
     */
    JobDTO createJob(CreateJobCommand command);
    
    /**
     * 更新作业
     *
     * @param command 更新作业命令
     * @return 更新后的作业DTO
     */
    JobDTO updateJob(UpdateJobCommand command);
    
    /**
     * 启用作业
     *
     * @param jobId 作业ID
     * @param modifier 修改者
     * @return 更新后的作业DTO
     */
    JobDTO enableJob(String jobId, String modifier);
    
    /**
     * 禁用作业
     *
     * @param jobId 作业ID
     * @param modifier 修改者
     * @return 更新后的作业DTO
     */
    JobDTO disableJob(String jobId, String modifier);
    
    /**
     * 归档作业
     *
     * @param jobId 作业ID
     * @param modifier 修改者
     * @return 更新后的作业DTO
     */
    JobDTO archiveJob(String jobId, String modifier);
    
    /**
     * 删除作业（逻辑删除）
     *
     * @param jobId 作业ID
     * @param modifier 修改者
     * @return 操作是否成功
     */
    boolean deleteJob(String jobId, String modifier);
    
    /**
     * 触发作业立即执行
     *
     * @param command 触发作业命令
     * @return 触发生成的任务实例ID
     */
    String triggerJob(TriggerJobCommand command);
    
    /**
     * 根据ID获取作业
     *
     * @param jobId 作业ID
     * @return 作业DTO
     */
    JobDTO getJobById(String jobId);
    
    /**
     * 根据名称获取作业
     *
     * @param name 作业名称
     * @return 作业DTO
     */
    JobDTO getJobByName(String name);
    
    /**
     * 获取所有作业
     *
     * @return 作业DTO列表
     */
    List<JobDTO> getAllJobs();
    
    /**
     * 根据状态获取作业
     *
     * @param status 作业状态
     * @return 作业DTO列表
     */
    List<JobDTO> getJobsByStatus(String status);
    
    /**
     * 根据类型获取作业
     *
     * @param type 作业类型
     * @return 作业DTO列表
     */
    List<JobDTO> getJobsByType(String type);
    
    /**
     * 根据状态和类型获取作业
     *
     * @param status 作业状态
     * @param type 作业类型
     * @return 作业DTO列表
     */
    List<JobDTO> getJobsByStatusAndType(String status, String type);
    
    /**
     * 获取依赖于指定作业的所有作业
     *
     * @param jobId 作业ID
     * @return 作业DTO列表
     */
    List<JobDTO> getDependentJobs(String jobId);
    
    /**
     * 添加作业依赖
     *
     * @param jobId 作业ID
     * @param dependencyJobId 依赖作业ID
     * @param type 依赖类型
     * @param condition 依赖条件
     * @param required 是否强制依赖
     * @param modifier 修改者
     * @return 更新后的作业DTO
     */
    JobDTO addJobDependency(String jobId, String dependencyJobId, String type, String condition, boolean required, String modifier);
    
    /**
     * 移除作业依赖
     *
     * @param jobId 作业ID
     * @param dependencyJobId 依赖作业ID
     * @param modifier 修改者
     * @return 更新后的作业DTO
     */
    JobDTO removeJobDependency(String jobId, String dependencyJobId, String modifier);
    
    /**
     * 获取作业统计信息
     *
     * @return 作业统计信息
     */
    JobStatistics getJobStatistics();
    
    /**
     * 获取作业执行计划
     *
     * @param jobId 作业ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 执行计划列表
     */
    List<ExecutionPlan> getJobExecutionPlan(String jobId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 作业统计信息
     */
    class JobStatistics {
        private int totalJobs;
        private int enabledJobs;
        private int disabledJobs;
        private int archivedJobs;
        private Map<String, Integer> jobCountByType;
        private int jobsWithDependencies;
        
        // Getters and setters
        
        public int getTotalJobs() {
            return totalJobs;
        }
        
        public void setTotalJobs(int totalJobs) {
            this.totalJobs = totalJobs;
        }
        
        public int getEnabledJobs() {
            return enabledJobs;
        }
        
        public void setEnabledJobs(int enabledJobs) {
            this.enabledJobs = enabledJobs;
        }
        
        public int getDisabledJobs() {
            return disabledJobs;
        }
        
        public void setDisabledJobs(int disabledJobs) {
            this.disabledJobs = disabledJobs;
        }
        
        public int getArchivedJobs() {
            return archivedJobs;
        }
        
        public void setArchivedJobs(int archivedJobs) {
            this.archivedJobs = archivedJobs;
        }
        
        public Map<String, Integer> getJobCountByType() {
            return jobCountByType;
        }
        
        public void setJobCountByType(Map<String, Integer> jobCountByType) {
            this.jobCountByType = jobCountByType;
        }
        
        public int getJobsWithDependencies() {
            return jobsWithDependencies;
        }
        
        public void setJobsWithDependencies(int jobsWithDependencies) {
            this.jobsWithDependencies = jobsWithDependencies;
        }
    }
    
    /**
     * 执行计划
     */
    class ExecutionPlan {
        private String jobId;
        private String jobName;
        private LocalDateTime plannedTime;
        private String scheduleType;
        private String scheduleExpression;
        
        // Getters and setters
        
        public String getJobId() {
            return jobId;
        }
        
        public void setJobId(String jobId) {
            this.jobId = jobId;
        }
        
        public String getJobName() {
            return jobName;
        }
        
        public void setJobName(String jobName) {
            this.jobName = jobName;
        }
        
        public LocalDateTime getPlannedTime() {
            return plannedTime;
        }
        
        public void setPlannedTime(LocalDateTime plannedTime) {
            this.plannedTime = plannedTime;
        }
        
        public String getScheduleType() {
            return scheduleType;
        }
        
        public void setScheduleType(String scheduleType) {
            this.scheduleType = scheduleType;
        }
        
        public String getScheduleExpression() {
            return scheduleExpression;
        }
        
        public void setScheduleExpression(String scheduleExpression) {
            this.scheduleExpression = scheduleExpression;
        }
    }
}
