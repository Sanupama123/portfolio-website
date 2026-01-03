<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - Real Estate Management System</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css">
    <link rel="stylesheet" href="css/style.css">
    <style>
        .auth-container {
            min-height: calc(100vh - 200px);
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: #f8f9fa;
            padding: 40px 0;
        }
        
        .auth-card {
            background-color: white;
            border-radius: 12px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 500px;
            overflow: hidden;
            display: flex;
            flex-direction: column;
        }
        
        .auth-header {
            background-color: var(--primary-color);
            padding: 30px;
            text-align: center;
            color: white;
        }
        
        .auth-header h2 {
            margin: 0;
            font-size: 28px;
            font-weight: 500;
        }
        
        .auth-header p {
            margin: 10px 0 0;
            opacity: 0.9;
            font-size: 15px;
        }
        
        .auth-body {
            padding: 35px 30px;
        }
        
        .form-group {
            margin-bottom: 20px;
            position: relative;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: #4a4a4a;
            font-size: 14px;
        }
        
        .form-control {
            width: 100%;
            padding: 14px 16px;
            font-size: 15px;
            border: 1px solid #e0e0e0;
            border-radius: 6px;
            transition: all 0.3s ease;
            background-color: #f9f9f9;
        }
        
        .form-control:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(26, 115, 232, 0.15);
            background-color: #fff;
        }
        
        .input-icon {
            position: absolute;
            top: 42px;
            left: 14px;
            color: #888;
            font-size: 18px;
        }
        
        .icon-input {
            padding-left: 45px;
        }
        
        .btn-block {
            width: 100%;
            padding: 14px;
            font-size: 16px;
            font-weight: 500;
        }
        
        .radio-group {
            display: flex;
            gap: 20px;
            margin-top: 10px;
        }
        
        .radio-group label {
            display: flex;
            align-items: center;
            cursor: pointer;
            font-weight: 400;
        }
        
        .radio-group input {
            margin-right: 8px;
        }
        
        .auth-footer {
            padding: 20px 30px;
            text-align: center;
            border-top: 1px solid #eee;
            background-color: #f9f9f9;
        }
        
        .auth-footer a {
            color: var(--primary-color);
            font-weight: 500;
            text-decoration: none;
        }
        
        .auth-footer a:hover {
            text-decoration: underline;
        }
        
        .form-row {
            display: flex;
            gap: 20px;
        }
        
        .form-row .form-group {
            flex: 1;
        }
        
        .terms-checkbox {
            display: flex;
            align-items: flex-start;
            margin: 15px 0 25px;
        }
        
        .terms-checkbox input {
            margin: 3px 10px 0 0;
        }
        
        .terms-checkbox label {
            font-size: 14px;
            color: #666;
            line-height: 1.5;
        }
        
        .terms-checkbox a {
            color: var(--primary-color);
            text-decoration: none;
        }
        
        .terms-checkbox a:hover {
            text-decoration: underline;
        }
        
        .register-benefits {
            background-color: #f0f7ff;
            padding: 15px;
            border-radius: 8px;
            margin-top: 20px;
        }
        
        .register-benefits h4 {
            font-size: 16px;
            margin: 0 0 10px;
            color: var(--primary-dark);
        }
        
        .benefits-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        
        .benefits-list li {
            display: flex;
            align-items: center;
            margin-bottom: 8px;
            font-size: 14px;
            color: #555;
        }
        
        .benefits-list li i {
            color: var(--success-color);
            margin-right: 8px;
            font-size: 14px;
        }
        
        @media (max-width: 576px) {
            .form-row {
                flex-direction: column;
                gap: 10px;
            }
            
            .auth-card {
                margin: 0 15px;
            }
        }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <nav class="navbar">
                <a href="index.jsp" class="logo">Real Estate</a>
                
                <ul class="nav-links">
                    <li><a href="index.jsp">Home</a></li>
                    <li><a href="properties.jsp">Properties</a></li>
                    <li><a href="about.jsp">About</a></li>
                    <li><a href="contact.jsp">Contact</a></li>
                </ul>
                
                <div class="user-actions">
                    <a href="login.jsp" class="btn btn-outline">Login</a>
                    <a href="register.jsp" class="btn btn-primary">Register</a>
                </div>
                
                <button class="menu-toggle">
                    <i class="fas fa-bars"></i>
                </button>
            </nav>
        </div>
    </header>
    
    <main>
        <div class="auth-container">
            <div class="auth-card">
                <div class="auth-header">
                    <h2>Create an Account</h2>
                    <p>Join us to find your dream property</p>
                </div>
                
                <div class="auth-body">
                    <% if (request.getAttribute("error") != null) { %>
                        <div class="error-message" style="margin-bottom: 20px; text-align: center;">
                            <%= request.getAttribute("error") %>
                        </div>
                    <% } %>
                    
                    <form id="register-form" action="register" method="post">
                        <div class="form-group">
                            <label for="fullName">Full Name</label>
                            <i class="fas fa-user input-icon"></i>
                            <input type="text" id="fullName" name="fullName" class="form-control icon-input" placeholder="Enter your full name" required>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="email">Email Address</label>
                                <i class="fas fa-envelope input-icon"></i>
                                <input type="email" id="email" name="email" class="form-control icon-input" placeholder="Enter your email" required>
                            </div>
                            
                            <div class="form-group">
                                <label for="phoneNumber">Phone Number</label>
                                <i class="fas fa-phone input-icon"></i>
                                <input type="tel" id="phoneNumber" name="phoneNumber" class="form-control icon-input" placeholder="Enter your phone number" required>
                            </div>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="password">Password</label>
                                <i class="fas fa-lock input-icon"></i>
                                <input type="password" id="password" name="password" class="form-control icon-input" placeholder="Create a password" required>
                            </div>
                            
                            <div class="form-group">
                                <label for="confirm-password">Confirm Password</label>
                                <i class="fas fa-lock input-icon"></i>
                                <input type="password" id="confirm-password" name="confirmPassword" class="form-control icon-input" placeholder="Confirm your password" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label>Register As:</label>
                            <div class="radio-group">
                                <label>
                                    <input type="radio" name="userType" value="buyer" checked> Buyer
                                </label>
                                <label>
                                    <input type="radio" name="userType" value="seller"> Seller
                                </label>
                            </div>
                        </div>
                        
                        <div class="terms-checkbox">
                            <input type="checkbox" id="terms" name="terms" required>
                            <label for="terms">I agree to the <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a></label>
                        </div>
                        
                        <div class="form-group" style="margin-bottom: 0;">
                            <button type="submit" class="btn btn-primary btn-block">Create Account</button>
                        </div>
                        
                        <div class="register-benefits">
                            <h4>Benefits of joining our platform:</h4>
                            <ul class="benefits-list">
                                <li><i class="fas fa-check-circle"></i> Access to exclusive property listings</li>
                                <li><i class="fas fa-check-circle"></i> Save favorite properties for later</li>
                                <li><i class="fas fa-check-circle"></i> Receive personalized property recommendations</li>
                                <li><i class="fas fa-check-circle"></i> Connect directly with sellers or buyers</li>
                            </ul>
                        </div>
                    </form>
                </div>
                
                <div class="auth-footer">
                    <p>Already have an account? <a href="login.jsp">Sign In</a></p>
                </div>
            </div>
        </div>
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
                        <li><a href="contact.jsp">Contact Us</a></li>
                    </ul>
                </div>
                
                <div class="footer-section">
                    <h3>Contact Us</h3>
                    <ul>
                        <li><i class="fas fa-map-marker-alt"></i> 123 Main St, City</li>
                        <li><i class="fas fa-phone"></i> (123) 456-7890</li>
                        <li><i class="fas fa-envelope"></i> info@realestate.com</li>
                    </ul>
                </div>
            </div>
            
            <div class="footer-bottom">
                <p>&copy; 2023 Real Estate Management System. All rights reserved.</p>
            </div>
        </div>
    </footer>
    
    <script src="js/main.js"></script>
    <script>
        // Password matching validation
        document.getElementById('register-form').addEventListener('submit', function(e) {
            var password = document.getElementById('password').value;
            var confirmPassword = document.getElementById('confirm-password').value;
            
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Passwords do not match');
            }
        });
    </script>
</body>
</html> 