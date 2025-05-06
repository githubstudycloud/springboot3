package com.platform.report.domain.model.report;

import lombok.Getter;

/**
 * 报表内容
 * 存储报表的二进制内容和元数据
 */
@Getter
public class ReportContent {
    
    private final byte[] content;
    private final String contentType;
    private final long size;
    private final String fileName;
    
    public ReportContent(byte[] content, String contentType, long size, String fileName) {
        this.content = content;
        this.contentType = contentType;
        this.size = size;
        this.fileName = fileName;
    }
}
