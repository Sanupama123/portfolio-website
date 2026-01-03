package com.bookstore.utils;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class BuyerTableSetup {
    public static void createBuyerTables() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create Shopping_Cart table
            String createShoppingCartTable = 
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Shopping_Cart]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE Shopping_Cart ( " +
                "    CartItemId VARCHAR(10) PRIMARY KEY, " +
                "    UserId VARCHAR(10) FOREIGN KEY REFERENCES [User](UserId), " +
                "    BookId VARCHAR(10) FOREIGN KEY REFERENCES Books(BookId), " +
                "    Quantity INT DEFAULT 1, " +
                "    AddedAt DATETIME DEFAULT GETDATE(), " +
                "    UNIQUE(UserId, BookId) " +
                ") " +
                "END";

            // Create Wishlist table
            String createWishlistTable = 
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Wishlist]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE Wishlist ( " +
                "    WishlistItemId VARCHAR(10) PRIMARY KEY, " +
                "    UserId VARCHAR(10) FOREIGN KEY REFERENCES [User](UserId), " +
                "    BookId VARCHAR(10) FOREIGN KEY REFERENCES Books(BookId), " +
                "    AddedAt DATETIME DEFAULT GETDATE(), " +
                "    UNIQUE(UserId, BookId) " +
                ") " +
                "END";

            // Create Book_Reviews table
            String createReviewsTable = 
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Book_Reviews]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE Book_Reviews ( " +
                "    ReviewId VARCHAR(10) PRIMARY KEY, " +
                "    BookId VARCHAR(10) FOREIGN KEY REFERENCES Books(BookId), " +
                "    UserId VARCHAR(10) FOREIGN KEY REFERENCES [User](UserId), " +
                "    Rating INT CHECK (Rating BETWEEN 1 AND 5), " +
                "    ReviewText NVARCHAR(MAX), " +
                "    CreatedAt DATETIME DEFAULT GETDATE(), " +
                "    UpdatedAt DATETIME DEFAULT GETDATE(), " +
                "    IsVerifiedPurchase BIT DEFAULT 0, " +
                "    UNIQUE(BookId, UserId) " +
                ") " +
                "END";

            // Execute the create table statements
            stmt.executeUpdate(createShoppingCartTable);
            System.out.println("Shopping_Cart table created/verified");
            
            stmt.executeUpdate(createWishlistTable);
            System.out.println("Wishlist table created/verified");
            
            stmt.executeUpdate(createReviewsTable);
            System.out.println("Book_Reviews table created/verified");

        } catch (SQLException e) {
            System.err.println("Error creating buyer tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}