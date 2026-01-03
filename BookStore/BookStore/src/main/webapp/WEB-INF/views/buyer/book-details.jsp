<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bookstore.models.User" %>
<%@ page import="com.bookstore.models.Book" %>
<%@ page import="com.bookstore.models.Review" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    Book book = (Book) request.getAttribute("book");
    Double averageRating = (Double) request.getAttribute("averageRating");
    Integer reviewCount = (Integer) request.getAttribute("reviewCount");
    Map<Integer, Integer> ratingDistribution = (Map<Integer, Integer>) request.getAttribute("ratingDistribution");
    List<Review> reviews = (List<Review>) request.getAttribute("reviews");
    Boolean hasReviewed = (Boolean) request.getAttribute("hasReviewed");
    Review userReview = (Review) request.getAttribute("userReview");
    Integer cartCount = (Integer) request.getAttribute("cartCount");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%=book.getTitle()%> - BookNest</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Poppins', sans-serif; background-color: #f8f9fa; }
        .book-cover { max-height: 420px; object-fit: contain; background: #fff; padding: 1rem; }
        .rating-stars { color: #ffc107; }
        .price { color: #198754; font-weight: 600; }
        .review-card { background: #fff; border: 1px solid #eee; border-radius: .5rem; padding: 1rem; }
    </style>
    <script>
        function postAction(action) {
            const form = document.getElementById('reviewForm');
            const fd = new URLSearchParams();
            fd.append('action', action);
            fd.append('bookId', '<%=book.getBookId()%>');
            if (action !== 'deleteReview') {
                fd.append('rating', document.getElementById('rating').value);
                fd.append('comment', document.getElementById('comment').value);
            }
            fetch('<%=request.getContextPath()%>/buyer/book/<%=book.getBookId()%>', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            , body: fd.toString() })
                .then(r => r.json())
                .then(_ => window.location.reload())
                .catch(_ => alert('Error submitting review'));
        }
    </script>
    <style>
        .star { color: #ffc107; }
    </style>
    <script>
        function renderStaticStars(avg) {
            const full = Math.floor(avg);
            const stars = [];
            for (let i = 1; i <= 5; i++) {
                stars.push('<i class="' + (i <= full ? 'fas' : 'far') + ' fa-star star"></i>');
            }
            return stars.join('');
        }
    </script>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}"><i class="fas fa-book-reader text-primary"></i> BookNest</a>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/buyer/books"><i class="fas fa-book"></i> Books</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/buyer/cart"><i class="fas fa-shopping-cart"></i> Cart <span class="badge bg-success"><%=cartCount != null ? cartCount : 0%></span></a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/buyer/dashboard"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
            </ul>
        </div>
    </div>
    </nav>

<div class="container py-5">
    <div class="row g-4">
        <div class="col-md-5">
            <img class="img-fluid book-cover" src="${pageContext.request.contextPath}/book-covers/<%=book.getCoverImagePath()%>" alt="<%=book.getTitle()%>">
        </div>
        <div class="col-md-7">
            <h2 class="mb-1"><%=book.getTitle()%></h2>
            <p class="text-muted mb-2">by <strong><%=book.getAuthor()%></strong></p>
            <div class="mb-2">
                <span id="avgStars"></span>
                <span class="ms-2 text-muted">(<%=reviewCount%> reviews)</span>
            </div>
            <div class="price h4 mb-3">$<%=String.format("%.2f", book.getPrice())%></div>
            <p><%=book.getDescription()%></p>
            <script>document.getElementById('avgStars').innerHTML = renderStaticStars(<%=averageRating != null ? averageRating : 0.0%>);</script>
        </div>
    </div>

    <div class="row mt-5">
        <div class="col-lg-6">
            <h4 class="mb-3">Ratings Breakdown</h4>
            <ul class="list-unstyled">
                <%
                    for (int i = 5; i >= 1; i--) {
                        Integer c = ratingDistribution != null ? ratingDistribution.get(i) : 0;
                %>
                <li class="d-flex align-items-center mb-1">
                    <span style="width:60px;"><%=i%> star</span>
                    <div class="progress flex-grow-1 mx-2" style="height:10px;">
                        <div class="progress-bar bg-warning" role="progressbar" style="width: <%=reviewCount != null && reviewCount > 0 ? (int)(c * 100.0 / reviewCount) : 0%>%"></div>
                    </div>
                    <span class="text-muted" style="width:40px; text-align:right;"><%=c%></span>
                </li>
                <% } %>
            </ul>
        </div>
        <div class="col-lg-6">
            <h4 class="mb-3"><%=hasReviewed != null && hasReviewed ? "Your Review" : "Write a Review"%></h4>
            <form id="reviewForm" onsubmit="return false;">
                <div class="mb-2">
                    <label class="form-label">Rating</label>
                    <select id="rating" class="form-select" required>
                        <option value="">Select</option>
                        <% for (int i=1;i<=5;i++) { %>
                            <option value="<%=i%>" <%= (userReview != null && userReview.getRating()==i) ? "selected" : "" %>><%=i%></option>
                        <% } %>
                    </select>
                </div>
                <div class="mb-3">
                    <label class="form-label">Review (optional)</label>
                    <textarea id="comment" class="form-control" rows="3"><%=userReview != null ? userReview.getReviewText() : ""%></textarea>
                </div>
                <div class="d-flex gap-2">
                    <% if (hasReviewed != null && hasReviewed) { %>
                        <button class="btn btn-primary" onclick="postAction('updateReview')">Update</button>
                        <button class="btn btn-outline-danger" onclick="postAction('deleteReview')">Delete</button>
                    <% } else { %>
                        <button class="btn btn-success" onclick="postAction('addReview')">Submit</button>
                    <% } %>
                </div>
            </form>
        </div>
    </div>

    <div class="mt-5">
        <h4 class="mb-3">Customer Reviews</h4>
        <div class="vstack gap-3">
            <% if (reviews != null && !reviews.isEmpty()) { %>
                <% for (Review r : reviews) { %>
                    <div class="review-card">
                        <div class="d-flex align-items-center justify-content-between">
                            <div>
                                <strong><%=r.getUsername()%></strong>
                                <span class="ms-2">
                                    <% for (int i=1;i<=5;i++){ %>
                                        <i class="fa<%= i <= r.getRating() ? 's' : 'r' %> fa-star star"></i>
                                    <% } %>
                                </span>
                            </div>
                            <small class="text-muted"><%= r.getCreatedAt() != null ? r.getCreatedAt().toString().replace('T',' ') : "" %></small>
                        </div>
                        <% if (r.getReviewText() != null && !r.getReviewText().trim().isEmpty()) { %>
                            <p class="mb-0 mt-2"><%=r.getReviewText()%></p>
                        <% } %>
                    </div>
                <% } %>
            <% } else { %>
                <div class="alert alert-info">No reviews yet. Be the first to review this book.</div>
            <% } %>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

