package com.realestate.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Buyer user in the system
 */
public class Buyer extends User {
    private static final long serialVersionUID = 1L;
    
    private List<String> favoritePropertyIds; // IDs of properties marked as favorite
    
    public Buyer() {
        super();
        this.favoritePropertyIds = new ArrayList<>();
    }
    
    public Buyer(String id, String firstName, String lastName, String email, String password, String phoneNumber) {
        super(id, firstName, lastName, email, password, phoneNumber);
        this.favoritePropertyIds = new ArrayList<>();
    }
    
    public List<String> getFavoritePropertyIds() {
        return favoritePropertyIds;
    }
    
    public void setFavoritePropertyIds(List<String> favoritePropertyIds) {
        this.favoritePropertyIds = favoritePropertyIds;
    }
    
    public void addFavoritePropertyId(String propertyId) {
        if (this.favoritePropertyIds == null) {
            this.favoritePropertyIds = new ArrayList<>();
        }
        if (!this.favoritePropertyIds.contains(propertyId)) {
            this.favoritePropertyIds.add(propertyId);
        }
    }
    
    public void removeFavoritePropertyId(String propertyId) {
        if (this.favoritePropertyIds != null) {
            this.favoritePropertyIds.remove(propertyId);
        }
    }
    
    @Override
    public String toString() {
        return "Buyer{" +
                super.toString() +
                ", favoritePropertyIds=" + favoritePropertyIds +
                '}';
    }
} 