package com.platform.scheduler.infrastructure.cluster;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 集群节点信息
 * 表示调度系统集群中的一个节点
 *
 * @author platform
 */
@Data
@EqualsAndHashCode(of = "nodeId")
public class ClusterNode implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 节点ID
     */
    private String nodeId;
    
    /**
     * 节点主机地址
     */
    private String host;
    
    /**
     * 节点端口号
     */
    private int port;
    
    /**
     * 节点状态
     */
    private NodeStatus status;
    
    /**
     * 节点角色
     */
    private NodeRole role;
    
    /**
     * 节点启动时间
     */
    private LocalDateTime startTime;
    
    /**
     * 最后心跳时间
     */
    private LocalDateTime lastHeartbeatTime;
    
    /**
     * CPU核心数
     */
    private int cpuCores;
    
    /**
     * CPU使用率
     */
    private double cpuUsage;
    
    /**
     * 总内存(字节)
     */
    private long totalMemory;
    
    /**
     * 空闲内存(字节)
     */
    private long freeMemory;
    
    /**
     * 最大并发任务数
     */
    private int maxConcurrency;
    
    /**
     * 当前任务数
     */
    private int currentTaskCount;
    
    /**
     * 运行中的任务IDs
     */
    private Map<String, LocalDateTime> runningTasks = new HashMap<>();
    
    /**
     * 节点版本号
     */
    private String version;
    
    /**
     * 节点属性
     */
    private Map<String, String> attributes = new HashMap<>();
    
    /**
     * 获取节点运行时间(秒)
     *
     * @return 节点运行时间
     */
    public long getUptimeSeconds() {
        if (startTime == null) {
            return 0;
        }
        return java.time.Duration.between(startTime, LocalDateTime.now()).getSeconds();
    }
    
    /**
     * 获取内存使用率
     *
     * @return 内存使用率(0-100)
     */
    public double getMemoryUsage() {
        if (totalMemory <= 0) {
            return 0;
        }
        return 100.0 * (totalMemory - freeMemory) / totalMemory;
    }
    
    /**
     * 获取负载百分比
     *
     * @return 负载百分比(0-100)
     */
    public double getLoadPercentage() {
        if (maxConcurrency <= 0) {
            return 0;
        }
        return 100.0 * currentTaskCount / maxConcurrency;
    }
    
    /**
     * 判断节点是否可用
     *
     * @return 如果节点状态为在线且角色不为LEAVING，则返回true
     */
    public boolean isAvailable() {
        return status == NodeStatus.ONLINE && role != NodeRole.LEAVING;
    }
    
    /**
     * 节点状态枚举
     */
    public enum NodeStatus {
        /**
         * 在线状态
         */
        ONLINE,
        
        /**
         * 离线状态
         */
        OFFLINE,
        
        /**
         * 维护状态
         */
        MAINTENANCE
    }
    
    /**
     * 节点角色枚举
     */
    public enum NodeRole {
        /**
         * 主节点
         */
        MASTER,
        
        /**
         * 工作节点
         */
        WORKER,
        
        /**
         * 候选主节点
         */
        CANDIDATE,
        
        /**
         * 正在离开的节点
         */
        LEAVING
    }
}
