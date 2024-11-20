package org.example.cm2601.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String username;
    private String password;

    private List<String> preferences;
    private List<String> readingHistory;

    public User(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.preferences = new ArrayList<>();
        this.readingHistory = new ArrayList<>();
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public List<String> getReadingHistory() {
        return readingHistory;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Methods to update preferences and reading history
    public void addPreference(String category) {
        if (!preferences.contains(category)) {
            preferences.add(category);
        }
    }

    public void addToReadingHistory(String articleTitle) {
        readingHistory.add(articleTitle);
    }
}
