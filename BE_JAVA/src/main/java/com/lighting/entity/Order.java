package com.lighting.entity;

import com.lighting.util.ColorConfig;
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
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    private String paymentId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Items are required")
    @Valid
    private List<OrderItem> items;

    @NotNull(message = "Total price is required")
    @Min(value = 0, message = "Total price must be positive")
    private Double totalPrice;

    @NotNull(message = "Address is required")
    @Valid
    private OrderAddress address;

    private OrderStatus status;

    private Boolean isPaid = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderAddress {
        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Phone is required")
        private String phone;

        @NotBlank(message = "Address line is required")
        private String addressLine1;

        @NotBlank(message = "Province is required")
        private String province;

        @NotBlank(message = "District is required")
        private String district;

        @NotBlank(message = "Ward is required")
        private String ward;

        @NotBlank(message = "Postal code is required")
        private String postalCode;

        @NotBlank(message = "Gender is required")
        private String gender;

        private String addressNote = "";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        @NotBlank(message = "Product ID is required")
        private String productId;

        @NotBlank(message = "Product type is required")
        private String type;

        @NotBlank(message = "Product name is required")
        private String name;

        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price must be positive")
        private Double price;

        private ColorConfig color; // Flyweight

        private String category;
        private List<String> images;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }

    public enum PaymentMethod {
        COD, TRANSFER
    }

    public enum OrderStatus {
        PENDING_PAYMENT("pending_payment"),
        PAID_PENDING_CONFIRMATION("paid_pending_confirmation"),
        PENDING_CONFIRMATION("pending_confirmation"),
        CONFIRMED("confirmed"),
        SHIPPING("shipping"),
        COMPLETED("completed"),
        CANCELLED("cancelled");

        private final String value;

        OrderStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public void setDefaultStatus() {
        if (this.paymentMethod == PaymentMethod.COD) {
            this.status = OrderStatus.PENDING_CONFIRMATION;
        } else {
            this.status = OrderStatus.PENDING_PAYMENT;
        }
    }
}