package com.bookstore.Servlets;

import com.bookstore.models.User;
import com.bookstore.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String error = null;

        System.out.println("Login attempt for username: " + username);

        try {
            User user = userService.authenticate(username, password);
            if (user != null) {
                System.out.println("Login successful for user: " + username + " with type: " + user.getUserType());
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("role", user.getUserType());
                
                // Redirect based on user type
                switch (user.getUserType()) {
                    case "ADMIN":
                        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                        break;
                    case "SELLER":
                        response.sendRedirect(request.getContextPath() + "/seller/dashboard");
                        break;
                    case "BUYER":
                    default:
                        response.sendRedirect(request.getContextPath() + "/buyer/dashboard");
                        break;
                }
                return;
            } else {
                System.out.println("Login failed for username: " + username + " - User not found or password incorrect");
                error = "Invalid username or password";
            }
        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
            e.printStackTrace();
            error = "Database error: " + e.getMessage();
        }

        request.setAttribute("error", error);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}