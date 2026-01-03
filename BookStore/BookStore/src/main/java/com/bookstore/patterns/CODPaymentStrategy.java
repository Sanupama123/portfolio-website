package com.bookstore.patterns;

import com.bookstore.models.Payment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Concrete Strategy for Cash on Delivery (COD) payment processing.
 * This is a Concrete Strategy in the Strategy pattern.
 */
public class CODPaymentStrategy implements PaymentStrategy {
    
    @Override
    public boolean processPayment(Payment payment, HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("Processing COD Payment...");
        
        // COD doesn't require immediate payment processing
        // Payment will be collected upon delivery
        
        // Set payment details for COD
        payment.setPaymentStatus("PENDING");
        payment.setTransactionId(generateTransactionId());
        
        System.out.println("COD payment registered successfully - payment pending until delivery");
        return true;
    }
    
    @Override
    public boolean validatePaymentData(HttpServletRequest request) {
        // COD doesn't require specific payment data validation
        // Just ensure the user is logged in and has items in cart
        return request.getSession().getAttribute("user") != null;
    }
    
    @Override
    public String getPaymentMethod() {
        return "COD";
    }
    
    /**
     * Generate transaction ID for COD payments
     */
    private String generateTransactionId() {
        return "COD-" + System.currentTimeMillis();
    }
}
