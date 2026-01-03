package com.bookstore.Servlets;

import com.bookstore.patterns.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Demonstration servlet showing the implemented design patterns:
 * 1. Factory Method Pattern - User Creation and Management
 * 2. Strategy Pattern - Payment Processing
 */
@WebServlet("/design-patterns-demo")
public class DesignPatternsDemoServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Design Patterns Demo - BookStore</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }");
        out.println(".container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        out.println(".pattern { border: 2px solid #007bff; margin: 20px 0; padding: 20px; border-radius: 8px; background: #f8f9fa; }");
        out.println(".pattern h3 { color: #007bff; margin-top: 0; border-bottom: 2px solid #007bff; padding-bottom: 10px; }");
        out.println(".demo-section { background: #e9ecef; padding: 15px; margin: 15px 0; border-radius: 5px; }");
        out.println("button { background: #007bff; color: white; padding: 12px 24px; border: none; border-radius: 5px; cursor: pointer; margin: 5px; font-size: 14px; }");
        out.println("button:hover { background: #0056b3; }");
        out.println(".success { color: #28a745; font-weight: bold; }");
        out.println(".info { color: #17a2b8; font-weight: bold; }");
        out.println(".code { background: #f8f9fa; padding: 10px; border-left: 4px solid #007bff; margin: 10px 0; font-family: monospace; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        
        out.println("<div class='container'>");
        out.println("<h1>🎯 Design Patterns Implementation Demo</h1>");
        out.println("<p>This page demonstrates the two design patterns implemented in the BookStore project:</p>");
        
        // Factory Method Pattern Demo
        out.println("<div class='pattern'>");
        out.println("<h3>🏭 Factory Method Pattern - User Creation and Management</h3>");
        out.println("<p>The Factory Method pattern provides an interface for creating objects in a superclass, ");
        out.println("but allows subclasses to alter the type of objects that will be created.</p>");
        
        out.println("<div class='demo-section'>");
        out.println("<h4>📋 Available User Factories:</h4>");
        
        try {
            String[] userTypes = UserFactoryProvider.getSupportedUserTypes();
            for (String userType : userTypes) {
                UserFactory factory = UserFactoryProvider.getFactory(userType);
                out.println("<p>✅ " + userType + " Factory: " + factory.getClass().getSimpleName() + "</p>");
            }
        } catch (Exception e) {
            out.println("<p>❌ Error loading factories: " + e.getMessage() + "</p>");
        }
        
        out.println("</div>");
        out.println("</div>");
        
        // Strategy Pattern Demo
        out.println("<div class='pattern'>");
        out.println("<h3>🎯 Strategy Pattern - Payment Processing</h3>");
        out.println("<p>The Strategy pattern defines a family of algorithms, encapsulates each one, ");
        out.println("and makes them interchangeable. Strategy lets the algorithm vary independently from clients that use it.</p>");
        
        out.println("<div class='demo-section'>");
        out.println("<h4>💳 Available Payment Strategies:</h4>");
        
        try {
            String[] paymentMethods = PaymentContext.getSupportedPaymentMethods();
            for (String method : paymentMethods) {
                PaymentContext context = new PaymentContext();
                context.setPaymentStrategy(method);
                out.println("<p>✅ " + method + " Strategy: " + context.getCurrentStrategy().getClass().getSimpleName() + "</p>");
            }
        } catch (Exception e) {
            out.println("<p>❌ Error loading strategies: " + e.getMessage() + "</p>");
        }
        
        out.println("</div>");
        out.println("</div>");
        
        // Demo Actions
        out.println("<div class='pattern'>");
        out.println("<h3>🚀 Pattern Demonstration</h3>");
        out.println("<p>Click the buttons below to see the patterns in action:</p>");
        
        out.println("<form method='post'>");
        out.println("<h4>Factory Method Pattern Tests:</h4>");
        out.println("<button type='submit' name='action' value='test_buyer_factory'>Test Buyer Factory</button>");
        out.println("<button type='submit' name='action' value='test_seller_factory'>Test Seller Factory</button>");
        out.println("<button type='submit' name='action' value='test_admin_factory'>Test Admin Factory</button>");
        
        out.println("<h4>Strategy Pattern Tests:</h4>");
        out.println("<button type='submit' name='action' value='test_card_strategy'>Test Card Payment Strategy</button>");
        out.println("<button type='submit' name='action' value='test_upi_strategy'>Test UPI Payment Strategy</button>");
        out.println("<button type='submit' name='action' value='test_cod_strategy'>Test COD Payment Strategy</button>");
        out.println("</form>");
        out.println("</div>");
        
        // Benefits Section
        out.println("<div class='pattern'>");
        out.println("<h3>📚 Pattern Benefits</h3>");
        out.println("<div class='demo-section'>");
        out.println("<h4>Factory Method Pattern Benefits:</h4>");
        out.println("<ul>");
        out.println("<li><strong>Flexibility:</strong> Easy to add new user types without modifying existing code</li>");
        out.println("<li><strong>Encapsulation:</strong> User creation logic is encapsulated in specific factories</li>");
        out.println("<li><strong>Maintainability:</strong> Each user type has its own creation logic</li>");
        out.println("<li><strong>Testability:</strong> Each factory can be tested independently</li>");
        out.println("</ul>");
        
        out.println("<h4>Strategy Pattern Benefits:</h4>");
        out.println("<ul>");
        out.println("<li><strong>Flexibility:</strong> Easy to add new payment methods</li>");
        out.println("<li><strong>Eliminates Switch Statements:</strong> No more complex if-else chains</li>");
        out.println("<li><strong>Open/Closed Principle:</strong> Open for extension, closed for modification</li>");
        out.println("<li><strong>Single Responsibility:</strong> Each strategy handles one payment method</li>");
        out.println("</ul>");
        out.println("</div>");
        out.println("</div>");
        
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Pattern Test Results</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }");
        out.println(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        out.println(".success { color: #28a745; font-weight: bold; }");
        out.println(".error { color: #dc3545; font-weight: bold; }");
        out.println(".info { color: #17a2b8; font-weight: bold; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<h2>Pattern Test Results</h2>");
        
        try {
            if (action.startsWith("test_") && action.endsWith("_factory")) {
                out.println("<h3>Factory Method Pattern Test</h3>");
                
                String userType = action.replace("test_", "").replace("_factory", "").toUpperCase();
                out.println("<p class='info'>Testing " + userType + " Factory...</p>");
                
                UserFactory factory = UserFactoryProvider.getFactory(userType);
                out.println("<p class='success'>✅ " + userType + " Factory created successfully!</p>");
                out.println("<p>Factory Type: " + factory.getClass().getSimpleName() + "</p>");
                out.println("<p>Handles User Type: " + factory.getUserType() + "</p>");
                
            } else if (action.startsWith("test_") && action.endsWith("_strategy")) {
                out.println("<h3>Strategy Pattern Test</h3>");
                
                String paymentMethod = action.replace("test_", "").replace("_strategy", "").toUpperCase();
                out.println("<p class='info'>Testing " + paymentMethod + " Payment Strategy...</p>");
                
                PaymentContext context = new PaymentContext();
                context.setPaymentStrategy(paymentMethod);
                
                out.println("<p class='success'>✅ " + paymentMethod + " Strategy created successfully!</p>");
                out.println("<p>Strategy Type: " + context.getCurrentStrategy().getClass().getSimpleName() + "</p>");
                out.println("<p>Payment Method: " + context.getCurrentStrategy().getPaymentMethod() + "</p>");
                
            } else {
                out.println("<p class='error'>❌ Unknown test action: " + action + "</p>");
            }
            
        } catch (Exception e) {
            out.println("<p class='error'>❌ Error during test: " + e.getMessage() + "</p>");
        }
        
        out.println("<br><a href='/design-patterns-demo'>← Back to Demo</a>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}
