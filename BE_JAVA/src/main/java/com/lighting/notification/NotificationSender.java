package com.lighting.notification;

/**
 * Notification message implementation interface
 * This is the implementor part of the Bridge pattern
 */
public interface NotificationSender {
    /**
     * Send a notification
     */
    void sendNotification(String recipient, String title, String message);
}
