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
    String firstName = request.getParameter("firstName");
    String lastName = request.getParameter("lastName");
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String phoneNumber = request.getParameter("phoneNumber");
    if (firstName == null || lastName == null || email == null || password == null || firstName.trim().isEmpty() || lastName.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
        response.sendRedirect("add-admin.jsp?error=All fields are required");
        return;
    }
    UserService userService = new UserService();
    if (userService.isEmailTaken(email, "")) {
        response.sendRedirect("add-admin.jsp?error=Email is already in use");
        return;
    }
    Admin newAdmin = new Admin(userService.generateAdminId(), firstName, lastName, email, password, phoneNumber != null ? phoneNumber : "");
    boolean success = userService.addAdmin(newAdmin);
    if (success) {
        response.sendRedirect("users.jsp?success=admin_added");
    } else {
        response.sendRedirect("add-admin.jsp?error=Failed to add admin");
    }
%>
<%-- If you ever add HTML output, use: <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css"> --%> 