package com.bookstore.patterns;

import com.bookstore.models.Payment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Context class that uses different payment strategies.
 * This is the Context in the Strategy pattern.
 */
public class PaymentContext {
    private PaymentStrategy strategy;
    private static Map<String, PaymentStrategy> strategies;
    
    static {
        strategies = new HashMap<>();
        strategies.put("CARD", new CardPaymentStrategy());
        strategies.put("UPI", new UPIPaymentStrategy());
        strategies.put("COD", new CODPaymentStrategy());
    }

    public void setPaymentStrategy(String paymentMethod) {
        this.strategy = strategies.get(paymentMethod.toUpperCase());
        if (this.strategy == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }
    }

    public boolean processPayment(Payment payment, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (strategy == null) {
            throw new IllegalStateException("Payment strategy not set");
        }
        
        // Validate payment data first
        if (!strategy.validatePaymentData(request)) {
            throw new Exception("Invalid payment data for " + strategy.getPaymentMethod());
        }
        
        // Process payment using the strategy
        return strategy.processPayment(payment, request, response);
    }
    
    /**
     * Get the current payment strategy
     * @return The current strategy
     */
    public PaymentStrategy getCurrentStrategy() {
        return strategy;
    }
    
    /**
     * Get all available payment methods
     * @return Array of supported payment methods
     */
    public static String[] getSupportedPaymentMethods() {
        return strategies.keySet().toArray(new String[0]);
    }
    
    /**
     * Check if a payment method is supported
     * @param paymentMethod The payment method to check
     * @return true if supported, false otherwise
     */
    public static boolean isPaymentMethodSupported(String paymentMethod) {
        return strategies.containsKey(paymentMethod.toUpperCase());
    }
}
