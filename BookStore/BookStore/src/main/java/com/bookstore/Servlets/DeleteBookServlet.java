package com.bookstore.Servlets;

import com.bookstore.dao.BookDAO;
import com.bookstore.models.Book;
import com.bookstore.models.User;
import com.bookstore.utils.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/seller/delete-book")
public class DeleteBookServlet extends HttpServlet {
    private BookDAO bookDAO;

    @Override
    public void init() throws ServletException {
        bookDAO = new BookDAO();
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

        try {
            String bookId = request.getParameter("bookId");
            Book book = bookDAO.getBook(bookId);

            if (book == null || !book.getSellerId().equals(seller.getUserId())) {
                session.setAttribute("errorMessage", "You don't have permission to delete this book.");
                response.sendRedirect(request.getContextPath() + "/seller/my-books");
                return;
            }

            // Delete the book cover image
            if (book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty()) {
                FileUploadUtil.deleteFile(book.getCoverImagePath(), getServletContext().getRealPath("/"));
            }

            // Delete the book from database
            if (bookDAO.deleteBook(bookId, seller.getUserId())) {
                session.setAttribute("successMessage", "Book deleted successfully!");
            } else {
                session.setAttribute("errorMessage", "Failed to delete the book.");
            }

        } catch (SQLException | NumberFormatException e) {
            session.setAttribute("errorMessage", "Error deleting book: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/seller/my-books");
    }
}