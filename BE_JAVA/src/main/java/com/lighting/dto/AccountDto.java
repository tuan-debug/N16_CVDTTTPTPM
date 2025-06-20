package com.lighting.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
    private String addressLine1;
    private String province;
    private String district;
    private String ward;
    private String postalCode;
    private String addressNote;
    private Boolean defaultAddress;
    private List<String> orders;
    private List<String> favorites;
    private List<String> cart;
    private String role;
    private Boolean isBlocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Sign Up Request DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpRequest {
        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        private String email;

        @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
        @NotBlank(message = "Phone number is required")
        private String phone;

        @Size(min = 8, message = "Password must be at least 8 characters")
        @NotBlank(message = "Password is required")
        private String password;

        private String gender;

        @NotBlank(message = "Address line is required")
        private String addressLine1;

        @NotBlank(message = "Province is required")
        private String province;

        @NotBlank(message = "District is required")
        private String district;

        @NotBlank(message = "Ward is required")
        private String ward;

        private String postalCode;
        private String addressNote;
        private Boolean defaultAddress = false;
    }

    // Sign In Request DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignInRequest {
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    // Refresh Token Request DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenRequest {
        @NotBlank(message = "Refresh token is required")
        private String refreshToken;
    }

    // Reset Password Request DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetPasswordRequest {
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Old password is required")
        private String oldPassword;

        @Size(min = 8, message = "New password must be at least 8 characters")
        @NotBlank(message = "New password is required")
        private String newPassword;
    }

    // Forgot Password Request DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForgotPasswordRequest {
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        private String email;

        @Size(min = 8, message = "New password must be at least 8 characters")
        @NotBlank(message = "New password is required")
        private String newPassword;

        @NotBlank(message = "Verification code is required")
        private String code;
    }

    // Verify Email Request DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyEmailRequest {
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Verification code is required")
        private String code;
    }

    // Verify PIN Request DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyPinRequest {
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "PIN code is required")
        private String code;
    }

    // Update User Info Request DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserInfoRequest {
        private String firstName;
        private String lastName;
        private String phone;
        private String gender;
        private String addressLine1;
        private String province;
        private String district;
        private String ward;
        private String postalCode;
        private String addressNote;
        private Boolean defaultAddress;
    }

    // Authentication Response DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private AccountDto user;
    }
}
