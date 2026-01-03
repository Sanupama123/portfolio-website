package com.bookstore.services;

import com.bookstore.dao.UserDAO;
import com.bookstore.models.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class UserService {
    private UserDAO userDAO;
    // WARNING: For testing only. Do NOT enable in production.
    private static final boolean STORE_PLAINTEXT_PASSWORDS = true;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public User authenticate(String username, String password) throws SQLException {
        System.out.println("Authenticating user: " + username);
        User user = userDAO.findByUsername(username);
        
        if (user != null) {
            System.out.println("User found: " + user.getUsername() + " with type: " + user.getUserType());
            System.out.println("Stored password value: " + user.getPassword());
            System.out.println("Input password hash: " + hashPassword(password));
            
            // If plaintext matches exactly, accept (testing mode) and do not migrate
            if (password.equals(user.getPassword())) {
                System.out.println("Plaintext match accepted (testing mode)");
                userDAO.updateLastLogin(user.getUserId());
                return user;
            }

            if (verifyPassword(password, user.getPassword())) {
                System.out.println("Password verification successful");
                userDAO.updateLastLogin(user.getUserId());
                return user;
            } else {
                System.out.println("Password verification failed");
            }
        } else {
            System.out.println("User not found in database");
        }
        return null;
    }

    public boolean registerUser(User user, String role) throws SQLException {
        try {
            // Check if username or email already exists
            if (userDAO.findByUsername(user.getUsername()) != null) {
                throw new SQLException("Username already exists");
            }
            if (userDAO.findByEmail(user.getEmail()) != null) {
                throw new SQLException("Email already exists");
            }

            // Store password based on testing flag
            if (STORE_PLAINTEXT_PASSWORDS) {
                // Keep plaintext for testing purposes
            } else {
                user.setPassword(hashPassword(user.getPassword()));
            }

            // Create user with role
            userDAO.create(user, role);
            return true;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Error during registration: " + e.getMessage());
        }
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private boolean verifyPassword(String inputPassword, String storedHash) {
        String inputHash = hashPassword(inputPassword);
        return inputHash.equals(storedHash);
    }
}