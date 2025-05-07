package com.example.platform.governance.metadata.infrastructure.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * MongoDB配置类
 */
@Configuration
public class MongoDBConfig extends AbstractMongoClientConfiguration {
    
    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017}")
    private String connectionString;
    
    @Value("${spring.data.mongodb.database:metadata}")
    private String databaseName;
    
    @Override
    protected String getDatabaseName() {
        return databaseName;
    }
    
    @Override
    public MongoClient mongoClient() {
        ConnectionString connString = new ConnectionString(connectionString);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .build();
        
        return MongoClients.create(settings);
    }
    
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}
