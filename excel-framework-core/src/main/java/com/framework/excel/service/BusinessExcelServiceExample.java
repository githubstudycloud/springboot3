package com.framework.excel.service;

import com.framework.excel.entity.config.ExcelFieldConfig;
import com.framework.excel.entity.config.ExcelTemplateConfig;
import com.framework.excel.entity.dto.BusinessConditions;
import com.framework.excel.entity.dto.ImportResult;
import com.framework.excel.mapper.DynamicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 业务Excel服务示例实现类
 * 展示如何继承AbstractExcelService并实现具体的业务逻辑
 * 
 * 此示例不依赖数据库配置，而是通过常量和业务方法提供配置
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Service("businessExcelService")
public class BusinessExcelServiceExample extends AbstractExcelService {

    // 如果需要数据库访问，可以注入具体的业务Mapper
    // @Autowired
    // private BusinessDataMapper businessDataMapper;
    
    // 如果需要下拉框功能，可以注入DynamicMapper
    @Autowired(required = false)
    private DynamicMapper dynamicMapper;

    // ==================== 模板配置常量 ====================
    
    /**
     * 用户模板配置常量
     */
    private static final String USER_TEMPLATE_KEY = "USER_TEMPLATE";
    private static final ExcelTemplateConfig USER_TEMPLATE_CONFIG = createUserTemplateConfig();
    
    /**
     * 产品模板配置常量
     */
    private static final String PRODUCT_TEMPLATE_KEY = "PRODUCT_TEMPLATE";
    private static final ExcelTemplateConfig PRODUCT_TEMPLATE_CONFIG = createProductTemplateConfig();

    // ==================== 抽象方法实现 ====================

    @Override
    protected BusinessConditions transformAndValidateParameters(String templateKey, BusinessConditions initialConditions) {
        // 参数转换和校验的业务逻辑
        if (initialConditions == null) {
            initialConditions = new BusinessConditions();
        }
        
        // 根据模板类型进行特定的参数转换
        switch (templateKey) {
            case USER_TEMPLATE_KEY:
                return transformUserTemplateParameters(initialConditions);
            case PRODUCT_TEMPLATE_KEY:
                return transformProductTemplateParameters(initialConditions);
            default:
                throw new IllegalArgumentException("不支持的模板类型: " + templateKey);
        }
    }

    @Override
    protected List<Map<String, Object>> fetchBusinessData(ExcelTemplateConfig config, BusinessConditions conditions) {
        // 根据模板类型获取不同的业务数据
        switch (config.getTemplateKey()) {
            case USER_TEMPLATE_KEY:
                return fetchUserData(conditions);
            case PRODUCT_TEMPLATE_KEY:
                return fetchProductData(conditions);
            default:
                throw new IllegalArgumentException("不支持的模板类型: " + config.getTemplateKey());
        }
    }

    @Override
    protected ExcelTemplateConfig getExportConfiguration(String templateKey, BusinessConditions conditions) {
        return getTemplateConfigByKey(templateKey);
    }

    @Override
    protected ExcelTemplateConfig getImportConfiguration(String templateKey, BusinessConditions conditions) {
        return getTemplateConfigByKey(templateKey);
    }

    @Override
    protected ExcelTemplateConfig getTemplateGenerationConfiguration(String templateKey, BusinessConditions conditions) {
        return getTemplateConfigByKey(templateKey);
    }

    @Override
    protected ImportResult saveBusinessData(List<Map<String, Object>> dataList, ExcelTemplateConfig config, BusinessConditions conditions) {
        // 根据模板类型保存不同的业务数据
        switch (config.getTemplateKey()) {
            case USER_TEMPLATE_KEY:
                return saveUserData(dataList, conditions);
            case PRODUCT_TEMPLATE_KEY:
                return saveProductData(dataList, conditions);
            default:
                throw new IllegalArgumentException("不支持的模板类型: " + config.getTemplateKey());
        }
    }

    @Override
    protected DynamicMapper getDynamicMapper() {
        // 如果不需要下拉框功能，返回null
        // 如果需要，返回注入的dynamicMapper
        return dynamicMapper;
    }

