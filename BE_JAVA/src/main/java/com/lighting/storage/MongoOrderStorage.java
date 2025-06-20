package com.lighting.storage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lighting.entity.Order;
import com.lighting.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MongoOrderStorage implements OrderStorage {
    private final OrderRepository orderRepository;

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(String id) {
        return orderRepository.findById(id);
    }

    @Override
    public void deleteById(String id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public List<Order> findByPaymentMethod(Order.PaymentMethod paymentMethod) {
        return orderRepository.findByPaymentMethod(paymentMethod);
    }

    @Override
    public List<Order> findByIsPaid(boolean isPaid) {
        return isPaid ? orderRepository.findByIsPaidTrue() : orderRepository.findByIsPaidFalse();
    }

    @Override
    public List<Order> findByUserIdAndStatus(String userId, Order.OrderStatus status) {
        return orderRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    public List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public List<Order> findByTotalPriceBetween(Double minPrice, Double maxPrice) {
        return orderRepository.findByTotalPriceBetween(minPrice, maxPrice);
    }

    @Override
    public long countByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    public long countByUserId(String userId) {
        return orderRepository.countByUserId(userId);
    }

    @Override
    public List<Order> findTop10ByOrderByCreatedAtDesc() {
        return orderRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    public List<Order> findOrdersContainingProduct(String productId) {
        return orderRepository.findOrdersContainingProduct(productId);
    }

    @Override
    public List<Order> findAllOrderTotals() {
        return orderRepository.findAllOrderTotals();
    }

    @Override
    public Optional<Order> findByPaymentId(String paymentId) {
        return orderRepository.findByPaymentId(paymentId);
    }
}