<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.realestate.model.Property" %>
<%@ page import="com.realestate.model.User" %>
<%@ page import="com.realestate.model.Seller" %>
<%@ page import="com.realestate.util.Constants" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Check if user is logged in and is a seller or admin
    User user = (User) session.getAttribute(Constants.SESSION_USER);
    boolean isAdmin = user != null && user.isAdmin();
    boolean isSeller = user != null && (user instanceof Seller);
    
    // Redirect if not seller or admin
    if (user == null || (!isAdmin && !isSeller)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
        return;
    }
    
    List<Property> properties = (List<Property>) request.getAttribute("properties");
    if (properties == null) {
        properties = new ArrayList<>();
    }
    
    // Get status messages
    String success = request.getParameter("success");
    String error = request.getParameter("error");
    
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Property Management - Real Estate</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        .property-actions {
            display: flex;
            gap: 10px;
        }
        
        .btn-sm {
            padding: 5px 10px;
            font-size: 12px;
        }
        
        .status-message {
            margin-bottom: 20px;
            padding: 10px 15px;
            border-radius: 4px;
        }
        
        .status-message.success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .status-message.error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .manage-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        
        .empty-properties {
            text-align: center;
            padding: 40px 0;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        
        .property-count {
            font-size: 14px;
            color: var(--text-light);
            margin-top: -20px;
            margin-bottom: 20px;
            text-align: center;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <header>
        <div class="container">
            <nav class="navbar">
                <a href="<%= request.getContextPath() %>/index.jsp" class="logo">Real Estate</a>
                <ul class="nav-links">
                    <li><a href="<%= request.getContextPath() %>/index.jsp">Home</a></li>
                    <li><a href="<%= request.getContextPath() %>/properties">Properties</a></li>
                    <li><a href="<%= request.getContextPath() %>/about.jsp">About</a></li>
                    <li><a href="<%= request.getContextPath() %>/contact.jsp">Contact</a></li>
                </ul>
                
                <div class="user-actions">
                    <a href="<%= request.getContextPath() %>/dashboard.jsp" class="btn btn-outline">Dashboard</a>
                    <a href="<%= request.getContextPath() %>/logout" class="btn btn-primary">Logout</a>
                </div>
                
                <button class="menu-toggle">
                    <i class="fa fa-bars"></i>
                </button>
            </nav>
        </div>
    </header>
    
    <!-- Main Content -->
    <main>
        <section>
            <div class="container">
                <div class="manage-header">
                    <h2 class="section-title"><%= isAdmin ? "Manage All Properties" : "Manage Your Properties" %></h2>
                    <a href="<%= request.getContextPath() %>/properties/add" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Add New Property
                    </a>
                </div>
                
                <% if (properties.size() > 0) { %>
                    <p class="property-count">Total: <%= properties.size() %> <%= properties.size() == 1 ? "property" : "properties" %></p>
                <% } %>
                
                <!-- Status Messages -->
                <% if (success != null) { %>
                    <div class="status-message success">
                        <% if ("property_added".equals(success)) { %>
                            <i class="fas fa-check-circle"></i> Property has been successfully added.
                        <% } else if ("property_updated".equals(success)) { %>
                            <i class="fas fa-check-circle"></i> Property has been successfully updated.
                        <% } else if ("property_deleted".equals(success)) { %>
                            <i class="fas fa-check-circle"></i> Property has been successfully deleted.
                        <% } else { %>
                            <i class="fas fa-check-circle"></i> Operation completed successfully.
                        <% } %>
                    </div>
                <% } %>
                
                <% if (error != null) { %>
                    <div class="status-message error">
                        <% if ("property_not_found".equals(error)) { %>
                            <i class="fas fa-exclamation-circle"></i> Property not found.
                        <% } else if ("unauthorized".equals(error)) { %>
                            <i class="fas fa-exclamation-circle"></i> You don't have permission to perform this action.
                        <% } else { %>
                            <i class="fas fa-exclamation-circle"></i> An error occurred while processing your request.
                        <% } %>
                    </div>
                <% } %>
                
                <% if (properties.isEmpty()) { %>
                    <div class="empty-properties">
                        <i class="fas fa-home" style="font-size: 48px; color: var(--primary-light); display: block; margin-bottom: 20px;"></i>
                        <p>You don't have any properties yet.</p>
                        <a href="<%= request.getContextPath() %>/properties/add" class="btn btn-primary" style="margin-top: 20px;">Add Your First Property</a>
                    </div>
                <% } else { %>
                    <!-- Property Table -->
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Image</th>
                                    <th>Title</th>
                                    <th>Type</th>
                                    <th>Price</th>
                                    <th>Location</th>
                                    <% if (isAdmin) { %>
                                    <th>Seller ID</th>
                                    <% } %>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Property property : properties) { %>
                                    <tr>
                                        <td><%= property.getId() %></td>
                                        <td>
                                            <% 
                                            List<String> images = property.getImageUrls();
                                            String imageUrl = request.getContextPath() + "/images/property-placeholder.jpg";
                                            if (images != null && !images.isEmpty() && images.get(0) != null) {
                                                imageUrl = request.getContextPath() + "/" + images.get(0);
                                            }
                                            %>
                                            <img src="<%= imageUrl %>" alt="<%= property.getTitle() %>" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;">
                                        </td>
                                        <td><%= property.getTitle() %></td>
                                        <td><%= property.getPropertyType() %></td>
                                        <td><%= currencyFormat.format(property.getPrice()) %></td>
                                        <td><%= property.getLocation() %></td>
                                        <% if (isAdmin) { %>
                                        <td><%= property.getSellerId() %></td>
                                        <% } %>
                                        <td class="property-actions">
                                            <a href="<%= request.getContextPath() %>/properties/view/<%= property.getId() %>" class="btn btn-sm btn-outline" title="View">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <a href="<%= request.getContextPath() %>/properties/edit/<%= property.getId() %>" class="btn btn-sm btn-outline" title="Edit">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <button class="btn btn-sm btn-danger" title="Delete" onclick="confirmDelete('<%= property.getId() %>')">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } %>
            </div>
        </section>
    </main>
    
    <!-- Footer -->
    <footer>
        <div class="container">
            <div class="footer-content">
                <div class="footer-section">
                    <h3 class="footer-title">Real Estate</h3>
                    <p>Find your dream property with our extensive listings of homes, apartments, and commercial spaces.</p>
                </div>
                <div class="footer-section">
                    <h3 class="footer-title">Quick Links</h3>
                    <ul class="footer-links">
                        <li><a href="<%= request.getContextPath() %>/index.jsp">Home</a></li>
                        <li><a href="<%= request.getContextPath() %>/properties">Properties</a></li>
                        <li><a href="<%= request.getContextPath() %>/about.jsp">About Us</a></li>
                        <li><a href="<%= request.getContextPath() %>/contact.jsp">Contact Us</a></li>
                    </ul>
                </div>
                <div class="footer-section">
                    <h3 class="footer-title">Contact</h3>
                    <ul class="footer-links">
                        <li><i class="fas fa-map-marker-alt"></i> 123 Real Estate St, City</li>
                        <li><i class="fas fa-phone"></i> (123) 456-7890</li>
                        <li><i class="fas fa-envelope"></i> info@realestate.com</li>
                    </ul>
                </div>
            </div>
            <div class="copyright">
                &copy; 2023 Real Estate. All rights reserved.
            </div>
        </div>
    </footer>
    
    <!-- Delete Confirmation Modal (simplified version without actual modal library) -->
    <div id="deleteModal" style="display: none; position: fixed; z-index: 1000; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.5); justify-content: center; align-items: center;">
        <div style="background-color: white; padding: 20px; border-radius: 8px; max-width: 400px; text-align: center;">
            <h3>Confirm Deletion</h3>
            <p>Are you sure you want to delete this property? This action cannot be undone.</p>
            <div style="margin-top: 20px;">
                <button id="confirmDelete" class="btn btn-danger">Delete</button>
                <button onclick="closeDeleteModal()" class="btn btn-outline" style="margin-left: 10px;">Cancel</button>
            </div>
        </div>
    </div>
    
    <script>
        // Mobile menu toggle
        document.querySelector('.menu-toggle').addEventListener('click', function() {
            document.querySelector('.nav-links').classList.toggle('active');
        });
        
        // Delete confirmation
        function confirmDelete(propertyId) {
            const modal = document.getElementById('deleteModal');
            const confirmButton = document.getElementById('confirmDelete');
            
            confirmButton.onclick = function() {
                window.location.href = '<%= request.getContextPath() %>/properties/delete/' + propertyId;
            };
            
            modal.style.display = 'flex';
        }
        
        function closeDeleteModal() {
            document.getElementById('deleteModal').style.display = 'none';
        }
        
        // Close modal if user clicks outside
        window.onclick = function(event) {
            const modal = document.getElementById('deleteModal');
            if (event.target === modal) {
                closeDeleteModal();
            }
        }
    </script>
</body>
</html> 