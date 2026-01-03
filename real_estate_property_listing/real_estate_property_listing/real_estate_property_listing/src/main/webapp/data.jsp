<%@ page import="com.realestate.model.User" %>
<%@ page import="com.realestate.util.Constants" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) session.getAttribute(Constants.SESSION_USER);
    if (user == null || !user.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=admin_required");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Data Management - Real Estate</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        body { background: #f4f8fb; font-family: 'Roboto', Arial, sans-serif; }
        .container {
            max-width: 700px;
            margin: 40px auto;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.07);
            padding: 32px 40px 40px 40px;
        }
        h2 { color: #1565c0; margin-bottom: 30px; text-align: center; }
        .data-actions {
            display: flex;
            flex-direction: column;
            gap: 18px;
            margin-top: 30px;
        }
        .btn-primary {
            background-color: #1976d2;
            color: #fff;
            border: none;
            padding: 10px 22px;
            border-radius: 4px;
            font-weight: 500;
            text-decoration: none;
            transition: background 0.2s;
            width: 100%;
            text-align: center;
        }
        .btn-primary:hover {
            background-color: #1565c0;
        }
        .info-box {
            background: #e8f0fe;
            color: #1565c0;
            border-radius: 6px;
            padding: 18px;
            margin-bottom: 18px;
            font-size: 16px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Data Management</h2>
        <div class="info-box">
            <i class="fas fa-info-circle"></i>
            Here you can backup, restore, or clean up your application's data files.
        </div>
        <div class="data-actions">
            <form method="post" action="data-backup-handler.jsp">
                <button type="submit" class="btn-primary"><i class="fas fa-download"></i> Backup Data</button>
            </form>
            <form method="post" action="data-restore-handler.jsp">
                <button type="submit" class="btn-primary"><i class="fas fa-upload"></i> Restore Data</button>
            </form>
            <form method="post" action="data-cleanup-handler.jsp" onsubmit="return confirm('Are you sure you want to clean up all data? This cannot be undone.');">
                <button type="submit" class="btn-primary" style="background:#d93025;"> <i class="fas fa-trash"></i> Clean Up Data</button>
            </form>
        </div>
    </div>
</body>
</html>