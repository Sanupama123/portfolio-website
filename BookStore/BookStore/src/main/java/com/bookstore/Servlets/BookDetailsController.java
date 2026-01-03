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
import java.util.*;

@WebServlet("/buyer/book/*")
public class BookDetailsController extends HttpServlet {
    private BookDAO bookDAO;
    private CategoryDAO categoryDAO;
    private WishlistDAO wishlistDAO;
    private CartDAO cartDAO;
    private ReviewDAO reviewDAO;

    @Override
    public void init() throws ServletException {
        bookDAO = new BookDAO();
        categoryDAO = new CategoryDAO();
        wishlistDAO = new WishlistDAO();
        cartDAO = new CartDAO();
        reviewDAO = new ReviewDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User user = (User) session.getAttribute("user");
            if (!user.isBuyer()) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            // Extract book ID from URL
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendRedirect(request.getContextPath() + "/buyer/browse");
                return;
            }

            String bookId = pathInfo.substring(1);
            Book book = bookDAO.findById(bookId);
            if (book == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
                return;
            }

            // Get book category
            CategoryDAO.Category category = categoryDAO.getCategoryById(book.getCategoryId());

            // Get book reviews with user info
            List<Review> reviews = reviewDAO.getBookReviews(bookId);
            // No need to fetch user info separately as it's included in Review objects

            // Get average rating and review count
            double averageRating = reviewDAO.getAverageRating(bookId);
            int reviewCount = reviewDAO.getReviewCount(bookId);

            // Get rating distribution (1-5 stars)
            Map<Integer, Integer> ratingDistribution = reviewDAO.getRatingDistribution(bookId);

            // Check if user has already reviewed
            Review userReview = reviewDAO.getUserReviewForBook(user.getUserId(), bookId);
            boolean hasReviewed = userReview != null;

            // Get wishlist and cart status
            boolean inWishlist = wishlistDAO.isInWishlist(user.getUserId(), bookId);
            int cartQuantity = cartDAO.getCartItemQuantity(user.getUserId(), bookId);

            // Get similar books based on category
            List<Book> similarBooks = bookDAO.findByCategoryId(book.getCategoryId())
                .stream()
                .filter(b -> b.getBookId() != bookId)
                .limit(4)
                .toList();

            // Get ratings for similar books
            Map<String, Double> similarBooksRatings = new HashMap<>();
            for (Book similarBook : similarBooks) {
                similarBooksRatings.put(
                    similarBook.getBookId(),
                    reviewDAO.getAverageRating(similarBook.getBookId())
                );
            }

            // Get counts for navbar
            int cartCount = cartDAO.getCartItemCount(user.getUserId());
            int wishlistCount = wishlistDAO.getWishlistCount(user.getUserId());

            // Set attributes for the view
            request.setAttribute("book", book);
            request.setAttribute("category", category);
            request.setAttribute("reviews", reviews);
            request.setAttribute("averageRating", averageRating);
            request.setAttribute("reviewCount", reviewCount);
            request.setAttribute("ratingDistribution", ratingDistribution);
            request.setAttribute("hasReviewed", hasReviewed);
            request.setAttribute("userReview", userReview);
            request.setAttribute("inWishlist", inWishlist);
            request.setAttribute("cartQuantity", cartQuantity);
            request.setAttribute("similarBooks", similarBooks);
            request.setAttribute("similarBooksRatings", similarBooksRatings);
            request.setAttribute("cartCount", cartCount);
            request.setAttribute("wishlistCount", wishlistCount);

            request.getRequestDispatcher("/WEB-INF/views/buyer/book-details.jsp")
                  .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid book ID");
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Please login to continue");
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");
        String bookId = request.getParameter("bookId");

        try {
            String message;
            boolean success = true;

            switch (action) {
                case "addReview":
                    int rating = Integer.parseInt(request.getParameter("rating"));
                    String comment = request.getParameter("comment");
                    Review newReview = new Review();
                    newReview.setUserId(user.getUserId());
                    newReview.setBookId(bookId);
                    newReview.setRating(rating);
                    newReview.setReviewText(comment);
                    newReview.setVerifiedPurchase(true); // You may want to check this based on order history
                    reviewDAO.addReview(newReview);
                    message = "Review added successfully";
                    break;

                case "updateReview":
                    rating = Integer.parseInt(request.getParameter("rating"));
                    comment = request.getParameter("comment");
                    Review updatedReview = reviewDAO.getUserReviewForBook(user.getUserId(), bookId);
                    if (updatedReview != null) {
                        updatedReview.setRating(rating);
                        updatedReview.setReviewText(comment);
                        reviewDAO.updateReview(updatedReview);
                    }
                    message = "Review updated successfully";
                    break;

                case "deleteReview":
                    reviewDAO.deleteReviewByUserAndBook(user.getUserId(), bookId);
                    message = "Review deleted successfully";
                    break;

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
        }
    }
}