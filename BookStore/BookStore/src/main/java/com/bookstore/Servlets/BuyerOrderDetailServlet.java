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

@WebServlet("/buyer/order")
public class BuyerOrderDetailServlet extends HttpServlet {
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
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String idParam = request.getParameter("id");
            if (idParam == null) { response.sendRedirect(request.getContextPath() + "/buyer/orders"); return; }
            String orderId = idParam;

            Order order = orderDAO.getOrderWithItems(orderId);
            if (order == null || !order.getUserId().equals(user.getUserId())) {
                response.sendRedirect(request.getContextPath() + "/buyer/orders");
                return;
            }

            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/buyer/order-detail.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }

            String orderId = request.getParameter("id");
            String action = request.getParameter("action");

            if ("cancel".equals(action)) {
                orderDAO.cancelOrder(orderId, user.getUserId());
            } else if ("return".equals(action)) {
                orderDAO.requestReturn(orderId, user.getUserId());
            } else if ("delete".equals(action)) {
                orderDAO.deleteOrderBuyer(orderId, user.getUserId());
            }

            response.sendRedirect(request.getContextPath() + "/buyer/order?id=" + orderId);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}


