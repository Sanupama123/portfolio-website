package com.bookstore.Servlets;

import com.bookstore.dao.*;
import com.bookstore.models.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/buyer/cart")
public class CartServlet extends HttpServlet {
    private CartDAO cartDAO;
    private WishlistDAO wishlistDAO;

    @Override
    public void init() throws ServletException {
        cartDAO = new CartDAO();
        wishlistDAO = new WishlistDAO();
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

            // Get cart items
            List<CartItem> cartItems = cartDAO.getCartItems(user.getUserId());
            double cartTotal = cartDAO.getCartTotal(user.getUserId());
            int cartCount = cartDAO.getCartItemCount(user.getUserId());
            int wishlistCount = wishlistDAO.getWishlistCount(user.getUserId());

            // Set attributes for the view
            request.setAttribute("cartItems", cartItems);
            request.setAttribute("cartTotal", cartTotal);
            request.setAttribute("cartCount", cartCount);
            request.setAttribute("wishlistCount", wishlistCount);

            request.getRequestDispatcher("/WEB-INF/views/buyer/cart.jsp").forward(request, response);

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
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"success\": false, \"message\": \"Please login first\"}");
                return;
            }

            String action = request.getParameter("action");
            String bookId = request.getParameter("bookId");
            boolean success = true;
            String message = "";

            switch (action) {
                case "updateQuantity":
                    int quantity = Integer.parseInt(request.getParameter("quantity"));
                    if (quantity > 0) {
                        cartDAO.updateQuantity(user.getUserId(), bookId, quantity);
                        message = "Quantity updated successfully";
                    } else {
                        cartDAO.removeFromCart(user.getUserId(), bookId);
                        message = "Item removed from cart";
                    }
                    break;

                case "remove":
                    cartDAO.removeFromCart(user.getUserId(), bookId);
                    message = "Item removed from cart";
                    break;

                default:
                    success = false;
                    message = "Invalid action";
            }

            // Send JSON response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(String.format(
                "{\"success\": %b, \"message\": \"%s\", \"cartCount\": %d}",
                success,
                message,
                cartDAO.getCartItemCount(user.getUserId())
            ));

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Database error occurred\"}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid parameters\"}");
        }
    }
}