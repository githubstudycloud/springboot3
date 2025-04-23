package com.platform.scheduler.model;

import java.util.Date;

import com.platform.scheduler.core.cluster.NodeStatus;

import lombok.Data;

/**
 * 调度节点实体类
 * 
 * @author platform
 */
@Data
public class SchedulerNode {
    
    /**
     * 节点ID
     */
    private String nodeId;
    
    /**
     * 主机名
     */
    private String hostName;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 端口
     */
    private Integer port;
    
    /**
     * 应用名称
     */
    private String applicationName;
    
    /**
     * 节点状态
     */
    private String status;
    
    /**
     * 最后心跳时间
     */
    private Date lastHeartbeatTime;
    
    /**
     * 注册时间
     */
    private Date registeredTime;
    
    /**
     * 创建时间
     */
    private Date createdTime;
    
    /**
     * 更新时间
     */
    private Date updatedTime;
    
    /**
     * 当前负载
     */
    private Integer currentLoad;
    
    /**
     * 最大负载
     */
    private Integer maxLoad;
    
    /**
     * 判断节点是否在线
     */
    public boolean isOnline() {
        return NodeStatus.ONLINE.getCode().equals(status);
    }
    
    /**
     * 判断节点是否离线
     */
    public boolean isOffline() {
        return NodeStatus.OFFLINE.getCode().equals(status);
    }
    
    /**
     * 判断节点是否可分配任务
     */
    public boolean isAssignable() {
        NodeStatus nodeStatus = NodeStatus.fromCode(status);
        return nodeStatus != null && nodeStatus.isAssignable();
    }
    
    /**
     * 判断节点是否活跃
     */
    public boolean isActive() {
        NodeStatus nodeStatus = NodeStatus.fromCode(status);
        return nodeStatus != null && nodeStatus.isActive();
    }
    
    /**
     * 判断心跳是否超时
     * 
     * @param timeoutSeconds 超时秒数
     * @return 是否超时
     */
    public boolean isHeartbeatTimeout(int timeoutSeconds) {
        if (lastHeartbeatTime == null) {
            return true;
        }
        long timeout = timeoutSeconds * 1000L;
        return System.currentTimeMillis() - lastHeartbeatTime.getTime() > timeout;
    }
    
    /**
     * 获取节点地址
     * 
     * @return 节点地址
     */
    public String getAddress() {
        return ipAddress + ":" + port;
    }
    
    /**
     * 获取节点状态描述
     * 
     * @return 状态描述
     */
    public String getStatusDesc() {
        NodeStatus nodeStatus = NodeStatus.fromCode(status);
        return nodeStatus != null ? nodeStatus.getDesc() : "未知状态";
    }
    
    /**
     * 获取负载百分比
     * 
     * @return 负载百分比
     */
    public int getLoadPercentage() {
        if (currentLoad == null || maxLoad == null || maxLoad == 0) {
            return 0;
        }
        return Math.min(100, currentLoad * 100 / maxLoad);
    }
}
