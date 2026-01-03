package com.bookstore.dao;

import com.bookstore.models.Book;
import com.bookstore.utils.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private static final String GET_ALL_BOOKS = "SELECT b.*, c.CategoryName, bm.CreatedAt, bm.UpdatedAt, bm.IsActive " +
            "FROM Books b " +
            "LEFT JOIN Categories c ON b.CategoryId = c.CategoryId " +
            "LEFT JOIN Book_Metadata bm ON b.BookId = bm.BookId " +
            "ORDER BY bm.CreatedAt DESC";
    
            
    private static final String GET_BOOK = "SELECT b.*, c.CategoryName, bm.CreatedAt, bm.UpdatedAt, bm.IsActive " +
            "FROM Books b " +
            "LEFT JOIN Categories c ON b.CategoryId = c.CategoryId " +
            "LEFT JOIN Book_Metadata bm ON b.BookId = bm.BookId " +
            "WHERE b.BookId = ?";
            
    private static final String UPDATE_STOCK = "UPDATE Books SET StockQuantity = ? WHERE BookId = ?";
    
    private static final String TOGGLE_ACTIVE = "UPDATE Book_Metadata SET IsActive = ? WHERE BookId = ?";

    public BookDAO() {
        // Ensure tables are created when DAO is initialized
        com.bookstore.utils.BookTableSetup.createBookTables();
    }

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_BOOKS)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        }
        return books;
    }

    public List<Book> searchAdmin(String query,
                                  String categoryId,
                                  Double minPrice,
                                  Double maxPrice,
                                  Double minRating,
                                  Boolean isActive) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT b.*, c.CategoryName, u.Username AS SellerName, bm.CreatedAt, bm.UpdatedAt, bm.IsActive, ");
        sql.append("(SELECT AVG(CAST(Rating AS FLOAT)) FROM Book_Reviews WHERE BookId = b.BookId) AS AverageRating ");
        sql.append("FROM Books b ");
        sql.append("LEFT JOIN Categories c ON b.CategoryId = c.CategoryId ");
        sql.append("LEFT JOIN [User] u ON b.SellerId = u.UserId ");
        sql.append("LEFT JOIN Book_Metadata bm ON b.BookId = bm.BookId ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (b.Title LIKE ? OR b.Author LIKE ? OR b.ISBN LIKE ?) ");
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (categoryId != null && !categoryId.isEmpty()) {
            sql.append("AND b.CategoryId = ? ");
            params.add(categoryId);
        }
        if (minPrice != null) {
            sql.append("AND b.Price >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append("AND b.Price <= ? ");
            params.add(maxPrice);
        }
        if (isActive != null) {
            sql.append("AND bm.IsActive = ? ");
            params.add(isActive);
        }
        if (minRating != null) {
            sql.append("AND (SELECT AVG(CAST(Rating AS FLOAT)) FROM Book_Reviews WHERE BookId = b.BookId) >= ? ");
            params.add(minRating);
        }

        sql.append("ORDER BY bm.CreatedAt DESC");

        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                if (p instanceof String) stmt.setString(idx++, (String)p);
                else if (p instanceof Integer) stmt.setInt(idx++, (Integer)p);
                else if (p instanceof Double) stmt.setDouble(idx++, (Double)p);
                else if (p instanceof Boolean) stmt.setBoolean(idx++, (Boolean)p);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapBook(rs));
                }
            }
        }
        return books;
    }

    public boolean deleteBook(String bookId, String sellerId) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // First, delete related records that reference this book
                // Delete from Shopping_Cart
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Shopping_Cart WHERE BookId = ?")) {
                    stmt.setString(1, bookId);
                    int cartResult = stmt.executeUpdate();
                    System.out.println("Deleted " + cartResult + " cart items for book: " + bookId);
                }
                
                // Delete from Wishlist
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Wishlist WHERE BookId = ?")) {
                    stmt.setString(1, bookId);
                    int wishlistResult = stmt.executeUpdate();
                    System.out.println("Deleted " + wishlistResult + " wishlist items for book: " + bookId);
                }
                
                // Delete from Book_Reviews
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Book_Reviews WHERE BookId = ?")) {
                    stmt.setString(1, bookId);
                    int reviewsResult = stmt.executeUpdate();
                    System.out.println("Deleted " + reviewsResult + " reviews for book: " + bookId);
                }
                
                // Delete from Order_Items
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Order_Items WHERE BookId = ?")) {
                    stmt.setString(1, bookId);
                    int orderItemsResult = stmt.executeUpdate();
                    System.out.println("Deleted " + orderItemsResult + " order items for book: " + bookId);
                }
                
                // Now delete the book itself
                String bookSql = (sellerId != null && !sellerId.isEmpty()) ? 
                    "DELETE FROM Books WHERE BookId = ? AND SellerId = ?" :
                    "DELETE FROM Books WHERE BookId = ?";
                
                try (PreparedStatement stmt = conn.prepareStatement(bookSql)) {
                    stmt.setString(1, bookId);
                    if (sellerId != null && !sellerId.isEmpty()) {
                        stmt.setString(2, sellerId);
                    }
                    
                    int bookResult = stmt.executeUpdate();
                    System.out.println("Delete book result: " + bookResult + " for bookId: " + bookId);
                    
                    if (bookResult > 0) {
                        conn.commit();
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
                
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error deleting book: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Error Code: " + e.getErrorCode());
                e.printStackTrace();
                throw e;
            }
        }
    }

    public boolean toggleActive(String bookId, boolean isActive) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(TOGGLE_ACTIVE)) {
            
            stmt.setBoolean(1, isActive);
            stmt.setString(2, bookId);
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateStock(String bookId, int quantity) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_STOCK)) {
            
            stmt.setInt(1, quantity);
            stmt.setString(2, bookId);
            
            return stmt.executeUpdate() > 0;
        }
    }

    public Book getBook(String bookId) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BOOK)) {
            
            stmt.setString(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
            }
        }
        return null;
    }

    public boolean adminUpdateBook(Book book) throws SQLException {
        String bookSql = "UPDATE Books SET Title=?, Author=?, ISBN=?, Description=?, " +
                    "Price=?, StockQuantity=?, CategoryId=?, CoverImagePath=?, PublishedYear=?, " +
                    "Publisher=?, Language=?, PageCount=? " +
                    "WHERE BookId=?";
        
        String metadataSql = "UPDATE Book_Metadata SET UpdatedAt=GETDATE(), IsActive=? WHERE BookId=?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement bookStmt = conn.prepareStatement(bookSql);
                 PreparedStatement metadataStmt = conn.prepareStatement(metadataSql)) {
                
                // Update book
                bookStmt.setString(1, book.getTitle());
                bookStmt.setString(2, book.getAuthor());
                bookStmt.setString(3, book.getIsbn());
                bookStmt.setString(4, book.getDescription());
                bookStmt.setDouble(5, book.getPrice());
                bookStmt.setInt(6, book.getStockQuantity());
                bookStmt.setString(7, book.getCategoryId());
                bookStmt.setString(8, book.getCoverImagePath() != null ? book.getCoverImagePath() : "");
                bookStmt.setInt(9, book.getPublishedYear());
                bookStmt.setString(10, book.getPublisher());
                bookStmt.setString(11, book.getLanguage());
                bookStmt.setInt(12, book.getPageCount());
                bookStmt.setString(13, book.getBookId());
                
                int bookResult = bookStmt.executeUpdate();
                
                // Update metadata
                metadataStmt.setBoolean(1, book.getMetadata() != null ? book.getMetadata().isActive() : true);
                metadataStmt.setString(2, book.getBookId());
                metadataStmt.executeUpdate();
                
                conn.commit();
                return bookResult > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean create(Book book) throws SQLException {
        // Ensure tables are created before inserting
        com.bookstore.utils.BookTableSetup.createBookTables();
        
        String bookId = com.bookstore.utils.IdGenerator.generateBookId();
        book.setBookId(bookId);
        
        System.out.println("Creating book with ID: " + bookId);
        
        String bookSql = "INSERT INTO Books (BookId, Title, Author, ISBN, Description, Price, StockQuantity, " +
                    "CategoryId, SellerId, CoverImagePath, PublishedYear, Publisher, Language, PageCount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        String metadataSql = "INSERT INTO Book_Metadata (MetadataId, BookId, CreatedAt, UpdatedAt, IsActive) " +
                    "VALUES (?, ?, GETDATE(), GETDATE(), ?)";
                    
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement bookStmt = conn.prepareStatement(bookSql);
                 PreparedStatement metadataStmt = conn.prepareStatement(metadataSql)) {
                
                // Insert book
                bookStmt.setString(1, book.getBookId());
                bookStmt.setString(2, book.getTitle());
                bookStmt.setString(3, book.getAuthor());
                bookStmt.setString(4, book.getIsbn());
                bookStmt.setString(5, book.getDescription());
                bookStmt.setDouble(6, book.getPrice());
                bookStmt.setInt(7, book.getStockQuantity());
                bookStmt.setString(8, book.getCategoryId());
                bookStmt.setString(9, book.getSellerId());
                bookStmt.setString(10, book.getCoverImagePath() != null ? book.getCoverImagePath() : "");
                bookStmt.setInt(11, book.getPublishedYear());
                bookStmt.setString(12, book.getPublisher());
                bookStmt.setString(13, book.getLanguage());
                bookStmt.setInt(14, book.getPageCount());
                
                int bookResult = bookStmt.executeUpdate();
                System.out.println("Book insert result: " + bookResult);
                
                // Insert metadata
                String metadataId = "MET" + String.format("%06d", System.currentTimeMillis() % 1000000);
                metadataStmt.setString(1, metadataId);
                metadataStmt.setString(2, book.getBookId());
                metadataStmt.setBoolean(3, book.getMetadata() != null ? book.getMetadata().isActive() : true);
                
                int metadataResult = metadataStmt.executeUpdate();
                System.out.println("Metadata insert result: " + metadataResult);
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error creating book: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Error Code: " + e.getErrorCode());
                e.printStackTrace();
                throw e;
            }
        }
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getString("BookId"));
        book.setTitle(rs.getString("Title"));
        book.setAuthor(rs.getString("Author"));
        book.setIsbn(rs.getString("ISBN"));
        book.setDescription(rs.getString("Description"));
        book.setPrice(rs.getDouble("Price"));
        book.setStockQuantity(rs.getInt("StockQuantity"));
        book.setCategoryId(rs.getString("CategoryId"));
        book.setCategoryName(rs.getString("CategoryName"));
        book.setSellerId(rs.getString("SellerId"));
        book.setCoverImagePath(rs.getString("CoverImagePath"));
        book.setPublishedYear(rs.getInt("PublishedYear"));
        book.setPublisher(rs.getString("Publisher"));
        book.setLanguage(rs.getString("Language"));
        book.setPageCount(rs.getInt("PageCount"));
        
        // Create and set metadata
        com.bookstore.models.BookMetadata metadata = new com.bookstore.models.BookMetadata();
        metadata.setBookId(rs.getString("BookId"));
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            metadata.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
        if (updatedAt != null) {
            metadata.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        // Handle case where metadata might be null
        try {
            metadata.setActive(rs.getBoolean("IsActive"));
        } catch (SQLException e) {
            // If IsActive is null, default to true
            metadata.setActive(true);
        }
        book.setMetadata(metadata);
        
        return book;
    }

    public List<Book> findBySellerId(String sellerId) throws SQLException {
        String sql = "SELECT b.*, c.CategoryName, bm.CreatedAt, bm.UpdatedAt, bm.IsActive " +
                    "FROM Books b " +
                    "LEFT JOIN Categories c ON b.CategoryId = c.CategoryId " +
                    "LEFT JOIN Book_Metadata bm ON b.BookId = bm.BookId " +
                    "WHERE b.SellerId = ? " +
                    "ORDER BY COALESCE(bm.CreatedAt, GETDATE()) DESC";
        
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sellerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        }
        
        return books;
    }

    public boolean updateBook(Book book) throws SQLException {
        String bookSql = "UPDATE Books SET Title=?, Author=?, ISBN=?, Description=?, " +
                    "Price=?, StockQuantity=?, CategoryId=?, CoverImagePath=?, PublishedYear=?, " +
                    "Publisher=?, Language=?, PageCount=? " +
                    "WHERE BookId=? AND SellerId=?";
        
        String metadataSql = "UPDATE Book_Metadata SET UpdatedAt=GETDATE() WHERE BookId=?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement bookStmt = conn.prepareStatement(bookSql);
                 PreparedStatement metadataStmt = conn.prepareStatement(metadataSql)) {
                
                // Update book
                bookStmt.setString(1, book.getTitle());
                bookStmt.setString(2, book.getAuthor());
                bookStmt.setString(3, book.getIsbn());
                bookStmt.setString(4, book.getDescription());
                bookStmt.setDouble(5, book.getPrice());
                bookStmt.setInt(6, book.getStockQuantity());
                bookStmt.setString(7, book.getCategoryId());
                bookStmt.setString(8, book.getCoverImagePath() != null ? book.getCoverImagePath() : "");
                bookStmt.setInt(9, book.getPublishedYear());
                bookStmt.setString(10, book.getPublisher());
                bookStmt.setString(11, book.getLanguage());
                bookStmt.setInt(12, book.getPageCount());
                bookStmt.setString(13, book.getBookId());
                bookStmt.setString(14, book.getSellerId());
                
                int bookResult = bookStmt.executeUpdate();
                System.out.println("Book update result: " + bookResult);
                
                // Update metadata
                metadataStmt.setString(1, book.getBookId());
                int metadataResult = metadataStmt.executeUpdate();
                System.out.println("Metadata update result: " + metadataResult);
                
                conn.commit();
                return bookResult > 0;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error updating book: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Error Code: " + e.getErrorCode());
                e.printStackTrace();
                throw e;
            }
        }
    }

    public List<Book> findAll() throws SQLException {
        String sql = "SELECT b.*, c.CategoryName, u.Username as SellerName, bm.CreatedAt, bm.UpdatedAt, bm.IsActive, " +
                    "(SELECT AVG(CAST(Rating AS FLOAT)) FROM Book_Reviews WHERE BookId = b.BookId) as AverageRating " +
                    "FROM Books b " +
                    "LEFT JOIN Categories c ON b.CategoryId = c.CategoryId " +
                    "LEFT JOIN [User] u ON b.SellerId = u.UserId " +
                    "LEFT JOIN Book_Metadata bm ON b.BookId = bm.BookId " +
                    "WHERE bm.IsActive = 1 " +
                    "ORDER BY bm.CreatedAt DESC";
        
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                books.add(mapBook(rs));
            }
        }
        
        return books;
    }

    public List<Book> findByCategoryId(String categoryId) throws SQLException {
        String sql = "SELECT b.*, c.CategoryName, u.Username as SellerName, bm.CreatedAt, bm.UpdatedAt, bm.IsActive, " +
                    "(SELECT AVG(CAST(Rating AS FLOAT)) FROM Book_Reviews WHERE BookId = b.BookId) as AverageRating " +
                    "FROM Books b " +
                    "LEFT JOIN Categories c ON b.CategoryId = c.CategoryId " +
                    "LEFT JOIN [User] u ON b.SellerId = u.UserId " +
                    "LEFT JOIN Book_Metadata bm ON b.BookId = bm.BookId " +
                    "WHERE b.CategoryId = ? AND bm.IsActive = 1 " +
                    "ORDER BY bm.CreatedAt DESC";
        
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoryId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapBook(rs));
                }
            }
        }
        
        return books;
    }

    public List<Book> search(String query) throws SQLException {
        String sql = "SELECT b.*, c.CategoryName, u.Username as SellerName, " +
                    "(SELECT AVG(CAST(Rating AS FLOAT)) FROM Book_Reviews WHERE BookId = b.BookId) as AverageRating " +
                    "FROM Books b " +
                    "LEFT JOIN Categories c ON b.CategoryId = c.CategoryId " +
                    "LEFT JOIN [User] u ON b.SellerId = u.UserId " +
                    "WHERE b.IsActive = 1 AND " +
                    "(b.Title LIKE ? OR b.Author LIKE ? OR b.ISBN LIKE ? OR b.Publisher LIKE ?) " +
                    "ORDER BY b.CreatedAt DESC";
        
        List<Book> books = new ArrayList<>();
        String searchPattern = "%" + query + "%";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapBook(rs));
                }
            }
        }
        
        return books;
    }

    public void update(Book book) throws SQLException {
        String bookSql = "UPDATE Books SET Title=?, Author=?, ISBN=?, Description=?, Price=?, " +
                    "StockQuantity=?, CategoryId=?, CoverImagePath=?, PublishedYear=?, " +
                    "Publisher=?, Language=?, PageCount=? " +
                    "WHERE BookId=? AND SellerId=?";
        
        String metadataSql = "UPDATE Book_Metadata SET UpdatedAt=GETDATE() WHERE BookId=?";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement bookStmt = conn.prepareStatement(bookSql);
                 PreparedStatement metadataStmt = conn.prepareStatement(metadataSql)) {
                
                // Update book
                bookStmt.setString(1, book.getTitle());
                bookStmt.setString(2, book.getAuthor());
                bookStmt.setString(3, book.getIsbn());
                bookStmt.setString(4, book.getDescription());
                bookStmt.setDouble(5, book.getPrice());
                bookStmt.setInt(6, book.getStockQuantity());
                bookStmt.setString(7, book.getCategoryId());
                bookStmt.setString(8, book.getCoverImagePath());
                bookStmt.setInt(9, book.getPublishedYear());
                bookStmt.setString(10, book.getPublisher());
                bookStmt.setString(11, book.getLanguage());
                bookStmt.setInt(12, book.getPageCount());
                bookStmt.setString(13, book.getBookId());
                bookStmt.setString(14, book.getSellerId());
                
                bookStmt.executeUpdate();
                
                // Update metadata
                metadataStmt.setString(1, book.getBookId());
                metadataStmt.executeUpdate();
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void delete(String bookId, String sellerId) throws SQLException {
        String sql = "UPDATE Book_Metadata SET IsActive=0 WHERE BookId=? AND BookId IN " +
                    "(SELECT BookId FROM Books WHERE BookId=? AND SellerId=?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bookId);
            stmt.setString(2, bookId);
            stmt.setString(3, sellerId);
            stmt.executeUpdate();
        }
    }

    public Book findById(String bookId) throws SQLException {
        String sql = "SELECT b.*, c.CategoryName, u.Username as SellerName, bm.CreatedAt, bm.UpdatedAt, bm.IsActive, " +
                    "(SELECT AVG(CAST(Rating AS FLOAT)) FROM Book_Reviews WHERE BookId = b.BookId) as AverageRating " +
                    "FROM Books b " +
                    "LEFT JOIN Categories c ON b.CategoryId = c.CategoryId " +
                    "LEFT JOIN [User] u ON b.SellerId = u.UserId " +
                    "LEFT JOIN Book_Metadata bm ON b.BookId = bm.BookId " +
                    "WHERE b.BookId = ? AND bm.IsActive = 1";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapBook(rs);
                }
            }
        }
        
        return null;
    }

    public List<Book> searchBuyer(String query,
                                 String categoryId,
                                 Double minPrice,
                                 Double maxPrice,
                                 Double minRating,
                                 String sortBy,
                                 String sortOrder) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT b.*, c.CategoryName, u.Username as SellerName, ");
        sql.append("(SELECT AVG(CAST(Rating AS FLOAT)) FROM Book_Reviews WHERE BookId = b.BookId) as AverageRating, ");
        sql.append("(SELECT COUNT(*) FROM Book_Reviews WHERE BookId = b.BookId) as ReviewCount ");
        sql.append("FROM Books b ");
        sql.append("LEFT JOIN Categories c ON b.CategoryId = c.CategoryId ");
        sql.append("LEFT JOIN [User] u ON b.SellerId = u.UserId ");
        sql.append("WHERE b.IsActive = 1 ");

        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (b.Title LIKE ? OR b.Author LIKE ? OR b.ISBN LIKE ? OR b.Publisher LIKE ?) ");
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (categoryId != null && !categoryId.isEmpty()) {
            sql.append("AND b.CategoryId = ? ");
            params.add(categoryId);
        }
        if (minPrice != null) {
            sql.append("AND b.Price >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append("AND b.Price <= ? ");
            params.add(maxPrice);
        }
        if (minRating != null) {
            sql.append("AND (SELECT AVG(CAST(Rating AS FLOAT)) FROM Book_Reviews WHERE BookId = b.BookId) >= ? ");
            params.add(minRating);
        }

        // Add sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy.toLowerCase()) {
                case "title":
                    sql.append("ORDER BY b.Title ");
                    break;
                case "author":
                    sql.append("ORDER BY b.Author ");
                    break;
                case "price":
                    sql.append("ORDER BY b.Price ");
                    break;
                case "rating":
                    sql.append("ORDER BY AverageRating ");
                    break;
                case "date":
                default:
                    sql.append("ORDER BY b.CreatedAt ");
                    break;
            }
            if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
                sql.append("DESC ");
            } else {
                sql.append("ASC ");
            }
        } else {
            sql.append("ORDER BY b.CreatedAt DESC ");
        }

        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                if (p instanceof String) stmt.setString(idx++, (String)p);
                else if (p instanceof Integer) stmt.setInt(idx++, (Integer)p);
                else if (p instanceof Double) stmt.setDouble(idx++, (Double)p);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapBook(rs));
                }
            }
        }
        return books;
    }

    private Book mapBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getString("BookId"));
        book.setTitle(rs.getString("Title"));
        book.setAuthor(rs.getString("Author"));
        book.setIsbn(rs.getString("ISBN"));
        book.setDescription(rs.getString("Description"));
        book.setPrice(rs.getDouble("Price"));
        book.setStockQuantity(rs.getInt("StockQuantity"));
        book.setCategoryId(rs.getString("CategoryId"));
        book.setSellerId(rs.getString("SellerId"));
        book.setCoverImagePath(rs.getString("CoverImagePath"));
        book.setPublishedYear(rs.getInt("PublishedYear"));
        book.setPublisher(rs.getString("Publisher"));
        book.setLanguage(rs.getString("Language"));
        book.setPageCount(rs.getInt("PageCount"));
        
        // Create and set metadata
        com.bookstore.models.BookMetadata metadata = new com.bookstore.models.BookMetadata();
        metadata.setBookId(rs.getString("BookId"));
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            metadata.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
        if (updatedAt != null) {
            metadata.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        // Handle case where metadata might be null
        try {
            metadata.setActive(rs.getBoolean("IsActive"));
        } catch (SQLException e) {
            // If IsActive is null, default to true
            metadata.setActive(true);
        }
        book.setMetadata(metadata);
        
        // Join fields
        book.setCategoryName(rs.getString("CategoryName"));
        try {
            book.setSellerName(rs.getString("SellerName"));
        } catch (SQLException e) {
            // SellerName might not be included in all queries
        }
        
        try {
            double avgRating = rs.getDouble("AverageRating");
            book.setAverageRating(avgRating > 0 ? avgRating : 0.0);
        } catch (SQLException e) {
            // AverageRating might not be included in all queries
            book.setAverageRating(0.0);
        }
        
        return book;
    }
}