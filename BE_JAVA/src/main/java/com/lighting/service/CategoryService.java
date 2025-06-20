package com.lighting.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lighting.entity.CompositeProductCategory;
import com.lighting.entity.Product;
import com.lighting.entity.ProductCategory;
import com.lighting.entity.SimpleProductCategory;

/**
 * Service to demonstrate the usage of the Composite Pattern with ProductCategory
 */
@Service
@Transactional
public class CategoryService {

    /**
     * Create a category hierarchy using the Composite pattern
     */
    public ProductCategory createCategoryHierarchy() {
        // Create main lighting category
        CompositeProductCategory lightingCategory = new CompositeProductCategory(
                "Lighting Products",
                "All types of lighting products for your home and office"
        );
        
        // Create subcategories
        CompositeProductCategory indoorLighting = new CompositeProductCategory(
                "Indoor Lighting",
                "Lighting products for indoor use"
        );
        
        CompositeProductCategory outdoorLighting = new CompositeProductCategory(
                "Outdoor Lighting",
                "Lighting products for outdoor use"
        );
        
        // Create leaf categories for indoor lighting
        SimpleProductCategory ceilingLights = new SimpleProductCategory(
                "Ceiling Lights",
                "Lights that mount on ceilings"
        );
        
        SimpleProductCategory tableLamps = new SimpleProductCategory(
                "Table Lamps",
                "Decorative lamps for tables and desks"
        );
        
        SimpleProductCategory wallLights = new SimpleProductCategory(
                "Wall Lights",
                "Lights that mount on walls"
        );
        
        // Create leaf categories for outdoor lighting
        SimpleProductCategory gardenLights = new SimpleProductCategory(
                "Garden Lights",
                "Lights for garden decoration"
        );
        
        SimpleProductCategory securityLights = new SimpleProductCategory(
                "Security Lights",
                "Motion-sensing security lights"
        );
        
        // Add subcategories to the main category
        lightingCategory.addSubcategory(indoorLighting);
        lightingCategory.addSubcategory(outdoorLighting);
        
        // Add leaf categories to indoor category
        indoorLighting.addSubcategory(ceilingLights);
        indoorLighting.addSubcategory(tableLamps);
        indoorLighting.addSubcategory(wallLights);
        
        // Add leaf categories to outdoor category
        outdoorLighting.addSubcategory(gardenLights);
        outdoorLighting.addSubcategory(securityLights);
        
        return lightingCategory;
    }
    
    /**
     * Get total product count across a category and all its subcategories
     */
    public int getTotalProductCount(ProductCategory category) {
        return category.getProductCount();
    }
    
    /**
     * Get all products from a category and its subcategories
     */
    public List<Product> getAllProducts(ProductCategory category) {
        return category.getProducts();
    }
    
    /**
     * Get products by category name, searching through the hierarchy
     */
    public List<Product> getProductsByCategoryName(ProductCategory rootCategory, String categoryName) {
        if (rootCategory.getName().equalsIgnoreCase(categoryName)) {
            return rootCategory.getProducts();
        }
          if (rootCategory instanceof CompositeProductCategory composite) {
            for (ProductCategory subcategory : composite.getSubcategories()) {
                List<Product> products = getProductsByCategoryName(subcategory, categoryName);
                if (!products.isEmpty()) {
                    return products;
                }
            }
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Print the category hierarchy for debugging
     */
    public void printCategoryHierarchy(ProductCategory category, String indent) {
        System.out.println(indent + "- " + category.getName() + " (" + category.getProductCount() + " products)");
          if (category instanceof CompositeProductCategory composite) {
            for (ProductCategory subcategory : composite.getSubcategories()) {
                printCategoryHierarchy(subcategory, indent + "  ");
            }
        }
    }
}
