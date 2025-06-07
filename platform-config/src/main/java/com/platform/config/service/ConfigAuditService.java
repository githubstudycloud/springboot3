package com.platform.config.service;

import com.platform.config.entity.ConfigAudit;
import com.platform.config.event.ConfigChangeEvent;
import com.platform.config.repository.ConfigAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 配置审计服务
 * 专门负责配置变更的审计日志管理
 *
 * @author Platform Team
 * @since 2.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigAuditService {

    private final ConfigAuditRepository auditRepository;

    /**
     * 记录配置变更审计日志
     *
     * @param application 应用名称
     * @param profile 环境配置
     * @param operation 操作类型
     * @param configKey 配置键
     * @param oldValue 旧值
     * @param newValue 新值
     * @param operator 操作人员
     * @return 保存的审计记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ConfigAudit recordAudit(String application, String profile, String operation,
                                 String configKey, String oldValue, String newValue,
                                 String operator) {
        return recordAudit(application, profile, operation, configKey, oldValue, newValue,
                          operator, null, null);
    }

    /**
     * 记录配置变更审计日志（完整参数）
     *
     * @param application 应用名称
     * @param profile 环境配置
     * @param operation 操作类型
     * @param configKey 配置键
     * @param oldValue 旧值
     * @param newValue 新值
     * @param operator 操作人员
     * @param versionId 关联版本ID
     * @param businessId 业务标识
     * @return 保存的审计记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ConfigAudit recordAudit(String application, String profile, String operation,
                                 String configKey, String oldValue, String newValue,
                                 String operator, Long versionId, String businessId) {
        // 获取请求信息
        String clientIp = null;
        String userAgent = null;
        String source = "API";

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                clientIp = getClientIp(request);
                userAgent = request.getHeader("User-Agent");
                source = determineSource(request);
            }
        } catch (Exception e) {
            log.debug("获取请求信息失败: {}", e.getMessage());
        }

        // 创建审计记录
        ConfigAudit audit = ConfigAudit.builder()
                .application(application)
                .profile(profile)
                .operation(operation)
                .configKey(configKey)
                .oldValue(oldValue)
                .newValue(newValue)
                .operator(operator)
                .source(source)
                .clientIp(clientIp)
                .userAgent(userAgent)
                .versionId(versionId)
                .businessId(businessId)
                .result("SUCCESS")
                .build();

        audit = auditRepository.save(audit);
        log.info("审计日志已记录，应用: {}, 操作: {}, 操作人: {}", application, operation, operator);

        return audit;
    }

    /**
     * 记录操作失败的审计日志
     *
     * @param application 应用名称
     * @param profile 环境配置
     * @param operation 操作类型
     * @param operator 操作人员
     * @param errorMessage 错误信息
     * @return 保存的审计记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ConfigAudit recordFailureAudit(String application, String profile, String operation,
                                        String operator, String errorMessage) {
        // 获取请求信息
        String clientIp = null;
        String userAgent = null;
        String source = "API";

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                clientIp = getClientIp(request);
                userAgent = request.getHeader("User-Agent");
                source = determineSource(request);
            }
        } catch (Exception e) {
            log.debug("获取请求信息失败: {}", e.getMessage());
        }

        ConfigAudit audit = ConfigAudit.builder()
                .application(application)
                .profile(profile)
                .operation(operation)
                .operator(operator)
                .source(source)
                .clientIp(clientIp)
                .userAgent(userAgent)
                .result("FAILED")
                .errorMessage(errorMessage)
                .build();

        audit = auditRepository.save(audit);
        log.warn("操作失败审计日志已记录，应用: {}, 操作: {}, 错误: {}", application, operation, errorMessage);

        return audit;
    }

    /**
     * 监听配置变更事件，自动记录审计日志
     */
    @EventListener
    @Async
    public void handleConfigChangeEvent(ConfigChangeEvent event) {
        try {
            recordAudit(
                event.getApplication(),
                event.getProfile(),
                event.getOperation(),
                event.getConfigKey(),
                event.getOldValue(),
                event.getNewValue(),
                event.getOperator(),
                event.getVersionId(),
                event.getBusinessId()
            );
        } catch (Exception e) {
            log.error("处理配置变更事件失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 根据应用和环境查询审计日志
     *
     * @param application 应用名称
     * @param profile 环境配置
     * @param pageable 分页参数
     * @return 审计日志分页数据
     */
    public Page<ConfigAudit> getAuditsByAppAndProfile(String application, String profile, Pageable pageable) {
        return auditRepository.findByApplicationAndProfileOrderByTimestampDesc(application, profile, pageable);
    }

    /**
     * 根据操作类型查询审计日志
     *
     * @param operation 操作类型
     * @param pageable 分页参数
     * @return 审计日志分页数据
     */
    public Page<ConfigAudit> getAuditsByOperation(String operation, Pageable pageable) {
        return auditRepository.findByOperationOrderByTimestampDesc(operation, pageable);
    }

    /**
     * 根据操作人员查询审计日志
     *
     * @param operator 操作人员
     * @param pageable 分页参数
     * @return 审计日志分页数据
     */
    public Page<ConfigAudit> getAuditsByOperator(String operator, Pageable pageable) {
        return auditRepository.findByOperatorOrderByTimestampDesc(operator, pageable);
    }

    /**
     * 根据时间范围查询审计日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    public List<ConfigAudit> getAuditsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return auditRepository.findAuditsByTimeRange(startTime, endTime);
    }

    /**
     * 查询失败的操作
     *
     * @param pageable 分页参数
     * @return 失败操作的审计日志
     */
    public Page<ConfigAudit> getFailedOperations(Pageable pageable) {
        return auditRepository.findByResultOrderByTimestampDesc("FAILED", pageable);
    }

    /**
     * 生成审计报告
     *
     * @param startDate 开始日期
     * @return 审计报告统计数据
     */
    public Map<String, Object> generateAuditReport(LocalDateTime startDate) {
        // 获取每日操作统计
        List<Object[]> dailyStats = auditRepository.getDailyOperationStats(startDate);
        
        // 获取操作类型统计
        List<Object[]> operationStats = auditRepository.getOperationTypeStats(startDate);
        
        // 计算总体统计
        long totalOperations = auditRepository.count();
        long failedOperations = auditRepository.countByOperation("FAILED");
        
        return Map.of(
            "dailyStats", dailyStats,
            "operationTypeStats", operationStats,
            "totalOperations", totalOperations,
            "failedOperations", failedOperations,
            "successRate", totalOperations > 0 ? (double)(totalOperations - failedOperations) / totalOperations * 100 : 0
        );
    }

    /**
     * 根据版本ID查询相关审计记录
     *
     * @param versionId 版本ID
     * @return 审计记录列表
     */
    public List<ConfigAudit> getAuditsByVersionId(Long versionId) {
        return auditRepository.findByVersionIdOrderByTimestampDesc(versionId);
    }

    /**
     * 根据业务标识查询审计记录
     *
     * @param businessId 业务标识
     * @return 审计记录列表
     */
    public List<ConfigAudit> getAuditsByBusinessId(String businessId) {
        return auditRepository.findByBusinessIdOrderByTimestampDesc(businessId);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        String xRealIp = request.getHeader("X-Real-IP");
        String remoteAddr = request.getRemoteAddr();

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return remoteAddr;
    }

    /**
     * 确定操作来源
     */
    private String determineSource(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String requestUri = request.getRequestURI();

        if (userAgent != null) {
            if (userAgent.contains("curl") || userAgent.contains("wget")) {
                return "CLI";
            }
            if (userAgent.contains("Java") || userAgent.contains("okhttp")) {
                return "API";
            }
            if (userAgent.contains("Mozilla") || userAgent.contains("Chrome")) {
                return "WEB";
            }
        }

        if (requestUri != null && requestUri.contains("/admin")) {
            return "ADMIN";
        }

        return "API";
    }
} 