package com.realestate.service;

import com.realestate.model.Feedback;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FeedbackService {
    private static final String FILE_PATH = "C:\\Users\\CYBORG\\Desktop\\New FInalized\\New_Finalized\\src\\main\\webapp\\data\\feedback.txt";

    public void saveFeedback(Feedback feedback) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write(feedback.toFileString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save feedback", e);
        }
    }

    public List<Feedback> getAllFeedback() {
        List<Feedback> feedbackList = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return feedbackList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        feedbackList.add(Feedback.fromFileString(line));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Skipping invalid line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read feedback", e);
        }
        return feedbackList;
    }

    public Feedback getFeedbackByTimestampAndEmail(String timestamp, String email) {
        List<Feedback> feedbackList = getAllFeedback();
        for (Feedback feedback : feedbackList) {
            String formattedTimestamp = feedback.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (formattedTimestamp.equals(timestamp) && feedback.getEmail().equals(email)) {
                return feedback;
            }
        }
        return null;
    }

    public void updateFeedback(String timestamp, String email, Feedback updatedFeedback) {
        List<Feedback> feedbackList = getAllFeedback();
        boolean found = false;

        for (int i = 0; i < feedbackList.size(); i++) {
            Feedback feedback = feedbackList.get(i);
            String formattedTimestamp = feedback.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (formattedTimestamp.equals(timestamp) && feedback.getEmail().equals(email)) {
                feedbackList.set(i, updatedFeedback);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new RuntimeException("Feedback not found");
        }

        rewriteFile(feedbackList);
    }

    public void deleteFeedback(String timestamp, String email) {
        List<Feedback> feedbackList = getAllFeedback();
        boolean found = false;

        for (int i = 0; i < feedbackList.size(); i++) {
            Feedback feedback = feedbackList.get(i);
            String formattedTimestamp = feedback.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (formattedTimestamp.equals(timestamp) && feedback.getEmail().equals(email)) {
                feedbackList.remove(i);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new RuntimeException("Feedback not found");
        }

        rewriteFile(feedbackList);
    }

    private void rewriteFile(List<Feedback> feedbackList) {
        try (FileWriter writer = new FileWriter(FILE_PATH, false)) {
            for (Feedback feedback : feedbackList) {
                writer.write(feedback.toFileString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to rewrite feedback file", e);
        }
    }
}
