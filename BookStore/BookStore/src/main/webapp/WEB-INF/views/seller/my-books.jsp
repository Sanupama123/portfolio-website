<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bookstore.models.User" %>
<%@ page import="com.bookstore.models.Book" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Books - BookNest</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <!-- DataTables -->
    <link href="https://cdn.datatables.net/1.13.7/css/dataTables.bootstrap5.min.css" rel="stylesheet">
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
            width: 80px;
            height: 120px;
            object-fit: cover;
            border-radius: 8px;
        }
        .book-status {
            width: 85px;
        }
        .stock-input {
            width: 80px;
        }
        .action-buttons .btn {
            padding: 0.25rem 0.5rem;
            font-size: 0.875rem;
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/seller/dashboard">
                            <i class="fas fa-tachometer-alt me-1"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/seller/my-books">
                            <i class="fas fa-books me-1"></i> My Books
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/seller/orders">
                            <i class="fas fa-shopping-cart me-1"></i> Orders
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/profile">
                            <i class="fas fa-user me-1"></i> Profile
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt me-1"></i> Logout
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Page Header -->
    <header class="page-header" style="margin-top: 56px;">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h1 class="h3 mb-0">
                        <i class="fas fa-books me-2"></i>My Books
                    </h1>
                </div>
                <div class="col-md-4 text-md-end">
                    <a href="${pageContext.request.contextPath}/seller/add-book" class="btn btn-light">
                        <i class="fas fa-plus-circle me-2"></i>Add New Book
                    </a>
                </div>
            </div>
        </div>
    </header>

    <!-- Main Content -->
    <div class="container mb-5">
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
                    <table class="table table-hover" id="booksTable">
                        <thead class="table-light">
                            <tr>
                                <th>Cover</th>
                                <th>Title</th>
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
                                    <img src="${pageContext.request.contextPath}/book-covers/<%= book.getCoverImagePath() %>"
                                         alt="<%= book.getTitle() %>" class="book-cover">
                                </td>
                                <td>
                                    <strong><a href="${pageContext.request.contextPath}/seller/book-details?id=<%= book.getBookId() %>" class="text-decoration-none">
                                        <%= book.getTitle() %>
                                    </a></strong><br>
                                    <small class="text-muted">by <%= book.getAuthor() %></small>
                                </td>
                                <td><%= book.getCategoryName() %></td>
                                <td>$<%= String.format("%.2f", book.getPrice()) %></td>
                                <td>
                                    <form class="stock-form" data-book-id="<%= book.getBookId() %>" onsubmit="return updateStock(this)">
                                        <div class="input-group input-group-sm">
                                            <input type="number" class="form-control stock-input" 
                                                   name="quantity"
                                                   value="<%= book.getStockQuantity() %>" 
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
                                        <label class="form-check-label book-status" for="active<%= book.getBookId() %>">
                                            <span class="badge <%= book.getMetadata() != null && book.getMetadata().isActive() ? "bg-success" : "bg-danger" %>">
                                                <%= book.getMetadata() != null && book.getMetadata().isActive() ? "Active" : "Inactive" %>
                                            </span>
                                        </label>
                                    </div>
                                </td>
                                <td>
                                    <div class="action-buttons">
                                        <a href="${pageContext.request.contextPath}/seller/book-details?id=<%= book.getBookId() %>"
                                           class="btn btn-primary btn-sm">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <button type="button" class="btn btn-danger btn-sm" 
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
                                        <p>Start by adding your first book!</p>
                                        <a href="${pageContext.request.contextPath}/seller/add-book" 
                                           class="btn btn-primary">
                                            <i class="fas fa-plus-circle me-2"></i>Add New Book
                                        </a>
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
    
    <script>
        let deleteBookId = null;
        const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));

        $(document).ready(function() {
            // Initialize DataTable
            $('#booksTable').DataTable({
                order: [[1, 'asc']], // Sort by title
                pageLength: 10,
                language: {
                    search: "Search books:"
                }
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
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/seller/my-books';

                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'delete';

                const bookIdInput = document.createElement('input');
                bookIdInput.type = 'hidden';
                bookIdInput.name = 'bookId';
                bookIdInput.value = deleteBookId;

                form.appendChild(actionInput);
                form.appendChild(bookIdInput);
                document.body.appendChild(form);
                form.submit();
            } else {
                console.log('No deleteBookId set!');
            }
            deleteModal.hide();
        }

        function toggleActive(bookId, isActive) {
            console.log('toggleActive called with bookId:', bookId, 'isActive:', isActive);
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/seller/my-books';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'toggleActive';

            const bookIdInput = document.createElement('input');
            bookIdInput.type = 'hidden';
            bookIdInput.name = 'bookId';
            bookIdInput.value = bookId;

            const isActiveInput = document.createElement('input');
            isActiveInput.type = 'hidden';
            isActiveInput.name = 'isActive';
            isActiveInput.value = isActive;

            form.appendChild(actionInput);
            form.appendChild(bookIdInput);
            form.appendChild(isActiveInput);
            document.body.appendChild(form);
            form.submit();
        }

        function updateStock(form) {
            const quantity = form.querySelector('input[name="quantity"]').value;
            const bookId = form.getAttribute('data-book-id');
            
            const submitForm = document.createElement('form');
            submitForm.method = 'POST';
            submitForm.action = '${pageContext.request.contextPath}/seller/my-books';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'updateStock';

            const bookIdInput = document.createElement('input');
            bookIdInput.type = 'hidden';
            bookIdInput.name = 'bookId';
            bookIdInput.value = bookId;

            const quantityInput = document.createElement('input');
            quantityInput.type = 'hidden';
            quantityInput.name = 'quantity';
            quantityInput.value = quantity;

            submitForm.appendChild(actionInput);
            submitForm.appendChild(bookIdInput);
            submitForm.appendChild(quantityInput);
            document.body.appendChild(submitForm);
            submitForm.submit();

            return false;
        }
    </script>
</body>
</html>