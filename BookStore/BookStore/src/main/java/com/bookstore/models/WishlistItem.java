package com.bookstore.models;

import java.time.LocalDateTime;

public class WishlistItem {
    private String wishlistItemId;
    private String userId;
    private String bookId;
    private LocalDateTime addedAt;
    
    // Book details (for display)
    private String title;
    private String author;
    private double price;
    private String coverImagePath;
    private boolean inStock;
    
    // Getters and Setters
    public String getWishlistItemId() { return wishlistItemId; }
    public void setWishlistItemId(String wishlistItemId) { this.wishlistItemId = wishlistItemId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getCoverImagePath() { return coverImagePath; }
    public void setCoverImagePath(String coverImagePath) { this.coverImagePath = coverImagePath; }
    
    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }
}