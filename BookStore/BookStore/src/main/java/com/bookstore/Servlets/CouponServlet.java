package com.bookstore.Servlets;

import com.bookstore.dao.DiscountCouponDAO;
import com.bookstore.models.DiscountCoupon;
import com.bookstore.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/buyer/apply-coupon")
public class CouponServlet extends HttpServlet {
    private DiscountCouponDAO couponDAO;

    @Override
    public void init() throws ServletException {
        couponDAO = new DiscountCouponDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\": false, \"message\": \"Please login first\"}");
                return;
            }

            String action = request.getParameter("action");
            String couponCode = request.getParameter("couponCode");
            double orderAmount = Double.parseDouble(request.getParameter("orderAmount"));

            if ("apply".equals(action)) {
                // Validate coupon
                String validationError = couponDAO.validateCoupon(couponCode, user.getUserId(), orderAmount);
                
                if (validationError != null) {
                    // Coupon is invalid
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(String.format(
                        "{\"success\": false, \"message\": \"%s\"}",
                        validationError
                    ));
                    return;
                }

                // Coupon is valid - calculate discount
                DiscountCoupon coupon = couponDAO.findByCode(couponCode);
                double discount = couponDAO.calculateDiscount(coupon, orderAmount);
                double newTotal = orderAmount - discount;

                // Store coupon in session
                session.setAttribute("appliedCoupon", coupon);
                session.setAttribute("discount", discount);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(String.format(
                    "{\"success\": true, \"message\": \"Coupon applied successfully!\", \"discount\": %.2f, \"newTotal\": %.2f, \"discountType\": \"%s\"}",
                    discount,
                    newTotal,
                    coupon.getDiscountType()
                ));

            } else if ("cancel".equals(action)) {
                // Remove coupon from session
                session.removeAttribute("appliedCoupon");
                session.removeAttribute("discount");

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(String.format(
                    "{\"success\": true, \"message\": \"Coupon removed\", \"discount\": 0.0, \"newTotal\": %.2f}",
                    orderAmount
                ));
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": false, \"message\": \"Database error occurred\"}");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid amount\"}");
        }
    }
}

