package com.example.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * JSON转Java实体类工具
 * <p>
 * 该工具可以将任意复杂的JSON字符串生成对应的Java实体类到指定目录。
 * 支持处理嵌套JSON对象，JSON数组，以及JSON字符串中包含的JSON字符串。
 * </p>
 *
 * @author platform
 * @since 1.0.0
 */
public final class JsonToJavaConverter {

    private static final Logger log = LoggerFactory.getLogger(JsonToJavaConverter.class);
    private static final ObjectMapper OBJECT_MAPPER = JsonUtil.getObjectMapper();
    
    // 存储已生成的类名，避免重复生成
    private static final Map<String, String> GENERATED_CLASSES = new ConcurrentHashMap<>();
    
    // Java关键字列表，避免使用这些词作为变量名
    private static final Set<String> JAVA_KEYWORDS = new HashSet<>();
    
    // 正则表达式用于校验和格式化类名
    private static final Pattern INVALID_CHARS = Pattern.compile("[^a-zA-Z0-9]");
    
    static {
        // 初始化Java关键字列表
        String[] keywords = {
                "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
                "continue", "default", "do", "double", "else", "enum", "extends", "false", "final", "finally",
                "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long",
                "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static",
                "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true",
                "try", "void", "volatile", "while", "var"
        };
        for (String keyword : keywords) {
            JAVA_KEYWORDS.add(keyword);
        }
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private JsonToJavaConverter() {
        // 空构造函数，工具类不应该被实例化
    }
    
    /**
     * 将JSON字符串转换为Java实体类文件
     *
     * @param jsonStr    JSON字符串
     * @param className  根实体类名
     * @param packageName 包名
     * @param outputDir  输出目录
     * @return 生成的Java文件路径列表
     * @throws IOException 如果生成过程中发生IO错误
     */
    public static List<String> convertJsonToJavaClass(String jsonStr, String className, 
                                                    String packageName, String outputDir) throws IOException {
        // 清空已生成类记录
        GENERATED_CLASSES.clear();
        
        // 创建输出目录
        Path packagePath = createPackageDirectories(outputDir, packageName);
        
        // 解析JSON字符串
        JsonNode rootNode = OBJECT_MAPPER.readTree(jsonStr);
        
        // 生成Java类
        String formattedClassName = formatClassName(className);
        generateJavaClass(rootNode, formattedClassName, packageName, packagePath.toString());
        
        // 返回生成的文件路径列表
        List<String> generatedFiles = new ArrayList<>();
        GENERATED_CLASSES.forEach((key, value) -> generatedFiles.add(value));
        
        return generatedFiles;
    }
    
    /**
     * 深度解析JSON，处理嵌套的JSON字符串
     *
     * @param jsonStr JSON字符串
     * @param className 根实体类名
     * @param packageName 包名
     * @param outputDir 输出目录
     * @return 生成的Java文件路径列表
     * @throws IOException 如果生成过程中发生IO错误
     */
    public static List<String> deepParseAndConvert(String jsonStr, String className, 
                                                 String packageName, String outputDir) throws IOException {
        // 清空已生成类记录
        GENERATED_CLASSES.clear();
        
        // 创建输出目录
        Path packagePath = createPackageDirectories(outputDir, packageName);
        
        // 解析JSON字符串
        JsonNode rootNode = parseJsonWithNestedContent(jsonStr);
        
        // 生成Java类
        String formattedClassName = formatClassName(className);
        generateJavaClass(rootNode, formattedClassName, packageName, packagePath.toString());
        
        // 返回生成的文件路径列表
        List<String> generatedFiles = new ArrayList<>();
        GENERATED_CLASSES.forEach((key, value) -> generatedFiles.add(value));
        
        return generatedFiles;
    }
    
    /**
     * 解析可能包含嵌套JSON字符串的JSON
     *
     * @param jsonStr JSON字符串
     * @return 解析后的JsonNode
     */
    private static JsonNode parseJsonWithNestedContent(String jsonStr) {
        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(jsonStr);
            return deepParseJsonNode(rootNode);
        } catch (IOException e) {
            log.error("解析JSON字符串时发生错误", e);
            throw new RuntimeException("解析JSON失败", e);
        }
    }
    
    /**
     * 深度解析JsonNode，处理可能是JSON字符串的字段
     *
     * @param node JsonNode
     * @return 处理后的JsonNode
     */
    private static JsonNode deepParseJsonNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
            
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String fieldName = entry.getKey();
                JsonNode fieldValue = entry.getValue();
                
