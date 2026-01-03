<%@ page import="com.realestate.model.User" %>
<%@ page import="com.realestate.util.Constants" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) session.getAttribute(Constants.SESSION_USER);
    if (user == null || !user.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=admin_required");
        return;
    }
    String success = request.getParameter("success");
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>System Settings - Real Estate</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        body { background: #f4f8fb; font-family: 'Roboto', Arial, sans-serif; }
        .container {
            max-width: 600px;
            margin: 40px auto;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.07);
            padding: 32px 40px 40px 40px;
        }
        h2 { color: #1565c0; margin-bottom: 30px; text-align: center; }
        .form-group { margin-bottom: 18px; }
        .form-label { display: block; margin-bottom: 6px; color: #1976d2; font-weight: 500; }
        .form-control { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 16px; }
        .form-control:focus { border-color: #1976d2; outline: none; box-shadow: 0 0 0 2px #e8f0fe; }
        .form-actions { margin-top: 25px; text-align: right; }
        .btn-primary {
            background-color: #1976d2;
            color: #fff;
            border: none;
            padding: 10px 22px;
            border-radius: 4px;
            font-weight: 500;
            text-decoration: none;
            transition: background 0.2s;
        }
        .btn-primary:hover { background-color: #1565c0; }
        .alert {
            padding: 12px 15px;
            border-radius: 4px;
            margin-bottom: 20px;
            font-size: 15px;
        }
        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
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
        <h2>System Settings</h2>
        <% if (success != null) { %>
        <div class="alert alert-success"><i class="fas fa-check-circle"></i> <%= success %></div>
        <% } %>
        <% if (error != null) { %>
        <div class="alert alert-danger"><i class="fas fa-exclamation-circle"></i> <%= error %></div>
        <% } %>
        <form method="post" action="settings-handler.jsp">
            <div class="form-group">
                <label for="siteTitle" class="form-label">Site Title</label>
                <input type="text" id="siteTitle" name="siteTitle" class="form-control" value="Real Estate Management System">
            </div>
            <div class="form-group">
                <label for="supportEmail" class="form-label">Support Email</label>
                <input type="email" id="supportEmail" name="supportEmail" class="form-control" value="support@realestate.com">
            </div>
            <div class="form-group">
                <label for="maintenanceMode" class="form-label">Maintenance Mode</label>
                <select id="maintenanceMode" name="maintenanceMode" class="form-control">
                    <option value="off" selected>Off</option>
                    <option value="on">On</option>
                </select>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn-primary">Save Settings</button>
            </div>
        </form>
    </div>
</body>
</html>