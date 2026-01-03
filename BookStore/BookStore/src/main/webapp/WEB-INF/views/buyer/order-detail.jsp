<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bookstore.models.User" %>
<%@ page import="com.bookstore.models.Order" %>
<%@ page import="com.bookstore.models.OrderItem" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    Order order = (Order) request.getAttribute("order");
    
    String statusClass = "status-processing";
    String statusIcon = "fa-hourglass-half";
    if ("SHIPPED".equals(order.getStatus())) {
        statusClass = "status-shipped";
        statusIcon = "fa-truck";
    } else if ("DELIVERED".equals(order.getStatus())) {
        statusClass = "status-delivered";
        statusIcon = "fa-check-circle";
    } else if ("CANCELLED".equals(order.getStatus())) {
        statusClass = "status-cancelled";
        statusIcon = "fa-ban";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order #<%= order.getOrderNumber() %> - BookNest</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            min-height: 100vh;
            padding-bottom: 50px;
        }
        .navbar {
            background: white !important;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 3rem 0;
            margin-top: 56px;
            margin-bottom: 3rem;
            box-shadow: 0 5px 20px rgba(0,0,0,0.2);
        }
        .card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            margin-bottom: 20px;
            background: white;
        }
        .card-header {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-bottom: 2px solid #dee2e6;
            border-radius: 15px 15px 0 0 !important;
            padding: 20px;
            font-weight: 600;
        }
        .status-badge {
            padding: 10px 20px;
            border-radius: 25px;
            font-size: 0.9rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            display: inline-block;
        }
        .status-processing {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
        }
        .status-shipped {
            background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
            color: white;
        }
        .status-delivered {
            background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
            color: white;
        }
        .status-cancelled {
            background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%);
            color: white;
        }
        .order-item {
            padding: 15px;
            border-bottom: 1px solid #e9ecef;
            transition: background 0.3s ease;
        }
        .order-item:hover {
            background: #f8f9fa;
        }
        .order-item:last-child {
            border-bottom: none;
        }
        .info-box {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .info-box h6 {
            color: #667eea;
            font-weight: 600;
            margin-bottom: 15px;
        }
        .timeline {
            position: relative;
            padding-left: 30px;
        }
        .timeline::before {
            content: '';
            position: absolute;
            left: 10px;
            top: 0;
            bottom: 0;
            width: 2px;
            background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
        }
        .timeline-item {
            position: relative;
            margin-bottom: 20px;
        }
        .timeline-item::before {
            content: '';
            position: absolute;
            left: -24px;
            top: 5px;
            width: 12px;
            height: 12px;
            border-radius: 50%;
            background: #667eea;
            border: 2px solid white;
            box-shadow: 0 0 0 2px #667eea;
        }
        .timeline-item.active::before {
            background: #28a745;
            box-shadow: 0 0 0 2px #28a745;
            animation: pulse 2s infinite;
        }
        @keyframes pulse {
            0%, 100% { transform: scale(1); }
            50% { transform: scale(1.2); }
        }
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
        }
        .btn-outline-danger {
            border-color: #eb3349;
            color: #eb3349;
        }
        .btn-outline-danger:hover {
            background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%);
            border-color: #eb3349;
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-light fixed-top">
        <div class="container">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}">
                <i class="fas fa-book-reader text-primary me-2"></i>BookNest
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/buyer/dashboard">
                            <i class="fas fa-home me-1"></i>Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/buyer/books">
                            <i class="fas fa-book me-1"></i>Books
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/buyer/orders">
                            <i class="fas fa-box me-1"></i>Orders
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/profile">
                            <i class="fas fa-user me-1"></i>Profile
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt me-1"></i>Logout
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Page Header -->
    <header class="page-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <div class="d-flex align-items-center mb-2">
                        <a href="${pageContext.request.contextPath}/buyer/orders" class="btn btn-light me-3">
                            <i class="fas fa-arrow-left"></i>
                        </a>
                        <div>
                            <h1 class="h3 mb-1">Order #<%= order.getOrderNumber() %></h1>
                            <p class="mb-0 opacity-75">
                                <i class="far fa-calendar me-2"></i>
                                Placed on <%= order.getCreatedAt() != null ? 
                                    order.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a")) : 
                                    "N/A" %>
                            </p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4 text-md-end mt-3 mt-md-0">
                    <span class="status-badge <%= statusClass %>">
                        <i class="fas <%= statusIcon %> me-2"></i><%= order.getStatus() %>
                    </span>
                </div>
            </div>
        </div>
    </header>

    <!-- Main Content -->
    <div class="container">
        <div class="row">
            <!-- Left Column - Order Items & Timeline -->
            <div class="col-lg-8">
                <!-- Order Items -->
                <div class="card">
                    <div class="card-header">
                        <i class="fas fa-shopping-bag me-2"></i>Order Items
                    </div>
                    <div class="card-body p-0">
                        <% for (OrderItem item : order.getItems()) { %>
                            <div class="order-item d-flex justify-content-between align-items-center">
                                <div class="flex-grow-1">
                                    <h6 class="mb-1"><%= item.getTitle() %></h6>
                                    <small class="text-muted">
                                        Quantity: <%= item.getQuantity() %> × $<%= String.format("%.2f", item.getPrice()) %>
                                    </small>
                                </div>
                                <div class="text-end">
                                    <h6 class="mb-0 text-primary">$<%= String.format("%.2f", item.getSubtotal()) %></h6>
                                </div>
                            </div>
                        <% } %>
                    </div>
                </div>

                <!-- Order Timeline -->
                <div class="card">
                    <div class="card-header">
                        <i class="fas fa-clock me-2"></i>Order Status Timeline
                    </div>
                    <div class="card-body">
                        <div class="timeline">
                            <div class="timeline-item active">
                                <strong>Order Placed</strong>
                                <div class="text-muted small">
                                    <%= order.getCreatedAt() != null ? 
                                        order.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a")) : 
                                        "N/A" %>
                                </div>
                            </div>
                            <div class="timeline-item <%= "PROCESSING".equals(order.getStatus()) || "SHIPPED".equals(order.getStatus()) || "DELIVERED".equals(order.getStatus()) ? "active" : "" %>">
                                <strong>Processing</strong>
                                <div class="text-muted small">Your order is being prepared</div>
                            </div>
                            <div class="timeline-item <%= "SHIPPED".equals(order.getStatus()) || "DELIVERED".equals(order.getStatus()) ? "active" : "" %>">
                                <strong>Shipped</strong>
                                <div class="text-muted small">
                                    <% if ("SHIPPED".equals(order.getStatus()) || "DELIVERED".equals(order.getStatus())) { %>
                                        Your order is on the way
                                    <% } else { %>
                                        Waiting for shipment
                                    <% } %>
                                </div>
                            </div>
                            <div class="timeline-item <%= "DELIVERED".equals(order.getStatus()) ? "active" : "" %>">
                                <strong>Delivered</strong>
                                <div class="text-muted small">
                                    <% if ("DELIVERED".equals(order.getStatus())) { %>
                                        Order delivered successfully
                                    <% } else { %>
                                        Pending delivery
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Right Column - Summary & Actions -->
            <div class="col-lg-4">
                <!-- Order Summary -->
                <div class="card">
                    <div class="card-header">
                        <i class="fas fa-file-invoice-dollar me-2"></i>Order Summary
                    </div>
                    <div class="card-body">
                        <div class="d-flex justify-content-between mb-2">
                            <span>Subtotal</span>
                            <span>$<%= String.format("%.2f", order.getSubtotal()) %></span>
                        </div>
                        <% if (order.getDiscount() > 0) { %>
                            <div class="d-flex justify-content-between mb-2 text-success">
                                <span>
                                    <i class="fas fa-tag"></i> Discount
                                    <% if (order.getCouponCode() != null) { %>
                                        <br><small class="text-muted">(<%=order.getCouponCode()%>)</small>
                                    <% } %>
                                </span>
                                <span>-$<%= String.format("%.2f", order.getDiscount()) %></span>
                            </div>
                        <% } %>
                        <div class="d-flex justify-content-between mb-2">
                            <span>Shipping</span>
                            <span class="text-success">
                                <%= order.getShipping() == 0 ? "Free" : "$" + String.format("%.2f", order.getShipping()) %>
                            </span>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between">
                            <strong class="h6">Total</strong>
                            <strong class="h5 text-primary">$<%= String.format("%.2f", order.getTotal()) %></strong>
                        </div>
                    </div>
                </div>

                <!-- Shipping Information -->
                <div class="info-box">
                    <h6><i class="fas fa-map-marker-alt me-2"></i>Shipping Address</h6>
                    <% if (order.getFullName() != null) { %>
                        <p class="mb-1"><strong><%= order.getFullName() %></strong></p>
                    <% } %>
                    <% if (order.getPhoneNumber() != null) { %>
                        <p class="mb-1 small"><i class="fas fa-phone me-2"></i><%= order.getPhoneNumber() %></p>
                    <% } %>
                    <p class="mb-0 small"><%= order.getShippingAddress() != null ? order.getShippingAddress() : "N/A" %></p>
                </div>

                <!-- Actions -->
                <div class="d-grid gap-2">
                    <% if ("PROCESSING".equals(order.getStatus())) { %>
                        <form method="post" class="d-inline">
                            <input type="hidden" name="id" value="<%=order.getOrderId()%>">
                            <input type="hidden" name="action" value="cancel">
                            <button type="submit" class="btn btn-outline-danger w-100" 
                                    onclick="return confirm('Are you sure you want to cancel this order?')">
                                <i class="fas fa-times-circle me-2"></i>Cancel Order
                            </button>
                        </form>
                    <% } else if ("DELIVERED".equals(order.getStatus())) { %>
                        <form method="post" class="d-inline">
                            <input type="hidden" name="id" value="<%=order.getOrderId()%>">
                            <input type="hidden" name="action" value="return">
                            <button type="submit" class="btn btn-outline-danger w-100">
                                <i class="fas fa-undo me-2"></i>Request Return
                            </button>
                        </form>
                    <% } %>
                    <a href="${pageContext.request.contextPath}/buyer/orders" class="btn btn-primary">
                        <i class="fas fa-list me-2"></i>Back to Orders
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
