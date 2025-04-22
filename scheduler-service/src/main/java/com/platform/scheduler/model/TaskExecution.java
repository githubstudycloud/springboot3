package com.platform.scheduler.model;

import java.util.Date;

import lombok.Data;

/**
 * 任务执行记录实体类
 * 
 * @author platform
 */
@Data
public class TaskExecution {
    
    /**
     * 执行ID
     */
    private Long id;
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 执行节点ID
     */
    private String nodeId;
    
    /**
     * 触发类型(SCHEDULED/MANUAL)
     */
    private String triggerType;
    
    /**
     * 开始时间
     */
    private Date startTime;
    
    /**
     * 结束时间
     */
    private Date endTime;
    
    /**
     * 执行状态(RUNNING/SUCCESS/FAILED/TIMEOUT/TERMINATED)
     */
    private String status;
    
    /**
     * 执行结果
     */
    private String result;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 获取执行持续时间（毫秒）
     * 
     * @return 执行持续时间
     */
    public Long getDuration() {
        if (startTime == null) {
            return null;
        }
        if (endTime == null) {
            return System.currentTimeMillis() - startTime.getTime();
        }
        return endTime.getTime() - startTime.getTime();
    }
    
    /**
     * 判断执行是否正在运行
     */
    public boolean isRunning() {
        return "RUNNING".equals(status);
    }
    
    /**
     * 判断执行是否成功
     */
    public boolean isSuccess() {
        return "SUCCESS".equals(status);
    }
    
    /**
     * 判断执行是否失败
     */
    public boolean isFailed() {
        return "FAILED".equals(status);
    }
    
    /**
     * 判断执行是否超时
     */
    public boolean isTimeout() {
        return "TIMEOUT".equals(status);
    }
    
    /**
     * 判断执行是否被终止
     */
    public boolean isTerminated() {
        return "TERMINATED".equals(status);
    }
}
