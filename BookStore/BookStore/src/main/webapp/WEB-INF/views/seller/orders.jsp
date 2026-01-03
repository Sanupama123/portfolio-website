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
    <title>Orders Management - Seller</title>
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
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            padding: 3rem 0;
            margin-top: 56px;
            margin-bottom: 3rem;
            box-shadow: 0 5px 20px rgba(0,0,0,0.2);
        }
        .stats-card {
            background: white;
            border-radius: 15px;
            padding: 20px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            margin-bottom: 20px;
            text-align: center;
            transition: all 0.3s ease;
        }
        .stats-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
        }
        .stats-card .icon {
            font-size: 2.5rem;
            margin-bottom: 10px;
        }
        .stats-card .number {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 5px;
        }
        .stats-card .label {
            color: #6c757d;
            font-size: 0.9rem;
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
        .status-return_requested {
            background: linear-gradient(135deg, #f7971e 0%, #ffd200 100%);
            color: white;
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
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
        }
        .update-status-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            padding: 8px 20px;
            border-radius: 20px;
            transition: all 0.3s ease;
        }
        .update-status-btn:hover {
            transform: scale(1.05);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        .status-select {
            border-radius: 10px;
            border: 2px solid #e9ecef;
            padding: 8px 12px;
            font-size: 0.9rem;
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/seller/dashboard">
                            <i class="fas fa-home me-1"></i>Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/seller/my-books">
                            <i class="fas fa-book me-1"></i>My Books
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/seller/orders">
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
                        <i class="fas fa-boxes me-3"></i>Order Management
                    </h1>
                    <p class="lead mb-0">Manage and track all your book orders</p>
                </div>
                <div class="col-md-4 text-md-end mt-3 mt-md-0">
                    <a href="${pageContext.request.contextPath}/seller/my-books" class="btn btn-light btn-lg">
                        <i class="fas fa-book me-2"></i>My Books
                    </a>
                </div>
            </div>
        </div>
    </header>

    <!-- Main Content -->
    <div class="container">
        <!-- Statistics -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="stats-card">
                    <div class="icon text-primary"><i class="fas fa-shopping-cart"></i></div>
                    <div class="number" id="totalOrders">0</div>
                    <div class="label">Total Orders</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-card">
                    <div class="icon text-info"><i class="fas fa-clock"></i></div>
                    <div class="number" id="processingOrders">0</div>
                    <div class="label">Processing</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-card">
                    <div class="icon text-warning"><i class="fas fa-truck"></i></div>
                    <div class="number" id="shippedOrders">0</div>
                    <div class="label">Shipped</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stats-card">
                    <div class="icon text-success"><i class="fas fa-check-circle"></i></div>
                    <div class="number" id="deliveredOrders">0</div>
                    <div class="label">Delivered</div>
                </div>
            </div>
        </div>

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
                <button class="filter-btn" onclick="filterOrders('RETURN_REQUESTED')">
                    <i class="fas fa-undo me-2"></i>Returns
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
                    else if ("RETURN_REQUESTED".equals(order.getStatus())) statusClass = "status-return_requested";
                %>
                    <div class="order-card" data-status="<%= order.getStatus() %>">
                        <div class="order-header">
                            <div class="d-flex justify-content-between align-items-start flex-wrap">
                                <div class="mb-2 mb-md-0">
                                    <div class="h5 mb-1">
                                        <i class="fas fa-receipt me-2"></i>Order #<%= order.getOrderNumber() %>
                                    </div>
                                    <div class="text-muted small">
                                        <i class="far fa-calendar me-2"></i>
                                        <%= order.getCreatedAt() != null ? 
                                            order.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a")) : 
                                            "N/A" %>
                                    </div>
                                    <div class="text-muted small mt-1">
                                        <i class="fas fa-user me-2"></i>Buyer ID: <%= order.getUserId() %>
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
                                    <% } else if ("RETURN_REQUESTED".equals(order.getStatus())) { %>
                                        <i class="fas fa-undo me-1"></i>
                                    <% } %>
                                    <%= order.getStatus().replace("_", " ") %>
                                </span>
                            </div>
                        </div>
                        <div class="order-body">
                            <div class="row align-items-center">
                                <div class="col-md-4 mb-3 mb-md-0">
                                    <div class="text-muted small mb-1">Total Amount</div>
                                    <div class="h4 mb-0 text-primary">$<%= String.format("%.2f", order.getTotal()) %></div>
                                    <% if (order.getDiscount() > 0) { %>
                                        <div class="text-success small mt-1">
                                            <i class="fas fa-tag me-1"></i>Discount: $<%= String.format("%.2f", order.getDiscount()) %>
                                        </div>
                                    <% } %>
                                </div>
                                <div class="col-md-8">
                                    <form method="post" class="d-flex gap-2 align-items-center flex-wrap">
                                        <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
                                        <input type="hidden" name="action" value="updateStatus">
                                        <label class="small text-muted mb-0">Update Status:</label>
                                        <select class="status-select flex-grow-1" name="status" id="status-<%= order.getOrderId() %>" style="max-width: 200px;">
                                            <option value="PROCESSING" <%= "PROCESSING".equals(order.getStatus()) ? "selected" : "" %>>Processing</option>
                                            <option value="SHIPPED" <%= "SHIPPED".equals(order.getStatus()) ? "selected" : "" %>>Shipped</option>
                                            <option value="DELIVERED" <%= "DELIVERED".equals(order.getStatus()) ? "selected" : "" %>>Delivered</option>
                                            <option value="CANCELLED" <%= "CANCELLED".equals(order.getStatus()) ? "selected" : "" %>>Cancelled</option>
                                            <option value="RETURN_REQUESTED" <%= "RETURN_REQUESTED".equals(order.getStatus()) ? "selected" : "" %>>Return Requested</option>
                                        </select>
                                        <button class="update-status-btn" type="submit">
                                            <i class="fas fa-sync-alt me-2"></i>Update
                                        </button>
                                    </form>
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
                <p class="text-muted mb-4">Orders for your books will appear here</p>
                <a href="${pageContext.request.contextPath}/seller/my-books" class="btn btn-lg update-status-btn">
                    <i class="fas fa-book me-2"></i>View My Books
                </a>
            </div>
        <% } %>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Calculate statistics
        function calculateStats() {
            const cards = document.querySelectorAll('.order-card');
            let total = 0, processing = 0, shipped = 0, delivered = 0;
            
            cards.forEach(card => {
                total++;
                const status = card.dataset.status;
                if (status === 'PROCESSING') processing++;
                else if (status === 'SHIPPED') shipped++;
                else if (status === 'DELIVERED') delivered++;
            });
            
            document.getElementById('totalOrders').textContent = total;
            document.getElementById('processingOrders').textContent = processing;
            document.getElementById('shippedOrders').textContent = shipped;
            document.getElementById('deliveredOrders').textContent = delivered;
        }

        // Filter orders
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
        
        // Initialize
        document.addEventListener('DOMContentLoaded', function() {
            calculateStats();
            
            // Animate cards on load
            document.querySelectorAll('.order-card').forEach((card, index) => {
                card.style.opacity = '0';
                card.style.transform = 'translateY(20px)';
                card.style.transition = 'all 0.3s ease';
                
                setTimeout(() => {
                    card.style.opacity = '1';
                    card.style.transform = 'translateY(0)';
                }, index * 100);
            });
        });
    </script>
</body>
</html>
