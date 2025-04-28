package com.example.tools;

import com.example.common.utils.JsonToJavaConverter;

import java.io.IOException;
import java.util.List;

/**
 * JsonToJavaConverter工具类的演示程序
 * 
 * @author platform
 */
public class JsonToJavaConverterDemo {

    /**
     * 演示JsonToJavaConverter工具类的用法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 示例JSON字符串（简单JSON）
        String simpleJson = "{\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"age\": 30,\n" +
                "  \"isActive\": true,\n" +
                "  \"address\": {\n" +
                "    \"street\": \"123 Main St\",\n" +
                "    \"city\": \"Anytown\",\n" +
                "    \"zipCode\": \"12345\"\n" +
                "  },\n" +
                "  \"phoneNumbers\": [\n" +
                "    {\n" +
                "      \"type\": \"home\",\n" +
                "      \"number\": \"555-1234\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"work\",\n" +
                "      \"number\": \"555-5678\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // 示例JSON字符串（嵌套JSON字符串）
        String complexJson = "{\n" +
                "  \"userId\": 1,\n" +
                "  \"username\": \"user1\",\n" +
                "  \"profile\": {\n" +
                "    \"fullName\": \"User One\",\n" +
                "    \"preferences\": \"{\\\"theme\\\":\\\"dark\\\",\\\"notifications\\\":true,\\\"language\\\":\\\"en\\\"}\"\n" +
                "  },\n" +
                "  \"orders\": [\n" +
                "    {\n" +
                "      \"orderId\": 101,\n" +
                "      \"total\": 99.99,\n" +
                "      \"items\": \"[{\\\"id\\\":1,\\\"name\\\":\\\"Product A\\\",\\\"price\\\":49.99,\\\"quantity\\\":1},{\\\"id\\\":2,\\\"name\\\":\\\"Product B\\\",\\\"price\\\":25.00,\\\"quantity\\\":2}]\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"orderId\": 102,\n" +
                "      \"total\": 199.99,\n" +
                "      \"items\": \"[{\\\"id\\\":3,\\\"name\\\":\\\"Product C\\\",\\\"price\\\":199.99,\\\"quantity\\\":1}]\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"metadata\": \"{\\\"lastLogin\\\":\\\"2023-01-15T14:30:00Z\\\",\\\"registrationDate\\\":\\\"2022-05-10\\\",\\\"settings\\\":{\\\"emailFrequency\\\":\\\"daily\\\",\\\"twoFactorAuth\\\":true}}\"\n" +
                "}";

        // 输出目录
        String outputDir = "src/main/java";
        // 包名
        String packageName = "com.example.generated";

        try {
            // 示例1：转换简单JSON
            System.out.println("转换简单JSON到Java实体类...");
            List<String> simpleFiles = JsonToJavaConverter.convertJsonToJavaClass(
                    simpleJson, "User", packageName, outputDir);
            System.out.println("生成了以下文件：");
            simpleFiles.forEach(System.out::println);
            System.out.println();

            // 示例2：深度解析并转换复杂JSON（包含嵌套的JSON字符串）
            System.out.println("深度解析并转换复杂JSON到Java实体类...");
            List<String> complexFiles = JsonToJavaConverter.deepParseAndConvert(
                    complexJson, "UserWithOrders", packageName, outputDir);
            System.out.println("生成了以下文件：");
            complexFiles.forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("转换过程中发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 以下是一个完整使用示例，可以作为实际项目中的参考
     */
    public static void convertJsonFileExample() {
        try {
            // 实际项目中可以从文件、API响应或其他来源获取JSON
            String jsonFromFile = "{ ... }"; // 从文件读取的JSON字符串
            
            // 设置输出目录和包名
            String outputDir = "src/main/java";
            String packageName = "com.example.domain.model";
            
            // 将JSON转换为Java实体类
            List<String> generatedFiles = JsonToJavaConverter.deepParseAndConvert(
                    jsonFromFile, "ApiResponse", packageName, outputDir);
            
            // 输出生成的文件路径
            System.out.println("成功生成以下实体类文件：");
            for (String filePath : generatedFiles) {
                System.out.println(filePath);
            }
            
        } catch (IOException e) {
            System.err.println("实体类生成失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}