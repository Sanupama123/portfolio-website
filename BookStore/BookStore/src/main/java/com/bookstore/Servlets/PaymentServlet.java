package com.bookstore.Servlets;

import com.bookstore.dao.*;
import com.bookstore.models.*;
import com.bookstore.patterns.PaymentContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/buyer/payment")
public class PaymentServlet extends HttpServlet {
    private PaymentDAO paymentDAO;
    private SavedCardDAO savedCardDAO;
    private OrderDAO orderDAO;
    private CartDAO cartDAO;
    private DiscountCouponDAO couponDAO;

    @Override
    public void init() throws ServletException {
        paymentDAO = new PaymentDAO();
        savedCardDAO = new SavedCardDAO();
        orderDAO = new OrderDAO();
        cartDAO = new CartDAO();
        couponDAO = new DiscountCouponDAO();
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

            // Check if buyer details are in session
            if (session.getAttribute("buyerDetailsId") == null) {
                response.sendRedirect(request.getContextPath() + "/buyer/buyer-details");
                return;
            }

            // Get saved cards
            List<SavedCard> savedCards = savedCardDAO.getSavedCardsByUserId(user.getUserId());
            request.setAttribute("savedCards", savedCards);

            request.getRequestDispatcher("/WEB-INF/views/buyer/payment.jsp").forward(request, response);

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

            String paymentMethod = request.getParameter("paymentMethod");
            
            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Please select a payment method");
                doGet(request, response);
                return;
            }

            // Get cart items and totals
            List<CartItem> cartItems = cartDAO.getCartItems(user.getUserId());
            if (cartItems.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/buyer/cart");
                return;
            }

            double subtotal = cartDAO.getCartTotal(user.getUserId());
            double shipping = 0.0; // Free shipping
            
            // Get discount from session
            DiscountCoupon appliedCoupon = (DiscountCoupon) session.getAttribute("appliedCoupon");
            Double discountAmount = (Double) session.getAttribute("discount");
            double discount = (discountAmount != null) ? discountAmount : 0.0;
            String couponCode = (appliedCoupon != null) ? appliedCoupon.getCode() : null;

            String buyerDetailsId = (String) session.getAttribute("buyerDetailsId");
            BuyerDetails buyerDetails = (BuyerDetails) session.getAttribute("buyerDetails");

            // Create Payment record
            Payment payment = new Payment();
            payment.setPaymentMethod(paymentMethod);
            payment.setAmount(subtotal - discount + shipping);

            // Use Strategy pattern for payment processing
            try {
                PaymentContext paymentContext = new PaymentContext();
                paymentContext.setPaymentStrategy(paymentMethod);
                
                // Process payment using the appropriate strategy
                boolean paymentSuccess = paymentContext.processPayment(payment, request, response);
                
                if (!paymentSuccess) {
                    request.setAttribute("errorMessage", "Payment processing failed");
                    doGet(request, response);
                    return;
                }
                
                System.out.println("Payment processed using Strategy pattern: " + paymentMethod);
                
            } catch (Exception e) {
                System.err.println("Error processing payment: " + e.getMessage());
                request.setAttribute("errorMessage", "Payment error: " + e.getMessage());
                doGet(request, response);
                return;
            }

            // Create the order with payment
            Order order = orderDAO.createOrderWithPayment(
                user.getUserId(),
                cartItems,
                subtotal,
                discount,
                shipping,
                couponCode,
                buyerDetails != null ? buyerDetails.getShippingAddress() : null,
                buyerDetailsId,
                null // Payment ID will be set after payment is created
            );

            // Set order ID in payment and create payment record
            payment.setOrderId(order.getOrderId());
            paymentDAO.createPayment(payment);

            // Record coupon usage if coupon was applied
            if (appliedCoupon != null && couponCode != null) {
                couponDAO.recordUsage(appliedCoupon.getCouponId(), user.getUserId(), order.getOrderId());
            }

            // Clear cart
            cartDAO.clearCart(user.getUserId());

            // Clear session data
            session.removeAttribute("appliedCoupon");
            session.removeAttribute("discount");
            session.removeAttribute("buyerDetailsId");
            session.removeAttribute("buyerDetails");
            session.removeAttribute("cartTotal");

            // Redirect to order confirmation page
            response.sendRedirect(request.getContextPath() + "/buyer/order-confirmation?orderId=" + order.getOrderId());

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error occurred", e);
        }
    }

}

