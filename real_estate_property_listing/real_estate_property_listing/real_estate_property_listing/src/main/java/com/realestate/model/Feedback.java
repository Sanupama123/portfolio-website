package com.realestate.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Feedback {
    private String username;
    private String email;
    private String message;
    private LocalDateTime timestamp;

    public Feedback(String username, String email, String message) {
        this.username = username;
        this.email = email;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public Feedback(String username, String email, String message, LocalDateTime timestamp) {
        this.username = username;
        this.email = email;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Add a method to convert LocalDateTime to Date
    public Date getTimestampAsDate() {
        return Date.from(timestamp.atZone(ZoneId.systemDefault()).toInstant());
    }

    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter) + "," + username + "," + email + "," + message.replace("\n", " ") + "\n";
    }

    public static Feedback fromFileString(String line) {
        String[] parts = line.split(",", 4);
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid feedback format: " + line);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timestamp = LocalDateTime.parse(parts[0], formatter);
        String username = parts[1];
        String email = parts[2];
        String message = parts[3];
        return new Feedback(username, email, message, timestamp);
    }
}

