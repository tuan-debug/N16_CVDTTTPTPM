package com.lighting.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final EmailService INSTANCE = new EmailService();
    private final EmailSender emailSender;

    private EmailService() {
        this.emailSender = new JavaMailSenderAdapter();
        log.info("EmailService singleton instance created");
    }

    private EmailService(EmailSender emailSender) {
        this.emailSender = emailSender;
        log.info("EmailService singleton instance created with custom EmailSender");
    }

    public static EmailService getInstance() {
        return INSTANCE;
    }

    public void sendEmail(String subject, String text, String toEmail) {
        try {
            emailSender.sendEmail(subject, text, toEmail);
            log.info("Email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    public void sendHtmlEmail(String subject, String htmlContent, String toEmail) {
        try {
            emailSender.sendHtmlEmail(subject, htmlContent, toEmail);
            log.info("HTML email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send HTML email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send HTML email: " + e.getMessage());
        }
    }

    public void sendEmailWithAttachments(String subject, String htmlContent, String toEmail,
                                         Map<String, byte[]> attachments) {
        try {
            emailSender.sendEmailWithAttachments(subject, htmlContent, toEmail, attachments);
            log.info("Email with attachments sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email with attachments to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email with attachments: " + e.getMessage());
        }
    }

    public void sendEmailComponent(EmailComposite emailComposite) {
        emailComposite.prepareContent();
        emailComposite.send(this);
        log.info("Email composite sent to: {}", emailComposite.getRecipient());
    }
}