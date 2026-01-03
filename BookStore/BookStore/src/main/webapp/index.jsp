<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bookstore.models.User" %>
<%
  User user = (User) session.getAttribute("user");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BookNest - Your Online Bookstore</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
        }
        .hero-section {
            background: linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.6)),
                        url('https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&q=80');
            background-size: cover;
            background-position: center;
            height: 600px;
            color: white;
        }
        .search-box {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
        }
        .navbar-brand {
            font-weight: 700;
            font-size: 1.8rem;
        }
        .nav-link {
            font-weight: 500;
        }
        .category-card {
            transition: transform 0.3s;
            cursor: pointer;
        }
        .category-card:hover {
            transform: translateY(-5px);
        }
        .cart-badge {
            position: relative;
            top: -10px;
            right: 5px;
            padding: 4px 6px;
            border-radius: 50%;
            background: #dc3545;
            color: white;
            font-size: 0.7rem;
        }
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm fixed-top">
        <div class="container">
            <a class="navbar-brand" href="#">
                <i class="fas fa-book-reader text-primary"></i> BookNest
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="index.jsp"><i class="fas fa-home"></i> Home</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="categories"><i class="fas fa-th"></i> Categories</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="deals"><i class="fas fa-tags"></i> Deals</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="cart">
                            <i class="fas fa-shopping-cart"></i>
                            <span class="cart-badge">0</span>
                        </a>
                    </li>
                    <% if (user != null) { %>
                        <!-- Logged in user navigation -->
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
                                <i class="fas fa-user-circle"></i> 
                                <%= user.getFirstName() != null ? user.getFirstName() : "User" %>
                            </a>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile">
                                    <i class="fas fa-user-cog me-2"></i>My Profile
                                </a></li>
                                <% if (user.isAdmin()) { %>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/dashboard">
                                        <i class="fas fa-tachometer-alt me-2"></i>Admin Dashboard
                                    </a></li>
                                <% } else if (user.isSeller()) { %>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/seller/dashboard">
                                        <i class="fas fa-store me-2"></i>Seller Dashboard
                                    </a></li>
                                <% } else { %>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/buyer/dashboard">
                                        <i class="fas fa-tachometer-alt me-2"></i>My Dashboard
                                    </a></li>
                                <% } %>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                    <i class="fas fa-sign-out-alt me-2"></i>Logout
                                </a></li>
                            </ul>
                        </li>
                    <% } else { %>
                        <!-- Guest user navigation -->
                        <li class="nav-item ms-2">
                            <a class="btn btn-outline-primary" href="login"><i class="fas fa-sign-in-alt"></i> Login</a>
                        </li>
                        <li class="nav-item ms-2">
                            <a class="btn btn-success" href="register"><i class="fas fa-user-plus"></i> Register</a>
                        </li>
                    <% } %>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Hero Section with Search -->
    <section class="hero-section d-flex align-items-center" style="margin-top: 56px;">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-8 text-center mb-4">
                    <h1 class="display-4 fw-bold mb-4">Discover Your Next Favorite Book</h1>
                    <p class="lead mb-5">Explore our vast collection of books across multiple genres</p>
                </div>
                <div class="col-md-10">
                    <div class="search-box">
                        <form>
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <div class="input-group">
                                        <span class="input-group-text bg-white">
                                            <i class="fas fa-search text-primary"></i>
                                        </span>
                                        <input type="text" class="form-control" placeholder="Search by title, author, or ISBN...">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <select class="form-select">
                                        <option selected>Select Genre</option>
                                        <option>Fiction</option>
                                        <option>Non-Fiction</option>
                                        <option>Science</option>
                                        <option>Technology</option>
                                        <option>Business</option>
                                        <option>Art & Music</option>
                                        <option>History</option>
                                        <option>Biography</option>
                                    </select>
                                </div>
                                <div class="col-md-3">
                                    <button type="submit" class="btn btn-primary w-100">Search Books</button>
                                </div>
                                <div class="col-12">
                                    <div class="row g-3">
                                        <div class="col-md-4">
                                            <select class="form-select">
                                                <option selected>Price Range</option>
                                                <option>Under $10</option>
                                                <option>$10 - $20</option>
                                                <option>$20 - $30</option>
                                                <option>Over $30</option>
                                            </select>
                                        </div>
                                        <div class="col-md-4">
                                            <select class="form-select">
                                                <option selected>Rating</option>
                                                <option>4★ & up</option>
                                                <option>3★ & up</option>
                                                <option>2★ & up</option>
                                            </select>
                                        </div>
                                        <div class="col-md-4">
                                            <select class="form-select">
                                                <option selected>Sort By</option>
                                                <option>Newest Arrivals</option>
                                                <option>Best Selling</option>
                                                <option>Price: Low to High</option>
                                                <option>Price: High to Low</option>
                                            </select>
                                        </div>
                                        <div class="col-12">
                                            <div class="d-flex gap-3 flex-wrap">
                                                <div class="form-check">
                                                    <input class="form-check-input" type="checkbox" id="inStock">
                                                    <label class="form-check-label" for="inStock">In Stock</label>
                                                </div>
                                                <div class="form-check">
                                                    <input class="form-check-input" type="checkbox" id="onSale">
                                                    <label class="form-check-label" for="onSale">On Sale</label>
                                                </div>
                                                <div class="form-check">
                                                    <input class="form-check-input" type="checkbox" id="bestSeller">
                                                    <label class="form-check-label" for="bestSeller">Best Sellers</label>
                                                </div>
                                                <div class="form-check">
                                                    <input class="form-check-input" type="checkbox" id="newRelease">
                                                    <label class="form-check-label" for="newRelease">New Releases</label>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Featured Categories -->
    <section class="py-5">
        <div class="container">
            <h2 class="text-center mb-4">Popular Categories</h2>
            <div class="row g-4">
                <div class="col-md-3">
                    <div class="card category-card h-100">
                        <div class="card-body text-center">
                            <i class="fas fa-book fa-3x text-primary mb-3"></i>
                            <h5 class="card-title">Fiction</h5>
                            <p class="card-text text-muted">Explore imaginary worlds</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card category-card h-100">
                        <div class="card-body text-center">
                            <i class="fas fa-microscope fa-3x text-success mb-3"></i>
                            <h5 class="card-title">Science</h5>
                            <p class="card-text text-muted">Discover the universe</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card category-card h-100">
                        <div class="card-body text-center">
                            <i class="fas fa-laptop-code fa-3x text-info mb-3"></i>
                            <h5 class="card-title">Technology</h5>
                            <p class="card-text text-muted">Latest tech insights</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card category-card h-100">
                        <div class="card-body text-center">
                            <i class="fas fa-chart-line fa-3x text-warning mb-3"></i>
                            <h5 class="card-title">Business</h5>
                            <p class="card-text text-muted">Professional growth</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Featured Books Section -->
    <section class="py-5 bg-light">
        <div class="container">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2>Featured Books</h2>
                <a href="#" class="btn btn-outline-primary">View All</a>
            </div>
            <div class="row g-4">
                <!-- Featured Book 1 -->
                <div class="col-md-3">
                    <div class="card h-100 border-0 shadow-sm">
                        <div class="position-relative">
                            <img src="https://images.unsplash.com/photo-1544947950-fa07a98d237f" class="card-img-top" alt="Book Cover" style="height: 300px; object-fit: cover;">
                            <span class="position-absolute top-0 end-0 badge bg-danger m-2">20% OFF</span>
                        </div>
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span class="badge bg-warning">Best Seller</span>
                                <div class="text-warning">
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star-half-alt"></i>
                                    <span class="text-muted ms-1">(4.5)</span>
                                </div>
                            </div>
                            <h5 class="card-title">The Art of Programming</h5>
                            <p class="card-text text-muted mb-1">By John Smith</p>
                            <div class="d-flex justify-content-between align-items-center mt-3">
                                <div>
                                    <span class="text-danger fs-5 fw-bold">$24.99</span>
                                    <span class="text-muted text-decoration-line-through ms-2">$29.99</span>
                                </div>
                                <button class="btn btn-primary btn-sm"><i class="fas fa-cart-plus"></i> Add to Cart</button>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Featured Book 2 -->
                <div class="col-md-3">
                    <div class="card h-100 border-0 shadow-sm">
                        <div class="position-relative">
                            <img src="https://images.unsplash.com/photo-1589998059171-988d887df646" class="card-img-top" alt="Book Cover" style="height: 300px; object-fit: cover;">
                            <span class="position-absolute top-0 end-0 badge bg-success m-2">New</span>
                        </div>
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span class="badge bg-info">Science</span>
                                <div class="text-warning">
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="far fa-star"></i>
                                    <span class="text-muted ms-1">(4.0)</span>
                                </div>
                            </div>
                            <h5 class="card-title">Universe Explained</h5>
                            <p class="card-text text-muted mb-1">By Sarah Johnson</p>
                            <div class="d-flex justify-content-between align-items-center mt-3">
                                <div>
                                    <span class="text-danger fs-5 fw-bold">$19.99</span>
                                </div>
                                <button class="btn btn-primary btn-sm"><i class="fas fa-cart-plus"></i> Add to Cart</button>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Featured Book 3 -->
                <div class="col-md-3">
                    <div class="card h-100 border-0 shadow-sm">
                        <div class="position-relative">
                            <img src="https://images.unsplash.com/photo-1543002588-bfa74002ed7e" class="card-img-top" alt="Book Cover" style="height: 300px; object-fit: cover;">
                            <span class="position-absolute top-0 end-0 badge bg-danger m-2">Limited</span>
                        </div>
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span class="badge bg-success">Business</span>
                                <div class="text-warning">
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <span class="text-muted ms-1">(5.0)</span>
                                </div>
                            </div>
                            <h5 class="card-title">Success Principles</h5>
                            <p class="card-text text-muted mb-1">By Michael Brown</p>
                            <div class="d-flex justify-content-between align-items-center mt-3">
                                <div>
                                    <span class="text-danger fs-5 fw-bold">$34.99</span>
                                </div>
                                <button class="btn btn-primary btn-sm"><i class="fas fa-cart-plus"></i> Add to Cart</button>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Featured Book 4 -->
                <div class="col-md-3">
                    <div class="card h-100 border-0 shadow-sm">
                        <div class="position-relative">
                            <img src="https://images.unsplash.com/photo-1576872381149-7847515ce5d8" class="card-img-top" alt="Book Cover" style="height: 300px; object-fit: cover;">
                        </div>
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span class="badge bg-primary">Fiction</span>
                                <div class="text-warning">
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="fas fa-star"></i>
                                    <i class="far fa-star"></i>
                                    <span class="text-muted ms-1">(4.0)</span>
                                </div>
                            </div>
                            <h5 class="card-title">The Last Chapter</h5>
                            <p class="card-text text-muted mb-1">By Emily White</p>
                            <div class="d-flex justify-content-between align-items-center mt-3">
                                <div>
                                    <span class="text-danger fs-5 fw-bold">$15.99</span>
                                </div>
                                <button class="btn btn-primary btn-sm"><i class="fas fa-cart-plus"></i> Add to Cart</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Flash Sale Banner -->
    <section class="py-4 bg-primary text-white">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h3 class="mb-0">
                        <i class="fas fa-bolt"></i> Flash Sale! 50% OFF on Selected Books
                    </h3>
                </div>
                <div class="col-md-4 text-md-end">
                    <a href="#" class="btn btn-outline-light">Shop Now</a>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
        <!-- Footer Top - Newsletter -->
        <div class="bg-primary text-white py-4">
            <div class="container">
                <div class="row align-items-center">
                    <div class="col-md-6">
                        <h4 class="mb-0">Subscribe to Our Newsletter</h4>
                        <p class="mb-0">Get the latest updates, deals & more</p>
                    </div>
                    <div class="col-md-6">
                        <form class="d-flex gap-2">
                            <input type="email" class="form-control" placeholder="Enter your email address" required>
                            <button type="submit" class="btn btn-light">Subscribe</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer Main Content -->
        <div class="bg-dark text-white py-5">
            <div class="container">
                <div class="row g-4">
                    <!-- Company Info -->
                    <div class="col-lg-4 col-md-6">
                        <h5 class="mb-4 text-primary">
                            <i class="fas fa-book-reader"></i> BookNest
                        </h5>
                        <p class="text-white-50 mb-4">Your one-stop destination for all types of books. Discover millions of eBooks, audiobooks, and more at the best prices.</p>
                        <div class="d-flex gap-3 mb-4">
                            <a href="#" class="btn btn-outline-light btn-sm rounded-circle">
                                <i class="fab fa-facebook-f"></i>
                            </a>
                            <a href="#" class="btn btn-outline-light btn-sm rounded-circle">
                                <i class="fab fa-twitter"></i>
                            </a>
                            <a href="#" class="btn btn-outline-light btn-sm rounded-circle">
                                <i class="fab fa-instagram"></i>
                            </a>
                            <a href="#" class="btn btn-outline-light btn-sm rounded-circle">
                                <i class="fab fa-linkedin-in"></i>
                            </a>
                        </div>
                        <div class="d-flex gap-2 flex-wrap">
                            <img src="https://raw.githubusercontent.com/gauravghongde/social-icons/master/PNG/White/Visa_white.png" alt="Visa" height="30">
                            <img src="https://raw.githubusercontent.com/gauravghongde/social-icons/master/PNG/White/Mastercard_white.png" alt="Mastercard" height="30">
                            <img src="https://raw.githubusercontent.com/gauravghongde/social-icons/master/PNG/White/PayPal_white.png" alt="PayPal" height="30">
                            <img src="https://raw.githubusercontent.com/gauravghongde/social-icons/master/PNG/White/Apple_Pay_white.png" alt="Apple Pay" height="30">
                        </div>
                    </div>

                    <!-- Quick Links -->
                    <div class="col-lg-2 col-md-6">
                        <h5 class="text-white mb-4">Quick Links</h5>
                        <ul class="list-unstyled footer-links">
                            <li class="mb-2">
                                <a href="#" class="text-white-50 text-decoration-none hover-white">
                                    <i class="fas fa-chevron-right me-2 small"></i>About Us
                                </a>
                            </li>
                            <li class="mb-2">
                                <a href="#" class="text-white-50 text-decoration-none hover-white">
                                    <i class="fas fa-chevron-right me-2 small"></i>Contact Us
                                </a>
                            </li>
                            <li class="mb-2">
                                <a href="#" class="text-white-50 text-decoration-none hover-white">
                                    <i class="fas fa-chevron-right me-2 small"></i>Terms & Conditions
                                </a>
                            </li>
                            <li class="mb-2">
                                <a href="#" class="text-white-50 text-decoration-none hover-white">
                                    <i class="fas fa-chevron-right me-2 small"></i>Privacy Policy
                                </a>
                            </li>
                            <li class="mb-2">
                                <a href="#" class="text-white-50 text-decoration-none hover-white">
                                    <i class="fas fa-chevron-right me-2 small"></i>FAQs
                                </a>
                            </li>
                        </ul>
                    </div>

                    <!-- Customer Service -->
                    <div class="col-lg-3 col-md-6">
                        <h5 class="text-white mb-4">Customer Service</h5>
                        <ul class="list-unstyled footer-links">
                            <li class="mb-2">
                                <a href="#" class="text-white-50 text-decoration-none hover-white">
                                    <i class="fas fa-chevron-right me-2 small"></i>My Account
                                </a>
                            </li>
                            <li class="mb-2">
                                <a href="#" class="text-white-50 text-decoration-none hover-white">
                                    <i class="fas fa-chevron-right me-2 small"></i>Order Tracking
                                </a>
                            </li>
                            <li class="mb-2">
                                <a href="#" class="text-white-50 text-decoration-none hover-white">
                                    <i class="fas fa-chevron-right me-2 small"></i>Wishlist
                                </a>
                            </li>
                            <li class="mb-2">
                                <a href="#" class="text-white-50 text-decoration-none hover-white">
                                    <i class="fas fa-chevron-right me-2 small"></i>Returns & Refunds
                                </a>
                            </li>
                            <li class="mb-2">
                                <a href="#" class="text-white-50 text-decoration-none hover-white">
                                    <i class="fas fa-chevron-right me-2 small"></i>Shipping Information
                                </a>
                            </li>
                        </ul>
                    </div>

                    <!-- Contact Info -->
                    <div class="col-lg-3 col-md-6">
                        <h5 class="text-white mb-4">Contact Info</h5>
                        <ul class="list-unstyled footer-links">
                            <li class="d-flex mb-3">
                                <i class="fas fa-map-marker-alt text-primary me-3 mt-1"></i>
                                <span class="text-white-50">123 Book Street, Library District, Reading City, 12345</span>
                            </li>
                            <li class="d-flex mb-3">
                                <i class="fas fa-phone-alt text-primary me-3 mt-1"></i>
                                <span class="text-white-50">+1 (555) 123-4567</span>
                            </li>
                            <li class="d-flex mb-3">
                                <i class="fas fa-envelope text-primary me-3 mt-1"></i>
                                <span class="text-white-50">support@booknest.com</span>
                            </li>
                            <li class="d-flex mb-3">
                                <i class="fas fa-clock text-primary me-3 mt-1"></i>
                                <span class="text-white-50">Monday - Friday: 9:00 AM - 8:00 PM<br>Saturday & Sunday: 10:00 AM - 6:00 PM</span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer Bottom - Copyright -->
        <div class="bg-darker text-white-50 py-3">
            <div class="container">
                <div class="row align-items-center">
                    <div class="col-md-6 text-center text-md-start">
                        <p class="mb-md-0">&copy; 2025 BookNest. All rights reserved.</p>
                    </div>
                    <div class="col-md-6 text-center text-md-end">
                        <p class="mb-0">Designed with <i class="fas fa-heart text-danger"></i> for book lovers</p>
                    </div>
                </div>
            </div>
        </div>
    </footer>

    <!-- Add these styles to your existing style section -->
    <style>
        .bg-darker {
            background-color: #1a1a1a;
        }
        .footer-links a {
            transition: all 0.3s ease;
        }
        .footer-links a:hover {
            color: #fff !important;
            text-decoration: none;
            padding-left: 5px;
        }
        .btn-outline-light.rounded-circle {
            width: 36px;
            height: 36px;
            padding: 7px 0;
            text-align: center;
            transition: all 0.3s ease;
        }
        .btn-outline-light.rounded-circle:hover {
            transform: translateY(-3px);
        }
    </style>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
