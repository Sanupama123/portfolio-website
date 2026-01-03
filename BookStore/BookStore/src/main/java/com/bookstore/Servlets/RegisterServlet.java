package com.bookstore.Servlets;

import com.bookstore.models.User;
import com.bookstore.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        User user = new User();
        user.setUsername(request.getParameter("username"));
        user.setEmail(request.getParameter("email"));
        user.setPassword(request.getParameter("password"));
        user.setFirstName(request.getParameter("firstName"));
        user.setLastName(request.getParameter("lastName"));
        user.setPhoneNumber(request.getParameter("phoneNumber"));
        user.setAddress(request.getParameter("address"));

        String role = request.getParameter("role");
        String error = null;

        // Validate role (only BUYER or SELLER allowed)
        if (!role.equals("BUYER") && !role.equals("SELLER")) {
            error = "Invalid role selected";
        } else {
            try {
                if (userService.registerUser(user, role)) {
                    response.sendRedirect(request.getContextPath() + "/login?registered=true");
                    return;
                }
            } catch (SQLException e) {
                error = e.getMessage();
            }
        }

        request.setAttribute("error", error);
        request.setAttribute("user", user); // To preserve form data
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
}