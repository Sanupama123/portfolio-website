<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bookstore.models.User" %>
<%@ page import="com.bookstore.models.WishlistItem" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    List<WishlistItem> wishlistItems = (List<WishlistItem>) request.getAttribute("wishlistItems");
    Integer cartCount = (Integer) request.getAttribute("cartCount");
    Integer wishlistCount = (Integer) request.getAttribute("wishlistCount");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Wishlist - BookNest</title>
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
            transition: transform 0.3s;
        }
        .card:hover {
            transform: translateY(-5px);
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/buyer/cart">
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
                <h1 class="h3 mb-2">My Wishlist</h1>
                <p class="text-muted">Manage your wishlisted books here.</p>
            </div>
        </div>

        <!-- Wishlist Items -->
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-body">
                        <% if (wishlistItems != null && !wishlistItems.isEmpty()) { %>
                            <div class="table-responsive">
                                <table class="table table-hover align-middle">
                                    <thead>
                                        <tr>
                                            <th>Book</th>
                                            <th>Author</th>
                                            <th>Price</th>
                                            <th>Added On</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (WishlistItem item : wishlistItems) { %>
                                            <tr>
                                                <td>
                                                    <div class="d-flex align-items-center">
                                                        <img src="${pageContext.request.contextPath}/book-covers/<%=item.getCoverImagePath()%>" 
                                                             class="book-cover rounded me-3" 
                                                             alt="<%=item.getTitle()%>">
                                                        <div>
                                                            <h6 class="mb-0"><%=item.getTitle()%></h6>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td><%=item.getAuthor()%></td>
                                                <td class="price">$<%=String.format("%.2f", item.getPrice())%></td>
                                                <td><%=item.getAddedAt()%></td>
                                                <td>
                                                    <button class="btn btn-primary btn-sm me-2" 
                                                            onclick="addToCart('<%=item.getBookId()%>')">
                                                        <i class="fas fa-cart-plus"></i> Add to Cart
                                                    </button>
                                                    <button class="btn btn-danger btn-sm" 
                                                            onclick="removeFromWishlist('<%=item.getBookId()%>')">
                                                        <i class="fas fa-trash"></i> Remove
                                                    </button>
                                                </td>
                                            </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                        <% } else { %>
                            <div class="text-center py-5">
                                <i class="fas fa-heart text-muted fa-3x mb-3"></i>
                                <h5>Your wishlist is empty</h5>
                                <p class="text-muted">Browse our collection and add some books to your wishlist!</p>
                                <a href="${pageContext.request.contextPath}/buyer/books" class="btn btn-primary">
                                    Browse Books
                                </a>
                            </div>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
    function addToCart(bookId) {
        fetch('${pageContext.request.contextPath}/buyer/wishlist', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'action=moveToCart&bookId=' + bookId
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

    function removeFromWishlist(bookId) {
        if (!confirm('Are you sure you want to remove this book from your wishlist?')) {
            return;
        }

        fetch('${pageContext.request.contextPath}/buyer/wishlist', {
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
    </script>
</body>
</html>