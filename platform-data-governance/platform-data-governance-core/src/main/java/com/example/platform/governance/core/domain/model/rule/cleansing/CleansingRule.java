package com.example.platform.governance.core.domain.model.rule.cleansing;

import com.example.platform.governance.core.domain.model.rule.Rule;

/**
 * 数据清洗规则接口
 * 
 * 定义数据清洗规则的行为
 */
public interface CleansingRule extends Rule {
    
    /**
     * 应用清洗规则到特定字段的值
     * 
     * @param fieldValue 字段值
     * @param context 清洗上下文
     * @return 清洗结果
     */
    CleansingResult apply(Object fieldValue, CleansingContext context);
    
    /**
     * 获取规则应用的目标字段
     * 
     * @return 目标字段名
     */
    String getTargetField();
    
    /**
     * 检查规则是否可以应用于指定类型的值
     * 
     * @param valueClass 值的类型
     * @return 是否可应用
     */
    boolean canApplyTo(Class<?> valueClass);
}
