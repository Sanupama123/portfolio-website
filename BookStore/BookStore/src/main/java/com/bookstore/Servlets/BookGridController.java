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
import java.util.stream.Collectors;

@WebServlet("/buyer/browse/*")
public class BookGridController extends HttpServlet {
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

            // Get all available categories
            List<CategoryDAO.Category> categories = categoryDAO.getAllCategories();
            
            // Parse filters from request
            Map<String, String> filters = parseFilters(request);
            
            // Get filtered and sorted books
            List<Book> books = getFilteredBooks(filters);
            
            // Get average ratings for all books
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
            Map<String, Boolean> cartStatus = new HashMap<>();
            for (Book book : books) {
                cartStatus.put(book.getBookId(), 
                    cartDAO.isInCart(user.getUserId(), book.getBookId()));
            }

            // Get counts for navbar
            int cartCount = cartDAO.getCartItemCount(user.getUserId());
            int wishlistCount = wishlistDAO.getWishlistCount(user.getUserId());

            // Set attributes for the view
            request.setAttribute("books", books);
            request.setAttribute("categories", categories);
            request.setAttribute("filters", filters);
            request.setAttribute("bookRatings", bookRatings);
            request.setAttribute("reviewCounts", reviewCounts);
            request.setAttribute("wishlistStatus", wishlistStatus);
            request.setAttribute("cartStatus", cartStatus);
            request.setAttribute("cartCount", cartCount);
            request.setAttribute("wishlistCount", wishlistCount);

            request.getRequestDispatcher("/WEB-INF/views/buyer/book-grid.jsp")
                  .forward(request, response);

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

    private Map<String, String> parseFilters(HttpServletRequest request) {
        Map<String, String> filters = new HashMap<>();
        
        // Category filter
        String categoryId = request.getParameter("category");
        if (categoryId != null && !categoryId.isEmpty()) {
            filters.put("category", categoryId);
        }

        // Price range
        String minPrice = request.getParameter("minPrice");
        String maxPrice = request.getParameter("maxPrice");
        if (minPrice != null && !minPrice.isEmpty()) filters.put("minPrice", minPrice);
        if (maxPrice != null && !maxPrice.isEmpty()) filters.put("maxPrice", maxPrice);

        // Search query
        String query = request.getParameter("q");
        if (query != null && !query.isEmpty()) filters.put("query", query);

        // Sort order
        String sortBy = request.getParameter("sort");
        if (sortBy != null && !sortBy.isEmpty()) filters.put("sort", sortBy);

        return filters;
    }

    private List<Book> getFilteredBooks(Map<String, String> filters) throws SQLException {
        List<Book> books;

        // First get the base list of books
        if (filters.containsKey("category")) {
            books = bookDAO.findByCategoryId(filters.get("category"));
        } else if (filters.containsKey("query")) {
            books = bookDAO.search(filters.get("query"));
        } else {
            books = bookDAO.findAll();
        }

        // Apply price filters
        if (filters.containsKey("minPrice") || filters.containsKey("maxPrice")) {
            double minPrice = filters.containsKey("minPrice") ? 
                Double.parseDouble(filters.get("minPrice")) : 0;
            double maxPrice = filters.containsKey("maxPrice") ? 
                Double.parseDouble(filters.get("maxPrice")) : Double.MAX_VALUE;

            books = books.stream()
                .filter(book -> book.getPrice() >= minPrice && book.getPrice() <= maxPrice)
                .collect(Collectors.toList());
        }

        // Apply sorting
        if (filters.containsKey("sort")) {
            switch (filters.get("sort")) {
                case "price_asc":
                    books.sort(Comparator.comparing(Book::getPrice));
                    break;
                case "price_desc":
                    books.sort(Comparator.comparing(Book::getPrice).reversed());
                    break;
                case "title":
                    books.sort(Comparator.comparing(Book::getTitle));
                    break;
                case "rating":
                    // Note: This would be more efficient if done at the database level
                    books.sort((b1, b2) -> {
                        try {
                            return Double.compare(
                                reviewDAO.getAverageRating(b2.getBookId()),
                                reviewDAO.getAverageRating(b1.getBookId())
                            );
                        } catch (SQLException e) {
                            return 0;
                        }
                    });
                    break;
            }
        }

        return books;
    }
}