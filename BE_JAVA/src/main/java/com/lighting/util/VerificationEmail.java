package com.lighting.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerificationEmail implements EmailComponent {
    private static final Logger log = LoggerFactory.getLogger(VerificationEmail.class);
    private final String recipient;
    private final String code;
    private String subject;
    private String content;

    public VerificationEmail(String recipient, String code) {
        this.recipient = recipient;
        this.code = code;
    }

    @Override
    public void prepareContent() {
        this.subject = "Email Verification";
        this.content = "Your verification code is: " + code;
        log.info("Prepared verification email for: {}", recipient);
    }

    @Override
    public void send(EmailService emailService) {
        emailService.sendEmail(subject, content, recipient);
        log.info("Verification email sent to: {}", recipient);
    }

    @Override
    public String getRecipient() {
        return recipient;
    }
}