package com.realestate.service;

import com.realestate.model.Admin;
import com.realestate.model.Buyer;
import com.realestate.model.Seller;
import com.realestate.model.User;
import com.realestate.util.FileUtil;
import com.realestate.util.Constants;
import com.realestate.util.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for user management operations
 */
public class UserService {
    private List<Admin> admins;
    private List<Seller> sellers;
    private List<Buyer> buyers;
    
    public UserService() {
        loadUsers();
    }
    
    /**
     * Load users from files
     */
    private void loadUsers() {
        admins = FileManager.loadAdmins();
        sellers = FileManager.loadSellers();
        buyers = FileManager.loadBuyers();
        
        // Initialize with default admin if none exists
        if (admins.isEmpty()) {
            Admin defaultAdmin = new Admin(
                FileManager.generateUserId(Constants.ROLE_ADMIN),
                "Admin",
                "User",
                "admin@realestate.com",
                "admin123",
                "1234567890"
            );
            admins.add(defaultAdmin);
            saveAdmins();
        }
    }
    
    /**
     * Save admins to file
     */
    private boolean saveAdmins() {
        return FileManager.saveAdmins(admins);
    }
    
    /**
     * Save sellers to file
     */
    private boolean saveSellers() {
        return FileManager.saveSellers(sellers);
    }
    
    /**
     * Save buyers to file
     */
    private boolean saveBuyers() {
        return FileManager.saveBuyers(buyers);
    }
    
    /**
     * Authenticate a user
     * @param email User's email
     * @param password User's password
     * @return The authenticated user or null if authentication fails
     */
    public User authenticate(String email, String password) {
        return FileManager.findUserByEmailAndPassword(email, password);
    }
    
    /**
     * Register a new seller
     * @param seller The seller to register
     * @return The registered seller with generated ID
     */
    public Seller registerSeller(Seller seller) {
        // Check if email already exists
        if (FileManager.emailExists(seller.getEmail())) {
            return null;
        }
        
        // Generate a unique ID
        seller.setId(FileManager.generateUserId(Constants.ROLE_SELLER));
        
        sellers.add(seller);
        saveSellers();
        return seller;
    }
    
    /**
     * Register a new buyer
     * @param buyer The buyer to register
     * @return The registered buyer with generated ID
     */
    public Buyer registerBuyer(Buyer buyer) {
        // Check if email already exists
        if (FileManager.emailExists(buyer.getEmail())) {
            return null;
        }
        
        // Generate a unique ID
        buyer.setId(FileManager.generateUserId(Constants.ROLE_BUYER));
        
        buyers.add(buyer);
        saveBuyers();
        return buyer;
    }
    
    /**
     * Get all admins
     * @return List of all admins
     */
    public List<Admin> getAllAdmins() {
        return new ArrayList<>(admins);
    }
    
    /**
     * Get all sellers
     * @return List of all sellers
     */
    public List<Seller> getAllSellers() {
        return new ArrayList<>(sellers);
    }
    
    /**
     * Get all buyers
     * @return List of all buyers
     */
    public List<Buyer> getAllBuyers() {
        return new ArrayList<>(buyers);
    }
    
    /**
     * Get a seller by ID
     * @param sellerId The ID of the seller
     * @return The seller, or null if not found
     */
    public Seller getSellerById(String sellerId) {
        for (Seller seller : sellers) {
            if (seller.getId().equals(sellerId)) {
                return seller;
            }
        }
        return null;
    }
    
    /**
     * Get a buyer by ID
     * @param buyerId The ID of the buyer
     * @return The buyer, or null if not found
     */
    public Buyer getBuyerById(String buyerId) {
        for (Buyer buyer : buyers) {
            if (buyer.getId().equals(buyerId)) {
                return buyer;
            }
        }
        return null;
    }
    
    /**
     * Update a seller
     * @param seller The seller with updated information
     * @return true if successful, false if seller not found
     */
    public boolean updateSeller(Seller seller) {
        for (int i = 0; i < sellers.size(); i++) {
            if (sellers.get(i).getId().equals(seller.getId())) {
                sellers.set(i, seller);
                return saveSellers();
            }
        }
        return false;
    }
    
    /**
     * Update a buyer
     * @param buyer The buyer with updated information
     * @return true if successful, false if buyer not found
     */
    public boolean updateBuyer(Buyer buyer) {
        for (int i = 0; i < buyers.size(); i++) {
            if (buyers.get(i).getId().equals(buyer.getId())) {
                buyers.set(i, buyer);
                return saveBuyers();
            }
        }
        return false;
    }
    
    /**
     * Add a property to a seller's list
     * @param sellerId The ID of the seller
     * @param propertyId The ID of the property
     * @return true if successful, false if seller not found
     */
    public boolean addPropertyToSeller(String sellerId, String propertyId) {
        Seller seller = getSellerById(sellerId);
        if (seller != null) {
            seller.addPropertyId(propertyId);
            return updateSeller(seller);
        }
        return false;
    }
    
    /**
     * Remove a property from a seller's list
     * @param sellerId The ID of the seller
     * @param propertyId The ID of the property
     * @return true if successful, false if seller not found
     */
    public boolean removePropertyFromSeller(String sellerId, String propertyId) {
        Seller seller = getSellerById(sellerId);
        if (seller != null) {
            seller.removePropertyId(propertyId);
            return updateSeller(seller);
        }
        return false;
    }
    
