package com.lighting.demo;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lighting.entity.ProductCategory;
import com.lighting.notification.AccountNotification;
import com.lighting.notification.EmailNotificationSender;
import com.lighting.notification.NotificationSender;
import com.lighting.notification.OrderNotification;
import com.lighting.notification.SmsNotificationSender;
import com.lighting.payment.PaymentProcessorAdapter;
import com.lighting.service.AuthenticationService;
import com.lighting.service.CategoryService;
import com.lighting.util.LightingSpecification;
import com.lighting.util.ProductSpecificationFlyweightFactory;

/**
 * Helper class to demonstrate the use of multiple design patterns together
 */
@Component
public class DesignPatternDemonstrator {

    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private AuthenticationService authService;
    
    /**
     * Run a demonstration of all the design patterns working together
     */
    public void runDemo() {

        // 2. Decorator Pattern - Authentication Service with Logging and Caching
        System.out.println("\n2. DECORATOR PATTERN");
        System.out.println("AuthenticationService is decorated with LoggingAuthenticationDecorator and CachingAuthenticationDecorator");
        System.out.println("When authenticate() is called, it will log timing information and cache token validation results");
        String demoToken = authService.generateToken("user123", "user@example.com", "user");
        System.out.println("Generated token: " + demoToken.substring(0, 15) + "...");
        boolean isValid = authService.validateToken(demoToken);
        System.out.println("Token validation result: " + isValid);
        // Second validation should use the cache
        isValid = authService.validateToken(demoToken);
        System.out.println("Second validation (using cache): " + isValid);
        
        // 3. Flyweight Pattern - Product Specifications
        System.out.println("\n3. FLYWEIGHT PATTERN");
        System.out.println("Creating multiple products with shared specifications...");
        // Create product specifications using the flyweight factory
        LightingSpecification ledSpec = ProductSpecificationFlyweightFactory.getLightingSpecification(
                "LED", 9, "Cool White", 800, 25000, "A+");
        LightingSpecification ledSpec2 = ProductSpecificationFlyweightFactory.getLightingSpecification(
                "LED", 9, "Cool White", 800, 25000, "A+");
        LightingSpecification halogenSpec = ProductSpecificationFlyweightFactory.getLightingSpecification(
                "Halogen", 40, "Warm White", 600, 2000, "C");
        
        System.out.println("LED Specification: " + ledSpec.getSpecificationText());
        System.out.println("Halogen Specification: " + halogenSpec.getSpecificationText());
        System.out.println("Total unique specifications created: " + ProductSpecificationFlyweightFactory.getSpecificationCount());
        System.out.println("Both LED specs reference the same object: " + (ledSpec == ledSpec2));
        
        // 4. Adapter Pattern - Payment Processing
        System.out.println("\n4. ADAPTER PATTERN");
        System.out.println("Using the PaymentProcessorAdapter to interface with a third-party payment system...");
        PaymentProcessorAdapter paymentAdapter = new PaymentProcessorAdapter();
        String orderId = "ORDER-12345";
        boolean paymentResult = paymentAdapter.processPayment(
                orderId, new BigDecimal("129.99"), "credit_card", 
                "4111111111111111", "123", "12/25");
        System.out.println("Payment result: " + (paymentResult ? "Success" : "Failed"));
        String paymentStatus = paymentAdapter.getPaymentStatus(orderId);
        System.out.println("Payment status: " + paymentStatus);
        
        // 5. Bridge Pattern - Notifications
        System.out.println("\n5. BRIDGE PATTERN");
        System.out.println("Using Bridge pattern to separate notification types from delivery methods...");
        
        // Create different notification senders (implementations)
        NotificationSender emailSender = new EmailNotificationSender();
        NotificationSender smsSender = new SmsNotificationSender();
        
        // Create different notification types (abstractions) with different implementations
        OrderNotification orderEmailNotification = new OrderNotification(emailSender);
        OrderNotification orderSmsNotification = new OrderNotification(smsSender);
        AccountNotification accountEmailNotification = new AccountNotification(emailSender);
        
        // Demonstrate sending notifications
        System.out.println("Sending order confirmation via email...");
        orderEmailNotification.sendOrderConfirmation("user@example.com", "ORDER-12345", 129.99);
        
        System.out.println("Sending shipping notification via SMS...");
        orderSmsNotification.sendShippingNotification("+1234567890", "ORDER-12345", "TRACK-9876543");
        
        System.out.println("Sending welcome email...");
        accountEmailNotification.sendWelcome("newuser@example.com", "John");
        
        // Change the implementation at runtime
        System.out.println("Changing notification sender from email to SMS at runtime...");
        accountEmailNotification.setSender(smsSender);
        accountEmailNotification.sendPasswordReset("+1234567890", "123456");
        
        // 6. Composite Pattern - Product Categories
        System.out.print("\n6. COMPOSITE PATTERN");
        System.out.println("Creating product category hierarchy using Composite pattern...");
        
        ProductCategory rootCategory = categoryService.createCategoryHierarchy();
        System.out.println("Category hierarchy created. Printing structure:");
        categoryService.printCategoryHierarchy(rootCategory, "");
        
        System.out.println("\n===== DEMONSTRATION COMPLETE =====\n");
    }
}
