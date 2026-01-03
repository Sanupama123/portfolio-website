package com.bookstore.patterns;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory Provider that manages all user factories.
 * This class provides a centralized way to get the appropriate factory
 * for creating different types of users.
 */
public class UserFactoryProvider {
    private static Map<String, UserFactory> factories;
    
    static {
        factories = new HashMap<>();
        factories.put("BUYER", new BuyerFactory());
        factories.put("SELLER", new SellerFactory());
        factories.put("ADMIN", new AdminFactory());
    }

    public static UserFactory getFactory(String userType) {
        UserFactory factory = factories.get(userType.toUpperCase());
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported user type: " + userType);
        }
        return factory;
    }

    public static String[] getSupportedUserTypes() {
        return factories.keySet().toArray(new String[0]);
    }

    public static boolean isUserTypeSupported(String userType) {
        return factories.containsKey(userType.toUpperCase());
    }
}


