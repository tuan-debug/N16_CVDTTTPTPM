package com.lighting.payment;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Third-party payment processor interface
 * This represents a legacy or external payment system with an incompatible interface
 */
public interface ThirdPartyPaymentProcessor {
    
    /**
     * Process payment with the third-party system
     */
    boolean processPayment(String merchantId, BigDecimal amount, String currency, Map<String, String> paymentInfo);
    
    /**
     * Verify payment status in the third-party system
     */
    String checkPaymentStatus(String transactionId);
    
    /**
     * Request a refund from the third-party system
     */
    boolean requestRefund(String transactionId, BigDecimal amount);
}
