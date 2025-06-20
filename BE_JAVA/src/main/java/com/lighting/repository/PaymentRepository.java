package com.lighting.repository;

import com.lighting.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    // Find payments by user ID
    List<Payment> findByUserId(String userId);

    // Find payments by order ID
    Optional<Payment> findByOrderId(String orderId);

    // Find payments by status
    List<Payment> findByStatus(Payment.PaymentStatus status);

    // Find payments by payment method
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);

    // Find payments by transaction ID
    Optional<Payment> findByTransactionId(String transactionId);

    // Find payments within date range
    @Query("{'createdAt': {$gte: ?0, $lte: ?1}}")
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);


    // Find payments by amount range
    @Query("{'amount': {$gte: ?0, $lte: ?1}}")
    List<Payment> findByAmountBetween(Double minAmount, Double maxAmount);

    // Count payments by status
    long countByStatus(Payment.PaymentStatus status);

    // Count payments by payment method
    long countByPaymentMethod(Payment.PaymentMethod paymentMethod);

    // Find successful payments by user
    @Query("{'userId': ?0, 'status': 'completed'}")
    List<Payment> findSuccessfulPaymentsByUser(String userId);

    // Calculate total successful payment amount
    @Query(value = "{'status': 'completed'}", fields = "{'amount': 1}")
    List<Payment> findAllSuccessfulPaymentAmounts();
}
