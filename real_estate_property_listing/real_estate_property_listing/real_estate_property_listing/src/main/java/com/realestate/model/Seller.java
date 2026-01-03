package com.realestate.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Seller user in the system
 */
public class Seller extends User {
    private static final long serialVersionUID = 1L;
    
    private List<String> propertyIds; // IDs of properties listed by this seller
    private List<String> favoritePropertyIds; // IDs of properties marked as favorite
    private String companyName; // Company name for the seller
    
    public Seller() {
        super();
        this.propertyIds = new ArrayList<>();
        this.favoritePropertyIds = new ArrayList<>();
        this.companyName = "";
    }
    
    public Seller(String id, String firstName, String lastName, String email, String password, String phoneNumber) {
        super(id, firstName, lastName, email, password, phoneNumber);
        this.propertyIds = new ArrayList<>();
        this.favoritePropertyIds = new ArrayList<>();
        this.companyName = "";
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public List<String> getPropertyIds() {
        return propertyIds;
    }
    
    public void setPropertyIds(List<String> propertyIds) {
        this.propertyIds = propertyIds;
    }
    
    public void addPropertyId(String propertyId) {
        if (this.propertyIds == null) {
            this.propertyIds = new ArrayList<>();
        }
        this.propertyIds.add(propertyId);
    }
    
    public void removePropertyId(String propertyId) {
        if (this.propertyIds != null) {
            this.propertyIds.remove(propertyId);
        }
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
        return "Seller{" +
                super.toString() +
                ", propertyIds=" + propertyIds +
                ", favoritePropertyIds=" + favoritePropertyIds +
                ", companyName='" + companyName + '\'' +
                '}';
    }
} 