package com.example.util;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonParserTool 单元测试
 */
@SpringBootTest
public class JsonParserToolTest {

    private JsonParserTool jsonParserTool;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        jsonParserTool = new JsonParserTool();
    }

    @Test
    public void testParseSimpleJson() throws Exception {
        String json = "{\"name\": \"test\", \"age\": 25}";
        Map<String, Object> result = jsonParserTool.parseJson(json, new TypeReference<Map<String, Object>>() {});
        
        assertEquals("test", result.get("name"));
        assertEquals(25, result.get("age"));
    }

    @Test
    public void testParseNestedJsonString() throws Exception {
        String json = new JsonBuilder()
            .startObject()
            .field("id", 1).comma()
            .jsonField("data", "{\"nested\": true, \"value\": 100}")
            .newLine().endObject()
            .build();
        
        Map<String, Object> result = jsonParserTool.parseJson(json, new TypeReference<Map<String, Object>>() {});
        
        assertEquals(1, result.get("id"));
        assertTrue(result.get("data") instanceof Map);
        
        Map<String, Object> nestedData = (Map<String, Object>) result.get("data");
        assertEquals(true, nestedData.get("nested"));
        assertEquals(100, nestedData.get("value"));
    }

    @Test
    public void testParseArrayWithNestedJson() throws Exception {
        String json = new JsonBuilder()
            .startArray()
                .startObject()
                .jsonField("items", "[{\"id\": 1}, {\"id\": 2}]")
                .newLine().endObject()
            .newLine().endArray()
            .build();
        
        List<Map<String, Object>> result = jsonParserTool.parseJson(json, 
            new TypeReference<List<Map<String, Object>>>() {});
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        Map<String, Object> firstItem = result.get(0);
        assertTrue(firstItem.get("items") instanceof List);
        
        List<Map<String, Object>> items = (List<Map<String, Object>>) firstItem.get("items");
        assertEquals(2, items.size());
        assertEquals(1, items.get(0).get("id"));
        assertEquals(2, items.get(1).get("id"));
    }

    @Test
    public void testGenerateClassFile() throws Exception {
        String json = new JsonBuilder()
            .startObject()
            .field("id", 1).comma()
            .field("name", "Test User").comma()
            .field("email", "test@example.com").comma()
            .raw("    \"profile\": ").startObject()
                .field("age", 25).comma()
                .field("city", "Beijing")
            .newLine().endObject().comma()
            .raw("    \"tags\": [\"java\", \"spring\", \"test\"]")
            .newLine().endObject()
            .build();
        
        jsonParserTool.generateClassFile(json, "TestUser", "com.test.generated", tempDir.toString());
        
        Path generatedFile = tempDir.resolve("com/test/generated/TestUser.java");
        assertTrue(generatedFile.toFile().exists());
        
        // 验证生成的文件内容
        String content = new String(java.nio.file.Files.readAllBytes(generatedFile));
        assertTrue(content.contains("public class TestUser"));
        assertTrue(content.contains("private Integer id"));
        assertTrue(content.contains("private String name"));
        assertTrue(content.contains("private String email"));
        assertTrue(content.contains("private TestUserProfile profile"));
        assertTrue(content.contains("private List<String> tags"));
    }

    @Test
    public void testParseAndGenerateClass() throws Exception {
        String json = JsonBuilder.object(
            "code", 200,
            "message", "success",
            "data", JsonBuilder.object(
                "userId", 12345,
                "username", "testuser"
            )
        );
        
        Class<?> generatedClass = jsonParserTool.parseAndGenerateClass(
            json, "ApiResponse", "com.test.dynamic");
        
        assertNotNull(generatedClass);
        assertEquals("com.test.dynamic.ApiResponse", generatedClass.getName());
        
        // 验证生成的类可以正常使用
        Object instance = jsonParserTool.parseJson(json, generatedClass);
        assertNotNull(instance);
        
        // 使用反射验证字段值
        assertEquals(200, generatedClass.getMethod("getCode").invoke(instance));
        assertEquals("success", generatedClass.getMethod("getMessage").invoke(instance));
        assertNotNull(generatedClass.getMethod("getData").invoke(instance));
    }

    @Test
    public void testMergeJsonObjects() throws Exception {
        String json1 = JsonBuilder.object(
            "name", "original",
            "version", 1,
            "config", JsonBuilder.object(
                "timeout", 30,
                "retries", 3
            )
        );
        
        String json2 = JsonBuilder.object(
            "version", 2,
            "author", "tester",
            "config", JsonBuilder.object(
                "timeout", 60,
                "maxConnections", 10
            )
        );
        
        String merged = jsonParserTool.mergeJsonObjects(json1, json2);
        Map<String, Object> result = jsonParserTool.parseJson(merged, 
            new TypeReference<Map<String, Object>>() {});
        
        assertEquals("original", result.get("name"));
        assertEquals(2, result.get("version")); // 被覆盖
        assertEquals("tester", result.get("author")); // 新增
        
        Map<String, Object> config = (Map<String, Object>) result.get("config");
        assertEquals(60, config.get("timeout")); // 被覆盖
        assertEquals(3, config.get("retries")); // 保留
        assertEquals(10, config.get("maxConnections")); // 新增
    }

    @Test
    public void testComplexNestedJsonWithEscapes() throws Exception {
        // 构建复杂的嵌套JSON，包含多层转义
        String complexJson = "{\n" +
            "    \"level1\": {\n" +
            "        \"stringData\": \"Just a string\",\n" +
            "        \"jsonString\": \"{\\\"level2\\\": {\\\"jsonArray\\\": \\\"[1, 2, {\\\\\\\"value\\\\\\\": 3}]\\\", \\\"bool\\\": true}}\",\n" +
            "        \"arrayData\": [\n" +
            "            \"plain string\",\n" +
            "            \"{\\\"embedded\\\": \\\"json\\\", \\\"number\\\": 42}\",\n" +
            "            123\n" +
            "        ]\n" +
            "    }\n" +
            "}";
        
        Map<String, Object> result = jsonParserTool.parseJson(complexJson, 
            new TypeReference<Map<String, Object>>() {});
        
        assertNotNull(result);
        Map<String, Object> level1 = (Map<String, Object>) result.get("level1");
        
        assertEquals("Just a string", level1.get("stringData"));
        
        // 验证嵌套的JSON字符串被正确解析
        assertTrue(level1.get("jsonString") instanceof Map);
        Map<String, Object> jsonString = (Map<String, Object>) level1.get("jsonString");
        Map<String, Object> level2 = (Map<String, Object>) jsonString.get("level2");
        
        assertTrue(level2.get("jsonArray") instanceof List);
        assertEquals(true, level2.get("bool"));
        
        // 验证数组中的JSON字符串被解析
        List<Object> arrayData = (List<Object>) level1.get("arrayData");
        assertEquals(3, arrayData.size());
        assertEquals("plain string", arrayData.get(0));
        assertTrue(arrayData.get(1) instanceof Map);
        assertEquals(123, arrayData.get(2));
    }

    @Test
    public void testHandleInvalidJson() {
        String invalidJson = "{invalid json}";
        
        assertThrows(Exception.class, () -> {
            jsonParserTool.parseJson(invalidJson, Map.class);
        });
    }

    @Test
    public void testEmptyAndNullHandling() throws Exception {
        String jsonWithNulls = new JsonBuilder()
            .startObject()
            .field("notNull", "value").comma()
            .field("isNull", null).comma()
            .field("emptyString", "").comma()
            .raw("    \"emptyObject\": {},").newLine()
            .raw("    \"emptyArray\": []")
            .newLine().endObject()
            .build();
        
        Map<String, Object> result = jsonParserTool.parseJson(jsonWithNulls, 
            new TypeReference<Map<String, Object>>() {});
        
        assertEquals("value", result.get("notNull"));
        assertNull(result.get("isNull"));
        assertEquals("", result.get("emptyString"));
        assertTrue(result.get("emptyObject") instanceof Map);
        assertTrue(result.get("emptyArray") instanceof List);
        
        assertEquals(0, ((Map) result.get("emptyObject")).size());
        assertEquals(0, ((List) result.get("emptyArray")).size());
    }

    @Test
    public void testBatchJsonFiles() throws Exception {
        // 创建测试JSON文件
        Path file1 = tempDir.resolve("test1.json");
        Path file2 = tempDir.resolve("test2.json");
        
        String json1 = JsonBuilder.object("id", 1, "name", "File 1");
        String json2 = JsonBuilder.object("id", 2, "name", "File 2");
        
        java.nio.file.Files.write(file1, json1.getBytes());
        java.nio.file.Files.write(file2, json2.getBytes());
        
        List<String> filePaths = java.util.Arrays.asList(
            file1.toString(),
            file2.toString()
        );
        
        Map<String, Object> results = jsonParserTool.parseBatchJsonFiles(filePaths, Map.class);
        
        assertEquals(2, results.size());
        assertTrue(results.containsKey(file1.toString()));
        assertTrue(results.containsKey(file2.toString()));
    }

    @Test
    public void testDateStringRecognition() throws Exception {
        String jsonWithDates = new JsonBuilder()
            .startObject()
            .field("date1", "2024-01-01").comma()
            .field("date2", "2024-01-01 10:30:00").comma()
            .field("date3", "2024-01-01T10:30:00").comma()
            .field("notDate", "just-a-string")
            .newLine().endObject()
            .build();
        
        // 生成类文件以验证日期识别
        jsonParserTool.generateClassFile(jsonWithDates, "DateTest", "com.test", tempDir.toString());
        
        Path generatedFile = tempDir.resolve("com/test/DateTest.java");
        String content = new String(java.nio.file.Files.readAllBytes(generatedFile));
        
        // 验证日期字段被识别
        assertTrue(content.contains("import java.util.Date"));
        assertTrue(content.contains("private Date date1"));
        assertTrue(content.contains("private Date date2"));
        assertTrue(content.contains("private Date date3"));
        assertTrue(content.contains("private String notDate"));
    }

    @Test
    public void testCamelCaseConversion() throws Exception {
        String jsonWithUnderscore = new JsonBuilder()
            .startObject()
            .field("user_name", "test").comma()
            .field("first-name", "John").comma()
            .field("last name", "Doe").comma()
            .field("normalField", "value")
            .newLine().endObject()
            .build();
        
        jsonParserTool.generateClassFile(jsonWithUnderscore, "CamelCaseTest", "com.test", tempDir.toString());
        
        Path generatedFile = tempDir.resolve("com/test/CamelCaseTest.java");
        String content = new String(java.nio.file.Files.readAllBytes(generatedFile));
        
        // 验证字段名转换
        assertTrue(content.contains("private String userName"));
        assertTrue(content.contains("private String firstName"));
        assertTrue(content.contains("private String lastName"));
        assertTrue(content.contains("private String normalField"));
    }

    @Test
    public void testSpecialCharactersInJson() throws Exception {
        String specialJson = new JsonBuilder()
            .startObject()
            .field("quote", "He said \"Hello\"").comma()
            .field("backslash", "C:\\\\Windows\\\\System32").comma()
            .field("newline", "Line 1\nLine 2").comma()
            .field("tab", "Col1\tCol2").comma()
            .field("unicode", "\u4e2d\u6587")
            .newLine().endObject()
            .build();
        
        Map<String, Object> result = jsonParserTool.parseJson(specialJson, 
            new TypeReference<Map<String, Object>>() {});
        
        assertEquals("He said \"Hello\"", result.get("quote"));
        assertEquals("C:\\Windows\\System32", result.get("backslash"));
        assertTrue(result.get("newline").toString().contains("Line 1"));
        assertTrue(result.get("tab").toString().contains("Col1"));
        assertEquals("中文", result.get("unicode"));
    }
}
