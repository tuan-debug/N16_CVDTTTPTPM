package com.lighting.service;

import java.util.Random;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lighting.dto.AccountDto;
import com.lighting.entity.Account;
import com.lighting.repository.AccountRepository;
import com.lighting.util.EmailComposite;
import com.lighting.util.EmailService;
import com.lighting.util.JwtTokenUtil;
import com.lighting.util.RedisService;
import com.lighting.util.VerificationEmail;
import com.lighting.util.WelcomeEmail;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AccessService {

    private static final Logger log = LoggerFactory.getLogger(AccessService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final RedisService redisService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    public void signUp(AccountDto.SignUpRequest request) {
        log.info("Sign up attempt for email: {}", request.getEmail());

        String emailSentKey = redisService.pinVerifyKey(request.getEmail()) + ":sent";
        if (redisService.get(emailSentKey) != null) {
            throw new RuntimeException("Verification email already sent. Please check your inbox.");
        }

        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new RuntimeException("Invalid email");
        }

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (accountRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already exists");
        }

        String code = getRandomSixDigit();

        // Use EmailComposite to send multiple emails
        EmailComposite emailComposite = new EmailComposite(request.getEmail());
        emailComposite.addComponent(new VerificationEmail(request.getEmail(), code));
        emailComposite.addComponent(new WelcomeEmail(request.getEmail()));
        emailService.sendEmailComponent(emailComposite);

        redisService.set(redisService.pinVerifyKey(request.getEmail()), code, 60);
        redisService.set(emailSentKey, "true", 60);

        redisService.set(redisService.pinVerifyKey(request.getEmail()) + ":firstName", request.getFirstName(), 6000);
        redisService.set(redisService.pinVerifyKey(request.getEmail()) + ":lastName", request.getLastName(), 6000);
        redisService.set(redisService.pinVerifyKey(request.getEmail()) + ":password", passwordEncoder.encode(request.getPassword()), 6000);
        redisService.set(redisService.pinVerifyKey(request.getEmail()) + ":phone", request.getPhone(), 6000);
        redisService.set(redisService.pinVerifyKey(request.getEmail()) + ":gender", request.getGender(), 6000);
        redisService.set(redisService.pinVerifyKey(request.getEmail()) + ":addressLine1", request.getAddressLine1(), 6000);
        redisService.set(redisService.pinVerifyKey(request.getEmail()) + ":province", request.getProvince(), 6000);
        redisService.set(redisService.pinVerifyKey(request.getEmail()) + ":district", request.getDistrict(), 6000);
        redisService.set(redisService.pinVerifyKey(request.getEmail()) + ":ward", request.getWard(), 6000);
        redisService.set(redisService.pinVerifyKey(request.getEmail()) + ":postalCode", request.getPostalCode(), 6000);
        redisService.set(redisService.pinVerifyKey(request.getEmail()) + ":addressNote", request.getAddressNote(), 6000);
        redisService.set(redisService.pinVerifyKey(request.getEmail()) + ":defaultAddress", String.valueOf(request.getDefaultAddress()), 6000);

        log.info("Verification and welcome emails sent to: {}", request.getEmail());
    }

    public void resetPassword(AccountDto.ResetPasswordRequest request) {
        log.info("Reset password attempt for email: {}", request.getEmail());

        String code = getRandomSixDigit();
        String subject = "Reset Password";
        String text = "Your code is: " + code;

        emailService.sendEmail(subject, text, request.getEmail());

        redisService.set(redisService.pinVerifyKey(request.getEmail()), code, 60);

        log.info("Reset password email sent to: {}", request.getEmail());
    }
    

    public Account verifyEmail(AccountDto.VerifyEmailRequest request) {
        log.info("Email verification attempt for: {}", request.getEmail());

        String storedCode = redisService.get(redisService.pinVerifyKey(request.getEmail()));

        if (storedCode == null || !storedCode.equals(request.getCode())) {
            throw new RuntimeException("Invalid code");
        }

        // Delete verification code from Redis
        redisService.delete(redisService.pinVerifyKey(request.getEmail()));
        
        // Get stored user information from Redis
        String storedPassword = redisService.get(redisService.pinVerifyKey(request.getEmail()) + ":password");
        String firstName = redisService.get(redisService.pinVerifyKey(request.getEmail()) + ":firstName");
        String lastName = redisService.get(redisService.pinVerifyKey(request.getEmail()) + ":lastName");
        String phone = redisService.get(redisService.pinVerifyKey(request.getEmail()) + ":phone");
        String gender = redisService.get(redisService.pinVerifyKey(request.getEmail()) + ":gender");
        String addressLine1 = redisService.get(redisService.pinVerifyKey(request.getEmail()) + ":addressLine1");
        String province = redisService.get(redisService.pinVerifyKey(request.getEmail()) + ":province");
        String district = redisService.get(redisService.pinVerifyKey(request.getEmail()) + ":district");
        String ward = redisService.get(redisService.pinVerifyKey(request.getEmail()) + ":ward");
        String postalCode = redisService.get(redisService.pinVerifyKey(request.getEmail()) + ":postalCode");
        String addressNote = redisService.get(redisService.pinVerifyKey(request.getEmail()) + ":addressNote");
        String defaultAddressStr = redisService.get(redisService.pinVerifyKey(request.getEmail()) + ":defaultAddress");
        
        if (storedPassword == null || firstName == null || lastName == null || phone == null) {
            throw new RuntimeException("Registration session expired. Please sign up again.");
        }
        
        // Delete all stored user data from Redis
        redisService.delete(redisService.pinVerifyKey(request.getEmail()) + ":password");
        redisService.delete(redisService.pinVerifyKey(request.getEmail()) + ":firstName");
        redisService.delete(redisService.pinVerifyKey(request.getEmail()) + ":lastName");
        redisService.delete(redisService.pinVerifyKey(request.getEmail()) + ":phone");
        redisService.delete(redisService.pinVerifyKey(request.getEmail()) + ":gender");
        redisService.delete(redisService.pinVerifyKey(request.getEmail()) + ":addressLine1");
        redisService.delete(redisService.pinVerifyKey(request.getEmail()) + ":province");
        redisService.delete(redisService.pinVerifyKey(request.getEmail()) + ":district");
        redisService.delete(redisService.pinVerifyKey(request.getEmail()) + ":ward");
        redisService.delete(redisService.pinVerifyKey(request.getEmail()) + ":postalCode");
        redisService.delete(redisService.pinVerifyKey(request.getEmail()) + ":addressNote");
        redisService.delete(redisService.pinVerifyKey(request.getEmail()) + ":defaultAddress");

        // Create new account with all user information
        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setPassword(passwordEncoder.encode(storedPassword));
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setPhone(phone);
        
        // Set optional fields if they exist
        if (gender != null && !gender.isEmpty()) {
            account.setGender(Account.Gender.valueOf(gender.toUpperCase()));
        }
        account.setAddressLine1(addressLine1);
        account.setProvince(province);
        account.setDistrict(district);
        account.setWard(ward);
        account.setPostalCode(postalCode);
        account.setAddressNote(addressNote);
        account.setDefaultAddress("true".equals(defaultAddressStr));
        
        // Set default values for other fields
        account.setRole(Account.Role.USER);
        account.setIsBlocked(false);

        Account savedAccount = accountRepository.save(account);

        log.info("Email verified and account created for: {}", request.getEmail());
        return savedAccount;
    }

    public AccountDto.AuthResponse signIn(AccountDto.SignInRequest request) {
        log.info("Sign in attempt for email: {}", request.getEmail());

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate tokens
        String accessToken = jwtTokenUtil.generateAccessToken(
                account.getId(),
                account.getEmail(),
                account.getRole().getValue()
        );
        String refreshToken = jwtTokenUtil.generateRefreshToken(
                account.getId(),
                account.getEmail(),
                account.getRole().getValue()
        );

        // Store refresh token in Redis
        String refreshKey = redisService.refreshTokenKey(account.getId());
        redisService.delete(refreshKey); // Delete existing token
        redisService.set(refreshKey, refreshToken);

        // Create response
        AccountDto accountDto = mapToDto(account);
        AccountDto.AuthResponse response = new AccountDto.AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUser(accountDto);

        log.info("Sign in successful for: {}", request.getEmail());
        return response;
    }

    public AccountDto.AuthResponse refreshToken(AccountDto.RefreshTokenRequest request) {
        log.info("Refresh token attempt");

        String refreshToken = request.getRefreshToken();

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("Unauthorized");
        }

        // Validate token
        if (!jwtTokenUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        if (jwtTokenUtil.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh token expired");
        }

        String userId = jwtTokenUtil.getUserIdFromToken(refreshToken);
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify stored refresh token
        String storedToken = redisService.get(redisService.refreshTokenKey(userId));
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Generate new tokens
        String newAccessToken = jwtTokenUtil.generateAccessToken(
                account.getId(),
                account.getEmail(),
                account.getRole().getValue()
        );
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(
                account.getId(),
                account.getEmail(),
                account.getRole().getValue()
        );

        // Update stored refresh token
        redisService.set(redisService.refreshTokenKey(userId), newRefreshToken, 7 * 24 * 60 * 60); // 7 days

        AccountDto.AuthResponse response = new AccountDto.AuthResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);

        log.info("Refresh token successful for user: {}", userId);
        return response;
    }

    public void signOut(String refreshToken, Account account) {
        log.info("Sign out attempt for user: {}", account.getId());

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("Unauthorized");
        }

        if (!jwtTokenUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String tokenUserId = jwtTokenUtil.getUserIdFromToken(refreshToken);

        if (!tokenUserId.equals(account.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        String storedToken = redisService.get(redisService.refreshTokenKey(account.getId()));
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Delete refresh token from Redis
        redisService.delete(redisService.refreshTokenKey(account.getId()));

        log.info("Sign out successful for user: {}", account.getId());
    }



    public void verifyPin(AccountDto.VerifyPinRequest request) {
        log.info("Pin verification attempt for email: {}", request.getEmail());

        String storedCode = redisService.get(redisService.pinVerifyKey(request.getEmail()));

        if (storedCode == null) {
            throw new RuntimeException("Code expired");
        }

        if (!storedCode.equals(request.getCode())) {
            throw new RuntimeException("Invalid code");
        }

        redisService.delete(redisService.pinVerifyKey(request.getEmail()));

        log.info("Pin verified successfully for email: {}", request.getEmail());
    }

    public void forgotPassword(AccountDto.ForgotPasswordRequest request) {
        log.info("Forgot password attempt for email: {}", request.getEmail());

        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        log.info("Password updated successfully for email: {}", request.getEmail());
    }

    private String getRandomSixDigit() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    private AccountDto mapToDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setFirstName(account.getFirstName());
        dto.setLastName(account.getLastName());
        dto.setEmail(account.getEmail());
        dto.setPhone(account.getPhone());
        dto.setGender(account.getGender() != null ? account.getGender().getValue() : null);
        dto.setAddressLine1(account.getAddressLine1());
        dto.setProvince(account.getProvince());
        dto.setDistrict(account.getDistrict());
        dto.setWard(account.getWard());
        dto.setPostalCode(account.getPostalCode());
        dto.setAddressNote(account.getAddressNote());
        dto.setDefaultAddress(account.getDefaultAddress());
        dto.setRole(account.getRole() != null ? account.getRole().getValue() : null);
        dto.setIsBlocked(account.getIsBlocked());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());
        return dto;
    }
}
