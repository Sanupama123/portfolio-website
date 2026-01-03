package com.realestate.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

/**
 * Utility class for file operations (reading/writing objects to text files using JSON)
 */
public class FileUtil {
    
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * Write a list of objects to a text file using JSON
     * @param objects The list of objects to write
     * @param filePath The path of the file to write to
     * @param <T> The type of objects in the list
     * @return true if successful, false otherwise
     */
    public static <T> boolean writeObjectsToFile(List<T> objects, String filePath) {
        try {
            File file = new File(filePath);
            // Ensure directory exists
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // Convert objects to JSON string
            String jsonData = gson.toJson(objects);
            
            // Write JSON string to file
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(jsonData);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Read a list of objects from a text file using JSON with explicit class type
     * @param filePath The path of the file to read from
     * @param classOfT The class type of the objects in the list
     * @param <T> The type of objects in the list
     * @return The list of objects read from the file, or an empty list if the file doesn't exist or an error occurs
     */
    public static <T> List<T> readObjectsFromFile(String filePath, Class<T[]> classOfT) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            
            // Read the JSON string from the file
            String jsonData = new String(Files.readAllBytes(Paths.get(file.getPath())));
            
            // If the file is empty or doesn't contain valid JSON data
            if (jsonData == null || jsonData.trim().isEmpty()) {
                return new ArrayList<>();
            }
            
            // Convert JSON string to array of type T
            T[] array = gson.fromJson(jsonData, classOfT);
            
            // Convert array to list
            List<T> result = new ArrayList<>();
            for (T item : array) {
                result.add(item);
            }
            
            return result;
        } catch (IOException | com.google.gson.JsonSyntaxException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Legacy method for backward compatibility - delegates to the new method with class type
     * @param filePath The path of the file to read from
     * @param <T> The type parameter (inferred from context)
     * @return The list of objects read from the file
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> readObjectsFromFile(String filePath) {
        // This method exists for backward compatibility but should not be used for new code
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            
            // Read the file as string
            String jsonData = new String(Files.readAllBytes(Paths.get(file.getPath())));
            
            // For empty or invalid JSON
            if (jsonData == null || jsonData.trim().isEmpty()) {
                return new ArrayList<>();
            }
            
            // Trying to handle common cases based on filename
            if (filePath.contains("Admin")) {
                return (List<T>) readObjectsFromFile(filePath, com.realestate.model.Admin[].class);
            } else if (filePath.contains("Seller")) {
                return (List<T>) readObjectsFromFile(filePath, com.realestate.model.Seller[].class);
            } else if (filePath.contains("Buyer")) {
                return (List<T>) readObjectsFromFile(filePath, com.realestate.model.Buyer[].class);
            } else if (filePath.contains("Property")) {
                return (List<T>) readObjectsFromFile(filePath, com.realestate.model.Property[].class);
            } else {
                // Fall back to String for test files or unknown types
                return (List<T>) readObjectsFromFile(filePath, String[].class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Get the application's data directory path
     * @return The path to the data directory
     */
    public static String getDataDirectory() {
        // Use the actual webapp path as specified by the user
        String projectPath = System.getProperty("user.dir");
        String dataDir = projectPath + File.separator + "src" + File.separator + "main" + 
                         File.separator + "webapp" + File.separator + "data";
        
        // Ensure the directory exists
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        return dataDir;
    }
    
    /**
     * Get the full path for a data file
     * @param fileName The name of the file
     * @return The full path
     */
    public static String getFilePath(String fileName) {
        return getDataDirectory() + File.separator + fileName;
    }
} 