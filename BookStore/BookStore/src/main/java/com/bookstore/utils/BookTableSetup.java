package com.bookstore.utils;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class BookTableSetup {
    public static void createBookTables() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create Categories table
            String createCategoriesTable = 
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Categories]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE Categories ( " +
                "    CategoryId VARCHAR(10) PRIMARY KEY, " +
                "    CategoryName VARCHAR(50) NOT NULL UNIQUE " +
                ") " +
                "END";

            // Create Books table
            String createBooksTable = 
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Books]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE Books ( " +
                "    BookId VARCHAR(10) PRIMARY KEY, " +
                "    Title VARCHAR(255) NOT NULL, " +
                "    Author VARCHAR(100) NOT NULL, " +
                "    ISBN VARCHAR(13) UNIQUE, " +
                "    Description TEXT, " +
                "    Price DECIMAL(10,2) NOT NULL, " +
                "    StockQuantity INT DEFAULT 0, " +
                "    CategoryId VARCHAR(10) FOREIGN KEY REFERENCES Categories(CategoryId), " +
                "    SellerId VARCHAR(10) FOREIGN KEY REFERENCES [User](UserId), " +
                "    CoverImagePath VARCHAR(255), " +
                "    PublishedYear INT, " +
                "    Publisher VARCHAR(100), " +
                "    Language VARCHAR(50), " +
                "    PageCount INT " +
                ") " +
                "END";

            // Create Book_Metadata table
            String createBookMetadataTable = 
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Book_Metadata]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE Book_Metadata ( " +
                "    MetadataId VARCHAR(10) PRIMARY KEY, " +
                "    BookId VARCHAR(10) NOT NULL UNIQUE, " +
                "    CreatedAt DATETIME DEFAULT GETDATE(), " +
                "    UpdatedAt DATETIME DEFAULT GETDATE(), " +
                "    IsActive BIT DEFAULT 1, " +
                "    FOREIGN KEY (BookId) REFERENCES Books(BookId) ON DELETE CASCADE " +
                ") " +
                "END";

            // Insert default categories
            String insertCategories = 
                "IF NOT EXISTS (SELECT * FROM Categories) " +
                "BEGIN " +
                "    INSERT INTO Categories (CategoryId, CategoryName) VALUES " +
                "    ('CAT001', 'Fiction'), " +
                "    ('CAT002', 'Non-Fiction'), " +
                "    ('CAT003', 'Science Fiction'), " +
                "    ('CAT004', 'Mystery'), " +
                "    ('CAT005', 'Romance'), " +
                "    ('CAT006', 'Biography'), " +
                "    ('CAT007', 'History'), " +
                "    ('CAT008', 'Technology'), " +
                "    ('CAT009', 'Business'), " +
                "    ('CAT010', 'Self-Help') " +
                "END";

            // Execute the create table statements
            stmt.executeUpdate(createCategoriesTable);
            System.out.println("Categories table created/verified");
            
            stmt.executeUpdate(createBooksTable);
            System.out.println("Books table created/verified");
            
            stmt.executeUpdate(createBookMetadataTable);
            System.out.println("Book_Metadata table created/verified");
            
            stmt.executeUpdate(insertCategories);
            System.out.println("Default categories inserted");

        } catch (SQLException e) {
            System.err.println("Error creating book tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}