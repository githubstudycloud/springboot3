package com.framework.excel.service;

import com.framework.excel.config.ExcelTemplateConfig;
import com.framework.excel.dto.ExportRequest;
import com.framework.excel.dto.ImportResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Excel数据服务接口
 *
 * @author framework
 * @since 1.0.0
 */
public interface ExcelDataService {

    /**
     * 下载Excel模板
     *
     * @param templateKey   模板标识
     * @param visibleFields 可见字段列表
     * @param response      HTTP响应
     */
    void downloadTemplate(String templateKey, List<String> visibleFields, HttpServletResponse response);

    /**
     * 导入Excel数据
     *
     * @param templateKey 模板标识
     * @param file        Excel文件
     * @param primaryKey  主键字段（可选，用于覆盖配置）
     * @return 导入结果
     */
    ImportResult importData(String templateKey, MultipartFile file, String primaryKey);

    /**
     * 导出Excel数据
     *
     * @param templateKey 模板标识
     * @param request     导出请求
     * @param response    HTTP响应
     */
    void exportData(String templateKey, ExportRequest request, HttpServletResponse response);

    /**
     * 批量保存数据
     *
     * @param templateKey 模板标识
     * @param dataList    数据列表
     * @param config      模板配置
     * @return 保存结果
     */
    ImportResult batchSaveData(String templateKey, List<Map<String, Object>> dataList, ExcelTemplateConfig config);

    /**
     * 根据条件查询数据
     *
     * @param templateKey 模板标识
     * @param conditions  查询条件
     * @return 数据列表
     */
    List<Map<String, Object>> queryData(String templateKey, Map<String, Object> conditions);

    /**
     * 验证数据
     *
     * @param templateKey 模板标识
     * @param dataList    数据列表
     * @param config      模板配置
     * @return 验证结果
     */
    ImportResult validateData(String templateKey, List<Map<String, Object>> dataList, ExcelTemplateConfig config);
}