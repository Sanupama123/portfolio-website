package com.bookstore.dao;

import com.bookstore.models.Buyer;
import com.bookstore.models.User;
import com.bookstore.utils.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BuyerDAO {
    public BuyerDAO() {
        DatabaseConfig.createTables();
    }

    public Buyer createBuyer(User user, String preferredPaymentMethod) throws SQLException {
        String sql = "INSERT INTO Buyer (BuyerId, PreferredPaymentMethod) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUserId());
            stmt.setString(2, preferredPaymentMethod);
            
            stmt.executeUpdate();
            
            Buyer buyer = new Buyer();
            buyer.setBuyerId(user.getUserId());
            buyer.setPreferredPaymentMethod(preferredPaymentMethod);
            buyer.setUser(user);
            
            return buyer;
        }
    }

    public Buyer findByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM Buyer WHERE BuyerId = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapBuyer(rs);
                }
            }
        }
        return null;
    }

    public List<Buyer> findAllBuyers() throws SQLException {
        List<Buyer> buyers = new ArrayList<>();
        String sql = "SELECT b.*, u.* FROM Buyer b JOIN [User] u ON b.BuyerId = u.UserId";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Buyer buyer = mapBuyer(rs);
                User user = mapUser(rs);
                buyer.setUser(user);
                buyers.add(buyer);
            }
        }
        return buyers;
    }

    public void updateBuyer(Buyer buyer) throws SQLException {
        String sql = "UPDATE Buyer SET PreferredPaymentMethod = ? WHERE BuyerId = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, buyer.getPreferredPaymentMethod());
            stmt.setString(2, buyer.getBuyerId());
            
            stmt.executeUpdate();
        }
    }

    public void deleteBuyer(String buyerId) throws SQLException {
        String sql = "DELETE FROM Buyer WHERE BuyerId = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, buyerId);
            stmt.executeUpdate();
        }
    }

    private Buyer mapBuyer(ResultSet rs) throws SQLException {
        Buyer buyer = new Buyer();
        buyer.setBuyerId(rs.getString("BuyerId"));
        buyer.setPreferredPaymentMethod(rs.getString("PreferredPaymentMethod"));
        
        return buyer;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getString("UserId"));
        user.setUsername(rs.getString("Username"));
        user.setEmail(rs.getString("Email"));
        user.setPassword(rs.getString("Password"));
        user.setFirstName(rs.getString("FirstName"));
        user.setLastName(rs.getString("LastName"));
        user.setPhoneNumber(rs.getString("PhoneNumber"));
        user.setAddress(rs.getString("Address"));
        user.setCreatedAt(rs.getTimestamp("CreatedAt"));
        user.setLastLogin(rs.getTimestamp("LastLogin"));
        user.setActive(rs.getBoolean("IsActive"));
        user.setSuspended(rs.getBoolean("IsSuspended"));
        user.setSystemAccount(rs.getBoolean("IsSystemAccount"));
        
        return user;
    }
}
