package com.realestate.util;

import com.realestate.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * File manager for handling specific file operations for users and properties
 */
public class FileManager {

    /**
     * Save admin users to file
     * @param admins the list of admin users
     * @return true if successful
     */
    public static boolean saveAdmins(List<Admin> admins) {
        return FileUtil.writeObjectsToFile(admins, FileUtil.getFilePath(Constants.ADMIN_FILE));
    }

    /**
     * Load admin users from file
     * @return list of admin users
     */
    public static List<Admin> loadAdmins() {
        return FileUtil.readObjectsFromFile(FileUtil.getFilePath(Constants.ADMIN_FILE), Admin[].class);
    }

    /**
     * Save seller users to file
     * @param sellers the list of seller users
     * @return true if successful
     */
    public static boolean saveSellers(List<Seller> sellers) {
        return FileUtil.writeObjectsToFile(sellers, FileUtil.getFilePath(Constants.SELLER_FILE));
    }

    /**
     * Load seller users from file
     * @return list of seller users
     */
    public static List<Seller> loadSellers() {
        return FileUtil.readObjectsFromFile(FileUtil.getFilePath(Constants.SELLER_FILE), Seller[].class);
    }

    /**
     * Save buyer users to file
     * @param buyers the list of buyer users
     * @return true if successful
     */
    public static boolean saveBuyers(List<Buyer> buyers) {
        return FileUtil.writeObjectsToFile(buyers, FileUtil.getFilePath(Constants.BUYER_FILE));
    }

    /**
     * Load buyer users from file
     * @return list of buyer users
     */
    public static List<Buyer> loadBuyers() {
        return FileUtil.readObjectsFromFile(FileUtil.getFilePath(Constants.BUYER_FILE), Buyer[].class);
    }

    /**
     * Save properties to file
     * @param properties the list of properties
     * @return true if successful
     */
    public static boolean saveProperties(List<Property> properties) {
        return FileUtil.writeObjectsToFile(properties, FileUtil.getFilePath(Constants.PROPERTY_FILE));
    }

    /**
     * Load properties from file
     * @return list of properties
     */
    public static List<Property> loadProperties() {
        return FileUtil.readObjectsFromFile(FileUtil.getFilePath(Constants.PROPERTY_FILE), Property[].class);
    }

    /**
     * Find user by email and password for login
     * @param email user email
     * @param password user password
     * @return User object if found, null otherwise
     */
    public static User findUserByEmailAndPassword(String email, String password) {
        // Try to find in admins
        Optional<Admin> admin = loadAdmins().stream()
                .filter(a -> a.getEmail().equals(email) && a.getPassword().equals(password))
                .findFirst();
        if (admin.isPresent()) {
            return admin.get();
        }

        // Try to find in sellers
        Optional<Seller> seller = loadSellers().stream()
                .filter(s -> s.getEmail().equals(email) && s.getPassword().equals(password))
                .findFirst();
        if (seller.isPresent()) {
            return seller.get();
        }

        // Try to find in buyers
        Optional<Buyer> buyer = loadBuyers().stream()
                .filter(b -> b.getEmail().equals(email) && b.getPassword().equals(password))
                .findFirst();
        if (buyer.isPresent()) {
            return buyer.get();
        }

        return null; // User not found
    }
    
    /**
     * Check if email already exists
     * @param email the email to check
     * @return true if email exists
     */
    public static boolean emailExists(String email) {
        // Check in admins
        for (Admin admin : loadAdmins()) {
            if (admin.getEmail().equals(email)) {
                return true;
            }
        }
        
        // Check in sellers
        for (Seller seller : loadSellers()) {
            if (seller.getEmail().equals(email)) {
                return true;
            }
        }
        
        // Check in buyers
        for (Buyer buyer : loadBuyers()) {
            if (buyer.getEmail().equals(email)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Generate a unique ID for a new user
     * @param role the role of the user (Constants.ROLE_ADMIN, Constants.ROLE_SELLER, or Constants.ROLE_BUYER)
     * @return a unique ID
     */
    public static String generateUserId(String role) {
        String prefix = role.substring(0, 1); // A, S, or B
        int maxId = 0;
        
        if (role.equals(Constants.ROLE_ADMIN)) {
            List<Admin> admins = loadAdmins();
            for (Admin admin : admins) {
                String id = admin.getId();
                if (id.startsWith(prefix) && id.length() > 1) {
                    try {
                        int idNum = Integer.parseInt(id.substring(1));
                        if (idNum > maxId) {
                            maxId = idNum;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore invalid IDs
                    }
                }
            }
        } else if (role.equals(Constants.ROLE_SELLER)) {
            List<Seller> sellers = loadSellers();
            for (Seller seller : sellers) {
                String id = seller.getId();
                if (id.startsWith(prefix) && id.length() > 1) {
                    try {
                        int idNum = Integer.parseInt(id.substring(1));
                        if (idNum > maxId) {
                            maxId = idNum;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore invalid IDs
                    }
                }
            }
        } else if (role.equals(Constants.ROLE_BUYER)) {
            List<Buyer> buyers = loadBuyers();
            for (Buyer buyer : buyers) {
                String id = buyer.getId();
                if (id.startsWith(prefix) && id.length() > 1) {
                    try {
                        int idNum = Integer.parseInt(id.substring(1));
                        if (idNum > maxId) {
                            maxId = idNum;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore invalid IDs
                    }
                }
            }
        }
        
        return prefix + (maxId + 1);
    }

    /**
     * Generate a unique ID for a new property
     * @return a unique ID
     */
    public static String generatePropertyId() {
        List<Property> properties = loadProperties();
        int maxId = 0;
        
        for (Property property : properties) {
            String id = property.getId();
            if (id.startsWith("P") && id.length() > 1) {
                try {
                    int idNum = Integer.parseInt(id.substring(1));
                    if (idNum > maxId) {
                        maxId = idNum;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }
        
        return "P" + (maxId + 1);
    }
} 