package com.example.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import org.springframework.stereotype.Component;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSON解析和实体类生成工具
 * 支持任意复杂的嵌套JSON，包括字符串中的JSON和JSONArray
 * 
 * @author JsonParserTool
 * @version 1.0
 */
@Component
public class JsonParserTool {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();
    private static final Pattern JSON_STRING_PATTERN = Pattern.compile("^\\s*[{\\[].*[}\\]]\\s*$", Pattern.DOTALL);
    
    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }
    
    /**
     * 解析JSON字符串为指定的实体类
     * 
     * @param jsonStr JSON字符串
     * @param clazz 目标类型
     * @return 解析后的对象
     */
    public <T> T parseJson(String jsonStr, Class<T> clazz) throws JsonProcessingException {
        JsonNode rootNode = parseToJsonNode(jsonStr);
        return MAPPER.treeToValue(processJsonNode(rootNode), clazz);
    }
    
    /**
     * 解析JSON字符串为指定的泛型类型
     * 
     * @param jsonStr JSON字符串
     * @param typeReference 泛型类型引用
     * @return 解析后的对象
     */
    public <T> T parseJson(String jsonStr, TypeReference<T> typeReference) throws JsonProcessingException {
        JsonNode rootNode = parseToJsonNode(jsonStr);
        return MAPPER.convertValue(processJsonNode(rootNode), typeReference);
    }
    
    /**
     * 解析JSON并自动生成实体类
     * 
     * @param jsonStr JSON字符串
     * @param className 生成的类名
     * @param packageName 包名
     * @return 生成的类对象
     */
    public Class<?> parseAndGenerateClass(String jsonStr, String className, String packageName) 
            throws Exception {
        String cacheKey = packageName + "." + className;
        
        // 检查缓存
        if (CLASS_CACHE.containsKey(cacheKey)) {
            return CLASS_CACHE.get(cacheKey);
        }
        
        JsonNode rootNode = parseToJsonNode(jsonStr);
        JsonNode processedNode = processJsonNode(rootNode);
        
        // 生成Java类代码
        String javaCode = generateJavaClass(processedNode, className, packageName);
        
        // 编译并加载类
        Class<?> clazz = compileAndLoadClass(javaCode, className, packageName);
        CLASS_CACHE.put(cacheKey, clazz);
        
        return clazz;
    }
    
    /**
     * 解析JSON并生成实体类文件
     * 
     * @param jsonStr JSON字符串
     * @param className 类名
     * @param packageName 包名
     * @param outputDir 输出目录
     */
    public void generateClassFile(String jsonStr, String className, String packageName, String outputDir) 
            throws Exception {
        JsonNode rootNode = parseToJsonNode(jsonStr);
        JsonNode processedNode = processJsonNode(rootNode);
        
        String javaCode = generateJavaClass(processedNode, className, packageName);
        
        // 创建目录结构
        Path packagePath = Paths.get(outputDir, packageName.replace(".", "/"));
        Files.createDirectories(packagePath);
        
        // 写入文件
        Path filePath = packagePath.resolve(className + ".java");
        Files.write(filePath, javaCode.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 将JSON字符串解析为JsonNode
     */
    private JsonNode parseToJsonNode(String jsonStr) throws JsonProcessingException {
        return MAPPER.readTree(jsonStr);
    }
    
    /**
     * 处理JsonNode，解析嵌套的JSON字符串
     */
    private JsonNode processJsonNode(JsonNode node) throws JsonProcessingException {
        if (node.isObject()) {
            ObjectNode objectNode = MAPPER.createObjectNode();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                objectNode.set(field.getKey(), processJsonNode(field.getValue()));
            }
            return objectNode;
            
        } else if (node.isArray()) {
            ArrayNode arrayNode = MAPPER.createArrayNode();
            for (JsonNode element : node) {
                arrayNode.add(processJsonNode(element));
            }
            return arrayNode;
            
        } else if (node.isTextual()) {
            String text = node.asText();
            // 检查是否是JSON字符串
            if (isJsonString(text)) {
                try {
                    // 尝试解析嵌套的JSON
                    JsonNode nestedNode = MAPPER.readTree(text);
                    return processJsonNode(nestedNode);
                } catch (Exception e) {
                    // 解析失败，保持原样
                    return node;
                }
            }
        }
        
        return node;
    }
    
    /**
     * 判断字符串是否为JSON格式
     */
    private boolean isJsonString(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        
        str = str.trim();
        return JSON_STRING_PATTERN.matcher(str).matches();
    }
    
    /**
     * 生成Java类代码
     */
    private String generateJavaClass(JsonNode node, String className, String packageName) {
        StringBuilder sb = new StringBuilder();
        Set<String> imports = new TreeSet<>();
        Map<String, String> innerClasses = new LinkedHashMap<>();
        String lineSep = System.lineSeparator();
        
        // 分析需要的导入
        analyzeImports(node, imports);
        
        // 包声明
        sb.append("package ").append(packageName).append(";").append(lineSep).append(lineSep);
        
        // 导入语句
        for (String imp : imports) {
            sb.append("import ").append(imp).append(";").append(lineSep);
        }
        if (!imports.isEmpty()) {
            sb.append(lineSep);
        }
        
        // 类声明
        sb.append("public class ").append(className).append(" {").append(lineSep);
        
        // 生成字段和方法
        if (node.isObject()) {
            generateClassMembers(node, sb, className, innerClasses);
        } else if (node.isArray() && node.size() > 0) {
            // 如果根节点是数组，创建一个包装类
            sb.append("    private List<");
            String itemType = getArrayItemType(node, className + "Item", innerClasses);
            sb.append(itemType).append("> items;").append(lineSep).append(lineSep);
            
            // getter和setter
            sb.append("    public List<").append(itemType).append("> getItems() {").append(lineSep);
            sb.append("        return items;").append(lineSep);
            sb.append("    }").append(lineSep).append(lineSep);
            
            sb.append("    public void setItems(List<").append(itemType).append("> items) {").append(lineSep);
            sb.append("        this.items = items;").append(lineSep);
            sb.append("    }").append(lineSep);
        }
        
        sb.append("}").append(lineSep);
        
        // 添加内部类
        for (Map.Entry<String, String> entry : innerClasses.entrySet()) {
            sb.append(lineSep).append(entry.getValue());
        }
        
        return sb.toString();
    }
    
    /**
     * 分析需要的导入
     */
    private void analyzeImports(JsonNode node, Set<String> imports) {
        if (node.isObject()) {
            Iterator<JsonNode> elements = node.elements();
            while (elements.hasNext()) {
                analyzeImports(elements.next(), imports);
            }
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                analyzeImports(element, imports);
            }
            imports.add("java.util.List");
        }
        
        // 如果包含日期类型，添加相应导入
        if (node.isTextual() && isDateString(node.asText())) {
            imports.add("java.util.Date");
            imports.add("com.fasterxml.jackson.annotation.JsonFormat");
        }
    }
    
    /**
     * 生成类成员
     */
    private void generateClassMembers(JsonNode node, StringBuilder sb, 
                                    String className, Map<String, String> innerClasses) {
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        String lineSep = System.lineSeparator();
        
        // 生成字段
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode fieldValue = field.getValue();
            
            String fieldType = getFieldType(fieldValue, className + capitalize(fieldName), innerClasses);
            
            // 添加字段
            sb.append("    private ").append(fieldType).append(" ")
              .append(toCamelCase(fieldName)).append(";").append(lineSep);
        }
        
        sb.append(lineSep);
        
        // 生成getter和setter
        fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode fieldValue = field.getValue();
            
            String camelCaseFieldName = toCamelCase(fieldName);
            String fieldType = getFieldType(fieldValue, className + capitalize(fieldName), innerClasses);
            
            // getter
            sb.append("    public ").append(fieldType).append(" get")
              .append(capitalize(camelCaseFieldName)).append("() {").append(lineSep);
            sb.append("        return ").append(camelCaseFieldName).append(";").append(lineSep);
            sb.append("    }").append(lineSep).append(lineSep);
            
            // setter
            sb.append("    public void set").append(capitalize(camelCaseFieldName))
              .append("(").append(fieldType).append(" ").append(camelCaseFieldName).append(") {").append(lineSep);
            sb.append("        this.").append(camelCaseFieldName).append(" = ")
              .append(camelCaseFieldName).append(";").append(lineSep);
            sb.append("    }").append(lineSep).append(lineSep);
        }
    }
    
    /**
     * 获取字段类型
     */
    private String getFieldType(JsonNode node, String suggestedClassName, Map<String, String> innerClasses) {
        if (node.isNull()) {
            return "Object";
        } else if (node.isBoolean()) {
            return "Boolean";
        } else if (node.isInt()) {
            return "Integer";
        } else if (node.isLong()) {
            return "Long";
        } else if (node.isDouble() || node.isFloat()) {
            return "Double";
        } else if (node.isTextual()) {
            if (isDateString(node.asText())) {
                return "Date";
            }
            return "String";
        } else if (node.isArray()) {
            return "List<" + getArrayItemType(node, suggestedClassName, innerClasses) + ">";
        } else if (node.isObject()) {
            // 生成内部类
            String innerClass = generateInnerClass(node, suggestedClassName, innerClasses);
            innerClasses.put(suggestedClassName, innerClass);
            return suggestedClassName;
        }
        
        return "Object";
    }
    
    /**
     * 获取数组元素类型
     */
    private String getArrayItemType(JsonNode arrayNode, String suggestedClassName, 
                                   Map<String, String> innerClasses) {
        if (arrayNode.size() == 0) {
            return "Object";
        }
        
        // 使用第一个元素推断类型
        JsonNode firstElement = arrayNode.get(0);
        return getFieldType(firstElement, suggestedClassName, innerClasses);
    }
    
    /**
     * 生成内部类
     */
    private String generateInnerClass(JsonNode node, String className, Map<String, String> innerClasses) {
        StringBuilder sb = new StringBuilder();
        String lineSep = System.lineSeparator();
        sb.append("class ").append(className).append(" {").append(lineSep);
        generateClassMembers(node, sb, className, innerClasses);
        sb.append("}").append(lineSep);
        return sb.toString();
    }
    
    /**
     * 编译并加载类
     */
    private Class<?> compileAndLoadClass(String javaCode, String className, String packageName) 
            throws Exception {
        // 获取Java编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Java编译器不可用，请确保使用JDK而非JRE");
        }
        
        // 创建诊断收集器
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        
        // 创建文件管理器
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        
        // 创建源代码对象
        JavaFileObject sourceFile = new SimpleJavaFileObject(
            URI.create("string:///" + packageName.replace(".", "/") + "/" + className + ".java"),
            JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return javaCode;
            }
        };
        
        // 编译选项
        List<String> options = Arrays.asList("-source", "8", "-target", "8");
        
        // 创建编译任务
        JavaCompiler.CompilationTask task = compiler.getTask(
            null, fileManager, diagnostics, options, null, Arrays.asList(sourceFile));
        
        // 执行编译
        boolean success = task.call();
        
        if (!success) {
            StringBuilder error = new StringBuilder("编译失败:");
            error.append(System.lineSeparator());
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                error.append(diagnostic.getMessage(null)).append(System.lineSeparator());
            }
            throw new RuntimeException(error.toString());
        }
        
        // 加载编译后的类
        URLClassLoader classLoader = URLClassLoader.newInstance(
            new URL[] { new File(".").toURI().toURL() });
        
        return Class.forName(packageName + "." + className, true, classLoader);
    }
    
    /**
     * 工具方法：将字符串转换为驼峰命名
     */
    private String toCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            
            if (ch == '_' || ch == '-' || ch == ' ') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(ch));
                    nextUpperCase = false;
                } else {
                    result.append(i == 0 ? Character.toLowerCase(ch) : ch);
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * 工具方法：首字母大写
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * 判断是否为日期字符串
     */
    private boolean isDateString(String str) {
        // 简单的日期格式判断
        String[] datePatterns = {
            "\\d{4}-\\d{2}-\\d{2}",
            "\\d{4}/\\d{2}/\\d{2}",
            "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}",
            "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}"
        };
        
        for (String pattern : datePatterns) {
            if (str.matches(pattern + ".*")) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 批量解析JSON文件
     */
    public Map<String, Object> parseBatchJsonFiles(List<String> filePaths, Class<?> targetClass) 
            throws IOException, JsonProcessingException {
        Map<String, Object> results = new HashMap<>();
        
        for (String filePath : filePaths) {
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            Object parsed = parseJson(jsonContent, targetClass);
            results.put(filePath, parsed);
        }
        
        return results;
    }
    
    /**
     * 深度合并多个JSON对象
     */
    public String mergeJsonObjects(String... jsonStrings) throws JsonProcessingException {
        ObjectNode result = MAPPER.createObjectNode();
        
        for (String jsonStr : jsonStrings) {
            JsonNode node = parseToJsonNode(jsonStr);
            if (node.isObject()) {
                mergeObjects(result, (ObjectNode) node);
            }
        }
        
        return MAPPER.writeValueAsString(result);
    }
    
    /**
     * 合并两个ObjectNode
     */
    private void mergeObjects(ObjectNode target, ObjectNode source) {
        Iterator<Map.Entry<String, JsonNode>> fields = source.fields();
        
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode fieldValue = field.getValue();
            
            if (target.has(fieldName) && target.get(fieldName).isObject() && fieldValue.isObject()) {
                // 递归合并对象
                mergeObjects((ObjectNode) target.get(fieldName), (ObjectNode) fieldValue);
            } else {
                // 直接覆盖
                target.set(fieldName, fieldValue);
            }
        }
    }
}
