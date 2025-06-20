package com.lighting.service;

import com.lighting.entity.Payment;
import com.lighting.entity.Order;
import com.lighting.dto.PaymentDto;
import com.lighting.repository.PaymentRepository;
import com.lighting.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public Payment createTransaction(String userId, Double amount) {
        log.info("Creating transaction for user: {} with amount: {}", userId, amount);

        try {
            String hexKey = generateKey(15);

            Payment transaction = new Payment();
            transaction.setUserId(userId);
            transaction.setAmount(amount);
            transaction.setHexKey(hexKey);
            transaction.setDescription(createDescription(hexKey));
            transaction.setStatus(Payment.PaymentStatus.PENDING);

            Payment savedTransaction = paymentRepository.save(transaction);

            log.info("Transaction created with ID: {} and hex key: {}", savedTransaction.getId(), hexKey);
            return savedTransaction;

        } catch (Exception e) {
            log.error("Error creating transaction: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteTransaction(String userId, String transactionId) {
        log.info("Deleting transaction: {} for user: {}", transactionId, userId);

        try {
            Payment transaction = paymentRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));

            log.info("Transaction user ID: {}, requesting user ID: {}", transaction.getUserId(), userId);
            if (!transaction.getUserId().equals(userId)) {
                throw new RuntimeException("Unauthorized");
            }

            paymentRepository.deleteById(transactionId);

            log.info("Transaction deleted successfully: {}", transactionId);

        } catch (Exception e) {
            log.error("Error deleting transaction: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, String> checkTransaction(String userId, String transactionId, String hexKey, Double amount) {
        log.info("Checking transaction: {} for user: {}", transactionId, userId);

        try {
            String status = "pending";

            Payment transaction = paymentRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));

            if (!transaction.getUserId().equals(userId)) {
                throw new RuntimeException("Unauthorized");
            }

            if (!transaction.getHexKey().equals(hexKey)) {
                throw new RuntimeException("Invalid transaction");
            }

            // TODO: Implement MBBank integration for checking transaction history
            // For now, simulating the check logic
            // const transactions = await MBBank.getTransactionsHistory();
            boolean transactionFound = checkBankTransactionHistory(hexKey, amount);

            if (transactionFound) {
                status = "success";

                // Update order status
                orderRepository.findByPaymentId(transactionId).ifPresent(order -> {
                    order.setStatus(Order.OrderStatus.PAID_PENDING_CONFIRMATION);
                    orderRepository.save(order);
                });

                // Update payment status
                transaction.setStatus(Payment.PaymentStatus.COMPLETED);
                paymentRepository.save(transaction);

                log.info("Transaction verified successfully: {}", transactionId);
                return Map.of("status", "success");
            }

            log.info("Transaction still pending: {}", transactionId);
            return Map.of("status", status);

        } catch (Exception e) {
            log.error("Error in checkTransaction: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public Payment getTransactionById(String userId, String id) {
        log.info("Getting transaction by ID: {} for user: {}", id, userId);

        try {
            Payment transaction = paymentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Transactions not found"));

            log.info("Transaction found: {}", transaction);

            if (!transaction.getUserId().equals(userId)) {
                throw new RuntimeException("Unauthorized");
            }

            return transaction;

        } catch (Exception e) {
            log.error("Error getting transaction: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    // Helper method to generate hex key matching Node.js paymentHelper.geneKey(15)
    private String generateKey(int length) {
        String characters = "0123456789ABCDEF";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString();
    }

    // Helper method to create description matching Node.js paymentHelper.createDescription(hex_key)
    private String createDescription(String hexKey) {
        return "Payment transaction: " + hexKey;
    }

    // Placeholder for MBBank integration matching Node.js MBBank.getTransactionsHistory()
    private boolean checkBankTransactionHistory(String hexKey, Double amount) {
        // TODO: Implement actual bank integration
        // For now, returning false (transaction not found in bank history)
        // In the Node.js version, this checks:
        // for (const t of transactions) {
        //     if (t.transactionDesc.includes(hex_key) && Number(t.creditAmount) === Number(amount)) {
        //         return true;
        //     }
        // }
        return false;
    }
}
