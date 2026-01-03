package com.bookstore.Servlets;

import com.bookstore.dao.SavedCardDAO;
import com.bookstore.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Servlet to handle deletion of saved cards.
 * Provides secure deletion with user verification.
 */
@WebServlet("/buyer/delete-saved-card")
public class DeleteSavedCardServlet extends HttpServlet {
    private SavedCardDAO savedCardDAO;
    
    @Override
    public void init() throws ServletException {
        this.savedCardDAO = new SavedCardDAO();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            // Check if user is logged in
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println("{\"success\": false, \"message\": \"User not logged in\"}");
                return;
            }
            
            // Get card ID from request
            String cardId = request.getParameter("cardId");
            if (cardId == null || cardId.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"success\": false, \"message\": \"Card ID is required\"}");
                return;
            }
            
            // Verify that the card belongs to the current user
            com.bookstore.models.SavedCard card = savedCardDAO.getSavedCardById(cardId);
            if (card == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("{\"success\": false, \"message\": \"Card not found\"}");
                return;
            }
            
            if (!card.getUserId().equals(user.getUserId())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.println("{\"success\": false, \"message\": \"Unauthorized access to card\"}");
                return;
            }
            
            // Delete the card
            savedCardDAO.deleteCard(cardId, user.getUserId());
            
            // Return success response
            out.println("{\"success\": true, \"message\": \"Card deleted successfully\"}");
            
        } catch (SQLException e) {
            System.err.println("Error deleting saved card: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"success\": false, \"message\": \"Database error occurred\"}");
        } catch (Exception e) {
            System.err.println("Unexpected error deleting saved card: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"success\": false, \"message\": \"An unexpected error occurred\"}");
        }
    }
}
