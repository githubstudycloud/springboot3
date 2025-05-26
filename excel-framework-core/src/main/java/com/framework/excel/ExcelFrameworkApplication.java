package com.framework.excel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

/**
 * Excel Framework 应用程序启动类
 *
 * @author Framework
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.framework.excel.mapper")
public class ExcelFrameworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExcelFrameworkApplication.class, args);
    }
}
