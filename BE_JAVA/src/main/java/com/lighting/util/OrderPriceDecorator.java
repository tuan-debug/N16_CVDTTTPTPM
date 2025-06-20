package com.lighting.util;

import com.lighting.dto.OrderDto;

public abstract class OrderPriceDecorator implements OrderPriceCalculator {
    protected final OrderPriceCalculator decoratedCalculator;

    protected OrderPriceDecorator(OrderPriceCalculator decoratedCalculator) {
        this.decoratedCalculator = decoratedCalculator;
    }

    @Override
    public double calculatePrice(OrderDto.CreateOrderRequest request) {
        return decoratedCalculator.calculatePrice(request);
    }
}