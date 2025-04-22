package com.platform.scheduler.model;

import java.util.Date;

import lombok.Data;

/**
 * 任务进度实体类
 * 
 * @author platform
 */
@Data
public class TaskProgress {
    
    /**
     * 进度ID
     */
    private Long id;
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 执行ID
     */
    private Long executionId;
    
    /**
     * 进度百分比(0-100)
     */
    private Integer progress;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 更新时间
     */
    private Date updatedTime;
    
    /**
     * 判断是否完成
     */
    public boolean isCompleted() {
        return progress != null && progress >= 100;
    }
}
