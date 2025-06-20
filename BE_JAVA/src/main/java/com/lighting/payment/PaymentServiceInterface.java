package com.lighting.payment;

import java.math.BigDecimal;

/**
 * Payment service interface used within our application
 */
public interface PaymentServiceInterface {
    /**
     * Process a payment
     */
    boolean processPayment(String orderId, BigDecimal amount, String paymentMethod, String cardNumber, String cvv, String expiryDate);
    
    /**
     * Get payment status
     */
    String getPaymentStatus(String orderId);
    
    /**
     * Process a refund
     */
    boolean processRefund(String orderId, BigDecimal amount);
}
