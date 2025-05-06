package com.platform.report.domain.model.distribution;

/**
 * 分发状态枚举
 */
public enum DistributionStatus {
    PENDING,     // 待分发
    IN_PROGRESS, // 分发中
    COMPLETED,   // 已完成
    FAILED       // 失败
}
