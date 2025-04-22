package com.platform.scheduler.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.scheduler.core.lock.DistributedLockManager;
import com.platform.scheduler.model.SchedulerNode;
import com.platform.scheduler.model.TaskExecution;
import com.platform.scheduler.repository.SchedulerNodeRepository;
import com.platform.scheduler.repository.TaskExecutionRepository;
import com.platform.scheduler.service.SchedulerNodeService;

/**
 * 调度节点服务实现类
 * 
 * @author platform
 */
@Service
public class SchedulerNodeServiceImpl implements SchedulerNodeService {
    
    private static final Logger logger = LoggerFactory.getLogger(SchedulerNodeServiceImpl.class);
    
    @Autowired
    private SchedulerNodeRepository nodeRepository;
    
    @Autowired
    private TaskExecutionRepository taskExecutionRepository;
    
    @Autowired
    private DistributedLockManager lockManager;
    
    @Value("${server.port:8080}")
    private int serverPort;
    
    @Value("${spring.application.name:scheduler-service}")
    private String applicationName;
    
    /**
     * 当前节点ID
     */
    private String currentNodeId;
    
    /**
     * 当前节点信息
     */
    private SchedulerNode currentNode;
    
    @Override
    @Transactional
    public SchedulerNode registerNode(SchedulerNode node) {
        // 生成节点ID
        if (node.getNodeId() == null) {
            node.setNodeId(generateNodeId());
        }
        
        // 设置注册时间和上次心跳时间
        Date now = new Date();
        node.setRegisteredTime(now);
        node.setLastHeartbeatTime(now);
        
        // 设置状态为在线
        node.setStatus("ONLINE");
        
        // 保存节点信息
        SchedulerNode savedNode = nodeRepository.save(node);
        logger.info("节点注册成功: {}", savedNode.getNodeId());
        
        // 如果是当前节点，保存节点ID和信息
        if (isCurrentNode(savedNode)) {
            currentNodeId = savedNode.getNodeId();
            currentNode = savedNode;
        }
        
        return savedNode;
    }
    
