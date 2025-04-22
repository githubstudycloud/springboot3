package com.platform.scheduler.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.scheduler.model.TaskLog;
import com.platform.scheduler.repository.LogQueryParam;
import com.platform.scheduler.repository.LogRepository;
import com.platform.scheduler.service.LogService;
import com.platform.scheduler.util.JsonUtils;

/**
 * 日志服务实现类
 * 
 * @author platform
 */
@Service
public class LogServiceImpl implements LogService {
    
    private static final Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);
    
    @Autowired
    private LogRepository logRepository;
    
    @Value("${scheduler.log.export.path:./export/logs}")
    private String logExportPath;
    
    @Override
    @Transactional
    public Long logInfo(TaskLog log) {
        return saveLog(log, "INFO");
    }
    
    @Override
    @Transactional
    public Long logWarning(TaskLog log) {
        return saveLog(log, "WARNING");
    }
    
    @Override
    @Transactional
    public Long logError(TaskLog log) {
        return saveLog(log, "ERROR");
    }
    
    /**
     * 保存日志
     * 
     * @param log 日志对象
     * @param level 日志级别
     * @return 日志ID
     */
    private Long saveLog(TaskLog log, String level) {
        log.setLevel(level);
        
        if (log.getCreatedTime() == null) {
            log.setCreatedTime(new Date());
        }
        
        TaskLog savedLog = logRepository.save(log);
        logger.debug("日志已保存: {}, 级别: {}", savedLog.getId(), level);
        
        return savedLog.getId();
    }
    
    @Override
    public Page<TaskLog> findLogsByTaskId(Long taskId, Pageable pageable) {
        return logRepository.findByTaskId(taskId, pageable);
    }
    
    @Override
    public Page<TaskLog> findLogsByExecutionId(Long executionId, Pageable pageable) {
        return logRepository.findByExecutionId(executionId, pageable);
    }
    
    @Override
    public Page<TaskLog> findLogsByLevel(String level, Pageable pageable) {
        return logRepository.findByLevel(level, pageable);
    }
    
    @Override
    public Page<TaskLog> findLogs(LogQueryParam queryParam, Pageable pageable) {
        return logRepository.findByQueryParam(queryParam, pageable);
    }
    
    @Override
    @Transactional
    public int cleanupLogs(int retentionDays) {
        // 计算保留日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -retentionDays);
        Date retentionDate = calendar.getTime();
        
        // 删除过期日志
        int count = logRepository.deleteByCreatedTimeBefore(retentionDate);
        logger.info("已清理过期日志: {} 条, 保留 {} 天的日志", count, retentionDays);
        
        return count;
    }
    
    @Override
    public String exportLogs(LogQueryParam queryParam) {
        try {
            // 创建导出目录
            File exportDir = new File(logExportPath);
            if (!exportDir.exists()) {
                if (!exportDir.mkdirs()) {
                    throw new IOException("无法创建导出目录: " + logExportPath);
                }
            }
            
            // 生成文件名
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = String.format("logs_%s.json", dateFormat.format(new Date()));
            File exportFile = new File(exportDir, fileName);
            
            // 分页查询并写入文件
            try (FileWriter writer = new FileWriter(exportFile)) {
                int pageSize = 1000;
                int pageNum = 0;
                Page<TaskLog> page;
                
                writer.write("[\n");
                boolean isFirst = true;
                
                do {
                    // 分页查询
                    Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNum++, pageSize);
                    page = logRepository.findByQueryParam(queryParam, pageable);
                    
                    // 写入数据
                    for (TaskLog log : page.getContent()) {
                        if (!isFirst) {
                            writer.write(",\n");
                        }
                        writer.write(JsonUtils.toJsonString(log));
                        isFirst = false;
                    }
                    
                } while (page.hasNext());
                
                writer.write("\n]");
            }
            
            logger.info("日志导出成功: {}", exportFile.getAbsolutePath());
            return exportFile.getAbsolutePath();
            
        } catch (IOException e) {
            logger.error("日志导出失败", e);
            throw new RuntimeException("日志导出失败: " + e.getMessage(), e);
        }
    }
}
