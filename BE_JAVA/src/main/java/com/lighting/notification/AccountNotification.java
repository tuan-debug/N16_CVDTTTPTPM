package com.lighting.notification;

/**
 * Account notification implementation
 * This is a refined abstraction in the Bridge pattern
 */
public class AccountNotification extends Notification {
    
    public AccountNotification(NotificationSender sender) {
        super(sender);
    }
    
    @Override
    public void send(String recipient, String message) {
        String title = "Account Notification";
        sender.sendNotification(recipient, title, message);
    }
    
    /**
     * Specialized method for welcome notifications
     */
    public void sendWelcome(String recipient, String firstName) {
        String message = String.format(
            "Welcome to Lighting Store, %s! " +
            "Thank you for creating an account with us. " +
            "Explore our extensive collection of lighting products and brighten up your space.",
            firstName);
        
        sender.sendNotification(recipient, "Welcome to Lighting Store", message);
    }
    
    /**
     * Specialized method for password reset notifications
     */
    public void sendPasswordReset(String recipient, String resetCode) {
        String message = String.format(
            "You have requested a password reset. " +
            "Please use the following code to reset your password: %s " +
            "If you did not request this password reset, please ignore this message.",
            resetCode);
        
        sender.sendNotification(recipient, "Password Reset Request", message);
    }
}
