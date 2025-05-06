package com.platform.scheduler.application.service;

import java.util.List;
import java.util.Map;

/**
 * 集群应用服务接口
 * 
 * @author platform
 */
public interface ClusterApplicationService {
    
    /**
     * 获取集群状态信息
     *
     * @return 集群状态信息
     */
    ClusterStatusDTO getClusterStatus();
    
    /**
     * 获取集群节点信息
     *
     * @return 节点信息列表
     */
    List<NodeInfoDTO> getClusterNodes();
    
    /**
     * 获取主节点信息
     *
     * @return 主节点信息
     */
    NodeInfoDTO getMasterNode();
    
    /**
     * 获取分布式锁持有信息
     *
     * @return 锁持有信息列表
     */
    List<LockInfoDTO> getLockInfo();
    
    /**
     * 手动触发集群节点选举
     *
     * @param operator 操作者
     * @return 是否成功
     */
    boolean triggerLeaderElection(String operator);
    
    /**
     * 设置节点维护模式
     *
     * @param nodeId 节点ID
     * @param maintenanceMode 是否进入维护模式
     * @param reason 原因
     * @param operator 操作者
     * @return 是否成功
     */
    boolean setNodeMaintenanceMode(String nodeId, boolean maintenanceMode, String reason, String operator);
    
    /**
     * 获取集群事件日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 集群事件列表
     */
    List<ClusterEventDTO> getClusterEvents(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime, int limit);
    
    /**
     * 获取集群任务分布统计
     *
     * @return 任务分布统计
     */
    Map<String, TaskDistributionDTO> getTaskDistribution();
    
    /**
     * 获取集群资源使用统计
     *
     * @return 资源使用统计
     */
    ClusterResourceUsageDTO getClusterResourceUsage();
    
    /**
     * 集群状态DTO
     */
    class ClusterStatusDTO {
        private String clusterId;
        private String clusterName;
        private String clusterMode;
        private int totalNodes;
        private int activeNodes;
        private int maintenanceNodes;
        private int offlineNodes;
        private String masterNodeId;
        private boolean healthy;
        private int totalTaskSlots;
        private int usedTaskSlots;
        private long uptime;
        private java.time.LocalDateTime startTime;
        private String coordinationMode;
        private int runningTasks;
        private int waitingTasks;
        
        // Getters and setters
        
        public String getClusterId() {
            return clusterId;
        }
        
        public void setClusterId(String clusterId) {
            this.clusterId = clusterId;
        }
        
        public String getClusterName() {
            return clusterName;
        }
        
        public void setClusterName(String clusterName) {
            this.clusterName = clusterName;
        }
        
        public String getClusterMode() {
            return clusterMode;
        }
        
        public void setClusterMode(String clusterMode) {
            this.clusterMode = clusterMode;
        }
        
        public int getTotalNodes() {
            return totalNodes;
        }
        
        public void setTotalNodes(int totalNodes) {
            this.totalNodes = totalNodes;
        }
        
        public int getActiveNodes() {
            return activeNodes;
        }
        
        public void setActiveNodes(int activeNodes) {
            this.activeNodes = activeNodes;
        }
        
        public int getMaintenanceNodes() {
            return maintenanceNodes;
        }
        
        public void setMaintenanceNodes(int maintenanceNodes) {
            this.maintenanceNodes = maintenanceNodes;
        }
        
        public int getOfflineNodes() {
            return offlineNodes;
        }
        
        public void setOfflineNodes(int offlineNodes) {
            this.offlineNodes = offlineNodes;
        }
        
        public String getMasterNodeId() {
            return masterNodeId;
        }
        
        public void setMasterNodeId(String masterNodeId) {
            this.masterNodeId = masterNodeId;
        }
        
        public boolean isHealthy() {
            return healthy;
        }
        
        public void setHealthy(boolean healthy) {
            this.healthy = healthy;
        }
        
        public int getTotalTaskSlots() {
            return totalTaskSlots;
        }
        
        public void setTotalTaskSlots(int totalTaskSlots) {
            this.totalTaskSlots = totalTaskSlots;
        }
        
        public int getUsedTaskSlots() {
            return usedTaskSlots;
        }
        
        public void setUsedTaskSlots(int usedTaskSlots) {
            this.usedTaskSlots = usedTaskSlots;
        }
        
