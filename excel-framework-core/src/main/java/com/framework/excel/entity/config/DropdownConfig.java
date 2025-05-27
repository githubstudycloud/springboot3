package com.framework.excel.entity.config;

import com.framework.excel.entity.dto.DropdownOption;
import lombok.Data;

import java.util.List;

/**
 * 下拉配置实体
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Data
public class DropdownConfig {
    private String type;              // RELATED_TABLE, STATIC
    private String tableName;         // 关联表名
    private String valueField;        // 值字段
    private String displayField;      // 显示字段
    private String whereClause;       // 查询条件
    private Boolean allowEmpty;       // 是否允许为空
    private List<DropdownOption> staticOptions; // 静态选项
}
