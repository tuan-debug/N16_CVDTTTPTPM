package com.lighting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lighting.demo.DesignPatternDemonstrator;

import java.util.Map;

/**
 * Controller to demonstrate design patterns
 */
@RestController
@RequestMapping("/api/demo")
public class DesignPatternController {

    @Autowired
    private DesignPatternDemonstrator demonstrator;
    
    /**
     * Run a demonstration of all the implemented design patterns
     */
    @GetMapping("/patterns")
    public ResponseEntity<Map<String, Object>> demonstrateDesignPatterns() {
        try {
            demonstrator.runDemo();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Design pattern demonstration completed successfully. Check server logs for details.",
                "patterns", new String[] {
                    "Singleton", "Decorator", "Flyweight", "Adapter", "Bridge", "Composite"
                }
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error during design pattern demonstration: " + e.getMessage()
            ));
        }
    }
}
