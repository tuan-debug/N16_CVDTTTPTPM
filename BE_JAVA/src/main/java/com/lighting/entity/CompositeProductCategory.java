package com.lighting.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Composite category implementation containing subcategories
 * This is the composite in the Composite pattern
 */
@Document(collection = "product_categories")
public class CompositeProductCategory implements ProductCategory {
    
    @Id
    private String id;
    
    private String name;
    private String description;
    
    @DBRef
    private List<Product> products = new ArrayList<>();
    
    @DBRef
    private List<ProductCategory> subcategories = new ArrayList<>();
    
    public CompositeProductCategory() {
    }
    
    public CompositeProductCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<Product> getProducts() {
        List<Product> allProducts = new ArrayList<>(products);
        
        // Also include products from subcategories
        for (ProductCategory subcategory : subcategories) {
            allProducts.addAll(subcategory.getProducts());
        }
        
        return allProducts;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public void addProduct(Product product) {
        if (!products.contains(product)) {
            products.add(product);
        }
    }

    @Override
    public void removeProduct(Product product) {
        products.remove(product);
    }

    /**
     * Get the direct subcategories
     */
    public List<ProductCategory> getSubcategories() {
        return new ArrayList<>(subcategories);
    }

    public void setSubcategories(List<ProductCategory> subcategories) {
        this.subcategories = subcategories;
    }
    
    /**
     * Add a subcategory
     */
    public void addSubcategory(ProductCategory subcategory) {
        if (!subcategories.contains(subcategory)) {
            subcategories.add(subcategory);
        }
    }
    
    /**
     * Remove a subcategory
     */
    public void removeSubcategory(ProductCategory subcategory) {
        subcategories.remove(subcategory);
    }

    @Override
    public int getProductCount() {
        int count = products.size();
        
        // Add the count of products in subcategories
        for (ProductCategory subcategory : subcategories) {
            count += subcategory.getProductCount();
        }
        
        return count;
    }
}
