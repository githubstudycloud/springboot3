package com.framework.excel.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入结果
 *
 * @author framework
 * @since 1.0.0
 */
@Data
public class ImportResult {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 总行数
     */
    private Integer totalRows;

    /**
     * 成功行数
     */
    private Integer successRows;

    /**
     * 失败行数
     */
    private Integer failedRows;

    /**
     * 跳过行数
     */
    private Integer skippedRows;

    /**
     * 插入行数
     */
    private Integer insertedRows;

    /**
     * 更新行数
     */
    private Integer updatedRows;

    /**
     * 错误信息列表
     */
    private List<ImportError> errors;

    /**
     * 处理耗时（毫秒）
     */
    private Long processTime;

    /**
     * 详细消息
     */
    private String message;

    public ImportResult() {
        this.success = true;
        this.totalRows = 0;
        this.successRows = 0;
        this.failedRows = 0;
        this.skippedRows = 0;
        this.insertedRows = 0;
        this.updatedRows = 0;
        this.errors = new ArrayList<>();
        this.processTime = 0L;
    }

    /**
     * 添加错误信息
     *
     * @param error 错误信息
     */
    public void addError(ImportError error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(error);
        this.failedRows++;
        this.success = false;
    }

    /**
     * 添加错误信息
     *
     * @param row     行号
     * @param field   字段名
     * @param value   字段值
     * @param message 错误消息
     */
    public void addError(Integer row, String field, Object value, String message) {
        addError(new ImportError(row, field, value, message));
    }

    /**
     * 记录成功
     */
    public void addSuccess() {
        this.successRows++;
    }

    /**
     * 记录插入
     */
    public void addInsert() {
        this.insertedRows++;
        addSuccess();
    }

    /**
     * 记录更新
     */
    public void addUpdate() {
        this.updatedRows++;
        addSuccess();
    }

    /**
     * 记录跳过
     */
    public void addSkip() {
        this.skippedRows++;
    }

    /**
     * 计算成功率
     *
     * @return 成功率（百分比）
     */
    public Double getSuccessRate() {
        if (totalRows == null || totalRows == 0) {
            return 0.0;
        }
        return (double) successRows / totalRows * 100;
    }
}