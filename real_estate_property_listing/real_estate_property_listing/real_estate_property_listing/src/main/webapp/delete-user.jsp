<%@ page import="com.realestate.model.*" %>
<%@ page import="com.realestate.util.Constants" %>
<%@ page import="com.realestate.service.UserService" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) session.getAttribute(Constants.SESSION_USER);
    if (user == null || !user.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=admin_required");
        return;
    }
    String userId = request.getParameter("id");
    String userType = request.getParameter("type");
    if (userId == null || userType == null) {
        response.sendRedirect("users.jsp?error=invalid_request");
        return;
    }
    boolean success = false;
    UserService userService = new UserService();
    switch (userType) {
        case "admin":
            success = userService.deleteAdmin(userId);
            break;
        case "seller":
            success = userService.deleteSeller(userId);
            break;
        case "buyer":
            success = userService.deleteBuyer(userId);
            break;
        default:
            break;
    }
    if (success) {
        response.sendRedirect("users.jsp?success=user_deleted");
    } else {
        response.sendRedirect("users.jsp?error=delete_failed");
    }
%>
<%-- If you ever add HTML output, use: <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css"> --%> 