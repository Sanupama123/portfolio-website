package com.realestate.servlet;

import com.realestate.model.*;
import com.realestate.service.PropertyService;
import com.realestate.service.UserService;
import com.realestate.util.Constants;
import com.realestate.util.FileUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * Servlet for admin-specific operations
 */
@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {
    private UserService userService;
    private PropertyService propertyService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
        propertyService = new PropertyService();
        System.out.println("AdminServlet initialized successfully");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }
        
        // Log the path for debugging
        System.out.println("AdminServlet received request for path: " + pathInfo);
        System.out.println("Full request URL: " + request.getRequestURL().toString());
        
        // Check if user is admin
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (user == null || !user.isAdmin()) {
            System.out.println("Access denied: User is not admin");
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=admin_required");
            return;
        }
        
        System.out.println("Processing admin request: " + pathInfo);
        
        if (pathInfo.equals("/") || pathInfo.equals("")) {
            // Redirect to users page when accessing just /admin
            System.out.println("Root admin path - redirecting to users");
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        } else if (pathInfo.equals("/users")) {
            System.out.println("Showing users page");
            showUsers(request, response);
        } else if (pathInfo.equals("/properties")) {
            System.out.println("Showing properties page");
            showAllProperties(request, response);
        } else if (pathInfo.equals("/data")) {
            System.out.println("Showing data management page");
            showDataManagement(request, response);
        } else if (pathInfo.equals("/settings")) {
            System.out.println("Showing settings page");
            showSettings(request, response);
        } else if (pathInfo.equals("/add-admin")) {
            System.out.println("Showing add admin form");
            showAddAdminForm(request, response);
        } else if (pathInfo.equals("/export-data")) {
            System.out.println("Handling data export");
            exportData(request, response);
        } else if (pathInfo.equals("/view-file")) {
            System.out.println("Handling file view");
            viewFile(request, response);
        } else {
            System.out.println("No handler found for path: " + pathInfo);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page not found: " + pathInfo);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }
        
        // Check if user is admin
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (user == null || !user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=admin_required");
            return;
        }
        
        if (pathInfo.equals("/add-admin")) {
            addNewAdmin(request, response);
        } else if (pathInfo.equals("/delete-user")) {
            deleteUser(request, response);
        } else if (pathInfo.equals("/update-settings")) {
            updateSettings(request, response);
        } else if (pathInfo.equals("/backup-data")) {
            backupData(request, response);
        } else if (pathInfo.equals("/restore-data")) {
            restoreData(request, response);
        } else if (pathInfo.equals("/cleanup-data")) {
            cleanupData(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    /**
     * Show all users (admins, sellers, buyers)
     */
    private void showUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Admin> admins = userService.getAllAdmins();
        List<Seller> sellers = userService.getAllSellers();
        List<Buyer> buyers = userService.getAllBuyers();
        
        request.setAttribute("admins", admins);
        request.setAttribute("sellers", sellers);
        request.setAttribute("buyers", buyers);
        
        request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
    }
    
    /**
     * Show all properties in the system
     */
    private void showAllProperties(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Property> properties = propertyService.getAllProperties();
        request.setAttribute("properties", properties);
        request.setAttribute("isAdmin", true);
        request.getRequestDispatcher("/admin/properties.jsp").forward(request, response);
    }
    
    /**
     * Show data management page
     */
    private void showDataManagement(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/admin/data.jsp").forward(request, response);
    }
    
    /**
     * Show system settings page
     */
    private void showSettings(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/admin/settings.jsp").forward(request, response);
    }
    
    /**
     * Show form to add a new admin
     */
    private void showAddAdminForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/admin/add-admin.jsp").forward(request, response);
    }
    
    /**
     * Add a new admin user
     */
    private void addNewAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phoneNumber = request.getParameter("phoneNumber");
        
        // Validate input
        if (firstName == null || lastName == null || email == null || password == null || 
            firstName.trim().isEmpty() || lastName.trim().isEmpty() || 
            email.trim().isEmpty() || password.trim().isEmpty()) {
            
            request.setAttribute("error", "All fields are required");
            request.getRequestDispatcher("/admin/add-admin.jsp").forward(request, response);
            return;
        }
        
        // Check if email is already taken
        if (userService.isEmailTaken(email, "")) {
            request.setAttribute("error", "Email is already in use");
            request.getRequestDispatcher("/admin/add-admin.jsp").forward(request, response);
            return;
        }
        
        // Create and register the admin
        Admin newAdmin = new Admin(
            userService.generateAdminId(),
            firstName,
            lastName,
            email,
            password,
            phoneNumber != null ? phoneNumber : ""
        );
        
        boolean success = userService.addAdmin(newAdmin);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/users?success=admin_added");
        } else {
            request.setAttribute("error", "Failed to add admin");
            request.getRequestDispatcher("/admin/add-admin.jsp").forward(request, response);
        }
    }
    
    /**
     * Delete a user (admin, seller, or buyer)
     */
    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = request.getParameter("id");
        String userType = request.getParameter("type");
        
        if (userId == null || userType == null) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=invalid_request");
            return;
        }
        
        boolean success = false;
        
        switch (userType) {
            case "admin":
                success = userService.deleteAdmin(userId);
                break;
            case "seller":
                success = userService.deleteSeller(userId);
                break;
            case "buyer":
                success = userService.deleteBuyer(userId);
                break;
            default:
                // Invalid user type
                break;
        }
        
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/users?success=user_deleted");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=delete_failed");
        }
    }
    
    /**
     * Update system settings
     */
    private void updateSettings(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // In a real app, we would save these settings to a settings file or database
        // For now, we'll just simulate success
        boolean success = true;
        
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/settings?success=settings_updated");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/settings?error=update_failed");
        }
    }
    
    /**
     * Backup system data
     */
    private void backupData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // In a real app, we would create a backup of all system data
        // For now, we'll just simulate success
        boolean success = true;
        
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/data?success=backup_created");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/data?error=backup_failed");
        }
    }
    
    /**
     * Restore system data from backup
     */
    private void restoreData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // In a real app, we would restore data from a backup
        // For now, we'll just simulate success
        boolean success = true;
        
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/data?success=data_restored");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/data?error=restore_failed");
        }
    }
    
    /**
     * Clean up temporary data
     */
    private void cleanupData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // In a real app, we would clean up temporary files or data
        // For now, we'll just simulate success
        boolean success = true;
        
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/data?success=cleanup_complete");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/data?error=cleanup_failed");
        }
    }
    
    /**
     * Export system data in requested format
     */
    private void exportData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String format = request.getParameter("format");
        if (format == null) {
            format = "json"; // Default format
        }
        
        response.setContentType(format.equalsIgnoreCase("json") ? "application/json" : "text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=export." + format.toLowerCase());
        
        // In a real app, we would export actual data
        // For now, we'll just output some sample data
        if (format.equalsIgnoreCase("json")) {
            response.getWriter().write("{\n" +
                "  \"users\": [\n" +
                "    { \"id\": \"1\", \"name\": \"Admin User\", \"email\": \"admin@realestate.com\" },\n" +
                "    { \"id\": \"2\", \"name\": \"Seller User\", \"email\": \"seller@example.com\" },\n" +
                "    { \"id\": \"3\", \"name\": \"Buyer User\", \"email\": \"buyer@example.com\" }\n" +
                "  ],\n" +
                "  \"properties\": [\n" +
                "    { \"id\": \"1\", \"title\": \"Luxury Villa\", \"price\": 500000 },\n" +
                "    { \"id\": \"2\", \"title\": \"City Apartment\", \"price\": 250000 },\n" +
                "    { \"id\": \"3\", \"title\": \"Country House\", \"price\": 350000 }\n" +
                "  ]\n" +
                "}");
        } else {
            response.getWriter().write("id,name,email\n" +
                "1,Admin User,admin@realestate.com\n" +
                "2,Seller User,seller@example.com\n" +
                "3,Buyer User,buyer@example.com\n\n" +
                "id,title,price\n" +
                "1,Luxury Villa,500000\n" +
                "2,City Apartment,250000\n" +
                "3,Country House,350000");
        }
    }
    
    /**
     * View a file from the data directory
     */
    private void viewFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fileName = request.getParameter("file");
        if (fileName == null || fileName.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File name is required");
            return;
        }
        
        // Get data directory
        String dataDir = FileUtil.getDataDirectory();
        File file = new File(dataDir + File.separator + fileName);
        
        if (!file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }
        
        // Determine content type based on file extension
        String contentType = getContentType(fileName);
        response.setContentType(contentType);
        
        // Stream the file content
        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
    
    /**
     * Determine content type based on file extension
     */
    private String getContentType(String fileName) {
        if (fileName.endsWith(".txt")) {
            return "text/plain";
        } else if (fileName.endsWith(".json")) {
            return "application/json";
        } else if (fileName.endsWith(".csv")) {
            return "text/csv";
        } else if (fileName.endsWith(".xml")) {
            return "application/xml";
        } else {
            return "application/octet-stream";
        }
    }
} 