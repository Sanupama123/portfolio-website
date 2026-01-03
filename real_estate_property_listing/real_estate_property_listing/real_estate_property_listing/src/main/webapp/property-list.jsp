<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.realestate.model.Property" %>
<%@ page import="com.realestate.model.User" %>
<%@ page import="com.realestate.util.Constants" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<Property> properties = (List<Property>) request.getAttribute("properties");
    if (properties == null) {
        properties = new ArrayList<>();
    }
    
    List<String> favoriteIds = (List<String>) request.getAttribute("favoriteIds");
    if (favoriteIds == null) {
        favoriteIds = new ArrayList<>();
    }
    
    String search = (String) request.getAttribute("search");
    String propertyType = (String) request.getAttribute("propertyType");
    String minPrice = (String) request.getAttribute("minPrice");
    String maxPrice = (String) request.getAttribute("maxPrice");
    String sortBy = (String) request.getAttribute("sortBy");
    String sortOrder = (String) request.getAttribute("sortOrder");
    
    // Check if user is logged in
    User user = (User) session.getAttribute(Constants.SESSION_USER);
    boolean isLoggedIn = user != null;
    
    // Format numbers
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Properties - Real Estate</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
    <style>
        .property-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 30px;
            margin-top: 30px;
        }
        
        .property-card {
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            overflow: hidden;
            transition: transform 0.3s, box-shadow 0.3s;
            background-color: white;
        }
        
        .property-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        }
        
        .property-image {
            height: 200px;
            overflow: hidden;
            position: relative;
        }
        
        .property-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        
        .property-favorite {
            position: absolute;
            top: 10px;
            right: 10px;
            background-color: rgba(255, 255, 255, 0.8);
            border-radius: 50%;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        
        .property-favorite:hover {
            background-color: white;
        }
        
        .property-content {
            padding: 15px;
        }
        
        .property-price {
            font-size: 20px;
            font-weight: bold;
            color: var(--primary-color);
            margin-bottom: 5px;
        }
        
        .property-title {
            font-size: 18px;
            margin-bottom: 5px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        
        .property-location {
            color: var(--text-light);
            font-size: 14px;
            margin-bottom: 10px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        
        .property-features {
            display: flex;
            justify-content: space-between;
            font-size: 14px;
            color: var(--text-light);
            margin-top: 10px;
            border-top: 1px solid #eee;
            padding-top: 10px;
        }
        
        .property-feature {
            display: flex;
            align-items: center;
        }
        
        .property-feature i {
            margin-right: 5px;
            font-size: 16px;
        }
        
        .property-link {
            text-decoration: none;
            color: inherit;
        }
        
        .no-properties {
            text-align: center;
            padding: 40px 0;
            font-size: 18px;
            color: var(--text-light);
            background-color: white;
            border-radius: 8px;
            margin-top: 30px;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <header>
        <div class="container">
            <nav class="navbar">
                <a href="index.jsp" class="logo">Real Estate</a>
                <ul class="nav-links">
                    <li><a href="index.jsp">Home</a></li>
                    <li><a href="properties" class="active">Properties</a></li>
                    <li><a href="about.jsp">About</a></li>
                    <li><a href="contact.jsp">Contact</a></li>
                </ul>
                
                <div class="user-actions">
                    <% if (isLoggedIn) { %>
                        <a href="properties/favorites" class="btn btn-outline"><i class="fas fa-heart"></i> Favorites</a>
                        <a href="dashboard.jsp" class="btn btn-outline">Dashboard</a>
                        <a href="logout" class="btn btn-primary">Logout</a>
                    <% } else { %>
                        <a href="login.jsp" class="btn btn-outline">Login</a>
                        <a href="register.jsp" class="btn btn-primary">Register</a>
                    <% } %>
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
            <div class="container property-list-page">
                <h2 class="section-title">Browse Properties</h2>
                
                <!-- Search and Filter -->
                <div class="filters">
                    <form action="properties" method="get">
                        <div class="filter-row">
                            <div class="filter-item">
                                <label for="search" class="form-label">Search</label>
                                <input type="text" id="search" name="search" class="form-control" placeholder="Search properties..." value="<%= search != null ? search : "" %>">
                            </div>
                            <div class="filter-item">
                                <label for="propertyType" class="form-label">Property Type</label>
                                <select id="propertyType" name="propertyType" class="form-control">
                                    <option value="all" <%= propertyType == null || propertyType.equals("all") ? "selected" : "" %>>All Types</option>
                                    <option value="House" <%= "House".equals(propertyType) ? "selected" : "" %>>House</option>
                                    <option value="Apartment" <%= "Apartment".equals(propertyType) ? "selected" : "" %>>Apartment</option>
                                    <option value="Villa" <%= "Villa".equals(propertyType) ? "selected" : "" %>>Villa</option>
                                    <option value="Land" <%= "Land".equals(propertyType) ? "selected" : "" %>>Land</option>
                                </select>
                            </div>
                        </div>
                        
                        <div class="filter-row">
                            <div class="filter-item">
                                <label for="minPrice" class="form-label">Min Price</label>
                                <input type="number" id="minPrice" name="minPrice" class="form-control" placeholder="Min price" value="<%= minPrice != null ? minPrice : "" %>">
                            </div>
                            <div class="filter-item">
                                <label for="maxPrice" class="form-label">Max Price</label>
                                <input type="number" id="maxPrice" name="maxPrice" class="form-control" placeholder="Max price" value="<%= maxPrice != null ? maxPrice : "" %>">
                            </div>
                            <div class="filter-item">
                                <label for="sortBy" class="form-label">Sort By</label>
                                <select id="sortBy" name="sortBy" class="form-control">
                                    <option value="" <%= sortBy == null || sortBy.isEmpty() ? "selected" : "" %>>Default</option>
                                    <option value="price" <%= "price".equals(sortBy) ? "selected" : "" %>>Price</option>
                                    <option value="newest" <%= "newest".equals(sortBy) ? "selected" : "" %>>Newest</option>
                                </select>
                            </div>
                            <div class="filter-item">
                                <label for="sortOrder" class="form-label">Order</label>
                                <select id="sortOrder" name="sortOrder" class="form-control">
                                    <option value="asc" <%= sortOrder == null || "asc".equals(sortOrder) ? "selected" : "" %>>Ascending</option>
                                    <option value="desc" <%= "desc".equals(sortOrder) ? "selected" : "" %>>Descending</option>
                                </select>
                            </div>
                        </div>
                        
                        <div style="margin-top: 20px; text-align: right;">
                            <button type="submit" class="btn btn-primary">Filter</button>
                            <a href="properties" class="btn btn-outline" style="margin-left: 10px;">Clear Filters</a>
                        </div>
                    </form>
                </div>
                
                <% if (properties.isEmpty()) { %>
                    <div class="no-properties">
                        <i class="fas fa-home" style="font-size: 48px; color: var(--primary-light); display: block; margin-bottom: 20px;"></i>
                        <p>No properties found matching your criteria.</p>
                    </div>
                <% } else { %>
                    <!-- Property Grid -->
                    <div class="property-grid">
                        <% for (Property property : properties) { %>
                            <a href="properties/view/<%= property.getId() %>" class="property-link">
                                <div class="property-card">
                                    <div class="property-image">
                                        <% 
                                        List<String> images = property.getImageUrls();
                                        String imageUrl = "images/property-placeholder.jpg";
                                        if (images != null && !images.isEmpty() && images.get(0) != null) {
                                            imageUrl = images.get(0);
                                        }
                                        %>
                                        <img src="<%= imageUrl %>" alt="<%= property.getTitle() %>">
                                        
                                        <% if (isLoggedIn) { %>
                                            <div class="property-favorite" 
                                                 onclick="toggleFavorite(event, '<%= property.getId() %>', this)"
                                                 data-property-id="<%= property.getId() %>">
                                                <i class="<%= favoriteIds.contains(property.getId()) ? "fas" : "far" %> fa-heart" 
                                                   style="<%= favoriteIds.contains(property.getId()) ? "color: #ff4081;" : "color: #555;" %>"></i>
                                            </div>
                                        <% } %>
                                    </div>
                                    <div class="property-content">
                                        <div class="property-price"><%= currencyFormat.format(property.getPrice()) %></div>
                                        <h3 class="property-title"><%= property.getTitle() %></h3>
                                        <p class="property-location"><i class="fas fa-map-marker-alt"></i> <%= property.getLocation() %></p>
                                        <div class="property-features">
                                            <div class="property-feature">
                                                <i class="fas fa-bed"></i> <%= property.getBedrooms() %> Beds
                                            </div>
                                            <div class="property-feature">
                                                <i class="fas fa-bath"></i> <%= property.getBathrooms() %> Baths
                                            </div>
                                            <div class="property-feature">
                                                <i class="fas fa-ruler-combined"></i> <%= property.getArea() %> sqft
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </a>
                        <% } %>
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
                        <li><a href="index.jsp">Home</a></li>
                        <li><a href="properties">Properties</a></li>
                        <li><a href="about.jsp">About Us</a></li>
                        <li><a href="contact.jsp">Contact Us</a></li>
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
    
    <script>
        // Mobile menu toggle
        document.querySelector('.menu-toggle').addEventListener('click', function() {
            document.querySelector('.nav-links').classList.toggle('active');
        });
        
        // Favorite toggle function
        function toggleFavorite(event, propertyId, element) {
            event.preventDefault();
            event.stopPropagation();
            
            fetch('properties/favorite/' + propertyId, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const heartIcon = element.querySelector('i');
                    if (data.added) {
                        heartIcon.classList.remove('far');
                        heartIcon.classList.add('fas');
                        heartIcon.style.color = '#ff4081';
                    } else {
                        heartIcon.classList.remove('fas');
                        heartIcon.classList.add('far');
                        heartIcon.style.color = '#555';
                    }
                } else {
                    alert(data.message || 'Error updating favorites');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred while updating favorites');
            });
        }
    </script>
</body>
</html> 