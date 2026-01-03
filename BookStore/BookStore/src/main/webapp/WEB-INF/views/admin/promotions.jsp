<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bookstore.models.DiscountCoupon" %>
<%@ page import="com.bookstore.models.Promotion" %>
<%@ page import="com.bookstore.models.Book" %>
<%
  com.bookstore.models.User current = (com.bookstore.models.User) session.getAttribute("user");
  if (current == null || !current.isAdmin()) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
  }
  List<DiscountCoupon> coupons = (List<DiscountCoupon>) request.getAttribute("coupons");
  List<Promotion> flashSales = (List<Promotion>) request.getAttribute("flashSales");
  List<Promotion> promotions = (List<Promotion>) request.getAttribute("promotions");
  List<Book> allBooks = (List<Book>) request.getAttribute("allBooks");
  String activeTab = (String) request.getAttribute("activeTab");
  if (activeTab == null || activeTab.isEmpty()) activeTab = "coupons";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Promotions Management - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Poppins', sans-serif; }
        .status-badge { font-size: 0.75rem; }
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
                <li class="nav-item"><a class="nav-link active" href="#"><i class="fas fa-percent"></i> Promotions</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container py-4" style="margin-top: 72px;">
    <!-- Error/Success Messages -->
    <% String errorMessage = (String) session.getAttribute("errorMessage"); %>
    <% String successMessage = (String) session.getAttribute("successMessage"); %>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i><%= errorMessage %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% session.removeAttribute("errorMessage"); %>
    <% } %>
    <% if (successMessage != null && !successMessage.isEmpty()) { %>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i><%= successMessage %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% session.removeAttribute("successMessage"); %>
    <% } %>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="mb-0">Promotions Management</h3>
        <div class="d-flex gap-2">
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createCouponModal">
                <i class="fas fa-plus me-1"></i>Create Coupon
            </button>
            <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#createFlashSaleModal">
                <i class="fas fa-bolt me-1"></i>Create Flash Sale
            </button>
        </div>
    </div>

    <ul class="nav nav-tabs mb-3" role="tablist">
        <li class="nav-item">
            <a class="nav-link <%= "coupons".equals(activeTab)?"active":"" %>" data-bs-toggle="tab" href="#coupons" role="tab">
                <i class="fas fa-ticket-alt me-1"></i>Discount Coupons
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link <%= "flash-sales".equals(activeTab)?"active":"" %>" data-bs-toggle="tab" href="#flash-sales" role="tab">
                <i class="fas fa-bolt me-1"></i>Flash Sales
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link <%= "promotions".equals(activeTab)?"active":"" %>" data-bs-toggle="tab" href="#promotions" role="tab">
                <i class="fas fa-gift me-1"></i>General Promotions
            </a>
        </li>
    </ul>

    <div class="tab-content">
        <!-- Discount Coupons Tab -->
        <div class="tab-pane fade <%= "coupons".equals(activeTab)?"show active":"" %>" id="coupons" role="tabpanel">
            <div class="table-responsive">
                <table class="table table-striped align-middle">
                    <thead>
                    <tr>
                        <th>Code</th>
                        <th>Description</th>
                        <th>Discount</th>
                        <th>Min Order</th>
                        <th>Usage</th>
                        <th>Valid Period</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% if (coupons != null) {
                        for (DiscountCoupon coupon : coupons) { %>
                        <tr>
                            <td><strong><%= coupon.getCode() %></strong></td>
                            <td><%= coupon.getDescription() != null ? coupon.getDescription() : "-" %></td>
                            <td><%= coupon.getFormattedDiscount() %></td>
                            <td>$<%= String.format("%.2f", coupon.getMinOrderAmount()) %></td>
                            <td>
                                <%= coupon.getUsedCount() %>
                                <% if (coupon.getUsageLimit() != null) { %>
                                    / <%= coupon.getUsageLimit() %>
                                <% } else { %>
                                    / ∞
                                <% } %>
                            </td>
                            <td>
                                <small>
                                    <%= coupon.getStartDate().toLocalDate() %><br>
                                    to <%= coupon.getEndDate().toLocalDate() %>
                                </small>
                            </td>
                            <td>
                                <% String status = coupon.getStatus(); %>
                                <span class="badge status-badge 
                                    <%= "ACTIVE".equals(status) ? "bg-success" : 
                                        "EXPIRED".equals(status) ? "bg-danger" :
                                        "UPCOMING".equals(status) ? "bg-info" :
                                        "INACTIVE".equals(status) ? "bg-secondary" : "bg-warning" %>">
                                    <%= status %>
                                </span>
                            </td>
                            <td>
                                <div class="d-flex gap-2">
                                    <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editCouponModal<%=coupon.getCouponId()%>">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <form class="d-inline" method="post" action="${pageContext.request.contextPath}/admin/promotions" onsubmit="return confirm('Delete this coupon?');">
                                        <input type="hidden" name="action" value="delete-coupon" />
                                        <input type="hidden" name="couponId" value="<%=coupon.getCouponId()%>" />
                                        <input type="hidden" name="tab" value="coupons" />
                                        <button type="submit" class="btn btn-sm btn-outline-danger">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>

                        <!-- Edit Coupon Modal -->
                        <div class="modal fade" id="editCouponModal<%=coupon.getCouponId()%>" tabindex="-1">
                            <div class="modal-dialog modal-lg">
                                <div class="modal-content">
                                    <form method="post" action="${pageContext.request.contextPath}/admin/promotions">
                                        <div class="modal-header">
                                            <h5 class="modal-title">Edit Coupon</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body">
                                            <input type="hidden" name="action" value="update-coupon" />
                                            <input type="hidden" name="couponId" value="<%=coupon.getCouponId()%>" />
                                            <input type="hidden" name="tab" value="coupons" />
                                            
                                            <div class="row">
                                                <div class="col-md-6 mb-3">
                                                    <label class="form-label">Coupon Code</label>
                                                    <input class="form-control" name="code" value="<%=coupon.getCode()%>" required />
                                                </div>
                                                <div class="col-md-6 mb-3">
                                                    <label class="form-label">Description</label>
                                                    <input class="form-control" name="description" value="<%=coupon.getDescription() != null ? coupon.getDescription() : ""%>" />
                                                </div>
                                            </div>
                                            
                                            <div class="row">
                                                <div class="col-md-6 mb-3">
                                                    <label class="form-label">Discount Type</label>
                                                    <select class="form-select" name="discountType" required>
                                                        <option value="PERCENTAGE" <%= "PERCENTAGE".equals(coupon.getDiscountType()) ? "selected" : "" %>>Percentage</option>
                                                        <option value="FIXED_AMOUNT" <%= "FIXED_AMOUNT".equals(coupon.getDiscountType()) ? "selected" : "" %>>Fixed Amount</option>
                                                    </select>
                                                </div>
                                                <div class="col-md-6 mb-3">
                                                    <label class="form-label">Discount Value</label>
                                                    <input class="form-control" name="discountValue" type="number" step="0.01" value="<%=coupon.getDiscountValue()%>" required />
                                                </div>
                                            </div>
                                            
                                            <div class="row">
                                                <div class="col-md-6 mb-3">
                                                    <label class="form-label">Minimum Order Amount</label>
                                                    <input class="form-control" name="minOrderAmount" type="number" step="0.01" value="<%=coupon.getMinOrderAmount()%>" required />
                                                </div>
                                                <div class="col-md-6 mb-3">
                                                    <label class="form-label">Max Discount Amount (Optional)</label>
                                                    <input class="form-control" name="maxDiscountAmount" type="number" step="0.01" value="<%=coupon.getMaxDiscountAmount() != null ? coupon.getMaxDiscountAmount() : ""%>" />
                                                </div>
                                            </div>
                                            
                                            <div class="row">
                                                <div class="col-md-6 mb-3">
                                                    <label class="form-label">Usage Limit (Optional)</label>
                                                    <input class="form-control" name="usageLimit" type="number" value="<%=coupon.getUsageLimit() != null ? coupon.getUsageLimit() : ""%>" />
                                                </div>
                                                <div class="col-md-6 mb-3">
                                                    <label class="form-label">Start Date</label>
                                                    <input class="form-control" name="startDate" type="date" value="<%=coupon.getStartDate().toLocalDate()%>" required />
                                                </div>
                                            </div>
                                            
                                            <div class="row">
                                                <div class="col-md-6 mb-3">
                                                    <label class="form-label">End Date</label>
                                                    <input class="form-control" name="endDate" type="date" value="<%=coupon.getEndDate().toLocalDate()%>" required />
                                                </div>
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                            <button type="submit" class="btn btn-primary">Update Coupon</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    <% } } %>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Flash Sales Tab -->
        <div class="tab-pane fade <%= "flash-sales".equals(activeTab)?"show active":"" %>" id="flash-sales" role="tabpanel">
            <div class="table-responsive">
                <table class="table table-striped align-middle">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Discount</th>
                        <th>Books</th>
                        <th>Valid Period</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% if (flashSales != null) {
                        for (Promotion sale : flashSales) { %>
                        <tr>
                            <td><strong><%= sale.getName() %></strong></td>
                            <td><%= sale.getDescription() != null ? sale.getDescription() : "-" %></td>
                            <td><%= sale.getFormattedDiscount() %></td>
                            <td>
                                <% List<Book> books = sale.getBooks(); %>
                                <% if (books != null && !books.isEmpty()) { %>
                                    <%= books.size() %> book(s)
                                <% } else { %>
                                    All books
                                <% } %>
                            </td>
                            <td>
                                <small>
                                    <%= sale.getStartDate().toLocalDate() %><br>
                                    to <%= sale.getEndDate().toLocalDate() %>
                                </small>
                            </td>
                            <td>
                                <% String status = sale.getStatus(); %>
                                <span class="badge status-badge 
                                    <%= "ACTIVE".equals(status) ? "bg-success" : 
                                        "EXPIRED".equals(status) ? "bg-danger" :
                                        "UPCOMING".equals(status) ? "bg-info" :
                                        "INACTIVE".equals(status) ? "bg-secondary" : "bg-warning" %>">
                                    <%= status %>
                                </span>
                            </td>
                            <td>
                                <div class="d-flex gap-2">
                                    <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editFlashSaleModal<%=sale.getPromotionId()%>">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <form class="d-inline" method="post" action="${pageContext.request.contextPath}/admin/promotions" onsubmit="return confirm('Delete this flash sale?');">
                                        <input type="hidden" name="action" value="delete-flash-sale" />
                                        <input type="hidden" name="promotionId" value="<%=sale.getPromotionId()%>" />
                                        <input type="hidden" name="tab" value="flash-sales" />
                                        <button type="submit" class="btn btn-sm btn-outline-danger">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    <% } } %>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- General Promotions Tab -->
        <div class="tab-pane fade <%= "promotions".equals(activeTab)?"show active":"" %>" id="promotions" role="tabpanel">
            <div class="table-responsive">
                <table class="table table-striped align-middle">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Discount</th>
                        <th>Min Order</th>
                        <th>Valid Period</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% if (promotions != null) {
                        for (Promotion promotion : promotions) { %>
                        <tr>
                            <td><strong><%= promotion.getName() %></strong></td>
                            <td><%= promotion.getDescription() != null ? promotion.getDescription() : "-" %></td>
                            <td><%= promotion.getFormattedDiscount() %></td>
                            <td>$<%= String.format("%.2f", promotion.getMinOrderAmount()) %></td>
                            <td>
                                <small>
                                    <%= promotion.getStartDate().toLocalDate() %><br>
                                    to <%= promotion.getEndDate().toLocalDate() %>
                                </small>
                            </td>
                            <td>
                                <% String status = promotion.getStatus(); %>
                                <span class="badge status-badge 
                                    <%= "ACTIVE".equals(status) ? "bg-success" : 
                                        "EXPIRED".equals(status) ? "bg-danger" :
                                        "UPCOMING".equals(status) ? "bg-info" :
                                        "INACTIVE".equals(status) ? "bg-secondary" : "bg-warning" %>">
                                    <%= status %>
                                </span>
                            </td>
                            <td>
                                <div class="d-flex gap-2">
                                    <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editPromotionModal<%=promotion.getPromotionId()%>">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <form class="d-inline" method="post" action="${pageContext.request.contextPath}/admin/promotions" onsubmit="return confirm('Delete this promotion?');">
                                        <input type="hidden" name="action" value="delete-promotion" />
                                        <input type="hidden" name="promotionId" value="<%=promotion.getPromotionId()%>" />
                                        <input type="hidden" name="tab" value="promotions" />
                                        <button type="submit" class="btn btn-sm btn-outline-danger">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    <% } } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Create Coupon Modal -->
