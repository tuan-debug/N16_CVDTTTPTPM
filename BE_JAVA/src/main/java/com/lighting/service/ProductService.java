package com.lighting.service;

import com.lighting.entity.Product;
import com.lighting.dto.ProductDto;
import com.lighting.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        log.info("Getting all active products");
        return productRepository.findAllActive();
    }

    public Optional<Product> getProductById(String id) {
        log.info("Getting product by id: {}", id);
        return productRepository.findByIdAndDeletedAtIsNull(id);
    }

    public Product createProduct(ProductDto.CreateProductRequest request) {
        log.info("Creating new product: {}", request.getName());

        Product product = new Product();
        product.setName(request.getName());
        product.setType(request.getType());
        product.setPrice(request.getPrice());
        product.setColors(request.getColors());
        product.setCategory(request.getCategory());
        product.setImages(request.getImages());
        product.setStock(request.getStock());
        product.setDescription(request.getDescription());
        product.setQuantity(1);
        product.setRating(0.0);

        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(String id, ProductDto.UpdateProductRequest request) {
        log.info("Updating product with id: {}", id);

        Optional<Product> productOpt = productRepository.findByIdAndDeletedAtIsNull(id);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            if (request.getName() != null) product.setName(request.getName());
            if (request.getType() != null) product.setType(request.getType());
            if (request.getPrice() != null) product.setPrice(request.getPrice());
            if (request.getColors() != null) product.setColors(request.getColors());
            if (request.getCategory() != null) product.setCategory(request.getCategory());
            if (request.getImages() != null) product.setImages(request.getImages());
            if (request.getStock() != null) product.setStock(request.getStock());
            if (request.getDescription() != null) product.setDescription(request.getDescription());

            Product savedProduct = productRepository.save(product);
            return Optional.of(savedProduct);
        }

        return Optional.empty();
    }

    public boolean deleteProduct(String id) {
        log.info("Soft deleting product with id: {}", id);

        Optional<Product> productOpt = productRepository.findByIdAndDeletedAtIsNull(id);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setDeletedAt(LocalDateTime.now());
            productRepository.save(product);
            return true;
        }

        return false;
    }

    public List<Product> getProductsByCategory(String category) {
        log.info("Getting products by category: {}", category);
        return productRepository.findByCategoryAndDeletedAtIsNull(category);
    }

    public List<Product> getProductsByType(String type) {
        log.info("Getting products by type: {}", type);
        return productRepository.findByTypeAndDeletedAtIsNull(type);
    }

    public List<Product> searchProducts(ProductDto.ProductSearchRequest request) {
        log.info("Searching products with criteria: {}", request);

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            return productRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(request.getName());
        }

        if (request.getCategory() != null && request.getType() != null) {
            return productRepository.findByCategoryAndTypeAndDeletedAtIsNull(request.getCategory(), request.getType());
        }

        if (request.getCategory() != null) {
            return productRepository.findByCategoryAndDeletedAtIsNull(request.getCategory());
        }

        if (request.getType() != null) {
            return productRepository.findByTypeAndDeletedAtIsNull(request.getType());
        }

        if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            return productRepository.findByPriceRangeAndDeletedAtIsNull(request.getMinPrice(), request.getMaxPrice());
        }

        if (Boolean.TRUE.equals(request.getInStock())) {
            return productRepository.findAllInStock();
        }

        return productRepository.findAllActive();
    }

    public List<Product> getInStockProducts() {
        log.info("Getting all products in stock");
        return productRepository.findAllInStock();
    }

    public long getProductCountByCategory(String category) {
        log.info("Getting product count for category: {}", category);
        return productRepository.countByCategoryAndDeletedAtIsNull(category);
    }
}
