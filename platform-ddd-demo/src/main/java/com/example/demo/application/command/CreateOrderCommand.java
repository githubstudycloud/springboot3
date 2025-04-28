package com.example.demo.application.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;

/**
 * 创建订单命令
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderCommand {
    @NotEmpty(message = "客户ID不能为空")
    private String customerId;
}
