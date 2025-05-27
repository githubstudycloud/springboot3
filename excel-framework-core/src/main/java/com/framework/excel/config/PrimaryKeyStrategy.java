package com.framework.excel.config;

import com.framework.excel.enums.UpdateMode;
import lombok.Data;

import java.util.List;

/**
 * 主键策略配置
 *
 * @author framework
 * @since 1.0.0
 */
@Data
public class PrimaryKeyStrategy {

    /**
     * 主键字段列表（支持复合主键）
     */
    private List<String> keyFields;

    /**
     * 更新模式
     */
    private UpdateMode updateMode;

    /**
     * 是否忽略大小写（字符串主键时使用）
     */
    private Boolean ignoreCase;

    /**
     * 是否去除空白字符（字符串主键时使用）
     */
    private Boolean trim;

    public PrimaryKeyStrategy() {
        this.updateMode = UpdateMode.INSERT_OR_UPDATE;
        this.ignoreCase = false;
        this.trim = true;
    }

    public PrimaryKeyStrategy(List<String> keyFields, UpdateMode updateMode) {
        this.keyFields = keyFields;
        this.updateMode = updateMode;
        this.ignoreCase = false;
        this.trim = true;
    }
}