package com.lighting.payment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Payment processor adapter that adapts our system's payment interface
 * to the third-party payment processor interface
 * This is an implementation of the Adapter pattern
 */
@Component
public class PaymentProcessorAdapter implements PaymentServiceInterface {
    
    private static final Logger log = LoggerFactory.getLogger(PaymentProcessorAdapter.class);
    private final ThirdPartyPaymentProcessor paymentProcessor;
    private final String merchantId;
    
    // Maps to keep track of orderId to transactionId
    private final Map<String, String> orderToTransactionMap = new HashMap<>();
    
    public PaymentProcessorAdapter() {
        // In a real application, this would be injected or configured
        this.paymentProcessor = new SampleThirdPartyPaymentProcessor();
        this.merchantId = "LIGHTING_STORE_001"; // Would normally be in configuration
    }
    
    @Override
    public boolean processPayment(String orderId, BigDecimal amount, String paymentMethod, 
                                String cardNumber, String cvv, String expiryDate) {
        log.info("Processing payment for order: {} with amount: {}", orderId, amount);
        
        // Convert our payment details to the format required by third-party processor
        Map<String, String> paymentInfo = new HashMap<>();
        paymentInfo.put("cardNumber", cardNumber);
        paymentInfo.put("cvv", cvv);
        paymentInfo.put("expiryDate", expiryDate);
        paymentInfo.put("method", paymentMethod);
        
        // Generate a transaction ID used by the third-party processor
        String transactionId = UUID.randomUUID().toString();
        paymentInfo.put("transactionId", transactionId);
        
        // Store the mapping between our orderId and the third-party transactionId
        orderToTransactionMap.put(orderId, transactionId);
        
        // Use the third-party processor to process the payment
        boolean result = paymentProcessor.processPayment(merchantId, amount, "USD", paymentInfo);
        
        log.info("Payment result for order {}: {}", orderId, result ? "SUCCESS" : "FAILURE");
        return result;
    }

    @Override
    public String getPaymentStatus(String orderId) {
        log.info("Checking payment status for order: {}", orderId);
        
        // Retrieve the transactionId associated with our orderId
        String transactionId = orderToTransactionMap.get(orderId);
        
        if (transactionId == null) {
            log.warn("No transaction found for order: {}", orderId);
            return "NOT_FOUND";
        }
        
        // Use the third-party processor to check the status
        String thirdPartyStatus = paymentProcessor.checkPaymentStatus(transactionId);
        
        // Map third-party status to our application's status format
        String status;
        switch (thirdPartyStatus) {
            case "COMPLETED":
                status = "PAID";
                break;
            case "PENDING":
                status = "PROCESSING";
                break;
            case "FAILED":
                status = "FAILED";
                break;
            default:
                status = "UNKNOWN";
                break;
        }
        
        log.info("Payment status for order {}: {}", orderId, status);
        return status;
    }

    @Override
    public boolean processRefund(String orderId, BigDecimal amount) {
        log.info("Processing refund for order: {} with amount: {}", orderId, amount);
        
        // Retrieve the transactionId associated with our orderId
        String transactionId = orderToTransactionMap.get(orderId);
        
        if (transactionId == null) {
            log.warn("No transaction found for order: {}", orderId);
            return false;
        }
        
        // Use the third-party processor to process the refund
        boolean result = paymentProcessor.requestRefund(transactionId, amount);
        
        log.info("Refund result for order {}: {}", orderId, result ? "SUCCESS" : "FAILURE");
        return result;
    }
}
