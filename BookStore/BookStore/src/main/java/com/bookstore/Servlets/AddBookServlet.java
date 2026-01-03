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

@WebServlet("/seller/add-book")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 5 * 1024 * 1024,   // 5MB
    maxRequestSize = 10 * 1024 * 1024 // 10MB
)
public class AddBookServlet extends HttpServlet {
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
        // Check if user is logged in and is a seller
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
            // Load categories for the dropdown
            request.setAttribute("categories", categoryDAO.getAllCategories());
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading categories: " + e.getMessage());
        }
        
        // Forward to the add-book.jsp page
        request.getRequestDispatcher("/WEB-INF/views/seller/add-book.jsp").forward(request, response);
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
            // Validate required fields
            String title = request.getParameter("title");
            String author = request.getParameter("author");
            String isbn = request.getParameter("isbn");
            String description = request.getParameter("description");
            
            if (title == null || title.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Book title is required.");
                response.sendRedirect(request.getContextPath() + "/seller/add-book");
                return;
            }
            
            if (author == null || author.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Author name is required.");
                response.sendRedirect(request.getContextPath() + "/seller/add-book");
                return;
            }
            
            if (isbn == null || isbn.trim().isEmpty()) {
                session.setAttribute("errorMessage", "ISBN is required.");
                response.sendRedirect(request.getContextPath() + "/seller/add-book");
                return;
            }
            
            if (description == null || description.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Book description is required.");
                response.sendRedirect(request.getContextPath() + "/seller/add-book");
                return;
            }
            
            // Validate ISBN format (10 or 13 digits)
            if (!isbn.matches("^(?:\\d{10}|\\d{13})$")) {
                session.setAttribute("errorMessage", "ISBN must be exactly 10 or 13 digits.");
                response.sendRedirect(request.getContextPath() + "/seller/add-book");
                return;
            }
            
            Book book = new Book();
            book.setTitle(title.trim());
            book.setAuthor(author.trim());
            book.setIsbn(isbn.trim());
            book.setDescription(description.trim());
            book.setPrice(Double.parseDouble(request.getParameter("price")));
            book.setStockQuantity(Integer.parseInt(request.getParameter("stockQuantity")));
            book.setCategoryId(request.getParameter("categoryId"));
            book.setSellerId(seller.getUserId());
            book.setPublishedYear(Integer.parseInt(request.getParameter("publishedYear")));
            book.setPublisher(request.getParameter("publisher"));
            book.setLanguage(request.getParameter("language"));
            book.setPageCount(Integer.parseInt(request.getParameter("pageCount")));

            // Handle cover image upload
            Part filePart = request.getPart("coverImage");
            if (filePart != null && filePart.getSize() > 0) {
                String coverImagePath = FileUploadUtil.saveFile(filePart, getServletContext().getRealPath("/"));
                book.setCoverImagePath(coverImagePath);
            }

            bookDAO.create(book);
            session.setAttribute("successMessage", "Book added successfully!");
            response.sendRedirect(request.getContextPath() + "/seller/my-books");

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Please enter valid numbers for price, stock quantity, published year, and page count.");
            response.sendRedirect(request.getContextPath() + "/seller/add-book");
        } catch (SQLException e) {
            String errorMsg = e.getMessage();
            if (errorMsg.contains("UNIQUE KEY constraint") && errorMsg.contains("ISBN")) {
                session.setAttribute("errorMessage", "A book with this ISBN already exists. Please check the ISBN number.");
            } else if (errorMsg.contains("UNIQUE KEY constraint")) {
                session.setAttribute("errorMessage", "This book information already exists in the system. Please check your input.");
            } else {
                session.setAttribute("errorMessage", "Unable to save the book. Please try again or contact support.");
            }
            response.sendRedirect(request.getContextPath() + "/seller/add-book");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            response.sendRedirect(request.getContextPath() + "/seller/add-book");
        }
    }
}