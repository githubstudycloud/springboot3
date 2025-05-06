package com.platform.scheduler.application.service;

import com.platform.scheduler.application.dto.TaskInstanceDTO;
import com.platform.scheduler.application.dto.TaskLogEntryDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 任务应用服务接口
 * 
 * @author platform
 */
public interface TaskApplicationService {
    
    /**
     * 根据ID获取任务实例
     *
     * @param taskInstanceId 任务实例ID
     * @return 任务实例DTO
     */
    TaskInstanceDTO getTaskInstanceById(String taskInstanceId);
    
    /**
     * 获取作业的所有任务实例
     *
     * @param jobId 作业ID
     * @return 任务实例DTO列表
     */
    List<TaskInstanceDTO> getTaskInstancesByJobId(String jobId);
    
    /**
     * 获取作业的任务实例历史
     *
     * @param jobId 作业ID
     * @param limit 限制数量
     * @return 任务实例DTO列表
     */
    List<TaskInstanceDTO> getTaskInstanceHistoryByJobId(String jobId, int limit);
    
    /**
     * 根据状态获取任务实例
     *
     * @param status 任务状态
     * @return 任务实例DTO列表
     */
    List<TaskInstanceDTO> getTaskInstancesByStatus(String status);
    
    /**
     * 获取执行器的任务实例
     *
     * @param executorId 执行器ID
     * @return 任务实例DTO列表
     */
    List<TaskInstanceDTO> getTaskInstancesByExecutorId(String executorId);
    
    /**
     * 获取指定时间段内创建的任务实例
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务实例DTO列表
     */
    List<TaskInstanceDTO> getTaskInstancesByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 暂停任务实例
     *
     * @param taskInstanceId 任务实例ID
     * @param reason 暂停原因
     * @param operator 操作者
     * @return 更新后的任务实例DTO
     */
    TaskInstanceDTO pauseTaskInstance(String taskInstanceId, String reason, String operator);
    
    /**
     * 恢复任务实例
     *
     * @param taskInstanceId 任务实例ID
     * @param operator 操作者
     * @return 更新后的任务实例DTO
     */
    TaskInstanceDTO resumeTaskInstance(String taskInstanceId, String operator);
    
    /**
     * 取消任务实例
     *
     * @param taskInstanceId 任务实例ID
     * @param reason 取消原因
     * @param operator 操作者
     * @return 更新后的任务实例DTO
     */
    TaskInstanceDTO cancelTaskInstance(String taskInstanceId, String reason, String operator);
    
    /**
     * 重试任务实例
     *
     * @param taskInstanceId 任务实例ID
     * @param operator 操作者
     * @return 重试生成的新任务实例ID
     */
    String retryTaskInstance(String taskInstanceId, String operator);
    
    /**
     * 获取任务实例日志
     *
     * @param taskInstanceId 任务实例ID
     * @return 日志条目DTO列表
     */
    List<TaskLogEntryDTO> getTaskInstanceLogs(String taskInstanceId);
    
    /**
     * 添加任务日志
     *
     * @param taskInstanceId 任务实例ID
     * @param level 日志级别
     * @param message 日志消息
     * @param operator 操作者
     * @return 是否成功
     */
    boolean addTaskLog(String taskInstanceId, String level, String message, String operator);
    
    /**
     * 更新任务实例进度
     *
     * @param taskInstanceId 任务实例ID
     * @param progressPercentage 进度百分比
     * @param message 进度消息
     * @param operator 操作者
     * @return 是否成功
     */
    boolean updateTaskProgress(String taskInstanceId, int progressPercentage, String message, String operator);
    
    /**
     * 清理历史任务数据
     *
     * @param retentionDays 保留天数
     * @param operator 操作者
     * @return 清理的任务数量
     */
    int cleanHistoricalTasks(int retentionDays, String operator);
    
    /**
     * 获取任务统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务统计信息
     */
    TaskStatistics getTaskStatistics(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取作业的任务成功率
     *
     * @param jobId 作业ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 成功率（0-100）
     */
    double getJobSuccessRate(String jobId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 任务统计信息
     */
    class TaskStatistics {
        private int totalTasks;
        private int waitingTasks;
        private int runningTasks;
        private int completedTasks;
        private int failedTasks;
        private int canceledTasks;
        private double averageDuration;
        private int maxRetries;
        private Map<String, Integer> taskCountByJobType;
        
        // Getters and setters
        
        public int getTotalTasks() {
            return totalTasks;
        }
        
        public void setTotalTasks(int totalTasks) {
            this.totalTasks = totalTasks;
        }
        
        public int getWaitingTasks() {
            return waitingTasks;
        }
        
        public void setWaitingTasks(int waitingTasks) {
            this.waitingTasks = waitingTasks;
        }
        
        public int getRunningTasks() {
            return runningTasks;
        }
        
        public void setRunningTasks(int runningTasks) {
            this.runningTasks = runningTasks;
        }
        
        public int getCompletedTasks() {
            return completedTasks;
        }
        
        public void setCompletedTasks(int completedTasks) {
            this.completedTasks = completedTasks;
        }
        
        public int getFailedTasks() {
            return failedTasks;
        }
        
        public void setFailedTasks(int failedTasks) {
            this.failedTasks = failedTasks;
        }
        
        public int getCanceledTasks() {
            return canceledTasks;
        }
        
        public void setCanceledTasks(int canceledTasks) {
            this.canceledTasks = canceledTasks;
        }
        
        public double getAverageDuration() {
            return averageDuration;
        }
        
        public void setAverageDuration(double averageDuration) {
            this.averageDuration = averageDuration;
        }
        
        public int getMaxRetries() {
            return maxRetries;
        }
        
        public void setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
        }
        
        public Map<String, Integer> getTaskCountByJobType() {
            return taskCountByJobType;
        }
        
        public void setTaskCountByJobType(Map<String, Integer> taskCountByJobType) {
            this.taskCountByJobType = taskCountByJobType;
        }
    }
}
