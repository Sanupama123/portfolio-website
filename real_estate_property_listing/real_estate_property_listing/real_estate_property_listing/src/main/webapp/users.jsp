<%@ page import="com.realestate.model.*" %>
<%@ page import="com.realestate.util.Constants" %>
<%@ page import="com.realestate.service.UserService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) session.getAttribute(Constants.SESSION_USER);
    if (user == null || !user.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=admin_required");
        return;
    }
    List<Admin> admins = (List<Admin>) request.getAttribute("admins");
    List<Seller> sellers = (List<Seller>) request.getAttribute("sellers");
    List<Buyer> buyers = (List<Buyer>) request.getAttribute("buyers");
    if (admins == null || sellers == null || buyers == null) {
        UserService userService = new UserService();
        if (admins == null) admins = userService.getAllAdmins();
        if (sellers == null) sellers = userService.getAllSellers();
        if (buyers == null) buyers = userService.getAllBuyers();
    }
    String success = request.getParameter("success");
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Users - Real Estate</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        body {
            background: #f4f8fb;
            font-family: 'Roboto', Arial, sans-serif;
        }
        .container {
            max-width: 1100px;
            margin: 40px auto;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.07);
            padding: 32px 40px 40px 40px;
        }
        h2 {
            color: #1565c0;
            margin-bottom: 30px;
        }
        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 18px;
        }
        .user-table {
            width: 100%;
            border-collapse: collapse;
            background: #fff;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 1px 4px rgba(21,101,192,0.07);
            margin-bottom: 32px;
        }
        .user-table th, .user-table td {
            padding: 14px 16px;
            text-align: left;
        }
        .user-table th {
            background: #1976d2;
            color: #fff;
            font-weight: 500;
            border-bottom: 2px solid #1565c0;
        }
        .user-table tr:nth-child(even) {
            background: #f0f6ff;
        }
        .user-table tr:hover {
            background: #e3f0ff;
        }
        .action-button {
            background: none;
            border: none;
            cursor: pointer;
            color: #d93025;
            font-size: 16px;
            padding: 6px 10px;
            border-radius: 4px;
            transition: background 0.2s;
        }
        .action-button:hover {
            background: #e3f0ff;
        }
        .btn-primary {
            background-color: #1976d2;
            color: #fff;
            border: none;
            padding: 8px 18px;
            border-radius: 4px;
            font-weight: 500;
            text-decoration: none;
            transition: background 0.2s;
        }
        .btn-primary:hover {
            background-color: #1565c0;
        }
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
        @media (max-width: 700px) {
            .container { padding: 15px; }
            .user-table th, .user-table td { padding: 8px 6px; }
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Manage Users</h2>
        <% if (success != null) { %>
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i>
            <% if (success.equals("admin_added")) { %>
                New admin has been added successfully.
            <% } else if (success.equals("user_deleted")) { %>
                User has been deleted successfully.
            <% } else { %>
                <%= success %>
            <% } %>
        </div>
        <% } %>
        <% if (error != null) { %>
        <div class="alert alert-danger">
            <i class="fas fa-exclamation-circle"></i>
            <% if (error.equals("delete_failed")) { %>
                Failed to delete user. Please try again.
            <% } else if (error.equals("invalid_request")) { %>
                Invalid request parameters.
            <% } else { %>
                <%= error %>
            <% } %>
        </div>
        <% } %>

        <!-- Admins Section -->
        <div class="section-header">
            <h3>Admin Users</h3>
            <a href="add-admin.jsp" class="btn-primary"><i class="fas fa-plus"></i> Add New Admin</a>
        </div>
        <table class="user-table">
            <thead>
                <tr>
                    <th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% for (Admin admin : admins) { %>
                <tr>
                    <td><%= admin.getId() %></td>
                    <td><%= admin.getFirstName() + " " + admin.getLastName() %></td>
                    <td><%= admin.getEmail() %></td>
                    <td><%= admin.getPhoneNumber() != null ? admin.getPhoneNumber() : "N/A" %></td>
                    <td>
                        <% if (admins.size() > 1) { %>
                        <form method="post" action="delete-user.jsp" style="display:inline;">
                            <input type="hidden" name="id" value="<%= admin.getId() %>" />
                            <input type="hidden" name="type" value="admin" />
                            <button type="submit" class="action-button" title="Delete"><i class="fas fa-trash"></i></button>
                        </form>
                        <% } %>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>

        <!-- Sellers Section -->
        <div class="section-header">
            <h3>Seller Users</h3>
        </div>
        <table class="user-table">
            <thead>
                <tr>
                    <th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Company</th><th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% for (Seller seller : sellers) { %>
                <tr>
                    <td><%= seller.getId() %></td>
                    <td><%= seller.getFirstName() + " " + seller.getLastName() %></td>
                    <td><%= seller.getEmail() %></td>
                    <td><%= seller.getPhoneNumber() != null ? seller.getPhoneNumber() : "N/A" %></td>
                    <td><%= seller.getCompanyName() != null ? seller.getCompanyName() : "N/A" %></td>
                    <td>
                        <form method="post" action="delete-user.jsp" style="display:inline;">
                            <input type="hidden" name="id" value="<%= seller.getId() %>" />
                            <input type="hidden" name="type" value="seller" />
                            <button type="submit" class="action-button" title="Delete"><i class="fas fa-trash"></i></button>
                        </form>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>

        <!-- Buyers Section -->
        <div class="section-header">
            <h3>Buyer Users</h3>
        </div>
        <table class="user-table">
            <thead>
                <tr>
                    <th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% for (Buyer buyer : buyers) { %>
                <tr>
                    <td><%= buyer.getId() %></td>
                    <td><%= buyer.getFirstName() + " " + buyer.getLastName() %></td>
                    <td><%= buyer.getEmail() %></td>
                    <td><%= buyer.getPhoneNumber() != null ? buyer.getPhoneNumber() : "N/A" %></td>
                    <td>
                        <form method="post" action="delete-user.jsp" style="display:inline;">
                            <input type="hidden" name="id" value="<%= buyer.getId() %>" />
                            <input type="hidden" name="type" value="buyer" />
                            <button type="submit" class="action-button" title="Delete"><i class="fas fa-trash"></i></button>
                        </form>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</body>
</html>