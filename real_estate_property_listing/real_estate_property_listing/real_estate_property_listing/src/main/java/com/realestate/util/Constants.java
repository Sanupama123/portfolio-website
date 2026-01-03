package com.realestate.util;

/**
 * Constants used throughout the application
 */
public class Constants {
    
    // Data file names
    public static final String USERS_FILE = "users.txt";
    public static final String ADMIN_FILE = "Admin.txt";
    public static final String SELLER_FILE = "Seller.txt";
    public static final String BUYER_FILE = "Buyer.txt";
    public static final String PROPERTY_FILE = "Property.txt";
    
    // Path constants
    public static final String DATA_DIR_NAME = "data";
    public static final String WEBAPP_DIR_PATH = "src/main/webapp";
    
    // Status messages
    public static final String SUCCESS_MESSAGE = "Operation completed successfully.";
    public static final String ERROR_MESSAGE = "An error occurred during the operation.";
    
    // User roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SELLER = "SELLER";
    public static final String ROLE_BUYER = "BUYER";
    
    // Session attributes
    public static final String SESSION_USER = "user";
    public static final String SESSION_ROLE = "role";
    
    // Request attributes
    public static final String ATTR_MESSAGE = "message";
    public static final String ATTR_ERROR = "error";
    
    private Constants() {
        // Private constructor to prevent instantiation
    }
} 