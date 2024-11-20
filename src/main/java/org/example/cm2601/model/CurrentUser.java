package org.example.cm2601.model;

import java.util.ArrayList;
import java.util.List;

public class CurrentUser {
    private static CurrentUser instance;
    private String username;

    // Private constructor to prevent direct instantiation
    private CurrentUser(String username) {
        this.username = username;
    }

    // Method to set the current user
    public static void setCurrentUser(String username) {
        if (instance == null) {
            instance = new CurrentUser(username);
        } else {
            instance.username = username;
        }
    }

    // Method to get the current user
    public static CurrentUser getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Current user is not set. Please login first.");
        }
        return instance;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Clear the current user
    public static void clearCurrentUser() {
        instance = null;
    }
}



