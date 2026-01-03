<%@ page import="com.realestate.model.Property" %>
<%@ page import="com.realestate.util.Constants" %>
<%@ page import="com.realestate.service.PropertyService" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) session.getAttribute(Constants.SESSION_USER);
    if (user == null || !user.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=admin_required");
        return;
    }
    PropertyService propertyService = new PropertyService();
    java.util.List<Property> properties = propertyService.getAllProperties();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Properties - Real Estate</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        body { background: #f4f8fb; font-family: 'Roboto', Arial, sans-serif; }
        .container { max-width: 1100px; margin: 40px auto; background: #fff; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.07); padding: 32px 40px 40px 40px; }
        h2 { color: #1565c0; margin-bottom: 30px; }
        .property-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 4px rgba(21,101,192,0.07); }
        .property-table th, .property-table td { padding: 14px 16px; text-align: left; }
        .property-table th { background: #1976d2; color: #fff; font-weight: 500; border-bottom: 2px solid #1565c0; }
        .property-table tr:nth-child(even) { background: #f0f6ff; }
        .property-table tr:hover { background: #e3f0ff; }
        .status { padding: 4px 12px; border-radius: 12px; font-size: 0.95em; font-weight: 500; color: #fff; background: #1976d2; display: inline-block; }
        .status.sold { background: #43a047; }
        .status.pending { background: #ffa000; }
        .status.inactive { background: #bdbdbd; }
    </style>
</head>
<body>
    <h2>Manage Properties</h2>
    <table border="1">
        <tr><th>ID</th><th>Title</th><th>Type</th><th>Price</th><th>Status</th></tr>
        <% for (Property property : properties) { %>
        <tr>
            <td><%= property.getId() %></td>
            <td><%= property.getTitle() %></td>
            <td><%= property.getType() %></td>
            <td><%= property.getPrice() %></td>
            <td><%= property.getStatus() %></td>
        </tr>
        <% } %>
    </table>
</body>
</html> 