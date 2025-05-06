package com.platform.report.domain.service;

import com.platform.report.domain.model.distribution.DistributionChannel;
import com.platform.report.domain.model.distribution.ReportDistribution;

import java.util.List;
import java.util.Map;

/**
 * 报表分发服务接口
 * 定义报表分发的领域服务方法
 */
public interface ReportDistributionService {
    
    /**
     * 创建新的报表分发
     *
     * @param name 分发名称
     * @param reportId 报表ID
     * @param reportVersionId 报表版本ID，可选
     * @param createdBy 创建者
     * @param comment 分发说明
     * @return 创建的报表分发
     */
    ReportDistribution createDistribution(String name, String reportId, String reportVersionId,
                                       String createdBy, String comment);
    
    /**
     * 添加分发渠道
     *
     * @param distributionId 分发ID
     * @param channelType 渠道类型
     * @param properties 渠道属性
     * @return 更新的报表分发
     */
    ReportDistribution addChannel(String distributionId, String channelType, Map<String, Object> properties);
    
    /**
     * 移除分发渠道
     *
     * @param distributionId 分发ID
     * @param channelId 渠道ID
     * @return 更新的报表分发
     */
    ReportDistribution removeChannel(String distributionId, String channelId);
    
    /**
     * 开始分发
     *
     * @param distributionId 分发ID
     * @return 更新的报表分发
     */
    ReportDistribution startDistribution(String distributionId);
    
    /**
     * 异步分发
     *
     * @param distributionId 分发ID
     * @return 更新的报表分发
     */
    ReportDistribution startDistributionAsync(String distributionId);
    
    /**
     * 获取分发状态
     *
     * @param distributionId 分发ID
     * @return 分发状态
     */
    String getDistributionStatus(String distributionId);
    
    /**
     * 重试失败的分发
     *
     * @param distributionId 分发ID
     * @return 更新的报表分发
     */
    ReportDistribution retryDistribution(String distributionId);
    
    /**
     * 取消分发
     *
     * @param distributionId 分发ID
     * @return 更新的报表分发
     */
    ReportDistribution cancelDistribution(String distributionId);
    
    /**
     * 获取分发历史
     *
     * @param reportId 报表ID
     * @return 分发历史列表
     */
    List<ReportDistribution> getDistributionHistory(String reportId);
    
    /**
     * 获取分发通道
     *
     * @param distributionId 分发ID
     * @return 分发通道列表
     */
    List<DistributionChannel> getDistributionChannels(String distributionId);
    
    /**
     * 获取分发结果
     *
     * @param distributionId 分发ID
     * @return 分发结果
     */
    Map<String, Object> getDistributionResult(String distributionId);
}
