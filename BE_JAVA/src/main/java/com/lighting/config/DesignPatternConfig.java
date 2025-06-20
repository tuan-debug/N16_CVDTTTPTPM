package com.lighting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.lighting.service.AuthenticationService;
import com.lighting.service.BasicAuthenticationService;
import com.lighting.service.CachingAuthenticationDecorator;
import com.lighting.service.LoggingAuthenticationDecorator;
import com.lighting.util.RedisService;

/**
 * Configuration class for design pattern implementations
 */
@Configuration
public class DesignPatternConfig {

    /**
     * Configure the authentication service with decorators
     * This demonstrates the Decorator pattern in Spring's DI container
     */
    @Bean
    @Primary
    public AuthenticationService authenticationService(
            BasicAuthenticationService basicAuthService, 
            RedisService redisService) {
        
        // First, decorate with logging
        LoggingAuthenticationDecorator loggingDecorator = 
                new LoggingAuthenticationDecorator(basicAuthService);
        
        // Then, further decorate with caching
        return new CachingAuthenticationDecorator(loggingDecorator, redisService);
    }
}
