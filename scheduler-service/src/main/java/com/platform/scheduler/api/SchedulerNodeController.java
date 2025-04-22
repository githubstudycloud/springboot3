package com.platform.scheduler.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.scheduler.model.SchedulerNode;
import com.platform.scheduler.service.SchedulerNodeService;

/**
 * 调度节点API控制器
 * 
 * @author platform
 */
@RestController
@RequestMapping("/api/nodes")
public class SchedulerNodeController {
    
    @Autowired
    private SchedulerNodeService schedulerNodeService;
    
    /**
     * 注册节点
     * 
     * @param node 节点信息
     * @return 注册后的节点
     */
    @PostMapping("/register")
    public ResponseEntity<SchedulerNode> registerNode(@RequestBody SchedulerNode node) {
        SchedulerNode registeredNode = schedulerNodeService.registerNode(node);
        return ResponseEntity.ok(registeredNode);
    }
    
    /**
     * 更新节点心跳
     * 
     * @param nodeId 节点ID
     * @return 操作结果
     */
    @PutMapping("/{nodeId}/heartbeat")
    public ResponseEntity<Void> updateHeartbeat(@PathVariable String nodeId) {
        boolean updated = schedulerNodeService.updateHeartbeat(nodeId);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 节点下线
     * 
     * @param nodeId 节点ID
     * @return 操作结果
     */
    @PutMapping("/{nodeId}/offline")
    public ResponseEntity<Void> offlineNode(@PathVariable String nodeId) {
        boolean offlined = schedulerNodeService.offlineNode(nodeId);
        return offlined ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 根据节点ID查询节点
     * 
     * @param nodeId 节点ID
     * @return 节点信息
     */
    @GetMapping("/{nodeId}")
    public ResponseEntity<SchedulerNode> getNodeById(@PathVariable String nodeId) {
        SchedulerNode node = schedulerNodeService.getNodeById(nodeId);
        return ResponseEntity.ok(node);
    }
    
    /**
     * 查询所有在线节点
     * 
     * @return 节点列表
     */
    @GetMapping("/online")
    public ResponseEntity<List<SchedulerNode>> findOnlineNodes() {
        List<SchedulerNode> nodes = schedulerNodeService.findOnlineNodes();
        return ResponseEntity.ok(nodes);
    }
    
    /**
     * 查询所有节点
     * 
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<Page<SchedulerNode>> findAllNodes(Pageable pageable) {
        Page<SchedulerNode> nodes = schedulerNodeService.findAllNodes(pageable);
        return ResponseEntity.ok(nodes);
    }
    
    /**
     * 查询状态节点
     * 
     * @param status 节点状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<SchedulerNode>> findNodesByStatus(
            @PathVariable String status,
            Pageable pageable) {
        Page<SchedulerNode> nodes = schedulerNodeService.findNodesByStatus(status, pageable);
        return ResponseEntity.ok(nodes);
    }
    
    /**
     * 检查并处理心跳超时的节点
     * 
     * @param timeoutSeconds 超时秒数
     * @return 处理的节点数
     */
    @PostMapping("/handle-timeout")
    public ResponseEntity<Integer> handleTimeoutNodes(@RequestParam int timeoutSeconds) {
        int count = schedulerNodeService.handleTimeoutNodes(timeoutSeconds);
        return ResponseEntity.ok(count);
    }
    
    /**
     * 获取当前节点
     * 
     * @return 当前节点信息
     */
    @GetMapping("/current")
    public ResponseEntity<SchedulerNode> getCurrentNode() {
        SchedulerNode node = schedulerNodeService.getCurrentNode();
        return ResponseEntity.ok(node);
    }
    
    /**
     * 选择可用节点执行任务
     * 
     * @param taskId 任务ID
     * @return 选中的节点
     */
    @GetMapping("/select")
    public ResponseEntity<SchedulerNode> selectNodeForTask(@RequestParam Long taskId) {
        SchedulerNode node = schedulerNodeService.selectNodeForTask(taskId);
        return ResponseEntity.ok(node);
    }
    
    /**
     * 重新分配节点上的任务
     * 
     * @param nodeId 节点ID
     * @return 重分配的任务数
     */
    @PostMapping("/{nodeId}/reassign-tasks")
    public ResponseEntity<Integer> reassignTasks(@PathVariable String nodeId) {
        int count = schedulerNodeService.reassignTasks(nodeId);
        return ResponseEntity.ok(count);
    }
}
