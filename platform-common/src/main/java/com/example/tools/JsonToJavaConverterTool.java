package com.example.tools;

import com.example.common.utils.JsonToJavaConverter;
import com.example.common.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * JSON转Java实体类命令行工具
 * <p>
 * 该工具可通过命令行将JSON文件转换为Java实体类
 * </p>
 *
 * @author platform
 * @since 1.0.0
 */
public class JsonToJavaConverterTool {

    /**
     * 主方法，处理命令行参数
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            printUsage();
            return;
        }

        try {
            String command = args[0];
            switch (command) {
                case "convert":
                    handleConvertCommand(args);
                    break;
                case "validate":
                    handleValidateCommand(args);
                    break;
                case "help":
                    printUsage();
                    break;
                default:
                    System.err.println("错误：未知命令 '" + command + "'");
                    printUsage();
                    break;
            }
        } catch (Exception e) {
            System.err.println("错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 处理convert命令
     *
     * @param args 命令行参数
     * @throws IOException 如果读取文件或写入文件失败
     */
    private static void handleConvertCommand(String[] args) throws IOException {
        if (args.length < 5) {
            System.err.println("错误：convert命令需要更多参数");
            printConvertUsage();
            return;
        }

        String inputPath = args[1];
        String className = args[2];
        String packageName = args[3];
        String outputDir = args[4];
        boolean deepParse = args.length > 5 && args[5].equals("--deep");

        // 验证输入文件存在
        File inputFile = new File(inputPath);
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.err.println("错误：输入文件不存在或不是文件：" + inputPath);
            return;
        }

        // 读取JSON文件内容
        String jsonContent = new String(Files.readAllBytes(Paths.get(inputPath)));

        // 执行转换
        List<String> generatedFiles;
        if (deepParse) {
            System.out.println("使用深度解析模式...");
            generatedFiles = JsonToJavaConverter.deepParseAndConvert(jsonContent, className, packageName, outputDir);
        } else {
            generatedFiles = JsonToJavaConverter.convertJsonToJavaClass(jsonContent, className, packageName, outputDir);
        }

        // 输出结果
        System.out.println("成功生成以下Java文件：");
        for (String filePath : generatedFiles) {
            System.out.println("  - " + filePath);
        }
    }

    /**
     * 处理validate命令
     *
     * @param args 命令行参数
     * @throws IOException 如果读取文件失败
     */
    private static void handleValidateCommand(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("错误：validate命令需要指定JSON文件路径");
            printValidateUsage();
            return;
        }

        String inputPath = args[1];
        
        // 验证输入文件存在
        File inputFile = new File(inputPath);
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.err.println("错误：输入文件不存在或不是文件：" + inputPath);
            return;
        }

        // 读取JSON文件内容
        String jsonContent = new String(Files.readAllBytes(Paths.get(inputPath)));

        try {
            // 使用Jackson验证JSON格式
            ObjectMapper objectMapper = JsonUtil.getObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonContent);
            
            // 输出JSON结构信息
            System.out.println("JSON验证成功！");
            System.out.println("JSON结构信息：");
            
            if (jsonNode.isObject()) {
                System.out.println("- 类型：JSON对象");
                System.out.println("- 属性数量：" + jsonNode.size());
            } else if (jsonNode.isArray()) {
                System.out.println("- 类型：JSON数组");
                System.out.println("- 元素数量：" + jsonNode.size());
            } else {
                System.out.println("- 类型：" + jsonNode.getNodeType());
            }
            
            // 输出格式化的JSON
            System.out.println("\n格式化的JSON：");
            System.out.println(JsonUtil.toPrettyJson(jsonNode));
            
        } catch (Exception e) {
            System.err.println("JSON验证失败！错误信息：" + e.getMessage());
        }
    }

    /**
     * 打印使用说明
     */
    private static void printUsage() {
        System.out.println("JSON转Java实体类工具 - 用法：");
        System.out.println("  java -cp <classpath> " + JsonToJavaConverterTool.class.getName() + " <command> [options]");
        System.out.println("\n可用命令：");
        System.out.println("  convert   - 将JSON文件转换为Java实体类");
        System.out.println("  validate  - 验证JSON文件格式");
        System.out.println("  help      - 显示帮助信息");
        System.out.println("\n示例：");
        System.out.println("  java -cp <classpath> " + JsonToJavaConverterTool.class.getName() 
                         + " convert input.json User com.example.model src/main/java");
        System.out.println("\n更多信息，请使用 'help <command>' 查看特定命令的帮助。");
    }

    /**
     * 打印convert命令的使用说明
     */
    private static void printConvertUsage() {
        System.out.println("'convert' 命令 - 用法：");
        System.out.println("  java -cp <classpath> " + JsonToJavaConverterTool.class.getName() 
                         + " convert <jsonFile> <className> <packageName> <outputDir> [--deep]");
        System.out.println("\n参数：");
        System.out.println("  jsonFile    - JSON文件路径");
        System.out.println("  className   - 生成的主实体类名");
        System.out.println("  packageName - 生成的Java文件的包名");
        System.out.println("  outputDir   - 输出目录");
        System.out.println("  --deep      - (可选) 使用深度解析模式，处理嵌套的JSON字符串");
        System.out.println("\n示例：");
        System.out.println("  java -cp <classpath> " + JsonToJavaConverterTool.class.getName() 
                         + " convert data.json ApiResponse com.example.model src/main/java --deep");
    }

    /**
     * 打印validate命令的使用说明
     */
    private static void printValidateUsage() {
        System.out.println("'validate' 命令 - 用法：");
        System.out.println("  java -cp <classpath> " + JsonToJavaConverterTool.class.getName() 
                         + " validate <jsonFile>");
        System.out.println("\n参数：");
        System.out.println("  jsonFile - 要验证的JSON文件路径");
        System.out.println("\n示例：");
        System.out.println("  java -cp <classpath> " + JsonToJavaConverterTool.class.getName() 
                         + " validate data.json");
    }
}