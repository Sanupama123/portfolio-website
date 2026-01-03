package com.realestate.servlet;

import com.realestate.model.Buyer;
import com.realestate.model.Seller;
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
 * Servlet for handling user registration
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
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
        
        // Otherwise, forward to registration page
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String userType = request.getParameter("userType");
        
        // Validate input
        if (fullName == null || fullName.trim().isEmpty() || 
            email == null || email.trim().isEmpty() || 
            phoneNumber == null || phoneNumber.trim().isEmpty() || 
            password == null || password.trim().isEmpty() || 
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            
            request.setAttribute("error", "All fields are required");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        // Register user based on type
        if ("buyer".equals(userType)) {
            Buyer buyer = new Buyer();
            buyer.setFullName(fullName);
            buyer.setEmail(email);
            buyer.setPhoneNumber(phoneNumber);
            buyer.setPassword(password);
            
            Buyer registeredBuyer = userService.registerBuyer(buyer);
            
            if (registeredBuyer == null) {
                request.setAttribute("error", "Email already exists");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
            
            // Create session and store user
            HttpSession session = request.getSession(true);
            session.setAttribute(Constants.SESSION_USER, registeredBuyer);
            session.setAttribute("userType", "buyer");
            
        } else if ("seller".equals(userType)) {
            Seller seller = new Seller();
            seller.setFullName(fullName);
            seller.setEmail(email);
            seller.setPhoneNumber(phoneNumber);
            seller.setPassword(password);
            
            Seller registeredSeller = userService.registerSeller(seller);
            
            if (registeredSeller == null) {
                request.setAttribute("error", "Email already exists");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
            
            // Create session and store user
            HttpSession session = request.getSession(true);
            session.setAttribute(Constants.SESSION_USER, registeredSeller);
            session.setAttribute("userType", "seller");
            
        } else {
            request.setAttribute("error", "Invalid user type");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        // Redirect to dashboard
        response.sendRedirect("dashboard.jsp");
    }
} 