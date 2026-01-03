package com.bookstore.Servlets;

import com.bookstore.dao.BookDAO;
import com.bookstore.models.Book;
import com.bookstore.models.Review;
import com.bookstore.dao.ReviewDAO;
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

@WebServlet("/seller/reviews")
public class SellerReviewsServlet extends HttpServlet {
    private BookDAO bookDAO;
    private ReviewDAO reviewDAO;

    @Override
    public void init() throws ServletException {
        bookDAO = new BookDAO();
        reviewDAO = new ReviewDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (!user.isSeller()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            List<Book> myBooks = bookDAO.findBySellerId(user.getUserId());
            List<Review> reviews = reviewDAO.getReviewsForSeller(user.getUserId());
            request.setAttribute("myBooks", myBooks);
            request.setAttribute("reviews", reviews);
            request.getRequestDispatcher("/WEB-INF/views/seller/reviews.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}


