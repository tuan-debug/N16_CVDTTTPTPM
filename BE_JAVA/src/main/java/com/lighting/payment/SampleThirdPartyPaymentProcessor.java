package com.lighting.payment;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Sample implementation of a third-party payment processor
 * In real application, this would be an external library or service
 */
public class SampleThirdPartyPaymentProcessor implements ThirdPartyPaymentProcessor {
    
    @Override
    public boolean processPayment(String merchantId, BigDecimal amount, String currency, Map<String, String> paymentInfo) {
        // Simulate a payment processing delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate successful payment (in a real implementation, this would make an API call)
        boolean isSuccess = amount.compareTo(BigDecimal.ZERO) > 0 
                && merchantId != null 
                && !merchantId.isEmpty() 
                && paymentInfo != null 
                && paymentInfo.containsKey("cardNumber");
        
        return isSuccess;
    }

    @Override
    public String checkPaymentStatus(String transactionId) {
        // Simulate a payment status check
        if (transactionId == null || transactionId.isEmpty()) {
            return "INVALID";
        }
        
        // For demo purposes, return a status based on transaction ID
        if (transactionId.endsWith("0")) {
            return "FAILED";
        } else if (transactionId.endsWith("1")) {
            return "PENDING";
        } else {
            return "COMPLETED";
        }
    }

    @Override
    public boolean requestRefund(String transactionId, BigDecimal amount) {
        // Simulate a refund processing delay
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate successful refund for most transactions
        return transactionId != null && !transactionId.isEmpty() && !transactionId.endsWith("0");
    }
}
