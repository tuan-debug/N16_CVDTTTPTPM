package com.lighting.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private PaymentStatus status = PaymentStatus.PENDING;

    private String transactionId;

    private String bankCode;

    private String description;

    private String hexKey;

    private LocalDateTime paidAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Enums
    public enum PaymentMethod {
        TRANSFER("TRANSFER"),
        COD("COD"),
        MOMO("MOMO"),
        VNPAY("VNPAY");

        private final String value;

        PaymentMethod(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PaymentStatus {
        PENDING("pending"),
        COMPLETED("completed"),
        FAILED("failed"),
        CANCELLED("cancelled");

        private final String value;

        PaymentStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