    /**
     * Add a property to a user's favorites
     * @param userId The ID of the user
     * @param propertyId The ID of the property
     * @return true if successful, false if user not found
     */
    public boolean addPropertyToFavorites(String userId, String propertyId) {
        // Check if user is a seller
        Seller seller = getSellerById(userId);
        if (seller != null) {
            seller.addFavoritePropertyId(propertyId);
            return updateSeller(seller);
        }
        
        // Check if user is a buyer
        Buyer buyer = getBuyerById(userId);
        if (buyer != null) {
            buyer.addFavoritePropertyId(propertyId);
            return updateBuyer(buyer);
        }
        
        // Check if user is an admin
        Admin admin = getAdminById(userId);
        if (admin != null) {
            admin.addFavoritePropertyId(propertyId);
            return updateAdmin(admin);
        }
        
        return false;
    }
    
    /**
     * Remove a property from a user's favorites
     * @param userId The ID of the user
     * @param propertyId The ID of the property
     * @return true if successful, false if user not found
     */
    public boolean removePropertyFromFavorites(String userId, String propertyId) {
        // Check if user is a seller
        Seller seller = getSellerById(userId);
        if (seller != null) {
            seller.removeFavoritePropertyId(propertyId);
            return updateSeller(seller);
        }
        
        // Check if user is a buyer
        Buyer buyer = getBuyerById(userId);
        if (buyer != null) {
            buyer.removeFavoritePropertyId(propertyId);
            return updateBuyer(buyer);
        }
        
        // Check if user is an admin
        Admin admin = getAdminById(userId);
        if (admin != null) {
            admin.removeFavoritePropertyId(propertyId);
            return updateAdmin(admin);
        }
        
        return false;
    }
    
    /**
     * Get a user's favorite property IDs
     * @param userId The ID of the user
     * @return List of favorite property IDs, or null if user not found
     */
    public List<String> getUserFavoritePropertyIds(String userId) {
        // Check if user is a seller
        Seller seller = getSellerById(userId);
        if (seller != null) {
            return seller.getFavoritePropertyIds();
        }
        
        // Check if user is a buyer
        Buyer buyer = getBuyerById(userId);
        if (buyer != null) {
            return buyer.getFavoritePropertyIds();
        }
        
        // Check if user is an admin
        Admin admin = getAdminById(userId);
        if (admin != null) {
            return admin.getFavoritePropertyIds();
        }
        
        return null;
    }
    
    /**
     * Check if an email is already taken by another user
     * @param email The email to check
     * @param currentUserId The ID of the current user (to exclude from the check)
     * @return true if the email is taken by another user, false otherwise
     */
    public boolean isEmailTaken(String email, String currentUserId) {
        // Check among sellers
        for (Seller seller : sellers) {
            if (!seller.getId().equals(currentUserId) && seller.getEmail().equals(email)) {
                return true;
            }
        }
        
        // Check among buyers
        for (Buyer buyer : buyers) {
            if (!buyer.getId().equals(currentUserId) && buyer.getEmail().equals(email)) {
                return true;
            }
        }
        
        // Check among admins
        for (Admin admin : admins) {
            if (!admin.getId().equals(currentUserId) && admin.getEmail().equals(email)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get an admin by ID
     * @param adminId The ID of the admin
     * @return The admin, or null if not found
     */
    public Admin getAdminById(String adminId) {
        for (Admin admin : admins) {
            if (admin.getId().equals(adminId)) {
                return admin;
            }
        }
        return null;
    }
    
    /**
     * Update an admin
     * @param admin The admin with updated information
     * @return true if successful, false if admin not found
     */
    public boolean updateAdmin(Admin admin) {
        for (int i = 0; i < admins.size(); i++) {
            if (admins.get(i).getId().equals(admin.getId())) {
                admins.set(i, admin);
                return saveAdmins();
            }
        }
        return false;
    }
    
    /**
     * Generate a new unique Admin ID
     * @return A new unique Admin ID
     */
    public String generateAdminId() {
        return FileManager.generateUserId(Constants.ROLE_ADMIN);
    }
    
    /**
     * Add a new admin
     * @param admin The admin to add
     * @return true if successful
     */
    public boolean addAdmin(Admin admin) {
        // Check if email already exists
        if (FileManager.emailExists(admin.getEmail())) {
            return false;
        }
        
        // Generate ID if not provided
        if (admin.getId() == null || admin.getId().isEmpty()) {
            admin.setId(generateAdminId());
        }
        
        admins.add(admin);
        return saveAdmins();
    }
    
    /**
     * Delete an admin
     * @param adminId The ID of the admin to delete
     * @return true if successful
     */
    public boolean deleteAdmin(String adminId) {
        // Can't delete the last admin
        if (admins.size() <= 1) {
            return false;
        }
        
        for (int i = 0; i < admins.size(); i++) {
            if (admins.get(i).getId().equals(adminId)) {
                admins.remove(i);
                return saveAdmins();
            }
        }
        
        return false;
    }
    
    /**
     * Delete a seller
     * @param sellerId The ID of the seller to delete
     * @return true if successful
     */
    public boolean deleteSeller(String sellerId) {
        for (int i = 0; i < sellers.size(); i++) {
            if (sellers.get(i).getId().equals(sellerId)) {
                sellers.remove(i);
                return saveSellers();
            }
        }
        
        return false;
    }
    
    /**
     * Delete a buyer
     * @param buyerId The ID of the buyer to delete
     * @return true if successful
     */
    public boolean deleteBuyer(String buyerId) {
        for (int i = 0; i < buyers.size(); i++) {
            if (buyers.get(i).getId().equals(buyerId)) {
                buyers.remove(i);
                return saveBuyers();
            }
        }
        
        return false;
    }
} 