<div class="modal fade" id="createCouponModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form method="post" action="${pageContext.request.contextPath}/admin/promotions">
                <div class="modal-header">
                    <h5 class="modal-title">Create Discount Coupon</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="action" value="create-coupon" />
                    <input type="hidden" name="tab" value="coupons" />
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Coupon Code</label>
                            <input class="form-control" name="code" placeholder="e.g., SAVE20" required />
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Description</label>
                            <input class="form-control" name="description" placeholder="Optional description" />
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Discount Type</label>
                            <select class="form-select" name="discountType" required>
                                <option value="">Select type</option>
                                <option value="PERCENTAGE">Percentage</option>
                                <option value="FIXED_AMOUNT">Fixed Amount</option>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Discount Value</label>
                            <input class="form-control" name="discountValue" type="number" step="0.01" placeholder="e.g., 20 or 5.00" required />
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Minimum Order Amount</label>
                            <input class="form-control" name="minOrderAmount" type="number" step="0.01" placeholder="e.g., 10.00" required />
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Max Discount Amount (Optional)</label>
                            <input class="form-control" name="maxDiscountAmount" type="number" step="0.01" placeholder="Leave empty for no limit" />
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Usage Limit (Optional)</label>
                            <input class="form-control" name="usageLimit" type="number" placeholder="Leave empty for unlimited" />
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Start Date</label>
                            <input class="form-control" name="startDate" type="date" required />
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">End Date</label>
                            <input class="form-control" name="endDate" type="date" required />
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Create Coupon</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Create Flash Sale Modal -->
<div class="modal fade" id="createFlashSaleModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form method="post" action="${pageContext.request.contextPath}/admin/promotions">
                <div class="modal-header">
                    <h5 class="modal-title">Create Flash Sale</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="action" value="create-flash-sale" />
                    <input type="hidden" name="tab" value="flash-sales" />
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Sale Name</label>
                            <input class="form-control" name="name" placeholder="e.g., Summer Flash Sale" required />
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Description</label>
                            <input class="form-control" name="description" placeholder="Optional description" />
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Discount Type</label>
                            <select class="form-select" name="discountType" required>
                                <option value="">Select type</option>
                                <option value="PERCENTAGE">Percentage</option>
                                <option value="FIXED_AMOUNT">Fixed Amount</option>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Discount Value</label>
                            <input class="form-control" name="discountValue" type="number" step="0.01" placeholder="e.g., 50 or 10.00" required />
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Minimum Order Amount</label>
                            <input class="form-control" name="minOrderAmount" type="number" step="0.01" placeholder="e.g., 10.00" required />
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Max Discount Amount (Optional)</label>
                            <input class="form-control" name="maxDiscountAmount" type="number" step="0.01" placeholder="Leave empty for no limit" />
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Usage Limit (Optional)</label>
                            <input class="form-control" name="usageLimit" type="number" placeholder="Leave empty for unlimited" />
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Start Date</label>
                            <input class="form-control" name="startDate" type="date" required />
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">End Date</label>
                            <input class="form-control" name="endDate" type="date" required />
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label">Select Books (Optional - Leave empty for all books)</label>
                        <select class="form-select" name="bookIds" multiple size="5">
                            <% if (allBooks != null) {
                                for (Book book : allBooks) { %>
                                    <option value="<%=book.getBookId()%>"><%=book.getTitle()%> by <%=book.getAuthor()%></option>
                                <% }
                            } %>
                        </select>
                        <small class="form-text text-muted">Hold Ctrl/Cmd to select multiple books</small>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success">Create Flash Sale</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
