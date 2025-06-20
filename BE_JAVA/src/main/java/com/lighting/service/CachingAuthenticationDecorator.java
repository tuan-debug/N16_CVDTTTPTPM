package com.lighting.service;

import org.springframework.stereotype.Service;

import com.lighting.util.RedisService;

/**
 * A decorator for AuthenticationService that adds caching functionality
 * Follows the Decorator pattern to extend functionality without changing the original service
 */
@Service
public class CachingAuthenticationDecorator implements AuthenticationService {
    
    private final AuthenticationService authService;
    private final RedisService redisService;
    
    public CachingAuthenticationDecorator(LoggingAuthenticationDecorator authService, RedisService redisService) {
        this.authService = authService;
        this.redisService = redisService;
    }
    
    @Override
    public boolean authenticate(String email, String password) {
        // Authentication should not be cached for security reasons
        return authService.authenticate(email, password);
    }

    @Override
    public boolean validateToken(String token) {
        // Check cache first
        String cacheKey = "token_validation:" + token;
        String cachedResult = redisService.get(cacheKey);
        
        if (cachedResult != null) {
            return Boolean.parseBoolean(cachedResult);
        }
        
        // If not in cache, perform validation and store result
        boolean result = authService.validateToken(token);
        
        // Cache result for 5 minutes (300 seconds)
        redisService.set(cacheKey, String.valueOf(result), 300);
        
        return result;
    }

    @Override
    public String generateToken(String userId, String email, String role) {
        // Token generation is unique each time and should not be cached
        return authService.generateToken(userId, email, role);
    }
}
