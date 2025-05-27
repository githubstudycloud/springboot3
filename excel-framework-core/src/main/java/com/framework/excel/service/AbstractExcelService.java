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
 * 具体业务相关的处理（包括配置获取、数据访问）由子类实现
 * 
 * 该抽象类不再直接依赖数据库配置，所有数据访问都通过抽象方法委托给子类
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

    protected ExcelTemplateGenerator templateGenerator;
    protected ExcelDataReader dataReader;
    protected ExcelDataWriter dataWriter;
    protected DropdownResolver dropdownResolver;

    public AbstractExcelService() {
        // 核心工具类的初始化保持在基类中
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
     * 可以从数据库、文件、API等任何数据源获取数据.
     *
     * @param config Excel模板配置
     * @param conditions 业务条件
     * @return 业务数据列表
     */
    protected abstract List<Map<String, Object>> fetchBusinessData(ExcelTemplateConfig config, BusinessConditions conditions);

    /**
     * 获取用于导出的模板配置.
     * 子类根据 templateKey 和 conditions 提供完整的模板配置.
     * 配置可以来自数据库、配置文件、硬编码常量等任何来源.
     *
     * @param templateKey 模板唯一标识
     * @param conditions 业务条件
     * @return 完整的、用于导出的Excel模板配置
     */
    protected abstract ExcelTemplateConfig getExportConfiguration(String templateKey, BusinessConditions conditions);
    
    /**
     * 获取用于导入的模板配置.
     * 子类根据 templateKey 和 conditions 提供完整的模板配置.
     * 配置可以来自数据库、配置文件、硬编码常量等任何来源.
     *
     * @param templateKey 模板唯一标识
     * @param conditions 业务条件
     * @return 完整的、用于导入的Excel模板配置
     */
    protected abstract ExcelTemplateConfig getImportConfiguration(String templateKey, BusinessConditions conditions);

    /**
     * 获取用于生成模板的配置.
     * 子类根据 templateKey 和 conditions 提供完整的模板配置.
     * 配置可以来自数据库、配置文件、硬编码常量等任何来源.
     * 
     * @param templateKey 模板唯一标识
     * @param conditions 业务条件
     * @return Excel模板配置
     */
    protected abstract ExcelTemplateConfig getTemplateGenerationConfiguration(String templateKey, BusinessConditions conditions);

    /**
     * 保存导入的业务数据.
     * 子类实现具体的业务数据持久化逻辑, 例如存入数据库、文件等.
     * 此方法需要处理事务、生成默认字段值等业务相关的保存操作.
     *
     * @param dataList 从Excel读取并转换后的数据列表
     * @param config Excel模板配置
     * @param conditions 业务条件 (可能包含保存时需要的额外参数)
     * @return 导入结果, 包含成功、失败信息
     */
    protected abstract ImportResult saveBusinessData(List<Map<String, Object>> dataList, ExcelTemplateConfig config, BusinessConditions conditions);

    /**
     * 获取动态Mapper用于下拉框数据解析.
     * 如果业务不需要下拉框功能，可以返回null.
     * 如果需要下拉框功能，子类需要提供相应的DynamicMapper实现.
     *
     * @return DynamicMapper实例，用于查询下拉框数据，可为null
     */
    protected abstract DynamicMapper getDynamicMapper();

    // --- Core service methods implementing the ExcelService interface ---

    @Override
    public byte[] generateTemplate(String templateKey, BusinessConditions conditions) {
        BusinessConditions processedConditions = transformAndValidateParameters(templateKey, conditions);
        ExcelTemplateConfig config = getTemplateGenerationConfiguration(templateKey, processedConditions);

        // 解析下拉框数据 (common logic)
        DynamicMapper dynamicMapper = getDynamicMapper();
        if (processedConditions != null && processedConditions.getDropdownParams() != null) {
            dropdownResolver.resolveDropdowns(config, processedConditions.getDropdownParams(), dynamicMapper);
        } else {
            dropdownResolver.resolveDropdowns(config, null, dynamicMapper);
        }
        
        return templateGenerator.generateTemplate(config, processedConditions);
    }

    @Override
    public ImportResult importData(String templateKey, MultipartFile file, BusinessConditions conditions) {
        BusinessConditions processedConditions = transformAndValidateParameters(templateKey, conditions);
        ExcelTemplateConfig config = getImportConfiguration(templateKey, processedConditions);
        
        // 读取Excel数据 (common logic)
        List<Map<String, Object>> dataList = dataReader.readExcel(file, config, processedConditions);
        
        // 执行数据保存 (delegated to subclass)
        return saveBusinessData(dataList, config, processedConditions);
    }

    @Override
    public byte[] exportData(String templateKey, BusinessConditions conditions) {
        BusinessConditions processedConditions = transformAndValidateParameters(templateKey, conditions);
        ExcelTemplateConfig config = getExportConfiguration(templateKey, processedConditions);
        
        // 查询数据 (delegated to subclass)
        List<Map<String, Object>> dataList = fetchBusinessData(config, processedConditions);
        
        // 生成Excel文件 (common logic)
        return dataWriter.writeExcel(dataList, config, processedConditions);
    }

    // --- Utility methods for subclasses ---

    /**
     * 获取Excel模板生成器，供子类在自定义逻辑中使用
     * @return ExcelTemplateGenerator实例
     */
    protected ExcelTemplateGenerator getTemplateGenerator() {
        return templateGenerator;
    }

    /**
     * 获取Excel数据读取器，供子类在自定义逻辑中使用
     * @return ExcelDataReader实例
     */
    protected ExcelDataReader getDataReader() {
        return dataReader;
    }

    /**
     * 获取Excel数据写入器，供子类在自定义逻辑中使用
     * @return ExcelDataWriter实例
     */
    protected ExcelDataWriter getDataWriter() {
        return dataWriter;
    }

    /**
     * 获取下拉框解析器，供子类在自定义逻辑中使用
     * @return DropdownResolver实例
     */
    protected DropdownResolver getDropdownResolver() {
        return dropdownResolver;
    }
} 