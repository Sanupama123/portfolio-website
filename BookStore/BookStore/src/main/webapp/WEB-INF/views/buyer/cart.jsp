<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bookstore.models.User" %>
<%@ page import="com.bookstore.models.CartItem" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    List<CartItem> cartItems = (List<CartItem>) request.getAttribute("cartItems");
    Double cartTotal = (Double) request.getAttribute("cartTotal");
    Integer cartCount = (Integer) request.getAttribute("cartCount");
    Integer wishlistCount = (Integer) request.getAttribute("wishlistCount");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shopping Cart - BookNest</title>
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
        .card {
            border: none;
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
        }
        .book-cover {
            height: 120px;
            object-fit: cover;
        }
        .price {
            font-size: 1.2rem;
            font-weight: 600;
            color: #198754;
        }
        .quantity-input {
            width: 70px;
        }
        .cart-summary {
            position: sticky;
            top: 2rem;
        }
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm">
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/buyer/books">
                            <i class="fas fa-book"></i> Books
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="#">
                            <i class="fas fa-shopping-cart"></i> Cart
                            <span class="badge bg-success"><%=cartCount != null ? cartCount : 0%></span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/buyer/dashboard">
                            <i class="fas fa-tachometer-alt"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/profile">
                            <i class="fas fa-user"></i> Profile
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container py-5">
        <!-- Page Header -->
        <div class="row mb-4">
            <div class="col-12">
                <h1 class="h3 mb-2">Shopping Cart</h1>
                <p class="text-muted">Review and manage your cart items here.</p>
            </div>
        </div>

        <% if (cartItems != null && !cartItems.isEmpty()) { %>
            <div class="row">
                <!-- Cart Items -->
                <div class="col-lg-8">
                    <div class="card mb-4">
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle">
                                    <thead>
                                        <tr>
                                            <th>Book</th>
                                            <th>Price</th>
                                            <th>Quantity</th>
                                            <th>Subtotal</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (CartItem item : cartItems) { %>
                                            <tr>
                                                <td>
                                                    <div class="d-flex align-items-center">
                                                        <img src="${pageContext.request.contextPath}/book-covers/<%=item.getCoverImagePath()%>" 
                                                             class="book-cover rounded me-3" 
                                                             alt="<%=item.getTitle()%>">
                                                        <div>
                                                            <h6 class="mb-0"><%=item.getTitle()%></h6>
                                                            <small class="text-muted"><%=item.getAuthor()%></small>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td class="price">$<%=String.format("%.2f", item.getPrice())%></td>
                                                <td>
                                                    <input type="number" 
                                                           class="form-control quantity-input"
                                                           value="<%=item.getQuantity()%>"
                                                           min="1"
                                                           onchange="updateQuantity('<%=item.getBookId()%>', this.value)">
                                                </td>
                                                <td class="price">$<%=String.format("%.2f", item.getSubtotal())%></td>
                                                <td>
                                                    <button class="btn btn-outline-danger btn-sm" 
                                                            onclick="removeFromCart('<%=item.getBookId()%>')">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </td>
                                            </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Cart Summary -->
                <div class="col-lg-4">
                    <!-- Coupon Section -->
                    <div class="card mb-3">
                        <div class="card-body">
                            <h5 class="card-title mb-3">
                                <i class="fas fa-ticket-alt text-primary"></i> Have a Coupon?
                            </h5>
                            <% 
                                com.bookstore.models.DiscountCoupon appliedCoupon = 
                                    (com.bookstore.models.DiscountCoupon) session.getAttribute("appliedCoupon");
                                Double discount = (Double) session.getAttribute("discount");
                                if (discount == null) discount = 0.0;
                            %>
                            <div class="row g-2">
                                <div class="col-8">
                                    <input type="text" 
                                           class="form-control" 
                                           id="couponCode" 
                                           placeholder="Enter coupon code"
                                           <% if (appliedCoupon != null) { %>
                                               value="<%=appliedCoupon.getCode()%>"
                                               readonly
                                           <% } %>>
                                </div>
                                <div class="col-4">
                                    <% if (appliedCoupon == null) { %>
                                        <button class="btn btn-success w-100" onclick="applyCoupon()">
                                            Apply
                                        </button>
                                    <% } else { %>
                                        <button class="btn btn-outline-danger w-100" onclick="cancelCoupon()">
                                            Remove
                                        </button>
                                    <% } %>
                                </div>
                            </div>
                            <div id="couponMessage" class="mt-2"></div>
                            <% if (appliedCoupon != null) { %>
                                <div class="alert alert-success mt-2 mb-0" role="alert">
                                    <small>
                                        <i class="fas fa-check-circle"></i> 
                                        Coupon "<%=appliedCoupon.getCode()%>" applied! 
                                        You save $<%=String.format("%.2f", discount)%>
                                    </small>
                                </div>
                            <% } %>
                        </div>
                    </div>

                    <!-- Order Summary -->
                    <div class="card cart-summary">
                        <div class="card-body">
                            <h5 class="card-title mb-4">Order Summary</h5>
                            <div class="d-flex justify-content-between mb-3">
                                <span>Subtotal</span>
                                <span class="price">$<%=String.format("%.2f", cartTotal)%></span>
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
                            <div class="d-flex justify-content-between mb-4">
                                <strong>Total</strong>
                                <span class="price h5 mb-0">$<%=String.format("%.2f", cartTotal - discount)%></span>
                            </div>
                            <button class="btn btn-primary w-100 btn-lg" onclick="proceedToCheckout()">
                                <i class="fas fa-lock"></i> Proceed to Checkout
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        <% } else { %>
            <div class="text-center py-5">
                <i class="fas fa-shopping-cart text-muted fa-3x mb-3"></i>
                <h5>Your cart is empty</h5>
                <p class="text-muted">Browse our collection and add some books to your cart!</p>
                <a href="${pageContext.request.contextPath}/buyer/books" class="btn btn-primary">
                    Browse Books
                </a>
            </div>
        <% } %>
    </div>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
    function updateQuantity(bookId, quantity) {
        fetch('${pageContext.request.contextPath}/buyer/cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'action=updateQuantity&bookId=' + bookId + '&quantity=' + quantity
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                window.location.reload();
            } else {
                alert(data.message);
                window.location.reload();
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred. Please try again.');
        });
    }

    function removeFromCart(bookId) {
        if (!confirm('Are you sure you want to remove this book from your cart?')) {
            return;
        }

        fetch('${pageContext.request.contextPath}/buyer/cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'action=remove&bookId=' + bookId
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                window.location.reload();
            }
            alert(data.message);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred. Please try again.');
        });
    }

    function proceedToCheckout() {
        window.location.href = '${pageContext.request.contextPath}/buyer/checkout';
    }

    function applyCoupon() {
        const couponCode = document.getElementById('couponCode').value.trim();
        if (!couponCode) {
            showCouponMessage('Please enter a coupon code', 'danger');
            return;
        }

        const orderAmount = <%=cartTotal%>;

        fetch('${pageContext.request.contextPath}/buyer/apply-coupon', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'action=apply&couponCode=' + encodeURIComponent(couponCode) + '&orderAmount=' + orderAmount
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showCouponMessage(data.message, 'success');
                setTimeout(() => window.location.reload(), 1000);
            } else {
                showCouponMessage(data.message, 'danger');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showCouponMessage('An error occurred. Please try again.', 'danger');
        });
    }

    function cancelCoupon() {
        const orderAmount = <%=cartTotal%>;

        fetch('${pageContext.request.contextPath}/buyer/apply-coupon', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'action=cancel&orderAmount=' + orderAmount
        })
        .then(response => response.json())
        .then(data => {
            showCouponMessage(data.message, 'info');
            setTimeout(() => window.location.reload(), 1000);
        })
        .catch(error => {
            console.error('Error:', error);
        });
    }

    function showCouponMessage(message, type) {
        const messageDiv = document.getElementById('couponMessage');
        messageDiv.innerHTML = '<div class="alert alert-' + type + ' alert-dismissible fade show" role="alert">' +
            message +
            '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' +
            '</div>';
    }
    </script>
</body>
</html>