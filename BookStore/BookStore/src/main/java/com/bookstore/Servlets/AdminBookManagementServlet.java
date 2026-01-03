package com.bookstore.Servlets;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.CategoryDAO;
import com.bookstore.models.Book;
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

@WebServlet("/admin/books")
public class AdminBookManagementServlet extends HttpServlet {
    private BookDAO bookDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        bookDAO = new BookDAO();
        categoryDAO = new CategoryDAO();
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
        if (!user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            String q = request.getParameter("q");
            String categoryId = null;
            Double minPrice = null, maxPrice = null, minRating = null;
            Boolean isActive = null;

            try { String cid = request.getParameter("categoryId"); if (cid != null && !cid.isEmpty()) categoryId = cid; } catch (Exception ignored) {}
            try { String mp = request.getParameter("minPrice"); if (mp != null && !mp.isEmpty() && !mp.trim().equals("")) minPrice = Double.parseDouble(mp); } catch (Exception ignored) {}
            try { String xp = request.getParameter("maxPrice"); if (xp != null && !xp.isEmpty() && !xp.trim().equals("")) maxPrice = Double.parseDouble(xp); } catch (Exception ignored) {}
            try { String mr = request.getParameter("minRating"); if (mr != null && !mr.isEmpty() && !mr.trim().equals("")) minRating = Double.parseDouble(mr); } catch (Exception ignored) {}
            try { String av = request.getParameter("active"); if (av != null && !av.isEmpty()) isActive = Boolean.parseBoolean(av); } catch (Exception ignored) {}

            List<Book> books = bookDAO.searchAdmin(q, categoryId, minPrice, maxPrice, minRating, isActive);
            request.setAttribute("q", q);
            request.setAttribute("selectedCategoryId", categoryId);
            request.setAttribute("minPrice", minPrice);
            request.setAttribute("maxPrice", maxPrice);
            request.setAttribute("minRating", minRating);
            request.setAttribute("active", isActive);
            request.setAttribute("books", books);
            request.setAttribute("categories", categoryDAO.getAllCategories());
            request.getRequestDispatcher("/WEB-INF/views/admin/books.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Failed to load books. Please try again later.");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
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
        if (!user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String action = request.getParameter("action");
        String bookId = request.getParameter("bookId");

        try {
            boolean success = false;
            String message = "";

            switch (action) {
                case "delete":
                    success = bookDAO.deleteBook(bookId, null); // Admin can delete any book (pass null for admin)
                    message = success ? "Book deleted successfully." : "Failed to delete book.";
                    break;
                case "toggleActive":
                    boolean isActive = Boolean.parseBoolean(request.getParameter("isActive"));
                    success = bookDAO.toggleActive(bookId, isActive);
                    message = success ? 
                        (isActive ? "Book activated successfully." : "Book deactivated successfully.") : 
                        "Failed to update book status.";
                    break;
                case "updateStock":
                    int newQuantity = Integer.parseInt(request.getParameter("quantity"));
                    success = bookDAO.updateStock(bookId, newQuantity);
                    message = success ? "Stock updated successfully." : "Failed to update stock.";
                    break;
                case "updateCategory":
                    String categoryId = request.getParameter("categoryId");
                    Book book = bookDAO.getBook(bookId);
                    if (book != null) {
                        book.setCategoryId(categoryId);
                        success = bookDAO.adminUpdateBook(book);
                        message = success ? "Category updated successfully." : "Failed to update category.";
                    }
                    break;
            }

            if (success) {
                session.setAttribute("successMessage", message);
            } else {
                session.setAttribute("errorMessage", message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "An error occurred. Please try again later.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/books");
    }
}