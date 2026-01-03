package com.bookstore.utils;

import com.bookstore.services.UserService;
import com.bookstore.services.CategoryService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=BookStoreDB;encrypt=true;trustServerCertificate=true";
    private static final String USERNAME = "sa"; // Replace with your SQL Server username
    private static final String PASSWORD = "1578"; // Replace with your SQL Server password

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC Driver not found.", e);
        }
    }

    public static void createTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Creating database tables...");

            // Initialize book tables
            com.bookstore.utils.BookTableSetup.createBookTables();
            
            // Initialize buyer tables (cart, wishlist, reviews)
            com.bookstore.utils.BuyerTableSetup.createBuyerTables();

            // Initialize order tables (orders and order items)
            com.bookstore.utils.OrderTableSetup.createOrderTables();

            // Initialize payment tables (buyer details, saved cards, payments)
            com.bookstore.utils.PaymentTableSetup.createPaymentTables();

            // Create User table (superclass)
            String createUserTable = 
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[User]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE [User] ( " +
                "    UserId VARCHAR(10) PRIMARY KEY, " +
                "    Username VARCHAR(50) NOT NULL UNIQUE, " +
                "    Email VARCHAR(100) NOT NULL UNIQUE, " +
                "    Password VARCHAR(255) NOT NULL, " +
                "    FirstName VARCHAR(50), " +
                "    LastName VARCHAR(50), " +
                "    PhoneNumber VARCHAR(20), " +
                "    Address TEXT, " +
                "    CreatedAt DATETIME DEFAULT GETDATE(), " +
                "    LastLogin DATETIME, " +
                "    IsActive BIT DEFAULT 1, " +
                "    IsSuspended BIT DEFAULT 0, " +
                "    IsSystemAccount BIT DEFAULT 0 " +
                ") " +
                "END";

            // Create Buyer table (subclass)
            String createBuyerTable = 
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Buyer]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE Buyer ( " +
                "    BuyerId VARCHAR(10) PRIMARY KEY, " +
                "    PreferredPaymentMethod VARCHAR(50), " +
                "    FOREIGN KEY (BuyerId) REFERENCES [User](UserId) ON DELETE CASCADE " +
                ") " +
                "END";

            // Create Seller table (subclass)
            String createSellerTable = 
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Seller]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE Seller ( " +
                "    SellerId VARCHAR(10) PRIMARY KEY, " +
                "    BusinessName VARCHAR(100), " +
                "    CommissionRate DECIMAL(5,2) DEFAULT 10.00, " +
                "    TotalSales DECIMAL(12,2) DEFAULT 0.00, " +
                "    FOREIGN KEY (SellerId) REFERENCES [User](UserId) ON DELETE CASCADE " +
                ") " +
                "END";

            // Create Admin table (subclass)
            String createAdminTable = 
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Admin]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE Admin ( " +
                "    AdminId VARCHAR(10) PRIMARY KEY, " +
                "    Role VARCHAR(50) DEFAULT 'ADMIN', " +
                "    LastSystemAccess DATETIME, " +
                "    FOREIGN KEY (AdminId) REFERENCES [User](UserId) ON DELETE CASCADE " +
                ") " +
                "END";

            // Insert default admin with hashed password
            String adminPassword = UserService.hashPassword("admin123");
            System.out.println("Admin password hash: " + adminPassword);
            
            String insertAdmin = 
                "IF NOT EXISTS (SELECT * FROM [User] WHERE Username = 'admin') " +
                "BEGIN " +
                "    INSERT INTO [User] (UserId, Username, Email, Password, FirstName, LastName, IsSystemAccount) " +
                "    VALUES ('USR001', 'admin', 'admin@booknest.com', '" + adminPassword + "', 'System', 'Administrator', 1); " +
                "    INSERT INTO Admin (AdminId, Role) VALUES ('USR001', 'ADMIN') " +
                "END";

            stmt.executeUpdate(createUserTable);
            System.out.println("User table created/verified");
            
            
            stmt.executeUpdate(createBuyerTable);
            System.out.println("Buyer table created/verified");
            
            stmt.executeUpdate(createSellerTable);
            System.out.println("Seller table created/verified");
            
            stmt.executeUpdate(createAdminTable);
            System.out.println("Admin table created/verified");
            
            stmt.executeUpdate(insertAdmin);
            System.out.println("Admin user created/verified");

            // Create Promotions table
            String createPromotionsTable = 
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Promotions]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE Promotions ( " +
                "    PromotionId VARCHAR(10) PRIMARY KEY, " +
                "    Name NVARCHAR(255) NOT NULL, " +
                "    Description NVARCHAR(MAX), " +
                "    Type NVARCHAR(50) NOT NULL CHECK (Type IN ('FLASH_SALE', 'COUPON')), " +
                "    DiscountType NVARCHAR(20) NOT NULL CHECK (DiscountType IN ('PERCENTAGE', 'FIXED_AMOUNT')), " +
                "    DiscountValue DECIMAL(10,2) NOT NULL, " +
                "    MinOrderAmount DECIMAL(10,2) DEFAULT 0, " +
                "    MaxDiscountAmount DECIMAL(10,2), " +
                "    UsageLimit INT, " +
                "    UsedCount INT DEFAULT 0, " +
                "    StartDate DATETIME NOT NULL, " +
                "    EndDate DATETIME NOT NULL, " +
                "    IsActive BIT DEFAULT 1, " +
                "    CreatedAt DATETIME DEFAULT GETDATE(), " +
                "    UpdatedAt DATETIME DEFAULT GETDATE() " +
                ") " +
                "END";

            // Create PromotionBooks table (many-to-many relationship)
            String createPromotionBooksTable = 
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[PromotionBooks]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE PromotionBooks ( " +
                "    PromotionId VARCHAR(10) NOT NULL, " +
                "    BookId VARCHAR(10) NOT NULL, " +
                "    PRIMARY KEY (PromotionId, BookId), " +
                "    FOREIGN KEY (PromotionId) REFERENCES Promotions(PromotionId) ON DELETE CASCADE, " +
                "    FOREIGN KEY (BookId) REFERENCES Books(BookId) ON DELETE CASCADE " +
                ") " +
                "END";

            // Create DiscountCoupons table
            String createDiscountCouponsTable = 
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[DiscountCoupons]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE DiscountCoupons ( " +
                "    CouponId VARCHAR(10) PRIMARY KEY, " +
                "    Code NVARCHAR(50) NOT NULL UNIQUE, " +
                "    Description NVARCHAR(MAX), " +
                "    DiscountType NVARCHAR(20) NOT NULL CHECK (DiscountType IN ('PERCENTAGE', 'FIXED_AMOUNT')), " +
                "    DiscountValue DECIMAL(10,2) NOT NULL, " +
                "    MinOrderAmount DECIMAL(10,2) DEFAULT 0, " +
                "    MaxDiscountAmount DECIMAL(10,2), " +
                "    UsageLimit INT, " +
                "    UsedCount INT DEFAULT 0, " +
                "    StartDate DATETIME NOT NULL, " +
                "    EndDate DATETIME NOT NULL, " +
                "    IsActive BIT DEFAULT 1, " +
                "    CreatedAt DATETIME DEFAULT GETDATE(), " +
                "    UpdatedAt DATETIME DEFAULT GETDATE() " +
                ") " +
                "END";

            // Create CouponUsage table to track usage by users
            String createCouponUsageTable = 
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[CouponUsage]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE CouponUsage ( " +
                "    UsageId VARCHAR(10) PRIMARY KEY, " +
                "    CouponId VARCHAR(10) NOT NULL, " +
                "    UserId VARCHAR(10) NOT NULL, " +
                "    OrderId VARCHAR(10), " +
                "    UsedAt DATETIME DEFAULT GETDATE(), " +
                "    FOREIGN KEY (CouponId) REFERENCES DiscountCoupons(CouponId) ON DELETE CASCADE, " +
                "    FOREIGN KEY (UserId) REFERENCES [User](UserId) ON DELETE CASCADE " +
                ") " +
                "END";

            stmt.executeUpdate(createPromotionsTable);
            System.out.println("Promotions table created/verified");
            
            stmt.executeUpdate(createPromotionBooksTable);
            System.out.println("PromotionBooks table created/verified");
            
            stmt.executeUpdate(createDiscountCouponsTable);
            System.out.println("DiscountCoupons table created/verified");
            
            stmt.executeUpdate(createCouponUsageTable);
            System.out.println("CouponUsage table created/verified");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Ensure default categories exist using CategoryService
        CategoryService.ensureDefaultCategories();
    }

    public static void ensureAdminUser() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check if admin user exists
            String checkAdmin = "SELECT COUNT(*) FROM [User] WHERE Username = 'admin'";
            try (ResultSet rs = stmt.executeQuery(checkAdmin)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("Admin user not found, creating...");
                    
                    // Create admin user
                    String adminPassword = UserService.hashPassword("admin123");
                    String createAdmin = String.format(
                        "INSERT INTO [User] (UserId, Username, Email, Password, FirstName, LastName, IsSystemAccount, IsActive) " +
                        "VALUES ('USR001', 'admin', 'admin@booknest.com', '%s', 'System', 'Administrator', 1, 1); " +
                        "INSERT INTO Admin (AdminId, Role) VALUES ('USR001', 'ADMIN')",
                        adminPassword
                    );
                    
                    stmt.executeUpdate(createAdmin);
                    System.out.println("Admin user created successfully");
                } else {
                    System.out.println("Admin user already exists");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error ensuring admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }

}