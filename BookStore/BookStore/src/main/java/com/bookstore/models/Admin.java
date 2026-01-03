package com.bookstore.models;

import java.util.Date;

public class Admin {
    private String adminId;
    private String role;
    private Date lastSystemAccess;
    private User user; // Reference to parent User

    public Admin() {}

    public Admin(String adminId, String role, Date lastSystemAccess) {
        this.adminId = adminId;
        this.role = role;
        this.lastSystemAccess = lastSystemAccess;
    }

    // Getters and Setters
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getLastSystemAccess() {
        return lastSystemAccess;
    }

    public void setLastSystemAccess(Date lastSystemAccess) {
        this.lastSystemAccess = lastSystemAccess;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
