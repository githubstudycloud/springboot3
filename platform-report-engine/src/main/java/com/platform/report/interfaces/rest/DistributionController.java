package com.platform.report.interfaces.rest;

import com.platform.report.application.dto.distribution.*;
import com.platform.report.application.service.DistributionApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报表分发REST控制器
 */
@RestController
@RequestMapping("/api/distributions")
@RequiredArgsConstructor
@Tag(name = "报表分发管理", description = "提供报表分发相关的管理API")
public class DistributionController {
    
    private final DistributionApplicationService distributionApplicationService;
    
    @PostMapping
    @Operation(summary = "创建分发", description = "创建新的报表分发")
    public ResponseEntity<String> createDistribution(@RequestBody CreateDistributionCommand command) {
        String distributionId = distributionApplicationService.createDistribution(command);
        return ResponseEntity.ok(distributionId);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取分发详情", description = "根据ID获取分发详情")
    public ResponseEntity<DistributionDetailDTO> getDistributionDetail(@PathVariable String id) {
        DistributionDetailDTO distribution = distributionApplicationService.getDistributionDetail(id);
        return ResponseEntity.ok(distribution);
    }
    
    @GetMapping
    @Operation(summary = "查询分发列表", description = "根据条件查询分发列表")
    public ResponseEntity<List<DistributionDTO>> getDistributionList(
            @ModelAttribute DistributionQueryDTO query) {
        List<DistributionDTO> distributions = distributionApplicationService.getDistributionList(query);
        return ResponseEntity.ok(distributions);
    }
    
    @PostMapping("/{id}/channels")
    @Operation(summary = "添加分发渠道", description = "为分发添加渠道")
    public ResponseEntity<String> addDistributionChannel(
            @PathVariable String id,
            @RequestBody AddChannelCommand command) {
        command.setDistributionId(id);
        String channelId = distributionApplicationService.addDistributionChannel(command);
        return ResponseEntity.ok(channelId);
    }
    
    @PutMapping("/{id}/channels/{channelId}")
    @Operation(summary = "更新分发渠道", description = "更新分发的渠道")
    public ResponseEntity<Boolean> updateDistributionChannel(
            @PathVariable String id,
            @PathVariable String channelId,
            @RequestBody UpdateChannelCommand command) {
        command.setDistributionId(id);
        command.setChannelId(channelId);
        boolean success = distributionApplicationService.updateDistributionChannel(command);
        return ResponseEntity.ok(success);
    }
    
    @DeleteMapping("/{id}/channels/{channelId}")
    @Operation(summary = "删除分发渠道", description = "删除分发的渠道")
    public ResponseEntity<Boolean> deleteDistributionChannel(
            @PathVariable String id,
            @PathVariable String channelId,
            @RequestParam String operatedBy) {
        boolean success = distributionApplicationService.deleteDistributionChannel(id, channelId, operatedBy);
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/{id}/start")
    @Operation(summary = "开始分发", description = "开始执行分发")
    public ResponseEntity<Boolean> startDistribution(
            @PathVariable String id,
            @RequestParam String operatedBy) {
        boolean success = distributionApplicationService.startDistribution(id, operatedBy);
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/{id}/start-async")
    @Operation(summary = "异步开始分发", description = "异步开始执行分发")
    public ResponseEntity<String> startDistributionAsync(
            @PathVariable String id,
            @RequestParam String operatedBy) {
        String taskId = distributionApplicationService.startDistributionAsync(id, operatedBy);
        return ResponseEntity.ok(taskId);
    }
    
    @GetMapping("/{id}/status")
    @Operation(summary = "获取分发状态", description = "获取分发的执行状态")
    public ResponseEntity<DistributionStatusDTO> getDistributionStatus(@PathVariable String id) {
        DistributionStatusDTO status = distributionApplicationService.getDistributionStatus(id);
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/{id}/retry")
    @Operation(summary = "重试分发", description = "重试失败的分发")
    public ResponseEntity<Boolean> retryDistribution(
            @PathVariable String id,
            @RequestParam String operatedBy) {
        boolean success = distributionApplicationService.retryDistribution(id, operatedBy);
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消分发", description = "取消正在执行的分发")
    public ResponseEntity<Boolean> cancelDistribution(
            @PathVariable String id,
            @RequestParam String operatedBy) {
        boolean success = distributionApplicationService.cancelDistribution(id, operatedBy);
        return ResponseEntity.ok(success);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分发", description = "删除指定分发")
    public ResponseEntity<Boolean> deleteDistribution(
            @PathVariable String id,
            @RequestParam String operatedBy) {
        boolean success = distributionApplicationService.deleteDistribution(id, operatedBy);
        return ResponseEntity.ok(success);
    }
    
    @GetMapping("/reports/{reportId}/history")
    @Operation(summary = "获取分发历史", description = "获取报表的分发历史记录")
    public ResponseEntity<List<DistributionDTO>> getDistributionHistory(@PathVariable String reportId) {
        List<DistributionDTO> history = distributionApplicationService.getDistributionHistory(reportId);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/channel-types")
    @Operation(summary = "获取渠道类型", description = "获取支持的分发渠道类型列表")
    public ResponseEntity<List<ChannelTypeDTO>> getChannelTypes() {
        List<ChannelTypeDTO> channelTypes = distributionApplicationService.getChannelTypes();
        return ResponseEntity.ok(channelTypes);
    }
}
