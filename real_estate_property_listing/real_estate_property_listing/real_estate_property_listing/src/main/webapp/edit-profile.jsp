<%@ page import="com.realestate.model.User" %>
<%@ page import="com.realestate.model.Seller" %>
<%@ page import="com.realestate.model.Buyer" %>
<%@ page import="com.realestate.util.Constants" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Check if user is logged in
    User user = (User) session.getAttribute(Constants.SESSION_USER);
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
        return;
    }
    
    boolean isSeller = user instanceof Seller;
    boolean isBuyer = user instanceof Buyer;
    
    // Get error and success messages
    String error = request.getParameter("error");
    String success = request.getParameter("success");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Profile - Real Estate</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        .profile-form {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            max-width: 600px;
            margin: 0 auto;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-row {
            display: flex;
            flex-wrap: wrap;
            margin: 0 -10px;
        }
        
        .form-col {
            flex: 1;
            padding: 0 10px;
            min-width: 200px;
        }
        
        .form-actions {
            margin-top: 30px;
            text-align: right;
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
        
        .profile-heading {
            margin-bottom: 30px;
            padding-bottom: 15px;
            border-bottom: 1px solid #eee;
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
                    <% if (isSeller || user.isAdmin()) { %>
                        <a href="<%= request.getContextPath() %>/properties/manage" class="btn btn-outline"><i class="fas fa-home"></i> My Properties</a>
                    <% } %>
                    <a href="<%= request.getContextPath() %>/properties/favorites" class="btn btn-outline"><i class="fas fa-heart"></i> Favorites</a>
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
                <h2 class="section-title">Edit Profile</h2>
                
                <% if (success != null) { %>
                    <div class="status-message success">
                        <i class="fas fa-check-circle"></i> 
                        <% if ("profile_updated".equals(success)) { %>
                            Your profile has been successfully updated.
                        <% } else { %>
                            Operation completed successfully.
                        <% } %>
                    </div>
                <% } %>
                
                <% if (error != null) { %>
                    <div class="status-message error">
                        <i class="fas fa-exclamation-circle"></i> 
                        <% if ("email_exists".equals(error)) { %>
                            This email is already in use.
                        <% } else if ("invalid_input".equals(error)) { %>
                            Please check your input and try again.
                        <% } else { %>
                            An error occurred. Please try again.
                        <% } %>
                    </div>
                <% } %>
                
                <div class="profile-form">
                    <div class="profile-heading">
                        <h3>Personal Information</h3>
                        <p>Update your personal information below</p>
                    </div>
                    
                    <form action="<%= request.getContextPath() %>/user/update-profile" method="post">
                        <input type="hidden" name="userId" value="<%= user.getId() %>">
                        
                        <div class="form-row">
                            <div class="form-col">
                                <div class="form-group">
                                    <label for="firstName" class="form-label">First Name*</label>
                                    <input type="text" id="firstName" name="firstName" class="form-control" value="<%= user.getFirstName() %>" required>
                                </div>
                            </div>
                            <div class="form-col">
                                <div class="form-group">
                                    <label for="lastName" class="form-label">Last Name*</label>
                                    <input type="text" id="lastName" name="lastName" class="form-control" value="<%= user.getLastName() %>" required>
                                </div>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="email" class="form-label">Email*</label>
                            <input type="email" id="email" name="email" class="form-control" value="<%= user.getEmail() %>" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="phoneNumber" class="form-label">Phone Number*</label>
                            <input type="tel" id="phoneNumber" name="phoneNumber" class="form-control" value="<%= user.getPhoneNumber() %>" required>
                        </div>
                        
                        <% if (isSeller) { %>
                            <div class="form-group">
                                <label for="companyName" class="form-label">Company Name</label>
                                <input type="text" id="companyName" name="companyName" class="form-control" value="<%= ((Seller)user).getCompanyName() %>">
                            </div>
                        <% } %>
                        
                        <div class="form-group">
                            <label for="currentPassword" class="form-label">Current Password* (required to confirm changes)</label>
                            <input type="password" id="currentPassword" name="currentPassword" class="form-control" required>
                        </div>
                        
                        <div class="profile-heading">
                            <h3>Change Password</h3>
                            <p>Leave blank if you don't want to change your password</p>
                        </div>
                        
                        <div class="form-group">
                            <label for="newPassword" class="form-label">New Password</label>
                            <input type="password" id="newPassword" name="newPassword" class="form-control">
                        </div>
                        
                        <div class="form-group">
                            <label for="confirmPassword" class="form-label">Confirm New Password</label>
                            <input type="password" id="confirmPassword" name="confirmPassword" class="form-control">
                        </div>
                        
                        <div class="form-actions">
                            <a href="<%= request.getContextPath() %>/dashboard.jsp" class="btn btn-outline">Cancel</a>
                            <button type="submit" class="btn btn-primary">Update Profile</button>
                        </div>
                    </form>
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
        
        // Password confirmation validation
        document.querySelector('form').addEventListener('submit', function(event) {
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (newPassword && newPassword !== confirmPassword) {
                event.preventDefault();
                alert('The passwords do not match. Please try again.');
            }
        });
    </script>
</body>
</html> 