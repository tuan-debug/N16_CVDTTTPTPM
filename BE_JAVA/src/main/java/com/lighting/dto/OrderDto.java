package com.lighting.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String id;
    private String userId;
    private String paymentId;
    private String paymentMethod;
    private List<OrderItemDto> items;
    private Double totalPrice;
    private OrderAddressDto address;
    private String status;
    private Boolean isPaid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Create Order Request DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOrderRequest {
        @NotNull(message = "Payment method is required")
        private String paymentMethod;

        @NotNull(message = "Items are required")
        @Valid
        private List<OrderItemDto> items;

        @NotNull(message = "Total price is required")
        @Min(value = 0, message = "Total price must be positive")
        private Double totalPrice;

        @NotNull(message = "Address is required")
        @Valid
        private OrderAddressDto address;
    }

    // Update Order Request DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateOrderRequest {
        private String status;
        private Boolean isPaid;
        private String paymentId;
    }

    // Order Item DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        @NotBlank(message = "Product ID is required")
        private String _id;

        @NotBlank(message = "Product type is required")
        private String type;

        @NotBlank(message = "Product name is required")
        private String name;

        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price must be positive")
        private Double price;

        private String color;
        private String category;
        private List<String> images;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }

    // Order Address DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderAddressDto {
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

    // Get All Orders Request DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetAllOrdersRequest {
        private Integer page = 1;
        private Integer limit = 10;
    }
}
