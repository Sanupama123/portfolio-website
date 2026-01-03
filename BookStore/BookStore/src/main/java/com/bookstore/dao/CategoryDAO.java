package com.bookstore.dao;

import com.bookstore.utils.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private final String UPDATE_CATEGORY = "UPDATE Categories SET CategoryName = ? WHERE CategoryId = ?";
    private final String DELETE_CATEGORY = "DELETE FROM Categories WHERE CategoryId = ?";
    private final String GET_ALL_CATEGORIES = "SELECT * FROM Categories ORDER BY CategoryName";
    private final String GET_CATEGORY_BY_ID = "SELECT * FROM Categories WHERE CategoryId = ?";

    public static class Category {
        private String id;
        private String name;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public Category createCategory(String name) throws SQLException {
        String categoryId = com.bookstore.utils.IdGenerator.generateCategoryId();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Categories (CategoryId, CategoryName) VALUES (?, ?)")) {
            
            pstmt.setString(1, categoryId);
            pstmt.setString(2, name);
            pstmt.executeUpdate();

            Category category = new Category();
            category.setId(categoryId);
            category.setName(name);
            return category;
        }
    }

    public boolean updateCategory(String id, String name) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_CATEGORY)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, id);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteCategory(String id) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_CATEGORY)) {
            
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_ALL_CATEGORIES)) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getString("CategoryId"));
                    category.setName(rs.getString("CategoryName"));
                    categories.add(category);
                }
            }
        }
        return categories;
    }

    public Category getCategoryById(String id) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_CATEGORY_BY_ID)) {
            
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getString("CategoryId"));
                    category.setName(rs.getString("CategoryName"));
                    return category;
                }
            }
        }
        return null;
    }
}