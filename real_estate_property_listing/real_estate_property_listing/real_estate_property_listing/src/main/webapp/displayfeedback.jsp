<%@ page import="com.realestate.service.FeedbackService" %>
<%@ page import="com.realestate.model.Feedback" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <title>Customer Feedbacks</title>
    <style>
        :root {
            --primary-blue: #2563eb;
            --light-blue: #3b82f6;
            --lighter-blue: #93c5fd;
            --white: #ffffff;
            --gray: #f3f4f6;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            background-color: var(--gray);
            color: #333;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }

        header {
            background-color: var(--primary-blue);
            color: var(--white);
            padding: 20px 0;
            text-align: center;
            margin-bottom: 30px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        h1 {
            margin: 0;
            font-size: 2.5rem;
        }

        .feedback-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 20px;
            margin-top: 30px;
        }

        .feedback-card {
            background-color: var(--white);
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            border-left: 4px solid var(--primary-blue);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            word-wrap: break-word;
            overflow-wrap: break-word;
        }

        .feedback-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
        }

        .feedback-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 1px solid var(--lighter-blue);
        }

        .feedback-user {
            font-weight: bold;
            color: var(--primary-blue);
            font-size: 1.1rem;
        }

        .feedback-email {
            color: #666;
            font-size: 0.9rem;
            word-break: break-all;
        }

        .feedback-date {
            color: #666;
            font-size: 0.8rem;
            white-space: nowrap;
            margin-left: 10px;
        }

        .feedback-message {
            line-height: 1.6;
            color: #444;
            word-wrap: break-word;
            overflow-wrap: break-word;
        }

        .back-btn {
            display: inline-block;
            margin-top: 30px;
            padding: 10px 20px;
            background-color: var(--primary-blue);
            color: var(--white);
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s;
        }

        .back-btn:hover {
            background-color: var(--light-blue);
        }

        .no-feedback {
            text-align: center;
            padding: 40px;
            color: #666;
            font-size: 1.2rem;
            grid-column: 1 / -1;
        }
    </style>
</head>
<body>
<header>
    <div class="container">
        <h1>Customer Feedbacks</h1>
    </div>
</header>

<div class="container">
    <%
        FeedbackService service = new FeedbackService();
        List<Feedback> feedbacks = service.getAllFeedback();

        if (feedbacks.isEmpty()) {
    %>
    <div class="no-feedback">
        <p>No feedbacks available yet. Be the first to share your thoughts!</p>
        <a href="feedback.jsp" class="back-btn">Submit Feedback</a>
    </div>
    <%
    } else {
    %>
    <div class="feedback-grid">
        <%
            for (Feedback feedback : feedbacks) {
        %>
        <div class="feedback-card">
            <div class="feedback-header">
                <div>
                    <div class="feedback-user"><%= feedback.getUsername() %></div>
                    <div class="feedback-email"><%= feedback.getEmail() %></div>
                </div>
                <div class="feedback-date"><%= feedback.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")) %></div>
            </div>
            <div class="feedback-message"><%= feedback.getMessage() %></div>
        </div>
        <%
            }
        %>
    </div>
    <a href="index.jsp" class="back-btn">Back to Home</a>
    <%
        }
    %>
</div>
</body>
</html>