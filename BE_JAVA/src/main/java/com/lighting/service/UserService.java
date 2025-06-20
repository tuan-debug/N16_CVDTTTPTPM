package com.lighting.service;

import com.lighting.entity.Account;
import com.lighting.dto.AccountDto;
import com.lighting.repository.AccountRepository;
import com.lighting.util.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final AccountRepository accountRepository;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public AccountDto getDetailUser(String id) {
        log.info("Getting user details for ID: {}", id);

        if (id == null || id.isEmpty()) {
            throw new RuntimeException("Id is required");
        }

        try {
            // Check cache first
            String cachedUser = redisService.get(userProfileKey(id));
            if (cachedUser != null) {
                log.info("User found in cache for ID: {}", id);
                return objectMapper.readValue(cachedUser, AccountDto.class);
            }

            // Find user in database
            Account user = accountRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            AccountDto userDto = mapToDto(user);

            // Cache the result for 1 hour (3600 seconds)
            redisService.set(userProfileKey(id), objectMapper.writeValueAsString(userDto), 3600);

            log.info("User details retrieved and cached for ID: {}", id);
            return userDto;

        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for user: {}", id, e);
            throw new RuntimeException("Internal Server Error");
        } catch (Exception e) {
            log.error("Error in getDetailUser: {}", e.getMessage(), e);
            throw new RuntimeException("Internal Server Error");
        }
    }

    public void updateUserInfo(String id, AccountDto.UpdateUserInfoRequest request) {
        log.info("Updating user info for ID: {}", id);

        if (id == null || id.isEmpty()) {
            throw new RuntimeException("Id is required");
        }

        try {
            // Check if user exists
            Account user = accountRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update user fields
            if (request.getFirstName() != null) {
                user.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null) {
                user.setLastName(request.getLastName());
            }
            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }
            if (request.getGender() != null) {
                user.setGender(Account.Gender.valueOf(request.getGender().toUpperCase()));
            }
            if (request.getAddressLine1() != null) {
                user.setAddressLine1(request.getAddressLine1());
            }
            if (request.getProvince() != null) {
                user.setProvince(request.getProvince());
            }
            if (request.getDistrict() != null) {
                user.setDistrict(request.getDistrict());
            }
            if (request.getWard() != null) {
                user.setWard(request.getWard());
            }
            if (request.getPostalCode() != null) {
                user.setPostalCode(request.getPostalCode());
            }
            if (request.getAddressNote() != null) {
                user.setAddressNote(request.getAddressNote());
            }
            if (request.getDefaultAddress() != null) {
                user.setDefaultAddress(request.getDefaultAddress());
            }

            // Save updated user
            accountRepository.save(user);

            // Clear cache
            redisService.delete(userProfileKey(id));
            redisService.delete(allUsersKey());

            log.info("User info updated successfully for ID: {}", id);

        } catch (Exception e) {
            log.error("Error in updateUserInfo: {}", e.getMessage(), e);
            throw new RuntimeException("Internal Server Error");
        }
    }

    public List<AccountDto> getAllUsers(String userId) {
        log.info("Getting all users for admin user ID: {}", userId);

        try {
            // Check if requesting user exists and is admin
            Account account = accountRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (account.getRole() != Account.Role.ADMIN) {
                throw new RuntimeException("Permission denied");
            }

            // Check cache first
            String cachedUsers = redisService.get(allUsersKey());
            if (cachedUsers != null) {
                log.info("All users found in cache");
                try {
                    List<AccountDto> users = objectMapper.readValue(cachedUsers,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, AccountDto.class));
                    return users;
                } catch (JsonProcessingException e) {
                    log.error("Error parsing cached users, fetching from database", e);
                }
            }

            // Get all users from database
            List<Account> users = accountRepository.findAll();
            if (users.isEmpty()) {
                throw new RuntimeException("Users not found");
            }

            List<AccountDto> userDtos = users.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());

            // Cache the result for 1 hour (3600 seconds)
            try {
                redisService.set(allUsersKey(), objectMapper.writeValueAsString(userDtos), 3600);
            } catch (JsonProcessingException e) {
                log.error("Error caching users", e);
            }

            log.info("All users retrieved and cached. Count: {}", userDtos.size());
            return userDtos;

        } catch (Exception e) {
            log.error("Error in getAllUsers: {}", e.getMessage(), e);
            throw new RuntimeException("Internal Server Error");
        }
    }

    private String userProfileKey(String userId) {
        return "user:profile:" + userId;
    }

    private String allUsersKey() {
        return "users:all";
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
