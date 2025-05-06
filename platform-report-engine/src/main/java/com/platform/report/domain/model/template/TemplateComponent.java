package com.platform.report.domain.model.template;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 模板组件
 * 定义报表中的可视化组件，如表格、图表、文本等
 */
@Getter
public class TemplateComponent {
    
    private final String id;
    private String name;
    private ComponentType type;
    private Position position;
    private Size size;
    private Map<String, Object> properties;
    private String dataBinding; // 数据绑定表达式
    
    /**
     * 创建新的模板组件
     */
    public static TemplateComponent create(String name, ComponentType type, 
                                         Position position, Size size, 
                                         Map<String, Object> properties,
                                         String dataBinding) {
        TemplateComponent component = new TemplateComponent();
        component.name = name;
        component.type = type;
        component.position = position;
        component.size = size;
        component.properties = properties != null ? properties : new HashMap<>();
        component.dataBinding = dataBinding;
        
        return component;
    }
    
    /**
     * 更新组件属性
     */
    public void updateProperties(Map<String, Object> properties) {
        this.properties.clear();
        if (properties != null) {
            this.properties.putAll(properties);
        }
    }
    
    /**
     * 更新数据绑定
     */
    public void updateDataBinding(String dataBinding) {
        this.dataBinding = dataBinding;
    }
    
    /**
     * 更新位置和大小
     */
    public void updateLayout(Position position, Size size) {
        this.position = position;
        this.size = size;
    }
    
    public String getId() {
        return id;
    }
    
    // 私有构造函数
    private TemplateComponent() {
        this.id = UUID.randomUUID().toString();
        this.properties = new HashMap<>();
    }
    
    /**
     * 组件类型枚举
     */
    public enum ComponentType {
        TABLE,    // 表格
        CHART,    // 图表
        TEXT,     // 文本
        IMAGE,    // 图片
        HEADER,   // 页眉
        FOOTER,   // 页脚
        GROUP,    // 分组
        SUBREPORT // 子报表
    }
    
    /**
     * 位置信息
     */
    @Getter
    public static class Position {
        private final int x;
        private final int y;
        
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    
    /**
     * 大小信息
     */
    @Getter
    public static class Size {
        private final int width;
        private final int height;
        
        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}
