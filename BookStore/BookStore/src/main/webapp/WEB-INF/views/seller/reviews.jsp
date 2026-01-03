<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bookstore.models.Book" %>
<%@ page import="com.bookstore.models.Review" %>
<%
    List<Book> myBooks = (List<Book>) request.getAttribute("myBooks");
    List<Review> reviews = (List<Review>) request.getAttribute("reviews");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Book Reviews - Seller</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Poppins', sans-serif; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm fixed-top">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}"><i class="fas fa-book-reader text-primary"></i> BookNest</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}"><i class="fas fa-home"></i> Home</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/seller/dashboard"><i class="fas fa-store"></i> Dashboard</a></li>
                <li class="nav-item"><a class="nav-link active" href="#"><i class="fas fa-comments"></i> Reviews</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container py-4" style="margin-top: 72px;">
    <h3 class="mb-3">Reviews on My Books</h3>

    <% if (myBooks == null || myBooks.isEmpty()) { %>
        <div class="alert alert-info">You have not added any books yet.</div>
    <% } else { %>
        <% for (Book b : myBooks) { %>
            <div class="card mb-4">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <div>
                        <strong><%= b.getTitle() %></strong>
                        <span class="text-muted">by <%= b.getAuthor() %></span>
                    </div>
                    <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/seller/book-details?id=<%=b.getBookId()%>">View details</a>
                </div>
                <div class="card-body">
                    <% boolean hasReviews = false; %>
                    <% if (reviews != null) { %>
                        <% for (Review r : reviews) { %>
                            <% if (r.getBookId().equals(b.getBookId())) { hasReviews = true; %>
                                <div class="mb-3 border-bottom pb-2">
                                    <div class="d-flex justify-content-between">
                                        <div>
                                            <span class="badge bg-warning text-dark">★ <%= r.getRating() %>/5</span>
                                            <span class="ms-2"><strong><%= r.getUsername() %></strong></span>
                                            <% if (r.isVerifiedPurchase()) { %>
                                                <span class="badge bg-success ms-2">Verified purchase</span>
                                            <% } %>
                                        </div>
                                        <% if (r.getCreatedAt() != null) { %>
                                            <small class="text-muted"><%= r.getCreatedAt() %></small>
                                        <% } %>
                                    </div>
                                    <div class="mt-2"><%= r.getReviewText() == null ? "" : r.getReviewText() %></div>
                                </div>
                            <% } %>
                        <% } %>
                    <% } %>
                    <% if (!hasReviews) { %>
                        <div class="text-muted">No reviews yet for this book.</div>
                    <% } %>
                </div>
            </div>
        <% } %>
    <% } %>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

