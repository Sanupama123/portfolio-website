package com.bookstore.Servlets;

import com.bookstore.dao.BuyerDetailsDAO;
import com.bookstore.dao.CartDAO;
import com.bookstore.models.BuyerDetails;
import com.bookstore.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/buyer/buyer-details")
public class BuyerDetailsServlet extends HttpServlet {
    private BuyerDetailsDAO buyerDetailsDAO;
    private CartDAO cartDAO;

    @Override
    public void init() throws ServletException {
        buyerDetailsDAO = new BuyerDetailsDAO();
        cartDAO = new CartDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Check if cart is empty
            int cartCount = cartDAO.getCartItemCount(user.getUserId());
            if (cartCount == 0) {
                response.sendRedirect(request.getContextPath() + "/buyer/cart");
                return;
            }

            // Get existing buyer details if available
            BuyerDetails existingDetails = buyerDetailsDAO.getBuyerDetailsByUserId(user.getUserId());
            request.setAttribute("buyerDetails", existingDetails);

            // Store cart total in session for display
            double cartTotal = cartDAO.getCartTotal(user.getUserId());
            session.setAttribute("cartTotal", cartTotal);

            request.getRequestDispatcher("/WEB-INF/views/buyer/buyer-details.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Retrieve form data
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phoneNumber = request.getParameter("phoneNumber");
            String shippingAddress = request.getParameter("shippingAddress");
            String city = request.getParameter("city");
            String postalCode = request.getParameter("postalCode");

            // Validate required fields
            if (fullName == null || fullName.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                phoneNumber == null || phoneNumber.trim().isEmpty() ||
                shippingAddress == null || shippingAddress.trim().isEmpty() ||
                city == null || city.trim().isEmpty() ||
                postalCode == null || postalCode.trim().isEmpty()) {
                
                request.setAttribute("errorMessage", "All fields are required");
                doGet(request, response);
                return;
            }

            // Validate email format
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                request.setAttribute("errorMessage", "Invalid email format");
                doGet(request, response);
                return;
            }

            // Validate phone number (10-15 digits)
            if (!phoneNumber.matches("[0-9]{10,15}")) {
                request.setAttribute("errorMessage", "Phone number must be 10-15 digits");
                doGet(request, response);
                return;
            }

            // Validate postal code (5-10 digits)
            if (!postalCode.matches("[0-9]{5,10}")) {
                request.setAttribute("errorMessage", "Invalid postal code format");
                doGet(request, response);
                return;
            }

            // Create or update buyer details
            BuyerDetails details = new BuyerDetails();
            details.setUserId(user.getUserId());
            details.setFullName(fullName.trim());
            details.setEmail(email.trim());
            details.setPhoneNumber(phoneNumber.trim());
            details.setShippingAddress(shippingAddress.trim());
            details.setCity(city.trim());
            details.setPostalCode(postalCode.trim());

            // Save buyer details and get the ID
            String detailsId = buyerDetailsDAO.saveBuyerDetails(details);

            // Store buyer details ID in session for payment page
            session.setAttribute("buyerDetailsId", detailsId);
            session.setAttribute("buyerDetails", details);

            // Redirect to payment page
            response.sendRedirect(request.getContextPath() + "/buyer/payment");

        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }
}

