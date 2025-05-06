package com.platform.scheduler.infrastructure.cluster;

import com.platform.scheduler.domain.event.common.DomainEventPublisher;
import com.platform.scheduler.domain.model.executor.Executor;
import com.platform.scheduler.domain.service.ClusterCoordinationService;
import com.platform.scheduler.infrastructure.util.JsonUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 基于Redis的集群协调实现
 * 使用Redis实现集群节点管理、主节点选举和集群状态维护
 *
 * @author platform
 */
@Service
public class RedisClusterCoordinator {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisClusterCoordinator.class);
    
    private static final String KEY_PREFIX = "scheduler:cluster:";
    private static final String NODES_KEY = KEY_PREFIX + "nodes";
    private static final String MASTER_KEY = KEY_PREFIX + "master";
    private static final String ELECTION_LOCK_KEY = KEY_PREFIX + "election_lock";
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;
    private final DomainEventPublisher eventPublisher;
    
    @Value("${scheduler.cluster.node-id:}")
    private String configuredNodeId;
    
    @Value("${scheduler.cluster.heartbeat-interval:5000}")
    private long heartbeatInterval;
    
    @Value("${scheduler.cluster.node-timeout:15000}")
    private long nodeTimeout;
    
    @Value("${server.port:8080}")
    private int serverPort;
    
    private String nodeId;
    private ClusterNode localNode;
    private boolean isMaster = false;
    
    @Autowired
    public RedisClusterCoordinator(
            RedisTemplate<String, Object> redisTemplate,
            RedissonClient redissonClient,
            DomainEventPublisher eventPublisher) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * 初始化集群协调器
     * 在应用启动时注册本地节点并尝试选举主节点
     */
    @PostConstruct
    public void init() {
        try {
            // 初始化节点ID
            this.nodeId = configuredNodeId != null && !configuredNodeId.trim().isEmpty() ?
                    configuredNodeId : generateNodeId();
            
            // 创建本地节点信息
            initLocalNode();
            
            // 注册本地节点
            registerNode();
            
            // 尝试选举主节点
            electMaster();
            
            logger.info("Cluster coordinator initialized, nodeId: {}, isMaster: {}", nodeId, isMaster);
        } catch (Exception e) {
            logger.error("Failed to initialize cluster coordinator", e);
        }
    }
    
    /**
     * 在应用关闭时清理资源
     * 注销本地节点并释放主节点锁
     */
    @PreDestroy
    public void cleanup() {
        try {
            // 将本地节点状态设置为离开
            if (localNode != null) {
                localNode.setRole(ClusterNode.NodeRole.LEAVING);
                localNode.setStatus(ClusterNode.NodeStatus.OFFLINE);
                updateNode(localNode);
            }
            
            // 如果是主节点，释放主节点锁
            if (isMaster) {
                redisTemplate.delete(MASTER_KEY);
            }
            
            // 删除节点信息
            redisTemplate.opsForHash().delete(NODES_KEY, nodeId);
            
            logger.info("Cluster coordinator cleaned up, nodeId: {}", nodeId);
        } catch (Exception e) {
            logger.error("Failed to cleanup cluster coordinator", e);
        }
    }
    
    /**
     * 初始化本地节点信息
     */
    private void initLocalNode() throws Exception {
        String host = InetAddress.getLocalHost().getHostName();
        
        localNode = new ClusterNode();
        localNode.setNodeId(nodeId);
        localNode.setHost(host);
        localNode.setPort(serverPort);
        localNode.setStatus(ClusterNode.NodeStatus.ONLINE);
        localNode.setRole(ClusterNode.NodeRole.WORKER);
        localNode.setStartTime(LocalDateTime.now());
        localNode.setLastHeartbeatTime(LocalDateTime.now());
        
        // 设置系统信息
        Runtime runtime = Runtime.getRuntime();
        localNode.setCpuCores(runtime.availableProcessors());
        localNode.setTotalMemory(runtime.totalMemory());
        localNode.setFreeMemory(runtime.freeMemory());
        
        // 设置版本信息
        localNode.setVersion("1.0.0");
        localNode.setMaxConcurrency(20);
        localNode.setCurrentTaskCount(0);
    }
    
    /**
     * 生成节点ID
     *
     * @return 生成的节点ID
     */
    private String generateNodeId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 注册节点
     */
    private void registerNode() {
        try {
            updateNode(localNode);
            logger.info("Node registered: {}", nodeId);
        } catch (Exception e) {
            logger.error("Failed to register node", e);
        }
    }
    
    /**
     * 更新节点信息
     *
     * @param node 节点信息
     */
    private void updateNode(ClusterNode node) {
        String nodeJson = JsonUtils.toJson(node);
        redisTemplate.opsForHash().put(NODES_KEY, node.getNodeId(), nodeJson);
        // 设置过期时间为心跳间隔的3倍
        redisTemplate.expire(NODES_KEY, nodeTimeout * 3, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 获取所有在线节点
     *
     * @return 在线节点列表
     */
    public List<ClusterNode> getAllNodes() {
        List<ClusterNode> nodes = new ArrayList<>();
        
        try {
            Map<Object, Object> nodeMap = redisTemplate.opsForHash().entries(NODES_KEY);
            
            for (Object value : nodeMap.values()) {
                ClusterNode node = JsonUtils.fromJson(value.toString(), ClusterNode.class);
                if (node != null) {
                    nodes.add(node);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to get all nodes", e);
        }
        
        return nodes;
    }
    
    /**
     * 获取主节点
     *
     * @return 主节点
     */
    public Optional<ClusterNode> getMasterNode() {
        try {
            Object masterNodeId = redisTemplate.opsForValue().get(MASTER_KEY);
            
            if (masterNodeId != null) {
                Object nodeJson = redisTemplate.opsForHash().get(NODES_KEY, masterNodeId.toString());
                
                if (nodeJson != null) {
                    ClusterNode masterNode = JsonUtils.fromJson(nodeJson.toString(), ClusterNode.class);
                    return Optional.ofNullable(masterNode);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to get master node", e);
        }
        
        return Optional.empty();
    }
    
    /**
     * 选举主节点
     */
    private void electMaster() {
        RLock lock = redissonClient.getLock(ELECTION_LOCK_KEY);
        
        try {
            // 尝试获取选举锁
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                try {
                    // 检查是否已有主节点
                    Object existingMasterNodeId = redisTemplate.opsForValue().get(MASTER_KEY);
                    
                    if (existingMasterNodeId != null) {
                        // 检查主节点是否存在且在线
                        Object nodeJson = redisTemplate.opsForHash().get(NODES_KEY, existingMasterNodeId.toString());
                        
                        if (nodeJson != null) {
                            ClusterNode masterNode = JsonUtils.fromJson(nodeJson.toString(), ClusterNode.class);
                            
                            if (masterNode != null && masterNode.getStatus() == ClusterNode.NodeStatus.ONLINE) {
                                // 如果主节点在线，则保持当前主节点
                                isMaster = nodeId.equals(masterNode.getNodeId());
                                return;
                            }
                        }
                    }
                    
                    // 选举新的主节点（简单选择第一个在线节点）
                    List<ClusterNode> onlineNodes = getAllNodes().stream()
                            .filter(node -> node.getStatus() == ClusterNode.NodeStatus.ONLINE)
                            .collect(Collectors.toList());
                    
                    if (!onlineNodes.isEmpty()) {
                        // 选择自己作为主节点
                        ClusterNode newMaster = null;
                        
                        for (ClusterNode node : onlineNodes) {
                            if (nodeId.equals(node.getNodeId())) {
                                newMaster = node;
                                break;
                            }
                        }
                        
                        // 如果自己不在在线节点列表中，选择第一个在线节点
                        if (newMaster == null && !onlineNodes.isEmpty()) {
                            newMaster = onlineNodes.get(0);
                        }
                        
                        if (newMaster != null) {
                            // 更新主节点
                            redisTemplate.opsForValue().set(MASTER_KEY, newMaster.getNodeId());
                            
                            // 更新本地标记
                            isMaster = nodeId.equals(newMaster.getNodeId());
                            
                            // 更新本地节点角色
                            if (isMaster) {
                                localNode.setRole(ClusterNode.NodeRole.MASTER);
                                updateNode(localNode);
                                logger.info("Node elected as master: {}", nodeId);
                            }
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Master election interrupted", e);
        } catch (Exception e) {
            logger.error("Failed to elect master", e);
        }
    }
    
    /**
     * 发送心跳
     * 定时更新本地节点状态并检查主节点
     */
    @Scheduled(fixedRateString = "${scheduler.cluster.heartbeat-interval:5000}")
    public void heartbeat() {
        try {
            // 更新本地节点信息
            updateLocalNodeInfo();
            
            // 更新节点信息
            updateNode(localNode);
            
            // 检查并清理离线节点
            checkAndCleanupOfflineNodes();
            
            // 如果没有主节点，尝试选举
            Optional<ClusterNode> masterNode = getMasterNode();
            if (!masterNode.isPresent()) {
                electMaster();
            } else if (isMaster && !nodeId.equals(masterNode.get().getNodeId())) {
                // 如果本地认为自己是主节点，但实际主节点不是自己，则更新本地标记
                isMaster = false;
                localNode.setRole(ClusterNode.NodeRole.WORKER);
                updateNode(localNode);
            }
            
            logger.debug("Heartbeat sent, nodeId: {}, isMaster: {}", nodeId, isMaster);
        } catch (Exception e) {
            logger.error("Failed to send heartbeat", e);
        }
    }
    
    /**
     * 更新本地节点信息
     */
    private void updateLocalNodeInfo() {
        // 更新系统信息
        Runtime runtime = Runtime.getRuntime();
        localNode.setFreeMemory(runtime.freeMemory());
        
        // 更新心跳时间
        localNode.setLastHeartbeatTime(LocalDateTime.now());
        
        // TODO: 更新CPU使用率和当前任务数
    }
    
    /**
     * 检查并清理离线节点
     */
    private void checkAndCleanupOfflineNodes() {
        if (!isMaster) {
            return;
        }
        
        try {
            List<ClusterNode> nodes = getAllNodes();
            LocalDateTime now = LocalDateTime.now();
            
            for (ClusterNode node : nodes) {
                if (node.getLastHeartbeatTime() != null) {
                    // 计算节点最后心跳时间与当前时间的差值
                    long millisSinceLastHeartbeat = java.time.Duration.between(
                            node.getLastHeartbeatTime(), now).toMillis();
                    
                    // 如果超过超时时间且不是本地节点，则标记为离线
                    if (millisSinceLastHeartbeat > nodeTimeout && !nodeId.equals(node.getNodeId())) {
                        node.setStatus(ClusterNode.NodeStatus.OFFLINE);
                        updateNode(node);
                        logger.info("Node marked as offline: {}", node.getNodeId());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to check and cleanup offline nodes", e);
        }
    }
    
    /**
     * 强制触发主节点选举
     */
    public void triggerMasterElection() {
        try {
            // 删除当前主节点信息
            redisTemplate.delete(MASTER_KEY);
            
            // 选举新主节点
            electMaster();
            
            logger.info("Master election triggered");
        } catch (Exception e) {
            logger.error("Failed to trigger master election", e);
        }
    }
    
    /**
     * 检查当前节点是否为主节点
     *
     * @return 是否为主节点
     */
    public boolean isMasterNode() {
        return isMaster;
    }
    
    /**
     * 获取集群状态信息
     *
     * @return 集群状态信息
     */
    public ClusterStatus getClusterStatus() {
        ClusterStatus status = new ClusterStatus();
        
        try {
            // 获取所有节点
            List<ClusterNode> nodes = getAllNodes();
            
            // 获取主节点
            Optional<ClusterNode> masterNode = getMasterNode();
            
            // 设置集群状态信息
            status.setTotalNodes(nodes.size());
            status.setOnlineNodes((int) nodes.stream()
                    .filter(node -> node.getStatus() == ClusterNode.NodeStatus.ONLINE)
                    .count());
            status.setOfflineNodes((int) nodes.stream()
                    .filter(node -> node.getStatus() == ClusterNode.NodeStatus.OFFLINE)
                    .count());
            status.setMaintenanceNodes((int) nodes.stream()
                    .filter(node -> node.getStatus() == ClusterNode.NodeStatus.MAINTENANCE)
                    .count());
            
            masterNode.ifPresent(node -> status.setMasterNodeId(node.getNodeId()));
            
            // 计算总任务槽和已用任务槽
            int totalSlots = 0;
            int usedSlots = 0;
            
            for (ClusterNode node : nodes) {
                if (node.getStatus() == ClusterNode.NodeStatus.ONLINE) {
                    totalSlots += node.getMaxConcurrency();
                    usedSlots += node.getCurrentTaskCount();
                }
            }
            
            status.setTotalTaskSlots(totalSlots);
            status.setUsedTaskSlots(usedSlots);
            
            // 设置健康状态
            status.setHealthy(status.getOnlineNodes() > 0 && masterNode.isPresent());
            
        } catch (Exception e) {
            logger.error("Failed to get cluster status", e);
        }
        
        return status;
    }
    
    /**
     * 集群状态信息
     */
    @lombok.Data
    public static class ClusterStatus {
        private int totalNodes;
        private int onlineNodes;
        private int offlineNodes;
        private int maintenanceNodes;
        private String masterNodeId;
        private boolean healthy;
        private int totalTaskSlots;
        private int usedTaskSlots;
    }
}
