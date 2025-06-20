package com.lighting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lighting.storage.MongoOrderStorage;
import com.lighting.storage.OrderStorage;

@Configuration
public class StorageConfig {
    @Bean
    public OrderStorage orderStorage(MongoOrderStorage mongoOrderStorage) {
        return mongoOrderStorage; // Có thể thay bằng MySqlOrderStorage
    }
}