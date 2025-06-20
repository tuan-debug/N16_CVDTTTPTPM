package com.lighting.util;

import com.lighting.dto.OrderDto;

public interface OrderPriceCalculator {
    double calculatePrice(OrderDto.CreateOrderRequest request);
}