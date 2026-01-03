package com.bookstore.dao;

import com.bookstore.models.SavedCard;
import com.bookstore.utils.DatabaseConfig;
import com.bookstore.utils.IdGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SavedCardDAO {

    public String saveCard(SavedCard card) throws SQLException {
        String cardId = IdGenerator.generateSavedCardId();
        String sql = "INSERT INTO SavedCards (CardId, UserId, CardHolderName, CardNumber, ExpiryDate, CardType) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cardId);
            stmt.setString(2, card.getUserId());
            stmt.setString(3, card.getCardHolderName());
            stmt.setString(4, card.getCardNumber()); // Only last 4 digits
            stmt.setString(5, card.getExpiryDate());
            stmt.setString(6, card.getCardType());
            
            stmt.executeUpdate();
            return cardId;
        }
    }

    public List<SavedCard> getSavedCardsByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM SavedCards WHERE UserId = ? ORDER BY CreatedAt DESC";
        List<SavedCard> cards = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cards.add(mapResultSetToSavedCard(rs));
                }
            }
        }
        return cards;
    }

    public SavedCard getSavedCardById(String cardId) throws SQLException {
        String sql = "SELECT * FROM SavedCards WHERE CardId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cardId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSavedCard(rs);
                }
            }
        }
        return null;
    }

    public void deleteCard(String cardId, String userId) throws SQLException {
        String sql = "DELETE FROM SavedCards WHERE CardId = ? AND UserId = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cardId);
            stmt.setString(2, userId);
            
            stmt.executeUpdate();
        }
    }

    private SavedCard mapResultSetToSavedCard(ResultSet rs) throws SQLException {
        SavedCard card = new SavedCard();
        card.setCardId(rs.getString("CardId"));
        card.setUserId(rs.getString("UserId"));
        card.setCardHolderName(rs.getString("CardHolderName"));
        card.setCardNumber(rs.getString("CardNumber"));
        card.setExpiryDate(rs.getString("ExpiryDate"));
        card.setCardType(rs.getString("CardType"));
        
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) card.setCreatedAt(createdAt.toLocalDateTime());
        
        return card;
    }
}