// Validation functions
function validateCouponForm(form) {
    const errors = [];
    console.log('Validating coupon form...');
    console.log('Form element:', form);
    
    // Validate coupon code
    const codeInput = form.querySelector('input[name="code"]');
    console.log('Code input found:', codeInput);
    if (!codeInput) {
        errors.push('Coupon code field not found');
        console.log('Errors so far:', errors);
        return errors;
    }
    
    const code = codeInput.value.trim();
    console.log('Code value:', code);
    if (!code) {
        errors.push('Coupon code is required');
    } else if (code.length < 3 || code.length > 20) {
        errors.push('Coupon code must be between 3 and 20 characters');
    } else if (!/^[A-Z0-9_-]+$/.test(code)) {
        errors.push('Coupon code can only contain uppercase letters, numbers, hyphens, and underscores');
    }
    console.log('Errors after code validation:', errors);
    
    // Validate discount type and value
    const discountTypeSelect = form.querySelector('select[name="discountType"]');
    const discountValueInput = form.querySelector('input[name="discountValue"]');
    
    if (!discountTypeSelect) {
        errors.push('Discount type field not found');
    } else {
        const discountType = discountTypeSelect.value;
        if (!discountType) {
            errors.push('Discount type is required');
        }
    }
    
    if (!discountValueInput) {
        errors.push('Discount value field not found');
    } else {
        const discountValueStr = discountValueInput.value.trim();
        if (!discountValueStr || discountValueStr === '') {
            errors.push('Discount value is required');
        } else {
            const discountValue = parseFloat(discountValueStr);
            if (isNaN(discountValue)) {
                errors.push('Discount value must be a valid number');
            } else if (discountValue <= 0) {
                errors.push('Discount value must be greater than 0');
            } else if (discountTypeSelect && discountTypeSelect.value === 'PERCENTAGE' && discountValue > 100) {
                errors.push('Percentage discount cannot exceed 100%');
            }
        }
    }
    // Validate minimum order amount
    const minOrderAmountInput = form.querySelector('input[name="minOrderAmount"]');
    if (!minOrderAmountInput) {
        errors.push('Minimum order amount field not found');
    } else {
        const minOrderAmountValue = minOrderAmountInput.value.trim();
        if (!minOrderAmountValue || minOrderAmountValue === '') {
            errors.push('Minimum order amount is required');
        } else {
            const minOrderAmount = parseFloat(minOrderAmountValue);
            if (isNaN(minOrderAmount)) {
                errors.push('Minimum order amount must be a valid number');
            } else if (minOrderAmount < 0) {
                errors.push('Minimum order amount cannot be negative');
            }
        }
    }
    
    // Validate max discount amount if provided
    const maxDiscountAmount = form.querySelector('input[name="maxDiscountAmount"]').value;
    if (maxDiscountAmount && maxDiscountAmount.trim() !== '') {
        const maxDiscount = parseFloat(maxDiscountAmount);
        if (isNaN(maxDiscount) || maxDiscount <= 0) {
            errors.push('Maximum discount amount must be a positive number');
        } else if (maxDiscount < discountValue) {
            errors.push('Maximum discount amount cannot be less than discount value');
        }
    }
    
    // Validate usage limit if provided
    const usageLimit = form.querySelector('input[name="usageLimit"]').value;
    if (usageLimit && usageLimit.trim() !== '') {
        const limit = parseInt(usageLimit);
        if (isNaN(limit) || limit <= 0) {
            errors.push('Usage limit must be a positive integer');
        }
    }
    
    // Validate dates
    const startDate = form.querySelector('input[name="startDate"]').value;
    const endDate = form.querySelector('input[name="endDate"]').value;
    
    if (!startDate) {
        errors.push('Start date is required');
    }
    if (!endDate) {
        errors.push('End date is required');
    }
    
    if (startDate && endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        
        if (start < today) {
            errors.push('Start date cannot be in the past');
        }
        if (end <= start) {
            errors.push('End date must be after start date');
        }
    }
    
    return errors;
}

