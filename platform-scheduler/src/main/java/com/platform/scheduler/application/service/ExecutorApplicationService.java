package com.platform.scheduler.application.service;

import com.platform.scheduler.application.command.RegisterExecutorCommand;
import com.platform.scheduler.application.dto.ExecutorDTO;
import com.platform.scheduler.application.dto.TaskInstanceDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 执行器应用服务接口
 * 
 * @author platform
 */
public interface ExecutorApplicationService {
    
    /**
     * 注册执行器
     *
     * @param command 注册执行器命令
     * @return 注册的执行器DTO
     */
    ExecutorDTO registerExecutor(RegisterExecutorCommand command);
    
    /**
     * 注销执行器
     *
     * @param executorId 执行器ID
     * @param operator 操作者
     * @return 是否成功
     */
    boolean deregisterExecutor(String executorId, String operator);
    
    /**
     * 更新执行器心跳
     *
     * @param executorId 执行器ID
     * @param resourceInfo 资源信息
     * @return 是否成功
     */
    boolean updateExecutorHeartbeat(String executorId, Map<String, Object> resourceInfo);
    
    /**
     * 更新执行器状态
     *
     * @param executorId 执行器ID
     * @param status 新状态
     * @param reason 状态变更原因
     * @param operator 操作者
     * @return 更新后的执行器DTO
     */
    ExecutorDTO updateExecutorStatus(String executorId, String status, String reason, String operator);
    
    /**
     * 根据ID获取执行器
     *
     * @param executorId 执行器ID
     * @return 执行器DTO
     */
    ExecutorDTO getExecutorById(String executorId);
    
    /**
     * 根据名称获取执行器
     *
     * @param name 执行器名称
     * @return 执行器DTO
     */
    ExecutorDTO getExecutorByName(String name);
    
    /**
     * 获取所有执行器
     *
     * @return 执行器DTO列表
     */
    List<ExecutorDTO> getAllExecutors();
    
    /**
     * 根据状态获取执行器
     *
     * @param status 执行器状态
     * @return 执行器DTO列表
     */
    List<ExecutorDTO> getExecutorsByStatus(String status);
    
    /**
     * 根据类型获取执行器
     *
     * @param type 执行器类型
     * @return 执行器DTO列表
     */
    List<ExecutorDTO> getExecutorsByType(String type);
    
    /**
     * 根据标签获取执行器
     *
     * @param tags 标签集合
     * @return 执行器DTO列表
     */
    List<ExecutorDTO> getExecutorsByTags(Set<String> tags);
    
    /**
     * 获取所有可用执行器
     *
     * @return 可用执行器DTO列表
     */
    List<ExecutorDTO> getAvailableExecutors();
    
    /**
     * 更新执行器配置
     *
     * @param executorId 执行器ID
     * @param maxConcurrency 最大并发数
     * @param operator 操作者
     * @return 更新后的执行器DTO
     */
    ExecutorDTO updateExecutorConfiguration(String executorId, Integer maxConcurrency, String operator);
    
    /**
     * 添加执行器标签
     *
     * @param executorId 执行器ID
     * @param tag 标签
     * @param operator 操作者
     * @return 更新后的执行器DTO
     */
    ExecutorDTO addExecutorTag(String executorId, String tag, String operator);
    
    /**
     * 移除执行器标签
     *
     * @param executorId 执行器ID
     * @param tag 标签
     * @param operator 操作者
     * @return 更新后的执行器DTO
     */
    ExecutorDTO removeExecutorTag(String executorId, String tag, String operator);
    
    /**
     * 获取执行器的运行中任务
     *
     * @param executorId 执行器ID
     * @return 任务实例DTO列表
     */
    List<TaskInstanceDTO> getExecutorRunningTasks(String executorId);
    
    /**
     * 获取执行器负载信息
     *
     * @param executorId 执行器ID
     * @return 负载信息DTO
     */
    ExecutorLoadDTO getExecutorLoadInfo(String executorId);
    
    /**
     * 维护执行器健康状态
     * 检测离线节点，更新状态等
     *
     * @param operator 操作者
     * @return 处理的执行器数量
     */
    int maintainExecutorHealth(String operator);
    
    /**
     * 获取集群执行器统计信息
     *
     * @return 执行器统计信息
     */
    ExecutorStatistics getExecutorStatistics();
    
    /**
     * 重分配指定执行器的任务
     *
     * @param executorId 执行器ID
     * @param targetExecutorIds 目标执行器ID列表，为空则自动选择
     * @param operator 操作者
     * @return 重分配的任务数量
     */
    int reassignExecutorTasks(String executorId, List<String> targetExecutorIds, String operator);
    
    /**
     * 执行器负载信息DTO
     */
    class ExecutorLoadDTO {
        private String executorId;
        private String executorName;
        private Integer currentLoad;
        private Integer maxConcurrency;
        private Integer loadPercentage;
        private Long totalMemory;
        private Long freeMemory;
        private Integer memoryUsagePercentage;
        private Double cpuUsage;
        private Integer runningTaskCount;
        private LocalDateTime lastHeartbeatTime;
        private boolean healthy;
        
