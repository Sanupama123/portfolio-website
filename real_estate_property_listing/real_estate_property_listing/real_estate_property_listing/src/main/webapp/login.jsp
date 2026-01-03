<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Real Estate Management System</title>
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
            max-width: 450px;
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
            margin-bottom: 24px;
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
        
        .or-divider {
            display: flex;
            align-items: center;
            margin: 25px 0;
            color: #777;
        }
        
        .or-divider::before,
        .or-divider::after {
            content: "";
            flex: 1;
            height: 1px;
            background-color: #e0e0e0;
        }
        
        .or-divider span {
            padding: 0 15px;
            font-size: 14px;
            font-weight: 500;
        }
        
        .social-login {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin-bottom: 20px;
        }
        
        .social-btn {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 50px;
            height: 50px;
            border-radius: 50%;
            border: 1px solid #e0e0e0;
            background-color: white;
            font-size: 22px;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .social-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }
        
        .facebook {
            color: #3b5998;
        }
        
        .google {
            color: #db4437;
        }
        
        .twitter {
            color: #1da1f2;
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
                    <i class="fa fa-bars"></i>
                </button>
            </nav>
        </div>
    </header>
    
    <main>
        <div class="auth-container">
            <div class="auth-card">
                <div class="auth-header">
                    <h2>Welcome Back</h2>
                    <p>Sign in to access your account</p>
                </div>
                
                <div class="auth-body">
                    <% if (request.getAttribute("error") != null) { %>
                        <div class="error-message" style="margin-bottom: 20px; text-align: center;">
                            <%= request.getAttribute("error") %>
                        </div>
                    <% } %>
                    
                    <div class="social-login">
                        <button class="social-btn facebook" title="Login with Facebook">
                            <i class="fab fa-facebook-f"></i>
                        </button>
                        <button class="social-btn google" title="Login with Google">
                            <i class="fab fa-google"></i>
                        </button>
                        <button class="social-btn twitter" title="Login with Twitter">
                            <i class="fab fa-twitter"></i>
                        </button>
                    </div>
                    
                    <div class="or-divider">
                        <span>or login with email</span>
                    </div>
                    
                    <form id="login-form" action="login" method="post">
                        <div class="form-group">
                            <label for="email">Email Address</label>
                            <i class="fas fa-envelope input-icon"></i>
                            <input type="email" id="email" name="email" class="form-control icon-input" placeholder="Enter your email" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="password">Password</label>
                            <i class="fas fa-lock input-icon"></i>
                            <input type="password" id="password" name="password" class="form-control icon-input" placeholder="Enter your password" required>
                        </div>
                        
                        <div class="form-group">
                            <label>Login As:</label>
                            <div class="radio-group">
                                <label>
                                    <input type="radio" name="userType" value="buyer" checked> Buyer
                                </label>
                                <label>
                                    <input type="radio" name="userType" value="seller"> Seller
                                </label>
                                <label>
                                    <input type="radio" name="userType" value="admin"> Admin
                                </label>
                            </div>
                        </div>
                        
                        <div class="form-group" style="margin-bottom: 5px;">
                            <button type="submit" class="btn btn-primary btn-block">Sign In</button>
                        </div>
                    </form>
                </div>
                
                <div class="auth-footer">
                    <p>Don't have an account? <a href="register.jsp">Create Account</a></p>
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
</body>
</html> 