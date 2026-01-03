package com.realestate.model;

import java.io.Serializable;

/**
 * Base class for all user types in the system
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    
    public User() {
    }
    
    public User(String id, String firstName, String lastName, String email, String password, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public void setFullName(String fullName) {
        if (fullName != null && fullName.contains(" ")) {
            int spaceIndex = fullName.indexOf(' ');
            this.firstName = fullName.substring(0, spaceIndex);
            this.lastName = fullName.substring(spaceIndex + 1);
        } else {
            this.firstName = fullName;
            this.lastName = "";
        }
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    /**
     * Check if the user is an admin
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin() {
        // This is overridden in the Admin class
        return false;
    }
    
    /**
     * Verify if the provided password matches the user's password
     * @param password The password to verify
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String password) {
        return this.password != null && this.password.equals(password);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
} 