package com.bookstore.dao;

import com.bookstore.models.CartItem;
import com.bookstore.models.Order;
import com.bookstore.models.OrderItem;
import com.bookstore.utils.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderDAO {

    public Order createOrderFromCart(String userId, List<CartItem> cartItems, double subtotal, double shipping, String shippingAddress) throws SQLException {
        return createOrderWithPayment(userId, cartItems, subtotal, 0.0, shipping, null, shippingAddress, null, null);
    }

    public Order createOrderWithPayment(String userId, List<CartItem> cartItems, double subtotal, double discount, 
                                       double shipping, String couponCode, String buyerDetailsId, String paymentId) throws SQLException {
        String insertOrderSql = "INSERT INTO Orders (OrderId, UserId, OrderNumber, Status, Subtotal, Discount, Shipping, Total, CouponCode, BuyerDetailsId, PaymentId) " +
                "VALUES (?, ?, ?, 'PROCESSING', ?, ?, ?, ?, ?, ?, ?);";
        String insertItemSql = "INSERT INTO Order_Items (OrderItemId, OrderId, BookId, Title, Price, Quantity, Subtotal) VALUES (?,?,?,?,?,?,?);";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String orderNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
                String orderId = generateUniqueOrderId();
                double total = subtotal - discount + shipping;

                try (PreparedStatement stmt = conn.prepareStatement(insertOrderSql)) {
                    stmt.setString(1, orderId);
                    stmt.setString(2, userId);
                    stmt.setString(3, orderNumber);
                    stmt.setDouble(4, subtotal);
                    stmt.setDouble(5, discount);
                    stmt.setDouble(6, shipping);
                    stmt.setDouble(7, total);
                    stmt.setString(8, couponCode);
                    stmt.setString(9, buyerDetailsId);
                    stmt.setString(10, paymentId);
                    stmt.executeUpdate();
                }

                try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSql)) {
                    for (CartItem ci : cartItems) {
                        double lineSubtotal = ci.getPrice() * ci.getQuantity();
                        itemStmt.setString(1, generateUniqueOrderItemId());
                        itemStmt.setString(2, orderId);
                        itemStmt.setString(3, ci.getBookId());
                        itemStmt.setString(4, ci.getTitle());
                        itemStmt.setDouble(5, ci.getPrice());
                        itemStmt.setInt(6, ci.getQuantity());
                        itemStmt.setDouble(7, lineSubtotal);
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }

                conn.commit();

                Order order = new Order();
                order.setOrderId(orderId);
                order.setUserId(userId);
                order.setOrderNumber(orderNumber);
                order.setStatus("PROCESSING");
                order.setSubtotal(subtotal);
                order.setDiscount(discount);
                order.setShipping(shipping);
                order.setTotal(total);
                order.setCouponCode(couponCode);
                order.setBuyerDetailsId(buyerDetailsId);
                order.setPaymentId(paymentId);
                order.setCreatedAt(LocalDateTime.now());
                order.setUpdatedAt(LocalDateTime.now());

                List<OrderItem> items = new ArrayList<>();
                for (CartItem ci : cartItems) {
                    OrderItem oi = new OrderItem();
                    oi.setOrderId(orderId);
                    oi.setBookId(ci.getBookId());
                    oi.setTitle(ci.getTitle());
                    oi.setPrice(ci.getPrice());
                    oi.setQuantity(ci.getQuantity());
                    oi.setSubtotal(ci.getPrice() * ci.getQuantity());
                    items.add(oi);
                }
                order.setItems(items);
                return order;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public Order createOrderWithPayment(String userId, List<CartItem> cartItems, double subtotal, double discount, 
                                       double shipping, String couponCode, String shippingAddress, String buyerDetailsId, String paymentId) throws SQLException {
        String insertOrderSql;
        if (buyerDetailsId != null && paymentId != null) {
            insertOrderSql = "INSERT INTO Orders (OrderId, UserId, OrderNumber, Status, Subtotal, Discount, Shipping, Total, CouponCode, BuyerDetailsId, PaymentId) " +
                    "VALUES (?, ?, ?, 'PROCESSING', ?, ?, ?, ?, ?, ?, ?);";
        } else {
            insertOrderSql = "INSERT INTO Orders (OrderId, UserId, OrderNumber, Status, Subtotal, Discount, Shipping, Total, CouponCode, ShippingAddress) " +
                    "VALUES (?, ?, ?, 'PROCESSING', ?, ?, ?, ?, ?, ?);";
        }
        
        String insertItemSql = "INSERT INTO Order_Items (OrderItemId, OrderId, BookId, Title, Price, Quantity, Subtotal) VALUES (?,?,?,?,?,?,?);";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String orderNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
                String orderId = generateUniqueOrderId();
                double total = subtotal - discount + shipping;

                try (PreparedStatement stmt = conn.prepareStatement(insertOrderSql)) {
                    stmt.setString(1, orderId);
                    stmt.setString(2, userId);
                    stmt.setString(3, orderNumber);
                    stmt.setDouble(4, subtotal);
                    stmt.setDouble(5, discount);
                    stmt.setDouble(6, shipping);
                    stmt.setDouble(7, total);
                    stmt.setString(8, couponCode);
                    if (buyerDetailsId != null && paymentId != null) {
                        stmt.setString(9, buyerDetailsId);
                        stmt.setString(10, paymentId);
                    } else {
                        stmt.setString(9, shippingAddress);
                    }
                    stmt.executeUpdate();
                }

                try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSql)) {
                    for (CartItem ci : cartItems) {
                        double lineSubtotal = ci.getPrice() * ci.getQuantity();
                        itemStmt.setString(1, generateUniqueOrderItemId());
                        itemStmt.setString(2, orderId);
                        itemStmt.setString(3, ci.getBookId());
                        itemStmt.setString(4, ci.getTitle());
                        itemStmt.setDouble(5, ci.getPrice());
                        itemStmt.setInt(6, ci.getQuantity());
                        itemStmt.setDouble(7, lineSubtotal);
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }

                conn.commit();

                Order order = new Order();
                order.setOrderId(orderId);
                order.setUserId(userId);
                order.setOrderNumber(orderNumber);
                order.setStatus("PROCESSING");
                order.setSubtotal(subtotal);
                order.setDiscount(discount);
                order.setShipping(shipping);
                order.setTotal(total);
                order.setCouponCode(couponCode);
                order.setBuyerDetailsId(buyerDetailsId);
                order.setPaymentId(paymentId);
                order.setShippingAddress(shippingAddress);
                order.setCreatedAt(LocalDateTime.now());
                order.setUpdatedAt(LocalDateTime.now());

                List<OrderItem> items = new ArrayList<>();
                for (CartItem ci : cartItems) {
                    OrderItem oi = new OrderItem();
                    oi.setOrderId(orderId);
                    oi.setBookId(ci.getBookId());
                    oi.setTitle(ci.getTitle());
                    oi.setPrice(ci.getPrice());
                    oi.setQuantity(ci.getQuantity());
                    oi.setSubtotal(ci.getPrice() * ci.getQuantity());
                    items.add(oi);
                }
                order.setItems(items);
                return order;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Order> getOrdersByUser(String userId) throws SQLException {
        String sql = "SELECT * FROM Orders WHERE UserId = ? ORDER BY CreatedAt DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        }
        return orders;
    }

    public Order getOrderWithItems(String orderId) throws SQLException {
        String orderSql = "SELECT * FROM Orders WHERE OrderId = ?";
        String itemsSql = "SELECT * FROM Order_Items WHERE OrderId = ?";
        Order order = null;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderSql);
             PreparedStatement itemStmt = conn.prepareStatement(itemsSql)) {
            orderStmt.setString(1, orderId);
            try (ResultSet rs = orderStmt.executeQuery()) {
                if (rs.next()) order = mapOrder(rs);
            }
            if (order == null) return null;
            itemStmt.setString(1, orderId);
            List<OrderItem> items = new ArrayList<>();
            try (ResultSet rs = itemStmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem oi = new OrderItem();
                    oi.setOrderItemId(rs.getString("OrderItemId"));
                    oi.setOrderId(rs.getString("OrderId"));
                    oi.setBookId(rs.getString("BookId"));
                    oi.setTitle(rs.getString("Title"));
                    oi.setPrice(rs.getDouble("Price"));
                    oi.setQuantity(rs.getInt("Quantity"));
                    oi.setSubtotal(rs.getDouble("Subtotal"));
                    items.add(oi);
                }
            }
            order.setItems(items);
        }
        return order;
    }

    public void updateOrderStatus(String orderId, String status) throws SQLException {
        String sql = "UPDATE Orders SET Status = ?, UpdatedAt = GETDATE() WHERE OrderId = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, orderId);
            stmt.executeUpdate();
        }
    }

    public void cancelOrder(String orderId, String userId) throws SQLException {
        String sql = "UPDATE Orders SET Status = 'CANCELLED', UpdatedAt = GETDATE() WHERE OrderId = ? AND UserId = ? AND Status = 'PROCESSING'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderId);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
    }

    public void requestReturn(String orderId, String userId) throws SQLException {
        String sql = "UPDATE Orders SET Status = 'RETURN_REQUESTED', UpdatedAt = GETDATE() WHERE OrderId = ? AND UserId = ? AND Status = 'DELIVERED'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderId);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
    }

    public List<Order> getAllOrders() throws SQLException {
        String sql = "SELECT * FROM Orders ORDER BY CreatedAt DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                orders.add(mapOrder(rs));
            }
        }
        return orders;
    }

    public void deleteOrderAdmin(String orderId) throws SQLException {
        // ON DELETE CASCADE on Order_Items removes items
        String sql = "DELETE FROM Orders WHERE OrderId = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderId);
            stmt.executeUpdate();
        }
    }

    public void deleteOrderBuyer(String orderId, String userId) throws SQLException {
        String sql = "DELETE FROM Orders WHERE OrderId = ? AND UserId = ? AND Status IN ('CANCELLED','DELIVERED')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderId);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
    }

    private String generateUniqueOrderId() {
        String orderId;
        int attempts = 0;
        do {
            orderId = com.bookstore.utils.IdGenerator.generateOrderId();
            attempts++;
            if (attempts > 5) {
                // Fallback to timestamp-based ID if too many attempts
                orderId = "ORD" + System.currentTimeMillis();
                break;
            }
        } while (isOrderIdExists(orderId));
        return orderId;
    }
    
    private boolean isOrderIdExists(String orderId) {
        String sql = "SELECT COUNT(*) FROM Orders WHERE OrderId = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            // If we can't check, assume it doesn't exist and let the database handle it
            return false;
        }
        return false;
    }
    
    private String generateUniqueOrderItemId() {
        String orderItemId;
        int attempts = 0;
        do {
            orderItemId = com.bookstore.utils.IdGenerator.generateOrderItemId();
            attempts++;
            if (attempts > 5) {
                // Fallback to timestamp-based ID if too many attempts
                orderItemId = "OIT" + System.currentTimeMillis();
                break;
            }
        } while (isOrderItemIdExists(orderItemId));
        return orderItemId;
    }
    
    private boolean isOrderItemIdExists(String orderItemId) {
        String sql = "SELECT COUNT(*) FROM Order_Items WHERE OrderItemId = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderItemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            // If we can't check, assume it doesn't exist and let the database handle it
            return false;
        }
        return false;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getString("OrderId"));
        o.setUserId(rs.getString("UserId"));
        o.setOrderNumber(rs.getString("OrderNumber"));
        o.setStatus(rs.getString("Status"));
        o.setSubtotal(rs.getDouble("Subtotal"));
        o.setShipping(rs.getDouble("Shipping"));
        o.setTotal(rs.getDouble("Total"));
        o.setShippingAddress(rs.getString("ShippingAddress"));
        Timestamp c = rs.getTimestamp("CreatedAt");
        Timestamp u = rs.getTimestamp("UpdatedAt");
        if (c != null) o.setCreatedAt(c.toLocalDateTime());
        if (u != null) o.setUpdatedAt(u.toLocalDateTime());
        return o;
    }
}