function validateFlashSaleForm(form) {
    const errors = [];
    
    // Validate name
    const name = form.querySelector('input[name="name"]').value.trim();
    if (!name) {
        errors.push('Sale name is required');
    } else if (name.length < 3 || name.length > 100) {
        errors.push('Sale name must be between 3 and 100 characters');
    }
    
    // Validate discount type and value
    const discountType = form.querySelector('select[name="discountType"]').value;
    const discountValue = parseFloat(form.querySelector('input[name="discountValue"]').value);
    
    if (!discountType) {
        errors.push('Discount type is required');
    }
    
    if (isNaN(discountValue) || discountValue <= 0) {
        errors.push('Discount value must be a positive number');
    } else if (discountType === 'PERCENTAGE' && discountValue > 100) {
        errors.push('Percentage discount cannot exceed 100%');
    }
    
    // Validate minimum order amount
    const minOrderAmount = parseFloat(form.querySelector('input[name="minOrderAmount"]').value);
    if (isNaN(minOrderAmount) || minOrderAmount < 0) {
        errors.push('Minimum order amount must be a non-negative number');
    }
    
    // Validate max discount amount if provided
    const maxDiscountAmount = form.querySelector('input[name="maxDiscountAmount"]').value;
    if (maxDiscountAmount && maxDiscountAmount.trim() !== '') {
        const maxDiscount = parseFloat(maxDiscountAmount);
        if (isNaN(maxDiscount) || maxDiscount <= 0) {
            errors.push('Maximum discount amount must be a positive number');
        } else if (maxDiscount < discountValue) {
            errors.push('Maximum discount amount cannot be less than discount value');
        }
    }
    
    // Validate usage limit if provided
    const usageLimit = form.querySelector('input[name="usageLimit"]').value;
    if (usageLimit && usageLimit.trim() !== '') {
        const limit = parseInt(usageLimit);
        if (isNaN(limit) || limit <= 0) {
            errors.push('Usage limit must be a positive integer');
        }
    }
    
    // Validate dates
    const startDate = form.querySelector('input[name="startDate"]').value;
    const endDate = form.querySelector('input[name="endDate"]').value;
    
    if (!startDate) {
        errors.push('Start date is required');
    }
    if (!endDate) {
        errors.push('End date is required');
    }
    
    if (startDate && endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        
        if (start < today) {
            errors.push('Start date cannot be in the past');
        }
        if (end <= start) {
            errors.push('End date must be after start date');
        }
    }
    
    console.log('Final errors array:', errors);
    console.log('Error count:', errors.length);
    return errors;
}

