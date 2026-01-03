package com.bookstore.Servlets;

import com.bookstore.dao.PromotionDAO;
import com.bookstore.dao.DiscountCouponDAO;
import com.bookstore.dao.BookDAO;
import com.bookstore.models.Promotion;
import com.bookstore.models.DiscountCoupon;
import com.bookstore.models.Book;
import com.bookstore.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet("/admin/promotions")
public class AdminPromotionsServlet extends HttpServlet {
    private PromotionDAO promotionDAO;
    private DiscountCouponDAO couponDAO;
    private BookDAO bookDAO;

    @Override
    public void init() throws ServletException {
        promotionDAO = new PromotionDAO();
        couponDAO = new DiscountCouponDAO();
        bookDAO = new BookDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User current = (User) session.getAttribute("user");
        if (current == null || !current.isAdmin()) {
            response.sendRedirect(request.getContextPath());
            return;
        }

        try {
            String tab = request.getParameter("tab");
            if (tab == null || tab.isEmpty()) {
                tab = "coupons";
            }

            if ("coupons".equals(tab)) {
                List<DiscountCoupon> coupons = couponDAO.findAll();
                request.setAttribute("coupons", coupons);
            } else if ("flash-sales".equals(tab)) {
                List<Promotion> flashSales = promotionDAO.findByType("FLASH_SALE");
                request.setAttribute("flashSales", flashSales);
            } else if ("promotions".equals(tab)) {
                List<Promotion> promotions = promotionDAO.findByType("COUPON");
                request.setAttribute("promotions", promotions);
            }

            // Get all books for selection
            List<Book> allBooks = bookDAO.findAll();
            request.setAttribute("allBooks", allBooks);
            request.setAttribute("activeTab", tab);

            request.getRequestDispatcher("/WEB-INF/views/admin/promotions.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User current = (User) session.getAttribute("user");
        if (current == null || !current.isAdmin()) {
            response.sendRedirect(request.getContextPath());
            return;
        }

        String action = request.getParameter("action");
        String redirectTab = request.getParameter("tab");
        if (redirectTab == null || redirectTab.isEmpty()) {
            redirectTab = "coupons";
        }

        try {
            if ("create-coupon".equals(action)) {
                if (validateCouponData(request)) {
                    createCoupon(request);
                    request.getSession().setAttribute("successMessage", "Coupon created successfully!");
                } else {
                    return; // Validation failed, error messages already set
                }
            } else if ("update-coupon".equals(action)) {
                if (validateCouponData(request)) {
                    updateCoupon(request);
                    request.getSession().setAttribute("successMessage", "Coupon updated successfully!");
                } else {
                    return; // Validation failed, error messages already set
                }
            } else if ("delete-coupon".equals(action)) {
                deleteCoupon(request);
                request.getSession().setAttribute("successMessage", "Coupon deleted successfully!");
            } else if ("create-flash-sale".equals(action)) {
                if (validateFlashSaleData(request)) {
                    createFlashSale(request);
                    request.getSession().setAttribute("successMessage", "Flash sale created successfully!");
                } else {
                    return; // Validation failed, error messages already set
                }
            } else if ("update-flash-sale".equals(action)) {
                if (validateFlashSaleData(request)) {
                    updateFlashSale(request);
                    request.getSession().setAttribute("successMessage", "Flash sale updated successfully!");
                } else {
                    return; // Validation failed, error messages already set
                }
            } else if ("delete-flash-sale".equals(action)) {
                deleteFlashSale(request);
                request.getSession().setAttribute("successMessage", "Flash sale deleted successfully!");
            } else if ("create-promotion".equals(action)) {
                if (validatePromotionData(request)) {
                    createPromotion(request);
                    request.getSession().setAttribute("successMessage", "Promotion created successfully!");
                } else {
                    return; // Validation failed, error messages already set
                }
            } else if ("update-promotion".equals(action)) {
                if (validatePromotionData(request)) {
                    updatePromotion(request);
                    request.getSession().setAttribute("successMessage", "Promotion updated successfully!");
                } else {
                    return; // Validation failed, error messages already set
                }
            } else if ("delete-promotion".equals(action)) {
                deletePromotion(request);
                request.getSession().setAttribute("successMessage", "Promotion deleted successfully!");
            } else if ("toggle-active".equals(action)) {
                toggleActive(request);
            }
        } catch (SQLException e) {
            request.getSession().setAttribute("errorMessage", "Database error: " + e.getMessage());
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "An error occurred: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/promotions?tab=" + redirectTab);
    }

    private void createCoupon(HttpServletRequest request) throws SQLException {
        DiscountCoupon coupon = new DiscountCoupon();
        coupon.setCode(request.getParameter("code").toUpperCase());
        coupon.setDescription(request.getParameter("description"));
        coupon.setDiscountType(request.getParameter("discountType"));
        coupon.setDiscountValue(Double.parseDouble(request.getParameter("discountValue")));
        coupon.setMinOrderAmount(Double.parseDouble(request.getParameter("minOrderAmount")));
        
        String maxDiscountStr = request.getParameter("maxDiscountAmount");
        if (maxDiscountStr != null && !maxDiscountStr.isEmpty()) {
            coupon.setMaxDiscountAmount(Double.parseDouble(maxDiscountStr));
        }
        
        String usageLimitStr = request.getParameter("usageLimit");
        if (usageLimitStr != null && !usageLimitStr.isEmpty()) {
            coupon.setUsageLimit(Integer.parseInt(usageLimitStr));
        }
        
        coupon.setStartDate(LocalDateTime.parse(request.getParameter("startDate") + "T00:00:00"));
        coupon.setEndDate(LocalDateTime.parse(request.getParameter("endDate") + "T23:59:59"));
        coupon.setActive(true);
        
        couponDAO.create(coupon);
    }

    private void updateCoupon(HttpServletRequest request) throws SQLException {
        DiscountCoupon coupon = new DiscountCoupon();
        coupon.setCouponId(request.getParameter("couponId"));
        coupon.setCode(request.getParameter("code").toUpperCase());
        coupon.setDescription(request.getParameter("description"));
        coupon.setDiscountType(request.getParameter("discountType"));
        coupon.setDiscountValue(Double.parseDouble(request.getParameter("discountValue")));
        coupon.setMinOrderAmount(Double.parseDouble(request.getParameter("minOrderAmount")));
        
        String maxDiscountStr = request.getParameter("maxDiscountAmount");
        if (maxDiscountStr != null && !maxDiscountStr.isEmpty()) {
            coupon.setMaxDiscountAmount(Double.parseDouble(maxDiscountStr));
        }
        
        String usageLimitStr = request.getParameter("usageLimit");
        if (usageLimitStr != null && !usageLimitStr.isEmpty()) {
            coupon.setUsageLimit(Integer.parseInt(usageLimitStr));
        }
        
        coupon.setStartDate(LocalDateTime.parse(request.getParameter("startDate") + "T00:00:00"));
        coupon.setEndDate(LocalDateTime.parse(request.getParameter("endDate") + "T23:59:59"));
        coupon.setActive(true);
        
        couponDAO.update(coupon);
    }

    private void deleteCoupon(HttpServletRequest request) throws SQLException {
        String couponId = request.getParameter("couponId");
        couponDAO.delete(couponId);
    }

    private void createFlashSale(HttpServletRequest request) throws SQLException {
        Promotion promotion = new Promotion();
        promotion.setName(request.getParameter("name"));
        promotion.setDescription(request.getParameter("description"));
        promotion.setType("FLASH_SALE");
        promotion.setDiscountType(request.getParameter("discountType"));
        promotion.setDiscountValue(Double.parseDouble(request.getParameter("discountValue")));
        promotion.setMinOrderAmount(Double.parseDouble(request.getParameter("minOrderAmount")));
        
        String maxDiscountStr = request.getParameter("maxDiscountAmount");
        if (maxDiscountStr != null && !maxDiscountStr.isEmpty()) {
            promotion.setMaxDiscountAmount(Double.parseDouble(maxDiscountStr));
        }
        
        String usageLimitStr = request.getParameter("usageLimit");
        if (usageLimitStr != null && !usageLimitStr.isEmpty()) {
            promotion.setUsageLimit(Integer.parseInt(usageLimitStr));
        }
        
        promotion.setStartDate(LocalDateTime.parse(request.getParameter("startDate") + "T00:00:00"));
        promotion.setEndDate(LocalDateTime.parse(request.getParameter("endDate") + "T23:59:59"));
        promotion.setActive(true);
        
        promotionDAO.create(promotion);
        
        // Add books to promotion
        String[] bookIds = request.getParameterValues("bookIds");
        if (bookIds != null && bookIds.length > 0) {
            List<String> bookIdList = new ArrayList<>();
            for (String bookId : bookIds) {
                bookIdList.add(bookId);
            }
            promotionDAO.addBooksToPromotion(promotion.getPromotionId(), bookIdList);
        }
    }

    private void updateFlashSale(HttpServletRequest request) throws SQLException {
        Promotion promotion = new Promotion();
        promotion.setPromotionId(request.getParameter("promotionId"));
        promotion.setName(request.getParameter("name"));
        promotion.setDescription(request.getParameter("description"));
        promotion.setType("FLASH_SALE");
        promotion.setDiscountType(request.getParameter("discountType"));
        promotion.setDiscountValue(Double.parseDouble(request.getParameter("discountValue")));
        promotion.setMinOrderAmount(Double.parseDouble(request.getParameter("minOrderAmount")));
        
        String maxDiscountStr = request.getParameter("maxDiscountAmount");
        if (maxDiscountStr != null && !maxDiscountStr.isEmpty()) {
            promotion.setMaxDiscountAmount(Double.parseDouble(maxDiscountStr));
        }
        
        String usageLimitStr = request.getParameter("usageLimit");
        if (usageLimitStr != null && !usageLimitStr.isEmpty()) {
            promotion.setUsageLimit(Integer.parseInt(usageLimitStr));
        }
        
        promotion.setStartDate(LocalDateTime.parse(request.getParameter("startDate") + "T00:00:00"));
        promotion.setEndDate(LocalDateTime.parse(request.getParameter("endDate") + "T23:59:59"));
        promotion.setActive(true);
        
        promotionDAO.update(promotion);
        
        // Update books for promotion
        promotionDAO.removeBooksFromPromotion(promotion.getPromotionId());
        String[] bookIds = request.getParameterValues("bookIds");
        if (bookIds != null && bookIds.length > 0) {
            List<String> bookIdList = new ArrayList<>();
            for (String bookId : bookIds) {
                bookIdList.add(bookId);
            }
            promotionDAO.addBooksToPromotion(promotion.getPromotionId(), bookIdList);
        }
    }

    private void deleteFlashSale(HttpServletRequest request) throws SQLException {
        String promotionId = request.getParameter("promotionId");
        promotionDAO.delete(promotionId);
    }

    private void createPromotion(HttpServletRequest request) throws SQLException {
        Promotion promotion = new Promotion();
        promotion.setName(request.getParameter("name"));
        promotion.setDescription(request.getParameter("description"));
        promotion.setType("COUPON");
        promotion.setDiscountType(request.getParameter("discountType"));
        promotion.setDiscountValue(Double.parseDouble(request.getParameter("discountValue")));
        promotion.setMinOrderAmount(Double.parseDouble(request.getParameter("minOrderAmount")));
        
        String maxDiscountStr = request.getParameter("maxDiscountAmount");
        if (maxDiscountStr != null && !maxDiscountStr.isEmpty()) {
            promotion.setMaxDiscountAmount(Double.parseDouble(maxDiscountStr));
        }
        
        String usageLimitStr = request.getParameter("usageLimit");
        if (usageLimitStr != null && !usageLimitStr.isEmpty()) {
            promotion.setUsageLimit(Integer.parseInt(usageLimitStr));
        }
        
        promotion.setStartDate(LocalDateTime.parse(request.getParameter("startDate") + "T00:00:00"));
        promotion.setEndDate(LocalDateTime.parse(request.getParameter("endDate") + "T23:59:59"));
        promotion.setActive(true);
        
        promotionDAO.create(promotion);
    }

    private void updatePromotion(HttpServletRequest request) throws SQLException {
        Promotion promotion = new Promotion();
        promotion.setPromotionId(request.getParameter("promotionId"));
        promotion.setName(request.getParameter("name"));
        promotion.setDescription(request.getParameter("description"));
        promotion.setType("COUPON");
        promotion.setDiscountType(request.getParameter("discountType"));
        promotion.setDiscountValue(Double.parseDouble(request.getParameter("discountValue")));
        promotion.setMinOrderAmount(Double.parseDouble(request.getParameter("minOrderAmount")));
        
        String maxDiscountStr = request.getParameter("maxDiscountAmount");
        if (maxDiscountStr != null && !maxDiscountStr.isEmpty()) {
            promotion.setMaxDiscountAmount(Double.parseDouble(maxDiscountStr));
        }
        
        String usageLimitStr = request.getParameter("usageLimit");
        if (usageLimitStr != null && !usageLimitStr.isEmpty()) {
            promotion.setUsageLimit(Integer.parseInt(usageLimitStr));
        }
        
        promotion.setStartDate(LocalDateTime.parse(request.getParameter("startDate") + "T00:00:00"));
        promotion.setEndDate(LocalDateTime.parse(request.getParameter("endDate") + "T23:59:59"));
        promotion.setActive(true);
        
        promotionDAO.update(promotion);
    }

    private void deletePromotion(HttpServletRequest request) throws SQLException {
        String promotionId = request.getParameter("promotionId");
        promotionDAO.delete(promotionId);
    }

    private void toggleActive(HttpServletRequest request) throws SQLException {
        String type = request.getParameter("type");
        String id = request.getParameter("id");
        boolean active = Boolean.parseBoolean(request.getParameter("active"));
        
        if ("coupon".equals(type)) {
            DiscountCoupon coupon = couponDAO.findById(id);
            if (coupon != null) {
                coupon.setActive(active);
                couponDAO.update(coupon);
            }
        } else if ("promotion".equals(type)) {
            Promotion promotion = promotionDAO.findById(id);
            if (promotion != null) {
                promotion.setActive(active);
                promotionDAO.update(promotion);
            }
        }
    }

    // Validation methods
    private boolean validateCouponData(HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        
        // Validate coupon code
        String code = request.getParameter("code");
        if (code == null || code.trim().isEmpty()) {
            errors.add("Coupon code is required");
        } else {
            code = code.trim().toUpperCase();
            if (code.length() < 3 || code.length() > 20) {
                errors.add("Coupon code must be between 3 and 20 characters");
            } else if (!Pattern.matches("^[A-Z0-9_-]+$", code)) {
                errors.add("Coupon code can only contain uppercase letters, numbers, hyphens, and underscores");
            } else {
                // Check if code already exists (for create operations)
                try {
                    if (request.getParameter("action").equals("create-coupon")) {
                        DiscountCoupon existingCoupon = couponDAO.findByCode(code);
                        if (existingCoupon != null) {
                            errors.add("Coupon code already exists");
                        }
                    }
                } catch (SQLException e) {
                    errors.add("Error checking coupon code availability");
                }
            }
        }
        
        // Validate discount type and value
        String discountType = request.getParameter("discountType");
        if (discountType == null || discountType.trim().isEmpty()) {
            errors.add("Discount type is required");
        } else if (!discountType.equals("PERCENTAGE") && !discountType.equals("FIXED_AMOUNT")) {
            errors.add("Invalid discount type");
        }
        
        String discountValueStr = request.getParameter("discountValue");
        if (discountValueStr == null || discountValueStr.trim().isEmpty()) {
            errors.add("Discount value is required");
        } else {
            try {
                double discountValue = Double.parseDouble(discountValueStr);
                if (discountValue <= 0) {
                    errors.add("Discount value must be positive");
                } else if (discountType != null && discountType.equals("PERCENTAGE") && discountValue > 100) {
                    errors.add("Percentage discount cannot exceed 100%");
                }
            } catch (NumberFormatException e) {
                errors.add("Discount value must be a valid number");
            }
        }
        
        // Validate minimum order amount
        String minOrderAmountStr = request.getParameter("minOrderAmount");
        if (minOrderAmountStr == null || minOrderAmountStr.trim().isEmpty()) {
            errors.add("Minimum order amount is required");
        } else {
            try {
                double minOrderAmount = Double.parseDouble(minOrderAmountStr);
                if (minOrderAmount < 0) {
                    errors.add("Minimum order amount cannot be negative");
                }
            } catch (NumberFormatException e) {
                errors.add("Minimum order amount must be a valid number");
            }
        }
        
        // Validate max discount amount if provided
        String maxDiscountAmountStr = request.getParameter("maxDiscountAmount");
        if (maxDiscountAmountStr != null && !maxDiscountAmountStr.trim().isEmpty()) {
            try {
                double maxDiscountAmount = Double.parseDouble(maxDiscountAmountStr);
                if (maxDiscountAmount <= 0) {
                    errors.add("Maximum discount amount must be positive");
                } else if (discountValueStr != null && !discountValueStr.trim().isEmpty()) {
                    try {
                        double discountValue = Double.parseDouble(discountValueStr);
                        if (maxDiscountAmount < discountValue) {
                            errors.add("Maximum discount amount cannot be less than discount value");
                        }
                    } catch (NumberFormatException e) {
                        // Already handled above
                    }
                }
            } catch (NumberFormatException e) {
                errors.add("Maximum discount amount must be a valid number");
            }
        }
        
        // Validate usage limit if provided
        String usageLimitStr = request.getParameter("usageLimit");
        if (usageLimitStr != null && !usageLimitStr.trim().isEmpty()) {
            try {
                int usageLimit = Integer.parseInt(usageLimitStr);
                if (usageLimit <= 0) {
                    errors.add("Usage limit must be positive");
                }
            } catch (NumberFormatException e) {
                errors.add("Usage limit must be a valid integer");
            }
        }
        
        // Validate dates
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        
        if (startDateStr == null || startDateStr.trim().isEmpty()) {
            errors.add("Start date is required");
        }
        if (endDateStr == null || endDateStr.trim().isEmpty()) {
            errors.add("End date is required");
        }
        
        if (startDateStr != null && !startDateStr.trim().isEmpty() && 
            endDateStr != null && !endDateStr.trim().isEmpty()) {
            try {
                LocalDate startDate = LocalDate.parse(startDateStr);
                LocalDate endDate = LocalDate.parse(endDateStr);
                LocalDate today = LocalDate.now();
                
                if (startDate.isBefore(today)) {
                    errors.add("Start date cannot be in the past");
                }
                if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
                    errors.add("End date must be after start date");
                }
            } catch (Exception e) {
                errors.add("Invalid date format");
            }
        }
        
        if (!errors.isEmpty()) {
            request.getSession().setAttribute("errorMessage", String.join("; ", errors));
            return false;
        }
        
        return true;
    }
    
    private boolean validateFlashSaleData(HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        
        // Validate name
        String name = request.getParameter("name");
        if (name == null || name.trim().isEmpty()) {
            errors.add("Sale name is required");
        } else if (name.trim().length() < 3 || name.trim().length() > 100) {
            errors.add("Sale name must be between 3 and 100 characters");
        }
        
        // Validate discount type and value (same as coupon)
        String discountType = request.getParameter("discountType");
        if (discountType == null || discountType.trim().isEmpty()) {
            errors.add("Discount type is required");
        } else if (!discountType.equals("PERCENTAGE") && !discountType.equals("FIXED_AMOUNT")) {
            errors.add("Invalid discount type");
        }
        
        String discountValueStr = request.getParameter("discountValue");
        if (discountValueStr == null || discountValueStr.trim().isEmpty()) {
            errors.add("Discount value is required");
        } else {
            try {
                double discountValue = Double.parseDouble(discountValueStr);
                if (discountValue <= 0) {
                    errors.add("Discount value must be positive");
                } else if (discountType != null && discountType.equals("PERCENTAGE") && discountValue > 100) {
                    errors.add("Percentage discount cannot exceed 100%");
                }
            } catch (NumberFormatException e) {
                errors.add("Discount value must be a valid number");
            }
        }
        
        // Validate minimum order amount
        String minOrderAmountStr = request.getParameter("minOrderAmount");
        if (minOrderAmountStr == null || minOrderAmountStr.trim().isEmpty()) {
            errors.add("Minimum order amount is required");
        } else {
            try {
                double minOrderAmount = Double.parseDouble(minOrderAmountStr);
                if (minOrderAmount < 0) {
                    errors.add("Minimum order amount cannot be negative");
                }
            } catch (NumberFormatException e) {
                errors.add("Minimum order amount must be a valid number");
            }
        }
        
        // Validate max discount amount if provided (same as coupon)
        String maxDiscountAmountStr = request.getParameter("maxDiscountAmount");
        if (maxDiscountAmountStr != null && !maxDiscountAmountStr.trim().isEmpty()) {
            try {
                double maxDiscountAmount = Double.parseDouble(maxDiscountAmountStr);
                if (maxDiscountAmount <= 0) {
                    errors.add("Maximum discount amount must be positive");
                } else if (discountValueStr != null && !discountValueStr.trim().isEmpty()) {
                    try {
                        double discountValue = Double.parseDouble(discountValueStr);
                        if (maxDiscountAmount < discountValue) {
                            errors.add("Maximum discount amount cannot be less than discount value");
                        }
                    } catch (NumberFormatException e) {
                        // Already handled above
                    }
                }
            } catch (NumberFormatException e) {
                errors.add("Maximum discount amount must be a valid number");
            }
        }
        
        // Validate usage limit if provided (same as coupon)
        String usageLimitStr = request.getParameter("usageLimit");
        if (usageLimitStr != null && !usageLimitStr.trim().isEmpty()) {
            try {
                int usageLimit = Integer.parseInt(usageLimitStr);
                if (usageLimit <= 0) {
                    errors.add("Usage limit must be positive");
                }
            } catch (NumberFormatException e) {
                errors.add("Usage limit must be a valid integer");
            }
        }
        
        // Validate dates (same as coupon)
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        
        if (startDateStr == null || startDateStr.trim().isEmpty()) {
            errors.add("Start date is required");
        }
        if (endDateStr == null || endDateStr.trim().isEmpty()) {
            errors.add("End date is required");
        }
        
        if (startDateStr != null && !startDateStr.trim().isEmpty() && 
            endDateStr != null && !endDateStr.trim().isEmpty()) {
            try {
                LocalDate startDate = LocalDate.parse(startDateStr);
                LocalDate endDate = LocalDate.parse(endDateStr);
                LocalDate today = LocalDate.now();
                
                if (startDate.isBefore(today)) {
                    errors.add("Start date cannot be in the past");
                }
                if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
                    errors.add("End date must be after start date");
                }
            } catch (Exception e) {
                errors.add("Invalid date format");
            }
        }
        
        if (!errors.isEmpty()) {
            request.getSession().setAttribute("errorMessage", String.join("; ", errors));
            return false;
        }
        
        return true;
    }
    
    private boolean validatePromotionData(HttpServletRequest request) {
        // For now, use the same validation as flash sale
        return validateFlashSaleData(request);
    }
}
