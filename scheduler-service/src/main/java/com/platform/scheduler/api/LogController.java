package com.platform.scheduler.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.scheduler.model.TaskLog;
import com.platform.scheduler.repository.LogQueryParam;
import com.platform.scheduler.service.LogService;

/**
 * 日志API控制器
 * 
 * @author platform
 */
@RestController
@RequestMapping("/api/logs")
public class LogController {
    
    @Autowired
    private LogService logService;
    
    /**
     * 记录日志
     * 
     * @param log 日志对象
     * @return 日志ID
     */
    @PostMapping("/info")
    public ResponseEntity<Long> logInfo(@RequestBody TaskLog log) {
        Long logId = logService.logInfo(log);
        return ResponseEntity.ok(logId);
    }
    
    /**
     * 记录警告日志
     * 
     * @param log 日志对象
     * @return 日志ID
     */
    @PostMapping("/warning")
    public ResponseEntity<Long> logWarning(@RequestBody TaskLog log) {
        Long logId = logService.logWarning(log);
        return ResponseEntity.ok(logId);
    }
    
    /**
     * 记录错误日志
     * 
     * @param log 日志对象
     * @return 日志ID
     */
    @PostMapping("/error")
    public ResponseEntity<Long> logError(@RequestBody TaskLog log) {
        Long logId = logService.logError(log);
        return ResponseEntity.ok(logId);
    }
    
    /**
     * 根据任务ID查询日志
     * 
     * @param taskId 任务ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<Page<TaskLog>> findLogsByTaskId(
            @PathVariable Long taskId,
            Pageable pageable) {
        Page<TaskLog> logs = logService.findLogsByTaskId(taskId, pageable);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 根据执行ID查询日志
     * 
     * @param executionId 执行ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping("/execution/{executionId}")
    public ResponseEntity<Page<TaskLog>> findLogsByExecutionId(
            @PathVariable Long executionId,
            Pageable pageable) {
        Page<TaskLog> logs = logService.findLogsByExecutionId(executionId, pageable);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 根据日志级别查询日志
     * 
     * @param level 日志级别
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<Page<TaskLog>> findLogsByLevel(
            @PathVariable String level,
            Pageable pageable) {
        Page<TaskLog> logs = logService.findLogsByLevel(level, pageable);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 根据多条件查询日志
     * 
     * @param queryParam 查询参数
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<Page<TaskLog>> findLogs(
            @RequestBody LogQueryParam queryParam,
            Pageable pageable) {
        Page<TaskLog> logs = logService.findLogs(queryParam, pageable);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 清理过期日志
     * 
     * @param retentionDays 保留天数
     * @return 清理的记录数
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Integer> cleanupLogs(@RequestParam int retentionDays) {
        int count = logService.cleanupLogs(retentionDays);
        return ResponseEntity.ok(count);
    }
    
    /**
     * 导出日志
     * 
     * @param queryParam 查询参数
     * @return 导出文件路径
     */
    @PostMapping("/export")
    public ResponseEntity<String> exportLogs(@RequestBody LogQueryParam queryParam) {
        String filePath = logService.exportLogs(queryParam);
        return ResponseEntity.ok(filePath);
    }
}
