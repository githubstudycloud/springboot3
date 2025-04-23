package com.platform.scheduler.core.cluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.platform.scheduler.model.SchedulerNode;
import com.platform.scheduler.model.Task;
import com.platform.scheduler.repository.TaskExecutionRepository;

/**
 * 最小负载策略
 * 
 * @author platform
 */
@Component
public class LeastLoadStrategy implements LoadBalanceStrategy {
    
    @Autowired
    private TaskExecutionRepository taskExecutionRepository;
    
    /**
     * 节点负载缓存，定期更新
     */
    private final Map<String, Integer> nodeLoadCache = new ConcurrentHashMap<>();
    
    /**
     * 上次更新时间
     */
    private long lastUpdateTime = 0;
    
    /**
     * 缓存过期时间（5秒）
     */
    private static final long CACHE_EXPIRE_TIME = 5000;
    
    @Override
    public SchedulerNode selectNode(Task task, List<SchedulerNode> availableNodes) {
        if (availableNodes == null || availableNodes.isEmpty()) {
            return null;
        }
        
        // 获取节点负载
        Map<String, Integer> nodeLoads = getNodeLoads(availableNodes);
        
        // 找出负载最小的节点
        SchedulerNode selectedNode = null;
        int minLoad = Integer.MAX_VALUE;
        
        for (SchedulerNode node : availableNodes) {
            int load = nodeLoads.getOrDefault(node.getNodeId(), 0);
            if (load < minLoad) {
                minLoad = load;
                selectedNode = node;
            }
        }
        
        return selectedNode;
    }
    
    /**
     * 获取节点负载
     * 
     * @param nodes 节点列表
     * @return 节点负载映射
     */
    private Map<String, Integer> getNodeLoads(List<SchedulerNode> nodes) {
        long now = System.currentTimeMillis();
        
        // 如果缓存未过期，直接返回缓存
        if (now - lastUpdateTime < CACHE_EXPIRE_TIME && !nodeLoadCache.isEmpty()) {
            return new HashMap<>(nodeLoadCache);
        }
        
        // 更新缓存
        synchronized (this) {
            if (now - lastUpdateTime >= CACHE_EXPIRE_TIME || nodeLoadCache.isEmpty()) {
                // 清空缓存
                nodeLoadCache.clear();
                
                // 查询每个节点的运行中任务数
                for (SchedulerNode node : nodes) {
                    int runningCount = taskExecutionRepository.countByNodeIdAndStatus(
                            node.getNodeId(), "RUNNING");
                    nodeLoadCache.put(node.getNodeId(), runningCount);
                }
                
                // 更新时间
                lastUpdateTime = now;
            }
        }
        
        return new HashMap<>(nodeLoadCache);
    }
    
    @Override
    public String getName() {
        return "LeastLoad";
    }
    
    @Override
    public String getDescription() {
        return "最小负载策略：选择当前负载最小的节点";
    }
}
