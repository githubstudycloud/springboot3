package com.platform.scheduler.core.cluster;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.platform.scheduler.core.lock.DistributedLockManager;
import com.platform.scheduler.model.SchedulerNode;
import com.platform.scheduler.model.Task;
import com.platform.scheduler.service.SchedulerNodeService;
import com.platform.scheduler.util.SchedulerThreadFactory;

/**
 * 集群管理器
 * 
 * @author platform
 */
@Component
public class ClusterManager implements InitializingBean, DisposableBean {
    
    private static final Logger logger = LoggerFactory.getLogger(ClusterManager.class);
    
    @Autowired
    private SchedulerNodeService nodeService;
    
    @Autowired
    private DistributedLockManager lockManager;
    
    @Autowired
    private LoadBalanceStrategyFactory strategyFactory;
    
    @Value("${scheduler.cluster.health-check-rate:30000}")
    private long healthCheckRate;
    
    @Value("${scheduler.cluster.load-balance-strategy:RoundRobin}")
    private String loadBalanceStrategy;
    
    @Value("${scheduler.cluster.node-timeout:60}")
    private int nodeTimeout;
    
    @Value("${scheduler.cluster.leader-elect-enabled:true}")
    private boolean leaderElectEnabled;
    
    /**
     * 是否是主节点
     */
    private volatile boolean isLeader = false;
    
    /**
     * 调度器
     */
    private ScheduledExecutorService scheduler;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化线程池
        scheduler = new ScheduledThreadPoolExecutor(2, new SchedulerThreadFactory("ClusterManager"));
        
        // 注册当前节点
        registerCurrentNode();
        
        // 启动心跳任务
        startHeartbeat();
        
        // 启动节点检查任务
        startNodeCheck();
        
        // 启动主节点选举
        if (leaderElectEnabled) {
            startLeaderElection();
        }
    }
    
    @Override
    public void destroy() throws Exception {
        // 关闭调度器
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        // 下线当前节点
        offlineCurrentNode();
    }
    
    /**
     * 注册当前节点
     */
    private void registerCurrentNode() {
        try {
            SchedulerNode currentNode = nodeService.getCurrentNode();
            if (currentNode != null) {
                logger.info("当前节点注册成功: {}", currentNode.getNodeId());
            } else {
                logger.error("当前节点注册失败");
            }
        } catch (Exception e) {
            logger.error("注册当前节点异常", e);
        }
    }
    
    /**
     * 下线当前节点
     */
    private void offlineCurrentNode() {
        try {
            SchedulerNode currentNode = nodeService.getCurrentNode();
            if (currentNode != null) {
                nodeService.offlineNode(currentNode.getNodeId());
                logger.info("当前节点已下线: {}", currentNode.getNodeId());
            }
        } catch (Exception e) {
            logger.error("下线当前节点异常", e);
        }
    }
    
    /**
     * 启动心跳任务
     */
    private void startHeartbeat() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                SchedulerNode currentNode = nodeService.getCurrentNode();
                if (currentNode != null) {
                    nodeService.updateHeartbeat(currentNode.getNodeId());
                }
            } catch (Exception e) {
                logger.error("更新节点心跳异常", e);
            }
        }, 10, 10, TimeUnit.SECONDS);
        
        logger.info("节点心跳任务已启动");
    }
    
    /**
     * 启动节点检查任务
     */
    private void startNodeCheck() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (isLeader) {
                    logger.debug("主节点开始检查节点状态");
                    int count = nodeService.handleTimeoutNodes(nodeTimeout);
                    if (count > 0) {
                        logger.info("处理超时节点: {} 个", count);
                    }
                }
            } catch (Exception e) {
                logger.error("检查节点状态异常", e);
            }
        }, 30, 30, TimeUnit.SECONDS);
        
        logger.info("节点检查任务已启动");
    }
    
    /**
     * 启动主节点选举
     */
    private void startLeaderElection() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                electLeader();
            } catch (Exception e) {
                logger.error("主节点选举异常", e);
            }
        }, 5, 20, TimeUnit.SECONDS);
        
        logger.info("主节点选举任务已启动");
    }
    
    /**
     * 选举主节点
     */
    private void electLeader() {
        String leaderLockKey = "cluster_leader_lock";
        boolean gotLock = false;
        
        try {
            // 尝试获取分布式锁
            gotLock = lockManager.tryLock(leaderLockKey, 15, 10);
            
            if (gotLock) {
                if (!isLeader) {
                    isLeader = true;
                    logger.info("当前节点被选为主节点");
                }
            } else {
                if (isLeader) {
                    isLeader = false;
                    logger.info("当前节点不再是主节点");
                }
            }
        } catch (Exception e) {
            logger.error("获取主节点锁异常", e);
            isLeader = false;
        } finally {
            // 注意：这里不释放锁，让锁自动过期
            // 这样当前获得锁的节点可以保持主节点状态一段时间
        }
    }
    
    /**
     * 为任务选择节点
     * 
     * @param task 任务
     * @return 选择的节点，如果没有可用节点则返回null
     */
    public SchedulerNode selectNodeForTask(Task task) {
        try {
            // 获取所有在线节点
            List<SchedulerNode> onlineNodes = nodeService.findOnlineNodes();
            if (onlineNodes == null || onlineNodes.isEmpty()) {
                logger.warn("没有可用的在线节点");
                return null;
            }
            
            // 获取负载均衡策略
            LoadBalanceStrategy strategy = strategyFactory.getStrategy(loadBalanceStrategy);
            
            // 选择节点
            SchedulerNode selectedNode = strategy.selectNode(task, onlineNodes);
            
            if (selectedNode != null) {
                logger.info("为任务选择节点: 任务ID={}, 节点ID={}, 策略={}", 
                        task.getId(), selectedNode.getNodeId(), strategy.getName());
            } else {
                logger.warn("无法为任务选择合适节点: 任务ID={}", task.getId());
            }
            
            return selectedNode;
            
        } catch (Exception e) {
            logger.error("选择节点异常: 任务ID=" + task.getId(), e);
            return null;
        }
    }
    
    /**
     * 是否是主节点
     * 
     * @return 是否是主节点
     */
    public boolean isLeader() {
        return isLeader;
    }
}
