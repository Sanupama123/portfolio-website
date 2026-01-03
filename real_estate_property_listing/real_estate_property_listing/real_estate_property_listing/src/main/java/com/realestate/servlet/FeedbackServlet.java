package com.realestate.servlet;

import com.realestate.model.Feedback;
import com.realestate.service.FeedbackService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@WebServlet("/submitFeedback")
public class FeedbackServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(FeedbackServlet.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final String FEEDBACK_PAGE = "feedback.jsp";
    private final FeedbackService service;

    public FeedbackServlet() {
        this.service = new FeedbackService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String message = request.getParameter("message");

        // Basic validation
        if (isEmpty(username) || isEmpty(email) || isEmpty(message)) {
            response.sendRedirect(FEEDBACK_PAGE + "?error=Please+fill+all+fields");
            return;
        }

        // Email format validation
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            response.sendRedirect(FEEDBACK_PAGE + "?error=Invalid+email+format");
            return;
        }

        // Sanitize inputs
        username = sanitizeInput(username);
        email = sanitizeInput(email);
        message = sanitizeInput(message);

        try {
            Feedback feedback = new Feedback(username, email, message);
            service.saveFeedback(feedback);
            response.sendRedirect(FEEDBACK_PAGE + "?success=true");
        } catch (Exception e) {
            LOGGER.severe("Failed to submit feedback: " + e.getMessage());
            response.sendRedirect(FEEDBACK_PAGE + "?error=Failed+to+submit+feedback");
        }
    }

    private boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[<>\"&]", "");
    }
}
