package com.lighting.util;

import java.util.Map;

// Tác dụng: Đây là chuẩn bạn tự định nghĩa cho hệ thống gửi mail. 
// Target Interface	
// Mọi loại gửi mail nào (SMTP, SendGrid, Mailgun...) chỉ cần implements interface này là đủ.
public interface EmailSender {
    // Gửi email đơn giản với tiêu đề và nội dung văn bản
    void sendEmail(String subject, String text, String toEmail) throws Exception;
    // Gửi email với nội dung HTML
    void sendHtmlEmail(String subject, String htmlContent, String toEmail) throws Exception;
    // Gửi email với các tệp đính kèm
    void sendEmailWithAttachments(String subject, String htmlContent, String toEmail, 
                                 Map<String, byte[]> attachments) throws Exception;
}