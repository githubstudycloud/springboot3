package com.platform.scheduler.model;

import java.util.Date;

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
    private String id;
    
    /**
     * 主机名
     */
    private String host;
    
    /**
     * IP地址
     */
    private String ip;
    
    /**
     * 端口
     */
    private Integer port;
    
    /**
     * 节点状态(ONLINE/OFFLINE)
     */
    private String status;
    
    /**
     * 最后心跳时间
     */
    private Date lastHeartbeat;
    
    /**
     * 创建时间
     */
    private Date createdTime;
    
    /**
     * 更新时间
     */
    private Date updatedTime;
    
    /**
     * 判断节点是否在线
     */
    public boolean isOnline() {
        return "ONLINE".equals(status);
    }
    
    /**
     * 判断节点是否离线
     */
    public boolean isOffline() {
        return "OFFLINE".equals(status);
    }
    
    /**
     * 判断心跳是否超时
     * 
     * @param timeoutSeconds 超时秒数
     * @return 是否超时
     */
    public boolean isHeartbeatTimeout(int timeoutSeconds) {
        if (lastHeartbeat == null) {
            return true;
        }
        long timeout = timeoutSeconds * 1000L;
        return System.currentTimeMillis() - lastHeartbeat.getTime() > timeout;
    }
}
