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
    <title>Manage Reviews - Admin</title>
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
        <a class="navbar-brand" href="${pageContext.request.contextPath}"><i class="fas fa-book-reader text-primary"></i> BookNest Admin</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
                <li class="nav-item"><a class="nav-link active" href="#"><i class="fas fa-comments"></i> Reviews</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container py-4" style="margin-top: 72px;">
    <h3 class="mb-3">All Reviews</h3>
    <table class="table table-striped">
        <thead>
        <tr>
            <th>Book</th>
            <th>User</th>
            <th>Rating</th>
            <th>Review</th>
            <th>Created</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <% if (reviews != null) { for (Review r : reviews) { %>
            <tr>
                <td><%= r.getBookTitle() != null ? r.getBookTitle() : ("#" + r.getBookId()) %></td>
                <td><%= r.getUsername() %></td>
                <td><%= r.getRating() %></td>
                <td style="max-width:360px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;"><%= r.getReviewText() != null ? r.getReviewText() : "" %></td>
                <td><%= r.getCreatedAt() != null ? r.getCreatedAt().toString().replace('T',' ') : "" %></td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/admin/reviews" onsubmit="return confirm('Delete this review?');">
                        <input type="hidden" name="action" value="delete"/>
                        <input type="hidden" name="reviewId" value="<%=r.getReviewId()%>"/>
                        <button class="btn btn-sm btn-outline-danger">Delete</button>
                    </form>
                </td>
            </tr>
        <% } } %>
        </tbody>
    </table>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

