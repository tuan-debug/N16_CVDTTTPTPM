package com.lighting.util;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    private SecretKey getSigningKey() {
        // For HS512, we need at least 512 bits (64 bytes)
        // Use the provided secret if it's long enough, otherwise generate a secure key
        if (jwtSecret != null && jwtSecret.getBytes().length >= 64) {
            return Keys.hmacShaKeyFor(jwtSecret.getBytes());
        } else {
            // Generate a secure key for HMAC-SHA-512
            return Keys.hmacShaKeyFor(java.security.SecureRandom.getSeed(64));
        }
    }

    public String generateAccessToken(String userId, String email, String role) {
        java.util.Date now = new java.util.Date();
        java.util.Date expiryDate = new java.util.Date(now.getTime() + (2 * 60 * 60 * 1000)); // 2 hours

        return Jwts.builder()
                .claim("_id", userId)
                .claim("email", email)
                .claim("role", role)
                .subject(userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String userId, String email, String role) {
        java.util.Date now = new java.util.Date();
        java.util.Date expiryDate = new java.util.Date(now.getTime() + (7 * 24 * 60 * 60 * 1000L)); // 7 days

        return Jwts.builder()
                .claim("_id", userId)
                .claim("email", email)
                .claim("role", role)
                .subject(userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error parsing JWT token", e);
            throw new RuntimeException("Invalid token");
        }
    }

    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("_id", String.class);
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new java.util.Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
