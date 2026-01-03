package com.bookstore.patterns;

import com.bookstore.dao.SellerDAO;
import com.bookstore.models.Seller;
import com.bookstore.models.User;

import java.math.BigDecimal;

/**
 * Concrete Factory for creating Seller users.
 * This is a Concrete Creator in the Factory Method pattern.
 */
public class SellerFactory implements UserFactory {
    private SellerDAO sellerDAO;
    
    public SellerFactory() {
        this.sellerDAO = new SellerDAO();
    }
    
    @Override
    public User createUser(User user) throws Exception {
        try {
            // Create seller record in database
            sellerDAO.createSeller(user, "", new BigDecimal("10.00"));
            
            // Load the seller data and set it to the user
            Seller seller = sellerDAO.findByUserId(user.getUserId());
            if (seller != null) {
                seller.setUser(user);
                user.setSeller(seller);
            }
            
            System.out.println("Seller user created successfully: " + user.getUsername());
            return user;
            
        } catch (Exception e) {
            System.err.println("Error creating seller user: " + e.getMessage());
            throw new Exception("Failed to create seller user", e);
        }
    }
    
    @Override
    public String getUserType() {
        return "SELLER";
    }
}
