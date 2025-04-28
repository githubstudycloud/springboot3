package com.example.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonToJavaConverter 工具类的单元测试
 */
public class JsonToJavaConverterTest {

    @TempDir
    Path tempDir;

    /**
     * 测试简单JSON转换为Java类
     */
    @Test
    public void testSimpleJsonConversion() throws IOException {
        // 准备测试数据
        String simpleJson = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Test User\",\n" +
                "  \"email\": \"test@example.com\",\n" +
                "  \"active\": true\n" +
                "}";

        String packageName = "com.example.test.model";
        String className = "SimpleUser";
        
        // 执行转换
        List<String> generatedFiles = JsonToJavaConverter.convertJsonToJavaClass(
                simpleJson, className, packageName, tempDir.toString());
        
        // 验证结果
        assertNotNull(generatedFiles);
        assertEquals(1, generatedFiles.size());
        
        // 验证文件是否已生成
        String filePath = generatedFiles.get(0);
        File javaFile = new File(filePath);
        assertTrue(javaFile.exists());
        
        // 验证文件内容
        String fileContent = new String(Files.readAllBytes(javaFile.toPath()));
        assertTrue(fileContent.contains("package " + packageName));
        assertTrue(fileContent.contains("public class " + className));
        assertTrue(fileContent.contains("private Integer id"));
        assertTrue(fileContent.contains("private String name"));
        assertTrue(fileContent.contains("private String email"));
        assertTrue(fileContent.contains("private Boolean active"));
    }
    
    /**
     * 测试嵌套JSON转换为Java类
     */
    @Test
    public void testNestedJsonConversion() throws IOException {
        // 准备测试数据
        String nestedJson = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Test User\",\n" +
                "  \"address\": {\n" +
                "    \"street\": \"123 Main St\",\n" +
                "    \"city\": \"Testville\",\n" +
                "    \"zipCode\": \"12345\"\n" +
                "  },\n" +
                "  \"roles\": [\"user\", \"admin\"]\n" +
                "}";

        String packageName = "com.example.test.model";
        String className = "UserWithAddress";
        
        // 执行转换
        List<String> generatedFiles = JsonToJavaConverter.convertJsonToJavaClass(
                nestedJson, className, packageName, tempDir.toString());
        
        // 验证结果
        assertNotNull(generatedFiles);
        assertEquals(2, generatedFiles.size()); // 应生成两个类：UserWithAddress和Address
        
        // 验证主类文件
        boolean foundMainClass = false;
        boolean foundAddressClass = false;
        
        for (String filePath : generatedFiles) {
            File javaFile = new File(filePath);
            assertTrue(javaFile.exists());
            
            String fileContent = new String(Files.readAllBytes(javaFile.toPath()));
            if (fileContent.contains("public class " + className)) {
                foundMainClass = true;
                assertTrue(fileContent.contains("private Address address"));
                assertTrue(fileContent.contains("private List<String> roles"));
            } else if (fileContent.contains("public class Address")) {
                foundAddressClass = true;
                assertTrue(fileContent.contains("private String street"));
                assertTrue(fileContent.contains("private String city"));
                assertTrue(fileContent.contains("private String zipCode"));
            }
        }
        
        assertTrue(foundMainClass, "主类未生成");
        assertTrue(foundAddressClass, "嵌套的Address类未生成");
    }
    
    /**
     * 测试深度解析嵌套JSON字符串
     */
    @Test
    public void testDeepParseJsonConversion() throws IOException {
        // 准备测试数据 - 包含嵌套的JSON字符串
        String complexJson = "{\n" +
                "  \"userId\": 1,\n" +
                "  \"profile\": \"{\\\"name\\\":\\\"John\\\",\\\"age\\\":30}\",\n" +
                "  \"settings\": \"{\\\"theme\\\":\\\"dark\\\",\\\"notifications\\\":true}\"\n" +
                "}";

        String packageName = "com.example.test.model";
        String className = "UserWithNestedData";
        
        // 执行深度解析转换
        List<String> generatedFiles = JsonToJavaConverter.deepParseAndConvert(
                complexJson, className, packageName, tempDir.toString());
        
        // 验证结果
        assertNotNull(generatedFiles);
        assertTrue(generatedFiles.size() >= 3); // 应至少生成三个类：主类、Profile类和Settings类
        
        // 验证生成的文件是否存在
        for (String filePath : generatedFiles) {
            File javaFile = new File(filePath);
            assertTrue(javaFile.exists());
        }
        
        // 详细验证每个文件的具体内容可以添加更多断言
    }
}