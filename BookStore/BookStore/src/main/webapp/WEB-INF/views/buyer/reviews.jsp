<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bookstore.models.Review" %>
<%
    List<Review> reviews = (List<Review>) request.getAttribute("reviews");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Reviews - BookNest</title>
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
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/buyer/dashboard"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
                <li class="nav-item"><a class="nav-link active" href="#"><i class="fas fa-comments"></i> My Reviews</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container py-4" style="margin-top: 72px;">
    <h3 class="mb-3">My Reviews</h3>
    <div class="list-group">
        <% if (reviews != null && !reviews.isEmpty()) { %>
            <% for (Review r : reviews) { %>
                <a class="list-group-item list-group-item-action" href="${pageContext.request.contextPath}/buyer/book/<%=r.getBookId()%>">
                    <div class="d-flex w-100 justify-content-between">
                        <h5 class="mb-1"><%= r.getBookTitle() != null ? r.getBookTitle() : ("Book #" + r.getBookId()) %></h5>
                        <small class="text-muted"><%= r.getCreatedAt() != null ? r.getCreatedAt().toString().replace('T',' ') : "" %></small>
                    </div>
                    <div class="mb-1">
                        <% for (int i=1;i<=5;i++){ %>
                            <i class="fa<%= i <= r.getRating() ? 's' : 'r' %> fa-star text-warning"></i>
                        <% } %>
                    </div>
                    <% if (r.getReviewText() != null && !r.getReviewText().trim().isEmpty()) { %>
                        <p class="mb-1"><%= r.getReviewText() %></p>
                    <% } %>
                </a>
            <% } %>
        <% } else { %>
            <div class="alert alert-info">You haven't posted any reviews yet.</div>
        <% } %>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

