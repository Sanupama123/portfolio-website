<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bookstore.models.User" %>
<%@ page import="com.bookstore.models.Book" %>
<%@ page import="com.bookstore.dao.CategoryDAO.Category" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    List<Book> books = (List<Book>) request.getAttribute("books");
    Map<Integer, Double> bookRatings = (Map<Integer, Double>) request.getAttribute("bookRatings");
    Map<Integer, Integer> reviewCounts = (Map<Integer, Integer>) request.getAttribute("reviewCounts");
    Map<Integer, Boolean> wishlistStatus = (Map<Integer, Boolean>) request.getAttribute("wishlistStatus");
    Map<Integer, Integer> cartStatus = (Map<Integer, Integer>) request.getAttribute("cartStatus");
    Integer cartCount = (Integer) request.getAttribute("cartCount");
    Integer wishlistCount = (Integer) request.getAttribute("wishlistCount");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Browse Books - BookNest</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" rel="stylesheet">
    <!-- Custom Icon Style -->
    <style>
        .btn-cart {
            transition: color 0.3s ease;
        }
        /* Cart icon visibility fix: use custom state classes instead of 'far' (not available for shopping-cart) */
        .btn-cart i.fa-shopping-cart {
            color: #198754;
            transition: color 0.3s ease;
        }
        .btn-cart i.not-in-cart {
            opacity: 0.5;
        }
        .btn-cart:hover i.fa-shopping-cart {
            color: #146c43;
        }
    </style>
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
            height: 300px;
            object-fit: contain;
            padding: 1rem;
            background-color: #fff;
        }
        .rating-stars {
            color: #ffc107;
        }
        .rating-count {
            color: #6c757d;
            font-size: 0.9rem;
        }
        .price {
            font-size: 1.2rem;
            font-weight: 600;
            color: #198754;
        }
        .btn-wishlist {
            color: #dc3545;
            background: none;
            border: none;
            padding: 0;
        }
        .btn-wishlist:hover {
            color: #bb2d3b;
        }
        .btn-cart {
            color: #198754;
            background: none;
            border: none;
            padding: 0;
        }
        .btn-cart:hover {
            color: #146c43;
        }
        .filter-section {
            background: white;
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 2rem;
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
        }
        .filter-section .form-label {
            font-weight: 500;
            color: #495057;
            margin-bottom: 0.5rem;
        }
        .filter-section .form-control,
        .filter-section .form-select {
            border-radius: 8px;
            border: 1px solid #dee2e6;
            transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
        }
        .filter-section .form-control:focus,
        .filter-section .form-select:focus {
            border-color: #0d6efd;
            box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
        }
        .filter-section .btn {
            border-radius: 8px;
            font-weight: 500;
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
                        <a class="nav-link active" href="#">
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
                <h1 class="h3 mb-2">Browse Books</h1>
                <p class="text-muted">Discover your next favorite book from our collection.</p>
            </div>
        </div>

        <!-- Search and Filter Section -->
        <div class="filter-section">
                <form method="get" class="row g-3">
                    <div class="col-md-3">
                        <label class="form-label">Search</label>
                        <input type="text" class="form-control" name="q" 
                               value="<%= request.getAttribute("q") != null ? request.getAttribute("q") : "" %>" 
                               placeholder="Title, author, ISBN, publisher">
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Category</label>
                        <select class="form-select" name="categoryId">
                            <option value="">All Categories</option>
                            <% for (Category category : (List<Category>)request.getAttribute("categories")) { %>
                                <option value="<%= category.getId() %>" 
                                        <%= (request.getAttribute("selectedCategoryId") != null && category.getId().equals(request.getAttribute("selectedCategoryId"))) ? "selected" : "" %>>
                                    <%= category.getName() %>
                                </option>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Min Price</label>
                        <input type="number" step="0.01" class="form-control" name="minPrice" 
                               value="<%= request.getAttribute("minPrice") != null ? request.getAttribute("minPrice") : "" %>" 
                               placeholder="0.00">
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Max Price</label>
                        <input type="number" step="0.01" class="form-control" name="maxPrice" 
                               value="<%= request.getAttribute("maxPrice") != null ? request.getAttribute("maxPrice") : "" %>" 
                               placeholder="999.99">
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Min Rating</label>
                        <input type="number" step="0.1" min="0" max="5" class="form-control" name="minRating" 
                               value="<%= request.getAttribute("minRating") != null ? request.getAttribute("minRating") : "" %>" 
                               placeholder="0.0">
                    </div>
                    <div class="col-md-1">
                        <label class="form-label">&nbsp;</label>
                        <button type="submit" class="btn btn-primary w-100">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                </form>
                
                <!-- Sort Options -->
                <div class="row mt-3">
                    <div class="col-md-3">
                        <label class="form-label">Sort By</label>
                        <select class="form-select" name="sortBy" onchange="updateSort()">
                            <option value="">Default (Newest)</option>
                            <option value="title" <%= "title".equals(request.getAttribute("sortBy")) ? "selected" : "" %>>Title</option>
                            <option value="author" <%= "author".equals(request.getAttribute("sortBy")) ? "selected" : "" %>>Author</option>
                            <option value="price" <%= "price".equals(request.getAttribute("sortBy")) ? "selected" : "" %>>Price</option>
                            <option value="rating" <%= "rating".equals(request.getAttribute("sortBy")) ? "selected" : "" %>>Rating</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Order</label>
                        <select class="form-select" name="sortOrder" onchange="updateSort()">
                            <option value="asc" <%= "asc".equals(request.getAttribute("sortOrder")) ? "selected" : "" %>>Ascending</option>
                            <option value="desc" <%= "desc".equals(request.getAttribute("sortOrder")) ? "selected" : "" %>>Descending</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">&nbsp;</label>
                        <button type="button" class="btn btn-outline-secondary w-100" onclick="clearFilters()">
                            <i class="fas fa-times"></i> Clear Filters
                        </button>
                    </div>
                </div>
        </div>

        <!-- Results Info -->
        <div class="row mb-3">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center">
                    <h5 class="mb-0">
                        <% if (books != null && !books.isEmpty()) { %>
                            Found <%= books.size() %> book<%= books.size() != 1 ? "s" : "" %>
                        <% } else { %>
                            No books found
                        <% } %>
                    </h5>
                    <% if (request.getAttribute("q") != null || request.getAttribute("selectedCategoryId") != null || 
                          request.getAttribute("minPrice") != null || request.getAttribute("maxPrice") != null || 
                          request.getAttribute("minRating") != null) { %>
                        <small class="text-muted">
                            <i class="fas fa-filter me-1"></i>Filtered results
                        </small>
                    <% } %>
                </div>
            </div>
        </div>

        <!-- Books Grid -->
        <div class="row g-4">
            <% if (books != null && !books.isEmpty()) { 
                for (Book book : books) { 
                    Double rating = bookRatings.get(book.getBookId());
                    Integer reviewCount = reviewCounts.get(book.getBookId());
                    Boolean inWishlist = wishlistStatus.get(book.getBookId());
                    Integer inCart = cartStatus.get(book.getBookId());
            %>
            <div class="col-md-3">
                <div class="card h-100">
                    <a href="${pageContext.request.contextPath}/buyer/book/<%=book.getBookId()%>" class="text-decoration-none text-dark">
                        <img src="${pageContext.request.contextPath}/book-covers/<%=book.getCoverImagePath()%>" 
                             class="card-img-top book-cover" 
                             alt="<%=book.getTitle()%>">
                    </a>
                    <div class="card-body">
                        <h5 class="card-title text-truncate" title="<%=book.getTitle()%>">
                            <a href="${pageContext.request.contextPath}/buyer/book/<%=book.getBookId()%>" class="text-decoration-none text-dark">
                                <%=book.getTitle()%>
                            </a>
                        </h5>
                        <p class="card-text text-muted mb-2"><%=book.getAuthor()%></p>
                        
                        <!-- Rating -->
                        <div class="mb-2">
                            <span class="rating-stars" onclick="showRatingModal('<%=book.getBookId()%>', '<%=book.getTitle()%>')">
                                <% if (rating != null) {
                                    for (int i = 1; i <= 5; i++) { %>
                                        <i class="fa<%=i <= rating ? 's' : 'r'%> fa-star"></i>
                                    <% }
                                } else {
                                    for (int i = 1; i <= 5; i++) { %>
                                        <i class="far fa-star"></i>
                                    <% }
                                } %>
                            </span>
                            <span class="rating-count ms-1">
                                (<%=reviewCount != null ? reviewCount : 0%>)
                            </span>
                        </div>
                        
                        <div class="mt-3 d-flex justify-content-between align-items-center">
                            <span class="price">$<%=String.format("%.2f", book.getPrice())%></span>
                            <div>
                                <button class="btn btn-wishlist me-2" 
                                        onclick="toggleWishlist('<%=book.getBookId()%>')"
                                        title="<%=inWishlist ? "Remove from Wishlist" : "Add to Wishlist"%>">
                                    <i class="fa<%=inWishlist ? 's' : 'r'%> fa-heart"></i>
                                </button>
                                <button class="btn btn-cart" 
                                        onclick="toggleCart('<%=book.getBookId()%>')"
                                        title="<%=inCart != null && inCart > 0 ? "Already in Cart" : "Add to Cart"%>">
                                    <i class="fas fa-shopping-cart <%=inCart != null && inCart > 0 ? "in-cart" : "not-in-cart"%>"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <% }
            } else { %>
            <div class="col-12">
                <div class="alert alert-info">
                    No books available at the moment.
                </div>
            </div>
            <% } %>
        </div>
    </div>

    <!-- Rating Modal -->
    <div class="modal fade" id="ratingModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Rate Book</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <h6 class="book-title mb-4"></h6>
                    <div class="rating-stars-large text-center mb-3">
                        <i class="far fa-star fa-2x" data-rating="1"></i>
                        <i class="far fa-star fa-2x" data-rating="2"></i>
                        <i class="far fa-star fa-2x" data-rating="3"></i>
                        <i class="far fa-star fa-2x" data-rating="4"></i>
                        <i class="far fa-star fa-2x" data-rating="5"></i>
                    </div>
                    <div class="mb-3">
                        <label for="reviewText" class="form-label">Your Review (Optional)</label>
                        <textarea class="form-control" id="reviewText" rows="3"></textarea>
                    </div>
                    <input type="hidden" id="bookIdToRate">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" onclick="submitRating()">Submit Rating</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
    function toggleWishlist(bookId) {
        const isInWishlist = document.querySelector("button[onclick=\"toggleWishlist('" + bookId + "')\"] i").classList.contains('fas');
        
        fetch('${pageContext.request.contextPath}/buyer/books', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'action=' + (isInWishlist ? 'removeFromWishlist' : 'addToWishlist') + '&bookId=' + bookId
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                const icon = document.querySelector("button[onclick=\"toggleWishlist('" + bookId + "')\"] i");
                icon.classList.toggle('far');
                icon.classList.toggle('fas');
                // Update wishlist count in navbar
                const wishlistBadge = document.querySelector('.wishlist-badge');
                if (wishlistBadge) {
                    wishlistBadge.textContent = data.wishlistCount;
                }
            }
            // Show toast or alert with message
            alert(data.message);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred. Please try again.');
        });
    }

    function toggleCart(bookId) {
        const button = document.querySelector("button[onclick=\"toggleCart('" + bookId + "')\"]");
        const icon = button.querySelector("i");
        const isInCart = icon.classList.contains('in-cart');
        
        if (isInCart) {
            alert('Item is already in cart. Please visit cart to modify quantity.');
            return;
        }

        fetch('${pageContext.request.contextPath}/buyer/books', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'action=addToCart&bookId=' + bookId + '&quantity=1'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Update cart icon state
                icon.classList.remove('not-in-cart');
                icon.classList.add('in-cart');
                button.title = "Already in Cart";
                
                // Update cart count in navbar
                const cartBadge = document.querySelector('.badge.bg-success');
                if (cartBadge) {
                    cartBadge.textContent = data.cartCount;
                }
            }
            // Show toast or alert with message
            alert(data.message);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred. Please try again.');
        });
    }

    // Rating functionality
    const ratingModal = new bootstrap.Modal(document.getElementById('ratingModal'));
    const ratingStars = document.querySelectorAll('.rating-stars-large i');
    let selectedRating = 0;

    ratingStars.forEach(star => {
        star.addEventListener('mouseover', function() {
            const rating = this.getAttribute('data-rating');
            updateStars(rating);
        });

        star.addEventListener('click', function() {
            selectedRating = this.getAttribute('data-rating');
            updateStars(selectedRating);
        });
    });

    document.querySelector('.rating-stars-large').addEventListener('mouseout', function() {
        updateStars(selectedRating);
    });

    function showRatingModal(bookId, bookTitle) {
        document.querySelector('#ratingModal .book-title').textContent = bookTitle;
        document.getElementById('bookIdToRate').value = bookId;
        selectedRating = 0;
        updateStars(0);
        document.getElementById('reviewText').value = '';
        ratingModal.show();
    }

    function updateStars(rating) {
        ratingStars.forEach(star => {
            const starRating = star.getAttribute('data-rating');
            if (starRating <= rating) {
                star.classList.remove('far');
                star.classList.add('fas');
            } else {
                star.classList.remove('fas');
                star.classList.add('far');
            }
        });
    }

    function submitRating() {
        if (selectedRating === 0) {
            alert('Please select a rating');
            return;
        }

        const bookId = document.getElementById('bookIdToRate').value;
        const reviewText = document.getElementById('reviewText').value;

        fetch('${pageContext.request.contextPath}/buyer/books', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'action=rate&bookId=' + bookId + '&rating=' + selectedRating + '&reviewText=' + encodeURIComponent(reviewText)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                ratingModal.hide();
                window.location.reload();
            }
            alert(data.message);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred. Please try again.');
        });
    }

    function updateSort() {
        const form = document.querySelector('form');
        const sortBy = document.querySelector('select[name="sortBy"]').value;
        const sortOrder = document.querySelector('select[name="sortOrder"]').value;
        
        // Add sort parameters to form
        if (sortBy) {
            let sortByInput = form.querySelector('input[name="sortBy"]');
            if (!sortByInput) {
                sortByInput = document.createElement('input');
                sortByInput.type = 'hidden';
                sortByInput.name = 'sortBy';
                form.appendChild(sortByInput);
            }
            sortByInput.value = sortBy;
        }
        
        let sortOrderInput = form.querySelector('input[name="sortOrder"]');
        if (!sortOrderInput) {
            sortOrderInput = document.createElement('input');
            sortOrderInput.type = 'hidden';
            sortOrderInput.name = 'sortOrder';
            form.appendChild(sortOrderInput);
        }
        sortOrderInput.value = sortOrder;
        
        form.submit();
    }

    function clearFilters() {
        window.location.href = '${pageContext.request.contextPath}/buyer/books';
    }
    </script>
</body>
</html>