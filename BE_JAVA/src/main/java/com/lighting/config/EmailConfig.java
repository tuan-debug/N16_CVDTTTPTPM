package com.lighting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lighting.util.EmailSender;
import com.lighting.util.JavaMailSenderAdapter;

@Configuration
public class EmailConfig {

    @Bean
    public EmailSender emailSender() {
        return new JavaMailSenderAdapter();
    }
}