package com.sforge.quotes.entity;

/**
 * Immutable User entity.
 * Carries information about logged-in user.
 */
public class User {

    /**
     * Username field.
     */
    private final String username;

    /**
     * Email of the user.
     */
    private final String email;

    /**
     * Constructor for immutable instance creation.
     * @param username Username
     * @param email email of the user.
     */
    public User(final String username, final String email){
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
