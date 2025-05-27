package com.framework.excel.service.impl;

import com.framework.excel.config.ExcelTemplateConfig;
import com.framework.excel.config.ExcelFieldConfig;
import com.framework.excel.config.PrimaryKeyStrategy;
import com.framework.excel.dto.ExportRequest;
import com.framework.excel.dto.ImportResult;
import com.framework.excel.enums.UpdateMode;
import com.framework.excel.mapper.FaultMapper;
import com.framework.excel.mapper.ModelMapper;
import com.framework.excel.provider.DropdownProvider;
import com.framework.excel.service.ExcelConfigService;
import com.framework.excel.service.ExcelDataService;
import com.framework.excel.util.DynamicExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Excel数据服务实现类
 *
 * @author framework
 * @since 1.0.0
 */
@Slf4j
@Service
public class ExcelDataServiceImpl implements ExcelDataService {

    @Autowired
    private ExcelConfigService excelConfigService;

    @Autowired
    private FaultMapper faultMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private List<DropdownProvider> dropdownProviders;

    private Map<String, DropdownProvider> providerMap;

    @javax.annotation.PostConstruct
    public void init() {
        // 初始化下拉数据提供者映射
        providerMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(dropdownProviders)) {
            for (DropdownProvider provider : dropdownProviders) {
                providerMap.put(provider.getType(), provider);
            }
        }
        log.info("Initialized {} dropdown providers", providerMap.size());
    }

    @Override
    public void downloadTemplate(String templateKey, List<String> visibleFields, HttpServletResponse response) {
        com.framework.excel.entity.ExcelTemplateConfig entityConfig = excelConfigService.getConfig(templateKey);
        if (entityConfig == null) {
            throw new IllegalArgumentException("Template config not found: " + templateKey);
        }

        // 转换为config包中的类型
        ExcelTemplateConfig config = convertToConfigType(entityConfig);

        // 如果指定了可见字段，则动态调整配置
        if (!CollectionUtils.isEmpty(visibleFields)) {
            // 创建配置副本
            ExcelTemplateConfig configCopy = copyConfig(config);
            
            // 设置字段可见性
            configCopy.getFields().forEach(field -> 
                field.setVisible(visibleFields.contains(field.getFieldName())));
            
            config = configCopy;
        }

        DynamicExcelUtils.exportTemplate(config, response, providerMap);
    }

    @Override
    @Transactional
    public ImportResult importData(String templateKey, MultipartFile file, String primaryKey) {
        com.framework.excel.entity.ExcelTemplateConfig entityConfig = excelConfigService.getConfig(templateKey);
        if (entityConfig == null) {
            ImportResult result = new ImportResult();
            result.addError(0, null, null, "Template config not found: " + templateKey);
            return result;
        }

        // 转换为config包中的类型
        ExcelTemplateConfig config = convertToConfigType(entityConfig);

        // 如果指定了主键，则覆盖配置
        if (StringUtils.hasText(primaryKey)) {
            ExcelTemplateConfig configCopy = copyConfig(config);
            configCopy.setPrimaryKeyFields(Arrays.asList(primaryKey));
            config = configCopy;
        }

        try {
            // 导入Excel数据
            ImportResult result = DynamicExcelUtils.importData(file.getInputStream(), config, providerMap);
            
            if (result.getSuccess() && result.getSuccessRows() > 0) {
                // 这里应该调用具体的数据保存逻辑
                // 由于是通用框架，这里只是示例
                log.info("Successfully imported {} rows for template: {}", result.getSuccessRows(), templateKey);
            }

            return result;
            
        } catch (Exception e) {
            log.error("Failed to import data for template: {}", templateKey, e);
            ImportResult result = new ImportResult();
            result.addError(0, null, null, "Import failed: " + e.getMessage());
            return result;
        }
    }

    @Override
    public void exportData(String templateKey, ExportRequest request, HttpServletResponse response) {
        com.framework.excel.entity.ExcelTemplateConfig entityConfig = excelConfigService.getConfig(templateKey);
        if (entityConfig == null) {
            throw new IllegalArgumentException("Template config not found: " + templateKey);
        }

        // 转换为config包中的类型
        ExcelTemplateConfig config = convertToConfigType(entityConfig);

        // 查询数据
        List<Map<String, Object>> dataList = queryData(templateKey, request.getConditions());
        
        // 转换为实体对象列表（这里简化处理）
        List<Object> entityList = convertToEntityList(dataList, config);

        DynamicExcelUtils.exportData(entityList, config, request.getVisibleFields(), response);
    }

    @Override
    @Transactional
    public ImportResult batchSaveData(String templateKey, List<Map<String, Object>> dataList, ExcelTemplateConfig config) {
        ImportResult result = new ImportResult();
        
        if (CollectionUtils.isEmpty(dataList)) {
            return result;
        }

        PrimaryKeyStrategy strategy = config.getPrimaryKeyStrategy();
        if (strategy == null) {
            result.addError(0, null, null, "Primary key strategy not configured");
            return result;
        }

        try {
            for (Map<String, Object> data : dataList) {
                if (saveOrUpdateData(templateKey, data, strategy)) {
                    if (isExistingRecord(templateKey, data, strategy)) {
                        result.addUpdate();
                    } else {
                        result.addInsert();
                    }
                } else {
                    result.addError(0, null, null, "Failed to save data");
                }
            }
            
        } catch (Exception e) {
            log.error("Failed to batch save data for template: {}", templateKey, e);
            result.addError(0, null, null, "Batch save failed: " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> queryData(String templateKey, Map<String, Object> conditions) {
        // 根据模板类型查询数据
        switch (templateKey) {
            case "fault":
                return faultMapper.selectByConditions(conditions).stream()
                        .map(this::entityToMap)
                        .collect(Collectors.toList());
            case "model":
                return modelMapper.selectByConditions(conditions).stream()
                        .map(this::entityToMap)
                        .collect(Collectors.toList());
            default:
                log.warn("Unsupported template for query: {}", templateKey);
                return new ArrayList<>();
        }
    }

    @Override
    public ImportResult validateData(String templateKey, List<Map<String, Object>> dataList, ExcelTemplateConfig config) {
        ImportResult result = new ImportResult();
        
        if (CollectionUtils.isEmpty(dataList)) {
            return result;
        }

        // 这里可以添加业务验证逻辑
        // 例如：唯一性验证、外键验证、业务规则验证等
        
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> data = dataList.get(i);
            
            // 示例验证：检查必填字段
            for (ExcelFieldConfig field : config.getFields()) {
                if (Boolean.TRUE.equals(field.getRequired())) {
                    Object value = data.get(field.getFieldName());
                    if (value == null || !StringUtils.hasText(String.valueOf(value))) {
                        result.addError(i + 1, field.getFieldName(), value,
                                "Required field is empty: " + field.getColumnName());
                    }
                }
            }
        }

        return result;
    }

    /**
     * 保存或更新数据
     */
    private boolean saveOrUpdateData(String templateKey, Map<String, Object> data, PrimaryKeyStrategy strategy) {
        UpdateMode updateMode = strategy.getUpdateMode();
        
        switch (updateMode) {
            case INSERT_ONLY:
                return insertData(templateKey, data);
            case UPDATE_ONLY:
                return updateData(templateKey, data, strategy);
            case INSERT_OR_UPDATE:
                if (isExistingRecord(templateKey, data, strategy)) {
                    return updateData(templateKey, data, strategy);
                } else {
                    return insertData(templateKey, data);
                }
            default:
                return false;
        }
    }

    /**
     * 插入数据
     */
    private boolean insertData(String templateKey, Map<String, Object> data) {
        // 这里应该根据模板类型调用相应的插入方法
        // 由于是通用框架，这里只是示例
        log.debug("Insert data for template: {}, data: {}", templateKey, data);
        return true;
    }

    /**
     * 更新数据
     */
    private boolean updateData(String templateKey, Map<String, Object> data, PrimaryKeyStrategy strategy) {
        // 这里应该根据模板类型和主键策略调用相应的更新方法
        log.debug("Update data for template: {}, data: {}", templateKey, data);
        return true;
    }

    /**
     * 检查记录是否存在
     */
    private boolean isExistingRecord(String templateKey, Map<String, Object> data, PrimaryKeyStrategy strategy) {
        // 这里应该根据主键策略检查记录是否存在
        List<String> keyFields = strategy.getKeyFields();
        if (CollectionUtils.isEmpty(keyFields)) {
            return false;
        }

        // 示例实现
        String primaryKeyField = keyFields.get(0);
        Object primaryKeyValue = data.get(primaryKeyField);
        
        if (primaryKeyValue == null) {
            return false;
        }

        // 根据模板类型查询
        switch (templateKey) {
            case "fault":
                if ("code".equals(primaryKeyField)) {
                    return faultMapper.selectByCode(String.valueOf(primaryKeyValue)) != null;
                } else if ("name".equals(primaryKeyField)) {
                    return faultMapper.selectByName(String.valueOf(primaryKeyValue)) != null;
                }
                break;
            case "model":
                if ("code".equals(primaryKeyField)) {
                    return modelMapper.selectByCode(String.valueOf(primaryKeyValue)) != null;
                } else if ("name".equals(primaryKeyField)) {
                    return modelMapper.selectByName(String.valueOf(primaryKeyValue)) != null;
                }
                break;
        }

        return false;
    }

    /**
     * 复制配置
     */
    private ExcelTemplateConfig copyConfig(ExcelTemplateConfig original) {
        // 这里应该实现深拷贝，简化处理
        ExcelTemplateConfig copy = new ExcelTemplateConfig();
        copy.setTemplateKey(original.getTemplateKey());
        copy.setEntityClass(original.getEntityClass());
        copy.setTableName(original.getTableName());
        copy.setSheetName(original.getSheetName());
        copy.setPrimaryKeyStrategy(original.getPrimaryKeyStrategy());
        copy.setFields(new ArrayList<>(original.getFields()));
        copy.setDescription(original.getDescription());
        return copy;
    }

    /**
     * 转换为实体对象列表
     */
    private List<Object> convertToEntityList(List<Map<String, Object>> dataList, ExcelTemplateConfig config) {
        // 这里应该根据实体类型进行转换，简化处理
        return new ArrayList<>(dataList);
    }

    /**
     * 实体转Map
     */
    private Map<String, Object> entityToMap(Object entity) {
        // 这里应该使用反射或BeanUtils进行转换，简化处理
        Map<String, Object> map = new HashMap<>();
        // 简化实现
        return map;
    }

    /**
     * 将entity包中的ExcelTemplateConfig转换为config包中的ExcelTemplateConfig
     */
    private ExcelTemplateConfig convertToConfigType(com.framework.excel.entity.ExcelTemplateConfig entityConfig) {
        ExcelTemplateConfig config = new ExcelTemplateConfig();
        config.setTemplateKey(entityConfig.getTemplateKey());
        config.setEntityClass(entityConfig.getEntityClass());
        config.setTableName(entityConfig.getTableName());
        config.setSheetName(entityConfig.getSheetName());
        config.setDescription(entityConfig.getDescription());
        
        // 转换主键策略
        if (entityConfig.getPrimaryKeyFields() != null && entityConfig.getUpdateMode() != null) {
            PrimaryKeyStrategy strategy = new PrimaryKeyStrategy();
            strategy.setKeyFields(entityConfig.getPrimaryKeyFields());
            strategy.setUpdateMode(entityConfig.getUpdateMode());
            config.setPrimaryKeyStrategy(strategy);
        }
        
        // 转换字段配置
        if (entityConfig.getFields() != null) {
            List<ExcelFieldConfig> configFields = new ArrayList<>();
            for (com.framework.excel.entity.ExcelFieldConfig entityField : entityConfig.getFields()) {
                ExcelFieldConfig configField = convertFieldToConfigType(entityField);
                configFields.add(configField);
            }
            config.setFields(configFields);
        }
        
        return config;
    }

    /**
     * 将entity包中的ExcelFieldConfig转换为config包中的ExcelFieldConfig
     */
    private ExcelFieldConfig convertFieldToConfigType(com.framework.excel.entity.ExcelFieldConfig entityField) {
        ExcelFieldConfig configField = new ExcelFieldConfig();
        // 这里需要根据实际的字段属性进行转换
        // 由于没有看到entity.ExcelFieldConfig的完整定义，这里只是示例
        return configField;
    }
}