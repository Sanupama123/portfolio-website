package com.realestate.servlet;

import com.realestate.model.*;
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
 * Servlet for user account operations
 */
@WebServlet("/user/*")
public class UserServlet extends HttpServlet {
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null) {
            path = "/";
        }
        
        if (path.equals("/edit-profile")) {
            showEditProfile(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null) {
            path = "/";
        }
        
        if (path.equals("/update-profile")) {
            updateProfile(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    /**
     * Show the edit profile form
     */
    private void showEditProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }
        
        request.getRequestDispatcher("/edit-profile.jsp").forward(request, response);
    }
    
    /**
     * Update user profile
     */
    private void updateProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }
        
        // Get form data
        String userId = request.getParameter("userId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate input
        if (userId == null || firstName == null || lastName == null || email == null || 
            phoneNumber == null || currentPassword == null || 
            firstName.trim().isEmpty() || lastName.trim().isEmpty() || 
            email.trim().isEmpty() || phoneNumber.trim().isEmpty() || 
            currentPassword.trim().isEmpty()) {
            
            response.sendRedirect(request.getContextPath() + "/user/edit-profile?error=invalid_input");
            return;
        }
        
        // Verify current password
        if (!currentUser.verifyPassword(currentPassword)) {
            response.sendRedirect(request.getContextPath() + "/user/edit-profile?error=invalid_password");
            return;
        }
        
        // Check if email changed and if it's already in use by another user
        if (!email.equals(currentUser.getEmail()) && userService.isEmailTaken(email, userId)) {
            response.sendRedirect(request.getContextPath() + "/user/edit-profile?error=email_exists");
            return;
        }
        
        // Update user information
        if (currentUser instanceof Seller) {
            Seller seller = (Seller) currentUser;
            seller.setFirstName(firstName);
            seller.setLastName(lastName);
            seller.setEmail(email);
            seller.setPhoneNumber(phoneNumber);
            
            // Update company name if seller
            String companyName = request.getParameter("companyName");
            seller.setCompanyName(companyName != null ? companyName : "");
            
            // Update password if provided
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                seller.setPassword(newPassword);
            }
            
            userService.updateSeller(seller);
            request.getSession().setAttribute(Constants.SESSION_USER, seller);
            
        } else if (currentUser instanceof Buyer) {
            Buyer buyer = (Buyer) currentUser;
            buyer.setFirstName(firstName);
            buyer.setLastName(lastName);
            buyer.setEmail(email);
            buyer.setPhoneNumber(phoneNumber);
            
            // Update password if provided
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                buyer.setPassword(newPassword);
            }
            
            userService.updateBuyer(buyer);
            request.getSession().setAttribute(Constants.SESSION_USER, buyer);
        }
        
        // Redirect to profile with success message
        response.sendRedirect(request.getContextPath() + "/user/edit-profile?success=profile_updated");
    }
} 