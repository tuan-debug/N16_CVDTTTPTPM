package com.lighting.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lighting.entity.Account;
import com.lighting.repository.AccountRepository;
import com.lighting.util.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

/**
 * Basic authentication service implementation
 */
@Service
@RequiredArgsConstructor
public class BasicAuthenticationService implements AuthenticationService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    
    @Override
    public boolean authenticate(String email, String password) {
        Account account = accountRepository.findByEmail(email).orElse(null);
        if (account == null) {
            return false;
        }
        
        return passwordEncoder.matches(password, account.getPassword());
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenUtil.validateToken(token);
    }

    @Override
    public String generateToken(String userId, String email, String role) {
        return jwtTokenUtil.generateAccessToken(userId, email, role);
    }
}
