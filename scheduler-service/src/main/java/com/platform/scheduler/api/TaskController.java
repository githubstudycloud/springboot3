package com.platform.scheduler.api;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.scheduler.model.Task;
import com.platform.scheduler.service.TaskService;

/**
 * 任务API控制器
 * 
 * @author platform
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    /**
     * 创建任务
     * 
     * @param task 任务对象
     * @return 创建的任务
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }
    
    /**
     * 更新任务
     * 
     * @param taskId 任务ID
     * @param task 任务对象
     * @return 更新后的任务
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId, @RequestBody Task task) {
        task.setId(taskId);
        Task updatedTask = taskService.updateTask(task);
        return ResponseEntity.ok(updatedTask);
    }
    
    /**
     * 根据ID查询任务
     * 
     * @param taskId 任务ID
     * @return 任务对象
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
        Task task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }
    
    /**
     * 删除任务
     * 
     * @param taskId 任务ID
     * @return 操作结果
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        boolean deleted = taskService.deleteTask(taskId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 启用任务
     * 
     * @param taskId 任务ID
     * @return 更新后的任务
     */
    @PutMapping("/{taskId}/enable")
    public ResponseEntity<Task> enableTask(@PathVariable Long taskId) {
        Task task = taskService.enableTask(taskId);
        return ResponseEntity.ok(task);
    }
    
    /**
     * 禁用任务
     * 
     * @param taskId 任务ID
     * @return 更新后的任务
     */
    @PutMapping("/{taskId}/disable")
    public ResponseEntity<Task> disableTask(@PathVariable Long taskId) {
        Task task = taskService.disableTask(taskId);
        return ResponseEntity.ok(task);
    }
    
    /**
     * 暂停任务
     * 
     * @param taskId 任务ID
     * @return 更新后的任务
     */
    @PutMapping("/{taskId}/pause")
    public ResponseEntity<Task> pauseTask(@PathVariable Long taskId) {
        Task task = taskService.pauseTask(taskId);
        return ResponseEntity.ok(task);
    }
    
    /**
     * 恢复任务
     * 
     * @param taskId 任务ID
     * @return 更新后的任务
     */
    @PutMapping("/{taskId}/resume")
    public ResponseEntity<Task> resumeTask(@PathVariable Long taskId) {
        Task task = taskService.resumeTask(taskId);
        return ResponseEntity.ok(task);
    }
    
    /**
     * 手动触发任务
     * 
     * @param taskId 任务ID
     * @return 执行ID
     */
    @PostMapping("/{taskId}/trigger")
    public ResponseEntity<Long> triggerTask(@PathVariable Long taskId) {
        Long executionId = taskService.triggerTask(taskId);
        return ResponseEntity.ok(executionId);
    }
    
    /**
     * 分页查询任务
     * 
     * @param status 任务状态（可选）
     * @param type 任务类型（可选）
     * @param name 任务名称（可选，模糊匹配）
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<Page<Task>> findTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name,
            Pageable pageable) {
        Page<Task> tasks;
        
        if (status != null && type != null && name != null) {
            tasks = taskService.findTasksByStatusAndTypeAndName(status, type, name, pageable);
        } else if (status != null && type != null) {
            tasks = taskService.findTasksByStatusAndType(status, type, pageable);
        } else if (status != null && name != null) {
            tasks = taskService.findTasksByStatusAndName(status, name, pageable);
        } else if (type != null) {
            tasks = taskService.findTasksByType(type, pageable);
        } else if (status != null) {
            tasks = taskService.findTasksByStatus(status, pageable);
        } else if (name != null) {
            tasks = taskService.findTasksByName(name, pageable);
        } else {
            tasks = taskService.findTasks(pageable);
        }
        
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * 更新任务状态
     * 
     * @param taskId 任务ID
     * @param status 任务状态
     * @return 操作结果
     */
    @PutMapping("/{taskId}/status")
    public ResponseEntity<Void> updateTaskStatus(@PathVariable Long taskId, @RequestParam String status) {
        boolean updated = taskService.updateTaskStatus(taskId, status);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 更新任务下次执行时间
     * 
     * @param taskId 任务ID
     * @param nextExecutionTime 下次执行时间
     * @return 操作结果
     */
    @PutMapping("/{taskId}/next-execution-time")
    public ResponseEntity<Void> updateNextExecutionTime(
            @PathVariable Long taskId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date nextExecutionTime) {
        boolean updated = taskService.updateNextExecutionTime(taskId, nextExecutionTime);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
