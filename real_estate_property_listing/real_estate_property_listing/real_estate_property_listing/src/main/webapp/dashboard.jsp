<%@ page import="com.realestate.model.User" %>
<%@ page import="com.realestate.util.Constants" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Check if user is logged in
    User user = (User) session.getAttribute(Constants.SESSION_USER);
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    boolean isAdmin = false;
    boolean isSeller = false;
    boolean isBuyer = false;
    
    try {
        // Check user roles
        isAdmin = (boolean) user.getClass().getMethod("isAdmin").invoke(user);
    } catch (Exception e) {
        // Handle reflection error or if the method doesn't exist
    }
    
    try {
        // Check if user is a seller
        isSeller = user.getClass().getSimpleName().equals("Seller");
    } catch (Exception e) {
        // Handle error
    }
    
    try {
        // Check if user is a buyer
        isBuyer = user.getClass().getSimpleName().equals("Buyer");
    } catch (Exception e) {
        // Handle error
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Real Estate</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="css/style.css">
    <style>
        .dashboard-container {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            margin: 30px 0;
        }
        .dashboard-card {
            flex: 1;
            min-width: 300px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }
        .dashboard-card h3 {
            color: var(--primary-dark);
            margin-top: 0;
            border-bottom: 1px solid var(--primary-light);
            padding-bottom: 10px;
            margin-bottom: 15px;
        }
        .dashboard-menu {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        .dashboard-menu li {
            margin-bottom: 10px;
        }
        .dashboard-menu li a {
            display: flex;
            align-items: center;
            padding: 10px;
            color: var(--text-color);
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.3s;
        }
        .dashboard-menu li a:hover {
            background-color: var(--primary-light);
        }
        .dashboard-menu li a i {
            margin-right: 10px;
            color: var(--primary-color);
            width: 20px;
            text-align: center;
        }
        .user-info {
            background-color: var(--primary-light);
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        .user-info p {
            margin: 5px 0;
        }
        .user-info strong {
            color: var(--primary-dark);
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
                <h2 class="section-title">Dashboard</h2>
                
                <div class="dashboard-container">
                    <!-- User Info Card -->
                    <div class="dashboard-card">
                        <h3>User Information</h3>
                        <div class="user-info">
                            <p><strong>Name:</strong> <%= user.getFirstName() + " " + user.getLastName() %></p>
                            <p><strong>Email:</strong> <%= user.getEmail() %></p>
                            <p><strong>Role:</strong> 
                                <% if (isAdmin) { %>
                                    Administrator
                                <% } else if (isSeller) { %>
                                    Seller
                                <% } else if (isBuyer) { %>
                                    Buyer
                                <% } else { %>
                                    User
                                <% } %>
                            </p>
                        </div>
                        
                        <h3>Quick Links</h3>
                        <ul class="dashboard-menu">
                            <li>
                                <a href="<%= request.getContextPath() %>/properties">
                                    <i class="fa fa-home"></i> Browse Properties
                                </a>
                            </li>
                            <% if (isSeller || isAdmin) { %>
                            <li>
                                <a href="<%= request.getContextPath() %>/properties/add">
                                    <i class="fa fa-plus-circle"></i> Add New Property
                                </a>
                            </li>
                            <li>
                                <a href="<%= request.getContextPath() %>/properties/manage">
                                    <i class="fa fa-list"></i> <%= isAdmin ? "My Properties" : "Manage Properties" %>
                                </a>
                            </li>
                            <% } %>
                            <% if (isBuyer || isAdmin || isSeller) { %>
                            <li>
                                <a href="<%= request.getContextPath() %>/properties/favorites">
                                    <i class="fa fa-heart"></i> My Favorites
                                </a>
                            </li>
                            <% } %>
                            <li>
                                <a href="<%= request.getContextPath() %>/user/edit-profile">
                                    <i class="fa fa-user"></i> Edit Profile
                                </a>
                            </li>
                        </ul>
                    </div>
                    
                    <!-- Admin Tools Card (only visible to admins) -->
                    <% if (isAdmin) { %>
                    <div class="dashboard-card">
                        <h3>Admin Tools</h3>
                        <ul class="dashboard-menu">
                            <li>
                                <a href="users.jsp">
                                    <i class="fa fa-users"></i> Manage Users
                                </a>
                            </li>
                            <li>
                                <a href="add-admin.jsp">
                                    <i class="fa fa-user-plus"></i> Add New Admin
                                </a>
                            </li>
                            <li>
                                <a href="adminProperties.jsp">
                                    <i class="fa fa-building"></i> Manage Properties
                                </a>
                            </li>
                            <li>
                                <a href="data.jsp">
                                    <i class="fa fa-database"></i> Data Management
                                </a>
                            </li>
                            <li>
                                <a href="settings.jsp">
                                    <i class="fa fa-cogs"></i> System Settings
                                </a>
                            </li>
                            <li>
                                <a href="<%= request.getContextPath() %>/test">
                                    <i class="fa fa-wrench"></i> Servlet Test
                                </a>
                            </li>
                        </ul>
                    </div>
                    <% } %>
                    
                    <!-- Activity Card -->
                    <div class="dashboard-card">
                        <h3>Recent Activity</h3>
                        <p>Your recent activity will appear here.</p>
                        
                        <% if (isSeller) { %>
                        <h3>My Properties</h3>
                        <p>You haven't added any properties yet.</p>
                        <a href="<%= request.getContextPath() %>/properties/add" class="btn btn-primary">Add Property</a>
                        <% } else if (isBuyer) { %>
                        <h3>My Favorites</h3>
                        <p>You haven't added any properties to favorites yet.</p>
                        <a href="<%= request.getContextPath() %>/properties" class="btn btn-primary">Browse Properties</a>
                        <% } else if (isAdmin) { %>
                        <h3>Admin Actions</h3>
                        <p>Quick actions for administrators:</p>
                        <div style="margin-top: 10px;">
                            <a href="<%= request.getContextPath() %>/properties/add" class="btn btn-primary">Add Property</a>
                            <a href="<%= request.getContextPath() %>/properties/favorites" class="btn btn-outline">View Favorites</a>
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>
        </section>
    </main>
    
    <!-- Footer -->
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
                        <li><a href="<%= request.getContextPath() %>/index.jsp">Home</a></li>
                        <li><a href="<%= request.getContextPath() %>/properties">Properties</a></li>
                        <li><a href="<%= request.getContextPath() %>/about.jsp">About Us</a></li>
                        <li><a href="<%= request.getContextPath() %>/contact.jsp">Contact Us</a></li>
                    </ul>
                </div>
                
                <div class="footer-section">
                    <h3>Contact Us</h3>
                    <ul>
                        <li><i class="fa fa-map-marker"></i> 123 Main St, City</li>
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