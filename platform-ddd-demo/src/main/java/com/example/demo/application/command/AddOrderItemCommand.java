package com.example.demo.application.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 添加订单项命令
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrderItemCommand {
    @NotEmpty(message = "订单ID不能为空")
    private String orderId;
    
    @NotEmpty(message = "产品ID不能为空")
    private String productId;
    
    @Min(value = 1, message = "数量必须大于0")
    private int quantity;
    
    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;
}
