package com.bookstore.patterns;

import com.bookstore.models.Payment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Concrete Strategy for UPI payment processing.
 * This is a Concrete Strategy in the Strategy pattern.
 */
public class UPIPaymentStrategy implements PaymentStrategy {
    
    @Override
    public boolean processPayment(Payment payment, HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("Processing UPI Payment...");
        
        String upiId = request.getParameter("upiId");
        if (upiId == null || upiId.trim().isEmpty()) {
            throw new Exception("UPI ID is required for UPI payment");
        }
        
        // Validate UPI ID format (basic validation)
        if (!isValidUPIId(upiId)) {
            throw new Exception("Invalid UPI ID format");
        }
        
        // Simulate UPI payment processing
        System.out.println("Processing UPI payment for: " + upiId);
        
        // Set payment details
        payment.setPaymentStatus("COMPLETED");
        payment.setTransactionId(generateTransactionId());
        
        System.out.println("UPI payment processed successfully");
        return true;
    }
    
    @Override
    public boolean validatePaymentData(HttpServletRequest request) {
        String upiId = request.getParameter("upiId");
        return upiId != null && !upiId.trim().isEmpty() && isValidUPIId(upiId);
    }
    
    @Override
    public String getPaymentMethod() {
        return "UPI";
    }
    
    /**
     * Validate UPI ID format
     * Basic validation for UPI ID (should contain @)
     */
    private boolean isValidUPIId(String upiId) {
        return upiId != null && upiId.contains("@") && upiId.length() > 5;
    }
    
    /**
     * Generate transaction ID for UPI payments
     */
    private String generateTransactionId() {
        return "UPI-" + System.currentTimeMillis();
    }
}
