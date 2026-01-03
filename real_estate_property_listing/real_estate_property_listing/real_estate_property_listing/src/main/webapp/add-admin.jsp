<%@ page import="com.realestate.model.User" %>
<%@ page import="com.realestate.util.Constants" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) session.getAttribute(Constants.SESSION_USER);
    if (user == null || !user.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=admin_required");
        return;
    }
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add New Admin - Real Estate</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        body { background: #f4f8fb; font-family: 'Roboto', Arial, sans-serif; }
        .container {
            max-width: 500px;
            margin: 40px auto;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.07);
            padding: 32px 40px 40px 40px;
        }
        h2 {
            color: #1565c0;
            margin-bottom: 30px;
            text-align: center;
        }
        .form-group {
            margin-bottom: 18px;
        }
        .form-label {
            display: block;
            margin-bottom: 6px;
            color: #1976d2;
            font-weight: 500;
        }
        .form-control {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
        }
        .form-control:focus {
            border-color: #1976d2;
            outline: none;
            box-shadow: 0 0 0 2px #e8f0fe;
        }
        .form-actions {
            margin-top: 25px;
            text-align: right;
        }
        .btn-primary {
            background-color: #1976d2;
            color: #fff;
            border: none;
            padding: 10px 22px;
            border-radius: 4px;
            font-weight: 500;
            text-decoration: none;
            transition: background 0.2s;
            margin-left: 10px;
        }
        .btn-primary:hover {
            background-color: #1565c0;
        }
        .btn-outline {
            border: 1px solid #1976d2;
            color: #1976d2;
            background: transparent;
            padding: 10px 22px;
            border-radius: 4px;
            font-weight: 500;
            text-decoration: none;
            transition: background 0.2s;
        }
        .btn-outline:hover {
            background: #e8f0fe;
        }
        .alert {
            padding: 12px 15px;
            border-radius: 4px;
            margin-bottom: 20px;
            font-size: 15px;
        }
        .alert-danger {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Add New Admin</h2>
        <% if (error != null) { %>
        <div class="alert alert-danger">
            <i class="fas fa-exclamation-circle"></i> <%= error %>
        </div>
        <% } %>
        <form action="add-admin-handler.jsp" method="post" id="addAdminForm" autocomplete="off">
            <div class="form-group">
                <label for="firstName" class="form-label">First Name*</label>
                <input type="text" id="firstName" name="firstName" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="lastName" class="form-label">Last Name*</label>
                <input type="text" id="lastName" name="lastName" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="email" class="form-label">Email*</label>
                <input type="email" id="email" name="email" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="password" class="form-label">Password*</label>
                <input type="password" id="password" name="password" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="confirmPassword" class="form-label">Confirm Password*</label>
                <input type="password" id="confirmPassword" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="phoneNumber" class="form-label">Phone Number</label>
                <input type="tel" id="phoneNumber" name="phoneNumber" class="form-control">
            </div>
            <div class="form-actions">
                <a href="users.jsp" class="btn-outline">Cancel</a>
                <button type="submit" class="btn-primary">Add Admin</button>
            </div>
        </form>
    </div>
    <script>
        // Client-side password match validation
        document.getElementById('addAdminForm').addEventListener('submit', function(event) {
            var pw = document.getElementById('password').value;
            var cpw = document.getElementById('confirmPassword').value;
            if (pw !== cpw) {
                event.preventDefault();
                alert('Passwords do not match');
            }
        });
    </script>
</body>
</html>