        public long getUptime() {
            return uptime;
        }
        
        public void setUptime(long uptime) {
            this.uptime = uptime;
        }
        
        public java.time.LocalDateTime getStartTime() {
            return startTime;
        }
        
        public void setStartTime(java.time.LocalDateTime startTime) {
            this.startTime = startTime;
        }
        
        public String getCoordinationMode() {
            return coordinationMode;
        }
        
        public void setCoordinationMode(String coordinationMode) {
            this.coordinationMode = coordinationMode;
        }
        
        public int getRunningTasks() {
            return runningTasks;
        }
        
        public void setRunningTasks(int runningTasks) {
            this.runningTasks = runningTasks;
        }
        
        public int getWaitingTasks() {
            return waitingTasks;
        }
        
        public void setWaitingTasks(int waitingTasks) {
            this.waitingTasks = waitingTasks;
        }
    }
    
    /**
     * 节点信息DTO
     */
    class NodeInfoDTO {
        private String nodeId;
        private String host;
        private int port;
        private String status;
        private String role;
        private long startTime;
        private long uptime;
        private int currentLoad;
        private int maxConcurrency;
        private String version;
        private Map<String, Object> attributes;
        private boolean maintenanceMode;
        
        // Getters and setters
        
        public String getNodeId() {
            return nodeId;
        }
        
        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }
        
        public String getHost() {
            return host;
        }
        
        public void setHost(String host) {
            this.host = host;
        }
        
        public int getPort() {
            return port;
        }
        
        public void setPort(int port) {
            this.port = port;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }
        
        public long getUptime() {
            return uptime;
        }
        
        public void setUptime(long uptime) {
            this.uptime = uptime;
        }
        
        public int getCurrentLoad() {
            return currentLoad;
        }
        
        public void setCurrentLoad(int currentLoad) {
            this.currentLoad = currentLoad;
        }
        
        public int getMaxConcurrency() {
            return maxConcurrency;
        }
        
        public void setMaxConcurrency(int maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
        }
        
        public String getVersion() {
            return version;
        }
        
        public void setVersion(String version) {
            this.version = version;
        }
        
        public Map<String, Object> getAttributes() {
            return attributes;
        }
        
