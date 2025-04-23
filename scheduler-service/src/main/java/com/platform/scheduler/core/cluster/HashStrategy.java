package com.platform.scheduler.core.cluster;

import java.util.List;

import org.springframework.stereotype.Component;

import com.platform.scheduler.model.SchedulerNode;
import com.platform.scheduler.model.Task;

/**
 * 哈希负载均衡策略
 * 
 * @author platform
 */
@Component
public class HashStrategy implements LoadBalanceStrategy {
    
    @Override
    public SchedulerNode selectNode(Task task, List<SchedulerNode> availableNodes) {
        if (availableNodes == null || availableNodes.isEmpty()) {
            return null;
        }
        
        if (task == null || task.getId() == null) {
            // 如果没有任务ID，则使用随机选择
            int randomIndex = (int) (System.currentTimeMillis() % availableNodes.size());
            return availableNodes.get(randomIndex);
        }
        
        // 使用任务ID的哈希值选择节点
        int index = Math.abs(task.getId().hashCode() % availableNodes.size());
        return availableNodes.get(index);
    }
    
    @Override
    public String getName() {
        return "Hash";
    }
    
    @Override
    public String getDescription() {
        return "哈希策略：根据任务ID的哈希值选择节点，相同任务总是分配到相同节点";
    }
}
