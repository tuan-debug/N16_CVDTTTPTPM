package com.lighting.controller;

import com.lighting.entity.Product;
import com.lighting.dto.ProductDto;
import com.lighting.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Validated
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    @GetMapping("/product/all")
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(Map.of(
                "message", "Get all products successfully",
                "products", products
            ));
        } catch (Exception e) {
            log.error("Error getting all products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable String id) {
        try {
            Optional<Product> product = productService.getProductById(id);
            if (product.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "message", "Get product by id successfully",
                    "product", product.get()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Product not found"));
            }
        } catch (Exception e) {
            log.error("Error getting product by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/product/create_product")
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody ProductDto.CreateProductRequest request) {
        try {
            Product product = productService.createProduct(request);
            return ResponseEntity.ok(Map.of(
                "message", "Create product successfully",
                "product", product
            ));
        } catch (Exception e) {
            log.error("Error creating product", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/product/edit_product/{id}")
    public ResponseEntity<Map<String, Object>> editProduct(@PathVariable String id, @Valid @RequestBody ProductDto.UpdateProductRequest request) {
        try {
            Optional<Product> product = productService.updateProduct(id, request);
            if (product.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "message", "Edit product successfully",
                    "product", product.get()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Product not found"));
            }
        } catch (Exception e) {
            log.error("Error updating product with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/product/delete_product/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable String id) {
        try {
            boolean deleted = productService.deleteProduct(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Delete product successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Product not found"));
            }
        } catch (Exception e) {
            log.error("Error deleting product with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(@ModelAttribute ProductDto.ProductSearchRequest request) {
        try {
            List<Product> products = productService.searchProducts(request);
            return ResponseEntity.ok(Map.of(
                "message", "Search products successfully",
                "products", products
            ));
        } catch (Exception e) {
            log.error("Error searching products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Search products failed"));
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getProductsByCategory(@PathVariable String category) {
        try {
            List<Product> products = productService.getProductsByCategory(category);
            return ResponseEntity.ok(Map.of(
                "message", "Get products by category successfully",
                "products", products
            ));
        } catch (Exception e) {
            log.error("Error getting products by category: {}", category, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Get products by category failed"));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Map<String, Object>> getProductsByType(@PathVariable String type) {
        try {
            List<Product> products = productService.getProductsByType(type);
            return ResponseEntity.ok(Map.of(
                "message", "Get products by type successfully",
                "products", products
            ));
        } catch (Exception e) {
            log.error("Error getting products by type: {}", type, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Get products by type failed"));
        }
    }

    @GetMapping("/in-stock")
    public ResponseEntity<Map<String, Object>> getInStockProducts() {
        try {
            List<Product> products = productService.getInStockProducts();
            return ResponseEntity.ok(Map.of(
                "message", "Get in stock products successfully",
                "products", products
            ));
        } catch (Exception e) {
            log.error("Error getting in stock products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Get in stock products failed"));
        }
    }

    @GetMapping("/count/category/{category}")
    public ResponseEntity<Map<String, Object>> getProductCountByCategory(@PathVariable String category) {
        try {
            long count = productService.getProductCountByCategory(category);
            return ResponseEntity.ok(Map.of(
                "message", "Get product count by category successfully",
                "count", count
            ));
        } catch (Exception e) {
            log.error("Error getting product count by category: {}", category, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Get product count by category failed"));
        }
    }
}
