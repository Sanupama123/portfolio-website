package com.bookstore.Servlets;

import com.bookstore.dao.BookDAO;
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

@WebServlet("/seller/my-books")
public class MyBooksServlet extends HttpServlet {
    private BookDAO bookDAO;

    @Override
    public void init() throws ServletException {
        bookDAO = new BookDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User seller = (User) session.getAttribute("user");
        if (!seller.isSeller()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            List<Book> books = bookDAO.findBySellerId(seller.getUserId());
            request.setAttribute("books", books);
            request.getRequestDispatcher("/WEB-INF/views/seller/my-books.jsp").forward(request, response);
        } catch (SQLException e) {
            session.setAttribute("errorMessage", "Error loading books: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/seller/dashboard");
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

        User seller = (User) session.getAttribute("user");
        if (!seller.isSeller()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/seller/my-books");
            return;
        }

        try {
            boolean success = false;
            String message = "";

            switch (action) {
                case "updateStock":
                    String bookId = request.getParameter("bookId");
                    int quantity = Integer.parseInt(request.getParameter("quantity"));
                    success = bookDAO.updateStock(bookId, quantity);
                    message = success ? "Stock updated successfully." : "Failed to update stock.";
                    break;
                case "toggleActive":
                    bookId = request.getParameter("bookId");
                    boolean isActive = Boolean.parseBoolean(request.getParameter("isActive"));
                    success = bookDAO.toggleActive(bookId, isActive);
                    message = success ? 
                        (isActive ? "Book activated successfully." : "Book deactivated successfully.") : 
                        "Failed to update book status.";
                    break;
                case "delete":
                    bookId = request.getParameter("bookId");
                    success = bookDAO.deleteBook(bookId, seller.getUserId());
                    message = success ? "Book deleted successfully." : "Failed to delete book.";
                    break;
            }

            if (success) {
                session.setAttribute("successMessage", message);
            } else {
                session.setAttribute("errorMessage", message);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error processing request: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/seller/my-books");
    }
}