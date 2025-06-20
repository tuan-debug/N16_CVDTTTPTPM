package com.lighting.util;

import com.lighting.dto.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicOrderPriceCalculator implements OrderPriceCalculator {
    private static final Logger log = LoggerFactory.getLogger(BasicOrderPriceCalculator.class);

    @Override
    public double calculatePrice(OrderDto.CreateOrderRequest request) {
        double total = request.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        log.info("Tính giá cơ bản: {}", total);
        return total;
    }
}