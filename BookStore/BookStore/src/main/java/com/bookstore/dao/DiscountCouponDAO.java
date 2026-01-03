package com.bookstore.dao;

import com.bookstore.models.DiscountCoupon;
import com.bookstore.utils.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DiscountCouponDAO {
    
    public DiscountCouponDAO() {
        // Ensure database tables are created
        DatabaseConfig.createTables();
    }
    
    public void create(DiscountCoupon coupon) throws SQLException {
        String couponId = com.bookstore.utils.IdGenerator.generateCouponId();
        coupon.setCouponId(couponId);
        
        String sql = "INSERT INTO DiscountCoupons (CouponId, Code, Description, DiscountType, DiscountValue, " +
                    "MinOrderAmount, MaxDiscountAmount, UsageLimit, StartDate, EndDate, IsActive) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, coupon.getCouponId());
            stmt.setString(2, coupon.getCode());
            stmt.setString(3, coupon.getDescription());
            stmt.setString(4, coupon.getDiscountType());
            stmt.setDouble(5, coupon.getDiscountValue());
            stmt.setDouble(6, coupon.getMinOrderAmount());
            stmt.setObject(7, coupon.getMaxDiscountAmount());
            stmt.setObject(8, coupon.getUsageLimit());
            stmt.setTimestamp(9, Timestamp.valueOf(coupon.getStartDate()));
            stmt.setTimestamp(10, Timestamp.valueOf(coupon.getEndDate()));
            stmt.setBoolean(11, coupon.isActive());
            
            stmt.executeUpdate();
        }
    }

    public void update(DiscountCoupon coupon) throws SQLException {
        String sql = "UPDATE DiscountCoupons SET Code=?, Description=?, DiscountType=?, " +
                    "DiscountValue=?, MinOrderAmount=?, MaxDiscountAmount=?, UsageLimit=?, " +
                    "StartDate=?, EndDate=?, IsActive=?, UpdatedAt=GETDATE() WHERE CouponId=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, coupon.getCode());
            stmt.setString(2, coupon.getDescription());
            stmt.setString(3, coupon.getDiscountType());
            stmt.setDouble(4, coupon.getDiscountValue());
            stmt.setDouble(5, coupon.getMinOrderAmount());
            stmt.setObject(6, coupon.getMaxDiscountAmount());
            stmt.setObject(7, coupon.getUsageLimit());
            stmt.setTimestamp(8, Timestamp.valueOf(coupon.getStartDate()));
            stmt.setTimestamp(9, Timestamp.valueOf(coupon.getEndDate()));
            stmt.setBoolean(10, coupon.isActive());
            stmt.setString(11, coupon.getCouponId());
            
            stmt.executeUpdate();
        }
    }

    public void delete(String couponId) throws SQLException {
        String sql = "DELETE FROM DiscountCoupons WHERE CouponId=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, couponId);
            stmt.executeUpdate();
        }
    }

    public DiscountCoupon findById(String couponId) throws SQLException {
        String sql = "SELECT * FROM DiscountCoupons WHERE CouponId=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, couponId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCoupon(rs);
                }
            }
        }
        return null;
    }

    public DiscountCoupon findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM DiscountCoupons WHERE Code=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCoupon(rs);
                }
            }
        }
        return null;
    }

    public List<DiscountCoupon> findAll() throws SQLException {
        String sql = "SELECT * FROM DiscountCoupons ORDER BY CreatedAt DESC";
        List<DiscountCoupon> coupons = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DiscountCoupon coupon = mapResultSetToCoupon(rs);
                    coupon.setStatus(determineStatus(coupon));
                    coupons.add(coupon);
                }
            }
        }
        return coupons;
    }

    public List<DiscountCoupon> findActive() throws SQLException {
        String sql = "SELECT * FROM DiscountCoupons WHERE IsActive=1 AND StartDate<=GETDATE() AND EndDate>=GETDATE() ORDER BY CreatedAt DESC";
        List<DiscountCoupon> coupons = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DiscountCoupon coupon = mapResultSetToCoupon(rs);
                    coupon.setStatus("ACTIVE");
                    coupons.add(coupon);
                }
            }
        }
        return coupons;
    }

    public boolean isCodeUnique(String code, String excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DiscountCoupons WHERE Code=? AND CouponId!=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            stmt.setString(2, excludeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        }
        return false;
    }

    public boolean isCodeUnique(String code) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DiscountCoupons WHERE Code=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        }
        return false;
    }

    public void recordUsage(String couponId, String userId, String orderId) throws SQLException {
        String usageId = com.bookstore.utils.IdGenerator.generateCouponUsageId();
        String sql = "INSERT INTO CouponUsage (UsageId, CouponId, UserId, OrderId) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usageId);
            stmt.setString(2, couponId);
            stmt.setString(3, userId);
            stmt.setString(4, orderId);
            stmt.executeUpdate();
            
            // Increment usage count
            incrementUsage(couponId);
        }
    }

    public void incrementUsage(String couponId) throws SQLException {
        String sql = "UPDATE DiscountCoupons SET UsedCount = UsedCount + 1 WHERE CouponId = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, couponId);
            stmt.executeUpdate();
        }
    }

    public boolean hasUserUsedCoupon(String couponId, String userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM CouponUsage WHERE CouponId=? AND UserId=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, couponId);
            stmt.setString(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<DiscountCoupon> getCouponsUsedByUser(String userId) throws SQLException {
        String sql = "SELECT DISTINCT c.* FROM DiscountCoupons c " +
                    "JOIN CouponUsage cu ON c.CouponId = cu.CouponId " +
                    "WHERE cu.UserId = ? ORDER BY cu.UsedAt DESC";
        List<DiscountCoupon> coupons = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DiscountCoupon coupon = mapResultSetToCoupon(rs);
                    coupon.setStatus(determineStatus(coupon));
                    coupons.add(coupon);
                }
            }
        }
        return coupons;
    }

    public double calculateDiscount(DiscountCoupon coupon, double orderAmount) {
        if (coupon == null) return 0.0;
        
        // Check minimum order amount
        if (orderAmount < coupon.getMinOrderAmount()) {
            return 0.0;
        }
        
        double discount = 0.0;
        if ("PERCENTAGE".equals(coupon.getDiscountType())) {
            discount = orderAmount * (coupon.getDiscountValue() / 100.0);
            // Apply max discount limit if exists
            if (coupon.getMaxDiscountAmount() != null && discount > coupon.getMaxDiscountAmount()) {
                discount = coupon.getMaxDiscountAmount();
            }
        } else if ("FIXED".equals(coupon.getDiscountType())) {
            discount = coupon.getDiscountValue();
        }
        
        // Ensure discount doesn't exceed order amount
        if (discount > orderAmount) {
            discount = orderAmount;
        }
        
        return discount;
    }

    public String validateCoupon(String code, String userId, double orderAmount) throws SQLException {
        DiscountCoupon coupon = findByCode(code);
        
        if (coupon == null) {
            return "Invalid coupon code";
        }
        
        if (!coupon.isActive()) {
            return "This coupon is not active";
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartDate() != null && coupon.getStartDate().isAfter(now)) {
            return "This coupon is not yet valid";
        }
        
        if (coupon.getEndDate() != null && coupon.getEndDate().isBefore(now)) {
            return "This coupon has expired";
        }
        
        if (coupon.hasReachedUsageLimit()) {
            return "This coupon has reached its usage limit";
        }
        
        if (hasUserUsedCoupon(coupon.getCouponId(), userId)) {
            return "You have already used this coupon";
        }
        
        if (orderAmount < coupon.getMinOrderAmount()) {
            return String.format("Minimum order amount for this coupon is $%.2f", coupon.getMinOrderAmount());
        }
        
        return null; // null means valid
    }

    private DiscountCoupon mapResultSetToCoupon(ResultSet rs) throws SQLException {
        DiscountCoupon coupon = new DiscountCoupon();
        coupon.setCouponId(rs.getString("CouponId"));
        coupon.setCode(rs.getString("Code"));
        coupon.setDescription(rs.getString("Description"));
        coupon.setDiscountType(rs.getString("DiscountType"));
        coupon.setDiscountValue(rs.getDouble("DiscountValue"));
        coupon.setMinOrderAmount(rs.getDouble("MinOrderAmount"));
        coupon.setMaxDiscountAmount(rs.getObject("MaxDiscountAmount", Double.class));
        coupon.setUsageLimit(rs.getObject("UsageLimit", Integer.class));
        coupon.setUsedCount(rs.getInt("UsedCount"));
        coupon.setActive(rs.getBoolean("IsActive"));
        
        Timestamp startDate = rs.getTimestamp("StartDate");
        if (startDate != null) {
            coupon.setStartDate(startDate.toLocalDateTime());
        }
        
        Timestamp endDate = rs.getTimestamp("EndDate");
        if (endDate != null) {
            coupon.setEndDate(endDate.toLocalDateTime());
        }
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            coupon.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
        if (updatedAt != null) {
            coupon.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return coupon;
    }

    private String determineStatus(DiscountCoupon coupon) {
        if (!coupon.isActive()) {
            return "INACTIVE";
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartDate() != null && coupon.getStartDate().isAfter(now)) {
            return "UPCOMING";
        }
        
        if (coupon.getEndDate() != null && coupon.getEndDate().isBefore(now)) {
            return "EXPIRED";
        }
        
        if (coupon.hasReachedUsageLimit()) {
            return "LIMIT_REACHED";
        }
        
        return "ACTIVE";
    }
}
