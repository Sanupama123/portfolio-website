package com.bookstore.utils;

import com.bookstore.dao.UserDAO;
import com.bookstore.models.User;
import com.bookstore.services.UserService;

public class AdminUserTest {
    public static void main(String[] args) {
        System.out.println("=== Admin User Test ===");
        
        try {
            // Test database connection
            System.out.println("1. Testing database connection...");
            DatabaseConfig.getConnection();
            System.out.println("   ✓ Database connection successful");
            
            // Create tables and admin user
            System.out.println("2. Creating tables and admin user...");
            DatabaseConfig.createTables();
            DatabaseConfig.ensureAdminUser();
            System.out.println("   ✓ Tables and admin user created");
            
            // Test admin user lookup
            System.out.println("3. Testing admin user lookup...");
            UserDAO userDAO = new UserDAO();
            User adminUser = userDAO.findByUsername("admin");
            
            if (adminUser != null) {
                System.out.println("   ✓ Admin user found:");
                System.out.println("     - Username: " + adminUser.getUsername());
                System.out.println("     - Email: " + adminUser.getEmail());
                System.out.println("     - User Type: " + adminUser.getUserType());
                System.out.println("     - Is Active: " + adminUser.isActive());
                System.out.println("     - Is Admin: " + adminUser.isAdmin());
                
                // Test password verification
                System.out.println("4. Testing password verification...");
                UserService userService = new UserService();
                User authUser = userService.authenticate("admin", "admin123");
                
                if (authUser != null) {
                    System.out.println("   ✓ Authentication successful!");
                    System.out.println("   ✓ Admin login should work correctly");
                } else {
                    System.out.println("   ✗ Authentication failed!");
                    System.out.println("   ✗ Admin login will not work");
                }
            } else {
                System.out.println("   ✗ Admin user not found!");
                System.out.println("   ✗ Admin login will not work");
            }
            
        } catch (Exception e) {
            System.err.println("Error during test: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== Test Complete ===");
    }
}
