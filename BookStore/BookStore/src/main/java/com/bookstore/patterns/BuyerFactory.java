package com.bookstore.patterns;

import com.bookstore.dao.BuyerDAO;
import com.bookstore.models.Buyer;
import com.bookstore.models.User;

/**
 * Concrete Factory for creating Buyer users.
 * This is a Concrete Creator in the Factory Method pattern.
 */
public class BuyerFactory implements UserFactory {
    private BuyerDAO buyerDAO;
    
    public BuyerFactory() {
        this.buyerDAO = new BuyerDAO();
    }
    
    @Override
    public User createUser(User user) throws Exception {
        try {
            // Create buyer record in database
            buyerDAO.createBuyer(user, "CREDIT_CARD");
            
            // Load the buyer data and set it to the user
            Buyer buyer = buyerDAO.findByUserId(user.getUserId());
            if (buyer != null) {
                buyer.setUser(user);
                user.setBuyer(buyer);
            }
            
            System.out.println("Buyer user created successfully: " + user.getUsername());
            return user;
            
        } catch (Exception e) {
            System.err.println("Error creating buyer user: " + e.getMessage());
            throw new Exception("Failed to create buyer user", e);
        }
    }
    
    @Override
    public String getUserType() {
        return "BUYER";
    }
}



