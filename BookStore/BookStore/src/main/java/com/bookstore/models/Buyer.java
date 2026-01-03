package com.bookstore.models;

public class Buyer {
    private String buyerId;
    private String preferredPaymentMethod;
    private User user; // Reference to parent User

    public Buyer() {}

    public Buyer(String buyerId, String preferredPaymentMethod) {
        this.buyerId = buyerId;
        this.preferredPaymentMethod = preferredPaymentMethod;
    }

    // Getters and Setters
    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getPreferredPaymentMethod() {
        return preferredPaymentMethod;
    }

    public void setPreferredPaymentMethod(String preferredPaymentMethod) {
        this.preferredPaymentMethod = preferredPaymentMethod;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
