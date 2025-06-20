package com.lighting.controller;

import com.lighting.dto.AccountDto;
import com.lighting.service.UserService;
import com.lighting.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping("/get_detail_user/{id}")
    public ResponseEntity<Map<String, Object>> getDetailUser(@PathVariable String id) {
        try {
            AccountDto user = userService.getDetailUser(id);
            return ResponseEntity.ok(Map.of(
                "message", "Get detail user successfully",
                "user", user
            ));
        } catch (Exception e) {
            log.error("Get detail user failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Get all products failed"));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateUserInfo(
            @PathVariable String id,
            @Valid @RequestBody AccountDto.UpdateUserInfoRequest request) {
        try {
            userService.updateUserInfo(id, request);

            // Get updated user to return
            AccountDto updatedUser = userService.getDetailUser(id);

            return ResponseEntity.ok(Map.of(
                "message", "Update user info successfully",
                "user", updatedUser
            ));
        } catch (Exception e) {
            log.error("Update user info failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Update user info failed"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            String userId = AuthUtil.getCurrentUserId(); // Get from JWT token
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
            }

            List<AccountDto> users = userService.getAllUsers(userId);
            return ResponseEntity.ok(Map.of(
                "message", "Get all users successfully",
                "users", users
            ));
        } catch (Exception e) {
            log.error("Get all users failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Get all users failed"));
        }
    }
}
