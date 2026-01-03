<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.realestate.model.Property" %>
<%@ page import="com.realestate.model.User" %>
<%@ page import="com.realestate.util.Constants" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Check if user is logged in
    User user = (User) session.getAttribute(Constants.SESSION_USER);
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required&redirect=properties/favorites");
        return;
    }
    
    // Get favorite properties
    List<Property> properties = (List<Property>) request.getAttribute("properties");
    if (properties == null) {
        properties = new ArrayList<>();
    }
    
    // Format numbers
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    
    // Flag to handle special styling for favorites page
    boolean isFavoritesPage = request.getAttribute("isFavoritesPage") != null && 
                             (boolean) request.getAttribute("isFavoritesPage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Favorites - Real Estate</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
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
            position: relative;
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
            z-index: 10;
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
            display: block;
            height: 100%;
        }
        
        .empty-favorites {
            text-align: center;
            padding: 50px 0;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        
        .remove-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: rgba(0, 0, 0, 0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0;
            transition: opacity 0.3s;
        }
        
        .property-card:hover .remove-overlay {
            opacity: 1;
        }
        
        .remove-btn {
            background-color: white;
            color: var(--error-color);
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            font-weight: 500;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 5px;
            transition: all 0.3s;
        }
        
        .remove-btn:hover {
            background-color: var(--error-color);
            color: white;
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
                    <a href="<%= request.getContextPath() %>/properties/favorites" class="btn btn-outline active"><i class="fas fa-heart"></i> Favorites</a>
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
                <h2 class="section-title">My Favorites</h2>
                
                <% if (properties.isEmpty()) { %>
                    <div class="empty-favorites">
                        <i class="far fa-heart" style="font-size: 48px; color: var(--primary-light); display: block; margin-bottom: 20px;"></i>
                        <h3>Your favorites list is empty</h3>
                        <p style="margin-top: 10px; color: var(--text-light);">Start adding properties to your favorites to see them here.</p>
                        <a href="<%= request.getContextPath() %>/properties" class="btn btn-primary" style="margin-top: 20px;">Browse Properties</a>
                    </div>
                <% } else { %>
                    <div class="property-grid">
                        <% for (Property property : properties) { %>
                            <div class="property-card">
                                <a href="<%= request.getContextPath() %>/properties/view/<%= property.getId() %>" class="property-link">
                                    <div class="property-image">
                                        <% 
                                        List<String> images = property.getImageUrls();
                                        String imageUrl = request.getContextPath() + "/images/property-placeholder.jpg";
                                        if (images != null && !images.isEmpty() && images.get(0) != null) {
                                            imageUrl = request.getContextPath() + "/" + images.get(0);
                                        }
                                        %>
                                        <img src="<%= imageUrl %>" alt="<%= property.getTitle() %>">
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
                                </a>
                                <div class="property-favorite" 
                                     onclick="toggleFavorite(event, '<%= property.getId() %>', this)"
                                     data-property-id="<%= property.getId() %>">
                                    <i class="fas fa-heart" style="color: #ff4081;"></i>
                                </div>
                                
                                <div class="remove-overlay">
                                    <button class="remove-btn" onclick="toggleFavorite(event, '<%= property.getId() %>', this.parentNode.parentNode.querySelector('.property-favorite'))">
                                        <i class="fas fa-trash"></i> Remove from Favorites
                                    </button>
                                </div>
                            </div>
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
        
        // Favorite toggle function
        function toggleFavorite(event, propertyId, element) {
            event.preventDefault();
            event.stopPropagation();
            
            fetch('<%= request.getContextPath() %>/properties/favorite/' + propertyId, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // On favorites page, we always remove the card
                    const card = element.closest('.property-card');
                    card.style.opacity = '0';
                    card.style.transform = 'scale(0.8)';
                    card.style.transition = 'all 0.3s';
                    
                    setTimeout(() => {
                        card.remove();
                        
                        // If no more cards, show empty message
                        const cards = document.querySelectorAll('.property-card');
                        if (cards.length === 0) {
                            location.reload();
                        }
                    }, 300);
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