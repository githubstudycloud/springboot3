package com.platform.report.interfaces.rest;

import com.platform.report.application.dto.schedule.*;
import com.platform.report.application.service.ScheduleApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报表计划REST控制器
 */
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Tag(name = "报表计划管理", description = "提供报表计划相关的管理API")
public class ScheduleController {
    
    private final ScheduleApplicationService scheduleApplicationService;
    
    @PostMapping
    @Operation(summary = "创建报表计划", description = "创建新的报表计划")
    public ResponseEntity<String> createSchedule(@RequestBody CreateScheduleCommand command) {
        String scheduleId = scheduleApplicationService.createSchedule(command);
        return ResponseEntity.ok(scheduleId);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取计划详情", description = "根据ID获取计划详细信息")
    public ResponseEntity<ScheduleDetailDTO> getScheduleDetail(@PathVariable String id) {
        ScheduleDetailDTO schedule = scheduleApplicationService.getScheduleDetail(id);
        return ResponseEntity.ok(schedule);
    }
    
    @GetMapping
    @Operation(summary = "查询计划列表", description = "根据条件查询计划列表")
    public ResponseEntity<List<ScheduleDTO>> getScheduleList(
            @ModelAttribute ScheduleQueryDTO query) {
        List<ScheduleDTO> schedules = scheduleApplicationService.getScheduleList(query);
        return ResponseEntity.ok(schedules);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新计划", description = "更新计划配置")
    public ResponseEntity<Boolean> updateSchedule(
            @PathVariable String id,
            @RequestBody UpdateScheduleCommand command) {
        command.setScheduleId(id);
        boolean success = scheduleApplicationService.updateSchedule(command);
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/{id}/pause")
    @Operation(summary = "暂停计划", description = "暂停指定计划")
    public ResponseEntity<Boolean> pauseSchedule(
            @PathVariable String id,
            @RequestParam String operatedBy) {
        boolean success = scheduleApplicationService.pauseSchedule(id, operatedBy);
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/{id}/resume")
    @Operation(summary = "恢复计划", description = "恢复指定计划")
    public ResponseEntity<Boolean> resumeSchedule(
            @PathVariable String id,
            @RequestParam String operatedBy) {
        boolean success = scheduleApplicationService.resumeSchedule(id, operatedBy);
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消计划", description = "取消指定计划")
    public ResponseEntity<Boolean> cancelSchedule(
            @PathVariable String id,
            @RequestParam String operatedBy) {
        boolean success = scheduleApplicationService.cancelSchedule(id, operatedBy);
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/{id}/trigger")
    @Operation(summary = "触发计划执行", description = "手动触发计划执行")
    public ResponseEntity<String> triggerScheduleExecution(
            @PathVariable String id,
            @RequestParam String operatedBy) {
        String reportId = scheduleApplicationService.triggerScheduleExecution(id, operatedBy);
        return ResponseEntity.ok(reportId);
    }
    
    @GetMapping("/{id}/execution-history")
    @Operation(summary = "获取执行历史", description = "获取计划的执行历史记录")
    public ResponseEntity<List<ScheduleExecutionDTO>> getScheduleExecutionHistory(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<ScheduleExecutionDTO> history = 
                scheduleApplicationService.getScheduleExecutionHistory(id, pageNum, pageSize);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/recent-executions")
    @Operation(summary = "获取最近执行历史", description = "获取最近的计划执行历史记录")
    public ResponseEntity<List<ScheduleExecutionDTO>> getRecentExecutions(
            @RequestParam(defaultValue = "10") int count) {
        List<ScheduleExecutionDTO> executions = scheduleApplicationService.getRecentExecutions(count);
        return ResponseEntity.ok(executions);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除计划", description = "删除指定计划")
    public ResponseEntity<Boolean> deleteSchedule(
            @PathVariable String id,
            @RequestParam String operatedBy) {
        boolean success = scheduleApplicationService.deleteSchedule(id, operatedBy);
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/{id}/copy")
    @Operation(summary = "复制计划", description = "复制现有计划创建新计划")
    public ResponseEntity<String> copySchedule(
            @PathVariable String id,
            @RequestParam String newName,
            @RequestParam String createdBy) {
        String scheduleId = scheduleApplicationService.copySchedule(id, newName, createdBy);
        return ResponseEntity.ok(scheduleId);
    }
}
