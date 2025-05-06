package com.platform.monitor.interfaces.rest;

import com.platform.monitor.application.dto.DashboardDTO;
import com.platform.monitor.application.dto.DashboardPanelDTO;
import com.platform.monitor.application.dto.MetricQueryDTO;
import com.platform.monitor.application.service.DashboardAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 仪表板管理控制器
 */
@RestController
@RequestMapping("/api/v1/dashboards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "仪表板管理", description = "仪表板和面板管理相关接口")
public class DashboardController {
    
    private final DashboardAppService dashboardAppService;
    
    @PostMapping
    @Operation(summary = "创建仪表板", description = "创建新的仪表板")
    public ResponseEntity<DashboardDTO> createDashboard(
            @Parameter(description = "仪表板DTO", required = true) @RequestBody DashboardDTO dashboardDTO) {
        log.debug("创建仪表板: {}", dashboardDTO.getName());
        return ResponseEntity.ok(dashboardAppService.createDashboard(dashboardDTO));
    }
    
    @PutMapping
    @Operation(summary = "更新仪表板", description = "更新现有的仪表板")
    public ResponseEntity<DashboardDTO> updateDashboard(
            @Parameter(description = "仪表板DTO", required = true) @RequestBody DashboardDTO dashboardDTO) {
        log.debug("更新仪表板: {}", dashboardDTO.getId());
        return ResponseEntity.ok(dashboardAppService.updateDashboard(dashboardDTO));
    }
    
    @DeleteMapping("/{dashboardId}")
    @Operation(summary = "删除仪表板", description = "删除指定的仪表板")
    public ResponseEntity<Boolean> deleteDashboard(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId) {
        log.debug("删除仪表板: {}", dashboardId);
        return ResponseEntity.ok(dashboardAppService.deleteDashboard(dashboardId));
    }
    
    @GetMapping("/{dashboardId}")
    @Operation(summary = "获取仪表板", description = "获取指定仪表板的详情")
    public ResponseEntity<DashboardDTO> getDashboard(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId) {
        log.debug("获取仪表板: {}", dashboardId);
        return ResponseEntity.ok(dashboardAppService.getDashboard(dashboardId));
    }
    
    @GetMapping
    @Operation(summary = "获取所有仪表板", description = "获取系统中所有的仪表板")
    public ResponseEntity<List<DashboardDTO>> getAllDashboards() {
        log.debug("获取所有仪表板");
        return ResponseEntity.ok(dashboardAppService.getAllDashboards());
    }
    
    @GetMapping("/public")
    @Operation(summary = "获取公开仪表板", description = "获取系统中所有公开的仪表板")
    public ResponseEntity<List<DashboardDTO>> getPublicDashboards() {
        log.debug("获取公开仪表板");
        return ResponseEntity.ok(dashboardAppService.getPublicDashboards());
    }
    
    @GetMapping("/accessible/{userId}")
    @Operation(summary = "获取用户有权访问的仪表板", description = "获取指定用户有权访问的仪表板")
    public ResponseEntity<List<DashboardDTO>> getAccessibleDashboards(
            @Parameter(description = "用户ID", required = true) @PathVariable String userId) {
        log.debug("获取用户 [{}] 有权访问的仪表板", userId);
        return ResponseEntity.ok(dashboardAppService.getAccessibleDashboards(userId));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户创建的仪表板", description = "获取指定用户创建的仪表板")
    public ResponseEntity<List<DashboardDTO>> getUserDashboards(
            @Parameter(description = "用户ID", required = true) @PathVariable String userId) {
        log.debug("获取用户 [{}] 创建的仪表板", userId);
        return ResponseEntity.ok(dashboardAppService.getUserDashboards(userId));
    }
    
    @PostMapping("/{dashboardId}/panels")
    @Operation(summary = "创建面板", description = "为指定仪表板创建新的面板")
    public ResponseEntity<DashboardPanelDTO> createPanel(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId,
            @Parameter(description = "面板DTO", required = true) @RequestBody DashboardPanelDTO panelDTO) {
        log.debug("为仪表板 [{}] 创建面板: {}", dashboardId, panelDTO.getTitle());
        return ResponseEntity.ok(dashboardAppService.createPanel(dashboardId, panelDTO));
    }
    
    @PutMapping("/{dashboardId}/panels")
    @Operation(summary = "更新面板", description = "为指定仪表板更新面板")
    public ResponseEntity<DashboardPanelDTO> updatePanel(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId,
            @Parameter(description = "面板DTO", required = true) @RequestBody DashboardPanelDTO panelDTO) {
        log.debug("为仪表板 [{}] 更新面板: {}", dashboardId, panelDTO.getId());
        return ResponseEntity.ok(dashboardAppService.updatePanel(dashboardId, panelDTO));
    }
    
    @DeleteMapping("/{dashboardId}/panels/{panelId}")
    @Operation(summary = "删除面板", description = "删除指定仪表板的指定面板")
    public ResponseEntity<Boolean> deletePanel(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId,
            @Parameter(description = "面板ID", required = true) @PathVariable String panelId) {
        log.debug("删除仪表板 [{}] 的面板: {}", dashboardId, panelId);
        return ResponseEntity.ok(dashboardAppService.deletePanel(dashboardId, panelId));
    }
    
    @GetMapping("/{dashboardId}/panels/{panelId}")
    @Operation(summary = "获取面板", description = "获取指定仪表板的指定面板")
    public ResponseEntity<DashboardPanelDTO> getPanel(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId,
            @Parameter(description = "面板ID", required = true) @PathVariable String panelId) {
        log.debug("获取仪表板 [{}] 的面板: {}", dashboardId, panelId);
        return ResponseEntity.ok(dashboardAppService.getPanel(dashboardId, panelId));
    }
    
    @GetMapping("/{dashboardId}/panels")
    @Operation(summary = "获取仪表板的所有面板", description = "获取指定仪表板的所有面板")
    public ResponseEntity<List<DashboardPanelDTO>> getDashboardPanels(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId) {
        log.debug("获取仪表板 [{}] 的所有面板", dashboardId);
        return ResponseEntity.ok(dashboardAppService.getDashboardPanels(dashboardId));
    }
    
    @PostMapping("/{dashboardId}/panels/{panelId}/queries")
    @Operation(summary = "创建指标查询", description = "为指定面板创建指标查询")
    public ResponseEntity<MetricQueryDTO> createMetricQuery(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId,
            @Parameter(description = "面板ID", required = true) @PathVariable String panelId,
            @Parameter(description = "指标查询DTO", required = true) @RequestBody MetricQueryDTO queryDTO) {
        log.debug("为仪表板 [{}] 的面板 [{}] 创建指标查询", dashboardId, panelId);
        return ResponseEntity.ok(dashboardAppService.createMetricQuery(dashboardId, panelId, queryDTO));
    }
    
    @PutMapping("/{dashboardId}/panels/{panelId}/queries")
    @Operation(summary = "更新指标查询", description = "为指定面板更新指标查询")
    public ResponseEntity<MetricQueryDTO> updateMetricQuery(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId,
            @Parameter(description = "面板ID", required = true) @PathVariable String panelId,
            @Parameter(description = "指标查询DTO", required = true) @RequestBody MetricQueryDTO queryDTO) {
        log.debug("为仪表板 [{}] 的面板 [{}] 更新指标查询: {}", dashboardId, panelId, queryDTO.getId());
        return ResponseEntity.ok(dashboardAppService.updateMetricQuery(dashboardId, panelId, queryDTO));
    }
    
    @DeleteMapping("/{dashboardId}/panels/{panelId}/queries/{queryId}")
    @Operation(summary = "删除指标查询", description = "删除指定面板的指标查询")
    public ResponseEntity<Boolean> deleteMetricQuery(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId,
            @Parameter(description = "面板ID", required = true) @PathVariable String panelId,
            @Parameter(description = "查询ID", required = true) @PathVariable String queryId) {
        log.debug("删除仪表板 [{}] 的面板 [{}] 的指标查询: {}", dashboardId, panelId, queryId);
        return ResponseEntity.ok(dashboardAppService.deleteMetricQuery(dashboardId, panelId, queryId));
    }
    
    @PutMapping("/{dashboardId}/layout")
    @Operation(summary = "更新仪表板布局", description = "更新指定仪表板的布局")
    public ResponseEntity<DashboardDTO> updateDashboardLayout(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId,
            @Parameter(description = "布局信息（JSON格式）", required = true) @RequestBody String layout) {
        log.debug("更新仪表板 [{}] 的布局", dashboardId);
        return ResponseEntity.ok(dashboardAppService.updateDashboardLayout(dashboardId, layout));
    }
    
    @PostMapping("/{dashboardId}/share/{userId}")
    @Operation(summary = "分享仪表板", description = "向指定用户分享仪表板")
    public ResponseEntity<DashboardDTO> shareDashboard(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId,
            @Parameter(description = "用户ID", required = true) @PathVariable String userId) {
        log.debug("将仪表板 [{}] 分享给用户 [{}]", dashboardId, userId);
        return ResponseEntity.ok(dashboardAppService.shareDashboard(dashboardId, userId));
    }
    
    @DeleteMapping("/{dashboardId}/share/{userId}")
    @Operation(summary = "取消分享仪表板", description = "取消向指定用户分享仪表板")
    public ResponseEntity<DashboardDTO> unshareDashboard(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId,
            @Parameter(description = "用户ID", required = true) @PathVariable String userId) {
        log.debug("取消将仪表板 [{}] 分享给用户 [{}]", dashboardId, userId);
        return ResponseEntity.ok(dashboardAppService.unshareDashboard(dashboardId, userId));
    }
    
    @PutMapping("/{dashboardId}/public/{isPublic}")
    @Operation(summary = "设置仪表板公开状态", description = "设置指定仪表板的公开状态")
    public ResponseEntity<DashboardDTO> setDashboardPublic(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId,
            @Parameter(description = "是否公开", required = true) @PathVariable boolean isPublic) {
        log.debug("设置仪表板 [{}] 的公开状态为: {}", dashboardId, isPublic);
        return ResponseEntity.ok(dashboardAppService.setDashboardPublic(dashboardId, isPublic));
    }
    
    @PostMapping("/{dashboardId}/copy")
    @Operation(summary = "复制仪表板", description = "复制指定仪表板")
    public ResponseEntity<DashboardDTO> copyDashboard(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId,
            @Parameter(description = "新仪表板名称", required = true) @RequestParam String newName,
            @Parameter(description = "创建人", required = true) @RequestParam String createdBy) {
        log.debug("复制仪表板 [{}] 为新仪表板 [{}]", dashboardId, newName);
        return ResponseEntity.ok(dashboardAppService.copyDashboard(dashboardId, newName, createdBy));
    }
    
    @GetMapping("/{dashboardId}/panels/{panelId}/data")
    @Operation(summary = "获取面板数据", description = "获取指定面板的数据")
    public ResponseEntity<Map<String, Object>> getPanelData(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId,
            @Parameter(description = "面板ID", required = true) @PathVariable String panelId) {
        log.debug("获取仪表板 [{}] 的面板 [{}] 的数据", dashboardId, panelId);
        return ResponseEntity.ok(dashboardAppService.getPanelData(dashboardId, panelId));
    }
    
    @GetMapping("/{dashboardId}/data")
    @Operation(summary = "获取仪表板所有面板数据", description = "获取指定仪表板所有面板的数据")
    public ResponseEntity<Map<String, Map<String, Object>>> getDashboardData(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId) {
        log.debug("获取仪表板 [{}] 的所有面板数据", dashboardId);
        return ResponseEntity.ok(dashboardAppService.getDashboardData(dashboardId));
    }
    
    @GetMapping("/panel-types")
    @Operation(summary = "获取面板类型列表", description = "获取系统支持的所有面板类型")
    public ResponseEntity<List<Map<String, String>>> getPanelTypes() {
        log.debug("获取面板类型列表");
        return ResponseEntity.ok(dashboardAppService.getPanelTypes());
    }
    
    @GetMapping("/{dashboardId}/export")
    @Operation(summary = "导出仪表板", description = "以JSON格式导出指定仪表板")
    public ResponseEntity<String> exportDashboard(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId) {
        log.debug("导出仪表板: {}", dashboardId);
        return ResponseEntity.ok(dashboardAppService.exportDashboard(dashboardId));
    }
    
    @PostMapping("/import")
    @Operation(summary = "导入仪表板", description = "从JSON导入仪表板")
    public ResponseEntity<DashboardDTO> importDashboard(
            @Parameter(description = "仪表板JSON", required = true) @RequestBody String dashboardJson,
            @Parameter(description = "创建人", required = true) @RequestParam String createdBy) {
        log.debug("导入仪表板，创建人: {}", createdBy);
        return ResponseEntity.ok(dashboardAppService.importDashboard(dashboardJson, createdBy));
    }
    
    @GetMapping("/default")
    @Operation(summary = "获取系统默认仪表板", description = "获取系统预设的默认仪表板")
    public ResponseEntity<DashboardDTO> getDefaultDashboard() {
        log.debug("获取系统默认仪表板");
        return ResponseEntity.ok(dashboardAppService.getDefaultDashboard());
    }
    
    @PostMapping("/default")
    @Operation(summary = "创建系统默认仪表板", description = "创建系统预设的默认仪表板")
    public ResponseEntity<DashboardDTO> createDefaultDashboard(
            @Parameter(description = "创建人", required = true) @RequestParam String createdBy) {
        log.debug("创建系统默认仪表板，创建人: {}", createdBy);
        return ResponseEntity.ok(dashboardAppService.createDefaultDashboard(createdBy));
    }
    
    @GetMapping("/service/{serviceName}")
    @Operation(summary = "获取服务监控仪表板", description = "获取指定服务的监控仪表板")
    public ResponseEntity<DashboardDTO> getServiceDashboard(
            @Parameter(description = "服务名称", required = true) @PathVariable String serviceName) {
        log.debug("获取服务 [{}] 的监控仪表板", serviceName);
        return ResponseEntity.ok(dashboardAppService.getServiceDashboard(serviceName));
    }
    
    @PostMapping("/service/{serviceName}")
    @Operation(summary = "创建服务监控仪表板", description = "为指定服务创建监控仪表板")
    public ResponseEntity<DashboardDTO> createServiceDashboard(
            @Parameter(description = "服务名称", required = true) @PathVariable String serviceName,
            @Parameter(description = "创建人", required = true) @RequestParam String createdBy) {
        log.debug("为服务 [{}] 创建监控仪表板，创建人: {}", serviceName, createdBy);
        return ResponseEntity.ok(dashboardAppService.createServiceDashboard(serviceName, createdBy));
    }
    
    @PostMapping("/{dashboardId}/panels/{panelId}/refresh")
    @Operation(summary = "刷新面板数据", description = "刷新指定面板的数据")
    public ResponseEntity<Map<String, Object>> refreshPanelData(
            @Parameter(description = "仪表板ID", required = true) @PathVariable String dashboardId,
            @Parameter(description = "面板ID", required = true) @PathVariable String panelId) {
        log.debug("刷新仪表板 [{}] 的面板 [{}] 的数据", dashboardId, panelId);
        return ResponseEntity.ok(dashboardAppService.refreshPanelData(dashboardId, panelId));
    }
}