                if (fieldValue.isTextual()) {
                    // 尝试将文本字段解析为JSON
                    String text = fieldValue.asText();
                    try {
                        if ((text.startsWith("{") && text.endsWith("}")) || 
                            (text.startsWith("[") && text.endsWith("]"))) {
                            JsonNode parsedNode = OBJECT_MAPPER.readTree(text);
                            objectNode.set(fieldName, deepParseJsonNode(parsedNode));
                        }
                    } catch (IOException e) {
                        // 不是有效的JSON，保持原样
                    }
                } else if (fieldValue.isObject() || fieldValue.isArray()) {
                    // 递归处理对象或数组
                    objectNode.set(fieldName, deepParseJsonNode(fieldValue));
                }
            }
            return objectNode;
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode element = arrayNode.get(i);
                if (element.isObject() || element.isArray()) {
                    arrayNode.set(i, deepParseJsonNode(element));
                } else if (element.isTextual()) {
                    // 尝试将文本元素解析为JSON
                    String text = element.asText();
                    try {
                        if ((text.startsWith("{") && text.endsWith("}")) || 
                            (text.startsWith("[") && text.endsWith("]"))) {
                            JsonNode parsedNode = OBJECT_MAPPER.readTree(text);
                            arrayNode.set(i, deepParseJsonNode(parsedNode));
                        }
                    } catch (IOException e) {
                        // 不是有效的JSON，保持原样
                    }
                }
            }
            return arrayNode;
        }
        
        return node;
    }
    
    /**
     * 创建包目录结构
     *
     * @param outputDir   输出根目录
     * @param packageName 包名
     * @return 包目录的路径
     * @throws IOException 如果创建目录失败
     */
    private static Path createPackageDirectories(String outputDir, String packageName) throws IOException {
        String packagePath = packageName.replace('.', File.separatorChar);
        Path fullPath = Paths.get(outputDir, packagePath);
        
        if (!Files.exists(fullPath)) {
            Files.createDirectories(fullPath);
        }
        
        return fullPath;
    }
    
    /**
     * 生成Java类
     *
     * @param jsonNode    JSON节点
     * @param className   类名
     * @param packageName 包名
     * @param outputDir   输出目录
     * @throws IOException 如果写入文件失败
     */
    private static void generateJavaClass(JsonNode jsonNode, String className, 
                                        String packageName, String outputDir) throws IOException {
        // 如果不是对象节点，不生成类
        if (!jsonNode.isObject()) {
            return;
        }
        
        // 避免重复生成相同的类
        if (GENERATED_CLASSES.containsKey(className)) {
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        
        // 生成包声明
        sb.append("package ").append(packageName).append(";\n\n");
        
        // 导入需要的包
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Map;\n");
        sb.append("import java.util.Date;\n");
        sb.append("import java.math.BigDecimal;\n");
        sb.append("import java.math.BigInteger;\n");
        sb.append("import java.time.LocalDate;\n");
        sb.append("import java.time.LocalDateTime;\n");
        sb.append("import com.fasterxml.jackson.annotation.JsonProperty;\n");
        sb.append("import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\n\n");
        
        // 生成类声明
        sb.append("/**\n");
        sb.append(" * ").append(className).append(" 实体类\n");
        sb.append(" * 该类由JsonToJavaConverter自动生成\n");
        sb.append(" */\n");
        sb.append("@JsonIgnoreProperties(ignoreUnknown = true)\n");
        sb.append("public class ").append(className).append(" {\n\n");
        
        // 用于存储嵌套类的信息
        List<NestedClassInfo> nestedClasses = new ArrayList<>();
        
        // 生成字段
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String fieldName = entry.getKey();
            JsonNode fieldValue = entry.getValue();
            
            // 生成字段信息
            generateField(sb, fieldName, fieldValue, className, packageName, outputDir, nestedClasses);
        }
        
        // 生成默认构造函数
        sb.append("\n    /**\n");
        sb.append("     * 默认构造函数\n");
        sb.append("     */\n");
        sb.append("    public ").append(className).append("() {\n");
        sb.append("        // 默认构造函数\n");
        sb.append("    }\n\n");
        
        // 生成Getter和Setter方法
        fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String fieldName = entry.getKey();
            JsonNode fieldValue = entry.getValue();
            
            generateGetterAndSetter(sb, fieldName, fieldValue);
        }
        
        // 生成toString方法
        generateToString(sb, className, jsonNode);
        
        // 结束类声明
        sb.append("}\n");
        
        // 写入文件
        String filePath = outputDir + File.separator + className + ".java";
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(sb.toString());
        }
        
        // 记录已生成的类
        GENERATED_CLASSES.put(className, filePath);
        
        // 处理嵌套类
        for (NestedClassInfo nestedClass : nestedClasses) {
            generateJavaClass(nestedClass.jsonNode, nestedClass.className, packageName, outputDir);
        }
    }
    
    /**
     * 生成字段
     *
     * @param sb            StringBuilder
     * @param fieldName     字段名
     * @param fieldValue    字段值
     * @param className     当前类名
     * @param packageName   包名
     * @param outputDir     输出目录
     * @param nestedClasses 嵌套类信息列表
     */
    private static void generateField(StringBuilder sb, String fieldName, JsonNode fieldValue, 
                                    String className, String packageName, String outputDir, 
                                    List<NestedClassInfo> nestedClasses) {
        String javaFieldName = formatFieldName(fieldName);
        String fieldType = determineFieldType(fieldName, fieldValue, className, nestedClasses);
        
        // 生成字段注释
        sb.append("    /**\n");
        sb.append("     * ").append(fieldName).append("\n");
        sb.append("     */\n");
        
        // 添加@JsonProperty注解，确保序列化和反序列化使用原始字段名
        if (!fieldName.equals(javaFieldName)) {
            sb.append("    @JsonProperty(\"").append(fieldName).append("\")\n");
        }
        
        // 声明字段
        sb.append("    private ").append(fieldType).append(" ").append(javaFieldName).append(";\n\n");
    }
    
    /**
     * 确定字段类型
     *
     * @param fieldName     字段名
     * @param fieldValue    字段值
     * @param className     当前类名
     * @param nestedClasses 嵌套类信息列表
     * @return 字段类型字符串
     */
    private static String determineFieldType(String fieldName, JsonNode fieldValue, 
                                          String className, List<NestedClassInfo> nestedClasses) {
        if (fieldValue.isNull()) {
            return "Object";
        } else if (fieldValue.isTextual()) {
            return "String";
        } else if (fieldValue.isInt()) {
            return "Integer";
        } else if (fieldValue.isLong()) {
            return "Long";
        } else if (fieldValue.isDouble() || fieldValue.isFloat()) {
            return "Double";
        } else if (fieldValue.isBoolean()) {
            return "Boolean";
        } else if (fieldValue.isBigInteger()) {
            return "BigInteger";
        } else if (fieldValue.isBigDecimal()) {
            return "BigDecimal";
        } else if (fieldValue.isArray()) {
            // 处理数组
            if (fieldValue.size() > 0) {
                JsonNode firstElement = fieldValue.get(0);
                if (firstElement.isObject()) {
                    // 如果数组元素是对象，为其生成一个新类
                    String nestedClassName = formatClassName(getSingularName(fieldName));
                    nestedClasses.add(new NestedClassInfo(nestedClassName, firstElement));
                    return "List<" + nestedClassName + ">";
                } else {
                    // 根据数组第一个元素类型确定List的泛型
                    return "List<" + getJavaTypeForSimpleValue(firstElement) + ">";
                }
            }
            return "List<Object>";
        } else if (fieldValue.isObject()) {
            // 处理嵌套对象
            String nestedClassName = formatClassName(fieldName);
            nestedClasses.add(new NestedClassInfo(nestedClassName, fieldValue));
            return nestedClassName;
        } else {
            return "Object";
        }
    }
    
    /**
     * 获取简单值的Java类型
     *
     * @param value JSON节点
     * @return Java类型字符串
     */
    private static String getJavaTypeForSimpleValue(JsonNode value) {
        if (value.isNull()) {
            return "Object";
        } else if (value.isTextual()) {
            return "String";
        } else if (value.isInt()) {
            return "Integer";
        } else if (value.isLong()) {
            return "Long";
        } else if (value.isDouble() || value.isFloat()) {
            return "Double";
        } else if (value.isBoolean()) {
            return "Boolean";
        } else if (value.isBigInteger()) {
            return "BigInteger";
        } else if (value.isBigDecimal()) {
            return "BigDecimal";
        } else {
            return "Object";
        }
    }
    
    /**
     * 生成Getter和Setter方法
     *
     * @param sb         StringBuilder
     * @param fieldName  字段名
     * @param fieldValue 字段值
     */
    private static void generateGetterAndSetter(StringBuilder sb, String fieldName, JsonNode fieldValue) {
        String javaFieldName = formatFieldName(fieldName);
        String fieldType = determineFieldType(fieldName, fieldValue, "", new ArrayList<>());
        String capitalizedFieldName = capitalizeFirstLetter(javaFieldName);
        
        // 生成Getter
        sb.append("    /**\n");
        sb.append("     * 获取").append(fieldName).append("\n");
        sb.append("     *\n");
        sb.append("     * @return ").append(javaFieldName).append("\n");
        sb.append("     */\n");
        
        // 布尔类型使用is前缀，其他使用get前缀
        String getterPrefix = "get";
        if (fieldType.equals("Boolean") && !javaFieldName.startsWith("is")) {
            getterPrefix = "is";
        }
        
        sb.append("    public ").append(fieldType).append(" ").append(getterPrefix)
          .append(capitalizedFieldName).append("() {\n");
        sb.append("        return this.").append(javaFieldName).append(";\n");
        sb.append("    }\n\n");
        
        // 生成Setter
        sb.append("    /**\n");
        sb.append("     * 设置").append(fieldName).append("\n");
        sb.append("     *\n");
        sb.append("     * @param ").append(javaFieldName).append(" ").append(fieldName).append("\n");
        sb.append("     */\n");
        sb.append("    public void set").append(capitalizedFieldName).append("(")
          .append(fieldType).append(" ").append(javaFieldName).append(") {\n");
        sb.append("        this.").append(javaFieldName).append(" = ").append(javaFieldName).append(";\n");
        sb.append("    }\n\n");
    }
    
    /**
     * 生成toString方法
     *
     * @param sb        StringBuilder
     * @param className 类名
     * @param jsonNode  JSON节点
     */
    private static void generateToString(StringBuilder sb, String className, JsonNode jsonNode) {
        sb.append("    /**\n");
        sb.append("     * 重写toString方法\n");
        sb.append("     *\n");
        sb.append("     * @return 字符串表示\n");
        sb.append("     */\n");
        sb.append("    @Override\n");
        sb.append("    public String toString() {\n");
        sb.append("        return \"").append(className).append("{\" +\n");
        
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        boolean isFirst = true;
        
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String fieldName = formatFieldName(entry.getKey());
            
            if (isFirst) {
                sb.append("            \"").append(fieldName).append("='\" + ").append(fieldName).append(" + '\\'' +\n");
                isFirst = false;
            } else {
                sb.append("            \", ").append(fieldName).append("='\" + ").append(fieldName).append(" + '\\'' +\n");
            }
        }
        
        sb.append("            '}';\n");
        sb.append("    }\n");
    }
    
    /**
     * 格式化字段名为有效的Java标识符
     *
     * @param name 原始字段名
     * @return 格式化后的字段名
     */
    private static String formatFieldName(String name) {
        if (name == null || name.isEmpty()) {
            return "field";
        }
        
        // 替换非法字符
        String formattedName = INVALID_CHARS.matcher(name).replaceAll("_");
        
        // 确保第一个字符为小写字母
        if (!Character.isLetter(formattedName.charAt(0)) && formattedName.charAt(0) != '_') {
            formattedName = "f_" + formattedName;
        } else if (Character.isUpperCase(formattedName.charAt(0))) {
            formattedName = Character.toLowerCase(formattedName.charAt(0)) + formattedName.substring(1);
        }
        
        // 处理Java关键字
        if (JAVA_KEYWORDS.contains(formattedName)) {
            formattedName = formattedName + "_";
        }
        
        return formattedName;
    }
    
    /**
     * 格式化类名为有效的Java类名
     *
     * @param name 原始名称
     * @return 格式化后的类名
     */
    private static String formatClassName(String name) {
        if (name == null || name.isEmpty()) {
            return "GeneratedClass";
        }
        
        // 替换非法字符
        String formattedName = INVALID_CHARS.matcher(name).replaceAll("");
        
        // 确保每个单词首字母大写
        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : formattedName.toCharArray()) {
            if (c == '_' || c == ' ' || c == '-') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                sb.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                sb.append(c);
            }
        }
        
        formattedName = sb.toString();
        
        // 确保第一个字符为大写字母
        if (!Character.isLetter(formattedName.charAt(0))) {
            formattedName = "Class" + formattedName;
        } else if (Character.isLowerCase(formattedName.charAt(0))) {
            formattedName = Character.toUpperCase(formattedName.charAt(0)) + formattedName.substring(1);
        }
        
        return formattedName;
    }
    
    /**
     * 获取集合名称的单数形式
     *
     * @param name 集合名称
     * @return 单数形式
     */
    private static String getSingularName(String name) {
        if (name == null || name.isEmpty()) {
            return "item";
        }
        
        // 简单的英语复数规则处理
        if (name.endsWith("ies")) {
            return name.substring(0, name.length() - 3) + "y";
        } else if (name.endsWith("es")) {
            return name.substring(0, name.length() - 2);
        } else if (name.endsWith("s") && !name.endsWith("ss")) {
            return name.substring(0, name.length() - 1);
        }
        
        return name + "Item";
    }
    
    /**
     * 首字母大写
     *
     * @param str 字符串
     * @return 首字母大写的字符串
     */
    private static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * 嵌套类信息
     */
    private static class NestedClassInfo {
        private final String className;
        private final JsonNode jsonNode;
        
        public NestedClassInfo(String className, JsonNode jsonNode) {
            this.className = className;
            this.jsonNode = jsonNode;
        }
    }
}