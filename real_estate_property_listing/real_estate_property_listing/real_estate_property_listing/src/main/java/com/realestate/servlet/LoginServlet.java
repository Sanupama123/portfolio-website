package com.realestate.servlet;

import com.realestate.model.User;
import com.realestate.service.UserService;
import com.realestate.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for handling user login
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(Constants.SESSION_USER) != null) {
            response.sendRedirect("dashboard.jsp");
            return;
        }
        
        // Otherwise, forward to login page
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String userType = request.getParameter("userType");
        
        // Validate input
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Email and password are required");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        // Authenticate user
        User user = userService.authenticate(email, password);
        
        if (user == null) {
            request.setAttribute("error", "Invalid email or password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        // Check if user type matches
        boolean isCorrectType = false;
        switch (userType) {
            case "admin":
                isCorrectType = user instanceof com.realestate.model.Admin;
                break;
            case "seller":
                isCorrectType = user instanceof com.realestate.model.Seller;
                break;
            case "buyer":
                isCorrectType = user instanceof com.realestate.model.Buyer;
                break;
        }
        
        if (!isCorrectType) {
            request.setAttribute("error", "Invalid user type selected");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        // Create session and store user
        HttpSession session = request.getSession(true);
        session.setAttribute(Constants.SESSION_USER, user);
        session.setAttribute("userType", userType);
        
        // Redirect to dashboard
        response.sendRedirect("dashboard.jsp");
    }
} 