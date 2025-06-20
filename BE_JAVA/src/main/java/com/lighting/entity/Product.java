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
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Product")
public class Product {

    @Id
    private String id;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Product type is required")
    private String type; // product-selling, product-rental

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private Double price;

    private List<String> colors;

    @NotBlank(message = "Category is required")
    private String category;

    private List<String> images;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be non-negative")
    private Integer stock = 0;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;

    @Min(value = 0, message = "Rating must be non-negative")
    private Double rating = 0.0;

    private String description;

    private LocalDateTime deletedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Validation for product type
    public enum ProductType {
        PRODUCT_SELLING("product-selling"),
        PRODUCT_RENTAL("product-rental");

        private final String value;

        ProductType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
