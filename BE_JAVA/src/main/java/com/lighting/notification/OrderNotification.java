package com.lighting.notification;

/**
 * Order notification implementation
 * This is a refined abstraction in the Bridge pattern
 */
public class OrderNotification extends Notification {
    
    public OrderNotification(NotificationSender sender) {
        super(sender);
    }
    
    @Override
    public void send(String recipient, String message) {
        String title = "Order Update";
        sender.sendNotification(recipient, title, message);
    }
    
    /**
     * Specialized method for order confirmations
     */
    public void sendOrderConfirmation(String recipient, String orderId, double amount) {
        String message = String.format(
            "Thank you for your order! Your order %s has been confirmed. " +
            "The total amount of $%.2f has been charged. " +
            "You will receive another notification once your order has been shipped.",
            orderId, amount);
        
        sender.sendNotification(recipient, "Order Confirmation", message);
    }
    
    /**
     * Specialized method for shipping notifications
     */
    public void sendShippingNotification(String recipient, String orderId, String trackingNumber) {
        String message = String.format(
            "Your order %s has been shipped! " +
            "You can track your package using the tracking number: %s. " +
            "Thank you for shopping with Lighting Store!",
            orderId, trackingNumber);
        
        sender.sendNotification(recipient, "Order Shipped", message);
    }
}
