package com.bookstore.Servlets;

import com.bookstore.dao.UserDAO;
import com.bookstore.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/users")
public class AdminUsersServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User current = (User) session.getAttribute("user");
        if (current == null || !current.isAdmin()) {
            response.sendRedirect(request.getContextPath());
            return;
        }

        try {
            List<User> users = userDAO.listAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User current = (User) session.getAttribute("user");
        if (current == null || !current.isAdmin()) {
            response.sendRedirect(request.getContextPath());
            return;
        }

        String action = request.getParameter("action");
        String redirectTab = request.getParameter("tab");
        try {
            if ("create-admin".equals(action)) {
                User u = new User();
                u.setUsername(request.getParameter("username"));
                u.setEmail(request.getParameter("email"));
                u.setPassword(request.getParameter("password"));
                u.setFirstName(request.getParameter("firstName"));
                u.setLastName(request.getParameter("lastName"));
                u.setPhoneNumber(request.getParameter("phoneNumber"));
                u.setAddress(request.getParameter("address"));
                userDAO.create(u, "ADMIN");
                redirectTab = "admins";
            } else if ("update-admin".equals(action)) {
                User u = new User();
                u.setUserId(request.getParameter("userId"));
                u.setEmail(request.getParameter("email"));
                u.setFirstName(request.getParameter("firstName"));
                u.setLastName(request.getParameter("lastName"));
                u.setPhoneNumber(request.getParameter("phoneNumber"));
                u.setAddress(request.getParameter("address"));
                userDAO.updateProfile(u);
                redirectTab = "admins";
            } else if ("delete-admin".equals(action)) {
                String userId = request.getParameter("userId");
                userDAO.deleteById(userId);
                redirectTab = "admins";
            } else if ("suspend".equals(action)) {
                String userId = request.getParameter("userId");
                userDAO.setSuspended(userId, true);
            } else if ("unsuspend".equals(action)) {
                String userId = request.getParameter("userId");
                userDAO.setSuspended(userId, false);
            } else if ("delete-user".equals(action)) {
                String userId = request.getParameter("userId");
                // Safety: do not allow deleting admins via this route
                List<User> users = userDAO.listAllUsers();
                boolean isAdminTarget = false;
                for (User u : users) {
                    if (u.getUserId() == userId && u.isAdmin()) {
                        isAdminTarget = true;
                        break;
                    }
                }
                if (!isAdminTarget) {
                    userDAO.deleteById(userId);
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
        if (redirectTab == null || redirectTab.isEmpty()) redirectTab = request.getParameter("tab");
        if (redirectTab == null || redirectTab.isEmpty()) redirectTab = "admins";
        response.sendRedirect(request.getContextPath() + "/admin/users?tab=" + redirectTab);
    }
}


