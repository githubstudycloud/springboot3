package com.example.platform.collect.core.domain.model;

/**
 * 错误处理策略枚举
 * 定义任务执行过程中发生错误时的处理策略
 */
public enum ErrorHandlingStrategy {
    /**
     * 快速失败
     * 遇到任何错误立即停止任务执行
     */
    FAIL_FAST("快速失败", "遇到任何错误立即停止任务执行"),
    
    /**
     * 继续执行
     * 遇到错误时记录并继续执行下一条记录
     */
    CONTINUE("继续执行", "遇到错误时记录并继续执行下一条记录"),
    
    /**
     * 重试后失败
     * 遇到错误时尝试重试，重试失败后终止任务
     */
    RETRY_THEN_FAIL("重试后失败", "遇到错误时尝试重试，重试失败后终止任务"),
    
    /**
     * 重试后继续
     * 遇到错误时尝试重试，重试失败后跳过并继续执行
     */
    RETRY_THEN_CONTINUE("重试后继续", "遇到错误时尝试重试，重试失败后跳过并继续执行"),
    
    /**
     * 降级执行
     * 遇到错误时尝试使用备选方案或上次成功的数据
     */
    FALLBACK("降级执行", "遇到错误时尝试使用备选方案或上次成功的数据");
    
    private final String displayName;
    private final String description;
    
    ErrorHandlingStrategy(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * 获取错误处理策略显示名称
     *
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取错误处理策略描述
     *
     * @return 描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 判断策略是否需要重试
     *
     * @return 是否需要重试
     */
    public boolean shouldRetry() {
        return this == RETRY_THEN_FAIL || this == RETRY_THEN_CONTINUE;
    }
    
    /**
     * 判断重试失败后是否继续执行
     *
     * @return 是否继续执行
     */
    public boolean shouldContinueAfterRetry() {
        return this == CONTINUE || this == RETRY_THEN_CONTINUE || this == FALLBACK;
    }
}
