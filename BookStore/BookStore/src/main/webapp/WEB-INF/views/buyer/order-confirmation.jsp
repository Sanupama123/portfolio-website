<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bookstore.models.User" %>
<%@ page import="com.bookstore.models.Order" %>
<%@ page import="com.bookstore.models.OrderItem" %>
<%@ page import="com.bookstore.models.BuyerDetails" %>
<%@ page import="com.bookstore.models.Payment" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    Order order = (Order) request.getAttribute("order");
    BuyerDetails buyerDetails = (BuyerDetails) request.getAttribute("buyerDetails");
    Payment payment = (Payment) request.getAttribute("payment");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Confirmation - BookNest</title>
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
        .confirmation-container {
            max-width: 900px;
            margin: 0 auto;
        }
        .card {
            border: none;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
        }
        .success-icon {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            background: linear-gradient(135deg, #28a745, #20c997);
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 30px;
            animation: scaleIn 0.5s ease-out;
        }
        @keyframes scaleIn {
            from {
                transform: scale(0);
            }
            to {
                transform: scale(1);
            }
        }
        .success-icon i {
            font-size: 50px;
            color: white;
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
            background: #28a745;
            z-index: -1;
        }
        .step:last-child::before {
            display: none;
        }
        .step-number {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: #28a745;
            color: white;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            margin-bottom: 10px;
        }
        .info-section {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .order-item {
            border-bottom: 1px solid #e9ecef;
            padding: 15px 0;
        }
        .order-item:last-child {
            border-bottom: none;
        }
        .btn-primary {
            background: linear-gradient(135deg, #667eea, #764ba2);
            border: none;
            padding: 12px 30px;
        }
        .btn-outline-primary {
            border-color: #667eea;
            color: #667eea;
        }
        .btn-outline-primary:hover {
            background: #667eea;
            border-color: #667eea;
        }
    </style>
</head>
<body>
    <div class="confirmation-container">
        <!-- Progress Steps -->
        <div class="step-indicator mb-5">
            <div class="step">
                <div class="step-number"><i class="fas fa-check"></i></div>
                <div class="text-white small">Cart</div>
            </div>
            <div class="step">
                <div class="step-number"><i class="fas fa-check"></i></div>
                <div class="text-white small">Details</div>
            </div>
            <div class="step">
                <div class="step-number"><i class="fas fa-check"></i></div>
                <div class="text-white small">Payment</div>
            </div>
            <div class="step">
                <div class="step-number"><i class="fas fa-check"></i></div>
                <div class="text-white small">Confirmation</div>
            </div>
        </div>

        <!-- Success Card -->
        <div class="card">
            <div class="card-body p-5">
                <div class="success-icon">
                    <i class="fas fa-check"></i>
                </div>
                
                <div class="text-center mb-4">
                    <h2 class="text-success mb-2">Order Placed Successfully!</h2>
                    <p class="text-muted">Thank you for your purchase</p>
                    <h4 class="mb-0">Order #<%=order.getOrderNumber()%></h4>
                </div>

                <hr class="my-4">

                <!-- Order Summary -->
                <div class="row">
                    <div class="col-md-6">
                        <div class="info-section">
                            <h6 class="mb-3"><i class="fas fa-truck text-primary me-2"></i>Delivery Information</h6>
                            <% if (buyerDetails != null) { %>
                                <p class="mb-1"><strong><%=buyerDetails.getFullName()%></strong></p>
                                <p class="mb-1 small text-muted"><%=buyerDetails.getEmail()%></p>
                                <p class="mb-1 small text-muted"><%=buyerDetails.getPhoneNumber()%></p>
                                <p class="mb-0 small text-muted">
                                    <%=buyerDetails.getShippingAddress()%><br>
                                    <%=buyerDetails.getCity()%>, <%=buyerDetails.getPostalCode()%>
                                </p>
                            <% } else { %>
                                <p class="mb-0 small text-muted"><%=order.getShippingAddress()%></p>
                            <% } %>
                        </div>
                    </div>
                    
                    <div class="col-md-6">
                        <div class="info-section">
                            <h6 class="mb-3"><i class="fas fa-credit-card text-primary me-2"></i>Payment Information</h6>
                            <% if (payment != null) { %>
                                <p class="mb-1">
                                    <strong>Method:</strong> 
                                    <% if ("CARD".equals(payment.getPaymentMethod())) { %>
                                        <i class="fas fa-credit-card"></i> Card
                                    <% } else if ("UPI".equals(payment.getPaymentMethod())) { %>
                                        <i class="fas fa-mobile-alt"></i> UPI
                                    <% } else if ("COD".equals(payment.getPaymentMethod())) { %>
                                        <i class="fas fa-money-bill-wave"></i> Cash on Delivery
                                    <% } %>
                                </p>
                                <p class="mb-1">
                                    <strong>Status:</strong> 
                                    <span class="badge bg-<%="COMPLETED".equals(payment.getPaymentStatus()) ? "success" : "warning"%>">
                                        <%=payment.getPaymentStatus()%>
                                    </span>
                                </p>
                                <% if (payment.getTransactionId() != null) { %>
                                    <p class="mb-0 small text-muted">Transaction ID: <%=payment.getTransactionId()%></p>
                                <% } %>
                            <% } %>
                        </div>
                    </div>
                </div>

                <!-- Order Items -->
                <div class="info-section">
                    <h6 class="mb-3"><i class="fas fa-box text-primary me-2"></i>Order Items</h6>
                    <% if (order.getItems() != null && !order.getItems().isEmpty()) { %>
                        <% for (OrderItem item : order.getItems()) { %>
                            <div class="order-item d-flex justify-content-between align-items-center">
                                <div>
                                    <p class="mb-0"><strong><%=item.getTitle()%></strong></p>
                                    <small class="text-muted">Quantity: <%=item.getQuantity()%> × $<%=String.format("%.2f", item.getPrice())%></small>
                                </div>
                                <div>
                                    <strong>$<%=String.format("%.2f", item.getSubtotal())%></strong>
                                </div>
                            </div>
                        <% } %>
                    <% } %>
                </div>

                <!-- Price Breakdown -->
                <div class="info-section">
                    <div class="d-flex justify-content-between mb-2">
                        <span>Subtotal</span>
                        <span>$<%=String.format("%.2f", order.getSubtotal())%></span>
                    </div>
                    <% if (order.getDiscount() > 0) { %>
                        <div class="d-flex justify-content-between mb-2 text-success">
                            <span>
                                <i class="fas fa-tag"></i> Discount
                                <% if (order.getCouponCode() != null) { %>
                                    (<%=order.getCouponCode()%>)
                                <% } %>
                            </span>
                            <span>-$<%=String.format("%.2f", order.getDiscount())%></span>
                        </div>
                    <% } %>
                    <div class="d-flex justify-content-between mb-2">
                        <span>Shipping</span>
                        <span class="text-success"><%=order.getShipping() == 0 ? "Free" : "$" + String.format("%.2f", order.getShipping())%></span>
                    </div>
                    <hr>
                    <div class="d-flex justify-content-between">
                        <strong class="h5">Total Paid</strong>
                        <strong class="h4 text-primary">$<%=String.format("%.2f", order.getTotal())%></strong>
                    </div>
                </div>

                <!-- Action Buttons -->
                <div class="text-center mt-4">
                    <a href="${pageContext.request.contextPath}/buyer/orders" class="btn btn-primary btn-lg me-2">
                        <i class="fas fa-list me-2"></i>View All Orders
                    </a>
                    <a href="${pageContext.request.contextPath}/buyer/books" class="btn btn-outline-primary btn-lg">
                        <i class="fas fa-shopping-bag me-2"></i>Continue Shopping
                    </a>
                </div>

                <!-- Confirmation Message -->
                <div class="alert alert-info mt-4 text-center">
                    <i class="fas fa-info-circle me-2"></i>
                    <% if ("COD".equals(payment != null ? payment.getPaymentMethod() : "")) { %>
                        Your order will be delivered soon. Please keep cash ready for payment on delivery.
                    <% } else { %>
                        Your order is being processed. We'll send you tracking details via email.
                    <% } %>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

