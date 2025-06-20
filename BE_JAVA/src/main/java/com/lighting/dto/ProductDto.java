package com.lighting.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private String id;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Product type is required")
    private String type;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private Double price;

    private List<String> colors;

    @NotBlank(message = "Category is required")
    private String category;

    private List<String> images;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be non-negative")
    private Integer stock;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Min(value = 0, message = "Rating must be non-negative")
    private Double rating;

    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Request DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProductRequest {
        @NotBlank(message = "Product name is required")
        private String name;

        @NotBlank(message = "Product type is required")
        private String type;

        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price must be positive")
        private Double price;

        private List<String> colors;

        @NotBlank(message = "Category is required")
        private String category;

        private List<String> images;

        @NotNull(message = "Stock is required")
        @Min(value = 0, message = "Stock must be non-negative")
        private Integer stock;

        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProductRequest {
        private String name;
        private String type;
        private Double price;
        private List<String> colors;
        private String category;
        private List<String> images;
        private Integer stock;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSearchRequest {
        private String name;
        private String category;
        private String type;
        private Double minPrice;
        private Double maxPrice;
        private Boolean inStock;
    }
}
