<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.realestate.util.Constants" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Real Estate Management System</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <header>
        <div class="container">
            <nav class="navbar">
                <a href="index.jsp" class="logo">Real Estate</a>
                
                <ul class="nav-links">
                    <li><a href="index.jsp">Home</a></li>
                    <li><a href="property-details.jsp">Properties</a></li>
                    <li><a href="about.jsp">About</a></li>
                    <li><a href="displayfeedback.jsp">Reviews</a></li>
                </ul>
                
                <div class="user-actions">
                    <% if (session.getAttribute(Constants.SESSION_USER) == null) { %>
                        <a href="login.jsp" class="btn btn-outline">Login</a>
                        <a href="register.jsp" class="btn btn-primary">Register</a>
                    <% } else { %>
                        <a href="dashboard.jsp" class="btn btn-outline">Dashboard</a>
                        <a href="logout" class="btn btn-primary">Logout</a>
                    <% } %>
                </div>
                
                <button class="menu-toggle">
                    <i class="fa fa-bars"></i>
                </button>
            </nav>
        </div>
    </header>
    
    <main>
        <!-- Hero Section -->
        <section class="hero">
            <div class="hero-overlay"></div>
            <div class="container hero-container">
                <div class="hero-content">
                    <h1>Find Your Dream Home</h1>
                    <p>Discover thousands of properties for sale and rent across the country</p>
                </div>
                
                <!-- Modern Search Box -->
                <div class="search-container">
                    <div class="search-box">
                        <div class="search-box-main">
                            <div class="search-icon">
                                <i class="fas fa-search"></i>
                            </div>
                            <input type="text" id="main-search" placeholder="Enter location, property name, or address..." class="search-input">
                            <button type="button" class="search-btn" id="search-submit-btn">Search</button>
                        </div>
                        
                        <div class="search-filters">
                            <div class="filter-toggle">
                                <span>Advanced Filters</span>
                                <i class="fas fa-chevron-down"></i>
                            </div>
                            
                            <div class="expanded-filters">
                                <form action="properties" method="get" id="property-search-form">
                                    <input type="hidden" id="search" name="search">
                                    
                                    <div class="filter-grid">
                                        <div class="filter-item">
                                            <label for="propertyType">Property Type</label>
                                            <div class="select-wrapper">
                                                <select id="propertyType" name="propertyType" class="form-control">
                                                    <option value="all">All Types</option>
                                                    <option value="House">House</option>
                                                    <option value="Apartment">Apartment</option>
                                                    <option value="Villa">Villa</option>
                                                    <option value="Land">Land</option>
                                                </select>
                                                <i class="fas fa-chevron-down"></i>
                                            </div>
                                        </div>
                                        
                                        <div class="filter-item">
                                            <label>Price Range</label>
                                            <div class="range-inputs">
                                                <div class="input-wrapper">
                                                    <span class="currency-symbol">$</span>
                                                    <input type="number" id="minPrice" name="minPrice" class="form-control" placeholder="Min">
                                                </div>
                                                <span class="range-separator">to</span>
                                                <div class="input-wrapper">
                                                    <span class="currency-symbol">$</span>
                                                    <input type="number" id="maxPrice" name="maxPrice" class="form-control" placeholder="Max">
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <div class="filter-item">
                                            <label>Sort By</label>
                                            <div class="select-grid">
                                                <div class="select-wrapper">
                                                    <select id="sortBy" name="sortBy" class="form-control">
                                                        <option value="">Default</option>
                                                        <option value="price">Price</option>
                                                        <option value="newest">Newest</option>
                                                    </select>
                                                    <i class="fas fa-chevron-down"></i>
                                                </div>
                                                <div class="select-wrapper">
                                                    <select id="sortOrder" name="sortOrder" class="form-control">
                                                        <option value="asc">Ascending</option>
                                                        <option value="desc">Descending</option>
                                                    </select>
                                                    <i class="fas fa-chevron-down"></i>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="filter-actions">
                                        <button type="reset" class="btn btn-reset">Reset</button>
                                        <button type="submit" class="btn btn-apply">Apply Filters</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        
        <!-- Featured Properties Section -->
        <section class="featured-properties">
            <div class="container">
                <h2 class="section-title">Featured Properties</h2>
                <p class="section-subtitle">Handpicked properties from our trusted sellers</p>
                
                <div class="property-navigation">
                    <button class="nav-btn prev-btn" id="prev-property"><i class="fa fa-chevron-left"></i></button>
                    <button class="nav-btn next-btn" id="next-property"><i class="fa fa-chevron-right"></i></button>
                </div>
                
                <div class="property-slider">
                    <div class="property-grid" id="featured-properties">
                        <!-- Property cards will be dynamically loaded here -->
                        <div class="property-card">
                            <div class="property-image">
                                <div class="property-status-tag">For Sale</div>
                                <button class="favorite-button" data-property-id="1">
                                    <i class="far fa-heart"></i>
                                </button>
                                <img src="https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=60" alt="Modern Apartment">
                            </div>
                            <div class="property-details">
                                <h3 class="property-title">Modern Apartment</h3>
                                <div class="property-price">$250,000</div>
                                <div class="property-location">
                                    <i class="fa fa-map-marker-alt"></i> New York, NY
                                </div>
                                <div class="property-features">
                                    <span><i class="fa fa-bed"></i> 2 beds</span>
                                    <span><i class="fa fa-bath"></i> 2 baths</span>
                                    <span><i class="fa fa-ruler-combined"></i> 1,200 sqft</span>
                                </div>
                                <a href="property-details.jsp?id=1" class="btn btn-outline view-details">View Details</a>
                            </div>
                        </div>
                        
                        <div class="property-card">
                            <div class="property-image">
                                <div class="property-status-tag">For Sale</div>
                                <button class="favorite-button" data-property-id="2">
                                    <i class="far fa-heart"></i>
                                </button>
                                <img src="https://images.unsplash.com/photo-1570129477492-45c003edd2be?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=60" alt="Family House">
                            </div>
                            <div class="property-details">
                                <h3 class="property-title">Family House</h3>
                                <div class="property-price">$450,000</div>
                                <div class="property-location">
                                    <i class="fa fa-map-marker-alt"></i> Los Angeles, CA
                                </div>
                                <div class="property-features">
                                    <span><i class="fa fa-bed"></i> 4 beds</span>
                                    <span><i class="fa fa-bath"></i> 3 baths</span>
                                    <span><i class="fa fa-ruler-combined"></i> 2,500 sqft</span>
                                </div>
                                <a href="property-details.jsp?id=2" class="btn btn-outline view-details">View Details</a>
                            </div>
                        </div>
                        
                        <div class="property-card">
                            <div class="property-image">
                                <div class="property-status-tag">For Rent</div>
                                <button class="favorite-button" data-property-id="3">
                                    <i class="far fa-heart"></i>
                                </button>
                                <img src="https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=60" alt="Luxury Condo">
                            </div>
                            <div class="property-details">
                                <h3 class="property-title">Luxury Condo</h3>
                                <div class="property-price">$350,000</div>
                                <div class="property-location">
                                    <i class="fa fa-map-marker-alt"></i> Miami, FL
                                </div>
                                <div class="property-features">
                                    <span><i class="fa fa-bed"></i> 3 beds</span>
                                    <span><i class="fa fa-bath"></i> 2 baths</span>
                                    <span><i class="fa fa-ruler-combined"></i> 1,800 sqft</span>
                                </div>
                                <a href="property-details.jsp?id=3" class="btn btn-outline view-details">View Details</a>
                            </div>
                        </div>
                        
                        <div class="property-card">
                            <div class="property-image">
                                <div class="property-status-tag">For Rent</div>
                                <button class="favorite-button" data-property-id="4">
                                    <i class="far fa-heart"></i>
                                </button>
                                <img src="https://images.unsplash.com/photo-1493809842364-78817add7ffb?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=60" alt="Downtown Loft">
                            </div>
                            <div class="property-details">
                                <h3 class="property-title">Downtown Loft</h3>
                                <div class="property-price">$2,200/month</div>
                                <div class="property-location">
                                    <i class="fa fa-map-marker-alt"></i> Chicago, IL
                                </div>
                                <div class="property-features">
                                    <span><i class="fa fa-bed"></i> 1 beds</span>
                                    <span><i class="fa fa-bath"></i> 1 baths</span>
                                    <span><i class="fa fa-ruler-combined"></i> 950 sqft</span>
                                </div>
                                <a href="property-details.jsp?id=4" class="btn btn-outline view-details">View Details</a>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="view-all">
                    <a href="property-details.jsp" class="btn btn-outline">View All Properties</a>
                </div>
            </div>
        </section>


        
        <!-- Services Section -->
        <section class="services">
            <div class="container">
                <h2 class="section-title">Our Services</h2>
                
                <div class="services-grid">
                    <div class="service-card">
                        <div class="service-icon">
                            <i class="fa fa-home"></i>
                        </div>
                        <h3>Buy a Property</h3>
                        <p>Find your dream home from our extensive listings of properties.</p>
                    </div>
                    
                    <div class="service-card">
                        <div class="service-icon">
                            <i class="fa fa-money-bill-wave"></i>
                        </div>
                        <h3>Sell a Property</h3>
                        <p>List your property and reach thousands of potential buyers.</p>
                    </div>
                    
                    <div class="service-card">
                        <div class="service-icon">
                            <i class="fa fa-search"></i>
                        </div>
                        <h3>Property Search</h3>
                        <p>Advanced search options to find exactly what you're looking for.</p>
                    </div>
                </div>
            </div>
        </section>

        <!-- Reviews Section -->
        <section class="reviews">
            <div class="container">
                <h2 class="section-title">What Our Clients Say</h2>
                <p class="section-subtitle">Read testimonials from our satisfied customers</p>

                <div class="reviews-grid">
                    <div class="review-card">
                        <div class="review-rating">
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                        </div>
                        <p class="review-text">
                            "The team helped me find my dream home in just two weeks! Their service was exceptional and they really understood what I was looking for."
                        </p>
                        <div class="review-author">
                            <div class="author-avatar">
                                <img src="images/avatar-placeholder.jpg" alt="John D.">
                            </div>
                            <div class="author-info">
                                <h4>John D.</h4>
                                <p>Home Buyer</p>
                            </div>
                        </div>
                    </div>

                    <div class="review-card">
                        <div class="review-rating">
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                        </div>
                        <p class="review-text">
                            "Sold my property above asking price thanks to their excellent marketing strategy. Highly recommend their services!"
                        </p>
                        <div class="review-author">
                            <div class="author-avatar">
                                <img src="images/avatar-placeholder.jpg" alt="Sarah M.">
                            </div>
                            <div class="author-info">
                                <h4>Sarah M.</h4>
                                <p>Home Seller</p>
                            </div>
                        </div>
                    </div>

                    <div class="review-card">
                        <div class="review-rating">
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star-half-o"></i>
                        </div>
                        <p class="review-text">
                            "As a first-time buyer, I was nervous about the process, but my agent guided me through every step. Couldn't be happier with my new home!"
                        </p>
                        <div class="review-author">
                            <div class="author-avatar">
                                <img src="images/avatar-placeholder.jpg" alt="Michael T.">
                            </div>
                            <div class="author-info">
                                <h4>Michael T.</h4>
                                <p>First-time Buyer</p>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="review-cta">
                    <a href="feedback.jsp" class="btn btn-primary">Leave a Review</a>
                </div>
            </div>
        </section>


    </main>
    
    <footer>
        <div class="container">
            <div class="footer-content">
                <div class="footer-section">
                    <h3>Real Estate</h3>
                    <p>Your trusted partner in finding the perfect property.</p>
                </div>
                
                <div class="footer-section">
                    <h3>Quick Links</h3>
                    <ul>
                        <li><a href="index.jsp">Home</a></li>
                        <li><a href="properties.jsp">Properties</a></li>
                        <li><a href="about.jsp">About Us</a></li>
                        <li><a href="displayfeedback.jsp">Reviews</a></li>
                    </ul>
                </div>
                
                <div class="footer-section">
                    <h3>Contact Us</h3>
                    <ul>
                        <li><i class="fa fa-map-marker-alt"></i> 123 Main St, City</li>
                        <li><i class="fa fa-phone"></i> (123) 456-7890</li>
                        <li><i class="fa fa-envelope"></i> info@realestate.com</li>
                    </ul>
                </div>
            </div>
            
            <div class="footer-bottom">
                <p>&copy; 2023 Real Estate Management System. All rights reserved.</p>
            </div>
        </div>
    </footer>
    
    <script src="js/main.js"></script>
</body>
</html>
