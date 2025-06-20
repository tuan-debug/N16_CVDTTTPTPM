package com.lighting.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SMS notification sender implementation
 * This is a concrete implementor in the Bridge pattern
 */
public class SmsNotificationSender implements NotificationSender {
    private static final Logger log = LoggerFactory.getLogger(SmsNotificationSender.class);
    
    @Override
    public void sendNotification(String recipient, String title, String message) {
        // In a real application, this would use an SMS API client
        log.info("Sending SMS notification to {}", recipient);
        String smsContent = title + ": " + message;
        if (smsContent.length() > 160) {
            smsContent = smsContent.substring(0, 157) + "...";
        }
        
        log.debug("SMS content: {}", smsContent);
        
        // Simulate sending an SMS
        try {
            Thread.sleep(50); // Simulate network delay
            log.info("SMS notification sent to {}", recipient);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("SMS notification interrupted", e);
        }
    }
}
