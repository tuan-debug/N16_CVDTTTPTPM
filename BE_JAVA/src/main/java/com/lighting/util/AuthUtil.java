package com.lighting.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthUtil {

    /**
     * Get the authenticated user ID from the current request
     * Equivalent to req.user._id in JavaScript
     */
    public static String getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            Object userId = request.getAttribute("userId");
            return userId != null ? userId.toString() : null;
        }
        return null;
    }

    /**
     * Get the authenticated user email from the current request
     */
    public static String getCurrentUserEmail() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            Object userEmail = request.getAttribute("userEmail");
            return userEmail != null ? userEmail.toString() : null;
        }
        return null;
    }

    /**
     * Get the authenticated user role from the current request
     */
    public static String getCurrentUserRole() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            Object userRole = request.getAttribute("userRole");
            return userRole != null ? userRole.toString() : null;
        }
        return null;
    }

    /**
     * Check if current user is admin
     */
    public static boolean isCurrentUserAdmin() {
        String role = getCurrentUserRole();
        return "admin".equalsIgnoreCase(role);
    }

    /**
     * Get current HTTP request
     */
    private static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * Validate that the current user matches the provided user ID
     * Throws exception if not authorized
     */
    public static void validateUserAccess(String userId) {
        String currentUserId = getCurrentUserId();
        if (currentUserId == null || !currentUserId.equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
    }

    /**
     * Validate that the current user is admin or matches the provided user ID
     * Throws exception if not authorized
     */
    public static void validateUserOrAdminAccess(String userId) {
        if (!isCurrentUserAdmin()) {
            validateUserAccess(userId);
        }
    }
}
