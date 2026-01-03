<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bookstore.models.User" %>
<%@ page import="com.bookstore.models.BuyerDetails" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    BuyerDetails existingDetails = (BuyerDetails) request.getAttribute("buyerDetails");
    Double cartTotal = (Double) session.getAttribute("cartTotal");
    Double discount = (Double) session.getAttribute("discount");
    if (discount == null) discount = 0.0;
    double finalTotal = (cartTotal != null ? cartTotal : 0.0) - discount;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Buyer Details - BookNest</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px 0;
        }
        .checkout-container {
            max-width: 1200px;
            margin: 0 auto;
        }
        .card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.1);
        }
        .step-indicator {
            display: flex;
            justify-content: space-between;
            margin-bottom: 30px;
        }
        .step {
            flex: 1;
            text-align: center;
            position: relative;
        }
        .step::before {
            content: '';
            position: absolute;
            top: 25px;
            left: 50%;
            width: 100%;
            height: 2px;
            background: #dee2e6;
            z-index: -1;
        }
        .step:last-child::before {
            display: none;
        }
        .step-number {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: #dee2e6;
            color: #6c757d;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            margin-bottom: 10px;
        }
        .step.active .step-number {
            background: #667eea;
            color: white;
        }
        .step.completed .step-number {
            background: #28a745;
            color: white;
        }
        .form-label {
            font-weight: 500;
            color: #495057;
        }
        .required::after {
            content: '*';
            color: #dc3545;
            margin-left: 4px;
        }
    </style>
</head>
<body>
    <!-- Progress Steps -->
    <div class="checkout-container">
        <div class="step-indicator mb-4">
            <div class="step completed">
                <div class="step-number"><i class="fas fa-check"></i></div>
                <div>Cart</div>
            </div>
            <div class="step active">
                <div class="step-number">2</div>
                <div>Details</div>
            </div>
            <div class="step">
                <div class="step-number">3</div>
                <div>Payment</div>
            </div>
            <div class="step">
                <div class="step-number">4</div>
                <div>Confirmation</div>
            </div>
        </div>

        <div class="row">
            <!-- Buyer Details Form -->
            <div class="col-lg-8">
                <div class="card">
                    <div class="card-body p-4">
                        <h4 class="card-title mb-4">
                            <i class="fas fa-user-circle text-primary"></i> Delivery Information
                        </h4>
                        
                        <% if (request.getAttribute("errorMessage") != null) { %>
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <%= request.getAttribute("errorMessage") %>
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        <% } %>

                        <form method="post" onsubmit="return validateForm()">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="fullName" class="form-label required">Full Name</label>
                                    <input type="text" 
                                           class="form-control" 
                                           id="fullName" 
                                           name="fullName" 
                                           value="<%= existingDetails != null ? existingDetails.getFullName() : (user.getFirstName() + " " + user.getLastName()).trim() %>"
                                           required>
                                    <div class="invalid-feedback">Please enter your full name.</div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="email" class="form-label required">Email Address</label>
                                    <input type="email" 
                                           class="form-control" 
                                           id="email" 
                                           name="email" 
                                           value="<%= existingDetails != null ? existingDetails.getEmail() : user.getEmail() %>"
                                           required>
                                    <div class="invalid-feedback">Please enter a valid email.</div>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="phoneNumber" class="form-label required">Phone Number</label>
                                <input type="tel" 
                                       class="form-control" 
                                       id="phoneNumber" 
                                       name="phoneNumber" 
                                       value="<%= existingDetails != null ? existingDetails.getPhoneNumber() : (user.getPhoneNumber() != null ? user.getPhoneNumber() : "") %>"
                                       pattern="[0-9]{10,15}"
                                       required>
                                <div class="invalid-feedback">Please enter a valid phone number (10-15 digits).</div>
                            </div>

                            <div class="mb-3">
                                <label for="shippingAddress" class="form-label required">Shipping Address</label>
                                <textarea class="form-control" 
                                          id="shippingAddress" 
                                          name="shippingAddress" 
                                          rows="3" 
                                          required><%= existingDetails != null ? existingDetails.getShippingAddress() : (user.getAddress() != null ? user.getAddress() : "") %></textarea>
                                <div class="invalid-feedback">Please enter your shipping address.</div>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="city" class="form-label required">City</label>
                                    <input type="text" 
                                           class="form-control" 
                                           id="city" 
                                           name="city" 
                                           value="<%= existingDetails != null ? existingDetails.getCity() : "" %>"
                                           required>
                                    <div class="invalid-feedback">Please enter your city.</div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="postalCode" class="form-label required">Postal Code</label>
                                    <input type="text" 
                                           class="form-control" 
                                           id="postalCode" 
                                           name="postalCode" 
                                           value="<%= existingDetails != null ? existingDetails.getPostalCode() : "" %>"
                                           pattern="[0-9]{5,10}"
                                           required>
                                    <div class="invalid-feedback">Please enter a valid postal code.</div>
                                </div>
                            </div>

                            <div class="d-flex justify-content-between mt-4">
                                <a href="${pageContext.request.contextPath}/buyer/cart" class="btn btn-outline-secondary">
                                    <i class="fas fa-arrow-left"></i> Back to Cart
                                </a>
                                <button type="submit" class="btn btn-primary btn-lg px-5">
                                    Continue to Payment <i class="fas fa-arrow-right"></i>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Order Summary -->
            <div class="col-lg-4">
                <div class="card sticky-top" style="top: 20px;">
                    <div class="card-body">
                        <h5 class="card-title mb-4">
                            <i class="fas fa-shopping-bag"></i> Order Summary
                        </h5>
                        
                        <% if (cartTotal != null) { %>
                        <div class="d-flex justify-content-between mb-3">
                            <span>Subtotal</span>
                            <span class="fw-bold">$<%=String.format("%.2f", cartTotal)%></span>
                        </div>
                        
                        <% if (discount > 0) { %>
                        <div class="d-flex justify-content-between mb-3 text-success">
                            <span><i class="fas fa-tag"></i> Discount</span>
                            <span>-$<%=String.format("%.2f", discount)%></span>
                        </div>
                        <% } %>
                        
                        <div class="d-flex justify-content-between mb-3">
                            <span>Shipping</span>
                            <span class="text-success">Free</span>
                        </div>
                        
                        <hr>
                        
                        <div class="d-flex justify-content-between mb-3">
                            <strong>Total</strong>
                            <strong class="text-primary h5">$<%=String.format("%.2f", finalTotal)%></strong>
                        </div>
                        <% } %>
                        
                        <div class="alert alert-info mt-3">
                            <small>
                                <i class="fas fa-shield-alt"></i> 
                                Your information is secure and encrypted
                            </small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function validateForm() {
            const form = document.querySelector('form');
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
            return form.checkValidity();
        }
    </script>
</body>
</html>

