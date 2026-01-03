package com.bookstore.Servlets;

import com.bookstore.dao.BuyerDetailsDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.dao.PaymentDAO;
import com.bookstore.models.BuyerDetails;
import com.bookstore.models.Order;
import com.bookstore.models.Payment;
import com.bookstore.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/buyer/order-confirmation")
public class OrderConfirmationServlet extends HttpServlet {
    private OrderDAO orderDAO;
    private BuyerDetailsDAO buyerDetailsDAO;
    private PaymentDAO paymentDAO;

    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
        buyerDetailsDAO = new BuyerDetailsDAO();
        paymentDAO = new PaymentDAO();
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

            String orderId = request.getParameter("orderId");
            if (orderId == null || orderId.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/buyer/orders");
                return;
            }

            // Get order with items
            Order order = orderDAO.getOrderWithItems(orderId);
            
            if (order == null || !order.getUserId().equals(user.getUserId())) {
                response.sendRedirect(request.getContextPath() + "/buyer/orders");
                return;
            }

            // Get buyer details if available
            if (order.getBuyerDetailsId() != null) {
                BuyerDetails buyerDetails = buyerDetailsDAO.getBuyerDetailsById(order.getBuyerDetailsId());
                request.setAttribute("buyerDetails", buyerDetails);
            }

            // Get payment information
            if (order.getPaymentId() != null) {
                Payment payment = paymentDAO.getPaymentById(order.getPaymentId());
                request.setAttribute("payment", payment);
            } else {
                // Try to get payment by order ID
                Payment payment = paymentDAO.getPaymentByOrderId(orderId);
                request.setAttribute("payment", payment);
            }

            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/buyer/order-confirmation.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }
}

