package org.example.cm2601.model;

public class CurrentUser extends User {
    private static CurrentUser instance;

    // Private constructor to prevent direct instantiation
    private CurrentUser(String userId, String username, String password) {
        super(userId, username, password);
    }

    // Method to set the current user
    public static void setCurrentUser(String userId, String username, String password) {
        if (instance == null) {
            instance = new CurrentUser(userId, username, password);
        } else {
            instance.setUserId(userId);  // Update userId using setter
            instance.setUsername(username); // Update username using setter
            instance.setPassword(password); // Update password using setter
        }
    }

    // Method to get the current user
    public static CurrentUser getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Current user is not set. Please login first.");
        }
        return instance;
    }

    // Clear the current user
    public static void clearCurrentUser() {
        instance = null;
    }
}
