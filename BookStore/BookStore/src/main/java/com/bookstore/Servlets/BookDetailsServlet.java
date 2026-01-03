package com.bookstore.Servlets;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.CategoryDAO;
import com.bookstore.models.Book;
import com.bookstore.models.User;
import com.bookstore.utils.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/seller/book-details")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 5 * 1024 * 1024,   // 5MB
    maxRequestSize = 10 * 1024 * 1024 // 10MB
)
public class BookDetailsServlet extends HttpServlet {
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
        if (!user.isSeller()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String bookIdStr = request.getParameter("id");
        if (bookIdStr == null || bookIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/seller/my-books");
            return;
        }

        try {
            String bookId = bookIdStr;
            Book book = bookDAO.getBook(bookId);

            if (book == null || !book.getSellerId().equals(user.getUserId())) {
                response.sendRedirect(request.getContextPath() + "/seller/my-books");
                return;
            }

            request.setAttribute("book", book);
            request.setAttribute("categories", categoryDAO.getAllCategories());
            request.getRequestDispatcher("/WEB-INF/views/seller/book-details.jsp").forward(request, response);

        } catch (SQLException | NumberFormatException e) {
            session.setAttribute("errorMessage", "Error loading book details: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/seller/my-books");
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

        try {
            String bookId = request.getParameter("bookId");
            Book existingBook = bookDAO.getBook(bookId);

            if (existingBook == null || !existingBook.getSellerId().equals(seller.getUserId())) {
                session.setAttribute("errorMessage", "You don't have permission to edit this book.");
                response.sendRedirect(request.getContextPath() + "/seller/my-books");
                return;
            }

            Book book = new Book();
            book.setBookId(bookId);
            book.setTitle(request.getParameter("title"));
            book.setAuthor(request.getParameter("author"));
            book.setIsbn(request.getParameter("isbn"));
            book.setDescription(request.getParameter("description"));
            book.setPrice(Double.parseDouble(request.getParameter("price")));
            book.setStockQuantity(Integer.parseInt(request.getParameter("stockQuantity")));
            book.setCategoryId(request.getParameter("categoryId"));
            book.setSellerId(seller.getUserId());
            book.setPublishedYear(Integer.parseInt(request.getParameter("publishedYear")));
            book.setPublisher(request.getParameter("publisher"));
            book.setLanguage(request.getParameter("language"));
            book.setPageCount(Integer.parseInt(request.getParameter("pageCount")));
            book.setCoverImagePath(existingBook.getCoverImagePath());

            // Handle cover image upload if a new image is provided
            Part filePart = request.getPart("coverImage");
            if (filePart != null && filePart.getSize() > 0) {
                // Delete old image if it exists
                FileUploadUtil.deleteFile(existingBook.getCoverImagePath(), getServletContext().getRealPath("/"));
                
                // Save new image
                String coverImagePath = FileUploadUtil.saveFile(filePart, getServletContext().getRealPath("/"));
                book.setCoverImagePath(coverImagePath);
            }

            bookDAO.updateBook(book);
            session.setAttribute("successMessage", "Book updated successfully!");
            response.sendRedirect(request.getContextPath() + "/seller/book-details?id=" + bookId);

        } catch (SQLException e) {
            String errorMsg = e.getMessage();
            if (errorMsg.contains("UNIQUE KEY constraint") && errorMsg.contains("ISBN")) {
                session.setAttribute("errorMessage", "A book with this ISBN already exists. Please check the ISBN number.");
            } else if (errorMsg.contains("UNIQUE KEY constraint")) {
                session.setAttribute("errorMessage", "This book information already exists in the system. Please check your input.");
            } else {
                session.setAttribute("errorMessage", "Unable to update the book. Please try again or contact support.");
            }
            response.sendRedirect(request.getContextPath() + "/seller/book-details?id=" + request.getParameter("bookId"));
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Please enter valid numbers for price, stock quantity, published year, and page count.");
            response.sendRedirect(request.getContextPath() + "/seller/book-details?id=" + request.getParameter("bookId"));
        } catch (Exception e) {
            session.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            response.sendRedirect(request.getContextPath() + "/seller/book-details?id=" + request.getParameter("bookId"));
        }
    }
}