package org.example.platform.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Registry Service Application
 * Service Discovery using Netflix Eureka
 */
@SpringBootApplication
@EnableEurekaServer
public class RegistryServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(RegistryServiceApplication.class, args);
    }
}
