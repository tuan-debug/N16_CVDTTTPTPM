package com.lighting.util;

import com.lighting.dto.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaxDecorator extends OrderPriceDecorator {
    private static final Logger log = LoggerFactory.getLogger(TaxDecorator.class);
    private static final double VAT_RATE = 0.08; // 8% VAT

    public TaxDecorator(OrderPriceCalculator decoratedCalculator) {
        super(decoratedCalculator);
    }

    @Override
    public double calculatePrice(OrderDto.CreateOrderRequest request) {
        double basePrice = decoratedCalculator.calculatePrice(request);
        double tax = basePrice * VAT_RATE;
        log.info("Áp dụng thuế VAT 8%: {}", tax);
        return basePrice + tax;
    }
}