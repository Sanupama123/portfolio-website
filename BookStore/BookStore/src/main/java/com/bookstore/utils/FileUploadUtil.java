package com.bookstore.utils;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUploadUtil {
    private static final String BASE_UPLOAD_PATH = "C:" + File.separator + "Users" + File.separator + "KENUL" 
            + File.separator + "IdeaProjects" + File.separator + "BookStore" + File.separator + "bookstore-uploads";
    private static final String UPLOAD_DIRECTORY = "book-covers";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};

    static {
        // Create base upload directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(BASE_UPLOAD_PATH, UPLOAD_DIRECTORY));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public static String saveFile(Part filePart, String contextPath) throws IOException {
        // Validate file size
        if (filePart.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File is too large. Maximum size is 5MB");
        }

        // Get file extension
        String fileName = filePart.getSubmittedFileName();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        // Validate file extension
        boolean isValidExtension = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (ext.equals(fileExtension)) {
                isValidExtension = true;
                break;
            }
        }
        if (!isValidExtension) {
            throw new IOException("Invalid file type. Allowed types: jpg, jpeg, png, gif");
        }

        // Create unique filename
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // Get upload path
        Path uploadPath = Paths.get(BASE_UPLOAD_PATH, UPLOAD_DIRECTORY);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save the file
        File file = new File(uploadPath.toString(), uniqueFileName);
        String absolutePath = file.getAbsolutePath();
        System.out.println("Saving file to: " + absolutePath); // Debug log
        filePart.write(absolutePath);

        // Return the relative path that will be stored in the database
        return uniqueFileName;
    }

    public static void deleteFile(String fileName, String contextPath) {
        if (fileName != null && !fileName.isEmpty()) {
            try {
                Path fullPath = Paths.get(BASE_UPLOAD_PATH, UPLOAD_DIRECTORY, fileName);
                Files.deleteIfExists(fullPath);
            } catch (IOException e) {
                System.err.println("Error deleting file: " + e.getMessage());
            }
        }
    }
    
    public static Path getFullPath(String fileName) {
        return Paths.get(BASE_UPLOAD_PATH, UPLOAD_DIRECTORY, fileName);
    }
}