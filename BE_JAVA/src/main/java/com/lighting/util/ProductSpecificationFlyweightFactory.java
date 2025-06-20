package com.lighting.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Product specifications flyweight factory
 * This implements the Flyweight pattern to efficiently reuse product specifications
 * that are shared among multiple products
 */
public class ProductSpecificationFlyweightFactory {
    
    private static final Map<String, LightingSpecification> lightingSpecifications = new HashMap<>();
    
    // Private constructor to prevent instantiation
    private ProductSpecificationFlyweightFactory() {}
    
    /**
     * Get or create a lighting specification
     * If a specification with the given parameters already exists, reuse it
     * Otherwise, create a new one
     */
    public static LightingSpecification getLightingSpecification(
            String bulbType, int wattage, String colorTemperature, 
            int lumens, int lifeHours, String energyRating) {
        
        // Create a key for the specification
        String key = String.format("%s-%d-%s-%d-%d-%s", 
                bulbType, wattage, colorTemperature, lumens, lifeHours, energyRating);
        
        // Check if the specification already exists
        if (!lightingSpecifications.containsKey(key)) {
            // Create new specification if it doesn't exist
            lightingSpecifications.put(key, 
                    new LightingSpecification(bulbType, wattage, colorTemperature, 
                                          lumens, lifeHours, energyRating));
        }
        
        return lightingSpecifications.get(key);
    }
    
    /**
     * Get the current count of unique specifications
     */
    public static int getSpecificationCount() {
        return lightingSpecifications.size();
    }
    
    /**
     * Clear all cached specifications (mainly for testing purposes)
     */
    public static void clearCache() {
        lightingSpecifications.clear();
    }
}