function showValidationErrors(errors) {
    // Remove existing error alerts
    const existingAlerts = document.querySelectorAll('.validation-error, .validation-error-temp');
    existingAlerts.forEach(alert => alert.remove());
    
    if (errors && errors.length > 0) {
        console.log('Showing validation errors:', errors);
        
        // Create error message with line breaks for better readability
        const errorMessage = errors.join('<br>');
        console.log('Error message to display:', errorMessage);
        
        // Create a single alert div
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-danger validation-error';
        alertDiv.style.cssText = 'margin-bottom: 20px; padding: 15px; border: 1px solid #f5c6cb; background-color: #f8d7da; color: #721c24; border-radius: 5px; font-weight: 500;';
        alertDiv.innerHTML = `<strong><i class="fas fa-exclamation-triangle me-2"></i>Please fix the following errors:</strong><br>${errorMessage}`;
        
        // Try to find modal body with a small delay to ensure modal is fully loaded
        setTimeout(() => {
            const modalBody = document.querySelector('.modal.show .modal-body');
            if (modalBody) {
                console.log('Inserting into modal body');
                modalBody.insertBefore(alertDiv, modalBody.firstChild);
                modalBody.scrollTop = 0;
            } else {
                // Fallback: show at top of page
                const container = document.querySelector('.container');
                if (container) {
                    console.log('Inserting into container');
                    container.insertBefore(alertDiv, container.firstChild);
                } else {
                    // Last resort: append to body
                    console.log('Appending to body');
                    document.body.appendChild(alertDiv);
                }
            }
        }, 100);
    } else {
        console.log('No errors to display');
    }
}

