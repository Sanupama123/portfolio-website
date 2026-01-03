package com.bookstore.services;

import com.bookstore.dao.CategoryDAO;
import java.sql.SQLException;
import java.util.List;

/**
 * Service class for managing categories
 * Provides automatic category creation and management
 */
public class CategoryService {
    
    private static CategoryDAO categoryDAO = new CategoryDAO();
    
    /**
     * Ensures that default categories exist in the database
     * This method is called automatically when the application starts
     */
    public static void ensureDefaultCategories() {
        try {
            List<CategoryDAO.Category> existingCategories = categoryDAO.getAllCategories();
            
            if (existingCategories.isEmpty()) {
                System.out.println("No categories found, creating default categories automatically...");
                createDefaultCategories();
                System.out.println("Default categories created successfully");
            } else {
                System.out.println("Categories already exist (" + existingCategories.size() + " categories found)");
            }
        } catch (Exception e) {
            System.err.println("Error ensuring default categories: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates default categories automatically using IdGenerator
     */
    private static void createDefaultCategories() throws SQLException {
        String[][] defaultCategories = {
            {"Fiction", "Fictional literature including novels, short stories, and poetry"},
            {"Non-Fiction", "Non-fictional books including biographies, memoirs, and educational content"},
            {"Science Fiction", "Science fiction and fantasy literature"},
            {"Mystery", "Mystery and thriller novels"},
            {"Romance", "Romance novels and love stories"},
            {"Biography", "Biographical works and memoirs"},
            {"History", "Historical books and accounts"},
            {"Technology", "Technology, programming, and computer science books"},
            {"Business", "Business, management, and entrepreneurship books"},
            {"Self-Help", "Self-help, personal development, and motivational books"}
        };
        
        for (String[] categoryData : defaultCategories) {
            try {
                CategoryDAO.Category newCategory = categoryDAO.createCategory(categoryData[0]);
                System.out.println("Created category: " + newCategory.getId() + " - " + newCategory.getName());
            } catch (Exception e) {
                System.err.println("Error creating category " + categoryData[0] + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Creates a new category with automatic ID generation
     * @param name Category name
     * @return Created category
     */
    public static CategoryDAO.Category createCategory(String name) throws SQLException {
        return categoryDAO.createCategory(name);
    }
    
    /**
     * Gets all categories
     * @return List of all categories
     */
    public static List<CategoryDAO.Category> getAllCategories() throws SQLException {
        return categoryDAO.getAllCategories();
    }
    
    /**
     * Gets a category by ID
     * @param id Category ID
     * @return Category or null if not found
     */
    public static CategoryDAO.Category getCategoryById(String id) throws SQLException {
        return categoryDAO.getCategoryById(id);
    }
    
    /**
     * Updates a category
     * @param id Category ID
     * @param name New name
     * @return true if updated successfully
     */
    public static boolean updateCategory(String id, String name) throws SQLException {
        return categoryDAO.updateCategory(id, name);
    }
    
    /**
     * Deletes a category
     * @param id Category ID
     * @return true if deleted successfully
     */
    public static boolean deleteCategory(String id) throws SQLException {
        return categoryDAO.deleteCategory(id);
    }
}
