<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - BookNest</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background-color: #f8f9fa;
        }
        .register-container {
            max-width: 600px;
            margin: 40px auto;
        }
        .card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
        }
        .card-header {
            background: linear-gradient(135deg, #28a745, #1e7e34);
            color: white;
            text-align: center;
            border-radius: 15px 15px 0 0 !important;
            padding: 30px;
        }
        .form-control {
            border-radius: 10px;
            padding: 12px;
        }
        .btn-register {
            border-radius: 10px;
            padding: 12px;
            font-weight: 600;
        }
        .card-footer {
            background: none;
            border-top: none;
        }
        .brand-icon {
            font-size: 3rem;
            margin-bottom: 1rem;
        }
        .form-check-input:checked {
            background-color: #28a745;
            border-color: #28a745;
        }
    </style>
</head>
<body>
    <div class="container register-container">
        <div class="card">
            <div class="card-header">
                <i class="fas fa-user-plus brand-icon"></i>
                <h3 class="mb-0">Create an Account</h3>
                <p class="mb-0">Join BookNest today</p>
            </div>
            <div class="card-body p-4">
                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger" role="alert">
                        ${error}
                    </div>
                <% } %>

                <form action="register" method="post" id="registerForm">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="firstName" class="form-label">First Name</label>
                            <input type="text" class="form-control" id="firstName" name="firstName" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="lastName" class="form-label">Last Name</label>
                            <input type="text" class="form-control" id="lastName" name="lastName" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-user"></i>
                            </span>
                            <input type="text" class="form-control" id="username" name="username" required
                                   pattern="[a-zA-Z0-9]{5,20}" 
                                   title="Username must be 5-20 characters long and can only contain letters and numbers">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="email" class="form-label">Email Address</label>
                        <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-envelope"></i>
                            </span>
                            <input type="email" class="form-control" id="email" name="email" required>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="password" class="form-label">Password</label>
                            <div class="input-group">
                                <span class="input-group-text">
                                    <i class="fas fa-lock"></i>
                                </span>
                                <input type="password" class="form-control" id="password" name="password" 
                                       required minlength="6">
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="confirmPassword" class="form-label">Confirm Password</label>
                            <div class="input-group">
                                <span class="input-group-text">
                                    <i class="fas fa-lock"></i>
                                </span>
                                <input type="password" class="form-control" id="confirmPassword" 
                                       required minlength="6">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="phoneNumber" class="form-label">Phone Number</label>
                        <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-phone"></i>
                            </span>
                            <input type="tel" class="form-control" id="phoneNumber" name="phoneNumber" 
                                   pattern="[0-9]{10}" title="Please enter a valid 10-digit phone number" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="address" class="form-label">Address</label>
                        <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-map-marker-alt"></i>
                            </span>
                            <textarea class="form-control" id="address" name="address" rows="2" 
                                      required></textarea>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label d-block">Account Type</label>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" name="role" id="roleBuyer" 
                                   value="BUYER" checked required>
                            <label class="form-check-label" for="roleBuyer">
                                <i class="fas fa-shopping-cart me-1"></i> Buyer
                            </label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" name="role" id="roleSeller" 
                                   value="SELLER" required>
                            <label class="form-check-label" for="roleSeller">
                                <i class="fas fa-store me-1"></i> Seller
                            </label>
                        </div>
                    </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-success btn-register">
                            <i class="fas fa-user-plus me-2"></i> Create Account
                        </button>
                    </div>
                </form>
            </div>
            <div class="card-footer text-center p-4">
                <p class="mb-0">Already have an account? 
                    <a href="${pageContext.request.contextPath}/login" class="text-success fw-bold">Login</a>
                </p>
            </div>
        </div>
        <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/" class="text-muted">
                <i class="fas fa-home me-1"></i> Back to Home
            </a>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Form Validation Script -->
    <script>
        document.getElementById('registerForm').addEventListener('submit', function(event) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password !== confirmPassword) {
                event.preventDefault();
                alert('Passwords do not match!');
            }
        });
    </script>
</body>
</html>