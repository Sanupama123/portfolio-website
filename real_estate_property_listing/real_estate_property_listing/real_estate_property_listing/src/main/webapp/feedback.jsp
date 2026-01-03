<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html>
<head>
    <title>User Feedback</title>
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
            background-color: var(--gray);
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            color: #333;
        }

        .feedback-container {
            background-color: var(--white);
            border-radius: 10px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
            padding: 30px;
            width: 100%;
            max-width: 600px;
            margin: 20px;
        }

        h2 {
            color: var(--primary-blue);
            text-align: center;
            margin-bottom: 25px;
            font-size: 2rem;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: #444;
        }

        input[type="text"],
        input[type="email"],
        textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 1rem;
            transition: border-color 0.3s;
        }

        input[type="text"]:focus,
        input[type="email"]:focus,
        textarea:focus {
            border-color: var(--primary-blue);
            outline: none;
            box-shadow: 0 0 0 2px var(--lighter-blue);
        }

        textarea {
            min-height: 120px;
            resize: vertical;
        }

        .submit-btn {
            background-color: var(--primary-blue);
            color: var(--white);
            border: none;
            padding: 12px 20px;
            font-size: 1rem;
            border-radius: 5px;
            cursor: pointer;
            width: 100%;
            transition: background-color 0.3s;
        }

        .submit-btn:hover {
            background-color: var(--light-blue);
        }

        .message {
            text-align: center;
            margin: 15px 0;
            padding: 10px;
            border-radius: 5px;
        }

        .success {
            background-color: #d1fae5;
            color: #065f46;
        }

        .error {
            background-color: #fee2e2;
            color: #b91c1c;
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: var(--primary-blue);
            text-decoration: none;
        }

        .back-link:hover {
            text-decoration: underline;
        }

        .admin-link {
            display: block;
            text-align: center;
            margin-top: 15px;
            color: #666;
            font-size: 0.9rem;
        }
    </style>
</head>
<body>
<div class="feedback-container">
    <h2>Submit Your Feedback</h2>

    <% if ("true".equals(request.getParameter("success"))) { %>
    <div class="message success">
        Thank you for your feedback!
    </div>
    <% } %>

    <% if (request.getParameter("error") != null) { %>
    <div class="message error">
        <%= request.getParameter("error") %>
    </div>
    <% } %>

    <form action="submitFeedback" method="post">
        <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required>
        </div>

        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" required>
        </div>

        <div class="form-group">
            <label for="message">Message</label>
            <textarea id="message" name="message" required></textarea>
        </div>

        <button type="submit" class="submit-btn">Submit Feedback</button>
    </form>

    <a href="index.jsp" class="back-link">Back to Home</a>
    <!--Comment<a href="${pageContext.request.contextPath}/admin/feedback" class="admin-link">Manage Feedback (Admin)</a>-->
</div>
</body>
</html>