package com.lighting.util;

import com.lighting.dto.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShippingFeeDecorator extends OrderPriceDecorator {
    private static final Logger log = LoggerFactory.getLogger(ShippingFeeDecorator.class);
    private static final double STANDARD_SHIPPING_FEE = 5.0; // $5 cho Hà Nội
    private static final double REMOTE_SHIPPING_FEE = 10.0; // $10 cho nơi khác

    public ShippingFeeDecorator(OrderPriceCalculator decoratedCalculator) {
        super(decoratedCalculator);
    }

    @Override
    public double calculatePrice(OrderDto.CreateOrderRequest request) {
        double basePrice = decoratedCalculator.calculatePrice(request);
        double shippingFee = calculateShippingFee(request.getAddress());
        log.info("Áp dụng phí vận chuyển: {}", shippingFee);
        return basePrice + shippingFee;
    }

    private double calculateShippingFee(OrderDto.OrderAddressDto address) {
        if ("Hanoi".equalsIgnoreCase(address.getProvince())) {
            return STANDARD_SHIPPING_FEE;
        }
        return REMOTE_SHIPPING_FEE;
    }
}