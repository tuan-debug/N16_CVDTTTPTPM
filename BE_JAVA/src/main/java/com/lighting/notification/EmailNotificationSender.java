package com.lighting.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Email notification sender implementation
 * This is a concrete implementor in the Bridge pattern
 */
public class EmailNotificationSender implements NotificationSender {
    private static final Logger log = LoggerFactory.getLogger(EmailNotificationSender.class);
    
    @Override
    public void sendNotification(String recipient, String title, String message) {
        // In a real application, this would use JavaMailSender or similar
        log.info("Sending email notification to {} with title: {}", recipient, title);
        log.debug("Email content: {}", message);
        
        // Simulate sending an email
        try {
            Thread.sleep(100); // Simulate network delay
            log.info("Email notification sent to {}", recipient);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Email notification interrupted", e);
        }
    }
}
