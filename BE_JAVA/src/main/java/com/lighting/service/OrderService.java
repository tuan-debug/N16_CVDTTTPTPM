package com.lighting.service;

import com.lighting.entity.Account;
import com.lighting.entity.Order;
import com.lighting.entity.Payment;
import com.lighting.dto.OrderDto;
import com.lighting.repository.AccountRepository;
import com.lighting.repository.OrderRepository;
import com.lighting.repository.PaymentRepository;
import com.lighting.util.RedisService;
import com.lighting.util.OrderPriceCalculator;
import com.lighting.util.BasicOrderPriceCalculator;
import com.lighting.util.DiscountDecorator;
import com.lighting.util.TaxDecorator;
import com.lighting.util.ShippingFeeDecorator;
import com.lighting.util.ColorConfigFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, AccountRepository accountRepository,
                        PaymentRepository paymentRepository, RedisService redisService,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
        this.redisService = redisService;
        this.objectMapper = objectMapper;
    }

    public Order createOrder(String userId, OrderDto.CreateOrderRequest request) {
        log.info("Tạo đơn hàng cho người dùng: {}", userId);

        try {
            Account user = accountRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            OrderPriceCalculator calculator = new BasicOrderPriceCalculator();
            calculator = new DiscountDecorator(calculator, orderRepository, userId);
            calculator = new TaxDecorator(calculator);
            calculator = new ShippingFeeDecorator(calculator);
            double totalPrice = calculator.calculatePrice(request);

            Order newOrder = new Order();
            newOrder.setUserId(userId);
            newOrder.setPaymentMethod(Order.PaymentMethod.valueOf(request.getPaymentMethod()));
            newOrder.setItems(convertToOrderItems(request.getItems()));
            newOrder.setTotalPrice(totalPrice);
            newOrder.setAddress(convertToOrderAddress(request.getAddress()));
            newOrder.setDefaultStatus();
            newOrder.setIsPaid(false);

            Order savedOrder = orderRepository.save(newOrder);

            redisService.delete(userOrdersKey(userId));
            redisService.delete(allOrdersKey());

            log.info("Tạo đơn hàng thành công với ID: {}", savedOrder.getId());
            return savedOrder;

        } catch (Exception e) {
            log.error("Lỗi khi tạo đơn hàng: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public OrderDto getOrdersById(String userId, String orderId) {
        log.info("Lấy đơn hàng theo ID: {} cho người dùng: {}", orderId, userId);

        try {
            String cachedOrder = redisService.get(orderCacheKey(orderId));
            if (cachedOrder != null) {
                OrderDto order = objectMapper.readValue(cachedOrder, OrderDto.class);
                if (!order.getUserId().equals(userId)) {
                    throw new RuntimeException("Không được phép truy cập");
                }
                log.info("Tìm thấy đơn hàng trong cache: {}", orderId);
                return order;
            }

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

            if (!order.getUserId().equals(userId)) {
                throw new RuntimeException("Không được phép truy cập");
            }

            OrderDto orderDto = mapToDto(order);

            redisService.set(orderCacheKey(orderId), objectMapper.writeValueAsString(orderDto), 3600);

            log.info("Lấy và lưu cache đơn hàng: {}", orderId);
            return orderDto;

        } catch (JsonProcessingException e) {
            log.error("Lỗi xử lý JSON cho đơn hàng: {}", orderId, e);
            throw new RuntimeException("Lỗi máy chủ nội bộ");
        } catch (Exception e) {
            log.error("Lỗi khi lấy đơn hàng: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteOrder(String userId, String orderId) {
        log.info("Xóa đơn hàng: {} cho người dùng: {}", orderId, userId);

        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

            if (Order.PaymentMethod.TRANSFER.equals(order.getPaymentMethod()) && order.getPaymentId() != null) {
                Payment payment = paymentRepository.findById(order.getPaymentId()).orElse(null);
                if (payment == null) {
                    throw new RuntimeException("Không tìm thấy thanh toán");
                }

                redisService.delete(paymentTransactionKey(order.getPaymentId()));
                paymentRepository.deleteById(order.getPaymentId());
            }

            if (!order.getUserId().equals(userId)) {
                throw new RuntimeException("Không được phép truy cập");
            }

            orderRepository.deleteById(orderId);

            redisService.delete(orderCacheKey(orderId));
            redisService.delete(userOrdersKey(userId));
            redisService.delete(allOrdersKey());

            log.info("Xóa đơn hàng thành công: {}", orderId);

        } catch (Exception e) {
            log.error("Lỗi khi xóa đơn hàng: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void updateOrder(String userId, String orderId, OrderDto.UpdateOrderRequest request) {
        log.info("Cập nhật đơn hàng: {} cho người dùng: {}", orderId, userId);

        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

            if (!order.getUserId().equals(userId)) {
                throw new RuntimeException("Không được phép truy cập");
            }

            if (request.getStatus() != null) {
                order.setStatus(Order.OrderStatus.valueOf(request.getStatus()));
            }
            if (request.getIsPaid() != null) {
                order.setIsPaid(request.getIsPaid());
            }
            if (request.getPaymentId() != null) {
                order.setPaymentId(request.getPaymentId());
            }

            orderRepository.save(order);

            redisService.delete(orderCacheKey(orderId));
            redisService.delete(userOrdersKey(userId));
            redisService.delete(allOrdersKey());

            log.info("Cập nhật đơn hàng thành công: {}", orderId);

        } catch (Exception e) {
            log.error("Lỗi khi cập nhật đơn hàng: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<OrderDto> getOrdersByUserId(String userId) {
        log.info("Lấy danh sách đơn hàng cho người dùng: {}", userId);

        try {
            String cachedOrders = redisService.get(userOrdersKey(userId));
            if (cachedOrders != null) {
                log.info("Tìm thấy danh sách đơn hàng trong cache cho người dùng: {}", userId);
                return objectMapper.readValue(cachedOrders,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, OrderDto.class));
            }

            List<Order> orders = orderRepository.findByUserId(userId);
            List<OrderDto> orderDtos = orders.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());

            redisService.set(userOrdersKey(userId), objectMapper.writeValueAsString(orderDtos), 3600);

            log.info("Lấy và lưu cache danh sách đơn hàng cho người dùng: {}. Số lượng: {}", userId, orderDtos.size());
            return orderDtos;

        } catch (JsonProcessingException e) {
            log.error("Lỗi xử lý JSON cho danh sách đơn hàng: {}", userId, e);
            throw new RuntimeException("Lỗi máy chủ nội bộ");
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách đơn hàng: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<OrderDto> getAllOrders(String userId, Integer page, Integer limit) {
        log.info("Lấy tất cả đơn hàng cho quản trị viên: {} với trang: {}, giới hạn: {}", userId, page, limit);

        try {
            Account account = accountRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            if (account.getRole() != Account.Role.ADMIN) {
                throw new RuntimeException("Không được phép truy cập");
            }

            String cachedOrders = redisService.get(allOrdersKey());
            if (cachedOrders != null) {
                log.info("Tìm thấy tất cả đơn hàng trong cache");
                return objectMapper.readValue(cachedOrders,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, OrderDto.class));
            }

            List<Order> orders = orderRepository.findAll();
            List<OrderDto> orderDtos = orders.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());

            redisService.set(allOrdersKey(), objectMapper.writeValueAsString(orderDtos), 1800);

            log.info("Lấy và lưu cache tất cả đơn hàng. Số lượng: {}", orderDtos.size());
            return orderDtos;

        } catch (JsonProcessingException e) {
            log.error("Lỗi xử lý JSON cho tất cả đơn hàng", e);
            throw new RuntimeException("Lỗi máy chủ nội bộ");
        } catch (Exception e) {
            log.error("Lỗi khi lấy tất cả đơn hàng: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private String orderCacheKey(String orderId) {
        return "order:" + orderId;
    }

    private String userOrdersKey(String userId) {
        return "orders:user:" + userId;
    }

    private String allOrdersKey() {
        return "orders:all";
    }

    private String paymentTransactionKey(String paymentId) {
        return "transaction:" + paymentId;
    }

    private OrderDto mapToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setPaymentId(order.getPaymentId());
        dto.setPaymentMethod(order.getPaymentMethod().toString());
        dto.setItems(convertFromOrderItems(order.getItems()));
        dto.setTotalPrice(order.getTotalPrice());
        dto.setAddress(convertFromOrderAddress(order.getAddress()));
        dto.setStatus(order.getStatus().toString());
        dto.setIsPaid(order.getIsPaid());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }

    private List<Order.OrderItem> convertToOrderItems(List<OrderDto.OrderItemDto> dtoItems) {
        return dtoItems.stream().map(dto -> {
            Order.OrderItem item = new Order.OrderItem();
            item.setProductId(dto.get_id());
            item.setType(dto.getType());
            item.setName(dto.getName());
            item.setPrice(dto.getPrice());
            item.setColor(ColorConfigFactory.getColorConfig(dto.getColor()));
            item.setCategory(dto.getCategory());
            item.setImages(dto.getImages());
            item.setQuantity(dto.getQuantity());
            return item;
        }).collect(Collectors.toList());
    }

    private Order.OrderAddress convertToOrderAddress(OrderDto.OrderAddressDto dto) {
        Order.OrderAddress address = new Order.OrderAddress();
        address.setFirstName(dto.getFirstName());
        address.setLastName(dto.getLastName());
        address.setEmail(dto.getEmail());
        address.setPhone(dto.getPhone());
        address.setAddressLine1(dto.getAddressLine1());
        address.setProvince(dto.getProvince());
        address.setDistrict(dto.getDistrict());
        address.setWard(dto.getWard());
        address.setPostalCode(dto.getPostalCode());
        address.setGender(dto.getGender());
        address.setAddressNote(dto.getAddressNote() != null ? dto.getAddressNote() : "");
        return address;
    }

    private List<OrderDto.OrderItemDto> convertFromOrderItems(List<Order.OrderItem> entityItems) {
        return entityItems.stream().map(entity -> {
            OrderDto.OrderItemDto dto = new OrderDto.OrderItemDto();
            dto.set_id(entity.getProductId());
            dto.setType(entity.getType());
            dto.setName(entity.getName());
            dto.setPrice(entity.getPrice());
            dto.setColor(entity.getColor() != null ? entity.getColor().getName() : null);
            dto.setCategory(entity.getCategory());
            dto.setImages(entity.getImages());
            dto.setQuantity(entity.getQuantity());
            return dto;
        }).collect(Collectors.toList());
    }

    private OrderDto.OrderAddressDto convertFromOrderAddress(Order.OrderAddress entity) {
        OrderDto.OrderAddressDto dto = new OrderDto.OrderAddressDto();
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setAddressLine1(entity.getAddressLine1());
        dto.setProvince(entity.getProvince());
        dto.setDistrict(entity.getDistrict());
        dto.setWard(entity.getWard());
        dto.setPostalCode(entity.getPostalCode());
        dto.setGender(entity.getGender());
        dto.setAddressNote(entity.getAddressNote());
        return dto;
    }
}