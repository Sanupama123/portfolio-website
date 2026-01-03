package com.realestate.servlet;

import com.realestate.model.*;
import com.realestate.util.Constants;
import com.realestate.util.FileManager;
import com.realestate.util.FileUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Servlet to test and display the data storage path
 */
@WebServlet("/data-path")
public class DataPathServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("initialize".equals(action)) {
            initializeData();
            response.sendRedirect(request.getContextPath() + "/data-path?initialized=true");
        } else if ("view".equals(action)) {
            String fileName = request.getParameter("file");
            if (fileName != null && !fileName.trim().isEmpty()) {
                displayFileContent(fileName, response);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File name is required");
            }
        } else {
            displayDataPathTest(request, response);
        }
    }
    
    /**
     * Initialize sample data for the application
     */
    private void initializeData() {
        // Create admin users
        List<Admin> admins = new ArrayList<>();
        Admin admin = new Admin("A1", "Admin", "User", "admin@example.com", "password", "123-456-7890");
        admins.add(admin);
        FileManager.saveAdmins(admins);
        
        // Create seller users
        List<Seller> sellers = new ArrayList<>();
        Seller seller = new Seller("S1", "John", "Smith", "seller@example.com", "password", "123-456-7890");
        sellers.add(seller);
        FileManager.saveSellers(sellers);
        
        // Create buyer users
        List<Buyer> buyers = new ArrayList<>();
        Buyer buyer = new Buyer("B1", "Jane", "Doe", "buyer@example.com", "password", "123-456-7890");
        buyers.add(buyer);
        FileManager.saveBuyers(buyers);
        
        // Create properties
        List<Property> properties = new ArrayList<>();
        Property property1 = new Property();
        property1.setId("P1");
        property1.setTitle("Beautiful House in Suburbs");
        property1.setDescription("A stunning 3-bedroom house with a large garden.");
        property1.setPrice(350000.00);
        property1.setLocation("123 Main St, Anytown");
        property1.setPropertyType("House");
        property1.setSellerId("S1");
        property1.setBedrooms(3);
        property1.setBathrooms(2);
        property1.setArea(2000);
        property1.setImageUrls(Arrays.asList("house1.jpg", "house1-interior.jpg"));
        
        Property property2 = new Property();
        property2.setId("P2");
        property2.setTitle("Modern City Apartment");
        property2.setDescription("A sleek city-center apartment with amazing views.");
        property2.setPrice(250000.00);
        property2.setLocation("456 Urban Ave, Downtown");
        property2.setPropertyType("Apartment");
        property2.setSellerId("S1");
        property2.setBedrooms(2);
        property2.setBathrooms(1);
        property2.setArea(1200);
        property2.setImageUrls(Arrays.asList("apartment1.jpg", "apartment1-view.jpg"));
        
        properties.add(property1);
        properties.add(property2);
        FileManager.saveProperties(properties);
        
        // Update the seller's properties
        seller.addPropertyId("P1");
        seller.addPropertyId("P2");
        FileManager.saveSellers(sellers);
        
        // Add some favorites for the buyer
        buyer.addFavoritePropertyId("P1");
        FileManager.saveBuyers(buyers);
    }
    
    /**
     * Display the content of a specific file
     * @param fileName The name of the file to display
     * @param response The HTTP response
     */
    private void displayFileContent(String fileName, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String filePath = FileUtil.getFilePath(fileName);
        File file = new File(filePath);
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("<meta charset=\"UTF-8\">");
        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("<title>File Content - " + fileName + "</title>");
        out.println("<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap\">");
        out.println("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
        out.println("<link rel=\"stylesheet\" href=\"css/style.css\">");
        out.println("<style>");
        out.println(".content-container { max-width: 800px; margin: 30px auto; background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }");
        out.println(".file-info { margin-bottom: 20px; padding: 15px; background-color: var(--primary-light); border-radius: 4px; }");
        out.println(".file-content { padding: 15px; background-color: #f5f5f5; border-radius: 4px; overflow-x: auto; white-space: pre-wrap; font-family: monospace; }");
        out.println(".error-message { color: var(--error-color); }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        // Include header
        includeHeader(out, null);
        
        // Main content
        out.println("<main>");
        out.println("<section>");
        out.println("<div class=\"container\">");
        out.println("<h2 class=\"section-title\">File Content: " + fileName + "</h2>");
        
        out.println("<div class=\"content-container\">");
        
        out.println("<div class=\"file-info\">");
        out.println("<p><strong>File Path:</strong> " + filePath + "</p>");
        out.println("<p><strong>File Size:</strong> " + (file.exists() ? formatFileSize(file.length()) : "N/A") + "</p>");
        out.println("<p><strong>Last Modified:</strong> " + (file.exists() ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(file.lastModified())) : "N/A") + "</p>");
        out.println("</div>");
        
        if (file.exists() && file.isFile()) {
            try {
                // For text files with JSON content (previously binary serialized files)
                if (fileName.endsWith(".txt") || fileName.endsWith(".json")) {
                    List<?> objects;
                    if (fileName.contains("Admin")) {
                        objects = FileUtil.readObjectsFromFile(filePath, Admin[].class);
                    } else if (fileName.contains("Seller")) {
                        objects = FileUtil.readObjectsFromFile(filePath, Seller[].class);
                    } else if (fileName.contains("Buyer")) {
                        objects = FileUtil.readObjectsFromFile(filePath, Buyer[].class);
                    } else if (fileName.contains("Property")) {
                        objects = FileUtil.readObjectsFromFile(filePath, Property[].class);
                    } else {
                        // Default to String for unknown types
                        objects = FileUtil.readObjectsFromFile(filePath, String[].class);
                    }
                    
                    out.println("<h3>File Content (Parsed JSON Objects):</h3>");
                    if (objects != null && !objects.isEmpty()) {
                        out.println("<div class=\"file-content\">");
                        out.println("Total objects: " + objects.size() + "<br><br>");
                        for (int i = 0; i < objects.size(); i++) {
                            Object obj = objects.get(i);
                            out.println("Object " + (i + 1) + ": " + obj + "<br>");
                            if (i < objects.size() - 1) {
                                out.println("<hr>");
                            }
                        }
                        out.println("</div>");
                    } else {
                        out.println("<p class=\"error-message\">No objects found in the file or unable to parse JSON.</p>");
                    }
                } else {
                    // For other text files, read and display the content directly
                    out.println("<h3>File Content:</h3>");
                    out.println("<div class=\"file-content\">");
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            out.println(escapeHtml(line) + "<br>");
                        }
                    }
                    out.println("</div>");
                }
            } catch (Exception e) {
                out.println("<p class=\"error-message\">Error reading file: " + e.getMessage() + "</p>");
                e.printStackTrace();
            }
        } else {
            out.println("<p class=\"error-message\">File does not exist or is not accessible.</p>");
        }
        
        out.println("<div style=\"margin-top: 20px;\">");
        out.println("<a href=\"admin-data.jsp\" class=\"btn btn-primary\">Back to Data Management</a>");
        out.println("</div>");
        
        out.println("</div>"); // End of content-container
        
        out.println("</div>");
        out.println("</section>");
        out.println("</main>");
        
        // Include footer
        includeFooter(out);
        
        out.println("<script src=\"js/main.js\"></script>");
        out.println("</body>");
        out.println("</html>");
    }
    
    /**
     * Display the data path test page
     * @param request The HTTP request
     * @param response The HTTP response
     */
    private void displayDataPathTest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String dataDir = FileUtil.getDataDirectory();
        boolean initialized = "true".equals(request.getParameter("initialized"));
        
        // Write a test file
        List<String> testData = new ArrayList<>(Arrays.asList("Test data 1", "Test data 2", "Test data 3"));
        String testFilePath = FileUtil.getFilePath("test.txt");
        boolean writeSuccess = FileUtil.writeObjectsToFile(testData, testFilePath);
        
        // Read the test file
        List<String> readData = FileUtil.readObjectsFromFile(testFilePath, String[].class);
        
        // Check if data files exist
        File adminFile = new File(FileUtil.getFilePath(Constants.ADMIN_FILE));
        File sellerFile = new File(FileUtil.getFilePath(Constants.SELLER_FILE));
        File buyerFile = new File(FileUtil.getFilePath(Constants.BUYER_FILE));
        File propertyFile = new File(FileUtil.getFilePath(Constants.PROPERTY_FILE));
        
        boolean dataInitialized = adminFile.exists() && sellerFile.exists() && 
                               buyerFile.exists() && propertyFile.exists();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("<meta charset=\"UTF-8\">");
        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("<title>Data Storage Path Test</title>");
        out.println("<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap\">");
        out.println("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
        out.println("<link rel=\"stylesheet\" href=\"css/style.css\">");
        out.println("<style>");
        out.println(".info-container { max-width: 800px; margin: 30px auto; background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }");
        out.println(".info-item { margin-bottom: 15px; }");
        out.println(".info-label { font-weight: bold; color: var(--primary-dark); }");
        out.println(".info-value { padding: 10px; background-color: var(--primary-light); border-radius: 4px; word-break: break-all; }");
        out.println(".success { color: var(--success-color); }");
        out.println(".error { color: var(--error-color); }");
        out.println(".alert { padding: 15px; margin-bottom: 20px; border-radius: 4px; }");
        out.println(".alert-success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }");
        out.println(".data-status { display: flex; justify-content: space-between; margin-top: 20px; }");
        out.println(".data-file { flex: 1; margin: 0 5px; padding: 15px; border-radius: 4px; text-align: center; }");
        out.println(".data-file.exists { background-color: #d4edda; color: #155724; }");
        out.println(".data-file.missing { background-color: #f8d7da; color: #721c24; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        // Include header
        includeHeader(out, request);
        
        // Main content
        out.println("<main>");
        out.println("<section>");
        out.println("<div class=\"container\">");
        out.println("<h2 class=\"section-title\">Data Storage Path Test</h2>");
        
        out.println("<div class=\"info-container\">");
        
        // Show initialization success message if applicable
        if (initialized) {
            out.println("<div class=\"alert alert-success\">");
            out.println("<strong>Success!</strong> Sample data has been initialized successfully.");
            out.println("</div>");
        }
        
        out.println("<div class=\"info-item\">");
        out.println("<div class=\"info-label\">Project Directory:</div>");
        out.println("<div class=\"info-value\">" + System.getProperty("user.dir") + "</div>");
        out.println("</div>");
        
        out.println("<div class=\"info-item\">");
        out.println("<div class=\"info-label\">Data Directory:</div>");
        out.println("<div class=\"info-value\">" + dataDir + "</div>");
        out.println("</div>");
        
        out.println("<div class=\"info-item\">");
        out.println("<div class=\"info-label\">Test File Path:</div>");
        out.println("<div class=\"info-value\">" + testFilePath + "</div>");
        out.println("</div>");
        
        out.println("<div class=\"info-item\">");
        out.println("<div class=\"info-label\">Write Operation:</div>");
        if (writeSuccess) {
            out.println("<div class=\"info-value success\">Success! Test file was created successfully.</div>");
        } else {
            out.println("<div class=\"info-value error\">Failed to write test file.</div>");
        }
        out.println("</div>");
        
        out.println("<div class=\"info-item\">");
        out.println("<div class=\"info-label\">Read Operation:</div>");
        if (readData != null && !readData.isEmpty()) {
            out.println("<div class=\"info-value success\">Success! Read " + readData.size() + " items from the test file.</div>");
            out.println("<div class=\"info-value\">Data: " + readData + "</div>");
        } else {
            out.println("<div class=\"info-value error\">Failed to read test data or file is empty.</div>");
        }
        out.println("</div>");
        
        // Data files status
        out.println("<h3>Data Files Status</h3>");
        out.println("<div class=\"data-status\">");
        
        out.println("<div class=\"data-file " + (adminFile.exists() ? "exists" : "missing") + "\">");
        out.println("<i class=\"fa fa-" + (adminFile.exists() ? "check-circle" : "times-circle") + "\"></i>");
        out.println("<p>" + Constants.ADMIN_FILE + "</p>");
        out.println("</div>");
        
        out.println("<div class=\"data-file " + (sellerFile.exists() ? "exists" : "missing") + "\">");
        out.println("<i class=\"fa fa-" + (sellerFile.exists() ? "check-circle" : "times-circle") + "\"></i>");
        out.println("<p>" + Constants.SELLER_FILE + "</p>");
        out.println("</div>");
        
        out.println("<div class=\"data-file " + (buyerFile.exists() ? "exists" : "missing") + "\">");
        out.println("<i class=\"fa fa-" + (buyerFile.exists() ? "check-circle" : "times-circle") + "\"></i>");
        out.println("<p>" + Constants.BUYER_FILE + "</p>");
        out.println("</div>");
        
        out.println("<div class=\"data-file " + (propertyFile.exists() ? "exists" : "missing") + "\">");
        out.println("<i class=\"fa fa-" + (propertyFile.exists() ? "check-circle" : "times-circle") + "\"></i>");
        out.println("<p>" + Constants.PROPERTY_FILE + "</p>");
        out.println("</div>");
        
        out.println("</div>"); // End of data-status
        
        out.println("<div style=\"margin-top: 20px;\">");
        if (dataInitialized) {
            out.println("<p class=\"success\"><i class=\"fa fa-check-circle\"></i> Sample data is already initialized.</p>");
        } else {
            out.println("<a href=\"data-path?action=initialize\" class=\"btn btn-primary\">Initialize Sample Data</a>");
        }
        out.println("<a href=\"admin-data.jsp\" class=\"btn btn-outline\" style=\"margin-left: 10px;\">Go to Data Management</a>");
        out.println("</div>");
        
        out.println("</div>"); // End of info-container
        
        out.println("</div>");
        out.println("</section>");
        out.println("</main>");
        
        // Include footer
        includeFooter(out);
        
        out.println("<script src=\"js/main.js\"></script>");
        out.println("</body>");
        out.println("</html>");
    }
    
    /**
     * Include the header in the response
     * @param out The PrintWriter
     * @param request The HTTP request (can be null)
     */
    private void includeHeader(PrintWriter out, HttpServletRequest request) {
        out.println("<header>");
        out.println("<div class=\"container\">");
        out.println("<nav class=\"navbar\">");
        out.println("<a href=\"index.jsp\" class=\"logo\">Real Estate</a>");
        out.println("<ul class=\"nav-links\">");
        out.println("<li><a href=\"index.jsp\">Home</a></li>");
        out.println("<li><a href=\"properties.jsp\">Properties</a></li>");
        out.println("<li><a href=\"about.jsp\">About</a></li>");
        out.println("<li><a href=\"contact.jsp\">Contact</a></li>");
        out.println("</ul>");
        
        out.println("<div class=\"user-actions\">");
        if (request != null && request.getSession().getAttribute("user") == null) {
            out.println("<a href=\"login.jsp\" class=\"btn btn-outline\">Login</a>");
            out.println("<a href=\"register.jsp\" class=\"btn btn-primary\">Register</a>");
        } else {
            out.println("<a href=\"dashboard.jsp\" class=\"btn btn-outline\">Dashboard</a>");
            out.println("<a href=\"logout\" class=\"btn btn-primary\">Logout</a>");
        }
        out.println("</div>");
        
        out.println("<button class=\"menu-toggle\">");
        out.println("<i class=\"fa fa-bars\"></i>");
        out.println("</button>");
        out.println("</nav>");
        out.println("</div>");
        out.println("</header>");
    }
    
    /**
     * Include the footer in the response
     * @param out The PrintWriter
     */
    private void includeFooter(PrintWriter out) {
        out.println("<footer>");
        out.println("<div class=\"container\">");
        out.println("<div class=\"footer-content\">");
        out.println("<div class=\"footer-section\">");
        out.println("<h3>Real Estate</h3>");
        out.println("<p>Your trusted partner in finding the perfect property.</p>");
        out.println("</div>");
        
        out.println("<div class=\"footer-section\">");
        out.println("<h3>Quick Links</h3>");
        out.println("<ul>");
        out.println("<li><a href=\"index.jsp\">Home</a></li>");
        out.println("<li><a href=\"properties.jsp\">Properties</a></li>");
        out.println("<li><a href=\"about.jsp\">About Us</a></li>");
        out.println("<li><a href=\"contact.jsp\">Contact Us</a></li>");
        out.println("</ul>");
        out.println("</div>");
        
        out.println("<div class=\"footer-section\">");
        out.println("<h3>Contact Us</h3>");
        out.println("<ul>");
        out.println("<li><i class=\"fa fa-map-marker\"></i> 123 Main St, City</li>");
        out.println("<li><i class=\"fa fa-phone\"></i> (123) 456-7890</li>");
        out.println("<li><i class=\"fa fa-envelope\"></i> info@realestate.com</li>");
        out.println("</ul>");
        out.println("</div>");
        out.println("</div>");
        
        out.println("<div class=\"footer-bottom\">");
        out.println("<p>&copy; 2023 Real Estate Management System. All rights reserved.</p>");
        out.println("</div>");
        out.println("</div>");
        out.println("</footer>");
    }
    
    /**
     * Format file size in human-readable format
     * @param size The file size in bytes
     * @return Formatted file size
     */
    private String formatFileSize(long size) {
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int unitIndex = 0;
        double fileSize = size;
        
        while (fileSize > 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", fileSize, units[unitIndex]);
    }
    
    /**
     * Escape HTML special characters
     * @param input The input string
     * @return Escaped string
     */
    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
} 