package com.lighting.notification;

/**
 * Abstract notification class
 * This is the abstraction part of the Bridge pattern
 */
public abstract class Notification {
    protected NotificationSender sender;
    
    protected Notification(NotificationSender sender) {
        this.sender = sender;
    }
    
    /**
     * Send the notification
     */
    public abstract void send(String recipient, String message);
    
    /**
     * Change the notification sender implementation at runtime
     */
    public void setSender(NotificationSender sender) {
        this.sender = sender;
    }
}
