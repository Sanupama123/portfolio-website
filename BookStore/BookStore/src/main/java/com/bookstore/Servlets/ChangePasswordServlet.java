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

@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        System.out.println("Current stored password: " + user.getPassword());
        System.out.println("User entered current password: " + currentPassword);

        // Validate input
        if (currentPassword == null || newPassword == null || confirmPassword == null ||
            currentPassword.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            session.setAttribute("passwordError", "All fields are required");
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        // Check if new password matches confirmation
        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute("passwordError", "New password and confirmation do not match");
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        // Verify current password
        try {
            // Since we're in testing mode with plaintext passwords
            if (!currentPassword.equals(user.getPassword())) {
                session.setAttribute("passwordError", "Current password is incorrect");
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }

            // Update the password
            userDAO.updatePassword(user.getUserId(), newPassword);
            
            // Update session user object
            user.setPassword(newPassword);
            session.setAttribute("user", user);
            
            session.setAttribute("passwordSuccess", "Password updated successfully");
            System.out.println("Password updated successfully for user: " + user.getUsername());
            
        } catch (SQLException e) {
            System.err.println("Error during password change: " + e.getMessage());
            session.setAttribute("passwordError", "Error updating password: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/profile");
    }
}