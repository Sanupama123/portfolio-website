package com.bookstore.Servlets;

import com.bookstore.dao.CategoryDAO;
import com.bookstore.dao.CategoryDAO.Category;
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

@WebServlet("/admin/categories")
public class CategoryManagementServlet extends HttpServlet {
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
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
            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/WEB-INF/views/admin/categories.jsp").forward(request, response);
        } catch (SQLException e) {
            session.setAttribute("errorMessage", "Error loading categories: " + e.getMessage());
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
        try {
            boolean success = false;
            String message = "";

            switch (action) {
                case "create":
                    String name = request.getParameter("name");
                    Category newCategory = categoryDAO.createCategory(name);
                    success = newCategory != null && newCategory.getId() != null && !newCategory.getId().isEmpty();
                    message = success ? "Category created successfully." : "Failed to create category.";
                    break;

                case "update":
                    String categoryId = request.getParameter("categoryId");
                    name = request.getParameter("name");
                    success = categoryDAO.updateCategory(categoryId, name);
                    message = success ? "Category updated successfully." : "Failed to update category.";
                    break;

                case "delete":
                    categoryId = request.getParameter("categoryId");
                    success = categoryDAO.deleteCategory(categoryId);
                    message = success ? "Category deleted successfully." : 
                             "Failed to delete category. Make sure it's not assigned to any books.";
                    break;
            }

            if (success) {
                session.setAttribute("successMessage", message);
            } else {
                session.setAttribute("errorMessage", message);
            }
        } catch (SQLException e) {
            session.setAttribute("errorMessage", "Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid category ID.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/categories");
    }
}