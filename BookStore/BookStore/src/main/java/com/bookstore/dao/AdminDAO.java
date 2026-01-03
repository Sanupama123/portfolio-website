package com.bookstore.dao;

import com.bookstore.models.Admin;
import com.bookstore.models.User;
import com.bookstore.utils.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {
    public AdminDAO() {
        DatabaseConfig.createTables();
    }

    public Admin createAdmin(User user, String role) throws SQLException {
        String sql = "INSERT INTO Admin (AdminId, Role) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUserId());
            stmt.setString(2, role);
            
            stmt.executeUpdate();
            
            Admin admin = new Admin();
            admin.setAdminId(user.getUserId());
            admin.setRole(role);
            admin.setUser(user);
            
            return admin;
        }
    }

    public Admin findByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM Admin WHERE AdminId = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAdmin(rs);
                }
            }
        }
        return null;
    }

    public List<Admin> findAllAdmins() throws SQLException {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT a.*, u.* FROM Admin a JOIN [User] u ON a.AdminId = u.UserId";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Admin admin = mapAdmin(rs);
                User user = mapUser(rs);
                admin.setUser(user);
                admins.add(admin);
            }
        }
        return admins;
    }

    public void updateAdmin(Admin admin) throws SQLException {
        String sql = "UPDATE Admin SET Role = ?, LastSystemAccess = ? WHERE AdminId = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, admin.getRole());
            stmt.setTimestamp(2, admin.getLastSystemAccess() != null ? new Timestamp(admin.getLastSystemAccess().getTime()) : null);
            stmt.setString(3, admin.getAdminId());
            
            stmt.executeUpdate();
        }
    }

    public void deleteAdmin(String adminId) throws SQLException {
        String sql = "DELETE FROM Admin WHERE AdminId = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, adminId);
            stmt.executeUpdate();
        }
    }

    private Admin mapAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminId(rs.getString("AdminId"));
        admin.setRole(rs.getString("Role"));
        
        Timestamp lastSystemAccess = rs.getTimestamp("LastSystemAccess");
        if (lastSystemAccess != null) {
            admin.setLastSystemAccess(new java.util.Date(lastSystemAccess.getTime()));
        }
        
        return admin;
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
