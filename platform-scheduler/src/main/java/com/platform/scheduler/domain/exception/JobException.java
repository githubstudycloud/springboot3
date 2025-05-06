package com.platform.scheduler.domain.exception;

/**
 * 作业相关异常
 * 表示在作业领域模型中发生的异常
 *
 * @author platform
 */
public class JobException extends DomainException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 作业ID
     */
    private final String jobId;
    
    /**
     * 构造方法
     *
     * @param jobId 作业ID
     * @param message 异常消息
     */
    public JobException(String jobId, String message) {
        super("JOB_ERROR", message);
        this.jobId = jobId;
    }
    
    /**
     * 构造方法
     *
     * @param jobId 作业ID
     * @param errorCode 错误代码
     * @param message 异常消息
     */
    public JobException(String jobId, String errorCode, String message) {
        super(errorCode, message);
        this.jobId = jobId;
    }
    
    /**
     * 构造方法
     *
     * @param jobId 作业ID
     * @param errorCode 错误代码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public JobException(String jobId, String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.jobId = jobId;
    }
    
    /**
     * 获取作业ID
     *
     * @return 作业ID
     */
    public String getJobId() {
        return jobId;
    }
    
    /**
     * 作业未找到异常
     *
     * @param jobId 作业ID
     * @return 作业未找到异常
     */
    public static JobException notFound(String jobId) {
        return new JobException(jobId, "JOB_NOT_FOUND", "Job not found: " + jobId);
    }
    
    /**
     * 作业名称已存在异常
     *
     * @param jobName 作业名称
     * @return 作业名称已存在异常
     */
    public static JobException nameAlreadyExists(String jobName) {
        return new JobException(null, "JOB_NAME_ALREADY_EXISTS", "Job name already exists: " + jobName);
    }
    
    /**
     * 作业依赖循环异常
     *
     * @param jobId 作业ID
     * @param dependencyJobId 依赖作业ID
     * @return 作业依赖循环异常
     */
    public static JobException circularDependency(String jobId, String dependencyJobId) {
        return new JobException(jobId, "JOB_CIRCULAR_DEPENDENCY", 
                "Circular dependency detected between jobs: " + jobId + " and " + dependencyJobId);
    }
    
    /**
     * 作业状态无效异常
     *
     * @param jobId 作业ID
     * @param currentStatus 当前状态
     * @param expectedStatus 期望状态
     * @return 作业状态无效异常
     */
    public static JobException invalidStatus(String jobId, String currentStatus, String expectedStatus) {
        return new JobException(jobId, "JOB_INVALID_STATUS", 
                "Invalid job status: " + currentStatus + ", expected: " + expectedStatus);
    }
    
    /**
     * 作业调度策略无效异常
     *
     * @param jobId 作业ID
     * @param reason 原因
     * @return 作业调度策略无效异常
     */
    public static JobException invalidScheduleStrategy(String jobId, String reason) {
        return new JobException(jobId, "JOB_INVALID_SCHEDULE_STRATEGY", 
                "Invalid job schedule strategy for job " + jobId + ": " + reason);
    }
}
