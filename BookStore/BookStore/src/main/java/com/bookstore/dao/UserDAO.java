package com.bookstore.dao;

import com.bookstore.models.Admin;
import com.bookstore.models.Buyer;
import com.bookstore.models.Seller;
import com.bookstore.models.User;
import com.bookstore.utils.DatabaseConfig;
import com.bookstore.patterns.UserFactory;
import com.bookstore.patterns.UserFactoryProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private BuyerDAO buyerDAO;
    private SellerDAO sellerDAO;
    private AdminDAO adminDAO;
    
    public UserDAO() {
        // Ensure tables are created when DAO is initialized
        DatabaseConfig.createTables();
        // Ensure admin user exists
        DatabaseConfig.ensureAdminUser();
        this.buyerDAO = new BuyerDAO();
        this.sellerDAO = new SellerDAO();
        this.adminDAO = new AdminDAO();
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM [User] WHERE Username = ?";
        
        System.out.println("Searching for user with username: " + username);
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("User found in database");
                    User user = mapUser(rs);
                    loadUserSubclasses(user);
                    return user;
                } else {
                    System.out.println("No user found with username: " + username);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in findByUsername: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM [User] WHERE Email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapUser(rs);
                    loadUserSubclasses(user);
                    return user;
                }
            }
        }
        return null;
    }

    public List<User> listAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM [User] ORDER BY UserId DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User user = mapUser(rs);
                loadUserSubclasses(user);
                users.add(user);
            }
        }
        return users;
    }

    public void create(User user, String userType) throws SQLException {
        String userId = com.bookstore.utils.IdGenerator.generateUserId();
        user.setUserId(userId);
        
        String sql = "INSERT INTO [User] (UserId, Username, Email, Password, FirstName, LastName, PhoneNumber, Address) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getFirstName());
            stmt.setString(6, user.getLastName());
            stmt.setString(7, user.getPhoneNumber());
            stmt.setString(8, user.getAddress());
            
            stmt.executeUpdate();
            
            // Use Factory Method pattern to create user subclass
            try {
                UserFactory factory = UserFactoryProvider.getFactory(userType);
                factory.createUser(user);
                System.out.println("User created using Factory Method pattern: " + userType);
            } catch (Exception e) {
                System.err.println("Error creating user subclass: " + e.getMessage());
                throw new SQLException("Failed to create user subclass", e);
            }
        }
    }

    public void updateProfile(User user) throws SQLException {
        String sql = "UPDATE [User] SET FirstName=?, LastName=?, PhoneNumber=?, Address=? WHERE UserId=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getAddress());
            stmt.setString(5, user.getUserId());
            stmt.executeUpdate();
        }
    }

    public void updateLastLogin(String userId) throws SQLException {
        String sql = "UPDATE [User] SET LastLogin = GETDATE() WHERE UserId = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.executeUpdate();
        }
    }

    public void updatePassword(String userId, String newHashedPassword) throws SQLException {
        String sql = "UPDATE [User] SET Password = ? WHERE UserId = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHashedPassword);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
    }

    public void setSuspended(String userId, boolean suspended) throws SQLException {
        String sql = "UPDATE [User] SET IsSuspended = ?, IsActive = CASE WHEN ? = 1 THEN 0 ELSE 1 END WHERE UserId = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, suspended);
            stmt.setBoolean(2, suspended);
            stmt.setString(3, userId);
            stmt.executeUpdate();
        }
    }

    public void deleteById(String userId) throws SQLException {
        String sql = "DELETE FROM [User] WHERE UserId=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        }
    }

    // Helper method to load user subclasses
    private void loadUserSubclasses(User user) throws SQLException {
        // Load Buyer
        Buyer buyer = buyerDAO.findByUserId(user.getUserId());
        if (buyer != null) {
            buyer.setUser(user);
            user.setBuyer(buyer);
        }
        
        // Load Seller
        Seller seller = sellerDAO.findByUserId(user.getUserId());
        if (seller != null) {
            seller.setUser(user);
            user.setSeller(seller);
        }
        
        // Load Admin
        Admin admin = adminDAO.findByUserId(user.getUserId());
        if (admin != null) {
            admin.setUser(user);
            user.setAdmin(admin);
        }
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
        
        try {
            user.setCreatedAt(rs.getTimestamp("CreatedAt"));
            user.setLastLogin(rs.getTimestamp("LastLogin"));
            user.setActive(rs.getBoolean("IsActive"));
            user.setSuspended(rs.getBoolean("IsSuspended"));
            user.setSystemAccount(rs.getBoolean("IsSystemAccount"));
        } catch (SQLException e) {
            // These fields might be null or not available, which is fine
            user.setActive(true);
        }
        
        return user;
    }
}