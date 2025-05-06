package com.platform.report.domain.model.template;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板样式
 * 定义报表的全局样式配置
 */
@Getter
public class TemplateStyle {
    
    private ColorScheme colorScheme;
    private FontFamily fontFamily;
    private Map<String, String> cssVariables;
    private Map<String, Object> themeProperties;
    
    public TemplateStyle(ColorScheme colorScheme, FontFamily fontFamily) {
        this.colorScheme = colorScheme;
        this.fontFamily = fontFamily;
        this.cssVariables = new HashMap<>();
        this.themeProperties = new HashMap<>();
    }
    
    /**
     * 添加CSS变量
     */
    public void addCssVariable(String name, String value) {
        this.cssVariables.put(name, value);
    }
    
    /**
     * 添加主题属性
     */
    public void addThemeProperty(String name, Object value) {
        this.themeProperties.put(name, value);
    }
    
    /**
     * 颜色方案
     */
    @Getter
    public static class ColorScheme {
        private final String primary;
        private final String secondary;
        private final String accent;
        private final String background;
        private final String text;
        
        public ColorScheme(String primary, String secondary, String accent, 
                         String background, String text) {
            this.primary = primary;
            this.secondary = secondary;
            this.accent = accent;
            this.background = background;
            this.text = text;
        }
    }
    
    /**
     * 字体家族
     */
    @Getter
    public static class FontFamily {
        private final String headings;
        private final String body;
        private final String monospace;
        
        public FontFamily(String headings, String body, String monospace) {
            this.headings = headings;
            this.body = body;
            this.monospace = monospace;
        }
    }
}
