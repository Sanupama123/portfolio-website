package com.bookstore.models;

import java.math.BigDecimal;

public class Seller {
    private String sellerId;
    private String businessName;
    private BigDecimal commissionRate;
    private BigDecimal totalSales;
    private User user; // Reference to parent User

    public Seller() {}

    public Seller(String sellerId, String businessName, BigDecimal commissionRate, BigDecimal totalSales) {
        this.sellerId = sellerId;
        this.businessName = businessName;
        this.commissionRate = commissionRate;
        this.totalSales = totalSales;
    }

    // Getters and Setters
    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
