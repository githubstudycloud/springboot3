package com.platform.report.infrastructure.service;

import com.platform.report.domain.model.distribution.ChannelType;
import com.platform.report.domain.model.distribution.DistributionChannel;
import com.platform.report.domain.model.distribution.ReportDistribution;
import com.platform.report.domain.model.report.ReportContent;
import com.platform.report.domain.repository.ReportDistributionRepository;
import com.platform.report.domain.repository.ReportRepository;
import com.platform.report.infrastructure.exception.DistributionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 报表分发服务
 * 用于管理不同渠道的报表分发
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DistributionService {
    
    private final ReportRepository reportRepository;
    private final ReportDistributionRepository distributionRepository;
    private final FileStorageService fileStorageService;
    private final EmailDistributionService emailService;
    
    /**
     * 立即分发报表
     */
    @Transactional
    public boolean distributeReport(String distributionId) {
        ReportDistribution distribution = distributionRepository.findById(distributionId)
                .orElseThrow(() -> new IllegalArgumentException("Distribution not found: " + distributionId));
        
        log.info("Starting distribution: {}", distributionId);
        distribution.markAsInProgress();
        distributionRepository.save(distribution);
        
        try {
            // 获取报表内容
            ReportContent reportContent = getReportContent(distribution.getReportId(), distribution.getReportVersionId());
            
            // 处理所有渠道
            boolean allSuccess = true;
            int successCount = 0;
            int failCount = 0;
            
            for (DistributionChannel channel : distribution.getChannels()) {
                try {
                    boolean success = distributeToChannel(channel, reportContent, distribution);
                    if (success) {
                        successCount++;
                    } else {
                        failCount++;
                        allSuccess = false;
                    }
                } catch (Exception e) {
                    log.error("Failed to distribute to channel {}: {}", channel.getId(), e.getMessage(), e);
                    failCount++;
                    allSuccess = false;
                }
            }
            
            // 更新分发状态
            if (allSuccess) {
                distribution.markAsCompleted();
            } else {
                // 部分失败也标记为完成，但记录失败次数
                distribution.markAsCompleted();
            }
            
            distributionRepository.save(distribution);
            log.info("Distribution completed: {}, success: {}, fail: {}", distributionId, successCount, failCount);
            
            return allSuccess;
        } catch (Exception e) {
            log.error("Distribution failed: {}", distributionId, e);
            distribution.markAsFailed(e.getMessage());
            distributionRepository.save(distribution);
            throw new DistributionException("Failed to distribute report: " + e.getMessage(), e);
        }
    }
    
    /**
     * 异步分发报表
     */
    @Async("reportDistributionExecutor")
    public CompletableFuture<Boolean> distributeReportAsync(String distributionId) {
        try {
            boolean result = distributeReport(distributionId);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Async distribution failed: {}", distributionId, e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    /**
     * 获取报表内容
     */
    private ReportContent getReportContent(String reportId, String versionId) {
        // 从存储中获取报表内容
        // 这里简化处理，实际应用需要实现
        return null;
    }
    
    /**
     * 分发到特定渠道
     */
    private boolean distributeToChannel(DistributionChannel channel, ReportContent content, 
                                        ReportDistribution distribution) {
        switch (channel.getType()) {
            case EMAIL:
                return distributeByEmail(channel, content, distribution);
            case SMS:
                return distributeBySms(channel, content, distribution);
            case FTP:
                return distributeByFtp(channel, content, distribution);
            case SHARED_FOLDER:
                return distributeToSharedFolder(channel, content, distribution);
            case API_CALLBACK:
                return distributeByApiCallback(channel, content, distribution);
            default:
                log.warn("Unsupported channel type: {}", channel.getType());
                return false;
        }
    }
    
    /**
     * 通过邮件分发
     */
    private boolean distributeByEmail(DistributionChannel channel, ReportContent content, 
                                      ReportDistribution distribution) {
        try {
            Map<String, Object> props = channel.getProperties();
            
            @SuppressWarnings("unchecked")
            List<String> to = (List<String>) props.getOrDefault("to", List.of());
            
            @SuppressWarnings("unchecked")
            List<String> cc = (List<String>) props.getOrDefault("cc", List.of());
            
            String subject = (String) props.getOrDefault("subject", 
                    "Report: " + (content != null ? content.getFileName() : ""));
            
            String body = (String) props.getOrDefault("body", 
                    "Please find attached report: " + (content != null ? content.getFileName() : ""));
            
            // 发送邮件
            return emailService.sendReportEmail(content, to, cc, subject, body, new HashMap<>());
        } catch (Exception e) {
            log.error("Failed to distribute by email", e);
            return false;
        }
    }
    
    /**
     * 通过短信分发
     * 这里只是示例，实际应用需要实现
     */
    private boolean distributeBySms(DistributionChannel channel, ReportContent content, 
                                   ReportDistribution distribution) {
        // 实现短信分发逻辑
        return false;
    }
    
    /**
     * 通过FTP分发
     * 这里只是示例，实际应用需要实现
     */
    private boolean distributeByFtp(DistributionChannel channel, ReportContent content, 
                                   ReportDistribution distribution) {
        // 实现FTP分发逻辑
        return false;
    }
    
    /**
     * 分发到共享文件夹
     * 这里只是示例，实际应用需要实现
     */
    private boolean distributeToSharedFolder(DistributionChannel channel, ReportContent content, 
                                            ReportDistribution distribution) {
        // 实现共享文件夹分发逻辑
        return false;
    }
    
    /**
     * 通过API回调分发
     * 这里只是示例，实际应用需要实现
     */
    private boolean distributeByApiCallback(DistributionChannel channel, ReportContent content, 
                                           ReportDistribution distribution) {
        // 实现API回调分发逻辑
        return false;
    }
}
