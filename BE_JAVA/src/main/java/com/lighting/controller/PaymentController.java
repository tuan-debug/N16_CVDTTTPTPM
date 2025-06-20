package com.lighting.controller;

import com.lighting.entity.Payment;
import com.lighting.dto.PaymentDto;
import com.lighting.service.PaymentService;
import com.lighting.util.RedisService;
import com.lighting.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Validated
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final RedisService redisService;

    @PostMapping("/ispaid")
    public ResponseEntity<Map<String, Object>> checkTransaction(@Valid @RequestBody PaymentDto.CheckTransactionRequest request) {
        try {
            String userId = AuthUtil.getCurrentUserId(); // Get from JWT token
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
            }

            String hexKey = redisService.get("transaction:" + request.getTransactionId());
            Map<String, String> result = paymentService.checkTransaction(userId, request.getTransactionId(), hexKey, request.getAmount());

            return ResponseEntity.ok(Map.of(
                "message", "Check transaction successfully",
                "status", result.get("status")
            ));
        } catch (Exception e) {
            log.error("Check transaction failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Check transaction failed"));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createTransaction(@Valid @RequestBody PaymentDto.CreateTransactionRequest request) {
        try {
            String userId = AuthUtil.getCurrentUserId(); // Get from JWT token
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
            }

            Payment newTransaction = paymentService.createTransaction(userId, request.getAmount());

            if (newTransaction == null) {
                throw new RuntimeException("Create transaction failed");
            }

            // Set Redis key like in Node.js
            String redisKey = "transaction:" + newTransaction.getId();
            redisService.set(redisKey, newTransaction.getHexKey());

            return ResponseEntity.ok(Map.of(
                "message", "Create transaction successfully",
                "transaction", newTransaction
            ));
        } catch (Exception e) {
            log.error("Create transaction failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Create transaction failed"));
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteTransaction(@PathVariable String id) {
        try {
            String userId = AuthUtil.getCurrentUserId(); // Get from JWT token
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
            }

            paymentService.deleteTransaction(userId, id);

            return ResponseEntity.ok(Map.of(
                "message", "Delete transaction successfully"
            ));
        } catch (Exception e) {
            log.error("Delete transaction failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Delete transaction failed"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTransactionById(@PathVariable String id) {
        try {
            String userId = AuthUtil.getCurrentUserId(); // Get from JWT token
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
            }

            Payment transactions = paymentService.getTransactionById(userId, id);

            return ResponseEntity.ok(Map.of(
                "message", "Get transactions successfully",
                "transactions", transactions
            ));
        } catch (Exception e) {
            log.error("Get transactions failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Get transactions failed"));
        }
    }
}
