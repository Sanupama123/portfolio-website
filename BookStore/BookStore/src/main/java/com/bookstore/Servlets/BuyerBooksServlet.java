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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/buyer/books")
public class BuyerBooksServlet extends HttpServlet {
    private BookDAO bookDAO;
    private ReviewDAO reviewDAO;
    private CartDAO cartDAO;
    private WishlistDAO wishlistDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        bookDAO = new BookDAO();
        reviewDAO = new ReviewDAO();
        cartDAO = new CartDAO();
        wishlistDAO = new WishlistDAO();
        categoryDAO = new CategoryDAO();
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

            // Get search and filter parameters
            String query = request.getParameter("q");
            String categoryId = null;
            Double minPrice = null, maxPrice = null, minRating = null;
            String sortBy = request.getParameter("sortBy");
            String sortOrder = request.getParameter("sortOrder");

            try { 
                String cid = request.getParameter("categoryId"); 
                if (cid != null && !cid.isEmpty()) 
                    categoryId = cid; 
            } catch (Exception ignored) {}
            
            try { 
                String mp = request.getParameter("minPrice"); 
                if (mp != null && !mp.isEmpty() && !mp.trim().equals("")) 
                    minPrice = Double.parseDouble(mp); 
            } catch (Exception ignored) {}
            
            try { 
                String xp = request.getParameter("maxPrice"); 
                if (xp != null && !xp.isEmpty() && !xp.trim().equals("")) 
                    maxPrice = Double.parseDouble(xp); 
            } catch (Exception ignored) {}
            
            try { 
                String mr = request.getParameter("minRating"); 
                if (mr != null && !mr.isEmpty() && !mr.trim().equals("")) 
                    minRating = Double.parseDouble(mr); 
            } catch (Exception ignored) {}

            // Get books based on search/filter criteria
            List<Book> books;
            if (query != null || categoryId != null || minPrice != null || maxPrice != null || minRating != null || sortBy != null) {
                books = bookDAO.searchBuyer(query, categoryId, minPrice, maxPrice, minRating, sortBy, sortOrder);
            } else {
                books = bookDAO.findAll();
            }
            
            // Get ratings for all books
            Map<String, Double> bookRatings = new HashMap<>();
            Map<String, Integer> reviewCounts = new HashMap<>();
            for (Book book : books) {
                double avgRating = reviewDAO.getAverageRating(book.getBookId());
                int reviewCount = reviewDAO.getReviewCount(book.getBookId());
                bookRatings.put(book.getBookId(), avgRating);
                reviewCounts.put(book.getBookId(), reviewCount);
            }

            // Get wishlist status for each book
            Map<String, Boolean> wishlistStatus = new HashMap<>();
            for (Book book : books) {
                wishlistStatus.put(book.getBookId(), 
                    wishlistDAO.isInWishlist(user.getUserId(), book.getBookId()));
            }

            // Get cart status for each book
            Map<String, Integer> cartStatus = new HashMap<>();
            for (Book book : books) {
                cartStatus.put(book.getBookId(), 
                    cartDAO.getCartItemQuantity(user.getUserId(), book.getBookId()));
            }

            // Get cart and wishlist counts for navbar
            int cartCount = cartDAO.getCartItemCount(user.getUserId());
            int wishlistCount = wishlistDAO.getWishlistCount(user.getUserId());

            // Set attributes
            request.setAttribute("books", books);
            request.setAttribute("bookRatings", bookRatings);
            request.setAttribute("reviewCounts", reviewCounts);
            request.setAttribute("wishlistStatus", wishlistStatus);
            request.setAttribute("cartStatus", cartStatus);
            request.setAttribute("cartCount", cartCount);
            request.setAttribute("wishlistCount", wishlistCount);
            
            // Set filter parameters for form persistence
            request.setAttribute("q", query);
            request.setAttribute("selectedCategoryId", categoryId);
            request.setAttribute("minPrice", minPrice);
            request.setAttribute("maxPrice", maxPrice);
            request.setAttribute("minRating", minRating);
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("sortOrder", sortOrder);
            request.setAttribute("categories", categoryDAO.getAllCategories());

            request.getRequestDispatcher("/WEB-INF/views/buyer/books.jsp").forward(request, response);

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
                case "addToCart":
                    int quantity = Integer.parseInt(request.getParameter("quantity"));
                    cartDAO.addToCart(user.getUserId(), bookId, quantity);
                    message = "Book added to cart successfully";
                    break;

                case "addToWishlist":
                    wishlistDAO.addToWishlist(user.getUserId(), bookId);
                    message = "Book added to wishlist";
                    break;

                case "removeFromWishlist":
                    wishlistDAO.removeFromWishlist(user.getUserId(), bookId);
                    message = "Book removed from wishlist";
                    break;

                case "rate":
                    int rating = Integer.parseInt(request.getParameter("rating"));
                    String reviewText = request.getParameter("reviewText");
                    if (rating < 1 || rating > 5) {
                        throw new IllegalArgumentException("Rating must be between 1 and 5");
                    }
                    reviewDAO.addOrUpdateReview(user.getUserId(), bookId, rating, reviewText);
                    message = "Rating submitted successfully";
                    break;

                default:
                    success = false;
                    message = "Invalid action";
            }

            // Send JSON response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(String.format(
                "{\"success\": %b, \"message\": \"%s\", \"cartCount\": %d, \"wishlistCount\": %d}",
                success,
                message,
                cartDAO.getCartItemCount(user.getUserId()),
                wishlistDAO.getWishlistCount(user.getUserId())
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