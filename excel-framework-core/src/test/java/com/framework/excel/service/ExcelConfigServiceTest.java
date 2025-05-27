package com.framework.excel.service;

import com.framework.excel.config.ExcelTemplateConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Excel配置服务测试
 *
 * @author framework
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ExcelConfigServiceTest {

    @Autowired
    private ExcelConfigService excelConfigService;

    @Test
    public void testGetConfig() {
        // 测试获取故障模板配置
        ExcelTemplateConfig faultConfig = excelConfigService.getConfig("fault");
        assertNotNull("Fault config should not be null", faultConfig);
        assertEquals("fault", faultConfig.getTemplateKey());
        assertEquals("故障数据", faultConfig.getSheetName());
        assertNotNull("Fields should not be null", faultConfig.getFields());
        assertTrue("Should have fields", faultConfig.getFields().size() > 0);

        // 测试获取模型模板配置
        ExcelTemplateConfig modelConfig = excelConfigService.getConfig("model");
        assertNotNull("Model config should not be null", modelConfig);
        assertEquals("model", modelConfig.getTemplateKey());
        assertEquals("模型数据", modelConfig.getSheetName());
        assertNotNull("Fields should not be null", modelConfig.getFields());
        assertTrue("Should have fields", modelConfig.getFields().size() > 0);
    }

    @Test
    public void testGetAllConfigs() {
        var configs = excelConfigService.getAllConfigs();
        assertNotNull("Configs should not be null", configs);
        assertTrue("Should have at least 2 configs", configs.size() >= 2);
    }

    @Test
    public void testValidateConfig() {
        ExcelTemplateConfig config = excelConfigService.getConfig("fault");
        assertNotNull("Config should not be null", config);
        
        boolean isValid = excelConfigService.validateConfig(config);
        assertTrue("Config should be valid", isValid);
    }
}