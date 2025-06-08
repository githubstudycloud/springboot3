package com.platform.domain.config.template.valueobject;

/**
 * 模板类型枚举
 * 定义配置模板的不同类型
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public enum TemplateType {
    
    /**
     * YAML配置模板
     */
    YAML("YAML配置", "application.yml", "yaml"),
    
    /**
     * Properties配置模板
     */
    PROPERTIES("Properties配置", "application.properties", "properties"),
    
    /**
     * JSON配置模板
     */
    JSON("JSON配置", "config.json", "json"),
    
    /**
     * XML配置模板
     */
    XML("XML配置", "config.xml", "xml"),
    
    /**
     * 环境变量模板
     */
    ENV("环境变量", ".env", "env"),
    
    /**
     * Docker Compose模板
     */
    DOCKER_COMPOSE("Docker Compose", "docker-compose.yml", "yml"),
    
    /**
     * Kubernetes配置模板
     */
    KUBERNETES("Kubernetes配置", "deployment.yaml", "yaml"),
    
    /**
     * Nginx配置模板
     */
    NGINX("Nginx配置", "nginx.conf", "conf"),
    
    /**
     * 数据库配置模板
     */
    DATABASE("数据库配置", "database.yml", "yml"),
    
    /**
     * 自定义模板
     */
    CUSTOM("自定义模板", "custom.txt", "txt");
    
    private final String displayName;
    private final String defaultFileName;
    private final String fileExtension;
    
    TemplateType(String displayName, String defaultFileName, String fileExtension) {
        this.displayName = displayName;
        this.defaultFileName = defaultFileName;
        this.fileExtension = fileExtension;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDefaultFileName() {
        return defaultFileName;
    }
    
    public String getFileExtension() {
        return fileExtension;
    }
    
    /**
     * 检查是否为结构化配置类型
     */
    public boolean isStructured() {
        return this == YAML || this == JSON || this == XML;
    }
    
    /**
     * 检查是否为容器相关配置
     */
    public boolean isContainerConfig() {
        return this == DOCKER_COMPOSE || this == KUBERNETES;
    }
    
    /**
     * 获取内容类型
     */
    public String getContentType() {
        return switch (this) {
            case YAML, DOCKER_COMPOSE, KUBERNETES -> "application/x-yaml";
            case JSON -> "application/json";
            case XML -> "application/xml";
            case PROPERTIES -> "text/plain";
            case ENV -> "text/plain";
            case NGINX -> "text/plain";
            case DATABASE -> "application/x-yaml";
            case CUSTOM -> "text/plain";
        };
    }
} 