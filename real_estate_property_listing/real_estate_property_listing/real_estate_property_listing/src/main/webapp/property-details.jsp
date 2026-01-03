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
    Property property = (Property) request.getAttribute("property");
    Seller seller = (Seller) request.getAttribute("seller");
    Boolean isFavorite = (Boolean) request.getAttribute("isFavorite");
    
    if (property == null) {
        response.sendRedirect(request.getContextPath() + "/properties");
        return;
    }
    
    User user = (User) session.getAttribute(Constants.SESSION_USER);
    boolean isLoggedIn = user != null;
    boolean canEdit = isLoggedIn && (user.isAdmin() || (user instanceof Seller && property.getSellerId().equals(user.getId())));
    
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    
    List<String> images = property.getImageUrls();
    if (images == null) {
        images = new ArrayList<>();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= property.getTitle() %> - Real Estate</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        .property-details-container {
            display: flex;
            flex-wrap: wrap;
            gap: 30px;
            margin-top: 30px;
        }
        
        .property-gallery {
            flex: 1;
            min-width: 300px;
        }
        
        .property-info {
            flex: 1;
            min-width: 300px;
        }
        
        .property-main-image {
            width: 100%;
            height: 400px;
            border-radius: 8px;
            overflow: hidden;
            margin-bottom: 10px;
        }
        
        .property-main-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        
        .property-thumbnails {
            display: flex;
            gap: 10px;
            overflow-x: auto;
            margin-bottom: 20px;
        }
        
        .property-thumbnail {
            width: 80px;
            height: 60px;
            border-radius: 4px;
            overflow: hidden;
            cursor: pointer;
            border: 2px solid transparent;
            transition: border-color 0.3s;
        }
        
        .property-thumbnail.active {
            border-color: var(--primary-color);
        }
        
        .property-thumbnail img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        
        .property-title {
            font-size: 24px;
            margin-bottom: 10px;
            color: var(--text-color);
        }
        
        .property-price {
            font-size: 28px;
            font-weight: 700;
            color: var(--primary-color);
            margin-bottom: 15px;
        }
        
        .property-location {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
            color: var(--text-light);
        }
        
        .property-location i {
            margin-right: 5px;
            color: var(--primary-color);
        }
        
        .property-features {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            margin-bottom: 20px;
        }
        
        .property-feature {
            display: flex;
            align-items: center;
            background-color: var(--primary-light);
            padding: 8px 12px;
            border-radius: 4px;
            font-size: 14px;
        }
        
        .property-feature i {
            margin-right: 5px;
            color: var(--primary-color);
        }
        
        .property-description {
            margin-bottom: 30px;
        }
        
        .section-subtitle {
            font-size: 18px;
            margin-bottom: 10px;
            color: var(--text-color);
            border-bottom: 1px solid #eee;
            padding-bottom: 10px;
        }
        
        .property-actions {
            display: flex;
            gap: 10px;
            margin-top: 30px;
        }
        
        .seller-info {
            margin-top: 30px;
            background-color: #f9f9f9;
            border-radius: 8px;
            padding: 20px;
        }
        
        .seller-info h3 {
            margin-bottom: 10px;
            font-size: 18px;
        }
        
        .seller-details {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .seller-avatar {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background-color: var(--primary-light);
            display: flex;
            align-items: center;
            justify-content: center;
            color: var(--primary-color);
            font-weight: bold;
            font-size: 20px;
        }
        
        .seller-contact {
            margin-top: 15px;
        }
        
        .seller-contact p {
            margin-bottom: 5px;
        }
        
        .favorite-btn {
            display: flex;
            align-items: center;
            gap: 5px;
            padding: 8px 12px;
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.3s;
            font-weight: 500;
            text-decoration: none;
        }
        
        .favorite-btn.active {
            background-color: #ffecf1;
            color: #ff4081;
        }
        
        .favorite-btn:not(.active) {
            background-color: #f1f3f4;
            color: #555;
        }
        
        .favorite-btn:hover {
            transform: translateY(-2px);
        }
        
        .property-not-found {
            text-align: center;
            padding: 50px 0;
        }
        
        @media (max-width: 768px) {
            .property-main-image {
                height: 300px;
            }
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
                    <% if (isLoggedIn) { %>
                        <a href="<%= request.getContextPath() %>/properties/favorites" class="btn btn-outline"><i class="fas fa-heart"></i> Favorites</a>
                        <a href="<%= request.getContextPath() %>/dashboard.jsp" class="btn btn-outline">Dashboard</a>
                        <a href="<%= request.getContextPath() %>/logout" class="btn btn-primary">Logout</a>
                    <% } else { %>
                        <a href="<%= request.getContextPath() %>/login.jsp" class="btn btn-outline">Login</a>
                        <a href="<%= request.getContextPath() %>/register.jsp" class="btn btn-primary">Register</a>
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
            <div class="container">
                <div class="property-details-container">
                    <div class="property-gallery">
                        <div class="property-main-image">
                            <% 
                            String mainImage = "images/property-placeholder.jpg";
                            if (!images.isEmpty() && images.get(0) != null) {
                                mainImage = images.get(0);
                            }
                            %>
                            <img src="<%= request.getContextPath() + "/" + mainImage %>" alt="<%= property.getTitle() %>" id="mainImage">
                        </div>
                        
                        <% if (images.size() > 1) { %>
                            <div class="property-thumbnails">
                                <% for (int i = 0; i < images.size(); i++) { %>
                                    <div class="property-thumbnail <%= i == 0 ? "active" : "" %>" onclick="changeMainImage('<%= request.getContextPath() + "/" + images.get(i) %>', this)">
                                        <img src="<%= request.getContextPath() + "/" + images.get(i) %>" alt="Thumbnail <%= i + 1 %>">
                                    </div>
                                <% } %>
                            </div>
                        <% } %>
                    </div>
                    
                    <div class="property-info">
                        <h1 class="property-title"><%= property.getTitle() %></h1>
                        <div class="property-price"><%= currencyFormat.format(property.getPrice()) %></div>
                        <div class="property-location">
                            <i class="fas fa-map-marker-alt"></i>
                            <%= property.getLocation() %>
                        </div>
                        
                        <div class="property-features">
                            <div class="property-feature">
                                <i class="fas fa-home"></i>
                                <%= property.getPropertyType() %>
                            </div>
                            <div class="property-feature">
                                <i class="fas fa-bed"></i>
                                <%= property.getBedrooms() %> Bedrooms
                            </div>
                            <div class="property-feature">
                                <i class="fas fa-bath"></i>
                                <%= property.getBathrooms() %> Bathrooms
                            </div>
                            <div class="property-feature">
                                <i class="fas fa-ruler-combined"></i>
                                <%= property.getArea() %> sqft
                            </div>
                        </div>
                        
                        <div class="property-actions">
                            <% if (isLoggedIn) { %>
                                <button id="favoriteBtn" 
                                        class="favorite-btn <%= isFavorite != null && isFavorite ? "active" : "" %>"
                                        onclick="toggleFavorite('<%= property.getId() %>')">
                                    <i class="<%= isFavorite != null && isFavorite ? "fas" : "far" %> fa-heart"></i>
                                    <%= isFavorite != null && isFavorite ? "Remove from Favorites" : "Add to Favorites" %>
                                </button>
                            <% } %>
                            
                            <% if (canEdit) { %>
                                <a href="<%= request.getContextPath() %>/properties/edit/<%= property.getId() %>" class="btn btn-outline">
                                    <i class="fas fa-edit"></i> Edit
                                </a>
                            <% } %>
                        </div>
                        
                        <% if (seller != null) { %>
                            <div class="seller-info">
                                <h3>Seller Information</h3>
                                <div class="seller-details">
                                    <div class="seller-avatar">
                                        <%= seller.getFirstName().substring(0, 1) %>
                                    </div>
                                    <div>
                                        <p><strong><%= seller.getFullName() %></strong></p>
                                    </div>
                                </div>
                                <div class="seller-contact">
                                    <p><i class="fas fa-envelope"></i> <%= seller.getEmail() %></p>
                                    <p><i class="fas fa-phone"></i> <%= seller.getPhoneNumber() %></p>
                                </div>
                            </div>
                        <% } %>
                    </div>
                </div>
                
                <div style="margin-top: 40px;">
                    <h2 class="section-subtitle">Description</h2>
                    <div class="property-description">
                        <%= property.getDescription() %>
                    </div>
                </div>
                
                <div style="margin-top: 20px; text-align: center;">
                    <a href="<%= request.getContextPath() %>/properties" class="btn btn-outline">
                        <i class="fas fa-arrow-left"></i> Back to Properties
                    </a>
                </div>
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
    
    <script>
        // Mobile menu toggle
        document.querySelector('.menu-toggle').addEventListener('click', function() {
            document.querySelector('.nav-links').classList.toggle('active');
        });
        
        // Image gallery
        function changeMainImage(src, thumbnail) {
            document.getElementById('mainImage').src = src;
            
            // Update active state
            document.querySelectorAll('.property-thumbnail').forEach(function(thumb) {
                thumb.classList.remove('active');
            });
            
            thumbnail.classList.add('active');
        }
        
        // Favorite toggle
        function toggleFavorite(propertyId) {
            fetch('<%= request.getContextPath() %>/properties/favorite/' + propertyId, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const favoriteBtn = document.getElementById('favoriteBtn');
                    const favoriteIcon = favoriteBtn.querySelector('i');
                    
                    if (data.added) {
                        favoriteBtn.classList.add('active');
                        favoriteIcon.classList.remove('far');
                        favoriteIcon.classList.add('fas');
                        favoriteBtn.textContent = '';
                        favoriteBtn.appendChild(favoriteIcon);
                        favoriteBtn.appendChild(document.createTextNode(' Remove from Favorites'));
                    } else {
                        favoriteBtn.classList.remove('active');
                        favoriteIcon.classList.remove('fas');
                        favoriteIcon.classList.add('far');
                        favoriteBtn.textContent = '';
                        favoriteBtn.appendChild(favoriteIcon);
                        favoriteBtn.appendChild(document.createTextNode(' Add to Favorites'));
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