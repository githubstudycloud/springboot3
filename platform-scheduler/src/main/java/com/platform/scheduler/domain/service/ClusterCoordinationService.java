package com.platform.scheduler.domain.service;

import com.platform.scheduler.domain.model.executor.Executor;
import com.platform.scheduler.domain.model.executor.ExecutorId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 集群协调服务接口
 * 定义调度系统集群协调相关的领域服务
 * 
 * @author platform
 */
public interface ClusterCoordinationService {
    
    /**
     * 注册执行器
     *
     * @param executor 执行器实体
     * @return 是否成功注册
     */
    boolean registerExecutor(Executor executor);
    
    /**
     * 注销执行器
     *
     * @param executorId 执行器ID
     * @return 是否成功注销
     */
    boolean deregisterExecutor(ExecutorId executorId);
    
    /**
     * 更新执行器心跳
     *
     * @param executorId 执行器ID
     * @param resourceInfo 资源信息
     * @return 是否成功更新
     */
    boolean updateExecutorHeartbeat(ExecutorId executorId, Map<String, Object> resourceInfo);
    
    /**
     * 获取所有在线执行器
     *
     * @return 在线执行器列表
     */
    List<Executor> getAllOnlineExecutors();
    
    /**
     * 获取所有执行器状态
     *
     * @return 执行器ID和状态的映射
     */
    Map<ExecutorId, String> getAllExecutorStatus();
    
    /**
     * 选择适合指定作业的执行器
     *
     * @param jobType 作业类型
     * @param requiredTags 所需标签
     * @return 选择的执行器
     */
    Optional<Executor> selectExecutorForJob(String jobType, List<String> requiredTags);
    
    /**
     * 获取集群负载信息
     *
     * @return 负载信息
     */
    ClusterLoadInfo getClusterLoadInfo();
    
    /**
     * 获取分布式锁
     *
     * @param lockKey 锁键
     * @param timeoutSeconds 超时时间（秒）
     * @return 如果成功获取锁，则返回锁标识；否则返回null
     */
    String acquireLock(String lockKey, int timeoutSeconds);
    
    /**
     * 释放分布式锁
     *
     * @param lockKey 锁键
     * @param lockIdentifier 锁标识
     * @return 是否成功释放
     */
    boolean releaseLock(String lockKey, String lockIdentifier);
    
    /**
     * 维护集群健康
     * 检测离线节点，重分配任务等
     *
     * @return 处理的异常节点数
     */
    int maintainClusterHealth();
    
    /**
     * 检查是否为主节点
     *
     * @return 如果当前节点是主节点，则返回true
     */
    boolean isMasterNode();
    
    /**
     * 获取主节点信息
     *
     * @return 主节点信息
     */
    Optional<NodeInfo> getMasterNodeInfo();
    
    /**
     * 集群负载信息
     */
    class ClusterLoadInfo {
        private final int totalNodes;
        private final int activeNodes;
        private final int totalSlots;
        private final int usedSlots;
        private final double averageLoad;
        private final int waitingTasks;
        private final int runningTasks;
        
        public ClusterLoadInfo(int totalNodes, int activeNodes, int totalSlots, int usedSlots, 
                              double averageLoad, int waitingTasks, int runningTasks) {
            this.totalNodes = totalNodes;
            this.activeNodes = activeNodes;
            this.totalSlots = totalSlots;
            this.usedSlots = usedSlots;
            this.averageLoad = averageLoad;
            this.waitingTasks = waitingTasks;
            this.runningTasks = runningTasks;
        }
        
        public int getTotalNodes() {
            return totalNodes;
        }
        
        public int getActiveNodes() {
            return activeNodes;
        }
        
        public int getTotalSlots() {
            return totalSlots;
        }
        
        public int getUsedSlots() {
            return usedSlots;
        }
        
        public double getAverageLoad() {
            return averageLoad;
        }
        
        public int getWaitingTasks() {
            return waitingTasks;
        }
        
        public int getRunningTasks() {
            return runningTasks;
        }
        
        public double getUtilizationPercentage() {
            return totalSlots > 0 ? ((double) usedSlots / totalSlots) * 100 : 0;
        }
    }
    
    /**
     * 节点信息
     */
    class NodeInfo {
        private final String nodeId;
        private final String host;
        private final int port;
        private final String role; // 节点角色：master, worker
        private final long startTime;
        private final Map<String, Object> attributes;
        
        public NodeInfo(String nodeId, String host, int port, String role, 
                       long startTime, Map<String, Object> attributes) {
            this.nodeId = nodeId;
            this.host = host;
            this.port = port;
            this.role = role;
            this.startTime = startTime;
            this.attributes = attributes;
        }
        
        public String getNodeId() {
            return nodeId;
        }
        
        public String getHost() {
            return host;
        }
        
        public int getPort() {
            return port;
        }
        
        public String getRole() {
            return role;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public Map<String, Object> getAttributes() {
            return attributes;
        }
    }
}
