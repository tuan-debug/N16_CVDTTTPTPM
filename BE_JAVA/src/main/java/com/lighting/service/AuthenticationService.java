package com.lighting.service;

/**
 * Authentication service interface for implementing the Decorator pattern
 */
public interface AuthenticationService {
    /**
     * Authenticate a user with email and password
     */
    boolean authenticate(String email, String password);
    
    /**
     * Validate a token
     */
    boolean validateToken(String token);
    
    /**
     * Generate a new token for a user
     */
    String generateToken(String userId, String email, String role);
}
