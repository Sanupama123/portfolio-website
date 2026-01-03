package com.realestate.servlet;

import com.realestate.model.*;
import com.realestate.service.PropertyService;
import com.realestate.service.UserService;
import com.realestate.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Servlet for property management operations
 */
@WebServlet("/properties/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10,  // 10 MB
        maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class PropertyServlet extends HttpServlet {
    private PropertyService propertyService;
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        propertyService = new PropertyService();
        userService = new UserService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            listProperties(request, response);
        } else if (pathInfo.equals("/manage")) {
            manageProperties(request, response);
        } else if (pathInfo.equals("/add")) {
            showAddForm(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            showEditForm(request, response);
        } else if (pathInfo.equals("/favorites")) {
            showFavorites(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            showPropertyDetails(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null) {
            path = "/";
        }
        
        if (path.equals("/add")) {
            // Create new property
            addProperty(request, response);
        } else if (path.startsWith("/edit/")) {
            // Update property
            updateProperty(request, response);
        } else if (path.startsWith("/delete/")) {
            // Delete property
            deleteProperty(request, response);
        } else if (path.startsWith("/favorite/")) {
            // Toggle favorite status
            toggleFavorite(request, response);
        } else if (path.equals("/search")) {
            // Handle advanced search
            searchProperties(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    /**
     * List properties with optional search and filtering
     */
    private void listProperties(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get all properties
        List<Property> properties = propertyService.getAllProperties();
        
        // Apply filters if present
        String searchQuery = request.getParameter("search");
        String propertyType = request.getParameter("propertyType");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        
        // Filter by search query (title or description)
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            searchQuery = searchQuery.toLowerCase();
            List<Property> filteredProperties = new ArrayList<>();
            for (Property property : properties) {
                if (property.getTitle().toLowerCase().contains(searchQuery) || 
                    property.getDescription().toLowerCase().contains(searchQuery) ||
                    property.getLocation().toLowerCase().contains(searchQuery)) {
                    filteredProperties.add(property);
                }
            }
            properties = filteredProperties;
        }
        
        // Filter by property type
        if (propertyType != null && !propertyType.trim().isEmpty() && !propertyType.equals("all")) {
            List<Property> filteredProperties = new ArrayList<>();
            for (Property property : properties) {
                if (property.getPropertyType().equalsIgnoreCase(propertyType)) {
                    filteredProperties.add(property);
                }
            }
            properties = filteredProperties;
        }
        
        // Filter by price range
        double minPrice = -1;
        double maxPrice = Double.MAX_VALUE;
        if (minPriceStr != null && !minPriceStr.trim().isEmpty()) {
            try {
                minPrice = Double.parseDouble(minPriceStr);
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        }
        if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
            try {
                maxPrice = Double.parseDouble(maxPriceStr);
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        }
        
        if (minPrice >= 0 || maxPrice < Double.MAX_VALUE) {
            List<Property> filteredProperties = new ArrayList<>();
            for (Property property : properties) {
                double price = property.getPrice();
                if (price >= minPrice && price <= maxPrice) {
                    filteredProperties.add(property);
                }
            }
            properties = filteredProperties;
        }
        
        // Sort properties
        if (sortBy != null && !sortBy.isEmpty()) {
            final boolean isAscending = sortOrder == null || sortOrder.equals("asc");
            
            if (sortBy.equals("price")) {
                // Use quick sort for price sorting
                quickSort(properties, 0, properties.size() - 1, isAscending);
            } else if (sortBy.equals("newest")) {
                // Sort by date (assuming newer properties have higher IDs)
                Collections.sort(properties, (p1, p2) -> {
                    int id1 = extractIdNumber(p1.getId());
                    int id2 = extractIdNumber(p2.getId());
                    return isAscending ? (id1 - id2) : (id2 - id1);
                });
            }
        }
        
        // Set attributes for JSP
        request.setAttribute("properties", properties);
        request.setAttribute("search", searchQuery);
        request.setAttribute("propertyType", propertyType);
        request.setAttribute("minPrice", minPriceStr);
        request.setAttribute("maxPrice", maxPriceStr);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);
        
        // Check if user has favorites to display favorite icons
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (user != null) {
            List<String> favoriteIds = userService.getUserFavoritePropertyIds(user.getId());
            request.setAttribute("favoriteIds", favoriteIds != null ? favoriteIds : new ArrayList<>());
        }
        
        // Forward to the properties list page
        request.getRequestDispatcher("/property-list.jsp").forward(request, response);
    }
    
    /**
     * Quick sort algorithm for sorting properties by price
     */
    private void quickSort(List<Property> properties, int low, int high, boolean ascending) {
        if (low < high) {
            int pivotIndex = partition(properties, low, high, ascending);
            quickSort(properties, low, pivotIndex - 1, ascending);
            quickSort(properties, pivotIndex + 1, high, ascending);
        }
    }
    
    private int partition(List<Property> properties, int low, int high, boolean ascending) {
        double pivot = properties.get(high).getPrice();
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            boolean condition = ascending ? 
                properties.get(j).getPrice() <= pivot : 
                properties.get(j).getPrice() >= pivot;
                
            if (condition) {
                i++;
                Collections.swap(properties, i, j);
            }
        }
        
        Collections.swap(properties, i + 1, high);
        return i + 1;
    }
    
    /**
     * Extract numeric part of property ID for sorting
     */
    private int extractIdNumber(String id) {
        if (id != null && id.startsWith("P")) {
            try {
                return Integer.parseInt(id.substring(1));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Show property management page (sellers see their properties, admins see all)
     */
    private void manageProperties(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }
        
        List<Property> properties;
        
        // Admin sees all properties, seller sees only their own
        if (user.isAdmin()) {
            properties = propertyService.getAllProperties();
        } else if (user instanceof Seller) {
            Seller seller = (Seller) user;
            properties = propertyService.getPropertiesBySeller(seller.getId());
        } else {
            response.sendRedirect(request.getContextPath() + "/properties?error=unauthorized");
            return;
        }
        
        request.setAttribute("properties", properties);
        request.getRequestDispatcher("/property-manage.jsp").forward(request, response);
    }
    
    /**
     * Show add property form
     */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (user == null || !(user instanceof Seller || user.isAdmin())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }
        
        request.getRequestDispatcher("/property-form.jsp").forward(request, response);
    }
    
    /**
     * Show edit property form
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }
        
        // Extract property ID from path
        String pathInfo = request.getPathInfo();
        String propertyId = pathInfo.substring("/edit/".length());
        
        // Load property
        Property property = propertyService.getPropertyById(propertyId);
        if (property == null) {
            response.sendRedirect(request.getContextPath() + "/properties/manage?error=property_not_found");
            return;
        }
        
        // Check authorization (admin can edit any, seller can edit only their own)
        if (!user.isAdmin() && (!(user instanceof Seller) || !property.getSellerId().equals(user.getId()))) {
            response.sendRedirect(request.getContextPath() + "/properties?error=unauthorized");
            return;
        }
        
        request.setAttribute("property", property);
        request.getRequestDispatcher("/property-form.jsp").forward(request, response);
    }
    
    /**
     * Show favorites/wishlist
     */
    private void showFavorites(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required&redirect=properties/favorites");
            return;
        }
        
        List<String> favoriteIds = userService.getUserFavoritePropertyIds(user.getId());
        List<Property> favoriteProperties = new ArrayList<>();
        
        if (favoriteIds != null && !favoriteIds.isEmpty()) {
            for (String propertyId : favoriteIds) {
                Property property = propertyService.getPropertyById(propertyId);
                if (property != null) {
                    favoriteProperties.add(property);
                }
            }
            
            // Sort favorites by price (descending) using Quick Sort algorithm
            if (!favoriteProperties.isEmpty()) {
                quickSort(favoriteProperties, 0, favoriteProperties.size() - 1, false);
            }
        }
        
        // Add properties to request and set isFavoritesPage flag to true
        request.setAttribute("properties", favoriteProperties);
        request.setAttribute("isFavoritesPage", true);
        request.getRequestDispatcher("/favorites.jsp").forward(request, response);
    }
    
    /**
     * Show property details
     */
    private void showPropertyDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String propertyId = pathInfo.substring("/view/".length());
        
        Property property = propertyService.getPropertyById(propertyId);
        if (property == null) {
            response.sendRedirect(request.getContextPath() + "/properties?error=property_not_found");
            return;
        }
        
        // Check if the property is in user's favorites
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        boolean isFavorite = false;
        
        if (user != null) {
            List<String> favoriteIds = userService.getUserFavoritePropertyIds(user.getId());
            if (favoriteIds != null && favoriteIds.contains(propertyId)) {
                isFavorite = true;
            }
        }
        
        request.setAttribute("property", property);
        request.setAttribute("isFavorite", isFavorite);
        
        // Get seller information
        Seller seller = userService.getSellerById(property.getSellerId());
        if (seller != null) {
            request.setAttribute("seller", seller);
        }
        
        request.getRequestDispatcher("/property-details.jsp").forward(request, response);
    }
    
    /**
     * Add a new property
     */
    private void addProperty(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (user == null || !(user instanceof Seller || user.isAdmin())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        try {
            // Create new property
            Property property = new Property();
            
            // Set basic info
            property.setTitle(request.getParameter("title"));
            property.setDescription(request.getParameter("description"));
            property.setPrice(Double.parseDouble(request.getParameter("price")));
            property.setLocation(request.getParameter("location"));
            property.setPropertyType(request.getParameter("propertyType"));
            
            // Set seller ID
            if (user instanceof Seller) {
                property.setSellerId(user.getId());
            } else if (user.isAdmin() && request.getParameter("sellerId") != null) {
                property.setSellerId(request.getParameter("sellerId"));
            } else {
                throw new IllegalArgumentException("Seller ID is required");
            }
            
            // Handle image uploads
            List<String> imageUrls = new ArrayList<>();
            
            // Get the upload directory
            String uploadPath = getServletContext().getRealPath("") + "/images/properties";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Process each uploaded file
            for (Part part : request.getParts()) {
                if (part.getName().equals("images") && part.getSize() > 0) {
                    String fileName = getSubmittedFileName(part);
                    
                    // Create a unique file name to prevent overwriting
                    String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                    String filePath = uploadPath + File.separator + uniqueFileName;
                    
                    // Save the file
                    part.write(filePath);
                    
                    // Add to image URLs
                    imageUrls.add("images/properties/" + uniqueFileName);
                }
            }
            
            property.setImageUrls(imageUrls);
            
            // Save the property
            Property savedProperty = propertyService.addProperty(property);
            
            // Add property to seller's list
            userService.addPropertyToSeller(savedProperty.getSellerId(), savedProperty.getId());
            
            // Redirect to manage page with success message
            response.sendRedirect(request.getContextPath() + "/properties/manage?success=property_added");
            
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/property-form.jsp").forward(request, response);
        }
    }
    
    /**
     * Update existing property
     */
    private void updateProperty(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("updateProperty method called");
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        try {
            // Extract property ID from path
            String pathInfo = request.getPathInfo();
            System.out.println("Path Info: " + pathInfo);
            
            if (pathInfo == null || !pathInfo.startsWith("/edit/") || pathInfo.length() <= "/edit/".length()) {
                throw new IllegalArgumentException("Invalid URL format for property edit");
            }
            
            String propertyId = pathInfo.substring("/edit/".length());
            System.out.println("Extracted Property ID from path: " + propertyId);
            
            // Debug: print form parameters
            System.out.println("Form ID parameter: " + request.getParameter("id"));
            
            // Get existing property
            Property property = propertyService.getPropertyById(propertyId);
            if (property == null) {
                System.out.println("Property not found with ID: " + propertyId);
                response.sendRedirect(request.getContextPath() + "/properties/manage?error=property_not_found");
                return;
            }
            
            // Check authorization
            if (!user.isAdmin() && (!(user instanceof Seller) || !property.getSellerId().equals(user.getId()))) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            // Update property details
            property.setTitle(request.getParameter("title"));
            property.setDescription(request.getParameter("description"));
            
            try {
                property.setPrice(Double.parseDouble(request.getParameter("price")));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid price format");
            }
            
            property.setLocation(request.getParameter("location"));
            property.setPropertyType(request.getParameter("propertyType"));
            
            try {
                // Get parameter values and trim them
                String bedroomsStr = request.getParameter("bedrooms");
                String bathroomsStr = request.getParameter("bathrooms");
                String areaStr = request.getParameter("area");
                
                // Debug values
                System.out.println("Raw bedrooms: " + bedroomsStr);
                System.out.println("Raw bathrooms: " + bathroomsStr);
                System.out.println("Raw area: " + areaStr);
                
                // Parse bedrooms (integer)
                if (bedroomsStr != null && !bedroomsStr.trim().isEmpty()) {
                    try {
                        property.setBedrooms(Integer.parseInt(bedroomsStr.trim()));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid bedrooms value: " + bedroomsStr);
                    }
                } else {
                    property.setBedrooms(0); // Default value
                }
                
                // Parse bathrooms (can be decimal)
                if (bathroomsStr != null && !bathroomsStr.trim().isEmpty()) {
                    try {
                        // Using parseDouble to support decimal values like 1.5 bathrooms
                        double bathrooms = Double.parseDouble(bathroomsStr.trim());
                        // Convert to int (1 for each 0.5 bathroom)
                        int bathroomsInt = (int)Math.round(bathrooms * 2);
                        property.setBathrooms(bathroomsInt);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid bathrooms value: " + bathroomsStr);
                    }
                } else {
                    property.setBathrooms(0); // Default value
                }
                
                // Parse area (integer)
                if (areaStr != null && !areaStr.trim().isEmpty()) {
                    try {
                        property.setArea(Integer.parseInt(areaStr.trim()));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid area value: " + areaStr);
                    }
                } else {
                    property.setArea(0); // Default value
                }
                
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format for bedrooms, bathrooms, or area: " + e.getMessage());
            }
            
            // Handle new image uploads
            List<String> existingImages = property.getImageUrls();
            if (existingImages == null) {
                existingImages = new ArrayList<>();
            } else {
                // Create a new copy to avoid ConcurrentModificationException
                existingImages = new ArrayList<>(existingImages);
            }
            
            // Debug image handling
            System.out.println("Initial images: " + existingImages);
            
            // Check if any images should be removed
            String[] removeImages = request.getParameterValues("removeImages");
            if (removeImages != null) {
                System.out.println("Images to remove: " + Arrays.toString(removeImages));
                
                for (String imageUrl : removeImages) {
                    // Debug image removal
                    System.out.println("Processing image for removal: " + imageUrl);
                    
                    // Remove from memory list first
                    boolean removed = existingImages.remove(imageUrl);
                    System.out.println("Image removed from list: " + removed);
                    
                    // Try to delete physical file
                    String filePath = getServletContext().getRealPath("") + "/" + imageUrl;
                    try {
                        boolean fileDeleted = Files.deleteIfExists(Paths.get(filePath));
                        System.out.println("Image file deleted: " + fileDeleted + " - " + filePath);
                    } catch (IOException e) {
                        System.err.println("Failed to delete image file: " + filePath);
                        e.printStackTrace();
                    }
                }
            }
            
            System.out.println("Images after removal: " + existingImages);
            
            // Process new uploads
            String uploadPath = getServletContext().getRealPath("") + "/images/properties";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            for (Part part : request.getParts()) {
                if (part.getName().equals("images") && part.getSize() > 0) {
                    String fileName = getSubmittedFileName(part);
                    
                    if (fileName == null || fileName.isEmpty()) {
                        continue;  // Skip parts with no filename (form fields)
                    }
                    
                    // Create a unique file name
                    String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                    String filePath = uploadPath + File.separator + uniqueFileName;
                    
                    // Save the file
                    part.write(filePath);
                    
                    // Add to image URLs
                    existingImages.add("images/properties/" + uniqueFileName);
                }
            }
            
            property.setImageUrls(existingImages);
            
            // Save the updated property
            boolean updateSuccess = propertyService.updateProperty(property);
            
            if (!updateSuccess) {
                throw new IllegalStateException("Failed to update property in the database");
            }
            
            System.out.println("Property successfully updated: " + property.getId());
            
            // Redirect to manage page with success message
            response.sendRedirect(request.getContextPath() + "/properties/manage?success=property_updated");
            
        } catch (Exception e) {
            System.err.println("Error in updateProperty: " + e.getMessage());
            e.printStackTrace();
            
            // Set error message and forward back to the form
            request.setAttribute("error", e.getMessage());
            
            // Try to get property ID from the form parameter as fallback
            String formPropertyId = request.getParameter("id");
            if (formPropertyId != null && !formPropertyId.isEmpty()) {
                Property property = propertyService.getPropertyById(formPropertyId);
                if (property != null) {
                    request.setAttribute("property", property);
                }
            }
            
            request.getRequestDispatcher("/property-form.jsp").forward(request, response);
        }
    }
    
    /**
     * Delete property
     */
    private void deleteProperty(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        // Extract property ID from path
        String pathInfo = request.getPathInfo();
        String propertyId = pathInfo.substring("/delete/".length());
        
        // Get property
        Property property = propertyService.getPropertyById(propertyId);
        if (property == null) {
            response.sendRedirect(request.getContextPath() + "/properties/manage?error=property_not_found");
            return;
        }
        
        // Check authorization
        if (!user.isAdmin() && (!(user instanceof Seller) || !property.getSellerId().equals(user.getId()))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        // Delete property images
        if (property.getImageUrls() != null) {
            for (String imageUrl : property.getImageUrls()) {
                String filePath = getServletContext().getRealPath("") + "/" + imageUrl;
                try {
                    Files.deleteIfExists(Paths.get(filePath));
                } catch (IOException e) {
                    // Log but continue
                    System.err.println("Failed to delete image: " + filePath);
                }
            }
        }
        
        // Remove property from seller's list
        userService.removePropertyFromSeller(property.getSellerId(), propertyId);
        
        // Delete property
        propertyService.deleteProperty(propertyId);
        
        // Redirect back to manage page
        response.sendRedirect(request.getContextPath() + "/properties/manage?success=property_deleted");
    }
    
    /**
     * Toggle favorite status of a property
     */
    private void toggleFavorite(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"Login required\"}");
            return;
        }
        
        // Extract property ID from path
        String pathInfo = request.getPathInfo();
        String propertyId = pathInfo.substring("/favorite/".length());
        
        // Get property to make sure it exists
        Property property = propertyService.getPropertyById(propertyId);
        if (property == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"success\":false,\"message\":\"Property not found\"}");
            return;
        }
        
        // Check if property is already in favorites
        List<String> favoriteIds = userService.getUserFavoritePropertyIds(user.getId());
        boolean added;
        boolean opSuccess;
        if (favoriteIds != null && favoriteIds.contains(propertyId)) {
            // Remove from favorites
            opSuccess = userService.removePropertyFromFavorites(user.getId(), propertyId);
            added = false;
            if (!opSuccess) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"success\":false,\"message\":\"Failed to remove from favorites\"}");
                return;
            }
        } else {
            // Add to favorites
            opSuccess = userService.addPropertyToFavorites(user.getId(), propertyId);
            added = true;
            if (!opSuccess) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"success\":false,\"message\":\"Failed to add to favorites\"}");
                return;
            }
        }
        // Refresh the user object in session
        User updatedUser = userService.authenticate(user.getEmail(), user.getPassword());
        if (updatedUser != null) {
            request.getSession().setAttribute(Constants.SESSION_USER, updatedUser);
        }
        // Return JSON response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"success\":true,\"added\":" + added + "}");
    }
    
    /**
     * Advanced search for properties
     */
    private void searchProperties(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // This is similar to listProperties but for POST requests
        listProperties(request, response);
    }
    
    /**
     * Helper method to extract file name from Part
     */
    private String getSubmittedFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                return item.substring(item.indexOf("=") + 2, item.length() - 1);
            }
        }
        return "";
    }
} 