// Add form validation to all forms
document.addEventListener('DOMContentLoaded', function() {
    // Set today's date as minimum for date inputs
    const today = new Date().toISOString().split('T')[0];
    document.querySelectorAll('input[type="date"]').forEach(input => {
        input.setAttribute('min', today);
    });
    
    // Add validation to create coupon form
    const createCouponForm = document.querySelector('#createCouponModal form');
    console.log('Create coupon form found:', createCouponForm);
    if (createCouponForm) {
        createCouponForm.addEventListener('submit', function(e) {
            console.log('Form submit event triggered');
            e.preventDefault();
            
            // Clear previous errors
            const existingAlerts = document.querySelectorAll('.validation-error, .validation-error-temp');
            existingAlerts.forEach(alert => alert.remove());
            
            const errors = validateCouponForm(this);
            console.log('Coupon validation errors:', errors);
            if (errors.length > 0) {
                console.log('Calling showValidationErrors with:', errors);
                showValidationErrors(errors);
            } else {
                console.log('No errors, submitting form');
                // Remove any existing error alerts before submitting
                const existingAlerts = document.querySelectorAll('.validation-error, .validation-error-temp');
                existingAlerts.forEach(alert => alert.remove());
                this.submit();
            }
        });
    } else {
        console.log('Create coupon form not found!');
    }
    
    // Add validation to create flash sale form
    const createFlashSaleForm = document.querySelector('#createFlashSaleModal form');
    if (createFlashSaleForm) {
        createFlashSaleForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Clear previous errors
            const existingAlerts = document.querySelectorAll('.validation-error, .validation-error-temp');
            existingAlerts.forEach(alert => alert.remove());
            
            const errors = validateFlashSaleForm(this);
            console.log('Flash sale validation errors:', errors);
            if (errors.length > 0) {
                showValidationErrors(errors);
            } else {
                // Remove any existing error alerts before submitting
                const existingAlerts = document.querySelectorAll('.validation-error, .validation-error-temp');
                existingAlerts.forEach(alert => alert.remove());
                this.submit();
            }
        });
    }
    
    // Add validation to edit forms
    document.querySelectorAll('form[id^="editCouponModal"]').forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Clear previous errors
            const existingAlerts = document.querySelectorAll('.validation-error, .validation-error-temp');
            existingAlerts.forEach(alert => alert.remove());
            
            const errors = validateCouponForm(this);
            if (errors.length > 0) {
                showValidationErrors(errors);
            } else {
                // Remove any existing error alerts before submitting
                const existingAlerts = document.querySelectorAll('.validation-error, .validation-error-temp');
                existingAlerts.forEach(alert => alert.remove());
                this.submit();
            }
        });
    });
    
    // Clear errors when modals are opened
    document.querySelectorAll('[data-bs-toggle="modal"]').forEach(button => {
        button.addEventListener('click', function() {
            const existingAlerts = document.querySelectorAll('.validation-error, .validation-error-temp');
            existingAlerts.forEach(alert => alert.remove());
        });
    });
    
    // Real-time validation for discount value
    document.querySelectorAll('input[name="discountValue"]').forEach(input => {
        input.addEventListener('input', function() {
            const discountType = this.closest('form').querySelector('select[name="discountType"]').value;
            const value = parseFloat(this.value);
            
            if (discountType === 'PERCENTAGE' && value > 100) {
                this.setCustomValidity('Percentage discount cannot exceed 100%');
            } else {
                this.setCustomValidity('');
            }
        });
    });
    
    // Real-time validation for dates
    document.querySelectorAll('input[name="startDate"]').forEach(input => {
        input.addEventListener('change', function() {
            const endDateInput = this.closest('form').querySelector('input[name="endDate"]');
            if (endDateInput.value && new Date(this.value) >= new Date(endDateInput.value)) {
                endDateInput.setCustomValidity('End date must be after start date');
            } else {
                endDateInput.setCustomValidity('');
            }
        });
    });
    
    document.querySelectorAll('input[name="endDate"]').forEach(input => {
        input.addEventListener('change', function() {
            const startDateInput = this.closest('form').querySelector('input[name="startDate"]');
            if (startDateInput.value && new Date(this.value) <= new Date(startDateInput.value)) {
                this.setCustomValidity('End date must be after start date');
            } else {
                this.setCustomValidity('');
            }
        });
    });
    
    // Test function for debugging - can be called from browser console
    window.testErrorDisplay = function() {
        console.log('Testing error display...');
        const testErrors = ['Test error message 1', 'Test error message 2'];
        showValidationErrors(testErrors);
    };
    
    // Test validation function
    window.testValidation = function() {
        console.log('Testing validation...');
        const form = document.querySelector('#createCouponModal form');
        if (form) {
            console.log('Form found, testing validation...');
            const errors = validateCouponForm(form);
            console.log('Validation errors:', errors);
            showValidationErrors(errors);
        } else {
            console.log('Form not found!');
        }
    };
});
</script>
</body>
</html>
