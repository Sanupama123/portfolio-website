package com.bookstore.dao;

import com.bookstore.models.CartItem;
import com.bookstore.utils.DatabaseConfig;
import com.bookstore.utils.IdGenerator;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    public void addToCart(String userId, String bookId, int quantity) throws SQLException {
        String sql = "MERGE INTO Shopping_Cart AS target " +
                    "USING (SELECT ? as UserId, ? as BookId) AS source " +
                    "ON target.UserId = source.UserId AND target.BookId = source.BookId " +
                    "WHEN MATCHED THEN " +
                    "    UPDATE SET Quantity = target.Quantity + ? " +
                    "WHEN NOT MATCHED THEN " +
                    "    INSERT (CartItemId, UserId, BookId, Quantity) VALUES (?, ?, ?, ?);";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.setString(2, bookId);
            stmt.setInt(3, quantity);
            stmt.setString(4, IdGenerator.generateCartItemId());
            stmt.setString(5, userId);
            stmt.setString(6, bookId);
            stmt.setInt(7, quantity);
            
            stmt.executeUpdate();
        }
    }

    public void updateQuantity(String userId, String bookId, int quantity) throws SQLException {
        String sql = "UPDATE Shopping_Cart SET Quantity = ? " +
                    "WHERE UserId = ? AND BookId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantity);
            stmt.setString(2, userId);
            stmt.setString(3, bookId);
            
            stmt.executeUpdate();
        }
    }

    public void removeFromCart(String userId, String bookId) throws SQLException {
        String sql = "DELETE FROM Shopping_Cart WHERE UserId = ? AND BookId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.setString(2, bookId);
            
            stmt.executeUpdate();
        }
    }

    public void clearCart(String userId) throws SQLException {
        String sql = "DELETE FROM Shopping_Cart WHERE UserId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.executeUpdate();
        }
    }

    public List<CartItem> getCartItems(String userId) throws SQLException {
        String sql = "SELECT c.*, b.Title, b.Author, b.Price, b.CoverImagePath " +
                    "FROM Shopping_Cart c " +
                    "JOIN Books b ON c.BookId = b.BookId " +
                    "WHERE c.UserId = ?";

        List<CartItem> items = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setCartItemId(rs.getString("CartItemId"));
                    item.setUserId(rs.getString("UserId"));
                    item.setBookId(rs.getString("BookId"));
                    item.setQuantity(rs.getInt("Quantity"));
                    item.setTitle(rs.getString("Title"));
                    item.setAuthor(rs.getString("Author"));
                    item.setPrice(rs.getDouble("Price"));
                    item.setCoverImagePath(rs.getString("CoverImagePath"));
                    
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

    public int getCartItemCount(String userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Shopping_Cart WHERE UserId = ?";

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

    public double getCartTotal(String userId) throws SQLException {
        String sql = "SELECT SUM(b.Price * c.Quantity) " +
                    "FROM Shopping_Cart c " +
                    "JOIN Books b ON c.BookId = b.BookId " +
                    "WHERE c.UserId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }

        return 0.0;
    }

    public boolean isInCart(String userId, String bookId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Shopping_Cart WHERE UserId = ? AND BookId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.setString(2, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public int getCartItemQuantity(String userId, String bookId) throws SQLException {
        String sql = "SELECT Quantity FROM Shopping_Cart WHERE UserId = ? AND BookId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.setString(2, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Quantity");
                }
            }
        }

        return 0;
    }
}