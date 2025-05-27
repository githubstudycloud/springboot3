package com.framework.excel.provider;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.framework.excel.config.DropdownConfig;
import com.framework.excel.dto.DropdownOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 静态下拉数据提供者
 *
 * @author framework
 * @since 1.0.0
 */
@Slf4j
@Component
public class StaticDropdownProvider implements DropdownProvider {

    @Override
    public List<DropdownOption> getOptions(Object... params) {
        if (params == null || params.length == 0 || !(params[0] instanceof DropdownConfig)) {
            log.warn("StaticDropdownProvider requires DropdownConfig parameter");
            return new ArrayList<>();
        }

        DropdownConfig config = (DropdownConfig) params[0];
        String staticOptions = config.getStaticOptions();
        
        if (!StringUtils.hasText(staticOptions)) {
            log.warn("StaticDropdownProvider requires staticOptions configuration");
            return new ArrayList<>();
        }

        try {
            List<DropdownOption> options = new ArrayList<>();
            
            // 如果允许为空，添加空选项
            if (Boolean.TRUE.equals(config.getAllowEmpty())) {
                options.add(new DropdownOption(null, ""));
            }

            // 解析静态选项（支持JSON格式）
            if (staticOptions.startsWith("[")) {
                // JSON数组格式
                List<DropdownOption> staticList = JSON.parseObject(staticOptions, 
                        new TypeReference<List<DropdownOption>>() {});
                options.addAll(staticList);
            } else {
                // 简单字符串格式，用逗号分隔
                String[] items = staticOptions.split(",");
                for (String item : items) {
                    String trimmed = item.trim();
                    if (StringUtils.hasText(trimmed)) {
                        // 支持 "value:label" 格式
                        if (trimmed.contains(":")) {
                            String[] parts = trimmed.split(":", 2);
                            options.add(new DropdownOption(parts[0].trim(), parts[1].trim()));
                        } else {
                            options.add(new DropdownOption(trimmed, trimmed));
                        }
                    }
                }
            }

            log.debug("StaticDropdownProvider loaded {} options", options.size());
            return options;
            
        } catch (Exception e) {
            log.error("Failed to parse static dropdown options: {}", staticOptions, e);
            return new ArrayList<>();
        }
    }

    @Override
    public String getType() {
        return "STATIC";
    }
}