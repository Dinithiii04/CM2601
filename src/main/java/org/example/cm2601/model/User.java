package org.example.cm2601.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private List<String> preferences;
    private List<String> readingHistory;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.preferences = new ArrayList<>();
        this.readingHistory = new ArrayList<>();
    }

    // Getters
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

    // Methods to update preferences
    public void addPreference(String category) {
        if (!preferences.contains(category)) {
            preferences.add(category);
        }
    }

    public void addToReadingHistory(String articleTitle) {
        readingHistory.add(articleTitle);
    }
}
