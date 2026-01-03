package com.bookstore.dao;

import com.bookstore.models.WishlistItem;
import com.bookstore.utils.DatabaseConfig;
import com.bookstore.utils.IdGenerator;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {
    public void addToWishlist(String userId, String bookId) throws SQLException {
        String sql = "IF NOT EXISTS (SELECT 1 FROM Wishlist WHERE UserId = ? AND BookId = ?) " +
                    "INSERT INTO Wishlist (WishlistItemId, UserId, BookId) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.setString(2, bookId);
            stmt.setString(3, IdGenerator.generateWishlistItemId());
            stmt.setString(4, userId);
            stmt.setString(5, bookId);
            
            stmt.executeUpdate();
        }
    }

    public void removeFromWishlist(String userId, String bookId) throws SQLException {
        String sql = "DELETE FROM Wishlist WHERE UserId = ? AND BookId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.setString(2, bookId);
            
            stmt.executeUpdate();
        }
    }

    public List<WishlistItem> getWishlistItems(String userId) throws SQLException {
        String sql = "SELECT w.*, b.Title, b.Author, b.Price, b.CoverImagePath, b.StockQuantity " +
                    "FROM Wishlist w " +
                    "JOIN Books b ON w.BookId = b.BookId " +
                    "WHERE w.UserId = ?";

        List<WishlistItem> items = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    WishlistItem item = new WishlistItem();
                    item.setWishlistItemId(rs.getString("WishlistItemId"));
                    item.setUserId(rs.getString("UserId"));
                    item.setBookId(rs.getString("BookId"));
                    item.setTitle(rs.getString("Title"));
                    item.setAuthor(rs.getString("Author"));
                    item.setPrice(rs.getDouble("Price"));
                    item.setCoverImagePath(rs.getString("CoverImagePath"));
                    item.setInStock(rs.getInt("StockQuantity") > 0);
                    
                    Timestamp addedAt = rs.getTimestamp("AddedAt");
                    if (addedAt != null) {
                        item.setAddedAt(addedAt.toLocalDateTime());
                    }
                    
                    items.add(item);
                }
            }
        }

        return items;
    }

    public boolean isInWishlist(String userId, String bookId) throws SQLException {
        String sql = "SELECT 1 FROM Wishlist WHERE UserId = ? AND BookId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.setString(2, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int getWishlistCount(String userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Wishlist WHERE UserId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }
}