package com.bookstore.Servlets;

import com.bookstore.dao.SavedCardDAO;
import com.bookstore.models.SavedCard;
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
 * Test servlet to demonstrate saved cards functionality.
 * This servlet can be used to test the delete functionality.
 */
@WebServlet("/test-saved-cards")
public class SavedCardsTestServlet extends HttpServlet {
    private SavedCardDAO savedCardDAO;
    
    @Override
    public void init() throws ServletException {
        this.savedCardDAO = new SavedCardDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Saved Cards Test</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println(".card { border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 8px; }");
        out.println(".delete-btn { background: #dc3545; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; }");
        out.println(".delete-btn:hover { background: #c82333; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            if (user == null) {
                out.println("<h2>Please log in to view saved cards</h2>");
                out.println("<a href='/login'>Login</a>");
                return;
            }
            
            out.println("<h2>Saved Cards for User: " + user.getUsername() + "</h2>");
            
            // Get saved cards
            java.util.List<SavedCard> savedCards = savedCardDAO.getSavedCardsByUserId(user.getUserId());
            
            if (savedCards.isEmpty()) {
                out.println("<p>No saved cards found.</p>");
            } else {
                out.println("<p>Found " + savedCards.size() + " saved card(s):</p>");
                
                for (SavedCard card : savedCards) {
                    out.println("<div class='card'>");
                    out.println("<h4>" + card.getCardType() + " Card</h4>");
                    out.println("<p><strong>Card Number:</strong> **** **** **** " + card.getCardNumber() + "</p>");
                    out.println("<p><strong>Card Holder:</strong> " + card.getCardHolderName() + "</p>");
                    out.println("<p><strong>Expiry Date:</strong> " + card.getExpiryDate() + "</p>");
                    out.println("<p><strong>Card ID:</strong> " + card.getCardId() + "</p>");
                    out.println("<button class='delete-btn' onclick='deleteCard(\"" + card.getCardId() + "\")'>Delete Card</button>");
                    out.println("</div>");
                }
            }
            
            out.println("<br><a href='/buyer/payment'>Go to Payment Page</a>");
            
        } catch (SQLException e) {
            out.println("<h2>Error loading saved cards</h2>");
            out.println("<p>Error: " + e.getMessage() + "</p>");
        }
        
        out.println("<script>");
        out.println("function deleteCard(cardId) {");
        out.println("    if (confirm('Are you sure you want to delete this card?')) {");
        out.println("        fetch('/buyer/delete-saved-card', {");
        out.println("            method: 'POST',");
        out.println("            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },");
        out.println("            body: 'cardId=' + encodeURIComponent(cardId)");
        out.println("        })");
        out.println("        .then(response => response.json())");
        out.println("        .then(data => {");
        out.println("            if (data.success) {");
        out.println("                alert('Card deleted successfully');");
        out.println("                location.reload();");
        out.println("            } else {");
        out.println("                alert('Error: ' + data.message);");
        out.println("            }");
        out.println("        })");
        out.println("        .catch(error => {");
        out.println("            alert('Error: ' + error.message);");
        out.println("        });");
        out.println("    }");
        out.println("}");
        out.println("</script>");
        
        out.println("</body>");
        out.println("</html>");
    }
}
