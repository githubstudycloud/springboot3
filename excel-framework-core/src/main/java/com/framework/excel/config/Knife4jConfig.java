package com.framework.excel.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * Knife4j配置类
 * 
 * @author Excel Framework Team
 * @since 1.0.0
 */
@Configuration
@EnableSwagger2WebMvc
@EnableKnife4j
public class Knife4jConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("Excel Framework API")
                        .description("动态Excel导入导出框架API文档")
                        .version("1.0.0")
                        .contact(new Contact("Excel Framework Team", "", "team@framework.com"))
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.framework.excel.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}
