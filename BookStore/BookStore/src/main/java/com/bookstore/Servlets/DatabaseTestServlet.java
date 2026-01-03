package com.bookstore.Servlets;

import com.bookstore.utils.DatabaseConfig;
import com.bookstore.dao.DiscountCouponDAO;
import com.bookstore.dao.PromotionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/test-database")
public class DatabaseTestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<html><head><title>Database Test</title></head><body>");
        out.println("<h1>Database Connection and Table Test</h1>");
        
        try {
            // Test database connection
            out.println("<h2>1. Testing Database Connection...</h2>");
            Connection conn = DatabaseConfig.getConnection();
            out.println("<p style='color: green;'>✓ Database connection successful!</p>");
            
            // Test table creation
            out.println("<h2>2. Testing Table Creation...</h2>");
            DatabaseConfig.createTables();
            out.println("<p style='color: green;'>✓ Tables created/verified successfully!</p>");
            
            // Test if DiscountCoupons table exists
            out.println("<h2>3. Testing DiscountCoupons Table...</h2>");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM DiscountCoupons")) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    out.println("<p style='color: green;'>✓ DiscountCoupons table exists with " + count + " records</p>");
                }
            }
            
            // Test if Promotions table exists
            out.println("<h2>4. Testing Promotions Table...</h2>");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Promotions")) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    out.println("<p style='color: green;'>✓ Promotions table exists with " + count + " records</p>");
                }
            }
            
            // Test DAO initialization
            out.println("<h2>5. Testing DAO Initialization...</h2>");
            DiscountCouponDAO couponDAO = new DiscountCouponDAO();
            out.println("<p style='color: green;'>✓ DiscountCouponDAO initialized successfully!</p>");
            
            PromotionDAO promotionDAO = new PromotionDAO();
            out.println("<p style='color: green;'>✓ PromotionDAO initialized successfully!</p>");
            
            out.println("<h2 style='color: green;'>🎉 All tests passed! Database is ready.</h2>");
            
        } catch (Exception e) {
            out.println("<p style='color: red;'>❌ Error: " + e.getMessage() + "</p>");
            out.println("<pre>" + e.toString() + "</pre>");
        }
        
        out.println("</body></html>");
    }
}
