package com.lighting.config;

import com.lighting.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenUtil jwtTokenUtil;

    // List of public endpoints that don't require authentication
    private static final Set<String> PUBLIC_PATHS = new HashSet<>(Arrays.asList(
            "/signup",
            "/signin",
            "/refresh-token",
            "/reset-password",
            "/forgot-password",
            "/verify-email",
            "/verify_pin"
    ));

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Skip authentication for public endpoints
        boolean isPublicEndpoint = PUBLIC_PATHS.contains(path) ||
                path.startsWith("/uploads/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                path.startsWith("/actuator/");

        if (isPublicEndpoint) {
            log.debug("Skipping authentication for public endpoint: {}", path);
        }

        return isPublicEndpoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String userId = null;
        String jwtToken = null;

        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                Claims claims = jwtTokenUtil.getClaimsFromToken(jwtToken);
                userId = claims.get("_id", String.class);
                String email = claims.get("email", String.class);
                String role = claims.get("role", String.class);

                log.debug("JWT Token validated for user: {}, email: {}, role: {}", userId, email, role);

                // Create authentication object
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Validate token
                    if (!jwtTokenUtil.isTokenExpired(jwtToken)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Add user info to request attributes (similar to req.user in Node.js)
                        request.setAttribute("userId", userId);
                        request.setAttribute("userEmail", email);
                        request.setAttribute("userRole", role);

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("Security context set for user: {}", userId);
                    } else {
                        log.warn("JWT Token has expired");
                    }
                }
            } catch (ExpiredJwtException e) {
                log.warn("JWT Token has expired: {}", e.getMessage());
            } catch (Exception e) {
                log.warn("Unable to get JWT Token or JWT Token has expired: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"message\":\"Unauthorized - Invalid or expired token\"}");
                response.setContentType("application/json");
                return;
            }
        } else {
            log.debug("JWT Token does not begin with Bearer String");
        }

        filterChain.doFilter(request, response);
    }
}
