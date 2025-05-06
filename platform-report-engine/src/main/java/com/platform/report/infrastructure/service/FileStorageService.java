package com.platform.report.infrastructure.service;

import com.platform.report.domain.model.report.ReportContent;
import com.platform.report.infrastructure.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件存储服务
 * 用于存储报表生成的内容
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
    
    @Value("${report.storage.path:./report-storage}")
    private String storageRootPath;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    
    /**
     * 存储报表内容
     *
     * @param content 报表内容
     * @param reportId 报表ID
     * @return 存储路径
     */
    public String storeReportContent(ReportContent content, String reportId) {
        try {
            // 创建基于日期的目录结构
            String dateDir = LocalDateTime.now().format(DATE_FORMATTER);
            String fileName = generateFileName(content.getFileName());
            
            Path directoryPath = Paths.get(storageRootPath, "reports", reportId, dateDir);
            Files.createDirectories(directoryPath);
            
            Path targetPath = directoryPath.resolve(fileName);
            Files.copy(Files.newInputStream(Paths.get(content.getFileName())), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            return targetPath.toString();
        } catch (IOException e) {
            log.error("Failed to store report content", e);
            throw new FileStorageException("Failed to store report content: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检索报表内容
     *
     * @param storagePath 存储路径
     * @return 报表内容
     */
    public ReportContent retrieveReportContent(String storagePath) {
        try {
            Path path = Paths.get(storagePath);
            byte[] content = Files.readAllBytes(path);
            String fileName = path.getFileName().toString();
            String contentType = determineContentType(fileName);
            
            return new ReportContent(content, contentType, content.length, fileName);
        } catch (IOException e) {
            log.error("Failed to retrieve report content", e);
            throw new FileStorageException("Failed to retrieve report content: " + e.getMessage(), e);
        }
    }
    
    /**
     * 删除报表内容
     *
     * @param storagePath 存储路径
     * @return 是否成功
     */
    public boolean deleteReportContent(String storagePath) {
        try {
            Path path = Paths.get(storagePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Failed to delete report content", e);
            throw new FileStorageException("Failed to delete report content: " + e.getMessage(), e);
        }
    }
    
    /**
     * 生成唯一文件名
     */
    private String generateFileName(String originalFileName) {
        String extension = StringUtils.getFilenameExtension(originalFileName);
        return UUID.randomUUID().toString() + (extension != null ? "." + extension : "");
    }
    
    /**
     * 根据文件名确定内容类型
     */
    private String determineContentType(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        if (extension == null) {
            return "application/octet-stream";
        }
        
        switch (extension.toLowerCase()) {
            case "pdf":
                return "application/pdf";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "xls":
                return "application/vnd.ms-excel";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "doc":
                return "application/msword";
            case "html":
                return "text/html";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            default:
                return "application/octet-stream";
        }
    }
}
