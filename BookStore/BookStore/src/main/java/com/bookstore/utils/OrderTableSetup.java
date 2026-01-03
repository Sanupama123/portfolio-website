package com.bookstore.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderTableSetup {
    public static void createOrderTables() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            String createOrdersTable =
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Orders]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE Orders ( " +
                "    OrderId VARCHAR(10) PRIMARY KEY, " +
                "    UserId VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES [User](UserId), " +
                "    OrderNumber VARCHAR(64) NOT NULL UNIQUE, " +
                "    Status VARCHAR(20) NOT NULL DEFAULT 'PROCESSING', " +
                "    Subtotal DECIMAL(10,2) NOT NULL DEFAULT 0, " +
                "    Shipping DECIMAL(10,2) NOT NULL DEFAULT 0, " +
                "    Total DECIMAL(10,2) NOT NULL DEFAULT 0, " +
                "    ShippingAddress NVARCHAR(MAX), " +
                "    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(), " +
                "    UpdatedAt DATETIME NOT NULL DEFAULT GETDATE() " +
                ") " +
                "END";

            String createOrderItemsTable =
                "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Order_Items]') AND type in (N'U')) " +
                "BEGIN " +
                "CREATE TABLE Order_Items ( " +
                "    OrderItemId VARCHAR(10) PRIMARY KEY, " +
                "    OrderId VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES Orders(OrderId) ON DELETE CASCADE, " +
                "    BookId VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES Books(BookId), " +
                "    Title NVARCHAR(255) NOT NULL, " +
                "    Price DECIMAL(10,2) NOT NULL, " +
                "    Quantity INT NOT NULL, " +
                "    Subtotal DECIMAL(10,2) NOT NULL " +
                ") " +
                "END";

            String createIndexes =
                "IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_Orders_UserId') " +
                "CREATE INDEX IX_Orders_UserId ON Orders(UserId); " +
                "IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_OrderItems_OrderId') " +
                "CREATE INDEX IX_OrderItems_OrderId ON Order_Items(OrderId);";

            stmt.executeUpdate(createOrdersTable);
            stmt.executeUpdate(createOrderItemsTable);
            
            stmt.executeUpdate(createIndexes);

            System.out.println("Order tables created/verified");

        } catch (SQLException e) {
            System.err.println("Error creating order tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


