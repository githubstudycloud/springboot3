package com.platform.report.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import com.platform.report.infrastructure.config.AsyncConfig;

/**
 * 报表引擎模块配置
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.platform.report"})
@Import(AsyncConfig.class)
public class ReportEngineConfig implements WebMvcConfigurer {
    
    /**
     * 配置对象映射器
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
    
    /**
     * 配置资源处理器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/");
    }
    
    /**
     * 配置OpenAPI
     */
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Report Engine API")
                        .description("Platform Report Engine API Documentation")
                        .version("v1.0.0")
                        .license(new License().name("Proprietary").url("https://example.com")));
    }
}
