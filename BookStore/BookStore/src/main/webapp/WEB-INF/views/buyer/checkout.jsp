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
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout - BookNest</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}">
                <i class="fas fa-book-reader text-primary"></i> BookNest
            </a>
        </div>
    </nav>

    <div class="container py-5">
        <div class="row">
            <div class="col-lg-8">
                <div class="card mb-4">
                    <div class="card-body">
                        <h5 class="card-title mb-3">Shipping Address</h5>
                        <% if (request.getAttribute("errorMessage") != null) { %>
                            <div class="alert alert-danger"><%= request.getAttribute("errorMessage") %></div>
                        <% } %>
                        <form method="post">
                            <div class="mb-3">
                                <textarea class="form-control" name="shippingAddress" rows="4" required><%= user.getAddress() != null ? user.getAddress() : "" %></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary">Place Order</button>
                        </form>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Order Summary</h5>
                        <ul class="list-group list-group-flush mb-3">
                            <% for (CartItem ci : cartItems) { %>
                                <li class="list-group-item d-flex justify-content-between align-items-center">
                                    <span><%= ci.getTitle() %> x <%= ci.getQuantity() %></span>
                                    <span>$<%= String.format("%.2f", ci.getPrice() * ci.getQuantity()) %></span>
                                </li>
                            <% } %>
                        </ul>
                        <div class="d-flex justify-content-between">
                            <span>Total</span>
                            <strong>$<%= String.format("%.2f", cartTotal) %></strong>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>


