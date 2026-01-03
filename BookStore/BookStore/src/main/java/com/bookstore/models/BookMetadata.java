package com.bookstore.models;

import java.time.LocalDateTime;

public class BookMetadata {
    private String metadataId;
    private String bookId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive = true;

    // Default constructor
    public BookMetadata() {}

    // Constructor with parameters
    public BookMetadata(String metadataId, String bookId, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isActive) {
        this.metadataId = metadataId;
        this.bookId = bookId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getMetadataId() {
        return metadataId;
    }

    public void setMetadataId(String metadataId) {
        this.metadataId = metadataId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "BookMetadata{" +
                "metadataId='" + metadataId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isActive=" + isActive +
                '}';
    }
}
