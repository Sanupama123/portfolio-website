package com.realestate.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Test servlet to help diagnose servlet and JSP access issues
 */
@WebServlet("/test/*")
public class TestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Servlet Test</title></head>");
        out.println("<body>");
        out.println("<h1>Servlet Test Page</h1>");
        out.println("<p>This page demonstrates that servlets are working correctly.</p>");
        out.println("<p>Requested Path: " + pathInfo + "</p>");
        out.println("<p>Context Path: " + request.getContextPath() + "</p>");
        out.println("<p>Servlet Path: " + request.getServletPath() + "</p>");
        out.println("<p>Request URI: " + request.getRequestURI() + "</p>");
        out.println("<hr>");
        out.println("<h2>Test Admin Links:</h2>");
        out.println("<ul>");
        out.println("<li><a href='" + request.getContextPath() + "/admin/users'>Admin Users</a></li>");
        out.println("<li><a href='" + request.getContextPath() + "/admin/properties'>Admin Properties</a></li>");
        out.println("<li><a href='" + request.getContextPath() + "/admin/data'>Admin Data</a></li>");
        out.println("<li><a href='" + request.getContextPath() + "/admin/settings'>Admin Settings</a></li>");
        out.println("<li><a href='" + request.getContextPath() + "/admin/add-admin'>Admin Add Admin</a></li>");
        out.println("</ul>");
        out.println("<hr>");
        out.println("<h2>Back to Dashboard</h2>");
        out.println("<p><a href='" + request.getContextPath() + "/dashboard.jsp'>Go to Dashboard</a></p>");
        out.println("</body>");
        out.println("</html>");
    }
} 