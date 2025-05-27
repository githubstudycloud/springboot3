package com.framework.excel.service;

import com.framework.excel.entity.dto.BusinessConditions;
import com.framework.excel.entity.dto.ImportResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * 统一Excel服务接口
 * 
 * @author Framework Team
 * @since 1.0.0
 */
public interface ExcelService {
    
    /**
     * 生成Excel模板
     * @param templateKey 模板唯一标识
     * @param conditions 业务条件
     * @return Excel模板文件的字节数组
     */
    byte[] generateTemplate(String templateKey, BusinessConditions conditions);
    
    /**
     * 导入Excel数据
     * @param templateKey 模板唯一标识
     * @param file Excel文件
     * @param conditions 业务条件
     * @return 导入结果
     */
    ImportResult importData(String templateKey, MultipartFile file, BusinessConditions conditions);
    
    /**
     * 导出Excel数据
     * @param templateKey 模板唯一标识
     * @param conditions 业务条件
     * @return Excel文件的字节数组
     */
    byte[] exportData(String templateKey, BusinessConditions conditions);
} 