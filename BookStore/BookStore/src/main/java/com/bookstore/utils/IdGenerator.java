package com.bookstore.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public final class IdGenerator {

	private static final AtomicInteger userCounter = new AtomicInteger(0);
	private static final AtomicInteger orderCounter = new AtomicInteger(0);
	private static final AtomicInteger bookCounter = new AtomicInteger(0);
	private static final AtomicInteger categoryCounter = new AtomicInteger(0);
	private static final AtomicInteger reviewCounter = new AtomicInteger(0);
	private static final AtomicInteger cartCounter = new AtomicInteger(0);
	private static final AtomicInteger wishlistCounter = new AtomicInteger(0);
	private static final AtomicInteger orderItemCounter = new AtomicInteger(0);
	private static final AtomicInteger ratingCounter = new AtomicInteger(0);
	private static final AtomicInteger couponCounter = new AtomicInteger(0);
	private static final AtomicInteger promotionCounter = new AtomicInteger(0);
	private static final AtomicInteger buyerDetailsCounter = new AtomicInteger(0);
	private static final AtomicInteger savedCardCounter = new AtomicInteger(0);
	private static final AtomicInteger paymentCounter = new AtomicInteger(0);
	private static final AtomicInteger couponUsageCounter = new AtomicInteger(0);

	private static boolean initialized = false;

	private IdGenerator() {}

	private static synchronized void initializeCounters() {
		if (initialized) return;
		
		try {
			userCounter.set(getMaxIdFromTable("[User]", "UserId", "USR"));
			// For orders, get the maximum ID and add a buffer to avoid conflicts
			int maxOrderId = getMaxIdFromTable("Orders", "OrderId", "ORD");
			orderCounter.set(maxOrderId + 10); // Add buffer to avoid conflicts
			bookCounter.set(getMaxIdFromTable("Books", "BookId", "BOK"));
			categoryCounter.set(getMaxIdFromTable("Categories", "CategoryId", "CAT"));
			reviewCounter.set(getMaxIdFromTable("Book_Reviews", "ReviewId", "REV"));
			cartCounter.set(getMaxIdFromTable("Shopping_Cart", "CartItemId", "CIT"));
			wishlistCounter.set(getMaxIdFromTable("Wishlist", "WishlistItemId", "WIT"));
			// For order items, get the maximum ID and add a buffer to avoid conflicts
			int maxOrderItemId = getMaxIdFromTable("Order_Items", "OrderItemId", "OIT");
			orderItemCounter.set(maxOrderItemId + 10); // Add buffer to avoid conflicts
			ratingCounter.set(getMaxIdFromTable("Book_Ratings", "RatingId", "RAT"));
			couponCounter.set(getMaxIdFromTable("DiscountCoupons", "CouponId", "COU"));
			promotionCounter.set(getMaxIdFromTable("Promotions", "PromotionId", "PRO"));
			buyerDetailsCounter.set(getMaxIdFromTable("BuyerDetails", "DetailsId", "DTL"));
			savedCardCounter.set(getMaxIdFromTable("SavedCards", "CardId", "CRD"));
			// For payments, get the maximum ID and add a buffer to avoid conflicts
			int maxPaymentId = getMaxIdFromTable("Payments", "PaymentId", "PAY");
			paymentCounter.set(maxPaymentId + 10); // Add buffer to avoid conflicts
			couponUsageCounter.set(getMaxIdFromTable("CouponUsage", "UsageId", "USG"));
		} catch (SQLException e) {
			// If there's an error, start from 1
			userCounter.set(1);
			orderCounter.set(1);
			bookCounter.set(1);
			categoryCounter.set(1);
			reviewCounter.set(1);
			cartCounter.set(1);
			wishlistCounter.set(1);
			orderItemCounter.set(1);
			ratingCounter.set(1);
			couponCounter.set(1);
			promotionCounter.set(1);
			buyerDetailsCounter.set(1);
			savedCardCounter.set(1);
			paymentCounter.set(1);
			couponUsageCounter.set(1);
		}
		initialized = true;
	}

	private static int getMaxIdFromTable(String tableName, String idColumn, String prefix) throws SQLException {
		String sql = "SELECT MAX(CAST(SUBSTRING(" + idColumn + ", 4, LEN(" + idColumn + ")) AS INT)) FROM " + tableName + 
					" WHERE " + idColumn + " LIKE ?";
		
		try (Connection conn = DatabaseConfig.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setString(1, prefix + "%");
			
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next() && rs.getObject(1) != null) {
					return rs.getInt(1);
				}
			}
		}
		return 0;
	}

	public static String generateUserId() {
		initializeCounters();
		return "USR" + String.format("%03d", userCounter.incrementAndGet());
	}

	public static String generateOrderId() {
		initializeCounters();
		return generateUniqueOrderId();
	}
	
	private static String generateUniqueOrderId() {
		String orderId;
		int attempts = 0;
		do {
			orderId = "ORD" + String.format("%03d", orderCounter.incrementAndGet());
			attempts++;
			if (attempts > 10) {
				// Fallback to timestamp-based ID if too many attempts
				orderId = "ORD" + System.currentTimeMillis();
				break;
			}
		} while (isOrderIdExists(orderId));
		return orderId;
	}
	
	private static boolean isOrderIdExists(String orderId) {
		String sql = "SELECT COUNT(*) FROM Orders WHERE OrderId = ?";
		try (Connection conn = DatabaseConfig.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, orderId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			// If we can't check, assume it doesn't exist and let the database handle it
			return false;
		}
		return false;
	}

	public static String generateBookId() {
		initializeCounters();
		return "BOK" + String.format("%03d", bookCounter.incrementAndGet());
	}

	public static String generateCategoryId() {
		initializeCounters();
		return "CAT" + String.format("%03d", categoryCounter.incrementAndGet());
	}

	public static String generateReviewId() {
		initializeCounters();
		return "REV" + String.format("%03d", reviewCounter.incrementAndGet());
	}

	public static String generateCartItemId() {
		initializeCounters();
		return "CIT" + String.format("%03d", cartCounter.incrementAndGet());
	}

	public static String generateWishlistItemId() {
		initializeCounters();
		return "WIT" + String.format("%03d", wishlistCounter.incrementAndGet());
	}

	public static String generateOrderItemId() {
		initializeCounters();
		return generateUniqueOrderItemId();
	}
	
	private static String generateUniqueOrderItemId() {
		String orderItemId;
		int attempts = 0;
		do {
			orderItemId = "OIT" + String.format("%03d", orderItemCounter.incrementAndGet());
			attempts++;
			if (attempts > 10) {
				// Fallback to timestamp-based ID if too many attempts
				orderItemId = "OIT" + System.currentTimeMillis();
				break;
			}
		} while (isOrderItemIdExists(orderItemId));
		return orderItemId;
	}
	
	private static boolean isOrderItemIdExists(String orderItemId) {
		String sql = "SELECT COUNT(*) FROM Order_Items WHERE OrderItemId = ?";
		try (Connection conn = DatabaseConfig.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, orderItemId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			// If we can't check, assume it doesn't exist and let the database handle it
			return false;
		}
		return false;
	}

	public static String generateRatingId() {
		initializeCounters();
		return "RAT" + String.format("%03d", ratingCounter.incrementAndGet());
	}

	public static String generateCouponId() {
		initializeCounters();
		return "COU" + String.format("%03d", couponCounter.incrementAndGet());
	}

	public static String generatePromotionId() {
		initializeCounters();
		return "PRO" + String.format("%03d", promotionCounter.incrementAndGet());
	}

	public static String generateBuyerDetailsId() {
		initializeCounters();
		return "DTL" + String.format("%03d", buyerDetailsCounter.incrementAndGet());
	}

	public static String generateSavedCardId() {
		initializeCounters();
		return "CRD" + String.format("%03d", savedCardCounter.incrementAndGet());
	}

	public static String generatePaymentId() {
		initializeCounters();
		return generateUniquePaymentId();
	}
	
	private static String generateUniquePaymentId() {
		String paymentId;
		int attempts = 0;
		do {
			paymentId = "PAY" + String.format("%03d", paymentCounter.incrementAndGet());
			attempts++;
			if (attempts > 10) {
				// Fallback to timestamp-based ID if too many attempts
				paymentId = "PAY" + System.currentTimeMillis();
				break;
			}
		} while (isPaymentIdExists(paymentId));
		return paymentId;
	}
	
	private static boolean isPaymentIdExists(String paymentId) {
		String sql = "SELECT COUNT(*) FROM Payments WHERE PaymentId = ?";
		try (Connection conn = DatabaseConfig.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, paymentId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			// If we can't check, assume it doesn't exist and let the database handle it
			return false;
		}
		return false;
	}

	public static String generateCouponUsageId() {
		initializeCounters();
		return "USG" + String.format("%03d", couponUsageCounter.incrementAndGet());
	}
}