    @Override
    @Transactional
    public boolean updateHeartbeat(String nodeId) {
        try {
            // 尝试获取分布式锁
            String lockKey = "node_heartbeat_" + nodeId;
            boolean locked = lockManager.tryLock(lockKey, 5, 10);
            
            if (locked) {
                try {
                    // 更新心跳时间
                    int rows = nodeRepository.updateHeartbeat(nodeId, new Date());
                    boolean result = rows > 0;
                    
                    if (result) {
                        logger.debug("节点心跳更新成功: {}", nodeId);
                    } else {
                        logger.warn("节点心跳更新失败，节点可能不存在: {}", nodeId);
                    }
                    
                    return result;
                } finally {
                    // 释放锁
                    lockManager.unlock(lockKey);
                }
            } else {
                logger.warn("无法获取节点心跳锁: {}", nodeId);
                return false;
            }
        } catch (Exception e) {
            logger.error("更新节点心跳异常: " + nodeId, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean offlineNode(String nodeId) {
        // 更新节点状态为离线
        int rows = nodeRepository.updateStatus(nodeId, "OFFLINE");
        boolean result = rows > 0;
        
        if (result) {
            logger.info("节点已下线: {}", nodeId);
            
            // 如果是当前节点，清除当前节点信息
            if (nodeId.equals(currentNodeId)) {
                currentNodeId = null;
                currentNode = null;
            }
        } else {
            logger.warn("节点下线失败，节点可能不存在: {}", nodeId);
        }
        
        return result;
    }
    
    @Override
    public SchedulerNode getNodeById(String nodeId) {
        Optional<SchedulerNode> nodeOpt = nodeRepository.findById(nodeId);
        return nodeOpt.orElse(null);
    }
    
    @Override
    public List<SchedulerNode> findOnlineNodes() {
        return nodeRepository.findByStatus("ONLINE");
    }
    
    @Override
    public Page<SchedulerNode> findAllNodes(Pageable pageable) {
        return nodeRepository.findAll(pageable);
    }
    
    @Override
    public Page<SchedulerNode> findNodesByStatus(String status, Pageable pageable) {
        return nodeRepository.findByStatus(status, pageable);
    }
    
    @Override
    @Transactional
    public int handleTimeoutNodes(int timeoutSeconds) {
        // 计算超时时间
        Date timeoutTime = new Date(System.currentTimeMillis() - timeoutSeconds * 1000L);
        
        // 查询超时节点
        List<SchedulerNode> timeoutNodes = nodeRepository.findTimeoutNodes(timeoutTime);
        
        int count = 0;
        for (SchedulerNode node : timeoutNodes) {
            // 更新节点状态为超时
            int rows = nodeRepository.updateStatus(node.getNodeId(), "TIMEOUT");
            if (rows > 0) {
                logger.info("节点已标记为超时: {}", node.getNodeId());
                count++;
                
                // 重新分配该节点上的任务
                reassignTasks(node.getNodeId());
            }
        }
        
        return count;
    }
    
    @Override
    public SchedulerNode getCurrentNode() {
        // 如果当前节点信息不存在，则尝试注册
        if (currentNode == null) {
            synchronized (this) {
                if (currentNode == null) {
                    try {
                        // 创建当前节点信息
                        SchedulerNode node = new SchedulerNode();
                        node.setNodeId(generateNodeId());
                        node.setHostName(InetAddress.getLocalHost().getHostName());
                        node.setIpAddress(InetAddress.getLocalHost().getHostAddress());
                        node.setPort(serverPort);
                        node.setApplicationName(applicationName);
                        node.setStatus("ONLINE");
                        
                        // 注册节点
                        currentNode = registerNode(node);
                        currentNodeId = currentNode.getNodeId();
                        
                    } catch (UnknownHostException e) {
                        logger.error("获取主机信息异常", e);
                    }
                }
            }
        }
        
        return currentNode;
    }
    
    @Override
    public SchedulerNode selectNodeForTask(Long taskId) {
        // 默认选择策略：简单轮询
        // 获取所有在线节点
        List<SchedulerNode> onlineNodes = findOnlineNodes();
        if (onlineNodes == null || onlineNodes.isEmpty()) {
            logger.warn("没有可用的在线节点");
            return null;
        }
        
        // 使用任务ID哈希选择节点，实现简单的负载均衡
        int index = Math.abs(taskId.hashCode() % onlineNodes.size());
        SchedulerNode selectedNode = onlineNodes.get(index);
        
        logger.info("为任务选择节点: 任务ID={}, 节点ID={}", taskId, selectedNode.getNodeId());
        return selectedNode;
    }
    
    @Override
    @Transactional
    public int reassignTasks(String nodeId) {
        try {
            // 获取分布式锁
            String lockKey = "reassign_tasks_" + nodeId;
            boolean locked = lockManager.tryLock(lockKey, 30, 10);
            
            if (locked) {
                try {
                    // 查询节点上正在运行的任务
                    List<TaskExecution> runningExecutions = taskExecutionRepository.findByNodeIdAndStatus(nodeId, "RUNNING");
                    
                    // 将这些任务标记为失败
                    int count = 0;
                    for (TaskExecution execution : runningExecutions) {
                        execution.setStatus("FAILED");
                        execution.setEndTime(new Date());
                        execution.setErrorMessage("节点异常下线，任务执行中断");
                        execution.setUpdatedTime(new Date());
                        
                        taskExecutionRepository.save(execution);
                        count++;
                    }
                    
                    logger.info("重新分配节点任务完成: 节点={}, 任务数={}", nodeId, count);
                    return count;
                    
                } finally {
                    // 释放锁
                    lockManager.unlock(lockKey);
                }
            } else {
                logger.warn("无法获取任务重分配锁: {}", nodeId);
                return 0;
            }
        } catch (Exception e) {
            logger.error("重新分配任务异常: " + nodeId, e);
            return 0;
        }
    }
    
    /**
     * 生成节点ID
     * 
     * @return 节点ID
     */
    private String generateNodeId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 判断是否为当前节点
     * 
     * @param node 节点信息
     * @return 是否为当前节点
     */
    private boolean isCurrentNode(SchedulerNode node) {
        try {
            String localHostName = InetAddress.getLocalHost().getHostName();
            String localIpAddress = InetAddress.getLocalHost().getHostAddress();
            
            return localHostName.equals(node.getHostName()) 
                    && localIpAddress.equals(node.getIpAddress())
                    && serverPort == node.getPort();
                    
        } catch (UnknownHostException e) {
            logger.error("获取主机信息异常", e);
            return false;
        }
    }
}
