package com.realestate.service;

import com.realestate.model.Property;
import com.realestate.util.FileUtil;
import com.realestate.util.PropertyBST;
import com.realestate.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Service class for property management operations
 */
public class PropertyService {
    private PropertyBST propertyBST;
    
    public PropertyService() {
        loadProperties();
    }
    
    /**
     * Load properties from file into BST
     */
    private void loadProperties() {
        List<Property> properties = FileUtil.readObjectsFromFile(FileUtil.getFilePath(Constants.PROPERTY_FILE), Property[].class);
        propertyBST = new PropertyBST();
        
        for (Property property : properties) {
            propertyBST.insert(property);
        }
    }
    
    /**
     * Save properties from BST to file
     */
    private boolean saveProperties() {
        List<Property> properties = propertyBST.getAllProperties();
        return FileUtil.writeObjectsToFile(properties, FileUtil.getFilePath(Constants.PROPERTY_FILE));
    }
    
    /**
     * Add a new property
     * @param property The property to add
     * @return The added property with generated ID
     */
    public Property addProperty(Property property) {
        // Generate a unique ID
        property.setId(UUID.randomUUID().toString());
        
        propertyBST.insert(property);
        saveProperties();
        return property;
    }
    
    /**
     * Update an existing property
     * @param property The property with updated information
     * @return true if successful, false if property not found
     */
    public boolean updateProperty(Property property) {
        if (propertyBST.findById(property.getId()) == null) {
            return false;
        }
        
        // Delete and re-insert to maintain BST property based on price
        propertyBST.delete(property.getId());
        propertyBST.insert(property);
        return saveProperties();
    }
    
    /**
     * Delete a property by ID
     * @param propertyId The ID of the property to delete
     * @return The deleted property, or null if not found
     */
    public Property deleteProperty(String propertyId) {
        Property deletedProperty = propertyBST.delete(propertyId);
        if (deletedProperty != null) {
            saveProperties();
        }
        return deletedProperty;
    }
    
    /**
     * Get a property by ID
     * @param propertyId The ID of the property
     * @return The property, or null if not found
     */
    public Property getPropertyById(String propertyId) {
        return propertyBST.findById(propertyId);
    }
    
    /**
     * Get all properties
     * @return List of all properties (sorted by price)
     */
    public List<Property> getAllProperties() {
        return propertyBST.getAllProperties();
    }
    
    /**
     * Get properties by type
     * @param propertyType The type of property
     * @return List of properties of the specified type
     */
    public List<Property> getPropertiesByType(String propertyType) {
        return propertyBST.getPropertiesByType(propertyType);
    }
    
    /**
     * Get properties in a price range
     * @param minPrice The minimum price
     * @param maxPrice The maximum price
     * @return List of properties in the price range
     */
    public List<Property> getPropertiesInPriceRange(double minPrice, double maxPrice) {
        return propertyBST.getPropertiesInPriceRange(minPrice, maxPrice);
    }
    
    /**
     * Get properties by seller ID
     * @param sellerId The ID of the seller
     * @return List of properties owned by the seller
     */
    public List<Property> getPropertiesBySeller(String sellerId) {
        return propertyBST.getPropertiesBySeller(sellerId);
    }
    
    /**
     * Sort properties by price using Quick Sort
     * @param properties List of properties to sort
     * @param ascending true for ascending order, false for descending
     * @return Sorted list of properties
     */
    public List<Property> sortPropertiesByPrice(List<Property> properties, boolean ascending) {
        List<Property> sortedList = new ArrayList<>(properties);
        Collections.sort(sortedList);
        
        if (!ascending) {
            Collections.reverse(sortedList);
        }
        
        return sortedList;
    }
} 