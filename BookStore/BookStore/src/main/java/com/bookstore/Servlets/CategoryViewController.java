package com.bookstore.Servlets;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.CategoryDAO;
import com.bookstore.dao.WishlistDAO;
import com.bookstore.dao.CartDAO;
import com.bookstore.models.Book;
import com.bookstore.models.User;
import com.bookstore.dao.CategoryDAO.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/buyer/category/*")
public class CategoryViewController extends HttpServlet {
    private BookDAO bookDAO;
    private CategoryDAO categoryDAO;
    private WishlistDAO wishlistDAO;
    private CartDAO cartDAO;

    @Override
    public void init() throws ServletException {
        bookDAO = new BookDAO();
        categoryDAO = new CategoryDAO();
        wishlistDAO = new WishlistDAO();
        cartDAO = new CartDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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

        try {
            // Get all categories for the sidebar
            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);

            // Get category ID from URL if present
            String pathInfo = request.getPathInfo();
            String categoryId = null;
            if (pathInfo != null && pathInfo.length() > 1) {
                try {
                    categoryId = pathInfo.substring(1);
                } catch (NumberFormatException e) {
                    // Invalid category ID, redirect to all categories
                    response.sendRedirect(request.getContextPath() + "/buyer/category");
                    return;
                }
            }

            // Get sort and filter parameters
            String sortBy = request.getParameter("sort");
            String minPrice = request.getParameter("minPrice");
            String maxPrice = request.getParameter("maxPrice");
            String searchQuery = request.getParameter("search");

            // Get books based on filters
            List<Book> books;
            if (categoryId != null) {
                books = bookDAO.findByCategoryId(categoryId);
                // Add category name to request for display
                Category category = categoryDAO.getCategoryById(categoryId);
                if (category != null) {
                    request.setAttribute("categoryName", category.getName());
                }
            } else if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                books = bookDAO.search(searchQuery);
            } else {
                books = bookDAO.findAll();
            }

            // Get wishlist status for each book
            Map<String, Boolean> wishlistStatus = new HashMap<>();
            for (Book book : books) {
                wishlistStatus.put(book.getBookId(), 
                    wishlistDAO.isInWishlist(user.getUserId(), book.getBookId()));
            }

            // Get cart and wishlist counts
            int cartCount = cartDAO.getCartItemCount(user.getUserId());
            int wishlistCount = wishlistDAO.getWishlistCount(user.getUserId());

            request.setAttribute("books", books);
            request.setAttribute("wishlistStatus", wishlistStatus);
            request.setAttribute("cartCount", cartCount);
            request.setAttribute("wishlistCount", wishlistCount);
            request.setAttribute("currentCategoryId", categoryId);

            request.getRequestDispatcher("/WEB-INF/views/buyer/category-view.jsp")
                  .forward(request, response);

        } catch (SQLException e) {
            session.setAttribute("errorMessage", "Error loading categories: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/buyer/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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

        String action = request.getParameter("action");
        try {
            switch (action) {
                case "addToCart":
                    handleAddToCart(request, response, user);
                    break;
                case "addToWishlist":
                    handleAddToWishlist(request, response, user);
                    break;
                case "removeFromWishlist":
                    handleRemoveFromWishlist(request, response, user);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (SQLException e) {
            session.setAttribute("errorMessage", "Error processing request: " + e.getMessage());
            response.sendRedirect(request.getRequestURI());
        }
    }

    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response, User user)
            throws SQLException, IOException {
        String bookId = request.getParameter("bookId");
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        cartDAO.addToCart(user.getUserId(), bookId, quantity);
        request.getSession().setAttribute("successMessage", "Book added to cart successfully!");
        response.sendRedirect(request.getHeader("Referer"));
    }

    private void handleAddToWishlist(HttpServletRequest request, HttpServletResponse response, User user)
            throws SQLException, IOException {
        String bookId = request.getParameter("bookId");
        wishlistDAO.addToWishlist(user.getUserId(), bookId);
        request.getSession().setAttribute("successMessage", "Book added to wishlist successfully!");
        response.sendRedirect(request.getHeader("Referer"));
    }

    private void handleRemoveFromWishlist(HttpServletRequest request, HttpServletResponse response, User user)
            throws SQLException, IOException {
        String bookId = request.getParameter("bookId");
        wishlistDAO.removeFromWishlist(user.getUserId(), bookId);
        request.getSession().setAttribute("successMessage", "Book removed from wishlist successfully!");
        response.sendRedirect(request.getHeader("Referer"));
    }
}