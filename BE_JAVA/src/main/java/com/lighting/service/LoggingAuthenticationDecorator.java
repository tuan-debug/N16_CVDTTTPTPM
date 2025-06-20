package com.lighting.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * A decorator for AuthenticationService that adds logging functionality
 * Follows the Decorator pattern to extend functionality without changing the original service
 */
@Service
public class LoggingAuthenticationDecorator implements AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(LoggingAuthenticationDecorator.class);
    
    private final AuthenticationService authService;
    
    public LoggingAuthenticationDecorator(BasicAuthenticationService authService) {
        this.authService = authService;
    }
    
    @Override
    public boolean authenticate(String email, String password) {
        log.info("Authentication attempt for email: {}", email);
        long startTime = System.currentTimeMillis();
        
        boolean result = authService.authenticate(email, password);
        
        long endTime = System.currentTimeMillis();
        log.info("Authentication for {} completed in {}ms with result: {}", 
                email, (endTime - startTime), result ? "success" : "failure");
        
        return result;
    }

    @Override
    public boolean validateToken(String token) {
        log.info("Token validation attempt");
        long startTime = System.currentTimeMillis();
        
        boolean result = authService.validateToken(token);
        
        long endTime = System.currentTimeMillis();
        log.info("Token validation completed in {}ms with result: {}", 
                (endTime - startTime), result ? "valid" : "invalid");
        
        return result;
    }

    @Override
    public String generateToken(String userId, String email, String role) {
        log.info("Token generation for user: {}, email: {}, role: {}", userId, email, role);
        long startTime = System.currentTimeMillis();
        
        String result = authService.generateToken(userId, email, role);
        
        long endTime = System.currentTimeMillis();
        log.info("Token generation completed in {}ms", (endTime - startTime));
        
        return result;
    }
}
