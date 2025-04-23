package com.platform.scheduler.core.cluster;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.platform.scheduler.model.SchedulerNode;
import com.platform.scheduler.model.Task;

/**
 * 轮询负载均衡策略
 * 
 * @author platform
 */
@Component
public class RoundRobinStrategy implements LoadBalanceStrategy {
    
    /**
     * 当前索引
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    
    @Override
    public SchedulerNode selectNode(Task task, List<SchedulerNode> availableNodes) {
        if (availableNodes == null || availableNodes.isEmpty()) {
            return null;
        }
        
        // 获取下一个索引
        int index = getNextIndex(availableNodes.size());
        
        // 返回选中的节点
        return availableNodes.get(index);
    }
    
    /**
     * 获取下一个索引
     * 
     * @param size 节点数量
     * @return 索引
     */
    private int getNextIndex(int size) {
        // 递增索引并取模
        int current = currentIndex.getAndIncrement();
        
        // 处理溢出情况
        if (current >= 1_000_000) {
            // 重置计数器
            synchronized (this) {
                if (currentIndex.get() >= 1_000_000) {
                    currentIndex.set(0);
                    current = 0;
                }
            }
        }
        
        return Math.abs(current % size);
    }
    
    @Override
    public String getName() {
        return "RoundRobin";
    }
    
    @Override
    public String getDescription() {
        return "轮询策略：依次选择可用节点";
    }
}
