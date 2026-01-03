package com.bookstore.patterns;

import com.bookstore.dao.SavedCardDAO;
import com.bookstore.models.Payment;
import com.bookstore.models.SavedCard;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Concrete Strategy for Credit/Debit Card payment processing.
 * This is a Concrete Strategy in the Strategy pattern.
 */
public class CardPaymentStrategy implements PaymentStrategy {
    private SavedCardDAO savedCardDAO;
    
    public CardPaymentStrategy() {
        this.savedCardDAO = new SavedCardDAO();
    }
    
    @Override
    public boolean processPayment(Payment payment, HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("Processing Card Payment...");
        
        String savedCardId = request.getParameter("savedCard");
        
        if (savedCardId != null && !savedCardId.isEmpty()) {
            // Using saved card - verify it still exists
            com.bookstore.models.SavedCard savedCard = savedCardDAO.getSavedCardById(savedCardId);
            if (savedCard == null) {
                throw new Exception("Selected saved card no longer exists. Please select a different card or add a new one.");
            }
            
            payment.setCardId(savedCardId);
            payment.setPaymentStatus("COMPLETED");
            payment.setTransactionId(generateTransactionId());
            System.out.println("Using saved card: " + savedCardId);
            
        } else {
            // New card processing
            String cardHolderName = request.getParameter("cardHolderName");
            String cardNumber = request.getParameter("cardNumber");
            String expiryDate = request.getParameter("expiryDate");
            String cvv = request.getParameter("cvv");
            String saveCard = request.getParameter("saveCard");
            
            // Validate card details
            if (!validateCardDetails(cardHolderName, cardNumber, expiryDate, cvv)) {
                throw new Exception("Invalid card details provided");
            }
            
            // Determine card type
            String cardType = getCardType(cardNumber);
            System.out.println("Card type detected: " + cardType);
            
            // Save card if requested
            if ("on".equals(saveCard)) {
                SavedCard newCard = new SavedCard();
                newCard.setUserId(request.getSession().getAttribute("user") != null ? 
                    ((com.bookstore.models.User) request.getSession().getAttribute("user")).getUserId() : null);
                newCard.setCardHolderName(cardHolderName);
                newCard.setCardNumber(cardNumber.substring(cardNumber.length() - 4)); // Last 4 digits
                newCard.setExpiryDate(expiryDate);
                newCard.setCardType(cardType);
                
                savedCardDAO.saveCard(newCard);
                System.out.println("Card saved for future use");
            }
            
            payment.setPaymentStatus("COMPLETED");
            payment.setTransactionId(generateTransactionId());
        }
        
        System.out.println("Card payment processed successfully");
        return true;
    }
    
    @Override
    public boolean validatePaymentData(HttpServletRequest request) {
        String savedCardId = request.getParameter("savedCard");
        
        if (savedCardId != null && !savedCardId.isEmpty()) {
            // Using saved card - no additional validation needed
            return true;
        }
        
        // Validate new card details
        String cardHolderName = request.getParameter("cardHolderName");
        String cardNumber = request.getParameter("cardNumber");
        String expiryDate = request.getParameter("expiryDate");
        String cvv = request.getParameter("cvv");
        
        return validateCardDetails(cardHolderName, cardNumber, expiryDate, cvv);
    }
    
    @Override
    public String getPaymentMethod() {
        return "CARD";
    }
    
    /**
     * Validate card details
     */
    private boolean validateCardDetails(String cardHolderName, String cardNumber, String expiryDate, String cvv) {
        return cardHolderName != null && !cardHolderName.trim().isEmpty() &&
               cardNumber != null && !cardNumber.trim().isEmpty() &&
               expiryDate != null && !expiryDate.trim().isEmpty() &&
               cvv != null && !cvv.trim().isEmpty();
    }
    
    /**
     * Determine card type based on card number
     */
    private String getCardType(String cardNumber) {
        if (cardNumber.startsWith("4")) {
            return "VISA";
        } else if (cardNumber.startsWith("5")) {
            return "MASTERCARD";
        } else if (cardNumber.startsWith("3")) {
            return "AMEX";
        } else {
            return "OTHER";
        }
    }
    
    /**
     * Generate transaction ID for card payments
     */
    private String generateTransactionId() {
        return "CARD-" + System.currentTimeMillis();
    }
}
