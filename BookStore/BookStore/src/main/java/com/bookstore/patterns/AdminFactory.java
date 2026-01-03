package com.bookstore.patterns;

import com.bookstore.dao.AdminDAO;
import com.bookstore.models.Admin;
import com.bookstore.models.User;

/**
 * Concrete Factory for creating Admin users.
 * This is a Concrete Creator in the Factory Method pattern.
 */
public class AdminFactory implements UserFactory {
    private AdminDAO adminDAO;
    
    public AdminFactory() {
        this.adminDAO = new AdminDAO();
    }
    
    @Override
    public User createUser(User user) throws Exception {
        try {
            // Create admin record in database
            adminDAO.createAdmin(user, "ADMIN");
            
            // Load the admin data and set it to the user
            Admin admin = adminDAO.findByUserId(user.getUserId());
            if (admin != null) {
                admin.setUser(user);
                user.setAdmin(admin);
            }
            
            System.out.println("Admin user created successfully: " + user.getUsername());
            return user;
            
        } catch (Exception e) {
            System.err.println("Error creating admin user: " + e.getMessage());
            throw new Exception("Failed to create admin user", e);
        }
    }
    
    @Override
    public String getUserType() {
        return "ADMIN";
    }
}
