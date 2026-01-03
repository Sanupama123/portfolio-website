package com.bookstore.dao;

import com.bookstore.models.BuyerDetails;
import com.bookstore.utils.DatabaseConfig;
import com.bookstore.utils.IdGenerator;

import java.sql.*;

public class BuyerDetailsDAO {

    public String saveBuyerDetails(BuyerDetails details) throws SQLException {
        // Check if user already has saved details
        BuyerDetails existing = getBuyerDetailsByUserId(details.getUserId());
        
        if (existing != null) {
            // Update existing details
            updateBuyerDetails(details);
            return existing.getDetailsId();
        } else {
            // Create new details
            String detailsId = IdGenerator.generateBuyerDetailsId();
            String sql = "INSERT INTO BuyerDetails (DetailsId, UserId, FullName, Email, PhoneNumber, ShippingAddress, City, PostalCode) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, detailsId);
                stmt.setString(2, details.getUserId());
                stmt.setString(3, details.getFullName());
                stmt.setString(4, details.getEmail());
                stmt.setString(5, details.getPhoneNumber());
                stmt.setString(6, details.getShippingAddress());
                stmt.setString(7, details.getCity());
                stmt.setString(8, details.getPostalCode());
                
                stmt.executeUpdate();
                return detailsId;
            }
        }
    }

    public void updateBuyerDetails(BuyerDetails details) throws SQLException {
        String sql = "UPDATE BuyerDetails SET FullName = ?, Email = ?, PhoneNumber = ?, " +
                    "ShippingAddress = ?, City = ?, PostalCode = ?, UpdatedAt = GETDATE() " +
                    "WHERE UserId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, details.getFullName());
            stmt.setString(2, details.getEmail());
            stmt.setString(3, details.getPhoneNumber());
            stmt.setString(4, details.getShippingAddress());
            stmt.setString(5, details.getCity());
            stmt.setString(6, details.getPostalCode());
            stmt.setString(7, details.getUserId());
            
            stmt.executeUpdate();
        }
    }

    public BuyerDetails getBuyerDetailsByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM BuyerDetails WHERE UserId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBuyerDetails(rs);
                }
            }
        }
        return null;
    }

    public BuyerDetails getBuyerDetailsById(String detailsId) throws SQLException {
        String sql = "SELECT * FROM BuyerDetails WHERE DetailsId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, detailsId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBuyerDetails(rs);
                }
            }
        }
        return null;
    }

    private BuyerDetails mapResultSetToBuyerDetails(ResultSet rs) throws SQLException {
        BuyerDetails details = new BuyerDetails();
        details.setDetailsId(rs.getString("DetailsId"));
        details.setUserId(rs.getString("UserId"));
        details.setFullName(rs.getString("FullName"));
        details.setEmail(rs.getString("Email"));
        details.setPhoneNumber(rs.getString("PhoneNumber"));
        details.setShippingAddress(rs.getString("ShippingAddress"));
        details.setCity(rs.getString("City"));
        details.setPostalCode(rs.getString("PostalCode"));
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
        if (createdAt != null) details.setCreatedAt(createdAt.toLocalDateTime());
        if (updatedAt != null) details.setUpdatedAt(updatedAt.toLocalDateTime());
        
        return details;
    }
}