        public void setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
        }
        
        public boolean isMaintenanceMode() {
            return maintenanceMode;
        }
        
        public void setMaintenanceMode(boolean maintenanceMode) {
            this.maintenanceMode = maintenanceMode;
        }
    }
    
    /**
     * 锁信息DTO
     */
    class LockInfoDTO {
        private String lockKey;
        private String lockHolder;
        private java.time.LocalDateTime acquireTime;
        private java.time.LocalDateTime expireTime;
        private long timeToLiveSeconds;
        private boolean expired;
        
        // Getters and setters
        
        public String getLockKey() {
            return lockKey;
        }
        
        public void setLockKey(String lockKey) {
            this.lockKey = lockKey;
        }
        
        public String getLockHolder() {
            return lockHolder;
        }
        
        public void setLockHolder(String lockHolder) {
            this.lockHolder = lockHolder;
        }
        
        public java.time.LocalDateTime getAcquireTime() {
            return acquireTime;
        }
        
        public void setAcquireTime(java.time.LocalDateTime acquireTime) {
            this.acquireTime = acquireTime;
        }
        
        public java.time.LocalDateTime getExpireTime() {
            return expireTime;
        }
        
        public void setExpireTime(java.time.LocalDateTime expireTime) {
            this.expireTime = expireTime;
        }
        
        public long getTimeToLiveSeconds() {
            return timeToLiveSeconds;
        }
        
        public void setTimeToLiveSeconds(long timeToLiveSeconds) {
            this.timeToLiveSeconds = timeToLiveSeconds;
        }
        
        public boolean isExpired() {
            return expired;
        }
        
        public void setExpired(boolean expired) {
            this.expired = expired;
        }
    }
    
    /**
     * 集群事件DTO
     */
    class ClusterEventDTO {
        private String eventId;
        private String eventType;
        private java.time.LocalDateTime eventTime;
        private String nodeId;
        private String description;
        private Map<String, Object> attributes;
        
        // Getters and setters
        
        public String getEventId() {
            return eventId;
        }
        
        public void setEventId(String eventId) {
            this.eventId = eventId;
        }
        
        public String getEventType() {
            return eventType;
        }
        
        public void setEventType(String eventType) {
            this.eventType = eventType;
        }
        
        public java.time.LocalDateTime getEventTime() {
            return eventTime;
        }
        
        public void setEventTime(java.time.LocalDateTime eventTime) {
            this.eventTime = eventTime;
        }
        
        public String getNodeId() {
            return nodeId;
        }
        
        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Map<String, Object> getAttributes() {
            return attributes;
        }
        
        public void setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
        }
    }
    
    /**
     * 任务分布DTO
     */
    class TaskDistributionDTO {
        private String nodeId;
        private String nodeName;
        private int runningTasks;
        private int waitingTasks;
        private Map<String, Integer> taskCountByJobType;
        private double loadPercentage;
        
        // Getters and setters
        
        public String getNodeId() {
            return nodeId;
        }
        
        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }
        
        public String getNodeName() {
            return nodeName;
        }
        
        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }
        
        public int getRunningTasks() {
            return runningTasks;
        }
        
        public void setRunningTasks(int runningTasks) {
            this.runningTasks = runningTasks;
        }
        
        public int getWaitingTasks() {
            return waitingTasks;
        }
        
        public void setWaitingTasks(int waitingTasks) {
            this.waitingTasks = waitingTasks;
        }
        
        public Map<String, Integer> getTaskCountByJobType() {
            return taskCountByJobType;
        }
        
        public void setTaskCountByJobType(Map<String, Integer> taskCountByJobType) {
            this.taskCountByJobType = taskCountByJobType;
        }
        
        public double getLoadPercentage() {
            return loadPercentage;
        }
        
        public void setLoadPercentage(double loadPercentage) {
            this.loadPercentage = loadPercentage;
        }
    }
    
    /**
     * 集群资源使用DTO
     */
    class ClusterResourceUsageDTO {
        private int totalCpuCores;
        private double averageCpuUsage;
        private long totalMemory;
        private long usedMemory;
        private double memoryUsagePercentage;
        private int totalExecutors;
        private int totalTaskSlots;
        private int usedTaskSlots;
        private double taskSlotsUsagePercentage;
        private Map<String, Integer> resourceDistribution;
        
        // Getters and setters
        
        public int getTotalCpuCores() {
            return totalCpuCores;
        }
        
        public void setTotalCpuCores(int totalCpuCores) {
            this.totalCpuCores = totalCpuCores;
        }
        
        public double getAverageCpuUsage() {
            return averageCpuUsage;
        }
        
        public void setAverageCpuUsage(double averageCpuUsage) {
            this.averageCpuUsage = averageCpuUsage;
        }
        
        public long getTotalMemory() {
            return totalMemory;
        }
        
        public void setTotalMemory(long totalMemory) {
            this.totalMemory = totalMemory;
        }
        
        public long getUsedMemory() {
            return usedMemory;
        }
        
        public void setUsedMemory(long usedMemory) {
            this.usedMemory = usedMemory;
        }
        
        public double getMemoryUsagePercentage() {
            return memoryUsagePercentage;
        }
        
        public void setMemoryUsagePercentage(double memoryUsagePercentage) {
            this.memoryUsagePercentage = memoryUsagePercentage;
        }
        
        public int getTotalExecutors() {
            return totalExecutors;
        }
        
        public void setTotalExecutors(int totalExecutors) {
            this.totalExecutors = totalExecutors;
        }
        
        public int getTotalTaskSlots() {
            return totalTaskSlots;
        }
        
        public void setTotalTaskSlots(int totalTaskSlots) {
            this.totalTaskSlots = totalTaskSlots;
        }
        
        public int getUsedTaskSlots() {
            return usedTaskSlots;
        }
        
        public void setUsedTaskSlots(int usedTaskSlots) {
            this.usedTaskSlots = usedTaskSlots;
        }
        
        public double getTaskSlotsUsagePercentage() {
            return taskSlotsUsagePercentage;
        }
        
        public void setTaskSlotsUsagePercentage(double taskSlotsUsagePercentage) {
            this.taskSlotsUsagePercentage = taskSlotsUsagePercentage;
        }
        
        public Map<String, Integer> getResourceDistribution() {
            return resourceDistribution;
        }
        
        public void setResourceDistribution(Map<String, Integer> resourceDistribution) {
            this.resourceDistribution = resourceDistribution;
        }
    }
}
