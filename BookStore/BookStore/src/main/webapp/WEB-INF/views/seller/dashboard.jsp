<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bookstore.models.User" %>
<%
  User user = (User) session.getAttribute("user");
  if (user == null) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
  }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Seller Dashboard - BookNest</title>
    <!-- Bootstrap 5 CSS -->
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
        .navbar-brand {
            font-weight: 700;
            font-size: 1.8rem;
        }
        .nav-link {
            font-weight: 500;
        }
        .sidebar {
            min-height: 100vh;
            background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
        }
        .sidebar .nav-link {
            color: rgba(255, 255, 255, 0.8);
            padding: 0.75rem 1rem;
            border-radius: 8px;
            margin-bottom: 0.25rem;
            transition: all 0.3s ease;
        }
        .sidebar .nav-link:hover,
        .sidebar .nav-link.active {
            color: white;
            background: rgba(255, 255, 255, 0.1);
        }
        .main-content {
            background-color: #f8f9fa;
            min-height: 100vh;
        }
        .card {
            border: none;
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
            transition: transform 0.3s;
        }
        .card:hover {
            transform: translateY(-2px);
        }
        .stat-card {
            background: white;
            border-radius: 10px;
            padding: 1.5rem;
            text-align: center;
            border-left: 4px solid #0d6efd;
        }
        .stat-card.success {
            border-left-color: #198754;
        }
        .stat-card.warning {
            border-left-color: #ffc107;
        }
        .stat-card.danger {
            border-left-color: #dc3545;
        }
        .stat-value {
            font-size: 2rem;
            font-weight: 700;
            color: #212529;
        }
        .stat-label {
            color: #6c757d;
            font-size: 0.9rem;
            margin-top: 0.5rem;
        }
        .activity-item {
            padding: 1rem 0;
            border-bottom: 1px solid #e9ecef;
        }
        .activity-item:last-child {
            border-bottom: none;
        }
        .activity-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 1rem;
        }
        .btn-modern {
            background: #0d6efd;
            border: none;
            color: white;
            padding: 0.5rem 1rem;
            border-radius: 6px;
            font-weight: 500;
            text-decoration: none;
            transition: all 0.3s ease;
        }
        .btn-modern:hover {
            background: #0b5ed7;
            color: white;
            transform: translateY(-1px);
        }
        .btn-modern.danger {
            background: #dc3545;
        }
        .btn-modern.danger:hover {
            background: #bb2d3b;
        }
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm fixed-top">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}">
                <i class="fas fa-book-reader text-primary"></i> BookNest
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}">
                            <i class="fas fa-home"></i> Home
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/seller/dashboard">
                            <i class="fas fa-tachometer-alt"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/seller/my-books">
                            <i class="fas fa-books"></i> My Books
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/seller/add-book">
                            <i class="fas fa-plus-circle"></i> Add Book
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/seller/orders">
                            <i class="fas fa-shopping-cart"></i> Orders
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/profile">
                            <i class="fas fa-user-cog"></i> Profile
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt"></i> Logout
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container-fluid" style="margin-top: 70px;">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 px-0">
                <div class="sidebar p-3">
                    <h5 class="text-white mb-4">
                        <i class="fas fa-store me-2"></i>Seller Panel
                    </h5>
                    <nav class="nav flex-column">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/seller/dashboard">
                            <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                        </a>
                        <a class="nav-link" href="${pageContext.request.contextPath}/seller/my-books">
                            <i class="fas fa-book me-2"></i>My Books
                        </a>
                        <a class="nav-link" href="${pageContext.request.contextPath}/seller/add-book">
                            <i class="fas fa-plus-circle me-2"></i>Add Book
                        </a>
                        <a class="nav-link" href="${pageContext.request.contextPath}/seller/orders">
                            <i class="fas fa-shopping-bag me-2"></i>Orders
                        </a>
                        <a class="nav-link" href="${pageContext.request.contextPath}/seller/reviews">
                            <i class="fas fa-comments me-2"></i>Reviews
                        </a>
                        <a class="nav-link" href="#">
                            <i class="fas fa-chart-line me-2"></i>Analytics
                        </a>
                    </nav>
                    
                    <div class="mt-4 pt-3 border-top border-secondary">
                        <div class="d-flex align-items-center text-white mb-3">
                            <div class="bg-warning rounded-circle d-flex align-items-center justify-content-center me-2" style="width: 40px; height: 40px;">
                                <span class="fw-bold text-dark">
                                    <%= user.getFirstName() != null ? user.getFirstName().charAt(0) : 'S' %>
                                </span>
                            </div>
                            <div>
                                <div class="fw-bold">
                                    <%= user.getFirstName() != null ? user.getFirstName() + " " + user.getLastName() : "Seller User" %>
                                </div>
                                <small class="text-white-50">Book Seller</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Main Content -->
            <div class="col-md-9 col-lg-10 main-content">
                <div class="container-fluid py-4">
                    <!-- Page Header -->
                    <div class="row mb-4">
                        <div class="col-12">
                            <h1 class="h3 mb-1">Welcome back, <%= user.getFirstName() != null ? user.getFirstName() : "Seller" %>!</h1>
                            <p class="text-muted">Manage your books and track your sales performance.</p>
                        </div>
                    </div>

                    <!-- Stats Cards -->
                    <div class="row mb-4">
                        <div class="col-md-3 mb-3">
                            <div class="card stat-card">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <i class="fas fa-book fa-2x text-primary"></i>
                                        <span class="badge bg-primary">+3</span>
                                    </div>
                                    <div class="stat-value">84</div>
                                    <div class="stat-label">Total Books</div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="card stat-card success">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <i class="fas fa-shopping-bag fa-2x text-success"></i>
                                        <span class="badge bg-success">+12%</span>
                                    </div>
                                    <div class="stat-value">127</div>
                                    <div class="stat-label">Orders</div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="card stat-card warning">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <i class="fas fa-dollar-sign fa-2x text-warning"></i>
                                        <span class="badge bg-warning">+18%</span>
                                    </div>
                                    <div class="stat-value">$8,456</div>
                                    <div class="stat-label">Revenue</div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 mb-3">
                            <div class="card stat-card danger">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <i class="fas fa-exclamation-triangle fa-2x text-danger"></i>
                                        <span class="badge bg-danger">Alert</span>
                                    </div>
                                    <div class="stat-value">3</div>
                                    <div class="stat-label">Low Stock</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Recent Activity -->
                    <div class="row">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">
                                        <i class="fas fa-bell me-2"></i>Recent Activity
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <div class="activity-item">
                                        <div class="d-flex align-items-center">
                                            <div class="activity-icon bg-success me-3">
                                                <i class="fas fa-plus"></i>
                                            </div>
                                            <div class="flex-grow-1">
                                                <div class="fw-bold">New book "React Patterns" added</div>
                                                <small class="text-muted">2 minutes ago</small>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="activity-item">
                                        <div class="d-flex align-items-center">
                                            <div class="activity-icon bg-primary me-3">
                                                <i class="fas fa-shopping-cart"></i>
                                            </div>
                                            <div class="flex-grow-1">
                                                <div class="fw-bold">Order #555 received for "Clean Code"</div>
                                                <small class="text-muted">15 minutes ago</small>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="activity-item">
                                        <div class="d-flex align-items-center">
                                            <div class="activity-icon bg-warning me-3">
                                                <i class="fas fa-exclamation-triangle"></i>
                                            </div>
                                            <div class="flex-grow-1">
                                                <div class="fw-bold">Low stock alert: "Design Patterns" (3 left)</div>
                                                <small class="text-muted">1 hour ago</small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>