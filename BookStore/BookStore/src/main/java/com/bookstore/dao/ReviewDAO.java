package com.bookstore.dao;

import com.bookstore.models.Review;
import com.bookstore.utils.DatabaseConfig;
import com.bookstore.utils.IdGenerator;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewDAO {
    public void addOrUpdateReview(String userId, String bookId, int rating, String reviewText) throws SQLException {
        String sql = "MERGE INTO Book_Reviews AS target " +
                    "USING (VALUES (?, ?, ?, ?, ?, GETDATE(), GETDATE())) " +
                    "AS source (ReviewId, UserId, BookId, Rating, ReviewText, CreatedAt, UpdatedAt) " +
                    "ON target.UserId = source.UserId AND target.BookId = source.BookId " +
                    "WHEN MATCHED THEN " +
                    "    UPDATE SET Rating = source.Rating, " +
                    "               ReviewText = source.ReviewText, " +
                    "               UpdatedAt = GETDATE() " +
                    "WHEN NOT MATCHED THEN " +
                    "    INSERT (ReviewId, UserId, BookId, Rating, ReviewText, CreatedAt, UpdatedAt) " +
                    "    VALUES (source.ReviewId, source.UserId, source.BookId, source.Rating, source.ReviewText, " +
                    "            source.CreatedAt, source.UpdatedAt);";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, IdGenerator.generateReviewId());
            stmt.setString(2, userId);
            stmt.setString(3, bookId);
            stmt.setInt(4, rating);
            stmt.setString(5, reviewText);
            
            stmt.executeUpdate();
        }
    }
    public void addReview(Review review) throws SQLException {
        String reviewId = IdGenerator.generateReviewId();
        String sql = "INSERT INTO Book_Reviews (ReviewId, BookId, UserId, Rating, ReviewText, IsVerifiedPurchase) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, reviewId);
            stmt.setString(2, review.getBookId());
            stmt.setString(3, review.getUserId());
            stmt.setInt(4, review.getRating());
            stmt.setString(5, review.getReviewText());
            stmt.setBoolean(6, review.isVerifiedPurchase());
            
            stmt.executeUpdate();
            review.setReviewId(reviewId);
        }
    }

    public void updateReview(Review review) throws SQLException {
        String sql = "UPDATE Book_Reviews SET Rating = ?, ReviewText = ?, UpdatedAt = GETDATE() " +
                    "WHERE ReviewId = ? AND UserId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, review.getRating());
            stmt.setString(2, review.getReviewText());
            stmt.setString(3, review.getReviewId());
            stmt.setString(4, review.getUserId());
            
            stmt.executeUpdate();
        }
    }

    public void deleteReviewById(String reviewId) throws SQLException {
        String sql = "DELETE FROM Book_Reviews WHERE ReviewId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, reviewId);
            
            stmt.executeUpdate();
        }
    }

    public void deleteReviewByUserAndBook(String userId, String bookId) throws SQLException {
        String sql = "DELETE FROM Book_Reviews WHERE UserId = ? AND BookId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, bookId);
            stmt.executeUpdate();
        }
    }

    public List<Review> getBookReviews(String bookId) throws SQLException {
        String sql = "SELECT r.*, u.Username, b.Title AS BookTitle " +
                    "FROM Book_Reviews r " +
                    "JOIN [User] u ON r.UserId = u.UserId " +
                    "JOIN Books b ON r.BookId = b.BookId " +
                    "WHERE r.BookId = ? " +
                    "ORDER BY r.CreatedAt DESC";

        List<Review> reviews = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
        }

        return reviews;
    }

    public Review getUserReviewForBook(String userId, String bookId) throws SQLException {
        String sql = "SELECT r.*, u.Username, b.Title AS BookTitle " +
                    "FROM Book_Reviews r " +
                    "JOIN [User] u ON r.UserId = u.UserId " +
                    "JOIN Books b ON r.BookId = b.BookId " +
                    "WHERE r.BookId = ? AND r.UserId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bookId);
            stmt.setString(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReview(rs);
                }
            }
        }

        return null;
    }

    public double getAverageRating(String bookId) throws SQLException {
        String sql = "SELECT AVG(CAST(Rating AS FLOAT)) " +
                    "FROM Book_Reviews WHERE BookId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }

        return 0.0;
    }

    public int getReviewCount(String bookId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Book_Reviews WHERE BookId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    public Map<Integer, Integer> getRatingDistribution(String bookId) throws SQLException {
        String sql = "SELECT Rating, COUNT(*) as Count " +
                    "FROM Book_Reviews " +
                    "WHERE BookId = ? " +
                    "GROUP BY Rating " +
                    "ORDER BY Rating";

        Map<Integer, Integer> distribution = new HashMap<>();
        
        // Initialize counts for all ratings (1-5)
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0);
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    distribution.put(rs.getInt("Rating"), rs.getInt("Count"));
                }
            }
        }

        return distribution;
    }

    public List<Review> getRecentReviews(int limit) throws SQLException {
        String sql = "SELECT TOP(" + limit + ") r.*, u.Username, b.Title AS BookTitle " +
                "FROM Book_Reviews r " +
                "JOIN [User] u ON r.UserId = u.UserId " +
                "JOIN Books b ON r.BookId = b.BookId " +
                "ORDER BY r.CreatedAt DESC";

        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
        }
        return reviews;
    }

    public List<Review> getReviewsForSeller(String sellerId) throws SQLException {
        String sql = "SELECT r.*, u.Username, b.Title AS BookTitle " +
                "FROM Book_Reviews r " +
                "JOIN [User] u ON r.UserId = u.UserId " +
                "JOIN Books b ON r.BookId = b.BookId " +
                "WHERE b.SellerId = ? " +
                "ORDER BY b.Title ASC, r.CreatedAt DESC";

        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
        }
        return reviews;
    }

    public List<Review> getUserReviews(String userId) throws SQLException {
        String sql = "SELECT r.*, u.Username, b.Title AS BookTitle " +
                "FROM Book_Reviews r " +
                "JOIN [User] u ON r.UserId = u.UserId " +
                "JOIN Books b ON r.BookId = b.BookId " +
                "WHERE r.UserId = ? " +
                "ORDER BY r.CreatedAt DESC";

        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
        }
        return reviews;
    }

    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getString("ReviewId"));
        review.setBookId(rs.getString("BookId"));
        review.setUserId(rs.getString("UserId"));
        review.setRating(rs.getInt("Rating"));
        review.setReviewText(rs.getString("ReviewText"));
        review.setVerifiedPurchase(rs.getBoolean("IsVerifiedPurchase"));
        review.setUsername(rs.getString("Username"));
        try { review.setBookTitle(rs.getString("BookTitle")); } catch (SQLException ignored) {}
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            review.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
        if (updatedAt != null) {
            review.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return review;
    }
}