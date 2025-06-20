package com.lighting.util;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
//Adapter
public class JavaMailSenderAdapter implements EmailSender {
    // Adapter Pattern là một mẫu thiết kế cấu trúc (Structural Pattern) 
    // cho phép hai interface không tương thích có thể làm việc cùng nhau bằng cách "bọc" (wrap) một 
    // interface hiện tại thành một interface khác mà client mong muốn.
    // gòm 3 thành phần chính: 
    // 1. Target Interface: Đây là interface mà client mong muốn sử dụng.
    // 2. Adaptee: Đây là interface hiện tại mà client không thể sử dụng trực tiếp.
    // 3. Adapter: Đây là lớp trung gian, nó implements Target Interface và sử dụng Adaptee để thực hiện các công việc cần thiết.

    // JavaMailSenderAdapter — đây là "Adapter" wrap lại thư viện JavaMailSender của Spring.
    private static final Logger log = LoggerFactory.getLogger(JavaMailSenderAdapter.class);
    @Value("${mail.host}")
    private String mailHost;

    @Value("${mail.port}")
    private int mailPort;

    @Value("${mail.username}")
    private String mailUsername;

    @Value("${mail.password}")
    private String mailPassword;

    // adaptee 
    private JavaMailSender mailSender;

    // Khởi tạo mailSender sau khi Spring inject config
    @PostConstruct
    public void init() {
        JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailHost);
        mailSenderImpl.setPort(mailPort);
        mailSenderImpl.setUsername(mailUsername);
        mailSenderImpl.setPassword(mailPassword);

        Properties props = mailSenderImpl.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        this.mailSender = mailSenderImpl;
        log.info("JavaMailSenderAdapter initialized with host: {}", mailHost);
    }

    @Override
    public void sendEmail(String subject, String text, String toEmail) throws Exception {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailUsername);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(text, false); // false indicates plain text content

            mailSender.send(message);

            log.info("Email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to create or send email due to messaging error", e);
            throw new Exception("Failed to create email: " + e.getMessage());
        }
    }

    @Override
    public void sendHtmlEmail(String subject, String htmlContent, String toEmail) throws Exception {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailUsername);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indicates HTML content

            mailSender.send(message);

            log.info("HTML email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to create or send HTML email due to messaging error", e);
            throw new Exception("Failed to create HTML email: " + e.getMessage());
        }
    }

    @Override
    public void sendEmailWithAttachments(String subject, String htmlContent, String toEmail,
                                        Map<String, byte[]> attachments) throws Exception {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailUsername);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indicates HTML content

            // Add attachments
            if (attachments != null && !attachments.isEmpty()) {
                for (Map.Entry<String, byte[]> attachment : attachments.entrySet()) {
                    ByteArrayResource resource = new ByteArrayResource(attachment.getValue());
                    helper.addAttachment(attachment.getKey(), resource);
                }
            }

            mailSender.send(message);

            log.info("Email with attachments sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to create or send email with attachments due to messaging error", e);
            throw new Exception("Failed to create email with attachments: " + e.getMessage());
        }
    }
}