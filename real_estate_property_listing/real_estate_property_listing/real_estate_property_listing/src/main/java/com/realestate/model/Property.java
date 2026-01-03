package com.realestate.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a property listing in the system
 */
public class Property implements Serializable, Comparable<Property> {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String title;
    private String description;
    private double price;
    private String location;
    private int bedrooms;
    private int bathrooms;
    private double area; // in square feet
    private String propertyType; // apartment, house, condo, etc.
    private List<String> imageUrls;
    private String sellerId; // ID of the seller who listed this property
    
    public Property() {
        this.imageUrls = new ArrayList<>();
    }
    
    public Property(String id, String title, String description, double price, String location,
                   int bedrooms, int bathrooms, double area, String propertyType, String sellerId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.area = area;
        this.propertyType = propertyType;
        this.sellerId = sellerId;
        this.imageUrls = new ArrayList<>();
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public int getBedrooms() {
        return bedrooms;
    }
    
    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }
    
    public int getBathrooms() {
        return bathrooms;
    }
    
    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }
    
    public double getArea() {
        return area;
    }
    
    public void setArea(double area) {
        this.area = area;
    }
    
    public String getPropertyType() {
        return propertyType;
    }
    
    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }
    
    public List<String> getImageUrls() {
        return imageUrls;
    }
    
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
    
    public void addImageUrl(String imageUrl) {
        if (this.imageUrls == null) {
            this.imageUrls = new ArrayList<>();
        }
        this.imageUrls.add(imageUrl);
    }
    
    public String getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
    
    @Override
    public String toString() {
        return "Property{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", location='" + location + '\'' +
                ", bedrooms=" + bedrooms +
                ", bathrooms=" + bathrooms +
                ", area=" + area +
                ", propertyType='" + propertyType + '\'' +
                ", sellerId='" + sellerId + '\'' +
                '}';
    }
    
    // Implementation for Comparable interface to support sorting by price
    @Override
    public int compareTo(Property other) {
        return Double.compare(this.price, other.price);
    }
} 