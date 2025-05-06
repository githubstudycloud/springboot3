package com.platform.scheduler.domain.service;

import com.platform.scheduler.domain.model.job.Job;
import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.job.ScheduleStrategy;

/**
 * 作业调度服务接口
 * 定义作业调度相关的领域服务
 * 
 * @author platform
 */
public interface JobScheduleService {
    
    /**
     * 调度作业
     * 根据作业的调度策略创建调度计划
     *
     * @param job 作业实体
     * @return 是否成功调度
     */
    boolean scheduleJob(Job job);
    
    /**
     * 取消作业调度
     *
     * @param jobId 作业ID
     * @return 是否成功取消调度
     */
    boolean unscheduleJob(JobId jobId);
    
    /**
     * 暂停作业调度
     *
     * @param jobId 作业ID
     * @return 是否成功暂停调度
     */
    boolean pauseJobSchedule(JobId jobId);
    
    /**
     * 恢复作业调度
     *
     * @param jobId 作业ID
     * @return 是否成功恢复调度
     */
    boolean resumeJobSchedule(JobId jobId);
    
    /**
     * 触发作业立即执行一次
     *
     * @param jobId 作业ID
     * @param parameters 执行参数，可为null表示使用默认参数
     * @return 是否成功触发
     */
    boolean triggerJobExecution(JobId jobId, java.util.Map<String, String> parameters);
    
    /**
     * 更新作业调度策略
     *
     * @param jobId 作业ID
     * @param strategy 新的调度策略
     * @return 是否成功更新
     */
    boolean updateScheduleStrategy(JobId jobId, ScheduleStrategy strategy);
    
    /**
     * 检查作业是否可调度
     * 考虑作业状态、依赖关系等因素
     *
     * @param job 作业实体
     * @return 如果作业可以被调度，则返回true
     */
    boolean isSchedulable(Job job);
    
    /**
     * 获取下次执行时间
     *
     * @param job 作业实体
     * @return 下次执行时间，如果无法确定则返回null
     */
    java.time.LocalDateTime getNextExecutionTime(Job job);
    
    /**
     * 获取最近的调度历史
     *
     * @param jobId 作业ID
     * @param limit 限制返回的记录数
     * @return 调度历史记录列表
     */
    java.util.List<ScheduleHistory> getScheduleHistory(JobId jobId, int limit);
    
    /**
     * 调度历史记录
     */
    class ScheduleHistory {
        private final java.time.LocalDateTime scheduledTime;
        private final java.time.LocalDateTime actualFireTime;
        private final boolean successful;
        private final String result;
        
        public ScheduleHistory(java.time.LocalDateTime scheduledTime, 
                              java.time.LocalDateTime actualFireTime, 
                              boolean successful, 
                              String result) {
            this.scheduledTime = scheduledTime;
            this.actualFireTime = actualFireTime;
            this.successful = successful;
            this.result = result;
        }
        
        public java.time.LocalDateTime getScheduledTime() {
            return scheduledTime;
        }
        
        public java.time.LocalDateTime getActualFireTime() {
            return actualFireTime;
        }
        
        public boolean isSuccessful() {
            return successful;
        }
        
        public String getResult() {
            return result;
        }
    }
}
