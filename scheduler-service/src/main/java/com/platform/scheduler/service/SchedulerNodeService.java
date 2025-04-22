package com.platform.scheduler.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.platform.scheduler.model.SchedulerNode;

/**
 * 调度节点服务接口
 * 
 * @author platform
 */
public interface SchedulerNodeService {
    
    /**
     * 注册节点
     * 
     * @param node 节点信息
     * @return 注册后的节点
     */
    SchedulerNode registerNode(SchedulerNode node);
    
    /**
     * 更新节点心跳
     * 
     * @param nodeId 节点ID
     * @return 是否更新成功
     */
    boolean updateHeartbeat(String nodeId);
    
    /**
     * 节点下线
     * 
     * @param nodeId 节点ID
     * @return 是否下线成功
     */
    boolean offlineNode(String nodeId);
    
    /**
     * 根据节点ID查询节点
     * 
     * @param nodeId 节点ID
     * @return 节点信息
     */
    SchedulerNode getNodeById(String nodeId);
    
    /**
     * 查询所有在线节点
     * 
     * @return 节点列表
     */
    List<SchedulerNode> findOnlineNodes();
    
    /**
     * 查询所有节点
     * 
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<SchedulerNode> findAllNodes(Pageable pageable);
    
    /**
     * 查询状态节点
     * 
     * @param status 节点状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<SchedulerNode> findNodesByStatus(String status, Pageable pageable);
    
    /**
     * 检查并处理心跳超时的节点
     * 
     * @param timeoutSeconds 超时秒数
     * @return 处理的节点数
     */
    int handleTimeoutNodes(int timeoutSeconds);
    
    /**
     * 获取当前节点
     * 
     * @return 当前节点信息
     */
    SchedulerNode getCurrentNode();
    
    /**
     * 选择可用节点执行任务
     * 
     * @param taskId 任务ID
     * @return 选中的节点
     */
    SchedulerNode selectNodeForTask(Long taskId);
    
    /**
     * 重新分配节点上的任务
     * 
     * @param nodeId 节点ID
     * @return 重分配的任务数
     */
    int reassignTasks(String nodeId);
}
