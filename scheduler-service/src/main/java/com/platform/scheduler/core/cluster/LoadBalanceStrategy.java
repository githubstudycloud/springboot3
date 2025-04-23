package com.platform.scheduler.core.cluster;

import java.util.List;

import com.platform.scheduler.model.SchedulerNode;
import com.platform.scheduler.model.Task;

/**
 * 负载均衡策略接口
 * 
 * @author platform
 */
public interface LoadBalanceStrategy {
    
    /**
     * 为任务选择合适的节点
     * 
     * @param task 任务
     * @param availableNodes 可用节点列表
     * @return 选择的节点，如果没有合适的节点则返回null
     */
    SchedulerNode selectNode(Task task, List<SchedulerNode> availableNodes);
    
    /**
     * 获取策略名称
     * 
     * @return 策略名称
     */
    String getName();
    
    /**
     * 获取策略描述
     * 
     * @return 策略描述
     */
    String getDescription();
}
