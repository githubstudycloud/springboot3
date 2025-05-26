package com.framework.excel.exception;

/**
 * Excel操作异常类
 *
 * @author Framework
 * @since 1.0.0
 */
public class ExcelException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 错误详情
     */
    private String details;
    
    public ExcelException(String message) {
        super(message);
        this.code = 500;
    }
    
    public ExcelException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }
    
    public ExcelException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public ExcelException(Integer code, String message, String details) {
        super(message);
        this.code = code;
        this.details = details;
    }
    
    public ExcelException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
}
