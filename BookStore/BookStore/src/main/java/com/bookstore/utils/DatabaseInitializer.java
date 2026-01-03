package com.bookstore.utils;

import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    private static final String[] TABLE_CREATION_QUERIES = {
        // Shopping Cart table
        "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Shopping_Cart') " +
        "CREATE TABLE Shopping_Cart (" +
            "CartItemId VARCHAR(10) PRIMARY KEY, " +
            "UserId VARCHAR(10) NOT NULL, " +
            "BookId VARCHAR(10) NOT NULL, " +
            "Quantity INT NOT NULL DEFAULT 1, " +
            "AddedAt DATETIME DEFAULT GETDATE(), " +
            "FOREIGN KEY (UserId) REFERENCES [User](UserId), " +
            "FOREIGN KEY (BookId) REFERENCES Books(BookId), " +
            "CONSTRAINT UC_UserBook UNIQUE (UserId, BookId)" +
        ")",

        // Wishlist table
        "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Wishlist') " +
        "CREATE TABLE Wishlist (" +
            "WishlistItemId VARCHAR(10) PRIMARY KEY, " +
            "UserId VARCHAR(10) NOT NULL, " +
            "BookId VARCHAR(10) NOT NULL, " +
            "AddedAt DATETIME DEFAULT GETDATE(), " +
            "FOREIGN KEY (UserId) REFERENCES [User](UserId), " +
            "FOREIGN KEY (BookId) REFERENCES Books(BookId), " +
            "CONSTRAINT UC_UserBookWishlist UNIQUE (UserId, BookId)" +
        ")",

        // Book Reviews table
        "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Book_Reviews') " +
        "CREATE TABLE Book_Reviews (" +
            "ReviewId VARCHAR(10) PRIMARY KEY, " +
            "BookId VARCHAR(10) NOT NULL, " +
            "UserId VARCHAR(10) NOT NULL, " +
            "Rating INT NOT NULL CHECK (Rating >= 1 AND Rating <= 5), " +
            "ReviewText NVARCHAR(MAX), " +
            "IsVerifiedPurchase BIT DEFAULT 0, " +
            "CreatedAt DATETIME DEFAULT GETDATE(), " +
            "UpdatedAt DATETIME DEFAULT GETDATE(), " +
            "FOREIGN KEY (BookId) REFERENCES Books(BookId), " +
            "FOREIGN KEY (UserId) REFERENCES [User](UserId), " +
            "CONSTRAINT UC_UserBookReview UNIQUE (UserId, BookId)" +
        ")",

        // Add indexes for better performance
        "IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_ShoppingCart_UserId') " +
        "CREATE INDEX IX_ShoppingCart_UserId ON Shopping_Cart(UserId)",

        "IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_Wishlist_UserId') " +
        "CREATE INDEX IX_Wishlist_UserId ON Wishlist(UserId)",

        "IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_BookReviews_BookId') " +
        "CREATE INDEX IX_BookReviews_BookId ON Book_Reviews(BookId)",

        "IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_BookReviews_UserId') " +
        "CREATE INDEX IX_BookReviews_UserId ON Book_Reviews(UserId)"
    };

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String query : TABLE_CREATION_QUERIES) {
                stmt.execute(query);
            }

            System.out.println("Database tables initialized successfully");

        } catch (SQLException e) {
            System.err.println("Error initializing database tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup code if needed
    }
}