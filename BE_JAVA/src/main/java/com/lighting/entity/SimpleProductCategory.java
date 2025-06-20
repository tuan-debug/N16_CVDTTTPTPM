package com.lighting.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Simple category implementation (leaf in the Composite pattern)
 */
@Document(collection = "product_categories")
public class SimpleProductCategory implements ProductCategory {
    
    @Id
    private String id;
    
    private String name;
    private String description;
    
    @DBRef
    private List<Product> products = new ArrayList<>();
    
    public SimpleProductCategory() {
    }
    
    public SimpleProductCategory(String name, String description) {
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
        return new ArrayList<>(products);
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

    @Override
    public int getProductCount() {
        return products.size();
    }
}
