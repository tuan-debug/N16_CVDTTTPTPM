package com.lighting.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WelcomeEmail implements EmailComponent {
    private static final Logger log = LoggerFactory.getLogger(WelcomeEmail.class);
    private final String recipient;
    private String subject;
    private String htmlContent;

    public WelcomeEmail(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public void prepareContent() {
        this.subject = "Welcome to Lighting!";
        this.htmlContent = "<h1>Welcome!</h1><p>Thank you for joining our platform. Start shopping now!</p>";
        log.info("Prepared welcome email for: {}", recipient);
    }

    @Override
    public void send(EmailService emailService) {
        emailService.sendHtmlEmail(subject, htmlContent, recipient);
        log.info("Welcome email sent to: {}", recipient);
    }

    @Override
    public String getRecipient() {
        return recipient;
    }
}