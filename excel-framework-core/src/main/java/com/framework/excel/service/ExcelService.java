package com.framework.excel.service;

import com.alibaba.fastjson.JSON;
import com.framework.excel.entity.config.ExcelTemplateConfig;
import com.framework.excel.entity.dto.BusinessConditions;
import com.framework.excel.entity.dto.ImportResult;
import com.framework.excel.mapper.ExcelTemplateConfigMapper;
import com.framework.excel.mapper.ExcelFieldConfigMapper;
import com.framework.excel.mapper.DynamicMapper;
import com.framework.excel.util.excel.ExcelTemplateGenerator;
import com.framework.excel.util.excel.ExcelDataReader;
import com.framework.excel.util.excel.ExcelDataWriter;
import com.framework.excel.util.excel.DropdownResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 统一Excel服务
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Service
public class ExcelService {
    
    @Autowired
    private ExcelTemplateConfigMapper templateConfigMapper;
    
    @Autowired
    private ExcelFieldConfigMapper fieldConfigMapper;
    
    @Autowired
    private DynamicMapper dynamicMapper;
    
    private ExcelTemplateGenerator templateGenerator;
    private ExcelDataReader dataReader;
    private ExcelDataWriter dataWriter;
    private DropdownResolver dropdownResolver;
    
    public ExcelService() {
        this.dropdownResolver = new DropdownResolver();
        this.templateGenerator = new ExcelTemplateGenerator(dropdownResolver);
        this.dataReader = new ExcelDataReader();
        this.dataWriter = new ExcelDataWriter();
    }
    
    /**
     * 生成Excel模板
     */
    public byte[] generateTemplate(String templateKey, BusinessConditions conditions) {
        // 获取模板配置
        ExcelTemplateConfig config = getTemplateConfig(templateKey);
        
        // 加载字段配置
        config.setFields(fieldConfigMapper.selectByTemplateId(config.getId()));
        
        // 解析下拉框数据
        if (conditions != null && conditions.getDropdownParams() != null) {
            dropdownResolver.resolveDropdowns(config, conditions.getDropdownParams(), dynamicMapper);
        } else {
            dropdownResolver.resolveDropdowns(config, null, dynamicMapper);
        }
        
        // 处理可见字段过滤
        if (conditions != null && conditions.getVisibleFields() != null) {
            return templateGenerator.generateTemplate(config, conditions);
        }
        
        return templateGenerator.generateTemplate(config, conditions);
    }
    
    /**
     * 导入Excel数据
     */
    public ImportResult importData(String templateKey, MultipartFile file, BusinessConditions conditions) {
        // 获取模板配置
        ExcelTemplateConfig config = getTemplateConfig(templateKey);
        
        // 加载字段配置
        config.setFields(fieldConfigMapper.selectByTemplateId(config.getId()));
        
        // 读取Excel数据
        List<Map<String, Object>> dataList = dataReader.readExcel(file, config, conditions);
        
        // 执行数据导入
        return dataReader.importData(dataList, config, conditions, dynamicMapper);
    }
    
    /**
     * 导出Excel数据
     */
    public byte[] exportData(String templateKey, BusinessConditions conditions) {
        // 获取模板配置
        ExcelTemplateConfig config = getTemplateConfig(templateKey);
        
        // 加载字段配置
        config.setFields(fieldConfigMapper.selectByTemplateId(config.getId()));
        
        // 查询数据
        List<Map<String, Object>> dataList = queryData(config, conditions);
        
        // 生成Excel文件
        return dataWriter.writeExcel(dataList, config, conditions);
    }
    
    /**
     * 获取模板配置
     */
    private ExcelTemplateConfig getTemplateConfig(String templateKey) {
        ExcelTemplateConfig config = templateConfigMapper.selectByTemplateKey(templateKey);
        if (config == null) {
            throw new RuntimeException("模板配置不存在: " + templateKey);
        }
        if (!config.getEnabled()) {
            throw new RuntimeException("模板已禁用: " + templateKey);
        }
        return config;
    }
    
    /**
     * 查询数据
     */
    private List<Map<String, Object>> queryData(ExcelTemplateConfig config, BusinessConditions conditions) {
        Map<String, Object> queryConditions = conditions != null ? conditions.getConditions() : null;
        List<String> visibleFields = conditions != null ? conditions.getVisibleFields() : null;
        String orderBy = conditions != null ? conditions.getOrderBy() : null;
        Integer limit = conditions != null ? conditions.getLimit() : null;
        
        return dynamicMapper.selectByConditions(
            config.getTableName(),
            queryConditions,
            visibleFields,
            orderBy,
            limit
        );
    }
} 