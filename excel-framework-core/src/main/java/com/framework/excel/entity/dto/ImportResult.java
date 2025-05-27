package com.framework.excel.entity.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入结果DTO
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Data
public class ImportResult {
    private Integer totalRows = 0;
    private Integer successCount = 0;
    private Integer errorCount = 0;
    private Integer skippedCount = 0;
    private List<ErrorInfo> errors = new ArrayList<>();
    private List<WarningInfo> warnings = new ArrayList<>();
    
    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public void setSkippedCount(Integer skippedCount) {
        this.skippedCount = skippedCount;
    }
    
    /**
     * 增加成功计数
     */
    public void incrementSuccessCount() {
        this.successCount++;
    }
    
    /**
     * 增加跳过计数
     */
    public void incrementSkippedCount() {
        this.skippedCount++;
    }
    
    /**
     * 添加错误信息
     */
    public void addError(int row, String message) {
        this.errors.add(new ErrorInfo(row, message));
        this.errorCount++;
    }
    
    /**
     * 添加警告信息
     */
    public void addWarning(int row, String message) {
        this.warnings.add(new WarningInfo(row, message));
    }
    
    /**
     * 错误信息
     */
    @Data
    public static class ErrorInfo {
        private Integer row;
        private String message;

        public ErrorInfo(Integer row, String message) {
            this.row = row;
            this.message = message;
        }
    }
    
    /**
     * 警告信息
     */
    @Data
    public static class WarningInfo {
        private Integer row;
        private String message;

        public WarningInfo(Integer row, String message) {
            this.row = row;
            this.message = message;
        }
    }
}
