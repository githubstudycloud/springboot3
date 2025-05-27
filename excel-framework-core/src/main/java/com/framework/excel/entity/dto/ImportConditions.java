package com.framework.excel.entity.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 导入条件DTO
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Data
public class ImportConditions {
    private Boolean validateOnly = false;       // 是否仅验证不导入
    private Boolean skipDuplicates = false;     // 是否跳过重复数据
    private Integer batchSize = 100;            // 批处理大小
    private Map<String, Object> defaultValues = new HashMap<>();  // 默认值设置
    private Map<String, Object> businessParams = new HashMap<>(); // 业务参数
}
