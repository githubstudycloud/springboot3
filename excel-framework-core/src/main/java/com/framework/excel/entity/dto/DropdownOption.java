package com.framework.excel.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 下拉选项DTO
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DropdownOption {
    private String value;
    private String label;
}
