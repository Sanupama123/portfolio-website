package com.realestate.util;

import com.realestate.model.Property;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Binary Search Tree implementation for property management
 * Properties are sorted by price in the tree
 */
public class PropertyBST implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Node root;
    
    // Node class for the BST
    private static class Node implements Serializable {
        private static final long serialVersionUID = 1L;
        
        Property property;
        Node left;
        Node right;
        
        Node(Property property) {
            this.property = property;
            this.left = null;
            this.right = null;
        }
    }
    
    /**
     * Insert a property into the BST
     * @param property The property to insert
     */
    public void insert(Property property) {
        root = insertRec(root, property);
    }
    
    /**
     * Recursive helper method for insert
     */
    private Node insertRec(Node root, Property property) {
        // If tree is empty or we've reached a leaf
        if (root == null) {
            return new Node(property);
        }
        
        // Otherwise, recur down the tree
        if (property.getPrice() < root.property.getPrice()) {
            root.left = insertRec(root.left, property);
        } else {
            root.right = insertRec(root.right, property);
        }
        
        return root;
    }
    
    /**
     * Delete a property from the BST by ID
     * @param propertyId The ID of the property to delete
     * @return The deleted property, or null if not found
     */
    public Property delete(String propertyId) {
        FindResult result = new FindResult();
        root = deleteRec(root, propertyId, result);
        return result.property;
    }
    
    /**
     * Helper class to return the deleted property
     */
    private static class FindResult {
        Property property;
    }
    
    /**
     * Recursive helper method for delete
     */
    private Node deleteRec(Node root, String propertyId, FindResult result) {
        if (root == null) {
            return null;
        }
        
        if (root.property.getId().equals(propertyId)) {
            result.property = root.property;
            
            // Node with only one child or no child
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }
            
            // Node with two children: Get the inorder successor (smallest in right subtree)
            root.property = minValue(root.right);
            
            // Delete the inorder successor
            root.right = deleteRec(root.right, root.property.getId(), new FindResult());
        } else {
            // Search both left and right subtrees since property ID doesn't directly relate to BST ordering
            root.left = deleteRec(root.left, propertyId, result);
            if (result.property == null) {  // If not found in left subtree
                root.right = deleteRec(root.right, propertyId, result);
            }
        }
        
        return root;
    }
    
    /**
     * Find the property with minimum value (price) in a subtree
     */
    private Property minValue(Node root) {
        Property minv = root.property;
        while (root.left != null) {
            minv = root.left.property;
            root = root.left;
        }
        return minv;
    }
    
    /**
     * Find a property by ID
     * @param propertyId The ID to search for
     * @return The found property, or null if not found
     */
    public Property findById(String propertyId) {
        return findByIdRec(root, propertyId);
    }
    
    /**
     * Recursive helper method for findById
     */
    private Property findByIdRec(Node root, String propertyId) {
        if (root == null) {
            return null;
        }
        
        if (root.property.getId().equals(propertyId)) {
            return root.property;
        }
        
        Property found = findByIdRec(root.left, propertyId);
        if (found != null) {
            return found;
        }
        
        return findByIdRec(root.right, propertyId);
    }
    
    /**
     * Get all properties in the BST
     * @return List of all properties
     */
    public List<Property> getAllProperties() {
        List<Property> properties = new ArrayList<>();
        inOrderTraversal(root, properties);
        return properties;
    }
    
    /**
     * In-order traversal of the BST (gives properties sorted by price)
     */
    private void inOrderTraversal(Node node, List<Property> properties) {
        if (node != null) {
            inOrderTraversal(node.left, properties);
            properties.add(node.property);
            inOrderTraversal(node.right, properties);
        }
    }
    
    /**
     * Get properties by type
     * @param propertyType The type of property to filter by
     * @return List of properties of the specified type
     */
    public List<Property> getPropertiesByType(String propertyType) {
        List<Property> result = new ArrayList<>();
        getPropertiesByTypeRec(root, propertyType, result);
        return result;
    }
    
    private void getPropertiesByTypeRec(Node node, String propertyType, List<Property> result) {
        if (node != null) {
            getPropertiesByTypeRec(node.left, propertyType, result);
            
            if (node.property.getPropertyType().equalsIgnoreCase(propertyType)) {
                result.add(node.property);
            }
            
            getPropertiesByTypeRec(node.right, propertyType, result);
        }
    }
    
    /**
     * Get properties within a price range
     * @param minPrice The minimum price
     * @param maxPrice The maximum price
     * @return List of properties within the price range
     */
    public List<Property> getPropertiesInPriceRange(double minPrice, double maxPrice) {
        List<Property> result = new ArrayList<>();
        getPropertiesInPriceRangeRec(root, minPrice, maxPrice, result);
        return result;
    }
    
    private void getPropertiesInPriceRangeRec(Node node, double minPrice, double maxPrice, List<Property> result) {
        if (node != null) {
            if (node.property.getPrice() >= minPrice) {
                getPropertiesInPriceRangeRec(node.left, minPrice, maxPrice, result);
            }
            
            if (node.property.getPrice() >= minPrice && node.property.getPrice() <= maxPrice) {
                result.add(node.property);
            }
            
            if (node.property.getPrice() <= maxPrice) {
                getPropertiesInPriceRangeRec(node.right, minPrice, maxPrice, result);
            }
        }
    }
    
    /**
     * Get properties by seller ID
     * @param sellerId The ID of the seller
     * @return List of properties owned by the seller
     */
    public List<Property> getPropertiesBySeller(String sellerId) {
        List<Property> result = new ArrayList<>();
        getPropertiesBySellerRec(root, sellerId, result);
        return result;
    }
    
    private void getPropertiesBySellerRec(Node node, String sellerId, List<Property> result) {
        if (node != null) {
            getPropertiesBySellerRec(node.left, sellerId, result);
            
            if (node.property.getSellerId().equals(sellerId)) {
                result.add(node.property);
            }
            
            getPropertiesBySellerRec(node.right, sellerId, result);
        }
    }
    
    /**
     * Check if the BST is empty
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return root == null;
    }
} 