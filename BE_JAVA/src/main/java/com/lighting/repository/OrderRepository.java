package com.lighting.repository;

import com.lighting.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    // Find orders by user ID
    List<Order> findByUserId(String userId);

    // Find orders by status
    List<Order> findByStatus(Order.OrderStatus status);

    // Find orders by payment method
    List<Order> findByPaymentMethod(Order.PaymentMethod paymentMethod);

    // Find paid orders
    List<Order> findByIsPaidTrue();

    // Find unpaid orders
    List<Order> findByIsPaidFalse();

    // Find orders by user ID and status
    List<Order> findByUserIdAndStatus(String userId, Order.OrderStatus status);

    // Find orders created within date range
    @Query("{'createdAt': {$gte: ?0, $lte: ?1}}")
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find orders by total price range
    @Query("{'totalPrice': {$gte: ?0, $lte: ?1}}")
    List<Order> findByTotalPriceBetween(Double minPrice, Double maxPrice);

    // Count orders by status
    long countByStatus(Order.OrderStatus status);

    // Count orders by user ID
    long countByUserId(String userId);

    // Find latest orders
    List<Order> findTop10ByOrderByCreatedAtDesc();

    // Find orders that contain specific product
    @Query("{'items.productId': ?0}")
    List<Order> findOrdersContainingProduct(String productId);

    // Calculate total revenue
    @Query(value = "{}", fields = "{'totalPrice': 1}")
    List<Order> findAllOrderTotals();

    // Find orders by payment ID
    Optional<Order> findByPaymentId(String paymentId);
}
