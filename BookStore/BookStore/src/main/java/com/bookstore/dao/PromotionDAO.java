package com.bookstore.dao;

import com.bookstore.models.Promotion;
import com.bookstore.models.Book;
import com.bookstore.utils.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {
    
    public PromotionDAO() {
        // Ensure database tables are created
        DatabaseConfig.createTables();
    }
    
    public void create(Promotion promotion) throws SQLException {
        String promotionId = com.bookstore.utils.IdGenerator.generatePromotionId();
        promotion.setPromotionId(promotionId);
        
        String sql = "INSERT INTO Promotions (PromotionId, Name, Description, Type, DiscountType, DiscountValue, " +
                    "MinOrderAmount, MaxDiscountAmount, UsageLimit, StartDate, EndDate, IsActive) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, promotion.getPromotionId());
            stmt.setString(2, promotion.getName());
            stmt.setString(3, promotion.getDescription());
            stmt.setString(4, promotion.getType());
            stmt.setString(5, promotion.getDiscountType());
            stmt.setDouble(6, promotion.getDiscountValue());
            stmt.setDouble(7, promotion.getMinOrderAmount());
            stmt.setObject(8, promotion.getMaxDiscountAmount());
            stmt.setObject(9, promotion.getUsageLimit());
            stmt.setTimestamp(10, Timestamp.valueOf(promotion.getStartDate()));
            stmt.setTimestamp(11, Timestamp.valueOf(promotion.getEndDate()));
            stmt.setBoolean(12, promotion.isActive());
            
            stmt.executeUpdate();
        }
    }

    public void update(Promotion promotion) throws SQLException {
        String sql = "UPDATE Promotions SET Name=?, Description=?, Type=?, DiscountType=?, " +
                    "DiscountValue=?, MinOrderAmount=?, MaxDiscountAmount=?, UsageLimit=?, " +
                    "StartDate=?, EndDate=?, IsActive=?, UpdatedAt=GETDATE() WHERE PromotionId=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, promotion.getName());
            stmt.setString(2, promotion.getDescription());
            stmt.setString(3, promotion.getType());
            stmt.setString(4, promotion.getDiscountType());
            stmt.setDouble(5, promotion.getDiscountValue());
            stmt.setDouble(6, promotion.getMinOrderAmount());
            stmt.setObject(7, promotion.getMaxDiscountAmount());
            stmt.setObject(8, promotion.getUsageLimit());
            stmt.setTimestamp(9, Timestamp.valueOf(promotion.getStartDate()));
            stmt.setTimestamp(10, Timestamp.valueOf(promotion.getEndDate()));
            stmt.setBoolean(11, promotion.isActive());
            stmt.setString(12, promotion.getPromotionId());
            
            stmt.executeUpdate();
        }
    }

    public void delete(String promotionId) throws SQLException {
        String sql = "DELETE FROM Promotions WHERE PromotionId=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, promotionId);
            stmt.executeUpdate();
        }
    }

    public Promotion findById(String promotionId) throws SQLException {
        String sql = "SELECT * FROM Promotions WHERE PromotionId=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, promotionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPromotion(rs);
                }
            }
        }
        return null;
    }

    public List<Promotion> findAll() throws SQLException {
        String sql = "SELECT * FROM Promotions ORDER BY CreatedAt DESC";
        List<Promotion> promotions = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Promotion promotion = mapResultSetToPromotion(rs);
                    promotion.setStatus(determineStatus(promotion));
                    promotions.add(promotion);
                }
            }
        }
        return promotions;
    }

    public List<Promotion> findByType(String type) throws SQLException {
        String sql = "SELECT * FROM Promotions WHERE Type=? ORDER BY CreatedAt DESC";
        List<Promotion> promotions = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Promotion promotion = mapResultSetToPromotion(rs);
                    promotion.setStatus(determineStatus(promotion));
                    promotions.add(promotion);
                }
            }
        }
        return promotions;
    }

    public List<Promotion> findActive() throws SQLException {
        String sql = "SELECT * FROM Promotions WHERE IsActive=1 AND StartDate<=GETDATE() AND EndDate>=GETDATE() ORDER BY CreatedAt DESC";
        List<Promotion> promotions = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Promotion promotion = mapResultSetToPromotion(rs);
                    promotion.setStatus("ACTIVE");
                    promotions.add(promotion);
                }
            }
        }
        return promotions;
    }

    public void addBooksToPromotion(String promotionId, List<String> bookIds) throws SQLException {
        String sql = "INSERT INTO PromotionBooks (PromotionId, BookId) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (String bookId : bookIds) {
                stmt.setString(1, promotionId);
                stmt.setString(2, bookId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public void removeBooksFromPromotion(String promotionId) throws SQLException {
        String sql = "DELETE FROM PromotionBooks WHERE PromotionId=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, promotionId);
            stmt.executeUpdate();
        }
    }

    public List<Book> getBooksForPromotion(String promotionId) throws SQLException {
        String sql = "SELECT b.* FROM Books b " +
                    "JOIN PromotionBooks pb ON b.BookId = pb.BookId " +
                    "WHERE pb.PromotionId = ?";
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, promotionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setBookId(rs.getString("BookId"));
                    book.setTitle(rs.getString("Title"));
                    book.setAuthor(rs.getString("Author"));
                    book.setPrice(rs.getDouble("Price"));
                    // Note: Stock field may not exist in Book model, skipping for now
                    books.add(book);
                }
            }
        }
        return books;
    }

    public void incrementUsage(String promotionId) throws SQLException {
        String sql = "UPDATE Promotions SET UsedCount = UsedCount + 1 WHERE PromotionId = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, promotionId);
            stmt.executeUpdate();
        }
    }

    private Promotion mapResultSetToPromotion(ResultSet rs) throws SQLException {
        Promotion promotion = new Promotion();
        promotion.setPromotionId(rs.getString("PromotionId"));
        promotion.setName(rs.getString("Name"));
        promotion.setDescription(rs.getString("Description"));
        promotion.setType(rs.getString("Type"));
        promotion.setDiscountType(rs.getString("DiscountType"));
        promotion.setDiscountValue(rs.getDouble("DiscountValue"));
        promotion.setMinOrderAmount(rs.getDouble("MinOrderAmount"));
        promotion.setMaxDiscountAmount(rs.getObject("MaxDiscountAmount", Double.class));
        promotion.setUsageLimit(rs.getObject("UsageLimit", Integer.class));
        promotion.setUsedCount(rs.getInt("UsedCount"));
        promotion.setActive(rs.getBoolean("IsActive"));
        
        Timestamp startDate = rs.getTimestamp("StartDate");
        if (startDate != null) {
            promotion.setStartDate(startDate.toLocalDateTime());
        }
        
        Timestamp endDate = rs.getTimestamp("EndDate");
        if (endDate != null) {
            promotion.setEndDate(endDate.toLocalDateTime());
        }
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            promotion.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
        if (updatedAt != null) {
            promotion.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return promotion;
    }

    private String determineStatus(Promotion promotion) {
        if (!promotion.isActive()) {
            return "INACTIVE";
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (promotion.getStartDate() != null && promotion.getStartDate().isAfter(now)) {
            return "UPCOMING";
        }
        
        if (promotion.getEndDate() != null && promotion.getEndDate().isBefore(now)) {
            return "EXPIRED";
        }
        
        return "ACTIVE";
    }
}
