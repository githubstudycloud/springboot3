package com.platform.config.security;

import com.platform.config.security.jwt.JwtAuthenticationEntryPoint;
import com.platform.config.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * OAuth2安全配置
 * 提供JWT认证、RBAC权限控制和API安全保护
 *
 * @author Platform Team
 * @since 2.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class OAuth2SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 主要安全过滤器链
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
                // 认证端点
                .requestMatchers("/api/auth/**").permitAll()
                
                // 健康检查和监控端点
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/prometheus").permitAll()
                .requestMatchers("/actuator/metrics").hasRole("MONITOR")
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                
                // API文档
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // 配置查询 - 只读权限
                .requestMatchers(HttpMethod.GET, "/api/config/**").hasAnyRole("USER", "ADMIN", "CONFIG_READ")
                
                // 配置管理 - 写权限
                .requestMatchers(HttpMethod.POST, "/api/config/**").hasAnyRole("ADMIN", "CONFIG_WRITE")
                .requestMatchers(HttpMethod.PUT, "/api/config/**").hasAnyRole("ADMIN", "CONFIG_WRITE")
                .requestMatchers(HttpMethod.DELETE, "/api/config/**").hasRole("ADMIN")
                
                // 版本控制
                .requestMatchers(HttpMethod.GET, "/api/versions/**").hasAnyRole("USER", "ADMIN", "CONFIG_READ")
                .requestMatchers(HttpMethod.POST, "/api/versions/**").hasAnyRole("ADMIN", "CONFIG_WRITE")
                .requestMatchers("/api/versions/*/activate").hasAnyRole("ADMIN", "CONFIG_WRITE")
                .requestMatchers("/api/versions/*/rollback").hasRole("ADMIN")
                
                // 审计日志 - 仅管理员
                .requestMatchers("/api/audit/**").hasRole("ADMIN")
                
                // 响应式端点
                .requestMatchers("/api/reactive/**").hasAnyRole("USER", "ADMIN", "CONFIG_READ")
                
                // 管理员端点
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // 其他API需要认证
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 传统配置端点安全过滤器链
     */
    @Bean
    @Order(2)
    public SecurityFilterChain configFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**")
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
                // 健康检查
                .requestMatchers("/health").permitAll()
                
                // H2控制台 - 仅开发环境
                .requestMatchers("/h2-console/**").hasRole("ADMIN")
                
                // Spring Cloud Config端点
                .requestMatchers("/{application}/{profile}").hasAnyRole("USER", "ADMIN", "CONFIG_READ")
                .requestMatchers("/{application}/{profile}/{label}").hasAnyRole("USER", "ADMIN", "CONFIG_READ")
                .requestMatchers("/{application}-{profile}.properties").hasAnyRole("USER", "ADMIN", "CONFIG_READ")
                .requestMatchers("/{application}-{profile}.yml").hasAnyRole("USER", "ADMIN", "CONFIG_READ")
                .requestMatchers("/{application}-{profile}.yaml").hasAnyRole("USER", "ADMIN", "CONFIG_READ")
                .requestMatchers("/{label}/{application}-{profile}.properties").hasAnyRole("USER", "ADMIN", "CONFIG_READ")
                .requestMatchers("/{label}/{application}-{profile}.yml").hasAnyRole("USER", "ADMIN", "CONFIG_READ")
                .requestMatchers("/{label}/{application}-{profile}.yaml").hasAnyRole("USER", "ADMIN", "CONFIG_READ")
                
                // 加密/解密端点
                .requestMatchers("/encrypt").hasAnyRole("ADMIN", "CONFIG_WRITE")
                .requestMatchers("/decrypt").hasAnyRole("ADMIN", "CONFIG_WRITE")
                
                // 刷新端点
                .requestMatchers("/refresh").hasAnyRole("ADMIN", "CONFIG_WRITE")
                
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:*",
            "https://*.platform.com",
            "https://*.platform.local"
        ));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", 
            "Accept", "Origin", "Access-Control-Request-Method",
            "Access-Control-Request-Headers", "X-Client-Version",
            "X-Trace-Id", "X-User-Agent"
        ));
        
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials",
            "X-Total-Count", "X-Current-Page", "X-Page-Size"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 预检请求缓存时间

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
} 