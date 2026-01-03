package com.bookstore.Servlets;

import com.bookstore.dao.UserDAO;
import com.bookstore.models.User;
import com.bookstore.services.UserService;
import com.bookstore.utils.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/debug")
public class DebugServlet extends HttpServlet {
    private UserDAO userDAO;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        userService = new UserService();
        // Ensure admin user exists
        DatabaseConfig.ensureAdminUser();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<html><head><title>Debug Information</title></head><body>");
        out.println("<h1>Database Debug Information</h1>");
        
        try {
            // Test database connection
            out.println("<h2>Database Connection Test</h2>");
            out.println("<p>Connection successful!</p>");
            
            // Test admin user lookup
            out.println("<h2>Admin User Lookup</h2>");
            User adminUser = userDAO.findByUsername("admin");
            if (adminUser != null) {
                out.println("<p>Admin user found:</p>");
                out.println("<ul>");
                out.println("<li>Username: " + adminUser.getUsername() + "</li>");
                out.println("<li>Email: " + adminUser.getEmail() + "</li>");
                out.println("<li>User Type: " + adminUser.getUserType() + "</li>");
                out.println("<li>Password Hash: " + adminUser.getPassword() + "</li>");
                out.println("<li>Is Active: " + adminUser.isActive() + "</li>");
                out.println("</ul>");
                
                // Test password verification
                out.println("<h2>Password Verification Test</h2>");
                String testPassword = "admin123";
                String hashedPassword = UserService.hashPassword(testPassword);
                out.println("<p>Test password: " + testPassword + "</p>");
                out.println("<p>Hashed password: " + hashedPassword + "</p>");
                out.println("<p>Stored password: " + adminUser.getPassword() + "</p>");
                out.println("<p>Passwords match: " + hashedPassword.equals(adminUser.getPassword()) + "</p>");
                
                // Test authentication
                out.println("<h2>Authentication Test</h2>");
                User authUser = userService.authenticate("admin", "admin123");
                if (authUser != null) {
                    out.println("<p style='color: green;'>Authentication successful!</p>");
                } else {
                    out.println("<p style='color: red;'>Authentication failed!</p>");
                }
                
            } else {
                out.println("<p style='color: red;'>Admin user not found!</p>");
            }
            
        } catch (SQLException e) {
            out.println("<p style='color: red;'>Database error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
        
        out.println("</body></html>");
    }
}