    // ==================== 私有业务方法 ====================

    /**
     * 根据模板key获取配置
     */
    private ExcelTemplateConfig getTemplateConfigByKey(String templateKey) {
        switch (templateKey) {
            case USER_TEMPLATE_KEY:
                return USER_TEMPLATE_CONFIG;
            case PRODUCT_TEMPLATE_KEY:
                return PRODUCT_TEMPLATE_CONFIG;
            default:
                throw new IllegalArgumentException("不支持的模板类型: " + templateKey);
        }
    }

    /**
     * 转换用户模板参数
     */
    private BusinessConditions transformUserTemplateParameters(BusinessConditions conditions) {
        // 这里可以添加用户模板特有的参数转换逻辑
        // 例如：设置默认的查询条件、权限校验等
        return conditions;
    }

    /**
     * 转换产品模板参数
     */
    private BusinessConditions transformProductTemplateParameters(BusinessConditions conditions) {
        // 这里可以添加产品模板特有的参数转换逻辑
        return conditions;
    }

    /**
     * 获取用户数据
     */
    private List<Map<String, Object>> fetchUserData(BusinessConditions conditions) {
        // 这里实现获取用户数据的业务逻辑
        // 可以从数据库、API、文件等任何数据源获取
        List<Map<String, Object>> userList = new ArrayList<>();
        
        // 示例数据
        Map<String, Object> user1 = new HashMap<>();
        user1.put("id", 1L);
        user1.put("username", "admin");
        user1.put("realName", "管理员");
        user1.put("email", "admin@example.com");
        user1.put("createTime", LocalDateTime.now());
        userList.add(user1);
        
        Map<String, Object> user2 = new HashMap<>();
        user2.put("id", 2L);
        user2.put("username", "user001");
        user2.put("realName", "普通用户");
        user2.put("email", "user001@example.com");
        user2.put("createTime", LocalDateTime.now());
        userList.add(user2);
        
        return userList;
    }

    /**
     * 获取产品数据
     */
    private List<Map<String, Object>> fetchProductData(BusinessConditions conditions) {
        // 这里实现获取产品数据的业务逻辑
        List<Map<String, Object>> productList = new ArrayList<>();
        
        // 示例数据
        Map<String, Object> product1 = new HashMap<>();
        product1.put("id", 1L);
        product1.put("productName", "智能手机");
        product1.put("productCode", "SP001");
        product1.put("price", 2999.00);
        product1.put("category", "电子产品");
        productList.add(product1);
        
        return productList;
    }

    /**
     * 保存用户数据
     */
    private ImportResult saveUserData(List<Map<String, Object>> dataList, BusinessConditions conditions) {
        ImportResult result = new ImportResult();
        result.setTotalCount(dataList.size());
        
        int successCount = 0;
        int errorCount = 0;
        
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> userData = dataList.get(i);
            try {
                // 生成默认字段值
                userData.put("id", generateId());
                userData.put("createTime", LocalDateTime.now());
                userData.put("createBy", getCurrentUserId(conditions));
                userData.put("status", 1); // 默认状态为启用
                
                // 这里实现具体的保存逻辑
                // businessDataMapper.insertUser(userData);
                // 示例中只是打印
                System.out.println("保存用户数据: " + userData);
                
                successCount++;
            } catch (Exception e) {
                errorCount++;
                ImportResult.ErrorInfo error = new ImportResult.ErrorInfo(i + 1, e.getMessage());
                result.getErrors().add(error);
            }
        }
        
