package com.bookstore.patterns;

import com.bookstore.models.Payment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Strategy interface for different payment processing methods.
 * This is the Strategy interface in the Strategy pattern.
 */
public interface PaymentStrategy {

    boolean processPayment(Payment payment, HttpServletRequest request, HttpServletResponse response) throws Exception;

    boolean validatePaymentData(HttpServletRequest request);

    String getPaymentMethod();
}


