package com.lighting.util;

import com.lighting.dto.OrderDto;
import com.lighting.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscountDecorator extends OrderPriceDecorator {
    private static final Logger log = LoggerFactory.getLogger(DiscountDecorator.class);
    private final OrderRepository orderRepository;
    private final String userId;

    public DiscountDecorator(OrderPriceCalculator decoratedCalculator, OrderRepository orderRepository, String userId) {
        super(decoratedCalculator);
        this.orderRepository = orderRepository;
        this.userId = userId;
    }

    @Override
    public double calculatePrice(OrderDto.CreateOrderRequest request) {
        double basePrice = decoratedCalculator.calculatePrice(request);
        if (isFirstTimeCustomer()) {
            double discount = basePrice * 0.10; // Giảm 10%
            log.info("Áp dụng chiết khấu 10% cho khách hàng mới: {}", discount);
            return basePrice - discount;
        }
        return basePrice;
    }

    private boolean isFirstTimeCustomer() {
        long orderCount = orderRepository.countByUserId(userId);
        return orderCount == 0;
    }
}