        // Getters and setters
        
        public String getExecutorId() {
            return executorId;
        }
        
        public void setExecutorId(String executorId) {
            this.executorId = executorId;
        }
        
        public String getExecutorName() {
            return executorName;
        }
        
        public void setExecutorName(String executorName) {
            this.executorName = executorName;
        }
        
        public Integer getCurrentLoad() {
            return currentLoad;
        }
        
        public void setCurrentLoad(Integer currentLoad) {
            this.currentLoad = currentLoad;
        }
        
        public Integer getMaxConcurrency() {
            return maxConcurrency;
        }
        
        public void setMaxConcurrency(Integer maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
        }
        
        public Integer getLoadPercentage() {
            return loadPercentage;
        }
        
        public void setLoadPercentage(Integer loadPercentage) {
            this.loadPercentage = loadPercentage;
        }
        
        public Long getTotalMemory() {
            return totalMemory;
        }
        
        public void setTotalMemory(Long totalMemory) {
            this.totalMemory = totalMemory;
        }
        
        public Long getFreeMemory() {
            return freeMemory;
        }
        
        public void setFreeMemory(Long freeMemory) {
            this.freeMemory = freeMemory;
        }
        
        public Integer getMemoryUsagePercentage() {
            return memoryUsagePercentage;
        }
        
        public void setMemoryUsagePercentage(Integer memoryUsagePercentage) {
            this.memoryUsagePercentage = memoryUsagePercentage;
        }
        
        public Double getCpuUsage() {
            return cpuUsage;
        }
        
        public void setCpuUsage(Double cpuUsage) {
            this.cpuUsage = cpuUsage;
        }
        
        public Integer getRunningTaskCount() {
            return runningTaskCount;
        }
        
        public void setRunningTaskCount(Integer runningTaskCount) {
            this.runningTaskCount = runningTaskCount;
        }
        
        public LocalDateTime getLastHeartbeatTime() {
            return lastHeartbeatTime;
        }
        
        public void setLastHeartbeatTime(LocalDateTime lastHeartbeatTime) {
            this.lastHeartbeatTime = lastHeartbeatTime;
        }
        
        public boolean isHealthy() {
            return healthy;
        }
        
        public void setHealthy(boolean healthy) {
            this.healthy = healthy;
        }
    }
    
    /**
     * 执行器统计信息
     */
    class ExecutorStatistics {
        private int totalExecutors;
        private int onlineExecutors;
        private int offlineExecutors;
        private int busyExecutors;
        private Map<String, Integer> executorCountByType;
        private int totalConcurrencySlots;
        private int usedConcurrencySlots;
        private double averageLoadPercentage;
        private double averageCpuUsage;
        private double averageMemoryUsage;
        
        // Getters and setters
        
        public int getTotalExecutors() {
            return totalExecutors;
        }
        
        public void setTotalExecutors(int totalExecutors) {
            this.totalExecutors = totalExecutors;
        }
        
        public int getOnlineExecutors() {
            return onlineExecutors;
        }
        
        public void setOnlineExecutors(int onlineExecutors) {
            this.onlineExecutors = onlineExecutors;
        }
        
        public int getOfflineExecutors() {
            return offlineExecutors;
        }
        
        public void setOfflineExecutors(int offlineExecutors) {
            this.offlineExecutors = offlineExecutors;
        }
        
        public int getBusyExecutors() {
            return busyExecutors;
        }
        
        public void setBusyExecutors(int busyExecutors) {
            this.busyExecutors = busyExecutors;
        }
        
        public Map<String, Integer> getExecutorCountByType() {
            return executorCountByType;
        }
        
        public void setExecutorCountByType(Map<String, Integer> executorCountByType) {
            this.executorCountByType = executorCountByType;
        }
        
        public int getTotalConcurrencySlots() {
            return totalConcurrencySlots;
        }
        
        public void setTotalConcurrencySlots(int totalConcurrencySlots) {
            this.totalConcurrencySlots = totalConcurrencySlots;
        }
        
        public int getUsedConcurrencySlots() {
            return usedConcurrencySlots;
        }
        
        public void setUsedConcurrencySlots(int usedConcurrencySlots) {
            this.usedConcurrencySlots = usedConcurrencySlots;
        }
        
        public double getAverageLoadPercentage() {
            return averageLoadPercentage;
        }
        
        public void setAverageLoadPercentage(double averageLoadPercentage) {
            this.averageLoadPercentage = averageLoadPercentage;
        }
        
        public double getAverageCpuUsage() {
            return averageCpuUsage;
        }
        
        public void setAverageCpuUsage(double averageCpuUsage) {
            this.averageCpuUsage = averageCpuUsage;
        }
        
        public double getAverageMemoryUsage() {
            return averageMemoryUsage;
        }
        
        public void setAverageMemoryUsage(double averageMemoryUsage) {
            this.averageMemoryUsage = averageMemoryUsage;
        }
    }
}
