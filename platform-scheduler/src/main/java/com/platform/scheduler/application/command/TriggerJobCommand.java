package com.platform.scheduler.application.command;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 触发作业立即执行命令
 * 
 * @author platform
 */
@Data
public class TriggerJobCommand {
    
    /**
     * 作业ID
     */
    private String jobId;
    
    /**
     * 执行参数（可选，用于覆盖作业默认参数）
     */
    private Map<String, String> parameters = new HashMap<>();
    
    /**
     * 触发原因
     */
    private String reason;
    
    /**
     * 触发人
     */
    private String triggeredBy;
}
