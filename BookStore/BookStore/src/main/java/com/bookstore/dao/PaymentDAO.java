package com.bookstore.dao;

import com.bookstore.models.Payment;
import com.bookstore.utils.DatabaseConfig;
import com.bookstore.utils.IdGenerator;

import java.sql.*;
import java.util.UUID;

public class PaymentDAO {

    public String createPayment(Payment payment) throws SQLException {
        String paymentId = generateUniquePaymentId();
        String sql = "INSERT INTO Payments (PaymentId, OrderId, PaymentMethod, PaymentStatus, Amount, TransactionId, CardId) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paymentId);
            stmt.setString(2, payment.getOrderId());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setString(4, payment.getPaymentStatus());
            stmt.setDouble(5, payment.getAmount());
            stmt.setString(6, payment.getTransactionId());
            stmt.setString(7, payment.getCardId());
            
            stmt.executeUpdate();
            return paymentId;
        }
    }
    
    private String generateUniquePaymentId() {
        String paymentId;
        int attempts = 0;
        do {
            paymentId = IdGenerator.generatePaymentId();
            attempts++;
            if (attempts > 5) {
                // Fallback to timestamp-based ID if too many attempts
                paymentId = "PAY" + System.currentTimeMillis();
                break;
            }
        } while (isPaymentIdExists(paymentId));
        return paymentId;
    }
    
    private boolean isPaymentIdExists(String paymentId) {
        String sql = "SELECT COUNT(*) FROM Payments WHERE PaymentId = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paymentId);
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

    public void updatePaymentStatus(String paymentId, String status, String transactionId) throws SQLException {
        String sql = "UPDATE Payments SET PaymentStatus = ?, TransactionId = ?, UpdatedAt = GETDATE() WHERE PaymentId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setString(2, transactionId);
            stmt.setString(3, paymentId);
            
            stmt.executeUpdate();
        }
    }

    public Payment getPaymentById(String paymentId) throws SQLException {
        String sql = "SELECT * FROM Payments WHERE PaymentId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paymentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        }
        return null;
    }

    public Payment getPaymentByOrderId(String orderId) throws SQLException {
        String sql = "SELECT * FROM Payments WHERE OrderId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        }
        return null;
    }

    public String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getString("PaymentId"));
        payment.setOrderId(rs.getString("OrderId"));
        payment.setPaymentMethod(rs.getString("PaymentMethod"));
        payment.setPaymentStatus(rs.getString("PaymentStatus"));
        payment.setAmount(rs.getDouble("Amount"));
        payment.setTransactionId(rs.getString("TransactionId"));
        payment.setCardId(rs.getString("CardId"));
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
        if (createdAt != null) payment.setCreatedAt(createdAt.toLocalDateTime());
        if (updatedAt != null) payment.setUpdatedAt(updatedAt.toLocalDateTime());
        
        return payment;
    }
}

