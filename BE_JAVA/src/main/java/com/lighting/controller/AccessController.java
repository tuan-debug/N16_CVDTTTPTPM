package com.lighting.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lighting.dto.AccountDto;
import com.lighting.entity.Account;
import com.lighting.service.AccessService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Validated
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class AccessController {

    private static final Logger log = LoggerFactory.getLogger(AccessController.class);
    private final AccessService accessService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@Valid @RequestBody AccountDto.SignUpRequest request) {
        try {
            accessService.signUp(request);
            return ResponseEntity.ok(Map.of(
                "message", "Sign up successfully let verify your email"
            ));
        } catch (Exception e) {
            log.error("Sign up failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Sign up failed"));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signIn(
            @Valid @RequestBody AccountDto.SignInRequest request,
            HttpServletResponse response) {
        try {
            AccountDto.AuthResponse authResponse = accessService.signIn(request);

            // Set refresh token as HTTP-only cookie
            Cookie refreshTokenCookie = new Cookie("refreshToken", authResponse.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(Map.of(
                "message", "Sign in successfully",
                "refreshToken", authResponse.getRefreshToken(),
                "accessToken", authResponse.getAccessToken(),
                "user", authResponse.getUser()
            ));
        } catch (Exception e) {
            log.error("Sign in failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Sign in failed"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String refreshToken = getRefreshTokenFromCookies(request);

            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh token not found"));
            }

            AccountDto.RefreshTokenRequest refreshRequest = new AccountDto.RefreshTokenRequest();
            refreshRequest.setRefreshToken(refreshToken);

            AccountDto.AuthResponse authResponse = accessService.refreshToken(refreshRequest);

            // Set new refresh token as HTTP-only cookie
            Cookie refreshTokenCookie = new Cookie("refreshToken", authResponse.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(Map.of(
                "message", "Refresh token successfully",
                "accessToken", authResponse.getAccessToken()
            ));
        } catch (Exception e) {
            log.error("Refresh token failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Refresh token failed"));
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<Map<String, Object>> signOut(
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String refreshToken = getRefreshTokenFromCookies(request);

            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
            }

            // Get user ID from JWT token
            String userId = com.lighting.util.AuthUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
            }

            // Create account object with authenticated user ID
            Account account = new Account();
            account.setId(userId);

            accessService.signOut(refreshToken, account);

            // Clear refresh token cookie
            Cookie refreshTokenCookie = new Cookie("refreshToken", "");
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(0);
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(Map.of(
                "message", "Sign out successfully"
            ));
        } catch (Exception e) {
            log.error("Sign out failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Sign out failed"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@Valid @RequestBody AccountDto.ResetPasswordRequest request) {
        try {
            accessService.resetPassword(request);
            return ResponseEntity.ok(Map.of(
                "message", "Reset password successfully"
            ));
        } catch (Exception e) {
            log.error("Reset password failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Reset password failed"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@Valid @RequestBody AccountDto.ForgotPasswordRequest request) {
        try {
            accessService.forgotPassword(request);
            return ResponseEntity.ok(Map.of(
                "message", "Change password successfully"
            ));
        } catch (Exception e) {
            log.error("Change password failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Change password failed"));
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@Valid @RequestBody AccountDto.VerifyEmailRequest request) {
        try {
            Account account = accessService.verifyEmail(request);
            return ResponseEntity.ok(Map.of(
                "message", "Verify email successfully",
                "account", account
            ));
        } catch (Exception e) {
            log.error("Verify email failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Verify email failed"));
        }
    }

    @PostMapping("/verify_pin")
    public ResponseEntity<Map<String, Object>> verifyPin(@Valid @RequestBody AccountDto.VerifyPinRequest request) {
        try {
            accessService.verifyPin(request);
            return ResponseEntity.ok(Map.of(
                "message", "Verify pin successfully"
            ));
        } catch (Exception e) {
            log.error("Verify pin failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Verify pin failed"));
        }
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
