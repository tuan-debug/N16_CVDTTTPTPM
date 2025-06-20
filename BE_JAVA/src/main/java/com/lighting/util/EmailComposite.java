package com.lighting.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailComposite implements EmailComponent {
    private static final Logger log = LoggerFactory.getLogger(EmailComposite.class);
    private final List<EmailComponent> components = new ArrayList<>();
    private final String recipient;

    public EmailComposite(String recipient) {
        this.recipient = recipient;
    }

    public void addComponent(EmailComponent component) {
        components.add(component);
        log.info("Added email component for recipient: {}", recipient);
    }

    public void removeComponent(EmailComponent component) {
        components.remove(component);
        log.info("Removed email component for recipient: {}", recipient);
    }

    @Override
    public void prepareContent() {
        for (EmailComponent component : components) {
            component.prepareContent();
        }
        log.info("Prepared content for {} email components for recipient: {}", components.size(), recipient);
    }

    @Override
    public void send(EmailService emailService) {
        for (EmailComponent component : components) {
            component.send(emailService);
        }
        log.info("Sent {} email components to: {}", components.size(), recipient);
    }

    @Override
    public String getRecipient() {
        return recipient;
    }
}