<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bookstore.models.User" %>
<%@ page import="com.bookstore.models.Order" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    List<Order> orders = (List<Order>) request.getAttribute("orders");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Orders - BookNest</title>
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
        .order-card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            margin-bottom: 20px;
            transition: all 0.3s ease;
            background: white;
            overflow: hidden;
        }
        .order-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 30px rgba(0,0,0,0.15);
        }
        .order-header {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            padding: 20px;
            border-bottom: 2px solid #dee2e6;
        }
        .order-body {
            padding: 20px;
        }
        .status-badge {
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
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
        .order-number {
            font-size: 1.2rem;
            font-weight: 600;
            color: #667eea;
        }
        .order-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 10px;
        }
        .order-total {
            font-size: 1.5rem;
            font-weight: 700;
            color: #2d3436;
        }
        .view-details-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            padding: 10px 25px;
            border-radius: 25px;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
        }
        .view-details-btn:hover {
            transform: scale(1.05);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            color: white;
        }
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            background: white;
            border-radius: 15px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        }
        .empty-state i {
            font-size: 100px;
            color: #dee2e6;
            margin-bottom: 20px;
        }
        .filter-tabs {
            background: white;
            border-radius: 15px;
            padding: 15px;
            margin-bottom: 30px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        .filter-btn {
            border: none;
            background: transparent;
            padding: 10px 20px;
            margin: 0 5px;
            border-radius: 10px;
            transition: all 0.3s ease;
        }
        .filter-btn:hover, .filter-btn.active {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/buyer/cart">
                            <i class="fas fa-shopping-cart me-1"></i>Cart
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
                    <h1 class="display-5 fw-bold mb-2">
                        <i class="fas fa-box-open me-3"></i>My Orders
                    </h1>
                    <p class="lead mb-0">Track and manage your purchases</p>
                </div>
                <div class="col-md-4 text-md-end mt-3 mt-md-0">
                    <a href="${pageContext.request.contextPath}/buyer/books" class="btn btn-light btn-lg">
                        <i class="fas fa-shopping-bag me-2"></i>Continue Shopping
                    </a>
                </div>
            </div>
        </div>
    </header>

    <!-- Main Content -->
    <div class="container">
        <!-- Filter Tabs -->
        <div class="filter-tabs">
            <div class="d-flex justify-content-center flex-wrap">
                <button class="filter-btn active" onclick="filterOrders('all')">
                    <i class="fas fa-list me-2"></i>All Orders
                </button>
                <button class="filter-btn" onclick="filterOrders('PROCESSING')">
                    <i class="fas fa-clock me-2"></i>Processing
                </button>
                <button class="filter-btn" onclick="filterOrders('SHIPPED')">
                    <i class="fas fa-shipping-fast me-2"></i>Shipped
                </button>
                <button class="filter-btn" onclick="filterOrders('DELIVERED')">
                    <i class="fas fa-check-circle me-2"></i>Delivered
                </button>
                <button class="filter-btn" onclick="filterOrders('CANCELLED')">
                    <i class="fas fa-times-circle me-2"></i>Cancelled
                </button>
            </div>
        </div>

        <!-- Orders List -->
        <% if (orders != null && !orders.isEmpty()) { %>
            <div id="ordersContainer">
                <% for (Order order : orders) { 
                    String statusClass = "status-processing";
                    if ("SHIPPED".equals(order.getStatus())) statusClass = "status-shipped";
                    else if ("DELIVERED".equals(order.getStatus())) statusClass = "status-delivered";
                    else if ("CANCELLED".equals(order.getStatus())) statusClass = "status-cancelled";
                %>
                    <div class="order-card" data-status="<%= order.getStatus() %>">
                        <div class="order-header">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <div class="order-number">
                                        <i class="fas fa-receipt me-2"></i>Order #<%= order.getOrderNumber() %>
                                    </div>
                                    <div class="text-muted small mt-2">
                                        <i class="far fa-calendar me-2"></i>
                                        Placed on <%= order.getCreatedAt() != null ? 
                                            order.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a")) : 
                                            "N/A" %>
                                    </div>
                                </div>
                                <span class="status-badge <%= statusClass %>">
                                    <% if ("PROCESSING".equals(order.getStatus())) { %>
                                        <i class="fas fa-hourglass-half me-1"></i>
                                    <% } else if ("SHIPPED".equals(order.getStatus())) { %>
                                        <i class="fas fa-truck me-1"></i>
                                    <% } else if ("DELIVERED".equals(order.getStatus())) { %>
                                        <i class="fas fa-check-circle me-1"></i>
                                    <% } else if ("CANCELLED".equals(order.getStatus())) { %>
                                        <i class="fas fa-ban me-1"></i>
                                    <% } %>
                                    <%= order.getStatus() %>
                                </span>
                            </div>
                        </div>
                        <div class="order-body">
                            <div class="order-info">
                                <div>
                                    <div class="text-muted small mb-1">Total Amount</div>
                                    <div class="order-total">$<%= String.format("%.2f", order.getTotal()) %></div>
                                    <% if (order.getDiscount() > 0) { %>
                                        <div class="text-success small mt-1">
                                            <i class="fas fa-tag me-1"></i>
                                            Discount: $<%= String.format("%.2f", order.getDiscount()) %>
                                        </div>
                                    <% } %>
                                </div>
                                <div class="text-end">
                                    <a href="${pageContext.request.contextPath}/buyer/order?id=<%= order.getOrderId() %>" 
                                       class="view-details-btn">
                                        <i class="fas fa-eye me-2"></i>View Details
                                    </a>
                                </div>
                            </div>
                            
                            <% if (order.getShippingAddress() != null) { %>
                                <div class="mt-3 pt-3 border-top">
                                    <div class="text-muted small mb-1">
                                        <i class="fas fa-map-marker-alt me-2"></i>Shipping Address
                                    </div>
                                    <div class="small"><%= order.getShippingAddress() %></div>
                                </div>
                            <% } %>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } else { %>
            <div class="empty-state">
                <i class="fas fa-box-open"></i>
                <h3 class="mb-3">No Orders Yet</h3>
                <p class="text-muted mb-4">Start shopping and your orders will appear here</p>
                <a href="${pageContext.request.contextPath}/buyer/books" class="btn btn-lg view-details-btn">
                    <i class="fas fa-shopping-bag me-2"></i>Browse Books
                </a>
            </div>
        <% } %>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function filterOrders(status) {
            const cards = document.querySelectorAll('.order-card');
            const buttons = document.querySelectorAll('.filter-btn');
            
            // Update active button
            buttons.forEach(btn => btn.classList.remove('active'));
            event.target.closest('.filter-btn').classList.add('active');
            
            // Filter cards
            cards.forEach(card => {
                if (status === 'all' || card.dataset.status === status) {
                    card.style.display = 'block';
                    setTimeout(() => {
                        card.style.opacity = '1';
                        card.style.transform = 'translateY(0)';
                    }, 10);
                } else {
                    card.style.opacity = '0';
                    card.style.transform = 'translateY(20px)';
                    setTimeout(() => {
                        card.style.display = 'none';
                    }, 300);
                }
            });
        }
        
        // Initialize card animations
        document.querySelectorAll('.order-card').forEach((card, index) => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';
            card.style.transition = 'all 0.3s ease';
            
            setTimeout(() => {
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }, index * 100);
        });
    </script>
</body>
</html>
