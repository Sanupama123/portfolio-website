<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bookstore.models.Book" %>
<%@ page import="com.bookstore.dao.CategoryDAO.Category" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Book Details - BookNest</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            min-height: 100vh;
        }
        
        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 3rem 0;
            margin-bottom: 2rem;
            position: relative;
            overflow: hidden;
        }
        
        .page-header::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="1" fill="white" opacity="0.1"/><circle cx="10" cy="90" r="1" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
        }
        
        .card {
            border: none;
            box-shadow: 0 20px 40px rgba(0,0,0,.1);
            border-radius: 20px;
            backdrop-filter: blur(10px);
            background: rgba(255,255,255,0.95);
            overflow: hidden;
        }
        
        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 20px 20px 0 0 !important;
            padding: 2rem;
            border: none;
        }
        
        .form-control, .form-select {
            border-radius: 15px;
            padding: 1rem 1.25rem;
            border: 2px solid #e9ecef;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            background: rgba(255,255,255,0.9);
            backdrop-filter: blur(10px);
        }
        
        .form-control:focus, .form-select:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.25rem rgba(102,126,234,.15);
            transform: translateY(-2px);
        }
        
        .form-control:read-only {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-color: #ced4da;
            cursor: not-allowed;
        }
        
        .form-label {
            font-weight: 600;
            color: #495057;
            margin-bottom: 0.75rem;
            font-size: 0.95rem;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            padding: 1rem 2rem;
            border-radius: 15px;
            font-weight: 600;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            box-shadow: 0 8px 25px rgba(102,126,234,.3);
        }
        
        .btn-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 15px 35px rgba(102,126,234,.4);
        }
        
        .btn-danger {
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
            border: none;
            padding: 1rem 2rem;
            border-radius: 15px;
            font-weight: 600;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            box-shadow: 0 8px 25px rgba(255,107,107,.3);
        }
        
        .btn-danger:hover {
            transform: translateY(-3px);
            box-shadow: 0 15px 35px rgba(255,107,107,.4);
        }
        
        .btn-outline-secondary {
            border: 2px solid #6c757d;
            border-radius: 15px;
            padding: 1rem 2rem;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        
        .book-cover {
            max-width: 100%;
            height: auto;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0,0,0,.2);
            transition: all 0.3s ease;
        }
        
        .book-cover:hover {
            transform: scale(1.05);
        }
        
        .image-upload-container {
            position: relative;
            border: 3px dashed #dee2e6;
            border-radius: 15px;
            padding: 2rem;
            text-align: center;
            transition: all 0.3s ease;
            background: rgba(248,249,250,0.8);
            margin-top: 1rem;
        }
        
        .image-upload-container:hover {
            border-color: #667eea;
            background: rgba(102,126,234,0.05);
        }
        
        .image-upload-container.has-image {
            border-color: #28a745;
            background: rgba(40,167,69,0.05);
        }
        
        .preview-container {
            position: relative;
            display: none;
            margin-top: 1rem;
        }
        
        .preview-image {
            max-width: 200px;
            max-height: 250px;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,.2);
        }
        
        .remove-image-btn {
            position: absolute;
            top: -10px;
            right: -10px;
            width: 35px;
            height: 35px;
            border-radius: 50%;
            background: #dc3545;
            color: white;
            border: none;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(220,53,69,.3);
        }
        
        .remove-image-btn:hover {
            background: #c82333;
            transform: scale(1.1);
        }
        
        .invalid-feedback {
            font-size: 0.875rem;
            margin-top: 0.5rem;
        }
        
        .alert {
            border-radius: 15px;
            border: none;
            padding: 1.25rem 1.5rem;
            margin-bottom: 2rem;
        }
        
        .alert-danger {
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
            color: white;
        }
        
        .alert-success {
            background: linear-gradient(135deg, #51cf66 0%, #40c057 100%);
            color: white;
        }
        
        .navbar {
            backdrop-filter: blur(10px);
            background: rgba(255,255,255,0.95) !important;
        }
        
        .required-asterisk {
            color: #dc3545;
            font-weight: bold;
        }
        
        .readonly-badge {
            background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
            color: white;
            font-size: 0.75rem;
            padding: 0.25rem 0.5rem;
            border-radius: 8px;
            margin-left: 0.5rem;
        }
        
        @media (max-width: 768px) {
            .page-header {
                padding: 2rem 0;
            }
            
            .card {
                margin: 0 1rem;
            }
            
            .book-cover {
                max-width: 200px;
            }
        }
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-light fixed-top">
        <div class="container">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}">
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
                            <i class="fas fa-book me-1"></i> My Books
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/seller/add-book">
                            <i class="fas fa-plus-circle me-1"></i> Add Book
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
    <header class="page-header" style="margin-top: 76px;">
        <div class="container position-relative">
            <div class="row align-items-center">
                <div class="col-lg-8">
                    <h1 class="display-5 fw-bold mb-3">
                        <i class="fas fa-edit me-3"></i>Edit Book Details
                    </h1>
                    <p class="lead mb-0">Update your book information and keep your listing current</p>
                </div>
                <div class="col-lg-4 text-end d-none d-lg-block">
                    <i class="fas fa-book-open" style="font-size: 8rem; opacity: 0.1;"></i>
                </div>
            </div>
        </div>
    </header>

    <!-- Page Content -->
    <div class="container mb-5">
        <!-- Alert Messages -->
        <% if (session.getAttribute("successMessage") != null) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>
                <%= session.getAttribute("successMessage") %>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert"></button>
            </div>
            <% session.removeAttribute("successMessage"); %>
        <% } %>
        
        <% if (session.getAttribute("errorMessage") != null) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                <%= session.getAttribute("errorMessage") %>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert"></button>
            </div>
            <% session.removeAttribute("errorMessage"); %>
        <% } %>

        <% Book book = (Book) request.getAttribute("book"); %>
        
        <!-- Book Details Form -->
        <div class="card">
            <div class="card-header">
                <h3 class="mb-0">
                    <i class="fas fa-info-circle me-2"></i>Book Information
                </h3>
                <p class="mb-0 mt-2 opacity-75">Update your book details below</p>
            </div>
            <div class="card-body p-4">
                <form action="${pageContext.request.contextPath}/seller/book-details" method="POST" 
                      class="needs-validation" enctype="multipart/form-data" novalidate>
                    <input type="hidden" name="bookId" value="<%= book.getBookId() %>">
                    
                    <div class="row g-4">
                        <!-- Book Cover Section -->
                        <div class="col-lg-4">
                            <div class="text-center">
                                <label class="form-label d-block fw-bold">Current Book Cover</label>
                                <img src="${pageContext.request.contextPath}/book-covers/<%= book.getCoverImagePath() %>"
                                     alt="<%= book.getTitle() %>" class="book-cover mb-3">
                                
                                <!-- Change Cover Image -->
                                <div class="image-upload-container" id="imageUploadContainer">
                                    <input type="file" class="form-control d-none" id="coverImage" name="coverImage"
                                           accept="image/jpeg,image/png,image/gif">
                                    <div id="uploadPrompt">
                                        <i class="fas fa-camera fa-2x text-muted mb-2"></i>
                                        <h6>Click to change cover</h6>
                                        <small class="text-muted">Max 5MB • JPG, PNG, GIF</small>
                                    </div>
                                    <div class="preview-container" id="previewContainer">
                                        <img id="imagePreview" src="#" alt="New cover preview" class="preview-image">
                                        <button type="button" class="remove-image-btn" id="removeImageBtn">
                                            <i class="fas fa-times"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-8">
                            <div class="row g-3">
                                <!-- Title -->
                                <div class="col-12">
                                    <label for="title" class="form-label">Book Title <span class="required-asterisk">*</span></label>
                                    <input type="text" class="form-control" id="title" name="title"
                                           value="<%= book.getTitle() %>" required>
                                    <div class="invalid-feedback">Please enter a title.</div>
                                </div>

                                <!-- Author -->
                                <div class="col-md-6">
                                    <label for="author" class="form-label">Author <span class="required-asterisk">*</span></label>
                                    <input type="text" class="form-control" id="author" name="author"
                                           value="<%= book.getAuthor() %>" required>
                                    <div class="invalid-feedback">Please enter an author.</div>
                                </div>

                                <!-- ISBN - Read Only -->
                                <div class="col-md-6">
                                    <label for="isbn" class="form-label">
                                        ISBN 
                                        <span class="readonly-badge">
                                            <i class="fas fa-lock me-1"></i>Read Only
                                        </span>
                                    </label>
                                    <input type="text" class="form-control" id="isbn" name="isbn"
                                           value="<%= book.getIsbn() %>" readonly>
                                    <div class="form-text">
                                        <i class="fas fa-info-circle me-1"></i>
                                        ISBN cannot be changed after book creation for data integrity
                                    </div>
                                </div>

                                <!-- Price -->
                                <div class="col-md-6">
                                    <label for="price" class="form-label">Price ($) <span class="required-asterisk">*</span></label>
                                    <input type="number" step="0.01" class="form-control" id="price" name="price"
                                           value="<%= book.getPrice() %>" required min="0">
                                    <div class="invalid-feedback">Please enter a valid price.</div>
                                </div>

                                <!-- Stock Quantity -->
                                <div class="col-md-6">
                                    <label for="stockQuantity" class="form-label">Stock Quantity <span class="required-asterisk">*</span></label>
                                    <input type="number" class="form-control" id="stockQuantity" name="stockQuantity"
                                           value="<%= book.getStockQuantity() %>" required min="0">
                                    <div class="invalid-feedback">Please enter the stock quantity.</div>
                                </div>

                                <!-- Published Year - Enhanced Dropdown -->
                                <div class="col-md-6">
                                    <label for="publishedYear" class="form-label">Published Year <span class="required-asterisk">*</span></label>
                                    <select class="form-select" id="publishedYear" name="publishedYear" required>
                                        <option value="">Select published year</option>
                                        <%
                                            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                                            int bookYear = book.getPublishedYear();
                                            for (int year = currentYear; year >= 1980; year--) {
                                        %>
                                            <option value="<%= year %>" <%= year == bookYear ? "selected" : "" %>><%= year %></option>
                                        <% } %>
                                    </select>
                                    <div class="invalid-feedback">Please select the published year.</div>
                                </div>

                                <!-- Publisher -->
                                <div class="col-md-6">
                                    <label for="publisher" class="form-label">Publisher <span class="required-asterisk">*</span></label>
                                    <input type="text" class="form-control" id="publisher" name="publisher"
                                           value="<%= book.getPublisher() %>" required>
                                    <div class="invalid-feedback">Please enter a publisher.</div>
                                </div>

                                <!-- Category -->
                                <div class="col-md-6">
                                    <label for="categoryId" class="form-label">Category <span class="required-asterisk">*</span></label>
                                    <select class="form-select" id="categoryId" name="categoryId" required>
                                        <% for (Category category : (List<Category>)request.getAttribute("categories")) { %>
                                            <option value="<%= category.getId() %>"
                                                    <%= category.getId().equals(book.getCategoryId()) ? "selected" : "" %>>
                                                <%= category.getName() %>
                                            </option>
                                        <% } %>
                                    </select>
                                    <div class="invalid-feedback">Please select a category.</div>
                                </div>

                                <!-- Language -->
                                <div class="col-md-6">
                                    <label for="language" class="form-label">Language <span class="required-asterisk">*</span></label>
                                    <select class="form-select" id="language" name="language" required>
                                        <% String[] languages = {"English", "Spanish", "French", "German", "Chinese", "Japanese", "Arabic", "Portuguese", "Russian", "Italian"}; %>
                                        <% for (String lang : languages) { %>
                                            <option value="<%= lang %>" <%= lang.equals(book.getLanguage()) ? "selected" : "" %>>
                                                <%= lang %>
                                            </option>
                                        <% } %>
                                    </select>
                                    <div class="invalid-feedback">Please select a language.</div>
                                </div>

                                <!-- Page Count -->
                                <div class="col-md-6">
                                    <label for="pageCount" class="form-label">Page Count <span class="required-asterisk">*</span></label>
                                    <input type="number" class="form-control" id="pageCount" name="pageCount"
                                           value="<%= book.getPageCount() %>" required min="1">
                                    <div class="invalid-feedback">Please enter the page count.</div>
                                </div>

                                <!-- Description -->
                                <div class="col-12">
                                    <label for="description" class="form-label">Description <span class="required-asterisk">*</span></label>
                                    <textarea class="form-control" id="description" name="description" rows="6" required><%= book.getDescription() %></textarea>
                                    <div class="invalid-feedback">Please enter a description.</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="row mt-4">
                        <div class="col-12">
                            <div class="d-flex gap-3 justify-content-end flex-wrap">
                                <a href="${pageContext.request.contextPath}/seller/my-books" class="btn btn-outline-secondary">
                                    <i class="fas fa-arrow-left me-2"></i>Back to My Books
                                </a>
                                <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#deleteModal">
                                    <i class="fas fa-trash-alt me-2"></i>Delete Book
                                </button>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save me-2"></i>Save Changes
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0" style="border-radius: 20px;">
                <div class="modal-header bg-danger text-white" style="border-radius: 20px 20px 0 0;">
                    <h5 class="modal-title">
                        <i class="fas fa-exclamation-triangle me-2"></i>Confirm Delete
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="text-center">
                        <i class="fas fa-trash-alt fa-3x text-danger mb-3"></i>
                        <h5>Are you sure you want to delete this book?</h5>
                        <p class="text-muted mb-0">This action cannot be undone. All associated data will be permanently removed.</p>
                    </div>
                </div>
                <div class="modal-footer border-0 p-4">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times me-2"></i>Cancel
                    </button>
                    <form action="${pageContext.request.contextPath}/seller/delete-book" method="POST" style="display: inline;">
                        <input type="hidden" name="bookId" value="<%= book.getBookId() %>">
                        <button type="submit" class="btn btn-danger">
                            <i class="fas fa-trash-alt me-2"></i>Delete Permanently
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Form validation with enhanced feedback
        (function() {
            'use strict';
            var forms = document.querySelectorAll('.needs-validation');
            Array.from(forms).forEach(function(form) {
                form.addEventListener('submit', function(event) {
                    if (!form.checkValidity()) {
                        event.preventDefault();
                        event.stopPropagation();
                        
                        // Focus on first invalid field
                        const firstInvalid = form.querySelector(':invalid');
                        if (firstInvalid) {
                            firstInvalid.focus();
                            firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                        }
                    }
                    form.classList.add('was-validated');
                }, false);
            });
        })();

        // Enhanced image upload functionality
        const imageUploadContainer = document.getElementById('imageUploadContainer');
        const coverImageInput = document.getElementById('coverImage');
        const uploadPrompt = document.getElementById('uploadPrompt');
        const previewContainer = document.getElementById('previewContainer');
        const imagePreview = document.getElementById('imagePreview');
        const removeImageBtn = document.getElementById('removeImageBtn');

        // Click to upload
        imageUploadContainer.addEventListener('click', function(e) {
            if (e.target !== removeImageBtn && !removeImageBtn.contains(e.target)) {
                coverImageInput.click();
            }
        });

        // Drag and drop functionality
        imageUploadContainer.addEventListener('dragover', function(e) {
            e.preventDefault();
            imageUploadContainer.style.borderColor = '#667eea';
            imageUploadContainer.style.background = 'rgba(102,126,234,0.1)';
        });

        imageUploadContainer.addEventListener('dragleave', function(e) {
            e.preventDefault();
            imageUploadContainer.style.borderColor = '#dee2e6';
            imageUploadContainer.style.background = 'rgba(248,249,250,0.8)';
        });

        imageUploadContainer.addEventListener('drop', function(e) {
            e.preventDefault();
            imageUploadContainer.style.borderColor = '#dee2e6';
            imageUploadContainer.style.background = 'rgba(248,249,250,0.8)';
            
            const files = e.dataTransfer.files;
            if (files.length > 0) {
                coverImageInput.files = files;
                handleImagePreview(files[0]);
            }
        });

        // Image preview
        coverImageInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                handleImagePreview(file);
            }
        });

        function handleImagePreview(file) {
            // Validate file type
            if (!file.type.match('image.*')) {
                alert('Please select a valid image file.');
                return;
            }

            // Validate file size (5MB)
            if (file.size > 5 * 1024 * 1024) {
                alert('File size must be less than 5MB.');
                return;
            }

            const reader = new FileReader();
            reader.onload = function(e) {
                imagePreview.src = e.target.result;
                uploadPrompt.style.display = 'none';
                previewContainer.style.display = 'block';
                imageUploadContainer.classList.add('has-image');
            };
            reader.readAsDataURL(file);
        }

        // Remove image
        removeImageBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            coverImageInput.value = '';
            uploadPrompt.style.display = 'block';
            previewContainer.style.display = 'none';
            imageUploadContainer.classList.remove('has-image');
        });

        // Real-time validation feedback
        document.querySelectorAll('.form-control:not([readonly]), .form-select').forEach(function(input) {
            input.addEventListener('blur', function() {
                if (this.checkValidity()) {
                    this.classList.remove('is-invalid');
                    this.classList.add('is-valid');
                } else {
                    this.classList.remove('is-valid');
                    this.classList.add('is-invalid');
                }
            });

            input.addEventListener('input', function() {
                if (this.classList.contains('is-invalid') && this.checkValidity()) {
                    this.classList.remove('is-invalid');
                    this.classList.add('is-valid');
                }
            });
        });

        // Auto-hide alerts
        setTimeout(function() {
            var alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                var bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 8000);

        // Price formatting
        document.getElementById('price').addEventListener('input', function(e) {
            let value = parseFloat(e.target.value);
            if (value < 0) {
                e.target.value = 0;
            }
        });

        // Prevent form submission on Enter key for readonly ISBN field
        document.getElementById('isbn').addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
            }
        });
    </script>
</body>
</html>