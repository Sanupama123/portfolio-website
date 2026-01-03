package com.bookstore.patterns;

import com.bookstore.models.User;

/**
 * Abstract Factory interface for creating different types of users.
 * This is the Creator in the Factory Method pattern.
 */
public interface UserFactory {

    User createUser(User user) throws Exception;

    String getUserType();
}


