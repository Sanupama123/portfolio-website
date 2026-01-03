package com.bookstore.dao;

import com.bookstore.models.Seller;
import com.bookstore.models.User;
import com.bookstore.utils.DatabaseConfig;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SellerDAO {
    public SellerDAO() {
        DatabaseConfig.createTables();
    }

    public Seller createSeller(User user, String businessName, BigDecimal commissionRate) throws SQLException {
        String sql = "INSERT INTO Seller (SellerId, BusinessName, CommissionRate) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUserId());
            stmt.setString(2, businessName);
            stmt.setBigDecimal(3, commissionRate);
            
            stmt.executeUpdate();
            
            Seller seller = new Seller();
            seller.setSellerId(user.getUserId());
            seller.setBusinessName(businessName);
            seller.setCommissionRate(commissionRate);
            seller.setTotalSales(BigDecimal.ZERO);
            seller.setUser(user);
            
            return seller;
        }
    }

    public Seller findByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM Seller WHERE SellerId = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapSeller(rs);
                }
            }
        }
        return null;
    }

    public List<Seller> findAllSellers() throws SQLException {
        List<Seller> sellers = new ArrayList<>();
        String sql = "SELECT s.*, u.* FROM Seller s JOIN [User] u ON s.SellerId = u.UserId";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Seller seller = mapSeller(rs);
                User user = mapUser(rs);
                seller.setUser(user);
                sellers.add(seller);
            }
        }
        return sellers;
    }

    public void updateSeller(Seller seller) throws SQLException {
        String sql = "UPDATE Seller SET BusinessName = ?, CommissionRate = ?, TotalSales = ? WHERE SellerId = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, seller.getBusinessName());
            stmt.setBigDecimal(2, seller.getCommissionRate());
            stmt.setBigDecimal(3, seller.getTotalSales());
            stmt.setString(4, seller.getSellerId());
            
            stmt.executeUpdate();
        }
    }

    public void deleteSeller(String sellerId) throws SQLException {
        String sql = "DELETE FROM Seller WHERE SellerId = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sellerId);
            stmt.executeUpdate();
        }
    }

    private Seller mapSeller(ResultSet rs) throws SQLException {
        Seller seller = new Seller();
        seller.setSellerId(rs.getString("SellerId"));
        seller.setBusinessName(rs.getString("BusinessName"));
        seller.setCommissionRate(rs.getBigDecimal("CommissionRate"));
        seller.setTotalSales(rs.getBigDecimal("TotalSales"));
        
        return seller;
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
