// package com.lighting.storage;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.stereotype.Component;

// import com.lighting.entity.Order;

// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class MySqlOrderStorage implements OrderStorage {
//     private final JdbcTemplate jdbcTemplate;

//     @Override
//     public Order save(Order order) {
//         String sql = "INSERT INTO orders (id, user_id, payment_id, payment_method, items, total_price, address, status, is_paid, created_at, updated_at) " +
//                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//         jdbcTemplate.update(sql, order.getId(), order.getUserId(), order.getPaymentId(), order.getPaymentMethod().name(),
//                 order.getItems().toString(), order.getTotalPrice(), order.getAddress().toString(),
//                 order.getStatus().name(), order.getIsPaid(), order.getCreatedAt(), order.getUpdatedAt());
//         return order;
//     }

//     @Override
//     public Optional<Order> findById(String id) {
//         String sql = "SELECT * FROM orders WHERE id = ?";
//         return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
//             if (rs.next()) {
//                 Order order = new Order();
//                 order.setId(rs.getString("id"));
//                 order.setUserId(rs.getString("user_id"));
//                 order.setPaymentId(rs.getString("payment_id"));
//                 order.setPaymentMethod(Order.PaymentMethod.valueOf(rs.getString("payment_method")));
//                 // Giả định items và address được lưu dưới dạng JSON, cần parse
//                 order.setTotalPrice(rs.getDouble("total_price"));
//                 order.setStatus(Order.OrderStatus.valueOf(rs.getString("status")));
//                 order.setIsPaid(rs.getBoolean("is_paid"));
//                 order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
//                 order.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
//                 return Optional.of(order);
//             }
//             return Optional.empty();
//         });
//     }

//     @Override
//     public void deleteById(String id) {
//         String sql = "DELETE FROM orders WHERE id = ?";
//         jdbcTemplate.update(sql, id);
//     }

//     @Override
//     public List<Order> findByUserId(String userId) {
//         String sql = "SELECT * FROM orders WHERE user_id = ?";
//         return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
//             Order order = new Order();
//             order.setId(rs.getString("id"));
//             order.setUserId(rs.getString("user_id"));
//             order.setPaymentId(rs.getString("payment_id"));
//             order.setPaymentMethod(Order.PaymentMethod.valueOf(rs.getString("payment_method")));
//             order.setTotalPrice(rs.getDouble("total_price"));
//             order.setStatus(Order.OrderStatus.valueOf(rs.getString("status")));
//             order.setIsPaid(rs.getBoolean("is_paid"));
//             order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
//             order.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
//             return order;
//         });
//     }

//     @Override
//     public List<Order> findAll() {
//         String sql = "SELECT * FROM orders";
//         return jdbcTemplate.query(sql, (rs, rowNum) -> {
//             Order order = new Order();
//             order.setId(rs.getString("id"));
//             order.setUserId(rs.getString("user_id"));
//             order.setPaymentId(rs.getString("payment_id"));
//             order.setPaymentMethod(Order.PaymentMethod.valueOf(rs.getString("payment_method")));
//             order.setTotalPrice(rs.getDouble("total_price"));
//             order.setStatus(Order.OrderStatus.valueOf(rs.getString("status")));
//             order.setIsPaid(rs.getBoolean("is_paid"));
//             order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
//             order.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
//             return order;
//         });
//     }

//     // Các phương thức khác (findByStatus, findByPaymentMethod, v.v.) có thể triển khai tương tự
//     // Để đơn giản, giả định chỉ triển khai các phương thức chính

//     @Override
//     public List<Order> findByStatus(Order.OrderStatus status) {
//         throw new UnsupportedOperationException("Not implemented");
//     }

//     @Override
//     public List<Order> findByPaymentMethod(Order.PaymentMethod paymentMethod) {
//         throw new UnsupportedOperationException("Not implemented");
//     }

//     @Override
//     public List<Order> findByIsPaid(boolean isPaid) {
//         throw new UnsupportedOperationException("Not implemented");
//     }

//     @Override
//     public List<Order> findByUserIdAndStatus(String userId, Order.OrderStatus status) {
//         throw new UnsupportedOperationException("Not implemented");
//     }

//     @Override
//     public List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
//         throw new UnsupportedOperationException("Not implemented");
//     }

//     @Override
//     public List<Order> findByTotalPriceBetween(Double minPrice, Double maxPrice) {
//         throw new UnsupportedOperationException("Not implemented");
//     }

//     @Override
//     public long countByStatus(Order.OrderStatus status) {
//         throw new UnsupportedOperationException("Not implemented");
//     }

//     @Override
//     public long countByUserId(String userId) {
//         throw new UnsupportedOperationException("Not implemented");
//     }

//     @Override
//     public List<Order> findTop10ByOrderByCreatedAtDesc() {
//         throw new UnsupportedOperationException("Not implemented");
//     }

//     @Override
//     public List<Order> findOrdersContainingProduct(String productId) {
//         throw new UnsupportedOperationException("Not implemented");
//     }

//     @Override
//     public List<Order> findAllOrderTotals() {
//         throw new UnsupportedOperationException("Not implemented");
//     }

//     @Override
//     public Optional<Order> findByPaymentId(String paymentId) {
//         throw new UnsupportedOperationException("Not implemented");
//     }
// }