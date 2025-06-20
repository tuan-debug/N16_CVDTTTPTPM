package com.lighting.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private String id;
    private String userId;
    private String orderId;
    private Double amount;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private String bankCode;
    private String description;
    private String hexKey;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Check Transaction Request DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckTransactionRequest {
        @NotBlank(message = "Transaction ID is required")
        private String transactionId;

        @NotNull(message = "Amount is required")
        @Min(value = 0, message = "Amount must be positive")
        private Double amount;
    }

    // Create Transaction Request DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTransactionRequest {
        @NotNull(message = "Amount is required")
        @Min(value = 0, message = "Amount must be positive")
        private Double amount;

        private String description;
        private String orderId;
    }

    // Transaction Response DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionResponse {
        private String id;
        private String userId;
        private Double amount;
        private String status;
        private String hexKey;
        private String description;
        private LocalDateTime createdAt;
    }
}
