package com.platform.sqlanalyzer.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SQL脚本配置
 * 负责加载和管理所有SQL脚本资源
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "sql-analyzer")
@Data
public class SqlScriptConfig {

    /**
     * SQL脚本位置列表，支持通配符匹配
     */
    @Value("${sql-analyzer.script-locations:classpath:sql/**/*.sql}")
    private String scriptLocations;

    /**
     * 默认SQL超时时间（毫秒）
     */
    @Value("${sql-analyzer.default-timeout:30000}")
    private int defaultTimeout;

    /**
     * 加载所有SQL脚本并按类别分组
     */
    @Bean
    public Map<String, Map<String, SqlScript>> sqlScripts() throws IOException {
        Map<String, Map<String, SqlScript>> scriptsByCategory = new HashMap<>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(scriptLocations);
        
        log.info("Loading SQL scripts from {}, found {} scripts", scriptLocations, resources.length);
        
        for (Resource resource : resources) {
            String path = resource.getURI().toString();
            String filename = FilenameUtils.getName(path);
            String baseName = FilenameUtils.getBaseName(filename);
            
            // 从路径中提取分类信息，约定使用目录名作为分类
            String category = "default";
            if (path.contains("/sql/")) {
                int sqlIndex = path.indexOf("/sql/") + 5;
                int filenameIndex = path.lastIndexOf("/");
                if (filenameIndex > sqlIndex) {
                    category = path.substring(sqlIndex, filenameIndex);
                    // 处理可能的多级目录
                    if (category.contains("/")) {
                        category = category.substring(category.lastIndexOf("/") + 1);
                    }
                }
            }
            
            // 读取SQL脚本内容
            String content = readResourceContent(resource);
            
            // 解析SQL脚本信息
            SqlScript sqlScript = parseSqlScript(baseName, content);
            
            // 按分类存储
            scriptsByCategory
                .computeIfAbsent(category, k -> new HashMap<>())
                .put(baseName, sqlScript);
            
            log.debug("Loaded SQL script: {}, category: {}, description: {}", 
                    baseName, category, sqlScript.getDescription());
        }
        
        // 打印脚本加载统计
        scriptsByCategory.forEach((category, scripts) -> 
            log.info("Loaded {} scripts in category: {}", scripts.size(), category));
        
        return scriptsByCategory;
    }
    
    /**
     * 读取资源内容
     */
    private String readResourceContent(Resource resource) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
    
    /**
     * 解析SQL脚本，提取元数据和SQL内容
     */
    private SqlScript parseSqlScript(String name, String content) {
        SqlScript script = new SqlScript();
        script.setName(name);
        
        List<String> lines = content.lines().collect(Collectors.toList());
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder descriptionBuilder = new StringBuilder();
        
        // 解析元数据部分（SQL脚本开头的注释部分）
        boolean inMetadata = true;
        Map<String, String> metadata = new HashMap<>();
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            // 检查元数据注释
            if (inMetadata && trimmedLine.startsWith("--")) {
                String metaLine = trimmedLine.substring(2).trim();
                
                // 解析元数据键值对（格式：@key: value）
                if (metaLine.startsWith("@")) {
                    int colonIndex = metaLine.indexOf(":");
                    if (colonIndex > 0) {
                        String key = metaLine.substring(1, colonIndex).trim();
                        String value = metaLine.substring(colonIndex + 1).trim();
                        metadata.put(key, value);
                    }
                } else if (!metaLine.isEmpty()) {
                    // 如果不是键值对，但是有内容，作为描述
                    if (descriptionBuilder.length() > 0) {
                        descriptionBuilder.append("\n");
                    }
                    descriptionBuilder.append(metaLine);
                }
            } else {
                inMetadata = false;
                sqlBuilder.append(line).append("\n");
            }
        }
        
        // 设置脚本属性
        script.setSql(sqlBuilder.toString().trim());
        script.setDescription(descriptionBuilder.toString().trim());
        script.setParameters(parseParameters(metadata.getOrDefault("params", "")));
        
        // 设置超时
        String timeoutStr = metadata.get("timeout");
        if (StringUtils.hasText(timeoutStr)) {
            try {
                script.setTimeout(Integer.parseInt(timeoutStr));
            } catch (NumberFormatException e) {
                log.warn("Invalid timeout value for script {}: {}", name, timeoutStr);
                script.setTimeout(defaultTimeout);
            }
        } else {
            script.setTimeout(defaultTimeout);
        }
        
        // 设置数据库类型
        script.setDatabaseType(metadata.getOrDefault("dbType", "mysql"));
        
        return script;
    }
    
    /**
     * 解析参数定义
     * 格式: paramName1:type1, paramName2:type2
     */
    private List<SqlParameter> parseParameters(String paramsStr) {
        List<SqlParameter> params = new ArrayList<>();
        if (!StringUtils.hasText(paramsStr)) {
            return params;
        }
        
        String[] paramDefs = paramsStr.split(",");
        for (String paramDef : paramDefs) {
            String[] parts = paramDef.trim().split(":");
            if (parts.length >= 1) {
                SqlParameter param = new SqlParameter();
                param.setName(parts[0].trim());
                param.setType(parts.length > 1 ? parts[1].trim() : "string");
                params.add(param);
            }
        }
        
        return params;
    }
    
    /**
     * SQL脚本模型类
     */
    @Data
    public static class SqlScript {
        private String name;
        private String description;
        private String sql;
        private List<SqlParameter> parameters = new ArrayList<>();
        private int timeout;
        private String databaseType;
    }
    
    /**
     * SQL参数模型类
     */
    @Data
    public static class SqlParameter {
        private String name;
        private String type;
    }
}
