package com.platform.registry;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Registry Application - Entry point for the Service Registry.
 * 
 * This service provides service discovery and monitoring for all microservices.
 * It combines Eureka Server and Spring Boot Admin functionality.
 */
@SpringBootApplication
@EnableEurekaServer
@EnableAdminServer
public class RegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegistryApplication.class, args);
    }
}
