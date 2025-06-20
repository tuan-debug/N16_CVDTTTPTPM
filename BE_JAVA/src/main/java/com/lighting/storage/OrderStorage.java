package com.lighting.storage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.lighting.entity.Order;

public interface OrderStorage {
    Order save(Order order);
    Optional<Order> findById(String id);
    void deleteById(String id);
    List<Order> findByUserId(String userId);
    List<Order> findAll();
    List<Order> findByStatus(Order.OrderStatus status);
    List<Order> findByPaymentMethod(Order.PaymentMethod paymentMethod);
    List<Order> findByIsPaid(boolean isPaid);
    List<Order> findByUserIdAndStatus(String userId, Order.OrderStatus status);
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByTotalPriceBetween(Double minPrice, Double maxPrice);
    long countByStatus(Order.OrderStatus status);
    long countByUserId(String userId);
    List<Order> findTop10ByOrderByCreatedAtDesc();
    List<Order> findOrdersContainingProduct(String productId);
    List<Order> findAllOrderTotals();
    Optional<Order> findByPaymentId(String paymentId);
}