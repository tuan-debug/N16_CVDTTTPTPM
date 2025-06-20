package com.lighting.util;

public interface EmailComponent {
    void prepareContent(); // Prepare email content
    void send(EmailService emailService); // Send email using EmailService
    String getRecipient(); // Get recipient email address
}