package com.framework.excel.service;

// import com.alibaba.fastjson.JSON; // Not used directly, can be removed if not needed by business logic
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
// import org.springframework.stereotype.Service; // Will be on concrete implementations
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 抽象Excel服务基类
 * 包含Excel导入导出的核心流程和通用逻辑
 * 具体业务相关的处理由子类实现
 *
 * @author Framework Team
 * @since 1.0.0
 */
public abstract class AbstractExcelService implements ExcelService {

    @Autowired
    protected ExcelTemplateConfigMapper templateConfigMapper; // Changed to protected for potential subclass access

    @Autowired
    protected ExcelFieldConfigMapper fieldConfigMapper; // Changed to protected

    @Autowired
    protected DynamicMapper dynamicMapper; // Changed to protected

    protected ExcelTemplateGenerator templateGenerator; // Changed to protected
    protected ExcelDataReader dataReader; // Changed to protected
    protected ExcelDataWriter dataWriter; // Changed to protected
    protected DropdownResolver dropdownResolver; // Changed to protected

    public AbstractExcelService() {
        // Consider making these injectable if complex/stateful, or if subclasses need to override.
        // For now, direct instantiation is kept.
        this.dropdownResolver = new DropdownResolver();
        this.templateGenerator = new ExcelTemplateGenerator(dropdownResolver);
        this.dataReader = new ExcelDataReader();
        this.dataWriter = new ExcelDataWriter();
    }

    // --- Abstract methods to be implemented by business-specific subclasses ---

    /**
     * 转换和校验业务入参.
     * 子类可以根据具体业务需求对传入的 BusinessConditions 进行转换、校验或补充.
     *
     * @param templateKey 模板唯一标识
     * @param initialConditions 原始业务条件
     * @return 处理后的业务条件
     */
    protected abstract BusinessConditions transformAndValidateParameters(String templateKey, BusinessConditions initialConditions);

    /**
     * 获取业务数据.
     * 子类根据模板配置和业务条件查询并返回实际的业务数据列表.
     *
     * @param config Excel模板配置
     * @param conditions 业务条件
     * @return 业务数据列表
     */
    protected abstract List<Map<String, Object>> fetchBusinessData(ExcelTemplateConfig config, BusinessConditions conditions);

    /**
     * 获取用于导出的模板配置.
     * 子类可以根据 templateKey 和 conditions 动态构建或修改模板配置.
     * 通常, 这会涉及调用 getBaseTemplateConfig 来获取基础配置, 然后再进行定制.
     *
     * @param templateKey 模板唯一标识
     * @param conditions 业务条件
     * @return 完整的、用于导出的Excel模板配置
     */
    protected abstract ExcelTemplateConfig getExportConfiguration(String templateKey, BusinessConditions conditions);
    
    /**
     * 获取用于导入的模板配置.
     * 子类可以根据 templateKey 和 conditions 动态构建或修改模板配置.
     * 通常, 这会涉及调用 getBaseTemplateConfig 来获取基础配置, 然后再进行定制.
     *
     * @param templateKey 模板唯一标识
     * @param conditions 业务条件
     * @return 完整的、用于导入的Excel模板配置
     */
    protected abstract ExcelTemplateConfig getImportConfiguration(String templateKey, BusinessConditions conditions);


    /**
     * 获取用于生成模板的配置.
     * @param templateKey 模板唯一标识
     * @param conditions 业务条件
     * @return Excel模板配置
     */
    protected abstract ExcelTemplateConfig getTemplateGenerationConfiguration(String templateKey, BusinessConditions conditions);


    /**
     * 保存导入的业务数据.
     * 子类实现具体的业务数据持久化逻辑, 例如存入数据库.
     * 此方法需要处理事务、生成默认字段值等业务相关的保存操作.
     *
     * @param dataList 从Excel读取并转换后的数据列表
     * @param config Excel模板配置
     * @param conditions 业务条件 (可能包含保存时需要的额外参数)
     * @return 导入结果, 包含成功、失败信息
     */
    protected abstract ImportResult saveBusinessData(List<Map<String, Object>> dataList, ExcelTemplateConfig config, BusinessConditions conditions);

    // --- Core service methods implementing the ExcelService interface ---

