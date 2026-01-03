<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bookstore.models.User" %>
<%
  com.bookstore.models.User current = (com.bookstore.models.User) session.getAttribute("user");
  if (current == null || !current.isAdmin()) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
  }
  List<User> users = (List<User>) request.getAttribute("users");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Users - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Poppins', sans-serif; }
    </style>
    </head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm fixed-top">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}"><i class="fas fa-book-reader text-primary"></i> BookNest Admin</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard"><i class="fas fa-tachometer-alt"></i> Dashboard</a></li>
                <li class="nav-item"><a class="nav-link active" href="#"><i class="fas fa-users"></i> Users</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
            </ul>
        </div>
    </div>
 </nav>

<div class="container py-4" style="margin-top: 72px;">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="mb-0">Users Management</h3>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createAdminModal"><i class="fas fa-user-plus me-1"></i>Create Admin</button>
    </div>

<%
    String activeTab = request.getParameter("tab");
    if (activeTab == null || activeTab.isEmpty()) activeTab = "admins";
%>
    <ul class="nav nav-tabs mb-3" role="tablist">
        <li class="nav-item"><a class="nav-link <%= "admins".equals(activeTab)?"active":"" %>" data-bs-toggle="tab" href="#admins" role="tab">Admins</a></li>
        <li class="nav-item"><a class="nav-link <%= "sellers".equals(activeTab)?"active":"" %>" data-bs-toggle="tab" href="#sellers" role="tab">Sellers</a></li>
        <li class="nav-item"><a class="nav-link <%= "buyers".equals(activeTab)?"active":"" %>" data-bs-toggle="tab" href="#buyers" role="tab">Buyers</a></li>
    </ul>

    <div class="tab-content">
        <div class="tab-pane fade <%= "admins".equals(activeTab)?"show active":"" %>" id="admins" role="tabpanel">
            <div class="table-responsive">
                <table class="table table-striped align-middle">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Name</th>
                        <th>Active</th>
                        <th>System</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% if (users != null) {
                        for (User u : users) {
                            boolean isAdmin = u.isAdmin();
                            if (!isAdmin) continue; %>
                        <tr>
                            <td><%= u.getUserId() %></td>
                            <td><%= u.getUsername() %></td>
                            <td><%= u.getEmail() %></td>
                            <td><%= (u.getFirstName() == null ? "" : u.getFirstName()) + " " + (u.getLastName() == null ? "" : u.getLastName()) %></td>
                            <td><span class="badge <%= u.isActive() ? "bg-success" : "bg-secondary" %>"><%= u.isActive() ? "Yes" : "No" %></span></td>
                            <td><span class="badge <%= u.isSystemAccount() ? "bg-info" : "bg-light text-dark" %>"><%= u.isSystemAccount() ? "Yes" : "No" %></span></td>
                            <td>
                                <% if (u.isSystemAccount()) { %>
                                    <span class="text-muted">Protected</span>
                                <% } else { %>
                                    <div class="d-flex gap-2">
                                        <button class="btn btn-sm btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#editAdminModal<%=u.getUserId()%>"><i class="fas fa-edit"></i> Edit</button>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/users" onsubmit="return confirm('Delete this admin?');">
                                            <input type="hidden" name="action" value="delete-admin" />
                                            <input type="hidden" name="userId" value="<%=u.getUserId()%>" />
                                            <button type="submit" class="btn btn-sm btn-outline-danger"><i class="fas fa-trash"></i> Delete</button>
                                        </form>
                                    </div>
                                <% } %>
                            </td>
                        </tr>

                        <!-- Edit Admin Modal -->
                        <div class="modal fade" id="editAdminModal<%=u.getUserId()%>" tabindex="-1">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <form method="post" action="${pageContext.request.contextPath}/admin/users">
                                        <div class="modal-header">
                                            <h5 class="modal-title">Edit Admin</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body">
                                            <input type="hidden" name="action" value="update-admin" />
                                            <input type="hidden" name="userId" value="<%=u.getUserId()%>" />
                                            <div class="mb-2">
                                                <label class="form-label">Email</label>
                                                <input class="form-control" name="email" value="<%=u.getEmail()%>" />
                                            </div>
                                            <div class="mb-2">
                                                <label class="form-label">First Name</label>
                                                <input class="form-control" name="firstName" value="<%=u.getFirstName()==null?"":u.getFirstName()%>" />
                                            </div>
                                            <div class="mb-2">
                                                <label class="form-label">Last Name</label>
                                                <input class="form-control" name="lastName" value="<%=u.getLastName()==null?"":u.getLastName()%>" />
                                            </div>
                                            <div class="mb-2">
                                                <label class="form-label">Phone</label>
                                                <input class="form-control" name="phoneNumber" value="<%=u.getPhoneNumber()==null?"":u.getPhoneNumber()%>" />
                                            </div>
                                            <div class="mb-2">
                                                <label class="form-label">Address</label>
                                                <textarea class="form-control" name="address"><%=u.getAddress()==null?"":u.getAddress()%></textarea>
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                            <button type="submit" class="btn btn-primary">Save</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    <% } } %>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="tab-pane fade <%= "sellers".equals(activeTab)?"show active":"" %>" id="sellers" role="tabpanel">
            <div class="table-responsive">
                <table class="table table-striped align-middle">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Name</th>
                        <th>Active</th>
                        <th>Suspended</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% if (users != null) {
                        for (User u : users) {
                            boolean isSeller = u.isSeller();
                            if (!isSeller) continue; %>
                        <tr>
                            <td><%= u.getUserId() %></td>
                            <td><%= u.getUsername() %></td>
                            <td><%= u.getEmail() %></td>
                            <td><%= (u.getFirstName() == null ? "" : u.getFirstName()) + " " + (u.getLastName() == null ? "" : u.getLastName()) %></td>
                            <td><span class="badge <%= u.isActive() ? "bg-success" : "bg-secondary" %>"><%= u.isActive() ? "Yes" : "No" %></span></td>
                            <td><span class="badge <%= u.isSuspended() ? "bg-danger" : "bg-success" %>"><%= u.isSuspended() ? "Yes" : "No" %></span></td>
                            <td>
                                <div class="d-flex gap-2">
                                    <form class="d-inline" method="post" action="${pageContext.request.contextPath}/admin/users">
                                        <input type="hidden" name="userId" value="<%=u.getUserId()%>" />
                                        <input type="hidden" name="tab" value="sellers" />
                                        <% if (u.isSuspended()) { %>
                                            <input type="hidden" name="action" value="unsuspend" />
                                            <button type="submit" class="btn btn-sm btn-outline-success">Unsuspend</button>
                                        <% } else { %>
                                            <input type="hidden" name="action" value="suspend" />
                                            <button type="submit" class="btn btn-sm btn-outline-warning">Suspend</button>
                                        <% } %>
                                    </form>
                                    <form class="d-inline" method="post" action="${pageContext.request.contextPath}/admin/users" onsubmit="return confirm('Delete this seller?');">
                                        <input type="hidden" name="action" value="delete-user" />
                                        <input type="hidden" name="userId" value="<%=u.getUserId()%>" />
                                        <input type="hidden" name="tab" value="sellers" />
                                        <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    <% } } %>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="tab-pane fade <%= "buyers".equals(activeTab)?"show active":"" %>" id="buyers" role="tabpanel">
            <div class="table-responsive">
                <table class="table table-striped align-middle">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Name</th>
                        <th>Active</th>
                        <th>Suspended</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% if (users != null) {
                        for (User u : users) {
                            boolean isBuyer = u.isBuyer();
                            if (!isBuyer) continue; %>
                        <tr>
                            <td><%= u.getUserId() %></td>
                            <td><%= u.getUsername() %></td>
                            <td><%= u.getEmail() %></td>
                            <td><%= (u.getFirstName() == null ? "" : u.getFirstName()) + " " + (u.getLastName() == null ? "" : u.getLastName()) %></td>
                            <td><span class="badge <%= u.isActive() ? "bg-success" : "bg-secondary" %>"><%= u.isActive() ? "Yes" : "No" %></span></td>
                            <td><span class="badge <%= u.isSuspended() ? "bg-danger" : "bg-success" %>"><%= u.isSuspended() ? "Yes" : "No" %></span></td>
                            <td>
                                <div class="d-flex gap-2">
                                    <form class="d-inline" method="post" action="${pageContext.request.contextPath}/admin/users">
                                        <input type="hidden" name="userId" value="<%=u.getUserId()%>" />
                                        <input type="hidden" name="tab" value="buyers" />
                                        <% if (u.isSuspended()) { %>
                                            <input type="hidden" name="action" value="unsuspend" />
                                            <button type="submit" class="btn btn-sm btn-outline-success">Unsuspend</button>
                                        <% } else { %>
                                            <input type="hidden" name="action" value="suspend" />
                                            <button type="submit" class="btn btn-sm btn-outline-warning">Suspend</button>
                                        <% } %>
                                    </form>
                                    <form class="d-inline" method="post" action="${pageContext.request.contextPath}/admin/users" onsubmit="return confirm('Delete this buyer?');">
                                        <input type="hidden" name="action" value="delete-user" />
                                        <input type="hidden" name="userId" value="<%=u.getUserId()%>" />
                                        <input type="hidden" name="tab" value="buyers" />
                                        <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    <% } } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Create Admin Modal -->
    <div class="modal fade" id="createAdminModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/admin/users">
                    <div class="modal-header">
                        <h5 class="modal-title">Create Admin</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" name="action" value="create-admin" />
                        <div class="mb-2">
                            <label class="form-label">Username</label>
                            <input class="form-control" name="username" required />
                        </div>
                        <div class="mb-2">
                            <label class="form-label">Email</label>
                            <input class="form-control" name="email" type="email" required />
                        </div>
                        <div class="mb-2">
                            <label class="form-label">Password</label>
                            <input class="form-control" name="password" type="password" required />
                        </div>
                        <div class="mb-2">
                            <label class="form-label">First Name</label>
                            <input class="form-control" name="firstName" />
                        </div>
                        <div class="mb-2">
                            <label class="form-label">Last Name</label>
                            <input class="form-control" name="lastName" />
                        </div>
                        <div class="mb-2">
                            <label class="form-label">Phone</label>
                            <input class="form-control" name="phoneNumber" />
                        </div>
                        <div class="mb-2">
                            <label class="form-label">Address</label>
                            <textarea class="form-control" name="address"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Create</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>


