package com.framework.excel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Excel框架主启动类
 * 
 * @author framework
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.framework.excel.mapper")
public class ExcelFrameworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExcelFrameworkApplication.class, args);
        System.out.println("\n==================================");
        System.out.println("  Excel Framework 启动成功!");
        System.out.println("  API文档地址: http://localhost:8080/swagger-ui.html");
        System.out.println("==================================\n");
    }
}