package com.bookstore.Servlets;

import com.bookstore.dao.CartDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.models.CartItem;
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

@WebServlet("/buyer/checkout")
public class CheckoutServlet extends HttpServlet {
    private CartDAO cartDAO;
    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        cartDAO = new CartDAO();
        orderDAO = new OrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirect to buyer details page for the new payment flow
        response.sendRedirect(request.getContextPath() + "/buyer/buyer-details");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String address = request.getParameter("shippingAddress");
            if (address == null || address.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Shipping address is required");
                doGet(request, response);
                return;
            }

            List<CartItem> cartItems = cartDAO.getCartItems(user.getUserId());
            if (cartItems.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/buyer/cart");
                return;
            }
            double subtotal = cartDAO.getCartTotal(user.getUserId());
            double shipping = 0.0; // free shipping for now

            var order = orderDAO.createOrderFromCart(user.getUserId(), cartItems, subtotal, shipping, address);

            // clear cart
            cartDAO.clearCart(user.getUserId());

            response.sendRedirect(request.getContextPath() + "/buyer/orders?placed=" + order.getOrderNumber());
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}


