package com.platform.scheduler.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.scheduler.model.TaskProgress;
import com.platform.scheduler.service.TaskProgressService;

/**
 * 任务进度API控制器
 * 
 * @author platform
 */
@RestController
@RequestMapping("/api/progress")
public class TaskProgressController {
    
    @Autowired
    private TaskProgressService taskProgressService;
    
    /**
     * 更新任务进度
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @param progress 进度百分比
     * @param statusDesc 状态描述
     * @return 进度ID
     */
    @PutMapping
    public ResponseEntity<Long> updateProgress(
            @RequestParam Long taskId,
            @RequestParam Long executionId,
            @RequestParam int progress,
            @RequestParam(required = false) String statusDesc) {
        Long progressId = taskProgressService.updateProgress(taskId, executionId, progress, statusDesc);
        return ResponseEntity.ok(progressId);
    }
    
    /**
     * 完成任务进度
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @return 操作结果
     */
    @PutMapping("/complete")
    public ResponseEntity<Void> completeProgress(
            @RequestParam Long taskId,
            @RequestParam Long executionId) {
        boolean completed = taskProgressService.completeProgress(taskId, executionId);
        return completed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 根据任务ID和执行ID查询进度
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @return 任务进度
     */
    @GetMapping("/task/{taskId}/execution/{executionId}")
    public ResponseEntity<TaskProgress> getProgress(
            @PathVariable Long taskId,
            @PathVariable Long executionId) {
        TaskProgress progress = taskProgressService.getProgress(taskId, executionId);
        return ResponseEntity.ok(progress);
    }
    
    /**
     * 根据任务ID查询最新进度
     * 
     * @param taskId 任务ID
     * @return 任务进度
     */
    @GetMapping("/task/{taskId}/latest")
    public ResponseEntity<TaskProgress> getLatestProgress(@PathVariable Long taskId) {
        TaskProgress progress = taskProgressService.getLatestProgress(taskId);
        return ResponseEntity.ok(progress);
    }
    
    /**
     * 根据执行ID查询进度
     * 
     * @param executionId 执行ID
     * @return 任务进度
     */
    @GetMapping("/execution/{executionId}")
    public ResponseEntity<TaskProgress> getProgressByExecutionId(@PathVariable Long executionId) {
        TaskProgress progress = taskProgressService.getProgressByExecutionId(executionId);
        return ResponseEntity.ok(progress);
    }
    
    /**
     * 根据任务ID分页查询进度历史
     * 
     * @param taskId 任务ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping("/task/{taskId}/history")
    public ResponseEntity<Page<TaskProgress>> findProgressHistory(
            @PathVariable Long taskId,
            Pageable pageable) {
        Page<TaskProgress> progressHistory = taskProgressService.findProgressHistory(taskId, pageable);
        return ResponseEntity.ok(progressHistory);
    }
    
    /**
     * 清理已完成的进度记录
     * 
     * @param retentionCount 每个任务保留的记录数
     * @return 清理的记录数
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Integer> cleanupCompletedProgress(@RequestParam int retentionCount) {
        int count = taskProgressService.cleanupCompletedProgress(retentionCount);
        return ResponseEntity.ok(count);
    }
}
