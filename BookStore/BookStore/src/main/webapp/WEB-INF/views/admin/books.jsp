<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bookstore.models.User" %>
<%@ page import="com.bookstore.models.Book" %>
<%@ page import="com.bookstore.dao.CategoryDAO.Category" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Management - Admin Dashboard</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <!-- DataTables -->
    <link href="https://cdn.datatables.net/1.13.7/css/dataTables.bootstrap5.min.css" rel="stylesheet">
    <!-- Select2 -->
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/select2-bootstrap-5-theme@1.3.0/dist/select2-bootstrap-5-theme.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background-color: #f8f9fa;
        }
        /* Header theme unified with index.jsp (light navbar). Remove old blue overrides. */
        .page-header {
            background: linear-gradient(135deg, #0d6efd 0%, #0099ff 100%);
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        .card {
            border: none;
            box-shadow: 0 0.125rem 0.25rem rgba(0,0,0,.075);
            border-radius: 15px;
        }
        .card-header {
            background: white;
            border-bottom: 1px solid #e9ecef;
            border-radius: 15px 15px 0 0 !important;
            padding: 1.5rem;
        }
        .book-cover {
            width: 60px;
            height: 90px;
            object-fit: cover;
            border-radius: 6px;
        }
        .book-status {
            width: 85px;
        }
        .stock-input {
            width: 80px;
        }
        .category-select {
            min-width: 100px;
            max-width: 150px;
        }
        .input-group-sm .form-control,
        .input-group-sm .form-select {
            padding: 0.25rem 0.5rem;
            font-size: 0.875rem;
            border-radius: 0.25rem;
        }
        .input-group-sm > .btn {
            padding: 0.25rem 0.5rem;
            font-size: 0.875rem;
        }
        .action-buttons .btn {
            padding: 0.25rem 0.5rem;
            font-size: 0.875rem;
        }
        .select2-container--bootstrap-5 .select2-selection {
            border-radius: 10px;
        }
        .input-group .select2-container {
            width: 100% !important;
            flex: 1 1 auto;
            max-width: 150px;
        }
        .input-group .select2-container .select2-selection {
            border-top-right-radius: 0 !important;
            border-bottom-right-radius: 0 !important;
            border-right: 0 !important;
            height: 31px !important;
            min-height: 31px !important;
        }
        .input-group .select2-container .select2-selection .select2-selection__rendered {
            line-height: 29px !important;
            padding-left: 8px !important;
            padding-right: 8px !important;
            font-size: 0.875rem !important;
            white-space: nowrap !important;
            overflow: hidden !important;
            text-overflow: ellipsis !important;
        }
        .input-group .select2-container .select2-selection .select2-selection__arrow {
            height: 29px !important;
        }
        .filter-section {
            background: white;
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 2rem;
        }
        .table td {
            vertical-align: middle;
        }
        .table td .input-group {
            display: flex;
            align-items: center;
        }
        .table td:nth-child(3) {
            width: 200px;
            max-width: 200px;
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm fixed-top">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}">
                <i class="fas fa-book-reader text-primary me-2"></i> BookNest
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
                            <i class="fas fa-tachometer-alt me-1"></i>Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/admin/books">
                            <i class="fas fa-books me-1"></i>Books
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/users">
                            <i class="fas fa-users me-1"></i>Users
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/categories">
                            <i class="fas fa-tags me-1"></i>Categories
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt me-1"></i>Logout
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Page Header -->
    <header class="page-header" style="margin-top: 56px;">
        <div class="container">
            <h1 class="h3 mb-0">
                <i class="fas fa-books me-2"></i>Book Management
            </h1>
        </div>
    </header>

    <!-- Main Content -->
    <div class="container mb-5">
        <!-- Filters -->
        <div class="filter-section">
            <form method="get" class="row g-3 align-items-end">
                <div class="col-md-3">
                    <label class="form-label">Search</label>
                    <input type="text" class="form-control" name="q" value="<%= request.getAttribute("q") != null ? request.getAttribute("q") : "" %>" placeholder="Title, author, ISBN">
                </div>
                <div class="col-md-3">
                    <label class="form-label">Category</label>
                    <select class="form-select" name="categoryId">
                        <option value="">All</option>
                        <% for (Category category : (List<Category>)request.getAttribute("categories")) { %>
                            <option value="<%= category.getId() %>" <%= (request.getAttribute("selectedCategoryId") != null && category.getId().equals(request.getAttribute("selectedCategoryId"))) ? "selected" : "" %>><%= category.getName() %></option>
                        <% } %>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Min Price</label>
                    <input type="number" step="0.01" class="form-control" name="minPrice" value="<%= request.getAttribute("minPrice") != null ? request.getAttribute("minPrice") : "" %>">
                </div>
                <div class="col-md-2">
                    <label class="form-label">Max Price</label>
                    <input type="number" step="0.01" class="form-control" name="maxPrice" value="<%= request.getAttribute("maxPrice") != null ? request.getAttribute("maxPrice") : "" %>">
                </div>
                <div class="col-md-2">
                    <label class="form-label">Min Rating</label>
                    <input type="number" step="0.1" min="0" max="5" class="form-control" name="minRating" value="<%= request.getAttribute("minRating") != null ? request.getAttribute("minRating") : "" %>">
                </div>
                <div class="col-md-2">
                    <label class="form-label">Availability</label>
                    <select class="form-select" name="active">
                        <option value="">All</option>
                        <option value="true" <%= Boolean.TRUE.equals(request.getAttribute("active")) ? "selected" : "" %>>Active</option>
                        <option value="false" <%= Boolean.FALSE.equals(request.getAttribute("active")) ? "selected" : "" %>>Inactive</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary w-100"><i class="fas fa-search me-1"></i>Filter</button>
                </div>
            </form>
        </div>
        <!-- Alert Messages -->
        <% if (session.getAttribute("successMessage") != null) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>
                <%= session.getAttribute("successMessage") %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% session.removeAttribute("successMessage"); %>
        <% } %>
        <% if (session.getAttribute("errorMessage") != null) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i>
                <%= session.getAttribute("errorMessage") %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% session.removeAttribute("errorMessage"); %>
        <% } %>

        <!-- Books Table -->
        <div class="card">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle" id="booksTable">
                        <thead class="table-light">
                            <tr>
                                <th>Book</th>
                                <th>Seller</th>
                                <th>Category</th>
                                <th>Price</th>
                                <th>Stock</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                            List<Book> books = (List<Book>) request.getAttribute("books");
                            if (books != null && !books.isEmpty()) {
                                for (Book book : books) {
                            %>
                            <tr>
                                <td>
                                    <div class="d-flex align-items-center">
                                        <img src="${pageContext.request.contextPath}/book-covers/<%= book.getCoverImagePath() %>"
                                             alt="<%= book.getTitle() %>" class="book-cover me-3">
                                        <div>
                                            <strong><%= book.getTitle() %></strong><br>
                                            <small class="text-muted">
                                                by <%= book.getAuthor() %><br>
                                                ISBN: <%= book.getIsbn() %>
                                            </small>
                                        </div>
                                    </div>
                                </td>
                                <td><%= book.getSellerId() %></td>
                                <td>
                                    <form class="category-form" data-book-id="<%= book.getBookId() %>" onsubmit="return updateCategory(this)">
                                        <div class="input-group input-group-sm">
                                            <select class="form-select category-select" name="categoryId">
                                                <% for (Category category : (List<Category>)request.getAttribute("categories")) { %>
                                                    <option value="<%= category.getId() %>" 
                                                            <%= category.getId().equals(book.getCategoryId()) ? "selected" : "" %>>
                                                        <%= category.getName() %>
                                                    </option>
                                                <% } %>
                                            </select>
                                            <button type="submit" class="btn btn-outline-primary">
                                                <i class="fas fa-save"></i>
                                            </button>
                                        </div>
                                    </form>
                                </td>
                                <td>$<%= String.format("%.2f", book.getPrice()) %></td>
                                <td>
                                    <form class="stock-form" data-book-id="<%= book.getBookId() %>" onsubmit="return updateStock(this)">
                                        <div class="input-group input-group-sm">
                                            <input type="number" class="form-control stock-input"
                                                   value="<%= book.getStockQuantity() %>"
                                                   name="quantity"
                                                   min="0" required>
                                            <button type="submit" class="btn btn-outline-primary">
                                                <i class="fas fa-save"></i>
                                            </button>
                                        </div>
                                    </form>
                                </td>
                                <td>
                                    <div class="form-check form-switch">
                                        <input type="checkbox" class="form-check-input"
                                               id="active<%= book.getBookId() %>"
                                               <%= book.getMetadata() != null && book.getMetadata().isActive() ? "checked" : "" %>
                                               onchange="toggleActive('<%= book.getBookId() %>', this.checked)">
                                        <label class="form-check-label" for="active<%= book.getBookId() %>">
                                            <span class="badge <%= book.getMetadata() != null && book.getMetadata().isActive() ? "bg-success" : "bg-danger" %>">
                                                <%= book.getMetadata() != null && book.getMetadata().isActive() ? "Active" : "Inactive" %>
                                            </span>
                                        </label>
                                    </div>
                                </td>
                                <td>
                                    <div class="action-buttons">
                                        <button type="button" class="btn btn-danger btn-sm"
                                                data-book-id="<%= book.getBookId() %>"
                                                onclick="confirmDelete('<%= book.getBookId() %>')">
                                            <i class="fas fa-trash-alt"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                            <% 
                                }
                            } else {
                            %>
                            <tr>
                                <td colspan="7" class="text-center py-4">
                                    <div class="text-muted">
                                        <i class="fas fa-books fa-3x mb-3"></i>
                                        <h5>No books found</h5>
                                        <p>There are no books in the system yet.</p>
                                    </div>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Confirm Delete</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    Are you sure you want to delete this book? This action cannot be undone.
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-danger" onclick="deleteBook()">Delete</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.7/js/dataTables.bootstrap5.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    
    <script>
        let deleteBookId = null;
        const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));

        $(document).ready(function() {
            // Initialize DataTable
            $('#booksTable').DataTable({
                order: [[0, 'asc']], // Sort by title
                pageLength: 25,
                language: {
                    search: "Search books:"
                },
                columnDefs: [
                    { orderable: false, targets: [6] } // Disable sorting on Actions column
                ]
            });

            // Initialize Select2 for category dropdowns
            $('.category-select').select2({
                theme: 'bootstrap-5',
                width: '100%',
                dropdownParent: $('body'), // Ensure dropdown appears above other elements
                minimumResultsForSearch: Infinity, // Disable search in dropdown
                placeholder: 'Category',
                allowClear: false
            });
            
            // Ensure Select2 works properly with input-group
            $('.category-select').on('select2:open', function() {
                $(this).data('select2').$dropdown.addClass('select2-dropdown--above');
            });

            // Auto-hide alerts
            setTimeout(function() {
                $('.alert').alert('close');
            }, 5000);
        });

        function confirmDelete(bookId) {
            console.log('confirmDelete called with bookId:', bookId);
            deleteBookId = bookId;
            deleteModal.show();
        }

        function deleteBook() {
            console.log('deleteBook called, deleteBookId:', deleteBookId);
            if (deleteBookId) {
                console.log('Submitting delete form for bookId:', deleteBookId);
                submitForm('delete', { bookId: deleteBookId });
            } else {
                console.log('No deleteBookId set!');
            }
            deleteModal.hide();
        }

        function toggleActive(bookId, isActive) {
            console.log('toggleActive called with bookId:', bookId, 'isActive:', isActive);
            submitForm('toggleActive', { bookId: bookId, isActive: isActive });
        }

        function updateStock(form) {
            const bookId = form.getAttribute('data-book-id');
            const quantity = form.querySelector('input[name="quantity"]').value;
            submitForm('updateStock', { bookId: bookId, quantity: quantity });
            return false;
        }

        function updateCategory(form) {
            const bookId = form.getAttribute('data-book-id');
            const categoryId = form.querySelector('select[name="categoryId"]').value;
            submitForm('updateCategory', { bookId: bookId, categoryId: categoryId });
            return false;
        }

        function submitForm(action, data) {
            console.log('submitForm called with action:', action, 'data:', data);
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/admin/books';

            // Add action
            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = action;
            form.appendChild(actionInput);

            // Add other data
            Object.entries(data).forEach(([key, value]) => {
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = key;
                input.value = value;
                form.appendChild(input);
            });

            console.log('Form created, submitting to:', form.action);
            console.log('Form data:', new FormData(form));
            document.body.appendChild(form);
            form.submit();
        }
    </script>
</body>
</html>