package com.lighting.repository;

import com.lighting.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {

    // Find by email
    Optional<Account> findByEmail(String email);

    // Find by phone
    Optional<Account> findByPhone(String phone);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if phone exists
    boolean existsByPhone(String phone);

    // Find by role
    List<Account> findByRole(Account.Role role);

    // Find active accounts (not blocked)
    List<Account> findByIsBlockedFalse();

    // Find blocked accounts
    List<Account> findByIsBlockedTrue();

    // Find by email and password (for authentication)
    Optional<Account> findByEmailAndPassword(String email, String password);

    // Find accounts with orders
    @Query("{'orders': {$exists: true, $not: {$size: 0}}}")
    List<Account> findAccountsWithOrders();

    // Find accounts with favorites
    @Query("{'favorites': {$exists: true, $not: {$size: 0}}}")
    List<Account> findAccountsWithFavorites();

    // Search accounts by name
    @Query("{'$or': [{'firstName': {$regex: ?0, $options: 'i'}}, {'lastName': {$regex: ?0, $options: 'i'}}]}")
    List<Account> findByNameContainingIgnoreCase(String name);

    // Find by province
    List<Account> findByProvince(String province);

    // Count by role
    long countByRole(Account.Role role);
}