        result.setSuccessCount(successCount);
        result.setErrorCount(errorCount);
        return result;
    }

    /**
     * 保存产品数据
     */
    private ImportResult saveProductData(List<Map<String, Object>> dataList, BusinessConditions conditions) {
        ImportResult result = new ImportResult();
        result.setTotalCount(dataList.size());
        
        int successCount = 0;
        int errorCount = 0;
        
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> productData = dataList.get(i);
            try {
                // 生成默认字段值
                productData.put("id", generateId());
                productData.put("createTime", LocalDateTime.now());
                productData.put("createBy", getCurrentUserId(conditions));
                productData.put("status", 1);
                
                // 这里实现具体的保存逻辑
                // businessDataMapper.insertProduct(productData);
                System.out.println("保存产品数据: " + productData);
                
                successCount++;
            } catch (Exception e) {
                errorCount++;
                ImportResult.ErrorInfo error = new ImportResult.ErrorInfo(i + 1, e.getMessage());
                result.getErrors().add(error);
            }
        }
        
        result.setSuccessCount(successCount);
        result.setErrorCount(errorCount);
        return result;
    }

    // ==================== 工具方法 ====================

    /**
     * 生成ID（示例实现）
     */
    private Long generateId() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前用户ID（示例实现）
     */
    private Long getCurrentUserId(BusinessConditions conditions) {
        // 可以从SecurityContext、session或conditions中获取
        return 1L; // 示例返回固定值
    }

    // ==================== 模板配置创建方法 ====================

    /**
     * 创建用户模板配置
     */
    private static ExcelTemplateConfig createUserTemplateConfig() {
        ExcelTemplateConfig config = new ExcelTemplateConfig();
        config.setId(1L);
        config.setTemplateKey(USER_TEMPLATE_KEY);
        config.setTemplateName("用户信息模板");
        config.setSheetName("用户信息");
        config.setTableName("sys_user"); // 如果使用数据库
        config.setEnabled(true);
        
        // 创建字段配置
        List<ExcelFieldConfig> fields = new ArrayList<>();
        
        ExcelFieldConfig field1 = new ExcelFieldConfig();
        field1.setFieldName("username");
        field1.setFieldLabel("用户名");
        field1.setFieldType("STRING");
        field1.setColumnIndex(0);
        field1.setRequired(true);
        fields.add(field1);
        
        ExcelFieldConfig field2 = new ExcelFieldConfig();
        field2.setFieldName("realName");
        field2.setFieldLabel("真实姓名");
        field2.setFieldType("STRING");
        field2.setColumnIndex(1);
        field2.setRequired(true);
        fields.add(field2);
        
        ExcelFieldConfig field3 = new ExcelFieldConfig();
        field3.setFieldName("email");
        field3.setFieldLabel("邮箱");
        field3.setFieldType("STRING");
        field3.setColumnIndex(2);
        field3.setRequired(false);
        fields.add(field3);
        
        config.setFields(fields);
        return config;
    }

    /**
     * 创建产品模板配置
     */
    private static ExcelTemplateConfig createProductTemplateConfig() {
        ExcelTemplateConfig config = new ExcelTemplateConfig();
        config.setId(2L);
        config.setTemplateKey(PRODUCT_TEMPLATE_KEY);
        config.setTemplateName("产品信息模板");
        config.setSheetName("产品信息");
        config.setTableName("sys_product");
        config.setEnabled(true);
        
        List<ExcelFieldConfig> fields = new ArrayList<>();
        
        ExcelFieldConfig field1 = new ExcelFieldConfig();
        field1.setFieldName("productName");
        field1.setFieldLabel("产品名称");
        field1.setFieldType("STRING");
        field1.setColumnIndex(0);
        field1.setRequired(true);
        fields.add(field1);
        
        ExcelFieldConfig field2 = new ExcelFieldConfig();
        field2.setFieldName("productCode");
        field2.setFieldLabel("产品编码");
        field2.setFieldType("STRING");
        field2.setColumnIndex(1);
        field2.setRequired(true);
        fields.add(field2);
        
        ExcelFieldConfig field3 = new ExcelFieldConfig();
        field3.setFieldName("price");
        field3.setFieldLabel("价格");
        field3.setFieldType("DECIMAL");
        field3.setColumnIndex(2);
        field3.setRequired(true);
        fields.add(field3);
        
        ExcelFieldConfig field4 = new ExcelFieldConfig();
        field4.setFieldName("category");
        field4.setFieldLabel("分类");
        field4.setFieldType("STRING");
        field4.setColumnIndex(3);
        field4.setRequired(false);
        fields.add(field4);
        
        config.setFields(fields);
        return config;
    }
} 