package com.lighting.entity;

import java.util.List;

/**
 * Common interface for product categories
 * This is the component interface in the Composite pattern
 */
public interface ProductCategory {
    /**
     * Get the name of the category
     */
    String getName();
    
    /**
     * Get the description of the category
     */
    String getDescription();
    
    /**
     * Get the products in this category
     */
    List<Product> getProducts();
    
    /**
     * Add a product to this category
     */
    void addProduct(Product product);
    
    /**
     * Remove a product from this category
     */
    void removeProduct(Product product);
    
    /**
     * Get the number of products in this category (including subcategories)
     */
    int getProductCount();
}
