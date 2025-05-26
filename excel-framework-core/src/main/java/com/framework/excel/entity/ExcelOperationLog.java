package com.framework.excel.entity;

import java.util.Date;

/**
 * Excel操作日志实体类
 *
 * @author Framework
 * @since 1.0.0
 */
public class ExcelOperationLog extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 日志ID
     */
    private Long id;
    
    /**
     * 操作类型：IMPORT-导入，EXPORT-导出
     */
    private String operationType;
    
    /**
     * 模板Key
     */
    private String templateKey;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 总记录数
     */
    private Integer totalCount;
    
    /**
     * 成功记录数
     */
    private Integer successCount;
    
    /**
     * 失败记录数
     */
    private Integer failCount;
    
    /**
     * 操作结果：SUCCESS-成功，PARTIAL_SUCCESS-部分成功，FAILED-失败
     */
    private String result;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 操作人
     */
    private String operator;
    
    /**
     * 操作时间
     */
    private Date operateTime;
    
    /**
     * 执行时长（毫秒）
     */
    private Long duration;
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    
    public String getTemplateKey() {
        return templateKey;
    }
    
    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public Integer getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
    
    public Integer getSuccessCount() {
        return successCount;
    }
    
    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }
    
    public Integer getFailCount() {
        return failCount;
    }
    
    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public Date getOperateTime() {
        return operateTime;
    }
    
    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
    
    public Long getDuration() {
        return duration;
    }
    
    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