    @Override
    public byte[] generateTemplate(String templateKey, BusinessConditions conditions) {
        BusinessConditions processedConditions = transformAndValidateParameters(templateKey, conditions);
        ExcelTemplateConfig config = getTemplateGenerationConfiguration(templateKey, processedConditions);

        // 解析下拉框数据 (common logic)
        if (processedConditions != null && processedConditions.getDropdownParams() != null) {
            dropdownResolver.resolveDropdowns(config, processedConditions.getDropdownParams(), dynamicMapper);
        } else {
            dropdownResolver.resolveDropdowns(config, null, dynamicMapper); // Still pass null if no params
        }
        
        return templateGenerator.generateTemplate(config, processedConditions);
    }

    @Override
    public ImportResult importData(String templateKey, MultipartFile file, BusinessConditions conditions) {
        BusinessConditions processedConditions = transformAndValidateParameters(templateKey, conditions);
        ExcelTemplateConfig config = getImportConfiguration(templateKey, processedConditions); // Use specific import config method
        
        // 读取Excel数据 (common logic)
        List<Map<String, Object>> dataList = dataReader.readExcel(file, config, processedConditions);
        
        // 执行数据保存 (delegated to subclass)
        return saveBusinessData(dataList, config, processedConditions);
    }

    @Override
    public byte[] exportData(String templateKey, BusinessConditions conditions) {
        BusinessConditions processedConditions = transformAndValidateParameters(templateKey, conditions);
        ExcelTemplateConfig config = getExportConfiguration(templateKey, processedConditions); // Use specific export config method
        
        // 查询数据 (delegated to subclass)
        List<Map<String, Object>> dataList = fetchBusinessData(config, processedConditions);
        
        // 生成Excel文件 (common logic)
        return dataWriter.writeExcel(dataList, config, processedConditions);
    }

    // --- Utility methods for subclasses (can be overridden if needed) ---

    /**
     * 获取基础的模板配置信息 (不含字段).
     * 通常由子类的 getExportConfiguration, getImportConfiguration, getTemplateGenerationConfiguration 方法调用.
     * @param templateKey 模板唯一标识
     * @return 基础模板配置
     */
    protected ExcelTemplateConfig getBaseTemplateConfigByKey(String templateKey) {
        ExcelTemplateConfig config = templateConfigMapper.selectByTemplateKey(templateKey);
        if (config == null) {
            throw new RuntimeException("模板配置不存在: " + templateKey);
        }
        if (!config.getEnabled()) {
            throw new RuntimeException("模板已禁用: " + templateKey);
        }
        // Fields are NOT loaded here; subclasses will load them as needed via getFieldsByTemplateId
        return config;
    }
    
    /**
     * 根据模板ID加载字段配置.
     * @param templateId 模板ID
     * @return 字段配置列表
     */
    protected List<com.framework.excel.entity.config.ExcelFieldConfig> getFieldsByTemplateId(Long templateId) {
        return fieldConfigMapper.selectByTemplateId(templateId);
    }

    /**
     * 旧的 getTemplateConfig, 保留以便参考, 新的实现应该使用 getExportConfiguration, getImportConfiguration 等.
     * 或者将其改造为 getBaseTemplateConfigWithFields.
     * For now, marked as deprecated and private. Subclasses should use getBaseTemplateConfigByKey and getFieldsByTemplateId.
     */
    @Deprecated
    private ExcelTemplateConfig getFullTemplateConfigLegacy(String templateKey) {
        ExcelTemplateConfig config = templateConfigMapper.selectByTemplateKey(templateKey);
        if (config == null) {
            throw new RuntimeException("模板配置不存在: " + templateKey);
        }
        if (!config.getEnabled()) {
            throw new RuntimeException("模板已禁用: " + templateKey);
        }
        config.setFields(fieldConfigMapper.selectByTemplateId(config.getId()));
        return config;
    }

    /**
     * 旧的 queryData 方法, 现在由 fetchBusinessData 抽象方法取代.
     * This logic should be moved to the concrete subclass implementing fetchBusinessData.
     * Kept for reference, marked as deprecated.
     */
    @Deprecated
    protected List<Map<String, Object>> queryDataLegacy(ExcelTemplateConfig config, BusinessConditions conditions) {
        Map<String, Object> queryConditions = conditions != null ? conditions.getConditions() : null;
        List<String> visibleFields = conditions != null ? conditions.getVisibleFields() : null;
        String orderBy = conditions != null ? conditions.getOrderBy() : null;
        Integer limit = conditions != null ? conditions.getLimit() : null;
        
        return dynamicMapper.selectByConditions(
            config.getTableName(), // This assumes data is always from a single table via dynamicMapper
            queryConditions,
            visibleFields,
            orderBy,
            limit
        );
    }
} 