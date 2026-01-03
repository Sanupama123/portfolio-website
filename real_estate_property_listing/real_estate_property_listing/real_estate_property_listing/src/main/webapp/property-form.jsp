<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.realestate.model.Property" %>
<%@ page import="com.realestate.model.User" %>
<%@ page import="com.realestate.model.Seller" %>
<%@ page import="com.realestate.util.Constants" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Check if user is logged in and is a seller or admin
    User user = (User) session.getAttribute(Constants.SESSION_USER);
    boolean isAdmin = user != null && user.isAdmin();
    boolean isSeller = user != null && (user instanceof Seller);
    
    // Redirect if not seller or admin
    if (user == null || (!isAdmin && !isSeller)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
        return;
    }
    
    // Check if this is an edit or add operation
    Property property = (Property) request.getAttribute("property");
    boolean isEdit = property != null;
    
    if (property == null) {
        property = new Property(); // Initialize empty property for add
    }
    
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= isEdit ? "Edit" : "Add" %> Property - Real Estate</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        .property-form {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            max-width: 800px;
            margin: 0 auto;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-row {
            display: flex;
            flex-wrap: wrap;
            margin: 0 -10px;
        }
        
        .form-col {
            flex: 1;
            padding: 0 10px;
            min-width: 200px;
        }
        
        .form-actions {
            margin-top: 30px;
            text-align: right;
        }
        
        .image-preview {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin-top: 10px;
        }
        
        .image-item {
            position: relative;
            width: 100px;
            height: 100px;
            border-radius: 4px;
            overflow: hidden;
            border: 1px solid #ddd;
        }
        
        .image-item img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        
        .remove-image {
            position: absolute;
            top: 5px;
            right: 5px;
            background-color: rgba(255, 255, 255, 0.8);
            border-radius: 50%;
            width: 24px;
            height: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            font-size: 12px;
            color: #d9534f;
        }
        
        .remove-image input[type="checkbox"] {
            position: absolute;
            opacity: 0;
            width: 0;
            height: 0;
        }
        
        .remove-image label {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 100%;
            height: 100%;
            cursor: pointer;
        }
        
        .image-item.marked-for-removal {
            opacity: 0.5;
        }
        
        .error-message {
            color: #d9534f;
            margin-bottom: 20px;
            padding: 10px;
            background-color: #f8d7da;
            border-radius: 4px;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <header>
        <div class="container">
            <nav class="navbar">
                <a href="<%= request.getContextPath() %>/index.jsp" class="logo">Real Estate</a>
                <ul class="nav-links">
                    <li><a href="<%= request.getContextPath() %>/index.jsp">Home</a></li>
                    <li><a href="<%= request.getContextPath() %>/properties">Properties</a></li>
                    <li><a href="<%= request.getContextPath() %>/about.jsp">About</a></li>
                    <li><a href="<%= request.getContextPath() %>/contact.jsp">Contact</a></li>
                </ul>
                
                <div class="user-actions">
                    <a href="<%= request.getContextPath() %>/dashboard.jsp" class="btn btn-outline">Dashboard</a>
                    <a href="<%= request.getContextPath() %>/logout" class="btn btn-primary">Logout</a>
                </div>
                
                <button class="menu-toggle">
                    <i class="fa fa-bars"></i>
                </button>
            </nav>
        </div>
    </header>
    
    <!-- Main Content -->
    <main>
        <section>
            <div class="container">
                <h2 class="section-title"><%= isEdit ? "Edit" : "Add New" %> Property</h2>
                
                <% if (error != null) { %>
                    <div class="error-message">
                        <i class="fas fa-exclamation-circle"></i> <%= error %>
                    </div>
                <% } %>
                
                <div class="property-form">
                    <form action="<%= request.getContextPath() %>/properties<%= isEdit ? "/edit/" + property.getId() : "/add" %>" method="post" enctype="multipart/form-data">
                        <% if (isEdit) { %>
                            <input type="hidden" name="id" value="<%= property.getId() %>">
                        <% } %>
                        
                        <div class="form-group">
                            <label for="title" class="form-label">Title*</label>
                            <input type="text" id="title" name="title" class="form-control" required value="<%= isEdit ? property.getTitle() : "" %>">
                        </div>
                        
                        <div class="form-group">
                            <label for="description" class="form-label">Description*</label>
                            <textarea id="description" name="description" class="form-control" rows="5" required><%= isEdit ? property.getDescription() : "" %></textarea>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-col">
                                <div class="form-group">
                                    <label for="price" class="form-label">Price ($)*</label>
                                    <input type="number" id="price" name="price" class="form-control" min="0" step="0.01" required value="<%= isEdit ? property.getPrice() : "" %>">
                                </div>
                            </div>
                            <div class="form-col">
                                <div class="form-group">
                                    <label for="propertyType" class="form-label">Property Type*</label>
                                    <select id="propertyType" name="propertyType" class="form-control" required>
                                        <option value="" disabled <%= !isEdit ? "selected" : "" %>>Select property type</option>
                                        <option value="House" <%= isEdit && "House".equals(property.getPropertyType()) ? "selected" : "" %>>House</option>
                                        <option value="Apartment" <%= isEdit && "Apartment".equals(property.getPropertyType()) ? "selected" : "" %>>Apartment</option>
                                        <option value="Villa" <%= isEdit && "Villa".equals(property.getPropertyType()) ? "selected" : "" %>>Villa</option>
                                        <option value="Land" <%= isEdit && "Land".equals(property.getPropertyType()) ? "selected" : "" %>>Land</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="location" class="form-label">Location*</label>
                            <input type="text" id="location" name="location" class="form-control" required value="<%= isEdit ? property.getLocation() : "" %>">
                        </div>
                        
                        <div class="form-row">
                            <div class="form-col">
                                <div class="form-group">
                                    <label for="bedrooms" class="form-label">Bedrooms*</label>
                                    <input type="number" id="bedrooms" name="bedrooms" class="form-control" min="0" required value="<%= isEdit ? property.getBedrooms() : "0" %>">
                                </div>
                            </div>
                            <div class="form-col">
                                <div class="form-group">
                                    <label for="bathrooms" class="form-label">Bathrooms*</label>
                                    <input type="number" id="bathrooms" name="bathrooms" class="form-control" min="0" step="0.5" required value="<%= isEdit ? (property.getBathrooms() / 2.0) : "0" %>">
                                    <small class="form-text text-muted">Use .5 for half bathrooms (e.g., 2.5)</small>
                                </div>
                            </div>
                            <div class="form-col">
                                <div class="form-group">
                                    <label for="area" class="form-label">Area (sqft)*</label>
                                    <input type="number" id="area" name="area" class="form-control" min="0" required value="<%= isEdit ? property.getArea() : "" %>">
                                </div>
                            </div>
                        </div>
                        
                        <% if (isAdmin && !isEdit) { %>
                            <div class="form-group">
                                <label for="sellerId" class="form-label">Seller ID*</label>
                                <input type="text" id="sellerId" name="sellerId" class="form-control" required>
                                <small class="form-text text-muted">As an admin, you must specify which seller will own this property.</small>
                            </div>
                        <% } %>
                        
                        <div class="form-group">
                            <label for="images" class="form-label">Images</label>
                            <input type="file" id="images" name="images" class="form-control" multiple accept="image/*">
                            <small class="form-text text-muted">You can select multiple images. Maximum size per image: 10MB.</small>
                            
                            <% if (isEdit && property.getImageUrls() != null && !property.getImageUrls().isEmpty()) { %>
                                <p style="margin-top: 15px; font-weight: 500;">Current Images:</p>
                                <div class="image-preview">
                                    <% for (String imageUrl : property.getImageUrls()) { %>
                                        <div class="image-item">
                                            <img src="<%= request.getContextPath() + "/" + imageUrl %>" alt="Property Image">
                                            <div class="remove-image">
                                                <input type="checkbox" id="remove-<%= imageUrl %>" name="removeImages" value="<%= imageUrl %>" style="position: absolute; opacity: 0; width: 0; height: 0;">
                                                <label for="remove-<%= imageUrl %>" title="Remove this image">
                                                    <i class="fas fa-trash"></i>
                                                </label>
                                            </div>
                                        </div>
                                    <% } %>
                                </div>
                                <p><small class="text-muted">Check the trash icon to remove an image.</small></p>
                            <% } %>
                        </div>
                        
                        <div class="form-actions">
                            <a href="<%= request.getContextPath() %>/properties/manage" class="btn btn-outline">Cancel</a>
                            <button type="submit" class="btn btn-primary"><%= isEdit ? "Update" : "Add" %> Property</button>
                        </div>
                    </form>
                </div>
            </div>
        </section>
    </main>
    
    <!-- Footer -->
    <footer>
        <div class="container">
            <div class="footer-content">
                <div class="footer-section">
                    <h3 class="footer-title">Real Estate</h3>
                    <p>Find your dream property with our extensive listings of homes, apartments, and commercial spaces.</p>
                </div>
                <div class="footer-section">
                    <h3 class="footer-title">Quick Links</h3>
                    <ul class="footer-links">
                        <li><a href="<%= request.getContextPath() %>/index.jsp">Home</a></li>
                        <li><a href="<%= request.getContextPath() %>/properties">Properties</a></li>
                        <li><a href="<%= request.getContextPath() %>/about.jsp">About Us</a></li>
                        <li><a href="<%= request.getContextPath() %>/contact.jsp">Contact Us</a></li>
                    </ul>
                </div>
                <div class="footer-section">
                    <h3 class="footer-title">Contact</h3>
                    <ul class="footer-links">
                        <li><i class="fas fa-map-marker-alt"></i> 123 Real Estate St, City</li>
                        <li><i class="fas fa-phone"></i> (123) 456-7890</li>
                        <li><i class="fas fa-envelope"></i> info@realestate.com</li>
                    </ul>
                </div>
            </div>
            <div class="copyright">
                &copy; 2023 Real Estate. All rights reserved.
            </div>
        </div>
    </footer>
    
    <script>
        // Mobile menu toggle
        document.querySelector('.menu-toggle').addEventListener('click', function() {
            document.querySelector('.nav-links').classList.toggle('active');
        });
        
        // Image preview
        document.getElementById('images').addEventListener('change', function(event) {
            const files = event.target.files;
            
            if (!files || files.length === 0) {
                return;
            }
            
            // Create a preview container if it doesn't exist
            let previewContainer = document.querySelector('.uploaded-preview');
            if (!previewContainer) {
                previewContainer = document.createElement('div');
                previewContainer.className = 'image-preview uploaded-preview';
                previewContainer.style.marginTop = '10px';
                
                // Add title for new uploads
                const title = document.createElement('p');
                title.style.width = '100%';
                title.style.fontWeight = '500';
                title.textContent = 'New Uploads:';
                previewContainer.appendChild(title);
                
                // Add after the file input
                this.parentNode.appendChild(previewContainer);
            }
            
            // Clear existing previews (except title)
            while (previewContainer.childNodes.length > 1) {
                previewContainer.removeChild(previewContainer.lastChild);
            }
            
            // Add preview for each file
            for (let i = 0; i < files.length; i++) {
                const file = files[i];
                if (!file.type.startsWith('image/')) continue;
                
                const reader = new FileReader();
                reader.onload = function(e) {
                    const previewItem = document.createElement('div');
                    previewItem.className = 'image-item';
                    
                    const img = document.createElement('img');
                    img.src = e.target.result;
                    img.alt = 'Preview';
                    
                    previewItem.appendChild(img);
                    previewContainer.appendChild(previewItem);
                };
                
                reader.readAsDataURL(file);
            }
        });
        
        // Make the remove image checkboxes work with the label
        document.querySelectorAll('.remove-image label').forEach(function(label) {
            label.addEventListener('click', function(e) {
                e.preventDefault(); // Prevent default to avoid issues
                const checkbox = this.parentNode.querySelector('input[type="checkbox"]');
                checkbox.checked = !checkbox.checked;
                
                // Visual feedback
                if (checkbox.checked) {
                    this.parentNode.parentNode.style.opacity = '0.5';
                    this.parentNode.parentNode.classList.add('marked-for-removal');
                } else {
                    this.parentNode.parentNode.style.opacity = '1';
                    this.parentNode.parentNode.classList.remove('marked-for-removal');
                }
                
                // Log for debugging
                console.log('Checkbox ' + checkbox.id + ' is now ' + (checkbox.checked ? 'checked' : 'unchecked'));
            });
        });
    </script>
    
    <!-- Include form debugging script -->
    <script src="<%= request.getContextPath() %>/js/debug_form.js"></script>
</body>
</html>