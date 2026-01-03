package com.bookstore.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PaymentTableSetup {
    public static void createPaymentTables() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Create BuyerDetails table
            String createBuyerDetailsTable =
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[BuyerDetails]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE BuyerDetails ( " +
                "    DetailsId VARCHAR(10) PRIMARY KEY, " +
                "    UserId VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES [User](UserId), " +
                "    FullName NVARCHAR(100) NOT NULL, " +
                "    Email NVARCHAR(100) NOT NULL, " +
                "    PhoneNumber VARCHAR(20) NOT NULL, " +
                "    ShippingAddress NVARCHAR(MAX) NOT NULL, " +
                "    City NVARCHAR(100) NOT NULL, " +
                "    PostalCode VARCHAR(20) NOT NULL, " +
                "    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(), " +
                "    UpdatedAt DATETIME NOT NULL DEFAULT GETDATE() " +
                ") " +
                "END";

            // Create SavedCards table
            String createSavedCardsTable =
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[SavedCards]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE SavedCards ( " +
                "    CardId VARCHAR(10) PRIMARY KEY, " +
                "    UserId VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES [User](UserId), " +
                "    CardHolderName NVARCHAR(100) NOT NULL, " +
                "    CardNumber VARCHAR(4) NOT NULL, " + // Only last 4 digits
                "    ExpiryDate VARCHAR(7) NOT NULL, " + // Format: MM/YYYY
                "    CardType VARCHAR(20) NOT NULL, " + // VISA, MASTERCARD, etc.
                "    CreatedAt DATETIME NOT NULL DEFAULT GETDATE() " +
                ") " +
                "END";

            // Create Payments table
            String createPaymentsTable =
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Payments]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE Payments ( " +
                "    PaymentId VARCHAR(10) PRIMARY KEY, " +
                "    OrderId VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES Orders(OrderId) ON DELETE CASCADE, " +
                "    PaymentMethod VARCHAR(20) NOT NULL, " + // CARD, UPI, COD
                "    PaymentStatus VARCHAR(20) NOT NULL DEFAULT 'PENDING', " + // PENDING, COMPLETED, FAILED
                "    Amount DECIMAL(10,2) NOT NULL, " +
                "    TransactionId VARCHAR(100), " +
                "    CardId VARCHAR(10) FOREIGN KEY REFERENCES SavedCards(CardId), " +
                "    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(), " +
                "    UpdatedAt DATETIME NOT NULL DEFAULT GETDATE() " +
                ") " +
                "END";

            // Update Orders table to add new columns
            String alterOrdersTable = 
                "IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Orders]') AND name = 'Discount') " +
                "BEGIN " +
                "    ALTER TABLE Orders ADD Discount DECIMAL(10,2) NOT NULL DEFAULT 0; " +
                "    ALTER TABLE Orders ADD CouponCode VARCHAR(50); " +
                "    ALTER TABLE Orders ADD BuyerDetailsId VARCHAR(10) FOREIGN KEY REFERENCES BuyerDetails(DetailsId); " +
                "    ALTER TABLE Orders ADD PaymentId VARCHAR(10) FOREIGN KEY REFERENCES Payments(PaymentId); " +
                "END";

            // Create indexes
            String createIndexes =
                "IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_BuyerDetails_UserId') " +
                "CREATE INDEX IX_BuyerDetails_UserId ON BuyerDetails(UserId); " +
                "IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_SavedCards_UserId') " +
                "CREATE INDEX IX_SavedCards_UserId ON SavedCards(UserId); " +
                "IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_Payments_OrderId') " +
                "CREATE INDEX IX_Payments_OrderId ON Payments(OrderId);";

            stmt.executeUpdate(createBuyerDetailsTable);
            System.out.println("BuyerDetails table created/verified");

            stmt.executeUpdate(createSavedCardsTable);
            System.out.println("SavedCards table created/verified");

            stmt.executeUpdate(createPaymentsTable);
            System.out.println("Payments table created/verified");

            try {
                stmt.executeUpdate(alterOrdersTable);
                System.out.println("Orders table updated with payment columns");
            } catch (SQLException e) {
                System.out.println("Orders table columns already exist or error: " + e.getMessage());
            }

            stmt.executeUpdate(createIndexes);
            System.out.println("Payment indexes created/verified");

        } catch (SQLException e) {
            System.err.println("Error creating payment tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

