package com.framework.excel.provider;

import com.framework.excel.config.DropdownConfig;
import com.framework.excel.dto.DropdownOption;
import com.framework.excel.mapper.DynamicMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 关联表下拉数据提供者
 *
 * @author framework
 * @since 1.0.0
 */
@Slf4j
@Component
public class RelatedTableDropdownProvider implements DropdownProvider {

    @Autowired
    private DynamicMapper dynamicMapper;

    @Override
    public List<DropdownOption> getOptions(Object... params) {
        if (params == null || params.length == 0 || !(params[0] instanceof DropdownConfig)) {
            log.warn("RelatedTableDropdownProvider requires DropdownConfig parameter");
            return new ArrayList<>();
        }

        DropdownConfig config = (DropdownConfig) params[0];
        
        try {
            List<Map<String, Object>> results = dynamicMapper.selectDropdownOptions(
                    config.getTableName(),
                    config.getValueField(),
                    config.getDisplayField(),
                    config.getWhereClause()
            );

            List<DropdownOption> options = new ArrayList<>();
            
            // 如果允许为空，添加空选项
            if (Boolean.TRUE.equals(config.getAllowEmpty())) {
                options.add(new DropdownOption(null, ""));
            }

            // 转换查询结果为下拉选项
            for (Map<String, Object> result : results) {
                Object value = result.get("value");
                String label = String.valueOf(result.get("label"));
                
                if (value != null && StringUtils.hasText(label)) {
                    options.add(new DropdownOption(value, label));
                }
            }

            log.debug("RelatedTableDropdownProvider loaded {} options from table: {}", 
                     options.size(), config.getTableName());
            
            return options;
            
        } catch (Exception e) {
            log.error("Failed to load dropdown options from table: {}", config.getTableName(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public String getType() {
        return "RELATED_TABLE";
    }
}