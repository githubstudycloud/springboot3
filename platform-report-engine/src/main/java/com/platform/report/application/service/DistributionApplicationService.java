package com.platform.report.application.service;

import com.platform.report.application.dto.distribution.*;

import java.util.List;

/**
 * 报表分发应用服务接口
 * 提供报表分发相关功能的应用层服务
 */
public interface DistributionApplicationService {
    
    /**
     * 创建报表分发
     *
     * @param command 创建命令
     * @return 分发ID
     */
    String createDistribution(CreateDistributionCommand command);
    
    /**
     * 获取分发详情
     *
     * @param id 分发ID
     * @return 分发详情
     */
    DistributionDetailDTO getDistributionDetail(String id);
    
    /**
     * 获取分发列表
     *
     * @param query 查询参数
     * @return 分发列表
     */
    List<DistributionDTO> getDistributionList(DistributionQueryDTO query);
    
    /**
     * 添加分发渠道
     *
     * @param command 添加命令
     * @return 渠道ID
     */
    String addDistributionChannel(AddChannelCommand command);
    
    /**
     * 更新分发渠道
     *
     * @param command 更新命令
     * @return 是否成功
     */
    boolean updateDistributionChannel(UpdateChannelCommand command);
    
    /**
     * 删除分发渠道
     *
     * @param distributionId 分发ID
     * @param channelId 渠道ID
     * @param operatedBy 操作者
     * @return 是否成功
     */
    boolean deleteDistributionChannel(String distributionId, String channelId, String operatedBy);
    
    /**
     * 开始分发
     *
     * @param distributionId 分发ID
     * @param operatedBy 操作者
     * @return 是否成功
     */
    boolean startDistribution(String distributionId, String operatedBy);
    
    /**
     * 异步分发
     *
     * @param distributionId 分发ID
     * @param operatedBy 操作者
     * @return 任务ID
     */
    String startDistributionAsync(String distributionId, String operatedBy);
    
    /**
     * 获取分发状态
     *
     * @param distributionId 分发ID
     * @return 分发状态
     */
    DistributionStatusDTO getDistributionStatus(String distributionId);
    
    /**
     * 重试失败的分发
     *
     * @param distributionId 分发ID
     * @param operatedBy 操作者
     * @return 是否成功
     */
    boolean retryDistribution(String distributionId, String operatedBy);
    
    /**
     * 取消分发
     *
     * @param distributionId 分发ID
     * @param operatedBy 操作者
     * @return 是否成功
     */
    boolean cancelDistribution(String distributionId, String operatedBy);
    
    /**
     * 删除分发
     *
     * @param distributionId 分发ID
     * @param operatedBy 操作者
     * @return 是否成功
     */
    boolean deleteDistribution(String distributionId, String operatedBy);
    
    /**
     * 获取分发历史
     *
     * @param reportId 报表ID
     * @return 分发历史列表
     */
    List<DistributionDTO> getDistributionHistory(String reportId);
    
    /**
     * 获取分发渠道类型
     *
     * @return 渠道类型列表
     */
    List<ChannelTypeDTO> getChannelTypes();
}
