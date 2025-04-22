package com.platform.scheduler.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.platform.scheduler.model.SchedulerNode;
import com.platform.scheduler.repository.base.BaseRepository;

/**
 * 调度节点数据访问层接口
 * 
 * @author platform
 */
public interface SchedulerNodeRepository extends BaseRepository<SchedulerNode, String> {
    
    /**
     * 根据状态查询节点
     * 
     * @param status 节点状态
     * @return 节点列表
     */
    List<SchedulerNode> findByStatus(String status);
    
    /**
     * 根据状态分页查询节点
     * 
     * @param status 节点状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<SchedulerNode> findByStatus(String status, Pageable pageable);
    
    /**
     * 更新节点状态
     * 
     * @param nodeId 节点ID
     * @param status 节点状态
     * @return 更新行数
     */
    int updateStatus(String nodeId, String status);
    
    /**
     * 更新节点心跳时间
     * 
     * @param nodeId 节点ID
     * @param heartbeatTime 心跳时间
     * @return 更新行数
     */
    int updateHeartbeat(String nodeId, Date heartbeatTime);
    
    /**
     * 查询超时的节点
     * 
     * @param timeout 超时时间（毫秒）
     * @return 节点列表
     */
    List<SchedulerNode> findTimeout(long timeout);
    
    /**
     * 查询所有在线节点
     * 
     * @return 节点列表
     */
    List<SchedulerNode> findOnline();
    
    /**
     * 注册节点
     * 
     * @param node 节点信息
     * @return 节点信息
     */
    SchedulerNode register(SchedulerNode node);
    
    /**
     * 注销节点
     * 
     * @param nodeId 节点ID
     * @return 是否成功
     */
    boolean unregister(String nodeId);
}
