package com.bookstore.Servlets;

import com.bookstore.dao.OrderDAO;
import com.bookstore.models.Order;
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

@WebServlet("/admin/orders")
public class AdminOrdersServlet extends HttpServlet {
    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }
            // TODO: ensure admin role check if needed

            List<Order> orders = orderDAO.getAllOrders();
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            String orderId = request.getParameter("orderId");
            if ("updateStatus".equals(action)) {
                String status = request.getParameter("status");
                new OrderDAO().updateOrderStatus(orderId, status);
            } else if ("delete".equals(action)) {
                new OrderDAO().deleteOrderAdmin(orderId);
            }
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}


