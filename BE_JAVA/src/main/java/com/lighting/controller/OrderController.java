package com.lighting.controller;

import com.lighting.entity.Order;
import com.lighting.entity.Payment;
import com.lighting.dto.OrderDto;
import com.lighting.service.OrderService;
import com.lighting.service.PaymentService;
import com.lighting.util.RedisService;
import com.lighting.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final RedisService redisService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody OrderDto.CreateOrderRequest request) {
        try {
            String userId = AuthUtil.getCurrentUserId(); // Get from JWT token
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
            }

            log.info("Order data: {}", request);

            Payment payment = null;
            if ("TRANSFER".equals(request.getPaymentMethod())) {
                // Create transaction data exactly like Node.js
                var transactionData = Map.of(
                    "user_id", userId,
                    "amount", request.getTotalPrice()
                );

                payment = paymentService.createTransaction(userId, request.getTotalPrice());
                log.info("Payment: {}", payment);

                if (payment == null) {
                    throw new RuntimeException("Create transaction failed");
                }

                String redisKey = "transaction:" + payment.getId();
                redisService.set(redisKey, payment.getHexKey());
            }

            // Create order payload matching Node.js structure
            OrderDto.CreateOrderRequest payload = new OrderDto.CreateOrderRequest();
            payload.setPaymentMethod(request.getPaymentMethod());
            payload.setItems(request.getItems());
            payload.setTotalPrice(request.getTotalPrice());
            payload.setAddress(request.getAddress());
            // Add payment_id if payment exists
            String paymentId = payment != null ? payment.getId() : null;

            Order newOrder = orderService.createOrder(userId, payload);
            if (paymentId != null) {
                // Update order with payment ID after creation
                OrderDto.UpdateOrderRequest updateReq = new OrderDto.UpdateOrderRequest();
                updateReq.setPaymentId(paymentId);
                orderService.updateOrder(userId, newOrder.getId(), updateReq);
            }

            if (newOrder == null) {
                throw new RuntimeException("Create order failed");
            }

            return ResponseEntity.ok(Map.of(
                "message", "Create order successfully",
                "order", newOrder
            ));
        } catch (Exception e) {
            log.error("Create order failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Create order failed"));
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable String id) {
        try {
            String userId = AuthUtil.getCurrentUserId(); // Get from JWT token
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
            }

            OrderDto order = orderService.getOrdersById(userId, id);
            return ResponseEntity.ok(Map.of(
                "message", "Get order successfully",
                "order", order
            ));
        } catch (Exception e) {
            log.error("Get order failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Get order failed"));
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable String id) {
        try {
            String userId = AuthUtil.getCurrentUserId(); // Get from JWT token
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
            }

            log.info("User ID: {}, Order ID: {}", userId, id);
            orderService.deleteOrder(userId, id);

            return ResponseEntity.ok(Map.of(
                "message", "Delete order successfully"
            ));
        } catch (Exception e) {
            log.error("Delete order failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Delete order failed"));
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateOrder(
            @PathVariable String id,
            @Valid @RequestBody OrderDto.UpdateOrderRequest request) {
        try {
            String userId = AuthUtil.getCurrentUserId(); // Get from JWT token
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
            }

            log.info("User: {}", userId); // Matching Node.js log
            orderService.updateOrder(userId, id, request);

            return ResponseEntity.ok(Map.of(
                "message", "Update order successfully"
            ));
        } catch (Exception e) {
            log.error("Update order failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Update order failed"));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getOrdersByUserId(@PathVariable String userId) {
        try {
            // Validate that user can only access their own orders or is admin
            AuthUtil.validateUserOrAdminAccess(userId);

            List<OrderDto> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(Map.of(
                "message", "Get orders successfully",
                "orders", orders
            ));
        } catch (Exception e) {
            log.error("Get orders failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Get orders failed"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit) {
        try {
            log.info("Query params - page: {}, limit: {}", page, limit);

            String userId = AuthUtil.getCurrentUserId(); // Get from JWT token
            if (userId == null || !AuthUtil.isCurrentUserAdmin()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized - Admin access required"));
            }

            List<OrderDto> orders = orderService.getAllOrders(userId, page, limit);
            return ResponseEntity.ok(Map.of(
                "message", "Get all orders successfully",
                "orders", orders
            ));
        } catch (Exception e) {
            log.error("Get all orders failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Get all orders failed"));
        }
    }
}
